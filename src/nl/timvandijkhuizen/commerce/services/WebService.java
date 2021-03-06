package nl.timvandijkhuizen.commerce.services;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.webserver.HttpInitializer;
import nl.timvandijkhuizen.commerce.webserver.StaticRoute;
import nl.timvandijkhuizen.commerce.webserver.routes.RouteFavicon;
import nl.timvandijkhuizen.commerce.webserver.routes.RouteRobots;
import nl.timvandijkhuizen.commerce.webserver.templating.TemplateFunctions;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.helpers.ConsoleHelper;
import nl.timvandijkhuizen.spigotutils.services.Service;

public class WebService implements Service {

    public static final String BRANDING_HTML = "<!-- Powered by MinecraftCommerce -->";
    private static final EventLoopGroup THREAD_GROUP = new NioEventLoopGroup(4);

    private ServerBootstrap bootstrap = new ServerBootstrap();
    private Map<String, StaticRoute> routes = new HashMap<>();

    private TemplateEngine templateEngine = new TemplateEngine();
    private TemplateFunctions templateFunctions = new TemplateFunctions();

    @Override
    public String getHandle() {
        return "web";
    }

    @Override
    public void init() throws Throwable {
        YamlConfig config = Commerce.getInstance().getConfig();

        // Create SSL context
        // =================================================
        ConfigOption<File> optionSslCert = config.getOption("webserver.certificate");
        ConfigOption<File> optionSslKey = config.getOption("webserver.privateKey");
        SslContext sslContext = null;

        if (!optionSslCert.isValueEmpty(config) && !optionSslCert.isValueEmpty(config)) {
            sslContext = SslContextBuilder.forServer(optionSslCert.getValue(config), optionSslKey.getValue(config)).build();
        }

        // Setup web-server
        // =================================================
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.group(THREAD_GROUP);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new HttpInitializer(sslContext));
        bootstrap.childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true));
        bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);

        // Bind to configured port
        int port = config.getOptionValue("webserver.port");

        ConsoleHelper.printInfo("Starting webserver on port " + port);

        try {
            bootstrap.bind(port).sync();
            ConsoleHelper.printInfo("Successfully started webserver on port " + port);
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to start webserver on port " + port, e);
        }

        // Setup routes
        // =================================================
        routes.put("/favicon.ico", new RouteFavicon());
        routes.put("/robots.txt", new RouteRobots());

        // Setup template engine
        // =================================================

        // Add file resolver
        FileTemplateResolver fileResolver = new FileTemplateResolver();

        fileResolver.setCheckExistence(true);
        fileResolver.setPrefix(new File(".").getPath() + File.separatorChar);
        fileResolver.setSuffix(".html");
        fileResolver.setCharacterEncoding("UTF-8");
        fileResolver.setTemplateMode(TemplateMode.HTML);
        fileResolver.setOrder(1);

        templateEngine.addTemplateResolver(fileResolver);

        // Add class loader resolver
        ClassLoaderTemplateResolver resourceResolver = new ClassLoaderTemplateResolver();

        resourceResolver.setCheckExistence(true);
        resourceResolver.setPrefix("templates" + File.separatorChar);
        resourceResolver.setSuffix(".html");
        resourceResolver.setCharacterEncoding("UTF-8");
        resourceResolver.setTemplateMode(TemplateMode.HTML);
        resourceResolver.setOrder(2);

        templateEngine.addTemplateResolver(resourceResolver);
    }

    /**
     * Returns all registered routes.
     * 
     * @return
     */
    public Map<String, StaticRoute> getRoutes() {
        return routes;
    }
    
    /**
     * Registers a template resolver.
     * 
     * @param templateResolver
     */
    public void registerTemplateResolver(ITemplateResolver templateResolver) {
        templateEngine.addTemplateResolver(templateResolver);
    }

    /**
     * Renders a Thymeleaf template.
     * 
     * @param templateName
     * @param variables
     * @return
     */
    public String renderTemplate(String templateName, Map<String, Object> variables) {
        YamlConfig config = Commerce.getInstance().getConfig();
        Context context = new Context();

        // Add globals
        context.setVariable("serverName", config.getOptionValue("general.serverName"));
        context.setVariable("devMode", config.getOptionValue("general.devMode"));

        // Add functions
        context.setVariable("functions", templateFunctions);

        // Add custom variables
        context.setVariables(variables);

        return BRANDING_HTML + templateEngine.process(templateName, context);
    }

    /**
     * Renders a Thymeleaf template.
     * 
     * @param templateFile
     * @param variables
     * @return
     */
    public String renderTemplate(File templateFile, Map<String, Object> variables) {
        return renderTemplate(templateFile.getPath(), variables);
    }

}

package nl.timvandijkhuizen.custompayments;

import java.util.Currency;
import java.util.Locale;
import java.util.Map;

import org.bukkit.Material;

import nl.timvandijkhuizen.custompayments.base.Storage;
import nl.timvandijkhuizen.custompayments.commands.CommandCustomPayments;
import nl.timvandijkhuizen.custompayments.events.RegisterStorageTypesEvent;
import nl.timvandijkhuizen.custompayments.services.CategoryService;
import nl.timvandijkhuizen.custompayments.services.GatewayService;
import nl.timvandijkhuizen.custompayments.services.OrderService;
import nl.timvandijkhuizen.custompayments.services.ProductService;
import nl.timvandijkhuizen.custompayments.storage.StorageMysql;
import nl.timvandijkhuizen.spigotutils.MainThread;
import nl.timvandijkhuizen.spigotutils.PluginBase;
import nl.timvandijkhuizen.spigotutils.commands.CommandService;
import nl.timvandijkhuizen.spigotutils.config.ConfigIcon;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigTypes;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.menu.MenuService;
import nl.timvandijkhuizen.spigotutils.services.Service;

public class CustomPayments extends PluginBase {
    
    private static CustomPayments instance;
    private YamlConfig config;

    @Override
    public void load() throws Exception {
        instance = this;
        MainThread.setPlugin(this);
        
        // Setup config
        config = new YamlConfig(this);
        
        // Create options
        ConfigOption<String> optionStorageType = new ConfigOption<>("storage.type", ConfigTypes.STRING)
            .setIcon(new ConfigIcon(Material.ENDER_CHEST, "Storage Type"))
            .setRequired(true)
            .setReadOnly(true);
        
        ConfigOption<Currency> optionCurrency = new ConfigOption<>("general.currency", ConfigTypes.CURRENCY)
            .setIcon(new ConfigIcon(Material.SUNFLOWER, "Currency"))
            .setRequired(true)
            .setDefaultValue(Currency.getInstance(Locale.US));
        
        // Add options
        config.addOption(optionStorageType);
        config.addOption(optionCurrency);
    }

    @Override
    public void unload() throws Exception {

    }

    @Override
    public Service[] registerServices() throws Exception {
        CommandService commandService = new CommandService(this);

        commandService.register(new CommandCustomPayments());

        return new Service[] {
            getDatabase(),
            new MenuService(),
            new CategoryService(),
            new ProductService(),
            new GatewayService(),
            new OrderService(),
            commandService
        };
    }

    private Storage getDatabase() throws Exception {
        RegisterStorageTypesEvent event = new RegisterStorageTypesEvent();

        event.addStorageType("mysql", StorageMysql.class);
        getServer().getPluginManager().callEvent(event);

        // Register chosen storage type
        Map<String, Class<? extends Storage>> storageTypes = event.getStorageTypes();
        ConfigOption<String> optionType = config.getOption("storage.type");
        String storageTypeKey = optionType.getValue(config);

        if (storageTypes.containsKey(storageTypeKey)) {
            Class<? extends Storage> storageClass = storageTypes.get(storageTypeKey);
            return storageClass.newInstance();
        } else {
            throw new RuntimeException("Unsupported database driver");
        }
    }

    public static CustomPayments getInstance() {
        return instance;
    }

    public YamlConfig getConfig() {
        return config;
    }

    public Storage getStorage() {
        return getService("storage");
    }

}

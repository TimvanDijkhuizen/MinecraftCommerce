package nl.timvandijkhuizen.commerce.menu.content.shop.checkout;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.commerce.elements.Gateway;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.commerce.menu.actions.shop.ActionShopGateways;
import nl.timvandijkhuizen.commerce.services.GatewayService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.data.TypedValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuShopPayment implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        Menu menu = new Menu("Shop " + Icon.ARROW_RIGHT + " Payment (4/4)", MenuSize.LG);
        GatewayService gatewayService = Commerce.getInstance().getService(GatewayService.class);
        YamlConfig config = Commerce.getInstance().getConfig();
        
        // Get cart
        Order cart = args.get(0);
        Gateway gateway = cart.getGateway();
        StoreCurrency currency = cart.getCurrency();

        // Check if we've got terms
        ConfigOption<List<String>> optionTerms = config.getOption("general.termsAndConditions");
        boolean hasTerms = !optionTerms.isValueEmpty(config);
        TypedValue<Boolean> accepted = new TypedValue<>(!hasTerms);
        
        // Terms button
        if(hasTerms) {
            MenuItemBuilder acceptButton = new MenuItemBuilder(XMaterial.BOOK);
    
            acceptButton.setName(UI.color("Terms & Conditions", UI.COLOR_PRIMARY, ChatColor.BOLD));
    
            acceptButton.addLore("");
            acceptButton.addLore(UI.color("Left-click to accept the terms & conditions.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            acceptButton.addLore(UI.color("Right-click to view the terms & conditions.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
    
            acceptButton.setClickListener(event -> {
                ClickType type = event.getClickType();
    
                UI.playSound(player, UI.SOUND_CLICK);
    
                if (type == ClickType.LEFT) {
                    accepted.set(!accepted.get());
    
                    if (accepted.get()) {
                        acceptButton.addEnchantGlow();
                    } else {
                        acceptButton.removeEnchantGlow();
                    }
    
                    menu.refresh();
                } else if (!optionTerms.isValueEmpty(config) && type == ClickType.RIGHT) {
                    List<String> terms = optionTerms.getValue(config);

                    // Create book
                    ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
                    BookMeta meta = (BookMeta) book.getItemMeta();
                    
                    meta.setTitle("Terms & Conditions");
                    meta.setAuthor(config.getOptionValue("general.serverName"));
                    meta.setPages(terms);
                    
                    book.setItemMeta(meta);
                    
                    // Open book
                    UI.playSound(player, UI.SOUND_CLICK);
                    player.openBook(book);
                }
            });
    
            menu.setItem(acceptButton, 11);
        }

        // Pay button
        MenuItemBuilder payButton = new MenuItemBuilder(XMaterial.COMPARATOR);
        TypedValue<String> payActionLore = new TypedValue<>();
        String gatewayName = gateway != null ? gateway.getDisplayName() : "";

        payButton.setName(UI.color("Pay using " + gatewayName, UI.COLOR_PRIMARY, ChatColor.BOLD));

        payButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();

            if (payActionLore.get() != null) {
                lore.add(payActionLore.get());
            } else if (!cart.isValid(Order.SCENARIO_PAY)) {
                lore.add("");
                lore.add(UI.color("Errors:", UI.COLOR_ERROR, ChatColor.BOLD));

                for (String error : cart.getErrors()) {
                    lore.add(UI.color(UI.TAB + Icon.SQUARE + " " + error, UI.COLOR_ERROR));
                }
            } else if (!accepted.get()) {
                lore.add(UI.color("You must accept the terms & conditions.", UI.COLOR_ERROR));
            } else {
                lore.add("");
                lore.add(UI.color("Total: ", UI.COLOR_TEXT) + UI.color(ShopHelper.formatPrice(cart.getTotal(), currency), UI.COLOR_SECONDARY));
                lore.add(UI.color("Paid: ", UI.COLOR_TEXT) + UI.color(ShopHelper.formatPrice(cart.getAmountPaid(), currency), UI.COLOR_SECONDARY));
            }

            return lore;
        });

        payButton.setClickListener(event -> {
            if (cart.isValid(Order.SCENARIO_PAY) && accepted.get()) {
                UI.playSound(player, UI.SOUND_CLICK);
                payActionLore.set(UI.color("Loading...", UI.COLOR_TEXT));
                menu.disableItems();
                menu.refresh();

                gatewayService.createPaymentUrl(cart, url -> {
                    menu.enableItems();
                    
                    if (url != null) {
                        UI.playSound(player, UI.SOUND_SUCCESS);
                        menu.close(player);

                        // Send message
                        player.sendMessage(UI.color(UI.LINE, UI.COLOR_TEXT, ChatColor.BOLD));
                        player.sendMessage("");
                        player.sendMessage(UI.color("Complete your donation using this link:", UI.COLOR_PRIMARY, ChatColor.BOLD));
                        player.sendMessage(UI.color(url, UI.COLOR_SECONDARY));
                        player.sendMessage("");
                        player.sendMessage(UI.color(UI.LINE, UI.COLOR_TEXT, ChatColor.BOLD));
                    } else {
                        UI.playSound(player, UI.SOUND_ERROR);
                        payActionLore.set(UI.color("Failed to create payment url.", UI.COLOR_ERROR));
                        menu.refresh();
                    }
                });
            } else {
                UI.playSound(player, UI.SOUND_ERROR);
            }
        });

        menu.setItem(payButton, hasTerms ? 15 : 13);

        // Cart button
        menu.setItem(ShopHelper.createCartItem(cart), menu.getSize().getSlots() - 9 + 4);

        // Previous (gateway) button
        MenuItemBuilder previousButton = new MenuItemBuilder(XMaterial.OAK_FENCE_GATE);

        previousButton.setName(UI.color("Previous Step", UI.COLOR_SECONDARY, ChatColor.BOLD));
        previousButton.setLore(UI.color("Gateways", UI.COLOR_TEXT));
        previousButton.setClickListener(new ActionShopGateways());

        menu.setItem(previousButton, menu.getSize().getSlots() - 9);

        // Set bottom line
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 1);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 2);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 3);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 5);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 6);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 7);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 8);

        return menu;
    }

}

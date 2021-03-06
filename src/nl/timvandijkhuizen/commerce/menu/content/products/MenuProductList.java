package nl.timvandijkhuizen.commerce.menu.content.products;

import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.elements.Command;
import nl.timvandijkhuizen.commerce.elements.Product;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.ProductService;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuProductList implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        ProductService productService = Commerce.getInstance().getService(ProductService.class);
        PagedMenu menu = new PagedMenu("Admin " + Icon.ARROW_RIGHT + " Products", 3, 7, 1, 1);

        // Add product buttons
        Set<Product> products = args.getSet(0);

        for (Product product : products) {
            MenuItemBuilder item = new MenuItemBuilder(product.getIcon());

            // Set product name
            item.setName(UI.color(product.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            // Split lore into smaller lines
            String[] lines = ShopHelper.parseDescription(product.getDescription());

            for (String line : lines) {
                item.addLore(UI.color(line, UI.COLOR_TEXT));
            }

            // Category and price
            item.addLore("", UI.color("Category: ", UI.COLOR_TEXT) + UI.color(product.getCategory().getName(), UI.COLOR_SECONDARY));
            item.addLore(UI.color("Price: ", UI.COLOR_TEXT) + UI.color(ShopHelper.formatPrice(product.getPrice()), UI.COLOR_SECONDARY), "");

            // Commands
            item.addLore(UI.color("Commands:", UI.COLOR_PRIMARY));

            if (product.getCommands().size() > 0) {
                for (Command command : product.getCommands()) {
                    item.addLore(UI.color(UI.TAB + Icon.SQUARE + " " + command.getCommand(), UI.COLOR_SECONDARY));
                }
            } else {
                item.addLore(UI.color(UI.TAB + "None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            }

            item.addLore("", UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            item.addLore(UI.color("Right-click to delete.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

            // Set click listener
            item.setClickListener(event -> {
                ClickType clickType = event.getClickType();

                UI.playSound(player, UI.SOUND_CLICK);

                if (clickType == ClickType.LEFT) {
                    Menus.PRODUCT_EDIT.open(player, product);
                } else if (clickType == ClickType.RIGHT) {
                    item.setLore(UI.color("Deleting...", UI.COLOR_TEXT));
                    menu.refresh();

                    productService.deleteProduct(product, success -> {
                        if (success) {
                            UI.playSound(player, UI.SOUND_DELETE);
                            menu.removePagedItem(item);
                            menu.refresh();
                        } else {
                            UI.playSound(player, UI.SOUND_ERROR);
                            item.setLore(UI.color("Error: Failed to delete product.", UI.COLOR_ERROR));
                            menu.refresh();
                        }
                    });
                }
            });

            menu.addPagedItem(item);
        }

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.HOME.open(player);
        });

        menu.setItem(backButton, menu.getSize().getSlots() - 9 + 3);

        // Create new product button
        MenuItemBuilder createButton = new MenuItemBuilder(XMaterial.NETHER_STAR);

        createButton.setName(UI.color("Create Product", UI.COLOR_SECONDARY, ChatColor.BOLD));

        createButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.PRODUCT_EDIT.open(player);
        });

        menu.setItem(createButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }

}

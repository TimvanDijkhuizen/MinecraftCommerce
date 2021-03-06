package nl.timvandijkhuizen.commerce.menu.content.shop;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.elements.Category;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.elements.Product;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.commerce.menu.actions.shop.ActionShopCategories;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.data.TypedValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuShopProducts implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        OrderService orderService = Commerce.getInstance().getService(OrderService.class);
        Category category = args.get(0);
        PagedMenu menu = new PagedMenu("Shop " + Icon.ARROW_RIGHT + " " + category.getName(), 3, 7, 1, 1, 1, 5, 7);

        // Add product buttons
        Set<Product> products = args.getSet(1);
        Order cart = args.get(2);

        for (Product product : products) {
            MenuItemBuilder item = new MenuItemBuilder(product.getIcon());
            TypedValue<String> actionLore = new TypedValue<>();

            // Set product name
            item.setName(UI.color(product.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            item.setLoreGenerator(() -> {
                String[] description = ShopHelper.parseDescription(product.getDescription());
                List<String> lore = new ArrayList<>();

                // Add action lore if not null
                if (actionLore.get() != null) {
                    lore.add(actionLore.get());
                    return lore;
                }

                // Create lore
                for (String line : description) {
                    lore.add(UI.color(line, UI.COLOR_TEXT));
                }

                lore.add("");
                lore.add(UI.color(ShopHelper.formatPrice(product.getPrice(), cart.getCurrency()), UI.COLOR_SECONDARY));
                lore.add("");
                
                lore.add(UI.color("Right-click to add this item to your cart.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

                return lore;
            });

            // Set click listener
            item.setClickListener(event -> {
                UI.playSound(player, UI.SOUND_CLICK);
                actionLore.set(UI.color("Saving...", UI.COLOR_TEXT));
                menu.disableItems();
                menu.refresh();

                // Add item
                cart.addLineItem(new LineItem(product, 1));

                orderService.saveOrder(cart, success -> {
                    if (success) {
                        UI.playSound(player, UI.SOUND_SUCCESS);
                        actionLore.set(null);
                    } else {
                        UI.playSound(player, UI.SOUND_ERROR);
                        actionLore.set(UI.color("Failed to save cart.", UI.COLOR_ERROR));
                    }

                    menu.enableItems();
                    menu.refresh();
                });
            });

            menu.addPagedItem(item);
        }

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(new ActionShopCategories());

        menu.setItem(backButton, menu.getSize().getSlots() - 9 + 3);

        // Cart button
        menu.setItem(ShopHelper.createCartItem(cart), menu.getSize().getSlots() - 9 + 4);

        return menu;
    }

}

package nl.timvandijkhuizen.commerce.menu.content.products;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.elements.Product;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuProductIcon implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        PagedMenu menu = new PagedMenu("Product Icon", 3, 7, 1, 1, 1, 5, 7);
        Product product = args.get(0);
        Material selected = product.getIcon();

        for (Material icon : Commerce.MENU_ICONS) {
            MenuItemBuilder item = new MenuItemBuilder(icon);

            item.setName(UI.color(WordUtils.capitalize(icon.name().replace('_', ' ').toLowerCase()), UI.COLOR_PRIMARY, ChatColor.BOLD));

            // Enchant if selected
            if (icon == selected) {
                item.addEnchantGlow();
            }

            item.setClickListener(event -> {
                product.setIcon(icon);
                UI.playSound(player, UI.SOUND_CLICK);
                Menus.PRODUCT_EDIT.open(player, product);
            });

            menu.addPagedButton(item);
        }

        // Cancel button
        MenuItemBuilder cancelButton = MenuItems.CANCEL.clone();

        cancelButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.PRODUCT_EDIT.open(player, product);
        });

        menu.setButton(cancelButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}
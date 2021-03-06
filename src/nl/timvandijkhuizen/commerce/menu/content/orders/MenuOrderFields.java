package nl.timvandijkhuizen.commerce.menu.content.orders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.config.sources.OrderFieldData;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuOrderFields implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        PagedMenu menu = new PagedMenu("Admin " + Icon.ARROW_RIGHT + " Order Fields", 3, 7, 1, 1, 2, 5, 6);

        // Get order and fields
        Order order = args.get(0);
        OrderFieldData fieldData = order.getFieldData();
        Collection<ConfigOption<?>> options = fieldData.getOptions();

        // Add fields
        for (ConfigOption<?> option : options) {
            MenuItemBuilder item = new MenuItemBuilder(option.getIcon());

            item.setName(UI.color(option.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            item.setLoreGenerator(() -> {
                List<String> lore = new ArrayList<>();

                if (!option.isValueEmpty(fieldData)) {
                    lore.add(UI.color(option.getDisplayValue(fieldData), UI.COLOR_SECONDARY));
                } else {
                    lore.add(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                }

                return lore;
            });

            menu.addPagedItem(item);
        }

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.ORDER_VIEW.open(player, order);
        });

        menu.setItem(backButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}

package nl.timvandijkhuizen.commerce.menu.content.orders;

import java.util.Collection;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.base.ProductSnapshot;
import nl.timvandijkhuizen.commerce.config.sources.OrderFieldData;
import nl.timvandijkhuizen.commerce.elements.LineItem;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.elements.Transaction;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.menu.actions.ActionOrderList;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.data.DataList;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuOrderView implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        Menu menu = new Menu("Admin " + Icon.ARROW_RIGHT + " View Order", MenuSize.XXL);
        Order order = args.get(0);

        // UniqueId button
        // ===========================
        MenuItemBuilder uniqueIdButton = new MenuItemBuilder(XMaterial.PLAYER_HEAD);

        uniqueIdButton.setSkullOwner(order.getPlayerUniqueId());
        uniqueIdButton.setName(UI.color("Player UniqueId", UI.COLOR_PRIMARY, ChatColor.BOLD));
        uniqueIdButton.addLore(UI.color(order.getPlayerUniqueId().toString(), UI.COLOR_TEXT));

        menu.setItem(uniqueIdButton, 11);

        // Username button
        // ===========================
        MenuItemBuilder usernameButton = new MenuItemBuilder(XMaterial.NAME_TAG);

        usernameButton.setName(UI.color("Player Username", UI.COLOR_PRIMARY, ChatColor.BOLD));
        usernameButton.addLore(UI.color(order.getPlayerName(), UI.COLOR_TEXT));

        menu.setItem(usernameButton, 13);

        // Currency button
        // ===========================
        MenuItemBuilder currencyButton = new MenuItemBuilder(XMaterial.SUNFLOWER);

        currencyButton.setName(UI.color("Currency", UI.COLOR_PRIMARY, ChatColor.BOLD));
        currencyButton.addLore(UI.color(order.getCurrency().getCode().getDisplayName(), UI.COLOR_TEXT));

        menu.setItem(currencyButton, 15);

        // Products button
        // ===========================
        MenuItemBuilder itemsButton = new MenuItemBuilder(XMaterial.CHEST);

        itemsButton.setName(UI.color("Items", UI.COLOR_PRIMARY, ChatColor.BOLD));

        // Add items to lore
        DataList<LineItem> lineItems = order.getLineItems();

        if (lineItems.size() > 0) {
            for (LineItem lineItem : lineItems) {
                ProductSnapshot product = lineItem.getProduct();
                String quantity = lineItem.getQuantity() > 1 ? (lineItem.getQuantity() + "x ") : "";
                String price = ShopHelper.formatPrice(lineItem.getPrice());

                itemsButton.addLore(UI.color(Icon.SQUARE + " " + quantity + product.getName() + " " + Icon.ARROW_RIGHT + " " + price, UI.COLOR_TEXT));
            }
        } else {
            itemsButton.addLore(UI.color("None", UI.COLOR_TEXT, ChatColor.ITALIC));
        }

        itemsButton.addLore("", UI.color("Click to view details.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

        itemsButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.ORDER_ITEMS.open(player, order);
        });

        menu.setItem(itemsButton, 29);

        // Fields button
        // ===========================
        MenuItemBuilder fieldsButton = new MenuItemBuilder(XMaterial.OAK_SIGN);

        fieldsButton.setName(UI.color("Fields", UI.COLOR_PRIMARY, ChatColor.BOLD));

        // Add fields to lore
        OrderFieldData fieldData = order.getFieldData();
        Collection<ConfigOption<?>> options = fieldData.getOptions();

        if (options.size() > 0) {
            for (ConfigOption<?> option : options) {
                String value = UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC);

                if (!option.isValueEmpty(fieldData)) {
                    value = UI.color(option.getDisplayValue(fieldData), UI.COLOR_SECONDARY);
                }

                fieldsButton.addLore(UI.color(Icon.SQUARE + " " + option.getName() + ": ", UI.COLOR_TEXT) + value);
            }
        } else {
            fieldsButton.addLore(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
        }

        fieldsButton.addLore("", UI.color("Click to view details.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

        fieldsButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.ORDER_FIELDS.open(player, order);
        });

        menu.setItem(fieldsButton, 31);

        // Transaction button
        // ===========================
        MenuItemBuilder transactionButton = new MenuItemBuilder(XMaterial.DROPPER);
        Set<Transaction> transactions = order.getTransactions();

        transactionButton.setName(UI.color("Transaction", UI.COLOR_PRIMARY, ChatColor.BOLD));

        if (transactions.size() > 0) {
            for (Transaction transaction : transactions) {
                transactionButton.addLore(UI.color(Icon.SQUARE + " " + transaction.getReference(), UI.COLOR_TEXT));
            }
        } else {
            transactionButton.addLore(UI.color("None", UI.COLOR_TEXT, ChatColor.ITALIC));
        }
        
        menu.setItem(transactionButton, 33);
        
        // Set bottom line
        // ===========================
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 0);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 1);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 2);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 3);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 5);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 6);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 7);
        menu.setItem(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 8);

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(new ActionOrderList());

        menu.setItem(backButton, menu.getSize().getSlots() - 9 + 4);

        return menu;
    }

}

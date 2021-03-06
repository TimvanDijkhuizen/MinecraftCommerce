package nl.timvandijkhuizen.commerce.menu.content.shop.checkout;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.config.sources.OrderFieldData;
import nl.timvandijkhuizen.commerce.elements.Order;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.commerce.menu.actions.shop.ActionShopCart;
import nl.timvandijkhuizen.commerce.menu.actions.shop.ActionShopGateways;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.data.TypedValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.Icon;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuShopFields implements PredefinedMenu {

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Menu create(Player player, DataArguments args) {
        PagedMenu menu = new PagedMenu("Shop " + Icon.ARROW_RIGHT + " Fields (2/4)", 3, 7, 1, 1, 2, 5, 6);
        OrderService orderService = Commerce.getInstance().getService(OrderService.class);

        // Add field buttons
        Order cart = args.get(0);
        OrderFieldData fieldData = cart.getFieldData();

        for (ConfigOption option : fieldData.getOptions()) {
            MenuItemBuilder item = new MenuItemBuilder(option.getIcon());
            TypedValue<String> actionLore = new TypedValue<>();

            // Get meta and description
            DataArguments meta = option.getMeta();
            String description = meta.getString(0);

            item.setName(UI.color(option.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));

            item.setLoreGenerator(() -> {
                List<String> lore = new ArrayList<>();

                // Return action lore if not null
                if (actionLore.get() != null) {
                    lore.add(actionLore.get());
                    return lore;
                }

                lore.add(UI.color(description, UI.COLOR_TEXT));
                lore.add("");

                // Create lore
                if (!option.isValueEmpty(fieldData)) {
                    lore.add(UI.color("Value: ", UI.COLOR_TEXT) + UI.color(option.getDisplayValue(fieldData), UI.COLOR_SECONDARY));
                } else {
                    lore.add(UI.color("Value: ", UI.COLOR_TEXT) + UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                }

                // Add validation errors to lore
                if (cart.hasErrors(option.getPath())) {
                    lore.add("");
                    lore.add(UI.color("Errors:", UI.COLOR_ERROR, ChatColor.BOLD));

                    for (String error : cart.getErrors(option.getPath())) {
                        lore.add(UI.color(UI.TAB + Icon.SQUARE + " " + error, UI.COLOR_ERROR));
                    }
                }

                lore.add("");
                lore.add(UI.color("Left-click to edit this field.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                lore.add(UI.color("Right-click to reset this field.", UI.COLOR_SECONDARY, ChatColor.ITALIC));

                return lore;
            });

            // Set click listener
            item.setClickListener(event -> {
                ClickType type = event.getClickType();

                if (type == ClickType.LEFT) {
                    UI.playSound(player, UI.SOUND_CLICK);

                    option.getValueInput(fieldData, event, value -> {
                        option.setValue(fieldData, value);

                        actionLore.set(UI.color("Saving...", UI.COLOR_TEXT));
                        menu.disableItems();
                        menu.open(player);

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
                } else if (type == ClickType.RIGHT) {
                    UI.playSound(player, UI.SOUND_DELETE);
                    option.resetValue(fieldData);
                    menu.refresh();
                }
            });

            menu.addPagedItem(item);
        }

        // Previous (cart) button
        MenuItemBuilder previousButton = new MenuItemBuilder(XMaterial.MINECART);

        previousButton.setName(UI.color("Previous Step", UI.COLOR_SECONDARY, ChatColor.BOLD));
        previousButton.setLore(UI.color("Cart", UI.COLOR_TEXT));
        previousButton.setClickListener(new ActionShopCart());

        menu.setItem(previousButton, menu.getSize().getSlots() - 9);

        // Cart button
        menu.setItem(ShopHelper.createCartItem(cart), menu.getSize().getSlots() - 9 + 3);

        // Next (gateway) button
        MenuItemBuilder nextButton = new MenuItemBuilder(XMaterial.OAK_FENCE_GATE);

        nextButton.setName(UI.color("Next Step", UI.COLOR_SECONDARY, ChatColor.BOLD));

        nextButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();

            lore.add(UI.color("Gateway", UI.COLOR_TEXT));

            if (!cart.isValid(Order.SCENARIO_FIELDS)) {
                lore.add("");
                lore.add(UI.color("Errors: ", UI.COLOR_ERROR, ChatColor.BOLD));
                lore.add(UI.color(UI.TAB + Icon.SQUARE + " Field contains an invalid value.", UI.COLOR_ERROR));
            }

            return lore;
        });

        nextButton.setClickListener(event -> {
            if (cart.isValid(Order.SCENARIO_FIELDS)) {
                new ActionShopGateways().onClick(event);
            } else {
                UI.playSound(player, UI.SOUND_ERROR);
                menu.refresh();
            }
        });

        menu.setItem(nextButton, menu.getSize().getSlots() - 1);

        return menu;
    }

}

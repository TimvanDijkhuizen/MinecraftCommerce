package nl.timvandijkhuizen.commerce.menu.actions.shop;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.GatewayService;
import nl.timvandijkhuizen.commerce.services.OrderService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuClick;
import nl.timvandijkhuizen.spigotutils.menu.MenuClickListener;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ActionShopGateways implements MenuClickListener {

    @Override
    public void onClick(MenuClick event) {
        OrderService orderService = Commerce.getInstance().getService(OrderService.class);
        GatewayService gatewayService = Commerce.getInstance().getService(GatewayService.class);
        Player whoClicked = event.getPlayer();
        Menu activeMenu = event.getMenu();
        MenuItemBuilder clickedItem = event.getItem();

        UI.playSound(whoClicked, UI.SOUND_CLICK);

        clickedItem.setLore(UI.color("Loading...", UI.COLOR_TEXT));
        activeMenu.disableItems();
        activeMenu.refresh();

        // Create menu
        gatewayService.getGateways(gateways -> {
            if (gateways == null) {
                UI.playSound(whoClicked, UI.SOUND_ERROR);
                clickedItem.setLore(UI.color("Error: Failed to load gateways.", UI.COLOR_ERROR));
                activeMenu.enableItems();
                activeMenu.refresh();
                return;
            }

            orderService.getCart(whoClicked, cart -> {
                if (cart == null) {
                    UI.playSound(whoClicked, UI.SOUND_ERROR);
                    clickedItem.setLore(UI.color("Error: Failed to load cart.", UI.COLOR_ERROR));
                    activeMenu.enableItems();
                    activeMenu.refresh();
                    return;
                }

                Menus.SHOP_GATEWAY.open(whoClicked, gateways, cart);
            });
        });
    }

}
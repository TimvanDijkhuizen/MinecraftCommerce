package nl.timvandijkhuizen.custompayments.menu;

import java.util.stream.Stream;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.menu.content.MenuHome;
import nl.timvandijkhuizen.custompayments.menu.content.categories.MenuCategoryEdit;
import nl.timvandijkhuizen.custompayments.menu.content.categories.MenuCategoryList;
import nl.timvandijkhuizen.custompayments.menu.content.config.MenuConfig;
import nl.timvandijkhuizen.custompayments.menu.content.gateways.MenuGatewayEdit;
import nl.timvandijkhuizen.custompayments.menu.content.gateways.MenuGatewayList;
import nl.timvandijkhuizen.custompayments.menu.content.gateways.MenuGatewayOptions;
import nl.timvandijkhuizen.custompayments.menu.content.gateways.MenuGatewayType;
import nl.timvandijkhuizen.custompayments.menu.content.orders.MenuOrderList;
import nl.timvandijkhuizen.custompayments.menu.content.orders.MenuOrderView;
import nl.timvandijkhuizen.custompayments.menu.content.products.MenuProductCategory;
import nl.timvandijkhuizen.custompayments.menu.content.products.MenuProductCommands;
import nl.timvandijkhuizen.custompayments.menu.content.products.MenuProductEdit;
import nl.timvandijkhuizen.custompayments.menu.content.products.MenuProductIcon;
import nl.timvandijkhuizen.custompayments.menu.content.products.MenuProductList;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.PagedMenu;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;

public enum Menus {

    HOME(new MenuHome()),

    CONFIG(new MenuConfig()),

    CATEGORY_LIST(new MenuCategoryList()),
    CATEGORY_EDIT(new MenuCategoryEdit()),

    PRODUCT_LIST(new MenuProductList()),
    PRODUCT_EDIT(new MenuProductEdit()),
    PRODUCT_ICON(new MenuProductIcon()),
    PRODUCT_CATEGORY(new MenuProductCategory()),
    PRODUCT_COMMANDS(new MenuProductCommands()),

    FIELD_LIST(null),
    FIELD_EDIT(null),

    GATEWAY_LIST(new MenuGatewayList()),
    GATEWAY_EDIT(new MenuGatewayEdit()),
    GATEWAY_TYPE(new MenuGatewayType()),
    GATEWAY_OPTIONS(new MenuGatewayOptions()),
    
    ORDER_LIST(new MenuOrderList()),
    ORDER_VIEW(new MenuOrderView());

    private PredefinedMenu predefinedMenu;

    Menus(PredefinedMenu predefinedMenu) {
        this.predefinedMenu = predefinedMenu;
    }

    public void open(Player player, Object... args) {
        Menu menu = predefinedMenu.create(player, Stream.of(args).map(obj -> new DataValue(obj)).toArray(DataValue[]::new));

        if (menu instanceof PagedMenu) {
            ((PagedMenu) menu).open(player, 0);
        } else {
            menu.open(player);
        }
    }

}

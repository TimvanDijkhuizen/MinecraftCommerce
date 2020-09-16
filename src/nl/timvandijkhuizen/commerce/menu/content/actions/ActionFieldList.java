package nl.timvandijkhuizen.commerce.menu.content.actions;

import org.bukkit.entity.Player;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.services.FieldService;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemAction;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemClick;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class ActionFieldList implements MenuItemAction {

    private boolean clickSound = true;
    
    public ActionFieldList(boolean clickSound) {
        this.clickSound = clickSound;
    }
    
    public ActionFieldList() { }
    
    @Override
    public void onClick(MenuItemClick event) {
        FieldService fieldService = Commerce.getInstance().getService("fields");
        Player whoClicked = event.getPlayer();
        Menu activeMenu = event.getMenu();
        MenuItemBuilder clickedItem = event.getItem();

        if(clickSound) {
            UI.playSound(whoClicked, UI.SOUND_CLICK);
        }
        
        clickedItem.setLore(UI.color("Loading...", UI.COLOR_TEXT));
        activeMenu.disableButtons();
        activeMenu.refresh();

        // Create menu
        fieldService.getFields(fields -> {
            activeMenu.enableButtons();
            
            if (fields == null) {
                UI.playSound(whoClicked, UI.SOUND_ERROR);
                clickedItem.setLore(UI.color("Error: Failed to load fields.", UI.COLOR_ERROR));
                activeMenu.refresh();
                return;
            }

            Menus.FIELD_LIST.open(whoClicked, fields);
        });
    }

}
package nl.timvandijkhuizen.custompayments.menu.content.config;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.spigotutils.config.ConfigIcon;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.YamlConfig;
import nl.timvandijkhuizen.spigotutils.data.DataValue;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.MenuItems;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.types.PagedMenu;
import nl.timvandijkhuizen.spigotutils.ui.UI;

@SuppressWarnings({"rawtypes", "unchecked"})
public class MenuConfig implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataValue... args) {
        YamlConfig config = CustomPayments.getInstance().getConfig();
        PagedMenu menu = new PagedMenu("Configuration", 3, 7, 1, 1, 1, 5, 7);

        // Add config options
        for (ConfigOption option : config.getOptions()) {
            ConfigIcon icon = option.getIcon();
            
            // Ignore options without an icon
            if(icon == null) {
                continue;
            }
            
            // Create and add option
            MenuItemBuilder item = new MenuItemBuilder(icon.getMaterial());
            
            item.setName(UI.color(icon.getName(), UI.COLOR_PRIMARY, ChatColor.BOLD));
            
            if(!option.isValueEmpty(config)) {
                for(String line : option.getValueLore(config)) {
                    item.addLore(UI.color(line, UI.COLOR_SECONDARY));
                }
            } else {
                item.addLore(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            }
            
            if(option.isReadOnly()) {
                item.addLore("", UI.color("This option cannot be changed from the GUI.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            } else {
                item.addLore("", UI.color("Left-click to edit this setting.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                item.addEnchantGlow();
            }
            
            // Set click listener
            item.setClickListener(event -> {
                if(!option.isReadOnly()) {
                    UI.playSound(player, UI.SOUND_CLICK);
                    
                    option.getValueInput(player, option.getValue(config), value -> {
                        option.setValue(config, value);
                        config.save();
                        
                        // Clear lore
                        item.removeLore();
                        
                        // Set new lore
                        if(!option.isValueEmpty(config)) {
                            for(String line : option.getValueLore(config)) {
                                item.addLore(UI.color(line, UI.COLOR_SECONDARY));
                            }
                        } else {
                            item.addLore(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                        }
                        
                        if(option.isReadOnly()) {
                            item.addLore("", UI.color("This option cannot be changed from the GUI.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                        } else {
                            item.addLore("", UI.color("Left-click to edit this setting.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
                            item.addEnchantGlow();
                        }
                        
                        // Open menu
                        menu.open(player);
                    });
                } else {
                    UI.playSound(player, UI.SOUND_ERROR);
                }
            });
            
            menu.addPagedButton(item);
        }

        // Go back button
        MenuItemBuilder backButton = MenuItems.BACK.clone();

        backButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.HOME.open(player);
        });

        menu.setButton(backButton, menu.getSize().getSlots() - 9 + 3);

        return menu;
    }

}

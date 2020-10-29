package nl.timvandijkhuizen.commerce.menu.content.categories;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.elements.Category;
import nl.timvandijkhuizen.commerce.helpers.ValidationHelper;
import nl.timvandijkhuizen.commerce.menu.Menus;
import nl.timvandijkhuizen.commerce.menu.actions.ActionCategoryList;
import nl.timvandijkhuizen.commerce.services.CategoryService;
import nl.timvandijkhuizen.spigotutils.data.DataArguments;
import nl.timvandijkhuizen.spigotutils.menu.Menu;
import nl.timvandijkhuizen.spigotutils.menu.MenuSize;
import nl.timvandijkhuizen.spigotutils.menu.PredefinedMenu;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItemBuilder;
import nl.timvandijkhuizen.spigotutils.menu.items.MenuItems;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class MenuCategoryEdit implements PredefinedMenu {

    @Override
    public Menu create(Player player, DataArguments args) {
        CategoryService categoryService = Commerce.getInstance().getService("categories");
        Category category = args.get(0, new Category());
        Menu menu = new Menu((category.getId() != null ? "Edit" : "Create") + " Category", MenuSize.LG);

        // Icon button
        // ===========================
        MenuItemBuilder iconButton = new MenuItemBuilder();

        iconButton.setTypeGenerator(() -> category.getIcon());
        iconButton.setName(UI.color("Icon", UI.COLOR_PRIMARY, ChatColor.BOLD));

        iconButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            ValidationHelper.addErrorLore(lore, category, "icon");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        iconButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            Menus.CATEGORY_ICON.open(player, category, menu);
        });

        menu.setButton(iconButton, 11);

        // Name button
        // ===========================
        MenuItemBuilder nameButton = new MenuItemBuilder(XMaterial.NAME_TAG);

        nameButton.setName(UI.color("Name", UI.COLOR_PRIMARY, ChatColor.BOLD));

        nameButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            if (category.getName().length() > 0) {
                lore.add(UI.color(category.getName(), UI.COLOR_SECONDARY));
            } else {
                lore.add(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            }

            ValidationHelper.addErrorLore(lore, category, "name");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        nameButton.setClickListener(event -> {
            ConversationFactory factory = new ConversationFactory(Commerce.getInstance());

            UI.playSound(player, UI.SOUND_CLICK);

            Conversation conversation = factory.withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return UI.color("What should be the name of the category?", UI.COLOR_PRIMARY);
                }

                @Override
                public Prompt acceptInput(ConversationContext context, String input) {
                    category.setName(input);
                    menu.open(player);
                    return null;
                }
            }).withLocalEcho(false).buildConversation(player);

            menu.close(player);
            conversation.begin();
        });

        menu.setButton(nameButton, 13);

        // Description button
        // ===========================
        MenuItemBuilder descriptionButton = new MenuItemBuilder(XMaterial.PAPER);

        descriptionButton.setName(UI.color("Description", UI.COLOR_PRIMARY, ChatColor.BOLD));

        descriptionButton.setLoreGenerator(() -> {
            List<String> lore = new ArrayList<>();
            
            if (category.getDescription().length() > 0) {
                String[] lines = WordUtils.wrap(category.getDescription(), 40).split("\n");

                for (String line : lines) {
                    lore.add(UI.color(line, UI.COLOR_SECONDARY));
                }
            } else {
                lore.add(UI.color("None", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            }

            ValidationHelper.addErrorLore(lore, category, "description");
            
            lore.add("");
            lore.add(UI.color("Left-click to edit.", UI.COLOR_SECONDARY, ChatColor.ITALIC));
            
            return lore;
        });

        // Set click listener
        descriptionButton.setClickListener(event -> {
            ConversationFactory factory = new ConversationFactory(Commerce.getInstance());

            UI.playSound(player, UI.SOUND_CLICK);

            Conversation conversation = factory.withFirstPrompt(new StringPrompt() {
                @Override
                public String getPromptText(ConversationContext context) {
                    return UI.color("What should be the description of the category?", UI.COLOR_PRIMARY);
                }

                @Override
                public Prompt acceptInput(ConversationContext context, String input) {
                    category.setDescription(input);
                    menu.open(player);
                    return null;
                }
            }).withLocalEcho(false).buildConversation(player);

            menu.close(player);
            conversation.begin();
        });

        menu.setButton(descriptionButton, 15);

        // Set bottom line
        // ===========================
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 0);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 1);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 2);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 4);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 6);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 7);
        menu.setButton(MenuItems.BACKGROUND, menu.getSize().getSlots() - 9 + 8);

        // Cancel button
        // ===========================
        MenuItemBuilder cancelButton = MenuItems.CANCEL.clone();

        cancelButton.setClickListener(new ActionCategoryList());

        menu.setButton(cancelButton, menu.getSize().getSlots() - 9 + 3);

        // Save button
        // ===========================
        MenuItemBuilder saveButton = MenuItems.SAVE.clone();

        saveButton.setClickListener(event -> {
            UI.playSound(player, UI.SOUND_CLICK);
            
            saveButton.setLore(UI.color("Saving...", UI.COLOR_TEXT));
            menu.disableButtons();
            menu.refresh();

            // Save category
            categoryService.saveCategory(category, success -> {
                menu.enableButtons();

                if (success) {
                    UI.playSound(player, UI.SOUND_SUCCESS);
                    saveButton.setLore("");
                } else {
                    UI.playSound(player, UI.SOUND_ERROR);
                    saveButton.setLore(UI.color("Error: Field contains an invalid value.", UI.COLOR_ERROR));
                }
                
                menu.refresh();
            });
        });

        menu.setButton(saveButton, menu.getSize().getSlots() - 9 + 5);

        return menu;
    }

}

package nl.timvandijkhuizen.custompayments.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import nl.timvandijkhuizen.custompayments.CustomPayments;
import nl.timvandijkhuizen.custompayments.menu.Menus;
import nl.timvandijkhuizen.custompayments.services.OrderService;
import nl.timvandijkhuizen.spigotutils.commands.BaseCommand;
import nl.timvandijkhuizen.spigotutils.ui.UI;

public class CommandOrders extends BaseCommand {

    @Override
    public String getCommand() {
        return "orders";
    }

    @Override
    public String getUsage() {
        return "/custompayments orders";
    }

    @Override
    public void onPlayerUse(Player player, String[] args) throws Exception {
        OrderService orderService = CustomPayments.getInstance().getService("orders");

        player.sendMessage(UI.color("Loading...", UI.TEXT_COLOR));

        // Create menu
        orderService.getOrders(orders -> {
            if (orders == null) {
                player.sendMessage(UI.color("Failed to load orders.", UI.ERROR_COLOR));
                return;
            }

            Menus.ORDER_LIST.open(player, orders);
        });
    }

    @Override
    public void onConsoleUse(CommandSender console, String[] args) throws Exception {
        console.sendMessage(UI.color("You must be a player to use this command.", UI.ERROR_COLOR));
    }

}
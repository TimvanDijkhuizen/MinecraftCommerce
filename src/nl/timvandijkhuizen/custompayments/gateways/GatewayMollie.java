package nl.timvandijkhuizen.custompayments.gateways;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.Material;

import nl.timvandijkhuizen.custompayments.base.GatewayType;
import nl.timvandijkhuizen.custompayments.elements.Order;
import nl.timvandijkhuizen.custompayments.elements.Transaction;
import nl.timvandijkhuizen.custompayments.elements.TransactionResponse;
import nl.timvandijkhuizen.spigotutils.config.ConfigIcon;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.ConfigTypes;

public class GatewayMollie implements GatewayType {

    @Override
    public String getName() {
        return "Mollie";
    }

    @Override
    public String getHandle() {
        return "mollie";
    }
    
    @Override
    public Material getIcon() {
        return Material.LIGHT_BLUE_TERRACOTTA;
    }

    @Override
    public Collection<ConfigOption<?>> getOptions() {
        return Arrays.asList(
            new ConfigOption<>("apiKey", ConfigTypes.PASSWORD).setIcon(new ConfigIcon(Material.OAK_SIGN, "Api Key")).setRequired(true)
        );
    }
    
    @Override
    public boolean supportsCallback() {
        return true;
    }

    @Override
    public Transaction createTransaction(Order order) {
        return null;
    }

    @Override
    public void processCallback(TransactionResponse response) {

    }

}

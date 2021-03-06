package nl.timvandijkhuizen.commerce.elements;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import nl.timvandijkhuizen.commerce.base.Element;
import nl.timvandijkhuizen.commerce.config.objects.StoreCurrency;
import nl.timvandijkhuizen.commerce.config.sources.OrderFieldData;
import nl.timvandijkhuizen.commerce.helpers.ShopHelper;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.data.DataList;

public class Order extends Element {

    public static final String SCENARIO_FIELDS = "fields";
    public static final String SCENARIO_GATEWAYS = "gateways";
    public static final String SCENARIO_PAY = "pay";

    private UUID uniqueId;
    private UUID playerUniqueId;
    private String playerName;
    private StoreCurrency currency;
    private DataList<LineItem> lineItems;
    private OrderFieldData fieldData;
    private Gateway gateway;
    
    private boolean completed;
    private Set<Transaction> transactions;

    public Order(int id, UUID uniqueId, UUID playerUniqueId, String playerName, StoreCurrency currency, DataList<LineItem> lineItems, OrderFieldData fieldData, Gateway gateway, boolean completed, Set<Transaction> transactions) {
        this.setId(id);
        this.uniqueId = uniqueId;
        this.playerUniqueId = playerUniqueId;
        this.playerName = playerName;
        this.currency = currency;
        this.lineItems = lineItems;
        this.fieldData = fieldData;
        this.gateway = gateway;
        this.completed = completed;
        this.transactions = transactions;
    }

    public Order(UUID uniqueId, UUID playerUniqueId, String playerName, StoreCurrency currency) {
        this.uniqueId = uniqueId;
        this.playerUniqueId = playerUniqueId;
        this.playerName = playerName;
        this.currency = currency;
        this.lineItems = new DataList<>();
        this.fieldData = new OrderFieldData();
        this.transactions = new HashSet<>();
    }

    @Override
    protected boolean validate(String scenario) {
        if (uniqueId == null) {
            addError("uniqueId", "Unique ID is required");
            return false;
        }

        if (playerUniqueId == null) {
            addError("playerUniqueId", "Player unique id is required");
            return false;
        }

        if (playerName == null || playerName.length() == 0) {
            addError("playerName", "Player name is required");
            return false;
        }

        if (currency == null) {
            addError("currency", "Currency is required");
            return false;
        }

        if (fieldData == null) {
            addError("fields", "Fields is required");
            return false;
        }

        if (scenario.equals(SCENARIO_FIELDS) || scenario.equals(SCENARIO_PAY)) {
            Collection<ConfigOption<?>> options = fieldData.getOptions();
            boolean fieldsValid = true;

            for (ConfigOption<?> option : options) {
                if (option.isRequired() && option.isValueEmpty(fieldData)) {
                    addError(option.getPath(), "Field \"" + option.getName() + "\" is required");
                    fieldsValid = false;
                }
            }

            if (!fieldsValid) {
                return false;
            }
        }

        if ((scenario.equals(SCENARIO_GATEWAYS) || scenario.equals(SCENARIO_PAY)) && gateway == null) {
            addError("gateway", "Gateway is required");
            return false;
        }

        return true;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public UUID getPlayerUniqueId() {
        return playerUniqueId;
    }

    public String getPlayerName() {
        return playerName;
    }
    
    public void updatePlayerName(String playerName) {
        this.playerName = playerName;
    }

    public StoreCurrency getCurrency() {
        return currency;
    }

    public void setCurrency(StoreCurrency currency) {
        this.currency = currency;
    }

    public void addLineItem(LineItem lineItem) {
        Stream<LineItem> stream = StreamSupport.stream(lineItems.spliterator(), false);

        // Merge with existing or create new
        LineItem existing = stream
            .filter(i -> i.getProductId() == lineItem.getProductId())
            .findFirst()
            .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + lineItem.getQuantity());
        } else {
            lineItems.add(lineItem);
        }
    }

    public void removeLineItem(LineItem lineItem) {
        lineItems.remove(lineItem);
    }

    public DataList<LineItem> getLineItems() {
        return lineItems;
    }

    public OrderFieldData getFieldData() {
        return fieldData;
    }

    public Gateway getGateway() {
        return gateway;
    }

    public void setGateway(Gateway gateway) {
        this.gateway = gateway;
    }
    
    public Set<Transaction> getTransactions() {
        return transactions;
    }
    
    public float getTotal() {
        float total = 0;

        for (LineItem item : lineItems) {
            total += item.getPrice();
        }

        return total;
    }
    
    public float getAmountPaid() {
        StoreCurrency baseCurrency = ShopHelper.getBaseCurrency();
        float paid = 0;

        for (Transaction transaction : transactions) {
            float amount = transaction.getAmount();
            StoreCurrency transactionCurrency = transaction.getCurrency();
                
            paid += ShopHelper.convertPrice(amount, transactionCurrency, baseCurrency);
        }

        return paid;
    }
    
    public boolean isCompleted() {
        return completed;
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}

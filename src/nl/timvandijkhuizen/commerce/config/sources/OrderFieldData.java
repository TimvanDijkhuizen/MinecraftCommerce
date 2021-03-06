package nl.timvandijkhuizen.commerce.config.sources;

import java.util.Collection;

import com.google.gson.JsonObject;

import nl.timvandijkhuizen.commerce.Commerce;
import nl.timvandijkhuizen.commerce.services.FieldService;
import nl.timvandijkhuizen.spigotutils.config.ConfigOption;
import nl.timvandijkhuizen.spigotutils.config.sources.JsonConfig;

public class OrderFieldData extends JsonConfig {

    public OrderFieldData(JsonObject json) {
        super(json);
    }

    public OrderFieldData() {
        super();
    }

    @Override
    public void addOption(ConfigOption<?> option) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addOptions(Collection<ConfigOption<?>> options) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<ConfigOption<?>> getOptions() {
        FieldService fieldService = Commerce.getInstance().getService(FieldService.class);
        return fieldService.getOptions();
    }

}

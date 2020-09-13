package nl.timvandijkhuizen.commerce.events;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import nl.timvandijkhuizen.commerce.base.FieldType;

public class RegisterFieldTypesEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private Set<FieldType<?>> types = new LinkedHashSet<>();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    /**
     * Adds a field type.
     * 
     * @param type
     */
    public void addType(FieldType<?> type) {
        types.add(type);
    }

    /**
     * Returns all registered field types.
     * 
     * @return
     */
    public Set<FieldType<?>> getTypes() {
        return types;
    }

}

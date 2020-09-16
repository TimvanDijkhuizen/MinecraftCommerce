package nl.timvandijkhuizen.commerce.events;

import java.util.HashMap;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import nl.timvandijkhuizen.commerce.base.Storage;

public class RegisterStorageTypesEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private HashMap<String, Class<? extends Storage>> storageTypes = new HashMap<>();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public void addStorageType(String handle, Class<? extends Storage> storageType) {
        storageTypes.put(handle, storageType);
    }

    public HashMap<String, Class<? extends Storage>> getStorageTypes() {
        return storageTypes;
    }

}
package util;

import java.util.HashMap;
import java.util.UUID;

public class StorageManager {
    public static Object get(String id) {
        return storage.get(id);
    }

    public static String add(Object t) {
        UUID id = UUID.randomUUID();
        String uuidAsString = id.toString();
        String storageId = t.getClass().getName() + "::" + uuidAsString;
        storage.put(storageId, t);
        return storageId;
    }

    private StorageManager() {
    }

    private static HashMap<String, Object> storage = new HashMap<>();
}
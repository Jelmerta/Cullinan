package util;

import java.util.HashMap;
import java.util.UUID;

public class StorageManager {
    public static Object get(String id) {
        return storage.get(id);
    }

    public static String addUUID(Object t) {
        UUID id = UUID.randomUUID();
        String uuidAsString = id.toString();
        String className = t.getClass().getName();
        String storageId = className + "::" + uuidAsString;
        storage.put(storageId, t);
        return storageId;
    }

    public static String add(Object t) {
        String id = (t.getClass().getName() + "::") + t.hashCode();
        Object object = storage.get(id);
        if (object == null) {
            storage.put(id, t);
        } else {
            System.out.println("Warning: (We think) Variable already exists. Not adding again. " + t);
        }
        return id;
    }

    private StorageManager() {
    }

    private static HashMap<String, Object> storage = new HashMap<>();
}
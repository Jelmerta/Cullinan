package cullinan.helpers.decomposition.javagenerators;

import helpers.SpoonWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import spoon.reflect.declaration.CtClass;

// TODO Order of fields/methods a bit off...
// TODO We could have id generation in a helper
class StorageManagerCreatorTest {
    @Test
    void testStorageCreation() {
        StorageManagerCreator storageCreator = new StorageManagerCreator();
        CtClass storage = storageCreator.build();
        String output = SpoonWriter.write(storage);
        Assertions.assertEquals("public class StorageManager {\n" +
                "    public static Object get(String id) {\n" +
                "        return storage.get(id);\n" +
                "    }\n" +
                "\n" +
                "    public static String add(Object t) {\n" +
                "        UUID id = UUID.randomUUID();\n" +
                "        String uuidAsString = id.toString();\n" +
                "        String storageId = (t.getClass().getName() + \"::\") + uuidAsString;\n" +
                "        storage.put(storageId, t);\n" +
                "        return storageId;\n" +
                "    }\n" +
                "\n" +
                "    private StorageManager() {\n" +
                "    }\n" +
                "\n" +
                "    private static HashMap<String, Object> storage = new HashMap<>();\n" +
                "}", output);
    }
}
package cullinan.helpers.decomposition.javagenerators;

import helpers.SpoonWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;

class CullinanIdCreatorTest {
    @Test
    void testStorageCreation() {
        CullinanIdCreator referenceIdCreator = new CullinanIdCreator();
        CtClass referenceId = referenceIdCreator.build();
        String output = SpoonWriter.write(referenceId);
        Assertions.assertEquals("public class CullinanId {\n" +
                "    private final String id;\n" +
                "\n" +
                "    public CullinanId(String id) {\n" +
                "        this.id = id;\n" +
                "    }\n" +
                "\n" +
                "    public String getValue() {\n" +
                "        return id;\n" +
                "    }\n" +
                "}", output);
    }

}
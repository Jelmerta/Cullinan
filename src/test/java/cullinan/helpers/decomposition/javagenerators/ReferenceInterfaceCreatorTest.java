package cullinan.helpers.decomposition.javagenerators;

import helpers.SpoonWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import spoon.reflect.declaration.CtInterface;

class ReferenceInterfaceCreatorTest {
    @Test
    void testStorageCreation() {
        ReferenceInterfaceCreator referenceInterfaceCreator = new ReferenceInterfaceCreator();
        CtInterface referenceInterface = referenceInterfaceCreator.build();
        String output = SpoonWriter.write(referenceInterface);
        Assertions.assertEquals("public interface CullinanReference {\n" +
                "    String getReferenceId();\n" +
                "\n" +
                "    void setReferenceId(String referenceId);\n" +
                "}", output);
    }

}
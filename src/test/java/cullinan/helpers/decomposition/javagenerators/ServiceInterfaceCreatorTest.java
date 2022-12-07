package cullinan.helpers.decomposition.javagenerators;

import helpers.SpoonWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;
import spoonhelpers.managers.SpoonFactoryManager;

public class ServiceInterfaceCreatorTest {

    @Test
    void testInterfaceCreation() {
        CtClass originalClass = SpoonFactoryManager.getDefaultFactory().Class().get("cullinan.test.imports.SimpleClass");

        ServiceInterfaceCreator serviceInterfaceCreator = new ServiceInterfaceCreator(originalClass);
        CtInterface ctInterface = serviceInterfaceCreator.buildInterface();

        String output = SpoonWriter.write(ctInterface);
        Assertions.assertEquals("public interface SimpleClassInterface extends Remote {\n" +
                            "    String getValue(String referenceId) throws RemoteException;\n" +
                            "\n" +
                            "    String newSimpleClass(String parameterEncoded) throws RemoteException;\n" +
                            "}", output);
    }
}

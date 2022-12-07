package cullinan.helpers.decomposition.generators;

import cullinan.helpers.decomposition.javagenerators.ReferenceInterfaceCreator;
import cullinan.helpers.decomposition.javagenerators.SerializationUtilCreator;
import cullinan.helpers.decomposition.javagenerators.StorageManagerCreator;
import cullinan.helpers.decomposition.javagenerators.CullinanIdCreator;
import cullinan.helpers.decomposition.generators.model.GeneratedHelperClasses;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;

public class HelpersGenerator {
    public HelpersGenerator() {

    }

    public GeneratedHelperClasses generate() {
        GeneratedHelperClasses generatedHelperClasses = new GeneratedHelperClasses();

        CtClass referenceId = new CullinanIdCreator().build();
        generatedHelperClasses.addReferenceId(referenceId);

        CtInterface referenceInterface = new ReferenceInterfaceCreator().build();
        generatedHelperClasses.addReferenceInterface(referenceInterface);

        CtClass storage = new StorageManagerCreator().build();
        generatedHelperClasses.addStorageClass(storage);

        CtClass serializationUtil = new SerializationUtilCreator().build();
        generatedHelperClasses.addSerializationUtil(serializationUtil);

        return generatedHelperClasses;
    }
}

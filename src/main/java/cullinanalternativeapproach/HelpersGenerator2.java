package cullinanalternativeapproach;

import cullinan.helpers.decomposition.generators.model.GeneratedHelperClasses;
import cullinan.helpers.decomposition.javagenerators.CullinanIdCreator;
import cullinan.helpers.decomposition.javagenerators.ReferenceInterfaceCreator;
import cullinan.helpers.decomposition.javagenerators.SerializationUtilCreator;
import cullinan.helpers.decomposition.javagenerators.StorageManagerCreator;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;

public class HelpersGenerator2 {
    public HelpersGenerator2() {

    }

    public GeneratedHelperClasses2 generate() {
        GeneratedHelperClasses2 generatedHelperClasses = new GeneratedHelperClasses2();

        ReferenceId referenceId = new ReferenceId();
        generatedHelperClasses.addReferenceId(referenceId);

        ReferenceInterface referenceInterface = new ReferenceInterface();
        generatedHelperClasses.addReferenceInterface(referenceInterface);

        Storage storage = new Storage();
        generatedHelperClasses.addStorageClass(storage);

        SerializationUtil serializationUtil = new SerializationUtil();
        generatedHelperClasses.addSerializationUtil(serializationUtil);

        return generatedHelperClasses;
    }
}

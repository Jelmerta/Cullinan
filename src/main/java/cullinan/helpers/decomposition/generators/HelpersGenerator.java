package cullinan.helpers.decomposition.generators;

import cullinan.helpers.decomposition.generators.model.GeneratedHelperClasses;
import generatedfiles.ReferenceId;
import generatedfiles.ReferenceInterface;
import generatedfiles.SerializationUtil;
import generatedfiles.Storage;

public class HelpersGenerator {
    public HelpersGenerator() {

    }

    public GeneratedHelperClasses generate() {
        GeneratedHelperClasses generatedHelperClasses = new GeneratedHelperClasses();

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

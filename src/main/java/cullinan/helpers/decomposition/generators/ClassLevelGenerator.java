package cullinan.helpers.decomposition.generators;

import cullinan.helpers.decomposition.javagenerators.ProxyCreator;
import cullinan.helpers.decomposition.javagenerators.ServiceCreator;
import cullinan.helpers.decomposition.javagenerators.ServiceInterfaceCreator;
import cullinan.helpers.decomposition.javagenerators.ServiceOriginalClassWithIdCreator;
import cullinan.helpers.decomposition.javagenerators.ClientCreator;
import cullinan.helpers.decomposition.generators.model.GeneratedData;
import cullinan.helpers.decomposition.generators.model.GeneratedClassLevel;
import cullinan.helpers.decomposition.generators.model.GeneratedClientService;
import cullinan.helpers.decomposition.generators.model.GeneratedHelperClasses;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;

public class ClassLevelGenerator {
    private final GeneratedData generatedData;

    // TODO Generate the not implemented version...?
    // TODO Make sure the not implemented version is added to other services.
    // TODO We could use the class definitions for this maybe? although this is generated later... Not dependent on this data though...
    // TODO Guess we could just always generate it, only a little bit too much data generated.
    public ClassLevelGenerator(GeneratedData generatedData) {
        this.generatedData = generatedData;
    }

    public GeneratedClassLevel generate(CtClass originalClass) {
        GeneratedHelperClasses helpers = generatedData.getHelpers();

        CtInterface referenceInterface = helpers.getReferenceInterface();
        CtClass storageClass = helpers.getStorageClass(); // TODO Should require reference interface and set id
        CtClass serializationUtil = helpers.getSerializationUtil();

        CtClass serviceOriginalClass = new ServiceOriginalClassWithIdCreator(originalClass, referenceInterface).build();
        CtInterface serviceInterface = new ServiceInterfaceCreator(originalClass).buildInterface();
        CtClass client = new ClientCreator(originalClass, serviceInterface, serializationUtil).build();
        CtClass service = new ServiceCreator(originalClass, serviceInterface, storageClass, serializationUtil, serviceOriginalClass).build(); // TODO Should only need original class. Currently only using setId from classWithId, but id should be set in storagemanager anyway.
        CtClass proxy = new ProxyCreator(originalClass, referenceInterface, client, serializationUtil).build();

        GeneratedClientService generatedClientService = new GeneratedClientService();
        generatedClientService.addServiceInterface(serviceInterface);
        generatedClientService.addClient(client);
        generatedClientService.addService(service);

        String qualifiedName = originalClass.getQualifiedName();
        return new GeneratedClassLevel(qualifiedName, serviceOriginalClass, generatedClientService, proxy);
    }
}

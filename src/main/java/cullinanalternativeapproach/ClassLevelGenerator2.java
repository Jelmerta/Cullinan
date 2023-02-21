package cullinanalternativeapproach;

public class ClassLevelGenerator2 {
    private final GeneratedData2 generatedData;

    // TODO Generate the not implemented version...?
    // TODO Make sure the not implemented version is added to other services.
    // TODO We could use the class definitions for this maybe? although this is generated later... Not dependent on this data though...
    // TODO Guess we could just always generate it, only a little bit too much data generated.
    public ClassLevelGenerator2(GeneratedData2 generatedData) {
        this.generatedData = generatedData;
    }

    public GeneratedClassLevel2 generate(OriginalJava originalJava) {
        GeneratedHelperClasses2 helpers = generatedData.getHelpers();

        ReferenceInterface referenceInterface = helpers.getReferenceInterface();
        Storage storage = helpers.getStorageClass(); // TODO Should require reference interface and set id
        SerializationUtil serializationUtil = helpers.getSerializationUtil();

        Implementation serviceOriginalClass = new Implementation(originalJava, referenceInterface);
        ServiceInterface serviceInterface = new ServiceInterface(originalJava);
        Client client = new Client(originalJava, serviceInterface, serializationUtil);
        Service service = new Service(originalJava, serviceInterface, storage, serializationUtil, serviceOriginalClass); // TODO Should only need original class. Currently only using setId from classWithId, but id should be set in storagemanager anyway.
        Proxy proxy = new Proxy(originalJava, referenceInterface, client, serializationUtil);

        GeneratedClientService2 generatedClientService = new GeneratedClientService2();
        generatedClientService.addServiceInterface(serviceInterface);
        generatedClientService.addClient(client);
        generatedClientService.addService(service);

        return new GeneratedClassLevel2(originalJava.getJava().getQualifiedName(), serviceOriginalClass, generatedClientService, proxy);
    }
}

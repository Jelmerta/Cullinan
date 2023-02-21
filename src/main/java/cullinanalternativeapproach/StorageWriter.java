package cullinanalternativeapproach;

import java.util.List;

public class StorageWriter implements DataWriter2 {
    // TODO Currently main service depends on storage, even though there should not be anything stored atm. SerializationUtil should never have to access storage in main. Making a separate class only for main seems kind of silly though...
    private static final List<WriteDefinition> WRITE_DEFINITIONS = List.of(WriteDefinition.MAIN_SERVICE, WriteDefinition.OTHER_MICROSERVICES, WriteDefinition.THIS_MICROSERVICE);
    private static final JavaWriter3 javaWriter = new JavaWriter3();
    private final Storage storage;
    private final ServiceWriteDefinition serviceWriteDefinition;

    public StorageWriter(Storage storage) {
        this.storage = storage;
        this.serviceWriteDefinition = new ServiceWriteDefinition(WRITE_DEFINITIONS);
    }

    @Override
    public void write(ServiceDefinition serviceDefinition) {
        if (shouldWrite(serviceDefinition)) {
            javaWriter.write(serviceDefinition.getOutputPath(), storage.getJava());
        } else {
            throw new IllegalArgumentException("Service should not be written to"); // TODO... DO we want this
        }
    }

    public boolean shouldWrite(ServiceDefinition serviceDefinition) {
        return serviceWriteDefinition.shouldWrite(serviceDefinition);
        // TODO Separate Microservice check?
    }
}

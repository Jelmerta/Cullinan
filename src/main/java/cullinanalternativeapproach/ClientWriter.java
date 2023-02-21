package cullinanalternativeapproach;

import java.util.List;

public class ClientWriter implements DataWriter2 {
    private static final List<WriteDefinition> WRITE_DEFINITIONS = List.of(WriteDefinition.MAIN_SERVICE, WriteDefinition.OTHER_MICROSERVICES);
    private static final JavaWriter3 javaWriter = new JavaWriter3();
    private final Client client;
    private final ServiceWriteDefinition serviceWriteDefinition;

    public ClientWriter(Client client, String serviceOrigin) {
        this.client = client;
        this.serviceWriteDefinition = new ServiceWriteDefinition(WRITE_DEFINITIONS, serviceOrigin);
    }

    @Override
    public void write(ServiceDefinition serviceDefinition) {
        if (shouldWrite(serviceDefinition)) {
            javaWriter.write(serviceDefinition.getOutputPath(), client.getJava());
        } else {
            throw new IllegalArgumentException("Service should not be written to"); // TODO... DO we want this
        }
    }

    public boolean shouldWrite(ServiceDefinition serviceDefinition) {
        return serviceWriteDefinition.shouldWrite(serviceDefinition);
        // TODO Separate Microservice check?
    }
}

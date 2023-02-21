package cullinanalternativeapproach;

import java.util.List;

public class ReferenceInterfaceWriter implements DataWriter2 {
    private static final List<WriteDefinition> WRITE_DEFINITIONS = List.of(WriteDefinition.MAIN_SERVICE, WriteDefinition.OTHER_MICROSERVICES, WriteDefinition.THIS_MICROSERVICE);
    private static final JavaWriter3 javaWriter = new JavaWriter3();
    private final ReferenceInterface referenceInterface;
    private final ServiceWriteDefinition serviceWriteDefinition;

    public ReferenceInterfaceWriter(ReferenceInterface referenceInterface) {
        this.referenceInterface = referenceInterface;
        this.serviceWriteDefinition = new ServiceWriteDefinition(WRITE_DEFINITIONS);
    }

    @Override
    public void write(ServiceDefinition serviceDefinition) {
        if (shouldWrite(serviceDefinition)) {
            javaWriter.write(serviceDefinition.getOutputPath(), referenceInterface.getJava());
        } else {
            throw new IllegalArgumentException("Service should not be written to"); // TODO... DO we want this
        }
    }

    public boolean shouldWrite(ServiceDefinition serviceDefinition) {
        return serviceWriteDefinition.shouldWrite(serviceDefinition);
        // TODO Separate Microservice check?
    }
}

package cullinanalternativeapproach;

import java.util.List;

public class MicroserviceMainWriter implements DataWriter2 {
    private static final List<WriteDefinition> WRITE_DEFINITIONS = List.of(WriteDefinition.THIS_MICROSERVICE, WriteDefinition.OTHER_MICROSERVICES); // Just all microservices TODO
    private static final JavaWriter3 javaWriter = new JavaWriter3();
    private final MicroserviceMain microserviceMain;
    private final ServiceWriteDefinition serviceWriteDefinition;

    public MicroserviceMainWriter(MicroserviceMain microserviceMain) {
        this.microserviceMain = microserviceMain;
        serviceWriteDefinition = new ServiceWriteDefinition(WRITE_DEFINITIONS);
    }

    @Override
    public boolean shouldWrite(ServiceDefinition serviceDefinition) {
        return serviceWriteDefinition.shouldWrite(serviceDefinition);
    }

    @Override
    public void write(ServiceDefinition serviceDefinition) {
        if (serviceWriteDefinition.shouldWrite(serviceDefinition)) {
            javaWriter.write(serviceDefinition.getOutputPath(), microserviceMain.getJava());
        }
    }
}

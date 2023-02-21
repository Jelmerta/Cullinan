package writers;

import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.JavaWriter;
import cullinan.helpers.decomposition.writers.ServiceWriteDefinition;
import cullinan.helpers.decomposition.writers.WriteDefinition;
import generatedfiles.MicroserviceMain;
import generatedfiles.ServiceDefinition;

import java.util.List;

public class MicroserviceMainWriter implements DataWriter {
    private static final List<WriteDefinition> WRITE_DEFINITIONS = List.of(WriteDefinition.THIS_MICROSERVICE, WriteDefinition.OTHER_MICROSERVICES); // Just all microservices TODO
    private static final JavaWriter javaWriter = new JavaWriter();
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

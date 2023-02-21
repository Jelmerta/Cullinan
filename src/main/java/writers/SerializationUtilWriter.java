package writers;

import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.JavaWriter;
import cullinan.helpers.decomposition.writers.ServiceWriteDefinition;
import cullinan.helpers.decomposition.writers.WriteDefinition;
import generatedfiles.SerializationUtil;
import generatedfiles.ServiceDefinition;

import java.util.List;

public class SerializationUtilWriter implements DataWriter {
    private static final List<WriteDefinition> WRITE_DEFINITIONS = List.of(WriteDefinition.MAIN_SERVICE, WriteDefinition.OTHER_MICROSERVICES, WriteDefinition.THIS_MICROSERVICE);
    private static final JavaWriter javaWriter = new JavaWriter();
    private final SerializationUtil serializationUtil;
    private final ServiceWriteDefinition serviceWriteDefinition;

    public SerializationUtilWriter(SerializationUtil serializationUtil) {
        this.serializationUtil = serializationUtil;
        this.serviceWriteDefinition = new ServiceWriteDefinition(WRITE_DEFINITIONS);
    }

    @Override
    public void write(ServiceDefinition serviceDefinition) {
        if (shouldWrite(serviceDefinition)) {
            javaWriter.write(serviceDefinition.getOutputPath(), serializationUtil.getJava());
        } else {
            throw new IllegalArgumentException("Service should not be written to"); // TODO... DO we want this
        }
    }

    public boolean shouldWrite(ServiceDefinition serviceDefinition) {
        return serviceWriteDefinition.shouldWrite(serviceDefinition);
        // TODO Separate Microservice check?
    }
}

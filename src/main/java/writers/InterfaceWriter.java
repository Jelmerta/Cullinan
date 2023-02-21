package writers;

import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.JavaWriter;
import cullinan.helpers.decomposition.writers.ServiceWriteDefinition;
import cullinan.helpers.decomposition.writers.WriteDefinition;
import generatedfiles.ServiceDefinition;
import generatedfiles.ServiceInterface;

import java.util.List;

public class InterfaceWriter implements DataWriter {
    private static final List<WriteDefinition> WRITE_DEFINITIONS = List.of(WriteDefinition.INTERFACE_MODULE);
    private static final JavaWriter javaWriter = new JavaWriter();
    private final ServiceInterface interfaceWritable;
    private final ServiceWriteDefinition serviceWriteDefinition;

    public InterfaceWriter(ServiceInterface interfaceWritable) {
        this.interfaceWritable = interfaceWritable;
        this.serviceWriteDefinition = new ServiceWriteDefinition(WRITE_DEFINITIONS);
    }

    @Override
    public void write(ServiceDefinition serviceDefinition) {
        if (shouldWrite(serviceDefinition)) {
            javaWriter.write(serviceDefinition.getOutputPath(), interfaceWritable.getJava());
        } else {
            throw new IllegalArgumentException("Service should not be written to"); // TODO... DO we want this
        }
    }

    public boolean shouldWrite(ServiceDefinition serviceDefinition) {
        return serviceWriteDefinition.shouldWrite(serviceDefinition);
        // TODO Separate Microservice check?
    }
}


package cullinan.helpers.decomposition.writers;

import generatedfiles.ServiceDefinition;

// I know where to write and how to write and what to write!
public interface DataWriter {
    boolean shouldWrite(ServiceDefinition serviceDefinition);
    void write(ServiceDefinition serviceDefinition);
}

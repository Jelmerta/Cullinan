package cullinan.helpers.decomposition.writers;

import generatedfiles.ServiceDefinition;

import java.util.List;

public class ServiceWriteDefinition {
    private final List<WriteDefinition> writeDefinitions;

    // TODO nullable field, kind of ugly. Maybe we can have different write definitions
    private final String serviceOrigin; // Nullable

    public ServiceWriteDefinition(List<WriteDefinition> writeDefinitions) {
        this.writeDefinitions = writeDefinitions;
        this.serviceOrigin = null;
    }

    public ServiceWriteDefinition(List<WriteDefinition> writeDefinitions, String serviceOrigin) {
        this.writeDefinitions = writeDefinitions;
        this.serviceOrigin = serviceOrigin;
    }

    public boolean shouldWrite(ServiceDefinition serviceDefinition) {
        if (serviceDefinition.getServiceType().equals(ServiceType.MAIN_SERVICE)) {
            return writeDefinitions.contains(WriteDefinition.MAIN_SERVICE);
        }

        if (serviceDefinition.getServiceType().equals(ServiceType.INTERFACE_MODULE)) {
            return writeDefinitions.contains(WriteDefinition.INTERFACE_MODULE);
        }

        if (serviceDefinition.getServiceType().equals(ServiceType.MICROSERVICE)) {
            if (serviceOrigin == null) {
                return writeDefinitions.contains(WriteDefinition.THIS_MICROSERVICE) || writeDefinitions.contains(WriteDefinition.OTHER_MICROSERVICES);
            }

            if (writeDefinitions.contains(WriteDefinition.OTHER_MICROSERVICES)) {
                return !serviceDefinition.getName().equalsIgnoreCase(serviceOrigin);
            }

            if (writeDefinitions.contains(WriteDefinition.THIS_MICROSERVICE)) {
                return serviceDefinition.getName().equalsIgnoreCase(serviceOrigin);
            }
        }

        return false;
    }
}

package cullinanalternativeapproach;

import java.util.List;

public class ServiceDefinition {
    ServiceType serviceType;
    String name;
    String outputPath;
    List<String> classNames;

    public ServiceDefinition(ServiceType serviceType, String name, String outputPath, List<String> classNames) {
        this.serviceType = serviceType;
        this.name = name;
        this.outputPath = outputPath;
        this.classNames = classNames;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public String getName() {
        return name;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public List<String> getClassNames() {
        return classNames;
    }
}

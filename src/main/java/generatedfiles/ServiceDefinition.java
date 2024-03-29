package generatedfiles;

import cullinan.helpers.decomposition.writers.ModuleType;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceDefinition {
    ModuleType serviceType;
    String name;
    Path outputPath;
    List<String> classNames;

    public ServiceDefinition(ModuleType serviceType, String name, Path outputPath, List<String> classNames) {
        this.serviceType = serviceType;
        this.name = name;
        this.outputPath = outputPath;
        this.classNames = classNames.stream().map(String::toLowerCase).collect(Collectors.toList()); // TODO Probably good idea to just lowercase...?
    }

    public ModuleType getServiceType() {
        return serviceType;
    }

    public String getName() {
        return name;
    }

    public Path getOutputPath() {
        return outputPath;
    }

    public List<String> getClassNames() {
        return classNames;
    }
}

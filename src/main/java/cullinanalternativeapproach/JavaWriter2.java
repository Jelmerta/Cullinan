package cullinan.helpers.decomposition.writers;

import cullinanalternativeapproach.Proxy;
import cullinanalternativeapproach.DataWriter2;
import cullinanalternativeapproach.ServiceDefinition;
import cullinanalternativeapproach.ServiceType;
import cullinanalternativeapproach.WriteDefinition;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.support.JavaOutputProcessor;
import spoonhelpers.managers.SpoonFactoryManager;

import java.io.File;
import java.util.List;

public class JavaWriter2 implements DataWriter2 {
    private final CtType java;
    private final List<WriteDefinition> writeDefinitions;

    private static final JavaOutputProcessor processor;
    static {
        PrettyPrinter prettyPrinterAutoImport = SpoonFactoryManager.getDefaultFactory().getEnvironment().createPrettyPrinterAutoImport();
        processor = new JavaOutputProcessor(prettyPrinterAutoImport);
        processor.setFactory(SpoonFactoryManager.getDefaultFactory());
    }

    // Maybe this is a proxywriter... All definitions from proxy
    // Maybe just pass the writeDefinitions? Keeps one constructor, only CtType and WriteDefinitions?
    public JavaWriter2(Proxy proxy) {
        this.java = proxy.getJava();
        this.writeDefinitions = proxy.getWriteDefinition();
    }

    @Override
    public void write(ServiceDefinition serviceDefinition) {
        if (!shouldWrite(serviceDefinition)) {
            return;
        }

        processor.getEnvironment().setSourceOutputDirectory(new File(serviceDefinition.getOutputPath().toString() + "/src/main/java"));
        processor.createJavaFile(java);
    }

    private boolean shouldWrite(ServiceDefinition serviceDefinition) {
        if (serviceDefinition.getServiceType().equals(ServiceType.MAIN_SERVICE)) {
            return writeDefinitions.contains(WriteDefinition.MAIN_SERVICE);
        }

        if (serviceDefinition.getServiceType().equals(ServiceType.INTERFACE_MODULE)) {
            return writeDefinitions.contains(WriteDefinition.INTERFACE_MODULE);
        }

        if (serviceDefinition.getServiceType().equals(ServiceType.MICROSERVICE)) {
            if (writeDefinitions.contains(WriteDefinition.OTHER_MICROSERVICES)) {
                return !serviceDefinition.getClassNames().contains(java.getQualifiedName());
            }

            if (writeDefinitions.contains(WriteDefinition.THIS_MICROSERVICE)) {
                return serviceDefinition.getClassNames().contains(java.getQualifiedName());
            }
        }

        return false;
    }
}

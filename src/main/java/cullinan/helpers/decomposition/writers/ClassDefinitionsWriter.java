package cullinan.helpers.decomposition.writers;

import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.JavaWriter;
import cullinan.helpers.decomposition.writers.ServiceWriteDefinition;
import cullinan.helpers.decomposition.writers.WriteDefinition;
import generatedfiles.ClassDefinitions;
import generatedfiles.ServiceDefinition;

import java.util.List;

public class ClassDefinitionsWriter implements DataWriter {
//    private static final List<WriteDefinition> WRITE_DEFINITIONS = List.of(WriteDefinition.MAIN_SERVICE, WriteDefinition.THIS_MICROSERVICE, WriteDefinition.OTHER_MICROSERVICES); // TODO Just microservices...
    private static final JavaWriter javaWriter = new JavaWriter();
    private final ClassDefinitions classDefinitions;
    private final ServiceWriteDefinition serviceWriteDefinition;

    public ClassDefinitionsWriter(ClassDefinitions classDefinitions, WriteDefinition writeDefinition) {
        this.classDefinitions = classDefinitions;
        serviceWriteDefinition = new ServiceWriteDefinition(List.of(writeDefinition));
    }

    @Override
    public boolean shouldWrite(ServiceDefinition serviceDefinition) {
        return serviceWriteDefinition.shouldWrite(serviceDefinition);
    }

    @Override
    public void write(ServiceDefinition serviceDefinition) {
        if (serviceWriteDefinition.shouldWrite(serviceDefinition)) {
            javaWriter.write(serviceDefinition.getOutputPath(), classDefinitions.getJava());
        }
    }
}

//JavaOutputProcessor processor; // How to write
//PrettyPrinter prettyPrinterAutoImport = SpoonFactoryManager.getDefaultFactory().getEnvironment().createPrettyPrinterAutoImport();
//processor = new JavaOutputProcessor(prettyPrinterAutoImport);
//processor.setFactory(SpoonFactoryManager.getDefaultFactory());processor.getEnvironment().setSourceOutputDirectory(new File(serviceDefinition.getOutputPath().toString() + "/src/main/java"));         processor.createJavaFile(classDefinitions.getJava());


//public class ClassDefinitions {
//    private static final java.util.Set<java.lang.String> serviceClassDefinitions = new java.util.HashSet<>();
//
//    private static final java.util.Set<java.lang.String> proxyClassDefinitions = new java.util.HashSet<>();
//
//    static {
//    }
//
//    static {
//        proxyClassDefinitions.add("se.citerus.dddsample.domain.model.handling.UnknownLocationException");
//        proxyClassDefinitions.add("se.citerus.dddsample.domain.model.location.UnLocode");
//        proxyClassDefinitions.add("se.citerus.dddsample.domain.model.location.SampleLocations");
//        proxyClassDefinitions.add("se.citerus.dddsample.domain.model.location.Location");
//    }
//
//    public static boolean isServiceClass(java.lang.String className) {
//        return serviceClassDefinitions.contains(className.toLowerCase());
//    }
//
//    public static boolean isProxyClass(java.lang.String className) {
//        return proxyClassDefinitions.contains(className.toLowerCase());
//    }
//}
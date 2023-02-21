package cullinan.helpers.decomposition.writers;

import cullinanalternativeapproach.*;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.support.JavaOutputProcessor;
import spoonhelpers.managers.SpoonFactoryManager;

import java.io.File;
import java.util.List;

public class JavaWriter2 implements DataWriter2 {
    private final CtType java; // What to write
    private final List<WriteDefinition> writeDefinitions; // Where to write

    private static final JavaOutputProcessor processor; // How to write

    static {
        PrettyPrinter prettyPrinterAutoImport = SpoonFactoryManager.getDefaultFactory().getEnvironment().createPrettyPrinterAutoImport();
        processor = new JavaOutputProcessor(prettyPrinterAutoImport);
        processor.setFactory(SpoonFactoryManager.getDefaultFactory());
    }

    // Maybe this is a proxywriter... All definitions from proxy
    // Maybe just pass the writeDefinitions? Keeps one constructor, only CtType and WriteDefinitions?
//    public JavaWriter2(ProxyWritable proxy) {
//        this.java = proxy.getJava();
//        this.writeDefinitions = proxy.getWriteDefinition();
//    }

    public JavaWriter2(CtType java, List<WriteDefinition> writeDefinitions) {
        this.java = java;
        this.writeDefinitions = writeDefinitions;
    }

    // Probably needs a certain type of writable to work...
//    public JavaWriter2(Writable2 writable2) {
//
//    }

    @Override
    public boolean shouldWrite(ServiceDefinition serviceDefinition) {
        return false;
    }

    @Override
    public void write(ServiceDefinition serviceDefinition) {
        if (!shouldWrite(serviceDefinition)) {
            return;
        }

        processor.getEnvironment().setSourceOutputDirectory(new File(serviceDefinition.getOutputPath().toString() + "/src/main/java"));
        processor.createJavaFile(java);
    }
}

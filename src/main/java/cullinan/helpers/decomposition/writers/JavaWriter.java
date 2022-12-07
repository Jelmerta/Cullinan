package cullinan.helpers.decomposition.writers;

import cullinanalternativeapproach.ServiceDefinition;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.support.JavaOutputProcessor;
import spoonhelpers.managers.SpoonFactoryManager;

import java.io.File;
import java.nio.file.Path;

public class JavaWriter implements DataWriter {
    private final CtType java;

    private static final JavaOutputProcessor processor;
    static {
        PrettyPrinter prettyPrinterAutoImport = SpoonFactoryManager.getDefaultFactory().getEnvironment().createPrettyPrinterAutoImport();
        processor = new JavaOutputProcessor(prettyPrinterAutoImport);
        processor.setFactory(SpoonFactoryManager.getDefaultFactory());
    }

    public JavaWriter(CtType java) {
        this.java = java;
    }

    @Override
    public void write(Path rootPath) {
        processor.getEnvironment().setSourceOutputDirectory(new File(rootPath.toString() + "/src/main/java"));
        processor.createJavaFile(java);
    }
}

package helpers;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.support.JavaOutputProcessor;
import spoonhelpers.managers.SpoonFactoryManager;

import java.io.File;
import java.nio.file.Path;

public class SpoonWriter {
    private static JavaOutputProcessor processor;

    static {
        PrettyPrinter prettyPrinterAutoImport = SpoonFactoryManager.getDefaultFactory().getEnvironment().createPrettyPrinterAutoImport();
        processor = new JavaOutputProcessor(prettyPrinterAutoImport);
        processor.setFactory(SpoonFactoryManager.getDefaultFactory());
    }

    public static void write(CtClass ctClass, Path path) {
        processor.getEnvironment().setSourceOutputDirectory(new File(path.toString()));
        processor.createJavaFile(ctClass);
    }

    public static String write(CtClass ctClass) {
        return ctClass.toString();
    }

    public static String write(CtInterface ctInterface) {
        return ctInterface.toString();
    }
}

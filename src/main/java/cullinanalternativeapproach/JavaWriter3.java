package cullinanalternativeapproach;

import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.PrettyPrinter;
import spoon.support.JavaOutputProcessor;
import spoonhelpers.managers.SpoonFactoryManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class JavaWriter3 {
    private static final JavaOutputProcessor processor; // How to write
    static {
        PrettyPrinter prettyPrinterAutoImport = SpoonFactoryManager.getDefaultFactory().getEnvironment().createPrettyPrinterAutoImport();
        processor = new JavaOutputProcessor(prettyPrinterAutoImport);
        processor.setFactory(SpoonFactoryManager.getDefaultFactory());
    }

    public JavaWriter3() {

    }

    public void write(Path path, CtType java) {
        java.getPackage().addType(java); // TODO This returns the java from the package by name?? instead of the java passed... Solution now is to overwrite type in package... obviously not amazing...
        processor.getEnvironment().setSourceOutputDirectory(new File(path.toString() + "/src/main/java"));
        processor.createJavaFile(java);
    }

//        public void write(Path path, CtType java) {
//            try {
//                File file = new File(path.toString() + "/src/main/java/" +  java.getPackage().toString().replace(".", "/") + "/" + java.getSimpleName() + ".java"); // TODO original file name with dots?
//                file.getParentFile().mkdirs();
//                FileWriter fileWriter = new FileWriter(file);
//
//
////                System.out.println("WHAT THE FUCK");
////                System.out.println(java);
//                fileWriter.write(java.toStringWithImports());
//                fileWriter.close();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
////        processor.getEnvironment().setSourceOutputDirectory(new File(path.toString() + "/src/main/java"));
////        processor.createJavaFile(java.toString());
//    }
}

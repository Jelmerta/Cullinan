package spoonhelpers.managers;

import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

import java.nio.file.Path;
import java.util.HashMap;

public class SpoonVariableManager {
    private static Factory factory;

    static {
        factory = buildProject(Path.of("./src/test/resources/SimpleClass.java"));
        setupEnvironment(factory);
    }

    private static Factory buildProject (Path project){
        Launcher launcher = new Launcher();
        launcher.addInputResource(project.toString());
        launcher.buildModel();
        return launcher.getFactory();
    }

    private static void setupEnvironment (Factory factory){
        Environment environment = factory.getEnvironment();
        environment.setAutoImports(true);
        environment.setNoClasspath(true); // ?
        environment.setCommentEnabled(true); // Required?
    }
    public static CtTypeReference createHashMap(Class argument1, CtTypeReference argument2) {
        CtTypeReference hashMap = factory.createCtTypeReference(HashMap.class);
        hashMap.addActualTypeArgument(factory.createCtTypeReference(argument1));
//        hashMap.addActualTypeArgument(factory.createCtTypeReference(argument2));
        hashMap.addActualTypeArgument(argument2);

        return hashMap;
    }
}

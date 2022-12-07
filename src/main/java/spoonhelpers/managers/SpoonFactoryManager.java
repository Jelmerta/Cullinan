package spoonhelpers.managers;

import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.factory.Factory;

import java.nio.file.Path;

// TODO Enum factory?
public class SpoonFactoryManager {
    private static Factory factory;

    static {
        factory = createFactory(Path.of("./src/"));
    }

    public static Factory getDefaultFactory() {
        return factory;
    }

    public static Factory getTestFactory() {
        return createFactory(Path.of("./src/test/resources/"));
    }

    public static Factory getFactory(Path path) {
        return createFactory(path);
    }

    private static Factory createFactory(Path path) {
        Factory factory = buildProject(path);
        setupEnvironment(factory);
        return factory;
    }

    private static Factory buildProject(Path project) {
        Launcher launcher = new Launcher(); // TODO MavenLauncher difference?
        launcher.addInputResource(project.toString());
        launcher.buildModel();
        return launcher.getFactory();
    }

    private static void setupEnvironment(Factory factory) {
        Environment environment = factory.getEnvironment();
        environment.setAutoImports(true);
        environment.setNoClasspath(true); // ?
        environment.setCommentEnabled(true); // Required?
    }
}

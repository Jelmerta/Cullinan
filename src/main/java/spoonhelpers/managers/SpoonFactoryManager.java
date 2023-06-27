package spoonhelpers.managers;

import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.MavenLauncher.SOURCE_TYPE;
import spoon.compiler.Environment;
import spoon.reflect.factory.Factory;
import util.CullinanId;

import java.nio.file.Path;

// TODO Enum factory?
public class SpoonFactoryManager {
    private static Factory factory;

    static {
//        factory = createFactory(Path.of("../dddsample-core-master" + "/src/main/java")); // Just used as a generator factory
//        factory = createFactory(Path.of("../mybatis-3" + "/src/main/java")); // Just used as a generator factory
        factory = createFactory(Path.of("../cullinan_demo" + "/src/main/java")); // Just used as a generator factory
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
        Launcher launcher = new Launcher();
        launcher.createFactory();
        launcher.getFactory().getEnvironment().setComplianceLevel(16);
        setupEnvironment(launcher.getFactory());
        launcher.addInputResource(project.toString());
        launcher.buildModel();

        return launcher.getFactory();
    }

    private static void setupEnvironment(Factory factory) {
        Environment environment = factory.getEnvironment();

//        environment.setAutoImports(false); // False seems to help with static .* imports? SampleVoyages now import import se.citerus.dddsample.domain.model.location.SampleLocations;, otherwise wrong import
        environment.setAutoImports(true);
        environment.setNoClasspath(true);
        environment.setCommentEnabled(true);
        environment.setShouldCompile(true);
    }
}

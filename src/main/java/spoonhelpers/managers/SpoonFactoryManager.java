package spoonhelpers.managers;

import spoon.Launcher;
import spoon.MavenLauncher;
import spoon.compiler.Environment;
import spoon.reflect.factory.Factory;

import java.nio.file.Path;

// TODO Enum factory?
public class SpoonFactoryManager {
    private static Factory factory;

    static {
        factory = createFactory(Path.of("./src/"));
//        factory = createFactory(Path.of("."));
//        factory = createFactory(Path.of("../dddsample-core-master"));
//        factory = createFactory(Path.of("../dddsample-core-master/src/main/java"));
//        factory = createFactory(Path.of("/home/jelmer/Documents/Software Engineering/Master Project/projects/Cullinan/dddsample-core-master/pom.xml"));
//        factory = buildProject(Path.of("/home/jelmer/Documents/Software Engineering/Master Project/projects/Cullinan/dddsample-core-master/pom.xml"));
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
//        MavenLauncher launcher = new MavenLauncher("/home/jelmer/Documents/Software Engineering/Master Project/projects/Cullinan/dddsample-core-master/pom.xml", MavenLauncher.SOURCE_TYPE.APP_SOURCE); // TODO MavenLauncher difference?
        launcher.addInputResource(project.toString());
//        Launcher launcher = new MavenLauncher("/home/jelmer/Documents/Software Engineering/Master Project/projects/Cullinan/dddsample-core-master/pom.xml", MavenLauncher.SOURCE_TYPE.ALL_SOURCE); // TODO MavenLauncher difference?
//        launcher.getPomFile().
        launcher.buildModel();

        return launcher.getFactory();
    }

    private static void setupEnvironment(Factory factory) {
        Environment environment = factory.getEnvironment();

        environment.setAutoImports(false); // False seems to help with static .* imports? SampleVoyages now import import se.citerus.dddsample.domain.model.location.SampleLocations;, otherwise wrong import
//        environment.setAutoImports(true);
//        environment.setComplianceLevel(8);
//        environment.setShouldCompile(true); // TODO What does this do? I would like it to compile...
//        environment.
//        environment.setCopyResources(true); //???
        environment.setNoClasspath(true); // ?
        environment.setCommentEnabled(true); // Required?
    }
}

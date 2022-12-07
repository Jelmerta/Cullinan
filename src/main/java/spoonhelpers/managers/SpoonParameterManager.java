package spoonhelpers.managers;

import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoonhelpers.model.SpoonParameter;

import java.nio.file.Path;

public class SpoonParameterManager {
    private static Factory factory;

    static {
        factory = buildProject(Path.of("./src/test/resources/SimpleClass.java"));
        setupEnvironment(factory);
    }

    private static Factory buildProject(Path project) {
        Launcher launcher = new Launcher();
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

    public static SpoonParameter read(CtParameter parameter) {
        return SpoonParameter.newBuilder(parameter.getType(), parameter.getSimpleName(), false) // TODO Some will be service classes. We don't really know here... Where should this be set? Higher level?
                .build();
    }

    public static CtParameter write(SpoonParameter parameter) {
        CtParameter ctParameter = factory.createParameter();
        ctParameter.setType(parameter.getType());
        ctParameter.setSimpleName(parameter.getName());
        return ctParameter;
    }

    public static SpoonParameter create(CtTypeReference reference, String name) {
        CtParameter ctParameter = factory.createParameter();
        ctParameter.setType(reference);
        ctParameter.setSimpleName(name);
        return read(ctParameter);
    }
}

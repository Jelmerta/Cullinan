package spoonhelpers.managers;

import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoonhelpers.model.SpoonAccessModifier;
import spoonhelpers.model.SpoonField;
import spoonhelpers.model.SpoonMethod;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;

public class SpoonFieldManager {
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

    public static SpoonField createField(CtTypeReference type, String name) {
        SpoonField.Builder spoonField = SpoonField.newBuilder(type, name, SpoonAccessModifier.PRIVATE);
        spoonField.makeStatic();
        return spoonField.build();


//        SpoonField spoonField =
    }

    public static SpoonField createField(Class type, String name) {
        return createField(factory.createCtTypeReference(type), name);
    }

    public static CtField write(SpoonField field) {
        CtField ctField = factory.createField();
        Optional<ModifierKind> accessModifier = SpoonAccessModifierManager.to(field.getAccessModifier());
        accessModifier.ifPresent(ctField::addModifier);
        if (field.isStatic()) {
            ctField.addModifier(ModifierKind.STATIC);
        }

        ctField.setType(field.getType());
        ctField.setSimpleName(field.getName());

        return ctField;
    }

//    public static SpoonMethod getMethod(SpoonField spoonField, String valueOf) {
//        return SpoonMethodManager.
//    }
}

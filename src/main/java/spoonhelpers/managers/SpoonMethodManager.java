package spoonhelpers.managers;

import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoonhelpers.model.SpoonAccessModifier;
import spoonhelpers.model.SpoonMethod;
import spoonhelpers.model.SpoonParameter;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SpoonMethodManager {
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

    public static SpoonMethod read(CtMethod method) {
        SpoonAccessModifier accessModifier = SpoonAccessModifierManager.to(method);

//        Class returnType = method.getType().getActualClass(); // TODO Deprecated... Not sure what to use instead. Documentation does not show alternative
        SpoonMethod.Builder methodBuilder = SpoonMethod.newBuilder(method.getSimpleName(), accessModifier, method.getType(), false);

        for (CtAnnotation annotation : method.getAnnotations()) {
            methodBuilder.addAnnotation(annotation.getType());
        }

        if (method.isStatic()) {
            methodBuilder.makeStatic();
        }

        if (method.isFinal()) {
            methodBuilder.makeFinal();
        }

        List<CtParameter> parameters = method.getParameters();
        for (CtParameter parameter : parameters) {
            methodBuilder.addParameter(SpoonParameterManager.read(parameter));
        }

        methodBuilder.setBody(method.getBody());

        return methodBuilder.build();
    }

    // TODO Exceptions not added here and above, required?
    public static SpoonMethod read(CtConstructor constructor) {
        SpoonAccessModifier accessModifier = SpoonAccessModifierManager.to(constructor);
//        Class returnType = constructor.getType().getActualClass(); // TODO Deprecated... Not sure what to use instead. Documentation does not show alternative
//        String name = "new" + constructor.getType().getSimpleName(); // Constructor simple name is <init> so we use the type
        String name = constructor.getType().getSimpleName();
        SpoonMethod.Builder methodBuilder = SpoonMethod.newBuilder(name, accessModifier, factory.createCtTypeReference(String.class), true); // We return a reference id to the object instead of the object itself

        if (constructor.isStatic()) {
            methodBuilder.makeStatic();
        }

        if (constructor.isFinal()) {
            methodBuilder.makeFinal();
        }

        List<CtParameter> parameters = constructor.getParameters();
        for (CtParameter parameter : parameters) {
            methodBuilder.addParameter(SpoonParameterManager.read(parameter));
        }

        return methodBuilder.build();
    }

    public static CtMethod write(SpoonMethod spoonMethod) {
        CtMethod method = factory.createMethod(); // TODO + Package
        method.setSimpleName(spoonMethod.getName());
        method.setType(spoonMethod.getReturnType());
        Optional<ModifierKind> accessModifier = SpoonAccessModifierManager.to(spoonMethod.getAccessModifier());
        accessModifier.ifPresent(method::addModifier);

        if (spoonMethod.isStatic()) {
            method.addModifier(ModifierKind.STATIC);
        }

        if (spoonMethod.isFinal()) {
            method.addModifier(ModifierKind.FINAL);
        }

        for (SpoonParameter parameter : spoonMethod.getParameters()) {
            System.out.println("methodmanager");
            System.out.println(parameter.getName());
            method.addParameter(SpoonParameterManager.write(parameter));
        }

        for (CtTypeReference exceptionClass : spoonMethod.getExceptionClasses()) {
            method.addThrownType(exceptionClass);
        }

        method.setBody(spoonMethod.getBody());

        return method;
    }

    // TODO No parameters yet.
    public static CtMethod findMethod(CtTypeReference classType, String name) {
//        CtTypeReference<Object> ctTypeReference = factory.createCtTypeReference(classType);
        System.out.println("methods");
        System.out.println(classType.getTypeDeclaration().getMethods());
        Set<CtMethod<?>> methods = classType.getTypeDeclaration().getMethods();
        for (CtMethod<?> method : methods) {
            if (method.getSimpleName().equalsIgnoreCase(name)) {
                return method;
            }
        }
        throw new IllegalStateException(classType + " function " + name + " should exist");
    }


    public static boolean isNonStatic(CtConstructor ctConstructor) {
        return !ctConstructor.isStatic();
    }

    public static boolean isNonStatic(CtMethod method) {
        return !method.isStatic();
    }

    // TODO Does not actually check usage, only that it's possible...
    public static boolean usedOutsideService(CtMethod method) {
        boolean publicUse = method.hasModifier(ModifierKind.PUBLIC);
        boolean packageUse = !method.hasModifier(ModifierKind.PUBLIC) && !method.hasModifier(ModifierKind.PRIVATE) && !method.hasModifier(ModifierKind.PROTECTED);

        return publicUse || packageUse;
    }

    public static boolean usedOutsideService(CtConstructor constructor) {
        boolean publicUse = constructor.hasModifier(ModifierKind.PUBLIC);
        boolean packageUse = !constructor.hasModifier(ModifierKind.PUBLIC) && !constructor.hasModifier(ModifierKind.PRIVATE) && !constructor.hasModifier(ModifierKind.PROTECTED);

        return publicUse || packageUse;
    }

//    public static SpoonMethod findMethod(String name,) {
//        SpoonMethod spoonMethod = SpoonMethod.newBuilder(name, SpoonAccessModifier.PUBLIC, );
//    }
}

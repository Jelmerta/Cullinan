package cullinan.helpers.decomposition.javagenerators;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoonhelpers.managers.SpoonFactoryManager;
import spoonhelpers.managers.SpoonMethodManager;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class ClassDefinitionsCreator {
    private final List<String> serviceClassDefinitions;
    private final List<String> proxyClassDefinitions;

    private CtClass classDefinitions;

    public ClassDefinitionsCreator(List<String> serviceClassDefinitions, List<String> proxyClassDefinitions) {
        this.serviceClassDefinitions = serviceClassDefinitions;
        this.proxyClassDefinitions = proxyClassDefinitions;
    }

    public CtClass build() {
        createClassDefinitions();

        Factory templateFactory = SpoonFactoryManager.getFactory(Path.of("src/main/java/util/ClassDefinitions.java")); // TODO Hardcode not good
        CtClass original = templateFactory.Class().get("util.ClassDefinitions");

        CtField serviceClassDefinitions = original.getField("serviceClassDefinitions");
        this.classDefinitions.addField(serviceClassDefinitions);

        CtAnonymousExecutable staticServiceClassDefinitionsAssignment = buildClassDefinitions(this.serviceClassDefinitions, serviceClassDefinitions);
        this.classDefinitions.addAnonymousExecutable(staticServiceClassDefinitionsAssignment);

        CtField proxyClassDefinitionsField = original.getField("proxyClassDefinitions");
        this.classDefinitions.addField(proxyClassDefinitionsField);

        // TODO Figure out usage: which clients make use of the service class? Could we get away with this for now to just add every class that is not a service class, as we have definitions for all of these right now?
        CtAnonymousExecutable staticProxyClassDefinitionsAssignment = buildClassDefinitions(proxyClassDefinitions, proxyClassDefinitionsField);
        this.classDefinitions.addAnonymousExecutable(staticProxyClassDefinitionsAssignment);

        return classDefinitions;
    }

    private void createClassDefinitions() {
        classDefinitions = SpoonFactoryManager.getDefaultFactory().createClass();
        classDefinitions.setSimpleName("util.ClassDefinitions");
        classDefinitions.addModifier(ModifierKind.PUBLIC);
    }

    private CtAnonymousExecutable buildClassDefinitions(List<String> classDefinitions, CtField proxyClassDefinitionsField) {
        CtAnonymousExecutable staticProxyClassDefinitionsAssignment = SpoonFactoryManager.getDefaultFactory().createAnonymousExecutable();
        CtBlock block = SpoonFactoryManager.getDefaultFactory().createBlock();
        for (String classDefinition : classDefinitions) {
            proxyClassDefinitionsField.getReference();

            CtInvocation addToSet = SpoonFactoryManager.getDefaultFactory().createInvocation();
            CtVariableRead<Object> variableRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
            variableRead.setVariable(proxyClassDefinitionsField.getReference());
            addToSet.setTarget(variableRead);
            CtMethod addToSetMethod = SpoonMethodManager.findMethod(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Set.class), "add");
            addToSet.setExecutable(addToSetMethod.getReference());

            CtLiteral<String> proxyClassText = SpoonFactoryManager.getDefaultFactory().createLiteral();
            proxyClassText.setValue(classDefinition);
            addToSet.addArgument(proxyClassText);

            block.addStatement(addToSet);
        }

        staticProxyClassDefinitionsAssignment.setBody(block);
        staticProxyClassDefinitionsAssignment.addModifier(ModifierKind.STATIC);
        return staticProxyClassDefinitionsAssignment;
    }
}

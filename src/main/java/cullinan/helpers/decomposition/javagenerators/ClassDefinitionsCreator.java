package cullinan.helpers.decomposition.javagenerators;

import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoonhelpers.managers.SpoonFactoryManager;
import spoonhelpers.managers.SpoonMethodManager;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassDefinitionsCreator {
    private final List<String> serviceClassDefinitions;
    private final List<String> proxyClassDefinitions;

    private CtClass classDefinitions;

    public ClassDefinitionsCreator(List<String> serviceClassDefinitions, List<String> proxyClassDefinitions) {
        // To lower case for easier matching. Could probably do this during deserialization of classnames in microservices
        this.serviceClassDefinitions = serviceClassDefinitions.stream().map(String::toLowerCase).collect(Collectors.toList());
        this.proxyClassDefinitions = proxyClassDefinitions.stream().map(String::toLowerCase).collect(Collectors.toList());
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

        addIsServiceClassMethod();
        addIsProxyClassMethod();

        return classDefinitions;
    }

    private void createClassDefinitions() {
        classDefinitions = SpoonFactoryManager.getDefaultFactory().createClass("util.ClassDefinitions");
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

    private void addIsServiceClassMethod() {
        CtTypeReference<Object> returnType = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(boolean.class);
        CtMethod isServiceClass = SpoonFactoryManager.getDefaultFactory().createMethod();
        isServiceClass.setSimpleName("isServiceClass");
        isServiceClass.setType(returnType);
        isServiceClass.addModifier(ModifierKind.PUBLIC);
        isServiceClass.addModifier(ModifierKind.STATIC);

        CtParameter className = SpoonFactoryManager.getDefaultFactory().createParameter();
        className.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
        className.setSimpleName("className");
        isServiceClass.addParameter(className);
        CtVariableRead classNameRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
        classNameRead.setVariable(className.getReference());

        CtExecutableReference toLowerCaseReference = SpoonMethodManager.findMethod(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class), "toLowerCase").getReference();
        CtInvocation toLowerCase = SpoonFactoryManager.getDefaultFactory().createInvocation();
        toLowerCase.setTarget(classNameRead);
        toLowerCase.setExecutable(toLowerCaseReference);

        CtField serviceClassDefinitionsField = classDefinitions.getField("serviceClassDefinitions");
        CtVariableRead classDefinitionsRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
        classDefinitionsRead.setVariable(serviceClassDefinitionsField.getReference());

        CtExecutableReference contains = SpoonMethodManager.findMethod(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Set.class), "contains").getReference();
        CtInvocation containsCall = SpoonFactoryManager.getDefaultFactory().createInvocation();
        containsCall.setTarget(classDefinitionsRead);
        containsCall.setExecutable(contains);
        containsCall.addArgument(toLowerCase);

        CtReturn returnStatement = SpoonFactoryManager.getDefaultFactory().createReturn();
        returnStatement.setReturnedExpression(containsCall);

        isServiceClass.setBody(returnStatement);
        classDefinitions.addMethod(isServiceClass);
    }

    private void addIsProxyClassMethod() {
        CtTypeReference<Object> returnType = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(boolean.class);
        CtMethod isProxyClass = SpoonFactoryManager.getDefaultFactory().createMethod();
        isProxyClass.setSimpleName("isProxyClass");
        isProxyClass.setType(returnType);
        isProxyClass.addModifier(ModifierKind.PUBLIC);
        isProxyClass.addModifier(ModifierKind.STATIC);

        CtParameter className = SpoonFactoryManager.getDefaultFactory().createParameter();
        className.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
        className.setSimpleName("className");
        isProxyClass.addParameter(className);
        CtVariableRead classNameRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
        classNameRead.setVariable(className.getReference());

        CtExecutableReference toLowerCaseReference = SpoonMethodManager.findMethod(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class), "toLowerCase").getReference();
        CtInvocation toLowerCase = SpoonFactoryManager.getDefaultFactory().createInvocation();
        toLowerCase.setTarget(classNameRead);
        toLowerCase.setExecutable(toLowerCaseReference);

        CtField serviceClassDefinitionsField = classDefinitions.getField("proxyClassDefinitions");
        CtVariableRead classDefinitionsRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
        classDefinitionsRead.setVariable(serviceClassDefinitionsField.getReference());
        CtExecutableReference contains = SpoonMethodManager.findMethod(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Set.class), "contains").getReference();
        CtInvocation containsCall = SpoonFactoryManager.getDefaultFactory().createInvocation();
        containsCall.setTarget(classDefinitionsRead);
        containsCall.setExecutable(contains);
        containsCall.addArgument(toLowerCase);

        CtReturn returnStatement = SpoonFactoryManager.getDefaultFactory().createReturn();
        returnStatement.setReturnedExpression(containsCall);

        isProxyClass.setBody(returnStatement);
        classDefinitions.addMethod(isProxyClass);
    }
}

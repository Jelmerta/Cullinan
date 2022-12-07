package spoonhelpers.managers;


import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoonhelpers.model.*;

import java.rmi.server.UnicastRemoteObject;
import java.util.*;

// TODO I do kind of wonder if I should just use Spoon directly instead of my own classes. I think this is more clear about what I have actually been able to use.
// There is not really code for generating Spoon code.
// TODO The converters provided by Spoon might also be a nice way to decompose... All such different approaches. However they are more used for mutations I think. Though you could probably jus generate code as well.
public class SpoonClassManager {
    // Class to interface cast helper?
    // Copy from original code with changes?
    public static void main(String[] args) {
        // Load codebase

        // For service class in codebase

        // Read model using own helpers
        // Duplicate class to service
        // Create interface out of class -> Duplicate with public methods / rename / make interface / add extension / remove bodies / add throws
        // Create implementations out of class or interface ->
        // Create Storage manager separately

        // End For service class in codebase

        // Create main function to run all implementations

//        SpoonLine variableAssignment = SpoonVariableAssignmentLine.builder()
//                .build();
//
//        SpoonLine returnStatement = SpoonReturnLine.builder()
//                .build();
//
//        SpoonParameter parameter1 = SpoonParameter.builder()
//                .type() // Enum? Class? Generic? Interface impl?
//                .name()
//                .build();
//
//        SpoonMethod spoonMethod = SpoonMethod.builder()
//                .name("name")
//                .addParameter(parameter1)
//                .throwsException(RemoteException.class)
//                .addModifier() // Enum? Separate public and static things?
//                .addAnnotation() // Enum? Class?
//                .addLine(variableAssignment)
//                .addLine(returnStatement)
//                .build();
//
//        SpoonInterface spoonInterface = SpoonInterface.builder()
//                .name()
//                .addMethod(spoonMethod)
//                .extendsClass(Remote.class)
//                .build();
    }

    // TODO Is target required? What does it do
    public static void createNewClass(CtField field) {
        CtNewClass newClassCall = SpoonFactoryManager.getDefaultFactory().createNewClass();
        newClassCall.setTarget(field.getDefaultExpression());
        newClassCall.setType(field.getType());
        field.setAssignment(newClassCall);
    }

    public static SpoonClass buildImplementation(SpoonInterface classInterface) {
        String classImplementationName = classInterface.getName().replace("Interface", "Implementation");
        SpoonClass.Builder classImplementationBuilder = SpoonClass.newBuilder(classImplementationName, SpoonAccessModifier.PUBLIC);
        CtTypeReference unicastRemoteObject = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(UnicastRemoteObject.class);
        classImplementationBuilder.setSuperclass(unicastRemoteObject);
        classImplementationBuilder.addSuperInterface(classInterface);

        SpoonMethod constructor = SpoonMethod.newBuilder(classImplementationName, SpoonAccessModifier.PUBLIC, null, true)
                .emptyBody()
                .build();

        classImplementationBuilder.addMethod(constructor);

        for (SpoonMethod interfaceMethod : classInterface.getMethods()) {
            SpoonMethod.Builder implementationMethod = SpoonMethod.newBuilder(interfaceMethod.getName(), SpoonAccessModifier.PUBLIC, interfaceMethod.getReturnType(), false);
            // We could keep track of parameter being the class or a proxy in the SpoonParameter?
            implementationMethod.addAnnotation(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Override.class));

            implementationMethod.emptyBody();
            for (SpoonParameter parameter : interfaceMethod.getParameters()) {
                if (parameter.isServiceClass()) {
                    // Retrieve the object
                    // Assignment (Variable: Type + name) to (ClassMethodCall: Class + methodName)
                    // TODO Parameter and variable is kind of the same in terms of variables... Maybe we can merge them as one, unless we find differences?
                    SpoonVariable serviceObject = SpoonVariable.newBuilder(parameter.getType(), parameter.getName()).build();

                    // TODO Need the storage Retrieval class to exist (or at least the type f it in order to call the class)
//                    ClassAssignmentCall serviceRetrievalCall = ClassAssignmentCall.newBuilder().build();

//                    SpoonCodeLine storageRetrievalLine = SpoonAssignmentCodeLine.newBuilder(serviceObject, )
//                            .build();
                }
            }
        }

        return classImplementationBuilder.build();


//        // TODO Copy the parameters from the interface
//        // TODO Would it be helpful if we have a wrapper to have our own model instead of simply using Spoon?
//        // We then would better know for each method what should be done (service objects that need to be retrieved etc. Otherwise this is done based on naming of the variables which is probably not optimal.

//            CtVariableRead firstParameter = null;
//            CtLocalVariable calledObject = null;
//            boolean targetObjectFound = false;
//            for (CtParameter parameter : method.getParameters()) {
//                if (parameter.getType().equals(stringType)) { // Is this a good comparator?
//                    System.out.println("Found String argument!");
//                    if (parameter.getSimpleName().contains("eferenceId")) { // R might be capitalized or not... I dislike this check. Naming change would require two changes.
//                        System.out.println("Found a reference id. Looking up object in storage.");
//
//
//
//                        CtInvocation storageRetrievalCall = factory.createInvocation();
//
//                        CtExecutableReference storageGet = null;
//                        Set<CtMethod<?>> storageMethods = serviceStorageManager.getMethods();
//                        for (CtMethod<?> storageMethod : storageMethods) {
//                            if (storageMethod.getSimpleName().equalsIgnoreCase("get")) {
//                                storageGet = storageMethod.getReference();
//                            }
//                        }
//                        if (storageGet == null) {
//                            throw new IllegalStateException("Storage Manager function get should exist");
//                        }
//
//
//                        CtTypeAccess storageTarget = factory.createTypeAccess(serviceStorageManager.getReference());
//
//                        System.out.println(storageTarget);
//
//                        storageRetrievalCall.setTarget(storageTarget);
//                        System.out.println("Hi " + storageGet.getSimpleName() + storageGet.getSignature());
//                        storageRetrievalCall.setExecutable(storageGet);
//                        System.out.println(storageRetrievalCall);
//                        CtVariableAccess referenceArgument = factory.createVariableRead(parameter.getReference(), false);
//                        System.out.println(parameter);
//                        System.out.println(method.getParameters());
//
//                        storageRetrievalCall.addArgument(referenceArgument);
////                        CtTypeReference objectVariableType = serviceInterface.getReference();
//                        CtTypeReference objectVariableType = javaClass.getReference();
//
//                        CtLocalVariable variable = factory.createLocalVariable(objectVariableType, parameter.getSimpleName() + "Object", storageRetrievalCall);
//                        if (!targetObjectFound) {
//                            firstParameter = factory.createVariableRead();
//                            firstParameter.setVariable(parameter.getReference());
//                            calledObject = variable;
//                            targetObjectFound = true;
//                        }
//
//                        codeBlock.addStatement(variable);
//                    }
//                }
//            }
    }

    // TODO Package?
    public static SpoonClass read(CtClass ctClass) {
        String className = ctClass.getSimpleName();
        SpoonClass.Builder spoonClassBuilder = SpoonClass.newBuilder(className, SpoonAccessModifierManager.to(ctClass));

        Set<CtMethod> methods = ctClass.getMethods();
        for (CtMethod method : methods) {
            spoonClassBuilder.addMethod(SpoonMethodManager.read(method));
        }

        // TODO And used outside of service...
        Set<CtConstructor> constructors = ctClass.getConstructors(); // TODO Required? && ctConstructor.getSignature().substring(0, ctConstructor.getSignature().indexOf("(")).equals(javaClass.getSimpleName())) { // getSimpleName does not give name of constructor (instead returns init()???)
        for (CtConstructor constructor : constructors) {
            spoonClassBuilder.addMethod(SpoonMethodManager.read(constructor));
        }

        return spoonClassBuilder.build();
    }

    public static CtInterface write(SpoonInterface spoonInterface) {
        CtInterface serviceInterface = SpoonFactoryManager.getDefaultFactory().createInterface(spoonInterface.getPackage() + "." + spoonInterface.getName()); // TODO + Package
//        serviceInterface.setSuperclass(factory.createCtTypeReference(spoonInterface.getSuperclass()));
        serviceInterface.addSuperInterface(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(spoonInterface.getSuperclass()));
        Optional<ModifierKind> modifierKind = SpoonAccessModifierManager.to(spoonInterface.getModifierAccess());
        modifierKind.ifPresent(serviceInterface::addModifier);

        for (SpoonMethod method : spoonInterface.getMethods()) {
            serviceInterface.addMethod(SpoonMethodManager.write(method));
        }



        return serviceInterface;
    }

    public static CtClass write(SpoonClass spoonClass) {
        CtClass serviceClass = SpoonFactoryManager.getDefaultFactory().createClass(spoonClass.getName()); // TODO + Package
//        serviceInterface.setSuperclass(factory.createCtTypeReference(spoonInterface.getSuperclass()));
//        serviceClass.addSuperInterface(factory.createCtTypeReference(spoonClass.getSuperclass()));
        Optional<ModifierKind> modifierKind = SpoonAccessModifierManager.to(spoonClass.getModifierAccess());
        modifierKind.ifPresent(serviceClass::addModifier);

        for (SpoonMethod method : spoonClass.getMethods()) {
            serviceClass.addMethod(SpoonMethodManager.write(method));
        }

        serviceClass.addConstructor(spoonClass.getConstructor());

        for (CtField spoonField : spoonClass.getFields()) {
//            CtField ctField = SpoonFieldManager.write(spoonField);SpoonClass.Builder storageClassBuilder
            serviceClass.addField(spoonField);
        }

        return serviceClass;
    }


//    public static SpoonClass.Builder createClass() {
//        return SpoonClass.newBuilder(
//        );
//    }

    public static CtClass createClass(String name) {
        CtClass aClass = SpoonFactoryManager.getDefaultFactory().createClass(name);
        aClass.addModifier(ModifierKind.PUBLIC);
        return aClass;
    }
}

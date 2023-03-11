package cullinan.helpers.decomposition.javagenerators;

import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;
import spoonhelpers.managers.SpoonFactoryManager;
import spoonhelpers.managers.SpoonMethodManager;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

public class ServiceInterfaceCreator {
    private final CtClass originalClass;
    private final CtInterface result;

    public ServiceInterfaceCreator(CtClass originalClass) {
        this.originalClass = originalClass;
        result = SpoonFactoryManager.getDefaultFactory().createInterface("client.rmiinterface." + originalClass.getSimpleName() + "Interface");
    }

    public CtInterface buildInterface() {
        result.addModifier(ModifierKind.PUBLIC);
        result.addSuperInterface(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Remote.class));

        Set<CtMethod> methods = originalClass.getMethods();
        methods.stream()
                .filter(SpoonMethodManager::usedOutsideService)
                .map(this::convertToInterfaceMethod)
                .forEach(result::addMethod);

        Set<CtConstructor> constructors = originalClass.getConstructors();
        constructors.stream()
                .filter(SpoonMethodManager::usedOutsideService)
                .map(this::convertToInterfaceMethod)
                .forEach(result::addMethod);

        addInterfaceForVariables();

//         TODO Overlap? Hmm... Probably should just keep the new interface? Hm... Should this be done outside of here for every class? As this is pretty much gonna be done for proxy/client/interface/implementation
        Set<CtType> nestedTypes = originalClass.getNestedTypes();
        for (CtType nestedType : nestedTypes) {
            if (nestedType.isClass() || nestedType.isEnum()) { // TODO Any other things we need to move?
                ServiceInterfaceCreator serviceInterfaceCreator = new ServiceInterfaceCreator((CtClass) nestedType);
                CtInterface ctInterface = serviceInterfaceCreator.buildInterface();
                Set<CtMethod> allMethods = ctInterface.getAllMethods();
                for (CtMethod method : allMethods) { // TODO GetMethod or allmethods?
                    if (method.getSimpleName().contains("retrieveVariable")) { // Hack upon hack. We make sure not to add inner class again as we already did this
                        result.addMethod(method);
                        continue;
                    }
                    String newMethodName = nestedType.getSimpleName().substring(0, 1).toLowerCase() + nestedType.getSimpleName().substring(1) + "InnerClass" + method.getSimpleName().substring(0, 1).toUpperCase() + method.getSimpleName().substring(1);
                    method.setSimpleName(newMethodName);
                    result.addMethod(method);
                }
            }
        }
//        TODO It's just the proxy and implementation that need to be altered? Client/Interface can be added separately...

        return result;
    }

    private void addInterfaceForVariables() {
        List<CtField> fields = originalClass.getFields();
        for (CtField field : fields) {
            if (!field.isPrivate()) {
                createFieldInterfaceMethod(field);
            }
        }
    }

    private void createFieldInterfaceMethod(CtField field) {
        CtMethod variableRetrieveMethod = SpoonFactoryManager.getDefaultFactory().createMethod();
        variableRetrieveMethod.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
        String fullVariableName = ProxyCreator.findFullMethodName(originalClass, field.getSimpleName());
        fullVariableName = fullVariableName.substring(0, 1).toUpperCase() + fullVariableName.substring(1);
        variableRetrieveMethod.setSimpleName("retrieveVariable" + fullVariableName);
        addException(variableRetrieveMethod);

        if (!field.isStatic()) {
            CtParameter objectReference = SpoonFactoryManager.getDefaultFactory().createParameter();
            objectReference.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
            objectReference.setSimpleName("objectReferenceId");
            variableRetrieveMethod.addParameter(objectReference);
        }
        System.out.println("METHOD");
        System.out.println(variableRetrieveMethod.getSimpleName());

        result.addMethod(variableRetrieveMethod);
    }

    // Public helper function
    // TODO For now, we decompose all non-private variables. We show a warning if we decompose a variable we think should not be decomposed.
    public static boolean shouldBeDecomposedField(CtField field) {
        if (field.isPrivate()) {
            return false;
        } else {
            return true;
            // TODO Temporary solution is to just decompose all non-private, we probably do not want this.
        }

//        // field is non-private, final, and primitive, we can simply retrieve it, as it cannot be changed ever.
//        // Otherwise, there can be issues where data is duplicated between services leading to errors.
//        if (field.isFinal() && field.getType().isPrimitive()) {
//            // Retrieve variable
//            return true;
//        }
//
//        // If field is non-private and a decomposed type, we can create the proxy for it and let it handle that way. (we not easily have access here to decomposable type, maybe we can introduce a context object type.
//        // We assume that if fieldType is not null that it is part of the decomposition. Could theoretically not be in the decomposition file... We assume full decomposition
//        CtClass<Object> fieldType = SpoonFactoryManager.getDefaultFactory().Class().get(field.getType().getQualifiedName());
//        if (fieldType != null) {
//            // Create proxy
//            return true;
//        }
//
//        return false;
    }

    private CtMethod convertToInterfaceMethod(CtMethod method) {
        CtMethod result = SpoonFactoryManager.getDefaultFactory().createMethod();
        result.setType(method.getType());
        result.setSimpleName(method.getSimpleName());
        alterReturnType(result);
        addException(result);
        if (!method.isStatic()) {
            addObjectReferenceParameter(result);
        }
        addMethodParameters(method.getParameters(), result);
//        convertVarargs // Otherwise have issues with casting...? class spoon.support.reflect.reference.CtTypeReferenceImpl cannot be cast to class spoon.reflect.reference.CtArrayTypeReference (spoon.support.reflect.reference.CtTypeReferenceImpl and spoon.reflect.reference.CtArrayTypeReference are in unnamed module of loader 'app')
        return result;
    }

    private CtMethod convertToInterfaceMethod(CtConstructor constructor) {
        CtMethod result = SpoonFactoryManager.getDefaultFactory().createMethod();
        result.setSimpleName("new" + originalClass.getSimpleName());
        result.setType(constructor.getType());
        alterReturnType(result);
        addException(result);
        addMethodParameters(constructor.getParameters(), result);
        return result;
    }

    //             // However, for the objects being passed to this method, we want a reference id to it being added to the list of arguments

    //            // We also want a reference to the object this method is being called on.
    // Change parameters with objects that are contained within this service such that they are reference ids instead of the full object.
    // This is not sufficient for multiple microservice decompositions, because we need to make sure that objects outside of this service are also changed to reference ids.

    // Basically always a String returned if it is an object
    private void addMethodParameters(List<CtParameter> parameters, CtMethod method) {
        // TODO Make clone of param?
        parameters.stream()
                .map(CtParameter::clone)
                .map(this::handleComplexType)
                .forEach(method::addParameter);
    }

    private CtParameter handleComplexType(CtParameter parameter) {
//        TODO How do we fill this array correctly with ids...?
// TODO Maybe an error because we do not know how to deal with varargs... Original code should be changed by developer first?
//        Not supported for now.

//         TODO Pretty sure we have to not have a varargs in the interface... Not sure how... Maybe cast to object/String?

//        TODO Maybe just cast varargs to Object? Just encode as String?
        if (parameter.isVarArgs()) {
            parameter.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
            parameter.setSimpleName(parameter.getSimpleName() + "Encoded");
            parameter.setVarArgs(false);
            return parameter;
        }
//        if (parameter.isVarArgs()) {
//            throw new IllegalStateException("Cullinan does not yet support varargs arguments. Please make sure the original code base does not use varargs parameters.");
//        }

//        TODO Also don't really know how to deal with arrays of service objects, like: List<Location> or Location[]... Or even Object[]. How do we make sure to map each one to an id if necessary?...

//        if (parameter.isVarArgs() && isComplex(parameter.getType())) { // Object... type for example, should we cast this to String[]?
//            CtArrayTypeReference arrayTypeReference = SpoonFactoryManager.getDefaultFactory().createArrayTypeReference();
////            arrayTypeReference.setComponentType(parameter.getType());
//            arrayTypeReference.setComponentType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
//            parameter.setType(arrayTypeReference);
//            parameter.setVarArgs(false);
//            parameter.setSimpleName(parameter.getSimpleName() + "Encoded");
//
//            parameter.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
//            parameter.setVarArgs(true);
//            parameter.setSimpleName(parameter.getSimpleName() + "Encoded");
//            return parameter;
//        }

        if (isComplex(parameter.getType())) {
            parameter.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
            parameter.setSimpleName(parameter.getSimpleName() + "Encoded");
        }
        return parameter;
    }

    private boolean isComplex(CtTypeReference type) {
        return !type.isPrimitive();
    }

    private void addObjectReferenceParameter(CtMethod method) {
        CtParameter referenceParameter = SpoonFactoryManager.getDefaultFactory().createParameter();
        referenceParameter.setSimpleName("referenceId");
        referenceParameter.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
        method.addParameterAt(0, referenceParameter);
    }

    private void addException(CtMethod method) {
        method.addThrownType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(RemoteException.class));
    }

    private void alterReturnType(CtMethod method) {
        if (!method.getType().isPrimitive()) {
            method.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
        }
    }


}

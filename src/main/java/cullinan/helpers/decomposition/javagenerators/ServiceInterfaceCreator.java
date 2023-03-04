package cullinan.helpers.decomposition.javagenerators;

import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoonhelpers.managers.SpoonFactoryManager;
import spoonhelpers.managers.SpoonMethodManager;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

public class ServiceInterfaceCreator {
    private final CtClass originalClass;

    public ServiceInterfaceCreator(CtClass originalClass) {
        this.originalClass = originalClass;
    }

    public CtInterface buildInterface() {
        CtInterface result = SpoonFactoryManager.getDefaultFactory().createInterface("client.rmiinterface." + originalClass.getSimpleName() + "Interface");
        result.addModifier(ModifierKind.PUBLIC);
//        result.setSuperclass(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Remote.class));
        result.addSuperInterface(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Remote.class)); // TODO Not an interface yet it works?

        Set<CtMethod> methods = originalClass.getMethods();
        methods.stream()
                .filter(SpoonMethodManager::usedOutsideService)
//                .filter(SpoonMethodManager::isNonStatic)
                .map(this::convertToInterfaceMethod)
                .forEach(result::addMethod);

        Set<CtConstructor> constructors = originalClass.getConstructors();
        constructors.stream()
                .filter(SpoonMethodManager::usedOutsideService)
                .filter(SpoonMethodManager::isNonStatic) // TODO Constructors are always non-static?
                .map(this::convertToInterfaceMethod)
                .forEach(result::addMethod);

//         TODO Overlap? Hmm... Probably should just keep the new interface? Hm... Should this be done outside of here for every class? As this is pretty much gonna be done for proxy/client/interface/implementation
            Set<CtType> nestedTypes = originalClass.getNestedTypes();
            for (CtType nestedType : nestedTypes) {
                if (nestedType.isClass() || nestedType.isEnum()) { // TODO Any other things we need to move?
                    ServiceInterfaceCreator serviceInterfaceCreator = new ServiceInterfaceCreator((CtClass) nestedType);
                    CtInterface ctInterface = serviceInterfaceCreator.buildInterface();
                    Set<CtMethod> allMethods = ctInterface.getAllMethods();
                    for (CtMethod method : allMethods) { // TODO GetMethod or allmethods?
                        String newMethodName = nestedType.getSimpleName().substring(0, 1).toLowerCase() + nestedType.getSimpleName().substring(1) + "InnerClass" + method.getSimpleName().substring(0,1).toUpperCase() + method.getSimpleName().substring(1);
                        method.setSimpleName(newMethodName);
                        result.addMethod(method);
                    }
                }
            }
//        TODO It's just the proxy and implementation that need to be altered? Client/Interface can be added separately...

        return result;
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

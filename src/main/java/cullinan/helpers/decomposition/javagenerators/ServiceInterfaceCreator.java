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

    public ServiceInterfaceCreator(CtClass originalClass) { // TODO Would prefer to pass serviceDefinition instead of storage
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
                .filter(SpoonMethodManager::isNonStatic)
                .map(this::convertToInterfaceMethod)
                .forEach(result::addMethod);

        Set<CtConstructor> constructors = originalClass.getConstructors();
        constructors.stream()
                .filter(SpoonMethodManager::usedOutsideService)
                .filter(SpoonMethodManager::isNonStatic)
                .map(this::convertToInterfaceMethod)
                .forEach(result::addMethod);

        return result;
    }

    private CtMethod convertToInterfaceMethod(CtMethod method) {
        CtMethod result = SpoonFactoryManager.getDefaultFactory().createMethod();
        result.setType(method.getType());
        result.setSimpleName(method.getSimpleName());
        alterReturnType(result);
        addException(result);
        addObjectReferenceParameter(result);
        addMethodParameters(method.getParameters(), result);
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

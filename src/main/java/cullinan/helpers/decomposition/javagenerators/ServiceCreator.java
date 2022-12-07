package cullinan.helpers.decomposition.javagenerators;

import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoonhelpers.managers.SpoonFactoryManager;
import spoonhelpers.managers.SpoonMethodManager;

import javax.annotation.Nullable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ServiceCreator {
    private CtClass originalClass;
    private CtClass originalClassWithId;
    private CtInterface originalClassInterface;
    private CtClass storageManager; // We need to retrieve the referenced objects from the parameters
    private final CtClass serializationUtil;

    private CtClass serviceImplementation;
//    private Map<CtTypeReference, CtClass> storageClasses; // We need to retrieve the referenced objects from the parameters

//    public ServiceImplementationCreator(CtClass originalClass, CtInterface originalClassInterface, Map<CtTypeReference, CtClass> storageClasses) {
    // TODO Check if we can get rid of originalclass and just use the proxy object? What do we prefer to use where?
    public ServiceCreator(CtClass originalClass, CtInterface originalClassInterface, CtClass storageManager, CtClass serializationUtil, CtClass originalClassWithId) {
        this.originalClass = originalClass;
        this.originalClassWithId = originalClassWithId;
        this.storageManager = storageManager;
        this.originalClassInterface = originalClassInterface;
        this.serializationUtil = serializationUtil;
    }


    // TODO Constructor should not be init and return String

    //        // TODO Not sure yet if hashcode/tostring/equals also need to be created...
//            // Skip method if it is not used elsewhere
// TODO Think about protected/package? Check usage? (public without usage is not required). Maybe define something FindRelevantMethodsForClass

    // TODO Or other classes in the service? Basically have a better way to deal with parameters
    // TODO And used outside of service... Otherwise we dont need the call at all. Just being public is not enough

    // TODO Remove ; as first call (functionally identical though)
    public CtClass build() {
        // TODO Just use interface instead of javaclass if possible?
        serviceImplementation = SpoonFactoryManager.getDefaultFactory().createClass(originalClass.getQualifiedName() + "ServiceImplementation");
        serviceImplementation.addModifier(ModifierKind.PUBLIC);
        CtTypeReference unicastRemoteObject = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(UnicastRemoteObject.class);
        serviceImplementation.addSuperInterface(originalClassInterface.getReference());
        serviceImplementation.setSuperclass(unicastRemoteObject);

        addConstructor(serviceImplementation);

        Set<CtConstructor> constructors = originalClass.getConstructors();
        constructors.stream()
                .filter(SpoonMethodManager::usedOutsideService)
                .filter(SpoonMethodManager::isNonStatic)
                .forEach(constructor -> buildConstructorImplementation(constructor));

        // TODO Copy the parameters from the interface
        // TODO Would it be helpful if we have a wrapper to have our own model instead of simply using Spoon?
        // We then would better know for each method what should be done (service objects that need to be retrieved etc. Otherwise this is done based on naming of the variables which is probably not optimal.
//        for (CtMethod<?> method : (Set<CtMethod<?>>) serviceInterface.getMethods()) {
        Set<CtMethod> methods = originalClass.getMethods();
        methods.stream()
                .filter(SpoonMethodManager::usedOutsideService)
                .filter(SpoonMethodManager::isNonStatic)
                .forEach(method -> serviceImplementation.addMethod(buildMethodImplementation(method)));

        return serviceImplementation;
    }

    private CtMethod<?> buildMethodImplementation(CtMethod<?> method) {
        CtMethod<?> methodImpl = method.clone();
        methodImpl.setModifiers(new HashSet<>());
        methodImpl.addModifier(ModifierKind.PUBLIC);
        if (!method.getType().isPrimitive()) {
            methodImpl.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
        }
        methodImpl.setComments(Collections.emptyList());

        CtTypeReference overrideReference = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Override.class);
        CtAnnotation overrideAnnotation = SpoonFactoryManager.getDefaultFactory().createAnnotation(overrideReference);

        CtParameter objectParam = SpoonFactoryManager.getDefaultFactory().createParameter();
        objectParam.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
        objectParam.setSimpleName("objectReference");
        methodImpl.setParameters(new ArrayList<>());
        methodImpl.addParameterAt(0, objectParam);

        // Don't add the override annotation double
        if (!methodImpl.hasAnnotation(Override.class)) {
            methodImpl.addAnnotation(overrideAnnotation);
        }

        CtBlock codeBlock = SpoonFactoryManager.getDefaultFactory().createBlock();

        List<CtVariableRead> objectCallArguments = new ArrayList<>();

        CtLocalVariable objectCall = retrieveObjectCall(originalClass.getReference(), new ArrayList<>(), objectParam);
        codeBlock.addStatement(objectCall);

        // TODO DOes this create a new list?
        List<CtParameter<?>> otherParams = method.getParameters();
        for (CtParameter parameter : otherParams) {
            CtParameter implementationParameter = parameter.clone();
            CtVariableRead callArgument = SpoonFactoryManager.getDefaultFactory().createVariableRead();

            if (!parameter.getType().isPrimitive() && !parameter.getType().equals(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class))) { // Just check class...?
                // In this case always just "decode"?

                implementationParameter.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));

                CtExecutableReference getObject = SpoonMethodManager.findMethod(serializationUtil.getReference(), "decode").getReference();

                CtTypeAccess serializationTarget = SpoonFactoryManager.getDefaultFactory().createTypeAccess(serializationUtil.getReference());

                CtInvocation serializationRetrievalCall = SpoonFactoryManager.getDefaultFactory().createInvocation();
                serializationRetrievalCall.setTarget(serializationTarget);
                serializationRetrievalCall.setExecutable(getObject);
                CtVariableAccess referenceArgument = SpoonFactoryManager.getDefaultFactory().createVariableRead(implementationParameter.getReference(), false);

                serializationRetrievalCall.addArgument(referenceArgument);
                CtTypeReference objectVariableType = parameter.getType();

                CtLocalVariable variable = SpoonFactoryManager.getDefaultFactory().createLocalVariable(objectVariableType, parameter.getSimpleName() + "Instance", serializationRetrievalCall.addTypeCast(objectVariableType));
                codeBlock.addStatement(variable);

                callArgument.setVariable(variable.getReference());

            } else {
                callArgument.setVariable(parameter.getReference());
            }

            methodImpl.addParameter(implementationParameter);
            objectCallArguments.add(callArgument);
        }
        // TODO Note! Any return (sub)type that is the object itself needs to be converted to its reference id.
        // Not yet seen this case, but needs to be handled. Perhaps look to IBM for examples? Client also needs to convert this back to proxy object
        // A list of object also needs to be converted to a list of its ids?

        CtLocalVariable objectCallVariable = addObjectCall(method, codeBlock, objectCall, objectCallArguments);
        codeBlock.addStatement(objectCallVariable);

        CtVariableRead objectCallVariableRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
        objectCallVariableRead.setVariable(objectCallVariable.getReference());

        // TODO Temporary solution to simply cast
        // TODO Could we check return type of method (simpler?) instead of return type of the actuall service class function call
        if (!objectCallVariableRead.getType().isPrimitive() && !objectCallVariableRead.getType().equals(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class))) {
            CtMethod encodeMethod = serializationUtil.getMethod("encode", SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Object.class));
            CtExecutableReference reference = encodeMethod.getReference();
            CtInvocation encodingCall = SpoonFactoryManager.getDefaultFactory().createInvocation();
            encodingCall.setExecutable(reference);
            CtVariableRead parameterRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
            parameterRead.setVariable(objectCallVariable.getReference());
            encodingCall.addArgument(parameterRead);

            CtTypeAccess serializationTarget = SpoonFactoryManager.getDefaultFactory().createTypeAccess(serializationUtil.getReference());
            encodingCall.setTarget(serializationTarget);
            CtLocalVariable encodedParameter = SpoonFactoryManager.getDefaultFactory().createLocalVariable(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class),"encodedReturn", encodingCall);

            codeBlock.addStatement(encodedParameter);

            objectCallVariableRead.setVariable(encodedParameter.getReference());
        }
        // If result type is not primitive, we still need to do some serialization

        CtReturn functionReturn = SpoonFactoryManager.getDefaultFactory().createReturn();
        functionReturn.setReturnedExpression(objectCallVariableRead);
        codeBlock.addStatement(functionReturn);

        methodImpl.setBody(codeBlock);

        CtTypeReference remoteException = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(RemoteException.class);
        methodImpl.addThrownType(remoteException);
        return methodImpl;
    }

    // TODO Add assignment of reference id to service original class
    private void buildConstructorImplementation(CtConstructor constructor) {
        CtMethod constructorImplementation = SpoonFactoryManager.getDefaultFactory().createMethod();
        CtTypeReference returnType = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class);
        constructorImplementation.setType(returnType);
        constructorImplementation.setSimpleName("new" + constructor.getType());
        constructorImplementation.addModifier(ModifierKind.PUBLIC);

        CtTypeReference overrideReference = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Override.class);
        CtAnnotation overrideAnnotation = SpoonFactoryManager.getDefaultFactory().createAnnotation(overrideReference);
        constructorImplementation.addAnnotation(overrideAnnotation);

        // Make public ?
        CtBlock codeBlock = SpoonFactoryManager.getDefaultFactory().createBlock();
        CtNewClass constructorCall = SpoonFactoryManager.getDefaultFactory().createNewClass();
        constructorCall.setType(originalClassWithId.getReference());
        List<CtExpression> newObjectArguments = new ArrayList<>();
        List<CtParameter> parameters = constructor.getParameters();
        for (CtParameter parameter : parameters) {
            if (!parameter.getType().isPrimitive() && !parameter.getType().equals(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class))) {

                // Adding an empty list to this function call, useless...
// TODO HERE                constructorImplementation.getType() is string?
//                CtLocalVariable parameterObject = retrieveObjectCall(constructorImplementation.getType(), new ArrayList<>(), parameter);
                CtLocalVariable parameterObject = retrieveObjectCall(parameter.getType(), new ArrayList<>(), parameter);
                CtVariableAccess parameterObjectArgument = SpoonFactoryManager.getDefaultFactory().createVariableRead(parameterObject.getReference(), false);
                newObjectArguments.add(parameterObjectArgument);

                CtParameter objectParameter = SpoonFactoryManager.getDefaultFactory().createParameter();
                objectParameter.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
                objectParameter.setSimpleName(parameter.getSimpleName() + "ReferenceId");
                constructorImplementation.addParameter(objectParameter);


                CtLocalVariable objectCall = retrieveObjectCall(parameter.getType(), new ArrayList<>(), objectParameter);
                codeBlock.addStatement(objectCall);

                CtVariableRead objectRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
                objectRead.setVariable(objectCall.getReference());

                constructorCall.addArgument(objectRead);
            } else {
                CtVariableAccess parameterArgument = SpoonFactoryManager.getDefaultFactory().createVariableRead(parameter.getReference(), false);
                newObjectArguments.add(parameterArgument);
                constructorImplementation.addParameter(parameter);

                CtVariableAccess argument = SpoonFactoryManager.getDefaultFactory().createVariableRead(parameter.getReference(), false);
                constructorCall.addArgument(argument);
            }
        }

        System.out.println("null?");
        System.out.println(originalClassWithId);
        System.out.println(originalClassWithId.getReference());
//        originalClassWithId.getMethod()
        System.out.println(originalClassWithId.getReference().getTypeDeclaration());

        CtLocalVariable newObject = SpoonFactoryManager.getDefaultFactory().createLocalVariable(originalClassWithId.getReference(), "newObject", constructorCall.addTypeCast(originalClassWithId.getReference()));
        codeBlock.addStatement(newObject);

        CtInvocation storageAddCall = SpoonFactoryManager.getDefaultFactory().createInvocation();

//        CtClass thisStorage = storageClasses.get(javaClass.getReference());
        CtExecutableReference storageAdd = SpoonMethodManager.findMethod(storageManager.getReference(), "add").getReference();

        CtTypeAccess storageTarget = SpoonFactoryManager.getDefaultFactory().createTypeAccess(storageManager.getReference());

        storageAddCall.setTarget(storageTarget);
        storageAddCall.setExecutable(storageAdd);

        CtVariableRead newObjectRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
        newObjectRead.setType(newObject.getType());
        newObjectRead.setVariable(newObject.getReference());

        storageAddCall.addArgument(newObjectRead);

        CtTypeReference stringReference = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class);
        CtLocalVariable id = SpoonFactoryManager.getDefaultFactory().createLocalVariable(stringReference, "id", storageAddCall);

        codeBlock.addStatement(id);

        // Add id to service object (in order to be able to return it in the service when needed)
        // such as: unLocode.setReferenceId(idFromStorageCall)
        CtInvocation setReferenceId = SpoonFactoryManager.getDefaultFactory().createInvocation();
        setReferenceId.setTarget(newObjectRead);

        // TODO Require Reference Interface?
        CtExecutableReference setReferenceIdExecutable = originalClassWithId.getMethod("setReferenceId", SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class)).getReference();
        setReferenceId.setExecutable(setReferenceIdExecutable);

        CtVariableAccess idRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
        idRead.setVariable(id.getReference());
        setReferenceId.addArgument(idRead);

        codeBlock.addStatement(setReferenceId);


        CtReturn functionReturn = SpoonFactoryManager.getDefaultFactory().createReturn();
        functionReturn.setReturnedExpression(idRead);
        codeBlock.addStatement(functionReturn);

        constructorImplementation.setBody(codeBlock);

        serviceImplementation.addMethod(constructorImplementation);

        // Read params
        // Convert to local objects
        // empty body
        // Call new Object with params/objects
    }

    private CtLocalVariable retrieveObjectCall(CtTypeReference storageObjectType, List<CtVariableRead> objectCallArguments, CtParameter objectParameter) {
        CtVariableRead argument = SpoonFactoryManager.getDefaultFactory().createVariableRead();
        argument.setVariable(objectParameter.getReference());
        objectCallArguments.add(argument);
        CtInvocation serializationRetrievalCall = SpoonFactoryManager.getDefaultFactory().createInvocation();

        System.out.println("objecttype");
        System.out.println(storageObjectType);
//        CtClass storage = storageClasses.get(storageObjectType);

        CtExecutableReference serializationGet;
        serializationGet = SpoonMethodManager.findMethod(serializationUtil.getReference(), "decode").getReference();

        CtTypeAccess serializationTarget = SpoonFactoryManager.getDefaultFactory().createTypeAccess(serializationUtil.getReference());

        serializationRetrievalCall.setTarget(serializationTarget);
        serializationRetrievalCall.setExecutable(serializationGet);
        CtVariableAccess referenceArgument = SpoonFactoryManager.getDefaultFactory().createVariableRead(objectParameter.getReference(), false);

        serializationRetrievalCall.addArgument(referenceArgument);

        return SpoonFactoryManager.getDefaultFactory().createLocalVariable(storageObjectType, objectParameter.getSimpleName() + "Object", serializationRetrievalCall.addTypeCast(storageObjectType));
    }

    private void addConstructor(CtClass serviceImplementation) {
        CtConstructor constructor = SpoonFactoryManager.getDefaultFactory().createConstructor();
        constructor.setSimpleName(serviceImplementation.getSimpleName());
        constructor.addModifier(ModifierKind.PUBLIC);
        CtTypeReference remoteException = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(RemoteException.class);
        constructor.addThrownType(remoteException);
        CtBlock emptyBlock = SpoonFactoryManager.getDefaultFactory().createBlock();
        constructor.setBody(emptyBlock);
        serviceImplementation.addConstructor(constructor);
    }

    private CtLocalVariable addObjectCall(CtMethod<?> method, CtBlock codeBlock, @Nullable CtLocalVariable calledObject, List<CtVariableRead> objectCallArguments) {
        // Make sure the service calls the actual class
        // 1. First find the object in the list of stored references of the class, or call the constructor if it is a new object. So we need to have access to the created storemanager here?
        // Maybe something like a ServiceCreator which keeps track of spoon objects like the interface, impl, clients etc.
//            CtCodeSnippetStatement emptyStatement = SpoonFactoryManager.getFactory().createCodeSnippetStatement("");
        CtInvocation invocation = SpoonFactoryManager.getDefaultFactory().createInvocation();
//            CtTypeReference

        // TODO Is this how this should be done? Almost no code references to how this should be done. Taken from  https://github.com/INRIA/spoon/blob/master/src/main/java/spoon/reflect/factory/CodeSpoonFactoryManager.getFactory().java
        if (calledObject != null) {
            CtVariableAccess referenceObjectTarget = SpoonFactoryManager.getDefaultFactory().createVariableRead(calledObject.getReference(), false);
            invocation.setTarget(referenceObjectTarget);
        }
        // TODO Basiclly just the original call, why do this in such a difficult way?
        // TODO Maybe it's an idea to loop over the code once, going smaller and smaller (classes then methods then parameters) while keeping track of all the different implementations for interfaces/impl etc
        for (CtVariableRead argument : objectCallArguments) {
            invocation.addArgument(argument);
        }

        invocation.setExecutable(method.getReference());

        CtLocalVariable objectCallVariable = SpoonFactoryManager.getDefaultFactory().createLocalVariable(method.getType(), "result", invocation);
        return objectCallVariable;
//        }
        // TODO Are we handling null? I guess returning null for a null function is still valid code?
    }
}
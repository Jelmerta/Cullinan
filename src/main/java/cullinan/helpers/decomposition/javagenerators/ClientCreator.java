package cullinan.helpers.decomposition.javagenerators;

import spoon.javadoc.internal.Javadoc;
import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.ClassFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoonhelpers.managers.SpoonFactoryManager;
import spoonhelpers.managers.SpoonMethodManager;

import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;


// TODO We haven't added protected methods, which usually probably aren't going to be used outside the service, but it is possible with a bad decomposition...
public class ClientCreator {
    private final CtInterface ctInterface;
    private final CtClass serializationUtil;
    CtClass originalClass;
    CtClass result;
    
    // TODO If is complex -> Decode + cast
    public ClientCreator(CtClass originalClass, CtInterface ctInterface, CtClass serializationUtil) {
        this.originalClass = originalClass;
        this.ctInterface = ctInterface;
        this.serializationUtil = serializationUtil;
        this.result = SpoonFactoryManager.getDefaultFactory().createClass(originalClass.getQualifiedName() + "Client");
        this.result.addModifier(ModifierKind.PUBLIC);
        this.result.setFormalCtTypeParameters(originalClass.getFormalCtTypeParameters());
//        this.result.setTypeMembers(originalClass.getTypeMembers());

        //        Adding inner interfaces
        //        TODO Should also add inner static classes?
//        We could also rely on a common module as this is defined in many places now.
//         TODO We want to rely on the dependencies in proxy or maybe use common module... What package does it look for?
//        Set<CtType> nestedTypes = originalClass.getNestedTypes();
//        for (CtType nestedType : nestedTypes) {
//            if (nestedType.isInterface()) {
//                System.out.println("FOUND AN INTERFACE NESTED");
//                result.addTypeMember(nestedType);
//            }
//        }
    }

    public static CtParameter objectify(CtParameter parameter) {
        if ((!parameter.getType().isPrimitive() && !parameter.getType().equals(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class))) || parameter.isVarArgs()) {
            parameter.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Object.class));
            parameter.setVarArgs(false);
        }
        return parameter;
    }

    public CtClass build() {
        createServiceField();
        assignServiceField();
        addClientMethods();
        addClientConstructors();
        addVariableRetrievals();
        addInnerClassMethods();
        removeGenerics();

//         TODO Could this be used to generate classes? Is it better?
//        ClassFactory classFactory = new ClassFactory();

        return result;
    }

    private void addInnerClassMethods() {
        Set<CtType> nestedTypes = originalClass.getNestedTypes();
        for (CtType nestedType : nestedTypes) {
            if (nestedType.isClass() || nestedType.isEnum()) { // TODO Any other things we need to move?
//                ServiceInterfaceCreator serviceInterfaceCreator = new ServiceInterfaceCreator((CtClass) nestedType);
//                CtInterface ctInterface = serviceInterfaceCreator.buildInterface();
                ClientCreator clientCreator = new ClientCreator((CtClass) nestedType, ctInterface, serializationUtil);
                CtClass client = clientCreator.build();
                Set<CtMethod> clientAllMethods = client.getMethods(); // TODO methods vs all methods
                for (CtMethod method : clientAllMethods) {
                    if (method.getSimpleName().contains("retrieveVariable")) { // Hack upon hack. We make sure not to add inner class again as we already did this
                        result.addMethod(method);
                        continue;
                    }
                    String newMethodName = nestedType.getSimpleName().substring(0, 1).toLowerCase() + nestedType.getSimpleName().substring(1) + "InnerClass" + method.getSimpleName().substring(0,1).toUpperCase() + method.getSimpleName().substring(1);
                    method.setSimpleName(newMethodName);
                    result.addMethod(method);
                }
            }
        }

    }

    private void createServiceField() {
        CtField field = SpoonFactoryManager.getDefaultFactory().createField();
        field.setType(ctInterface.getReference());
        field.addModifier(ModifierKind.PRIVATE);
        field.addModifier(ModifierKind.STATIC);
        field.setSimpleName("service");
        result.addField(field);
    }

    private void assignServiceField() {
        Factory templateFactory = SpoonFactoryManager.getFactory(Path.of("src/main/java/cullinan/helpers/templates/ServiceClientTemplate.java")); // TODO Hardcode not good
        // TODO No longer an anonymousexecutable
        CtClass objectCtClass = templateFactory.Class().get("cullinan.helpers.templates.ServiceClientTemplate");
        Set<CtConstructor> constructors = objectCtClass.getConstructors();
        CtConstructor ctConstructor = constructors.stream().toList().get(0);
        PatternBuilder serviceAssignmentPattern = PatternBuilder.create(ctConstructor);
        Pattern serviceName = serviceAssignmentPattern
                .configurePatternParameters(pb -> {
                    pb.parameter("serviceClassName").byType(ClassNotFoundException.class);
                    pb.parameter("serviceVarName").byVariable("service");
                    pb.parameter("serviceLookupName").byString("//localhost/microserviceName");
                })
                .build();

        Map<String, Object> params = new HashMap<>();
        params.put("serviceClassName", ctInterface.getReference());
        CtLocalVariable localVariable = SpoonFactoryManager.getDefaultFactory().createLocalVariable().setSimpleName("service"); // Does not change
        CtVariableWrite variableWrite = SpoonFactoryManager.getDefaultFactory().createVariableWrite();
        variableWrite.setVariable(localVariable.getReference());
        params.put("serviceVarName", variableWrite);
        params.put("serviceLookupName", "//localhost/" + ctInterface.getSimpleName().replace("Interface", ""));

        List<CtConstructor> constructor = serviceName.generator()
                .generate(params);

        result.addConstructor(constructor.get(0));
//        result.addAnonymousExecutable(staticAssignmentBlock.get(0));
    }

    private void addClientMethods() {
        Set<CtMethod> methods = originalClass.getMethods();
        methods.stream()
                .filter(SpoonMethodManager::usedOutsideService)
//                .filter(SpoonMethodManager::isNonStatic) // We also want static methods
                .forEach(method -> addClientMethod(method, method.getSimpleName(), false));
    }

    private void addClientConstructors() {
        Set<CtConstructor> constructors = originalClass.getConstructors();
        constructors.stream()
                .filter(SpoonMethodManager::usedOutsideService)
                .filter(SpoonMethodManager::isNonStatic)
                .forEach(constructor -> addClientMethod(constructor, "new" + constructor.getType().getSimpleName(), true));
    }

    private void addVariableRetrievals() {
        List<CtField> fields = originalClass.getFields();
        for (CtField field : fields) {
            if (!field.isPrivate()) {
                createFieldInterfaceMethod(field);
            }
        }

//
//        Set<CtMethod> interfaceMethods = ctInterface.getMethods();
//        List<CtMethod> variableRetrievalMethods = interfaceMethods.stream()
//                .filter(interfaceMethod -> interfaceMethod.getSimpleName().contains("retrieveVariable"))
//                .collect(Collectors.toList());
//
//        for (CtMethod variableRetrievalMethod : variableRetrievalMethods) {
//            CtMethod clientMethod = createVariableRetrievalMethod(variableRetrievalMethod);
//            result.addMethod(clientMethod);
//        }
    }

    private void createFieldInterfaceMethod(CtField field) {
        String fullVariableName = ProxyCreator.findFullMethodName(originalClass, field.getSimpleName());
        fullVariableName = fullVariableName.substring(0, 1).toUpperCase() + fullVariableName.substring(1);
        String retrieveVarMethodName = "retrieveVariable" + fullVariableName;
        CtMethod variableRetrievalMethod = createVariableRetrievalMethod(retrieveVarMethodName);
        result.addMethod(variableRetrievalMethod);
    }

    private CtMethod createVariableRetrievalMethod(String retrieveVarName) {
        // Add client call to service
        System.out.println(retrieveVarName);
        System.out.println(ctInterface.getReference().getTypeDeclaration().getMethods());
        List<CtMethod> methodsByName = ctInterface.getReference().getTypeDeclaration().getMethodsByName(retrieveVarName);
        CtMethod interfaceMethod = methodsByName.get(0);
        CtMethod clientMethod = interfaceMethod.clone();
        clientMethod.setThrownTypes(Collections.emptySet());
        clientMethod.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Object.class));

        CtExecutableReference interfaceMethodExecutable = interfaceMethod.getReference();
        CtBlock<Object> body = SpoonFactoryManager.getDefaultFactory().createBlock();

        CtVariableRead serviceVariable = SpoonFactoryManager.getDefaultFactory().createVariableRead();
        CtFieldReference service = result.getField("service").getReference();
        serviceVariable.setVariable(service);
        CtInvocation serviceCall = SpoonFactoryManager.getDefaultFactory().createInvocation(serviceVariable, interfaceMethodExecutable);

        // Add argument if available in interface to client call, otherwise not
        List<CtParameter> parameters = clientMethod.getParameters();
        if (!parameters.isEmpty()) {
            CtParameter objectReferenceId = parameters.get(0);
            CtVariableRead argument = SpoonFactoryManager.getDefaultFactory().createVariableRead();
            argument.setVariable(objectReferenceId.getReference());
            serviceCall.addArgument(argument);
        }

        CtMethod decodeMethod = serializationUtil.getMethod("decode", SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
        CtExecutableReference reference = decodeMethod.getReference();
        CtInvocation decodeCall = SpoonFactoryManager.getDefaultFactory().createInvocation();
        decodeCall.setTarget(SpoonFactoryManager.getDefaultFactory().createTypeAccess(serializationUtil.getReference()));
        decodeCall.setExecutable(reference);
        decodeCall.addArgument(serviceCall);
        decodeCall.addTypeCast(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Object.class));

        CtReturn ctReturn = SpoonFactoryManager.getDefaultFactory().createReturn();
        ctReturn.setReturnedExpression(decodeCall);

        body.addStatement(ctReturn);

        CtTry tryBlock = wrapTryCatchRemoteException(body);

        clientMethod.setBody(tryBlock);

        return clientMethod;
    }

    // TODO (de)serialization and reference id mapping
    private void addClientMethod(CtExecutable originalMethod, String functionName, boolean isConstructor) {
        // Could just clone method, only body should be different?
        CtMethod clientMethod = SpoonFactoryManager.getDefaultFactory().createMethod();
        clientMethod.setSimpleName(functionName);

        if (isConstructor) {
            clientMethod.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
        } else {
            CtMethod ctMethod = (CtMethod) originalMethod;
            clientMethod.setFormalCtTypeParameters(ctMethod.getFormalCtTypeParameters());
            clientMethod.setType(originalMethod.getType());
        }
        List<CtParameter> parameters = originalMethod.getParameters();
        // TODO List of readable variables for function call?

        // TODO Are there other cases where we need to override? Interfaces?
//        if (originalMethod.getSimpleName().equals("equals") || originalMethod.getSimpleName().equals("hashCode") || originalMethod.getSimpleName().equals("toString")) {
//            clientMethod.addAnnotation(SpoonFactoryManager.getDefaultFactory().createAnnotation(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Override.class)));
//        }


        List<CtVariableAccess> serviceCallVariables = new ArrayList<>();

        if (!isConstructor && !((CtMethod<?>) originalMethod).isStatic()) { // Constructors and static methods do not have an object to call on
            CtParameter objectParam = SpoonFactoryManager.getDefaultFactory().createParameter();
            objectParam.setSimpleName("objectReferenceId");
            objectParam.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
            clientMethod.addParameter(objectParam);

            CtVariableRead objectParamRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
            objectParamRead.setVariable(objectParam.getReference());
            serviceCallVariables.add(objectParamRead);
        }


        CtBlock body = SpoonFactoryManager.getDefaultFactory().createBlock();

        // TODO if object is passed, dynamically should be reference id... Right? Can't just do this logic here then...?
        for (CtParameter parameter : parameters) {
            CtParameter newParameter = parameter.clone();
            clientMethod.addParameter(newParameter);

            if (!newParameter.getType().isPrimitive() && !newParameter.getType().equals(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class))) {
                CtMethod encodeMethod = serializationUtil.getMethod("encode", SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Object.class));
                CtExecutableReference reference = encodeMethod.getReference();
                CtInvocation encodingCall = SpoonFactoryManager.getDefaultFactory().createInvocation();
                encodingCall.setExecutable(reference);
                CtVariableRead parameterRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
                parameterRead.setVariable(newParameter.getReference());
                encodingCall.addArgument(parameterRead);

                CtTypeAccess serializationTarget = SpoonFactoryManager.getDefaultFactory().createTypeAccess(serializationUtil.getReference());
                encodingCall.setTarget(serializationTarget);


                CtLocalVariable encodedParameter = SpoonFactoryManager.getDefaultFactory().createLocalVariable(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class), newParameter.getSimpleName() + "Encoded", encodingCall);

                body.addStatement(encodedParameter);
                CtVariableRead encodedVariable = SpoonFactoryManager.getDefaultFactory().createVariableRead();
                encodedVariable.setVariable(encodedParameter.getReference());
                serviceCallVariables.add(encodedVariable);

            } else {
                CtVariableRead parameterRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
                parameterRead.setVariable(parameter.getReference());
                serviceCallVariables.add(parameterRead);
            }
        }
        clientMethod.addModifier(ModifierKind.PUBLIC);
//        clientMethod.addModifier(ModifierKind.STATIC); We have now chosen for an initialised client, so no calls through static methods...?


        CtStatement lastStatement;
        CtReturn returnStatement = SpoonFactoryManager.getDefaultFactory().createReturn();
//        CtExecutableReference interfaceMethod = SpoonMethodManager.findMethod(ctInterface.getReference(), originalMethod.getSimpleName()).getReference();
        List<CtParameter> parameterList = originalMethod.getParameters();
        List<CtTypeReference> collect = new ArrayList<>();
        if (!isConstructor && !((CtMethod<?>) originalMethod).isStatic()) { // Constructors and static methods do not have an object to call on.
            collect.add(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class)); // TODO Not for constructors...?
        }
        collect.addAll(
            parameterList.stream().map(CtParameter::getReference).map(ref ->
            {
                if (!ref.getType().isPrimitive() && !ref.getType().equals(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class))) {
                    return SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class); // TODO No need to map if it is already string...?
                } else {
                    return ref.getType();
                }
            }).collect(Collectors.toList())
        );

//        if (originalClass.getSimpleName().contains("ursor")) {
//            System.out.println("MMMM");
//            System.out.println(originalClass.getSimpleName());
//            System.out.println(functionName);
//            System.out.println(ctInterface.getReference().getTypeDeclaration().getAllMethods());
//        }

//        String newMethodName = nestedType.getSimpleName().substring(0, 1).toLowerCase() + nestedType.getSimpleName().substring(1) + "InnerClass" + method.getSimpleName().substring(0,1).toUpperCase() + method.getSimpleName().substring(1);
//        method.setSimpleName(newMethodName);
//        TODO Where does this belong?

        // Finding method by appending parent classes
//        It's beautiful (would prefer to generate separate proxies and clients maybe, but might be hard to pass. Proxy might need multiple cients then for example. Might also be other ways, topdown?)
        if (!originalClass.isTopLevel()) { // isInnerClass
            functionName = originalClass.getSimpleName().substring(0, 1).toLowerCase() + originalClass.getSimpleName().substring(1) + "InnerClass" + functionName.substring(0, 1).toUpperCase() + functionName.substring(1);
//            System.out.println(functionName);
            CtClass parent = (CtClass) originalClass.getParent();
            if (!parent.isTopLevel()) { // isInnerClass
                functionName = parent.getSimpleName().substring(0, 1).toLowerCase() + parent.getSimpleName().substring(1) + "InnerClass" + functionName.substring(0, 1).toUpperCase() + functionName.substring(1);
//                System.out.println(functionName);
                parent = (CtClass) parent.getParent();
                if (!parent.isTopLevel()) { // isInnerClass
                    functionName = parent.getSimpleName().substring(0, 1).toLowerCase() + parent.getSimpleName().substring(1) + "InnerClass" + functionName.substring(0, 1).toUpperCase() + functionName.substring(1);
//                    System.out.println(functionName);
                }
            }
        }

        CtExecutableReference interfaceMethod = ctInterface.getReference().getTypeDeclaration().getMethod(functionName, collect.toArray(new CtTypeReference[0])).getReference();
//        System.out.println(ctInterface.getReference().getTypeDeclaration().getMethod(functionName, collect.toArray(new CtTypeReference[0])).getSimpleName());
//        CtExecutableReference interfaceMethod = SpoonMethodManager.findMethod(ctInterface.getReference(), functionName).getReference();

        CtVariableRead serviceVariable = SpoonFactoryManager.getDefaultFactory().createVariableRead();
        CtFieldReference service = result.getField("service").getReference();
        serviceVariable.setVariable(service);

        CtInvocation serviceCall = SpoonFactoryManager.getDefaultFactory().createInvocation(serviceVariable, interfaceMethod);
        serviceCall.setArguments(serviceCallVariables);

        // Now overwrite Client method to return object if not primitive
        CtTypeReference returnType = clientMethod.getType();
        if (!returnType.isPrimitive() && !returnType.equals(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class))) {
            returnType = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Object.class);
            clientMethod.setType(returnType);
        }
        clientMethod.setFormalCtTypeParameters(Collections.EMPTY_LIST);

        // This could theoretically lead to two methods with a different object param to become the same method. Not optimal maybe... Think functionally it's fine since encode is called and made sure to encode correctly for the type
        List<CtParameter> clientParameters = clientMethod.getParameters();
        clientMethod.setParameters(Collections.emptyList());
        for (CtParameter parameter : clientParameters) {
            System.out.println(clientMethod);
            System.out.println(clientParameters);
            System.out.println(parameter);
            clientMethod.addParameter(objectify(parameter));
//            if (parameter.isVarArgs() || (!parameter.getType().equals(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class)))) {
//                parameter.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Object.class));
//            }
        }

        // Remove client method generic types (can lead to runtime errors, but otherwise we cannot cast to the generic variant as the class lives in proxy)
        // If return type does not match service call's return type, we need to deserialize the result (this happens when the return type is "complex")
//        CtTypeReference returnType = clientMethod.getType();
////        if (returnType.isGenerics()) { Does not seem to work... Probably is a good way to do this...
//        returnType.setActualTypeArguments(Collections.EMPTY_LIST); // We remove generics as otherwise we cannot cast to the generic variant
////        Hacky way to find a generic... Classes could just be one character long...
//        if (returnType.getSimpleName().length() == 1) { // isGeneric...
//            returnType = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Object.class);
//        }
//        clientMethod.setType(returnType);
//
//        List<CtParameter> clientParameters = clientMethod.getParameters();
//        clientMethod.setParameters(Collections.emptyList());
//        for (CtParameter parameter : clientParameters) {
//            CtTypeReference type = parameter.getType();
//            type.setActualTypeArguments(Collections.EMPTY_LIST);
//            if (type.getSimpleName().length() == 1) {
//                type = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Object.class);
//            }
//            parameter.setType(type);// Not sure if required
//            clientMethod.addParameter(parameter);
//        }

        if (!returnType.isPrimitive() && !returnType.equals(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class))) {
            CtLocalVariable encodedString = SpoonFactoryManager.getDefaultFactory().createLocalVariable(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class), "encodedServiceResult", serviceCall);
            body.addStatement(encodedString);

            // TODO Add assertion that return type of service is string? Because that is required for the decode call. Any situations where this is not the case?
            CtMethod decodeMethod = serializationUtil.getMethod("decode", SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
            CtExecutableReference reference = decodeMethod.getReference();
            CtInvocation decodeCall = SpoonFactoryManager.getDefaultFactory().createInvocation();
            decodeCall.setTarget(SpoonFactoryManager.getDefaultFactory().createTypeAccess(serializationUtil.getReference()));
            decodeCall.setExecutable(reference);
            CtVariableRead parameterRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
            parameterRead.setVariable(encodedString.getReference());
            decodeCall.addArgument(parameterRead);
            decodeCall.addTypeCast(returnType);
            returnStatement.setReturnedExpression(decodeCall);
            lastStatement = returnStatement;
        } else {
            if (returnType.equals(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(void.class))){
                lastStatement = serviceCall;
            } else {
                returnStatement.setReturnedExpression(serviceCall);
                lastStatement = returnStatement;
            }
        }

        body.addStatement(lastStatement);

        CtTry tryBlock = wrapTryCatchRemoteException(body);

        clientMethod.setBody(tryBlock);

        result.addMethod(clientMethod);
    }

    private CtTry wrapTryCatchRemoteException(CtStatement returnStatement) {
        CtTry ctTry = SpoonFactoryManager.getDefaultFactory().createTry();
        ctTry.setBody(returnStatement);

        CtCatch ctCatch = SpoonFactoryManager.getDefaultFactory().createCatch();
        CtCatchVariable exception = SpoonFactoryManager.getDefaultFactory().createCatchVariable();
        exception.setSimpleName("exception");
        exception.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(RemoteException.class));
        ctCatch.setParameter(exception);

        CtNewClass newException = SpoonFactoryManager.getDefaultFactory().createNewClass();
        newException.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(RuntimeException.class));
        CtVariableRead exceptionRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
        exceptionRead.setVariable(exception.getReference());
        newException.addArgument(exceptionRead);
        CtThrow aThrow = SpoonFactoryManager.getDefaultFactory().createThrow();
        CtStatement throwException = aThrow.setThrownExpression(newException);
        ctCatch.setBody(throwException);
        ctTry.addCatcher(ctCatch);

        return ctTry;
    }

//    public static void main(String[] args) {
//        CtClass originalClass = SpoonFactoryManager.getDefaultFactory().Class().get("cullinan.test.imports.SimpleClass");
//
//        ServiceInterfaceCreator serviceInterfaceCreator = new ServiceInterfaceCreator(originalClass);
//        CtInterface ctInterface = serviceInterfaceCreator.buildInterface();
//
//        ServiceClientCreator serviceClientCreator = new ServiceClientCreator(originalClass, ctInterface);
//        serviceClientCreator.build();
//    }


    private void removeGenerics() {
        Set<CtMethod> methods = result.getMethods();
        for (CtMethod method : methods) {
//            removeParameterGenerics();
//            removeReturnTypeGenerics();
//            removeCastGenerics();
        }
    }

    private void removeCastGenerics(CtMethod method) {
//        Done during creation of body...
    }

    public static CtTypeReference removeGenerics(CtTypeReference typeReference) {
        typeReference.setActualTypeArguments(Collections.EMPTY_LIST);
        if (typeReference.getSimpleName().length() == 1) {
            typeReference = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Object.class);
        }
        return typeReference;
    }
}
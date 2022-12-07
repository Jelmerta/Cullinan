package cullinan.helpers.decomposition.javagenerators;

import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
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
    }

    public CtClass build() {
        createServiceField();
        assignServiceField();
        addClientMethods();
        addClientConstructors();

        System.out.println(result);
        return result;
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
        PatternBuilder serviceAssignmentPattern = PatternBuilder.create(templateFactory.Class().get("cullinan.helpers.templates.ServiceClientTemplate").getAnonymousExecutables());
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

        List<CtAnonymousExecutable> staticAssignmentBlock = serviceName.generator()
                .generate(params);

        result.addAnonymousExecutable(staticAssignmentBlock.get(0));
    }

    private void addClientMethods() {
        Set<CtMethod> methods = originalClass.getMethods();
        methods.stream()
                .filter(SpoonMethodManager::usedOutsideService)
                .filter(SpoonMethodManager::isNonStatic)
                .forEach(method -> addClientMethod(method, method.getSimpleName(), false));
    }

    private void addClientConstructors() {
        Set<CtConstructor> constructors = originalClass.getConstructors();
        constructors.stream()
                .filter(SpoonMethodManager::usedOutsideService)
                .filter(SpoonMethodManager::isNonStatic)
                .forEach(constructor -> addClientMethod(constructor, "new" + constructor.getType().getSimpleName(), true));
    }

    // TODO (de)serialization and reference id mapping
    private void addClientMethod(CtExecutable originalMethod, String functionName, boolean isConstructor) {
        // Could just clone method, only body should be different?
        CtMethod clientMethod = SpoonFactoryManager.getDefaultFactory().createMethod();
        clientMethod.setSimpleName(functionName);
        if (isConstructor) {
            clientMethod.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
        } else {
            clientMethod.setType(originalMethod.getType());
        }
        List<CtParameter> parameters = originalMethod.getParameters();
        System.out.println();
        System.out.println(clientMethod.getSimpleName());
        // TODO List of readable variables for function call?

        // TODO Are there other cases where we need to override? Interfaces?
//        if (originalMethod.getSimpleName().equals("equals") || originalMethod.getSimpleName().equals("hashCode") || originalMethod.getSimpleName().equals("toString")) {
//            clientMethod.addAnnotation(SpoonFactoryManager.getDefaultFactory().createAnnotation(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Override.class)));
//        }


        List<CtVariableAccess> serviceCallVariables = new ArrayList<>();

        if (!isConstructor) {
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
        clientMethod.addModifier(ModifierKind.STATIC);

        CtReturn returnStatement = SpoonFactoryManager.getDefaultFactory().createReturn();
//        CtExecutableReference interfaceMethod = SpoonMethodManager.findMethod(ctInterface.getReference(), originalMethod.getSimpleName()).getReference();
        List<CtParameter> parameterList = originalMethod.getParameters();
        List<CtTypeReference> collect = new ArrayList<>();
        if (!isConstructor) {
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

        System.out.println();
        System.out.println(ctInterface.getReference().getTypeDeclaration());
        System.out.println(functionName);
        System.out.println(collect);
        CtExecutableReference interfaceMethod = ctInterface.getReference().getTypeDeclaration().getMethod(functionName, collect.toArray(new CtTypeReference[0])).getReference();
//        CtExecutableReference interfaceMethod = SpoonMethodManager.findMethod(ctInterface.getReference(), functionName).getReference();

        CtVariableRead serviceVariable = SpoonFactoryManager.getDefaultFactory().createVariableRead();
        CtFieldReference service = result.getField("service").getReference();
        serviceVariable.setVariable(service);

        CtInvocation serviceCall = SpoonFactoryManager.getDefaultFactory().createInvocation(serviceVariable, interfaceMethod);
        serviceCall.setArguments(serviceCallVariables);


        // If return type does not match service call's return type, we need to deserialize the result (this happens when the return type is "complex")
        CtTypeReference returnType = clientMethod.getType();
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
        } else {
            returnStatement.setReturnedExpression(serviceCall);
        }

        body.addStatement(returnStatement);

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
}
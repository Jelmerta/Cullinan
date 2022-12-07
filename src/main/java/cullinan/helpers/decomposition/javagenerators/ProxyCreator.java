package cullinan.helpers.decomposition.javagenerators;

import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoonhelpers.managers.SpoonFactoryManager;
import spoonhelpers.managers.SpoonMethodManager;
import util.CullinanId;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// TODO Constants should simply be retrieved from the service instead of redefined

// TODO We don't need to add reference (shouldnt add) reference id if class cannot be instantiated. Purely static class does not need it.

// TODO We should add static blocks?

// TODO Implicit constructor... Should they also set reference id? Or just no function made at all? Could be used though...
public class ProxyCreator {
    private final CtClass originalClass;
    private final CtInterface referenceInterface;
    private final CtClass client;
    private final CtClass serializationUtil;
    private CtClass serviceProxy;

    public ProxyCreator(CtClass originalClass, CtInterface referenceInterface, CtClass client, CtClass serializationUtil) { // TODO SerializationUtil should differ for monolith/location right...
        this.originalClass = originalClass;
        this.referenceInterface = referenceInterface;
        this.client = client;
        this.serializationUtil = serializationUtil;
    }

    // TODO OVerriddes remove?
    public CtClass build() {
        serviceProxy = SpoonFactoryManager.getDefaultFactory().createClass(originalClass.getQualifiedName());
        serviceProxy.addModifier(ModifierKind.PUBLIC);
        serviceProxy.setSuperclass(originalClass.getSuperclass());
        serviceProxy.setSuperInterfaces(originalClass.getSuperInterfaces());

        // Add reference interface
        serviceProxy.addSuperInterface(referenceInterface.getReference());

        // Add field
        CtField referenceId = SpoonFactoryManager.getDefaultFactory().createField();
        referenceId.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
        referenceId.setSimpleName("referenceId");
        referenceId.addModifier(ModifierKind.PRIVATE); // TODO It's not private?
        serviceProxy.addField(referenceId);

        CtVariableAccess referenceIdArgument = SpoonFactoryManager.getDefaultFactory().createVariableRead();
        referenceIdArgument.setVariable(referenceId.getReference());

        // Get method
        CtMethod getReference = SpoonFactoryManager.getDefaultFactory().createMethod();
        getReference.addModifier(ModifierKind.PUBLIC);
        getReference.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
        getReference.setSimpleName("getReferenceId");
        CtBlock getBody = SpoonFactoryManager.getDefaultFactory().createBlock();
        CtReturn ctReturn = SpoonFactoryManager.getDefaultFactory().createReturn();
        ctReturn.setReturnedExpression(referenceIdArgument);
        getBody.addStatement(ctReturn);
        getReference.setBody(getBody);

        serviceProxy.addMethod(getReference);

        // Set method
        CtMethod setReference = SpoonFactoryManager.getDefaultFactory().createMethod();
        setReference.addModifier(ModifierKind.PUBLIC);
        setReference.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(void.class));
        setReference.setSimpleName("setReferenceId");

        CtParameter referenceIdParam = SpoonFactoryManager.getDefaultFactory().createParameter();
        referenceIdParam.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
        referenceIdParam.setSimpleName("referenceId");
        setReference.addParameter(referenceIdParam);
        CtVariableAccess referenceRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
        referenceRead.setVariable(referenceIdParam.getReference());

        CtBlock setBody = SpoonFactoryManager.getDefaultFactory().createBlock();
        CtField referenceIdField = serviceProxy.getField("referenceId");
        CtAssignment variableAssignment = SpoonFactoryManager.getDefaultFactory().createVariableAssignment(referenceIdField.getReference(), false, referenceRead);

        setBody.addStatement(variableAssignment);
        setReference.setBody(setBody);

        serviceProxy.addMethod(setReference);

        System.out.println("PROXY");
        System.out.println(serviceProxy);
        System.out.println(serviceProxy.getReference());
        System.out.println(serviceProxy.getReference().getTypeDeclaration());

        // TODO We could just use the set function... doesn't really matter though?
        // Add constructor calls and make sure reference id is set
        Set<CtConstructor> constructors = originalClass.getConstructors();
        for (CtConstructor constructor : constructors) {
            if (!SpoonMethodManager.usedOutsideService(constructor)) {
                continue;
            }

            if (!SpoonMethodManager.isNonStatic(constructor)) {
                continue;
            }

            CtConstructor proxyConstructor = constructor.clone();

            CtBlock body = SpoonFactoryManager.getDefaultFactory().createBlock();
            List<CtVariableAccess> clientCallArguments = new ArrayList<>();

            List<CtParameter> parameters = constructor.getParameters();
            handleParameters(body, clientCallArguments, parameters);

            // Find client call
            List<CtParameter> parameterList = constructor.getParameters();

            List<CtTypeReference> collect = new ArrayList<>();
            collect.addAll(
                    parameterList.stream().map(CtParameter::getReference).map(ref ->
                    {
//                        if (!ref.getType().isPrimitive() && !ref.getType().equals(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class))) {
//                            return SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class); // TODO No need to map if it is already string...?
//                        } else {
                            return ref.getType();
//                        }
                    }).collect(Collectors.toList())
            );
            System.out.println();
            System.out.println(client.getReference().getTypeDeclaration());
            System.out.println(originalClass.getSimpleName());
            System.out.println(collect);

//            List<CtParameterReference> collect = parameterList.stream().map(CtParameter::getReference).collect(Collectors.toList());
            CtExecutableReference clientCall = client.getReference().getTypeDeclaration().getMethod("new" + originalClass.getSimpleName(), collect.toArray(new CtTypeReference[0])).getReference();
//            CtExecutableReference clientCall = SpoonMethodManager.findMethod(client.getReference(), "new" + originalClass.getSimpleName()).getReference();// TODO Multiple methods with different params should be taken into account
//            CtVariableRead referenceRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
//            referenceRead.setVariable(referenceId.getReference());
            CtInvocation clientInvocation = SpoonFactoryManager.getDefaultFactory().createInvocation(); // I feel like it should be easier to just use the arguments for createInvocation, but usage is a bit different then manually setting everything.
            CtTypeAccess target = SpoonFactoryManager.getDefaultFactory().createTypeAccess(client.getReference());
            clientInvocation.setTarget(target);
            clientInvocation.setExecutable(clientCall);
            clientInvocation.setArguments(clientCallArguments);

//            CtLocalVariable newId = SpoonFactoryManager.getDefaultFactory().createLocalVariable(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class), "id", clientInvocation);
            CtAssignment assignId = SpoonFactoryManager.getDefaultFactory().createVariableAssignment(referenceId.getReference(), false, clientInvocation);


//            CtReturn returnClientCall = SpoonFactoryManager.getDefaultFactory().createReturn();
//            returnClientCall.setReturnedExpression(clientInvocation);



            body.addStatement(assignId);
            proxyConstructor.setBody(body);

            serviceProxy.addConstructor(proxyConstructor);
        }

        // Add original calls and map to client calls using reference id
        Set<CtMethod> methods = originalClass.getMethods();
        for (CtMethod method : methods) { // TODO Only relevant methods, should be one definition for interface + here + everywhere (public methods, package?)
            if (!SpoonMethodManager.usedOutsideService(method)) {
                continue;
            }

            if (!SpoonMethodManager.isNonStatic(method)) {
                continue;
            }

            CtMethod proxyCall = method.clone();

            CtBlock body = SpoonFactoryManager.getDefaultFactory().createBlock();
            List<CtVariableAccess> clientCallArguments = new ArrayList<>();

            clientCallArguments.add(referenceIdArgument);

            // For every param, if needed encode, store in var and add to list of client call arguments

            List<CtParameter> parameters = method.getParameters();
            handleParameters(body, clientCallArguments, parameters);

            // Find client call
            List<CtParameter> parameterList = method.getParameters();
//            List<CtParameterReference> collect = parameterList.stream().map(CtParameter::getReference).collect(Collectors.toList());
            List<CtTypeReference> collect = new ArrayList<>();
            collect.add(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class)); // TODO Not for constructors...?
            collect.addAll(
                    parameterList.stream().map(CtParameter::getReference).map(ref ->
                    {
//                        if (!ref.getType().isPrimitive() && !ref.getType().equals(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class))) {
//                            return SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class); // TODO No need to map if it is already string...?
//                        } else {
                            return ref.getType();
//                        }
                    }).collect(Collectors.toList())
            );

            System.out.println();
            System.out.println(client.getReference().getTypeDeclaration());
            System.out.println(originalClass.getSimpleName());
            System.out.println(collect);
            CtExecutableReference clientCall = client.getReference().getTypeDeclaration().getMethod(method.getSimpleName(), collect.toArray(new CtTypeReference[0])).getReference();
//            CtExecutableReference clientCall = client.getReference().getTypeDeclaration().getMethod("new" + originalClass.getSimpleName(), collect.toArray(new CtTypeReference[0])).getReference();
//            CtExecutableReference clientCall = SpoonMethodManager.findMethod(client.getReference(), method.getSimpleName()).getReference();// TODO Multiple methods with different params should be taken into account
//            CtVariableRead referenceRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
//            referenceRead.setVariable(referenceId.getReference());
            CtInvocation clientInvocation = SpoonFactoryManager.getDefaultFactory().createInvocation(); // I feel like it should be easier to just use the arguments for createInvocation, but usage is a bit different then manually setting everything.
            CtTypeAccess target = SpoonFactoryManager.getDefaultFactory().createTypeAccess(client.getReference());
            clientInvocation.setTarget(target);
            clientInvocation.setExecutable(clientCall);
            clientInvocation.setArguments(clientCallArguments);

            CtReturn returnClientCall = SpoonFactoryManager.getDefaultFactory().createReturn();
            returnClientCall.setReturnedExpression(clientInvocation);

            body.addStatement(returnClientCall);
            proxyCall.setBody(body);

//            CtMethod proxyCall = SpoonFactoryManager.getDefaultFactory().createMethod();
//            proxyCall.setSimpleName(method.getSimpleName());
//            proxyCall.setType(method.getType());
//            proxyCall.addModifier(method)
//
//            List<CtParameter> parameters = method.getParameters();
//            for (CtParameter parameter : parameters) {
//                CtParameter proxyParam = SpoonFactoryManager.getDefaultFactory().createParameter();
//                proxyParam.setType(parameter.getType());
//                proxyParam.setSimpleName(parameter.getSimpleName());
//                proxyCall.addParameter(proxyParam);
//            }
//
            serviceProxy.addMethod(proxyCall);
        }

        for (CtMethod method : methods) {
            if (method.isStatic()) {
                serviceProxy.addMethod(method);
            }
        }

        // Add constants (Defined how? Public static final? Package also fine?)
        List<CtField> fields = originalClass.getFields();
        for (CtField field : fields) {
            if ( (field.isPublic() || (!field.isPrivate() && !field.isProtected())) && field.isStatic() && field.isFinal()) {
                serviceProxy.addField(field);
            }
        }

        serviceProxy.setAnonymousExecutables(originalClass.getAnonymousExecutables());

        addProxyInstanceCreatorFromReferenceId();

        return serviceProxy;
    }

    private void handleParameters(CtBlock body, List<CtVariableAccess> clientCallArguments, List<CtParameter> parameters) {
        for (CtParameter parameter : parameters) {
            CtVariableAccess paramArgument = SpoonFactoryManager.getDefaultFactory().createVariableRead();
            paramArgument.setVariable(parameter.getReference());

//            if (!parameter.getType().isPrimitive() && !parameter.getType().equals(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class))) { // Needs encoding
//                // TODO How do we decide when to reference id and when to encode...? Does monolith need internal information of every service? Probably not.
//                CtExecutableReference encode = SpoonMethodManager.findMethod(serializationUtil.getReference(), "encode").getReference();
//                CtInvocation encodeInvocation = SpoonFactoryManager.getDefaultFactory().createInvocation();
//                CtTypeAccess serializationTarget = SpoonFactoryManager.getDefaultFactory().createTypeAccess(serializationUtil.getReference());
//                encodeInvocation.setTarget(serializationTarget);
//                encodeInvocation.setExecutable(encode);
//
//                CtVariableAccess parameterRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
//                parameterRead.setVariable(parameter.getReference());
//                encodeInvocation.addArgument(parameterRead);
//
//                CtLocalVariable paramSerialized = SpoonFactoryManager.getDefaultFactory().createLocalVariable(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class), parameter.getSimpleName() + "Serialized", encodeInvocation);
//
//
//
//
////                body.addStatement(paramSerialized);
//            }

            clientCallArguments.add(paramArgument);
        }
    }

    // Create constructor with return type an instance of serviceProxy
    // Parameter is a CullinanId
    // We set the reference id
    private void addProxyInstanceCreatorFromReferenceId() {
        CtConstructor proxyInstanceConstructor = SpoonFactoryManager.getDefaultFactory().createConstructor();
        proxyInstanceConstructor.addModifier(ModifierKind.PUBLIC);
        proxyInstanceConstructor.setType(serviceProxy.getReference());
        proxyInstanceConstructor.setSimpleName(serviceProxy.getSimpleName());

        CtParameter cullinanIdParameter = SpoonFactoryManager.getDefaultFactory().createParameter();
        cullinanIdParameter.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(CullinanId.class));
        cullinanIdParameter.setSimpleName("referenceId");
        proxyInstanceConstructor.addParameter(cullinanIdParameter);
        CtVariableReference parameterReference = cullinanIdParameter.getReference();

        CtBlock block = SpoonFactoryManager.getDefaultFactory().createBlock();

        CtVariableRead variableRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
        variableRead.setVariable(parameterReference);

        CtExecutableReference getValueExecutable = SpoonMethodManager.findMethod(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(CullinanId.class), "getValue").getReference();
        CtInvocation getValue = SpoonFactoryManager.getDefaultFactory().createInvocation(variableRead, getValueExecutable);

        // Set reference id
        CtField referenceId = serviceProxy.getField("referenceId");
        CtAssignment referenceAssignment = SpoonFactoryManager.getDefaultFactory().createVariableAssignment(referenceId.getReference(), false, getValue);
        block.addStatement(referenceAssignment);

        proxyInstanceConstructor.setBody(block);

        serviceProxy.addConstructor(proxyInstanceConstructor);
    }
}

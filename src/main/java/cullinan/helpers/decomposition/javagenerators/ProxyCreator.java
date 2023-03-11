package cullinan.helpers.decomposition.javagenerators;

import generatedfiles.SerializationUtil;
import spoon.refactoring.Refactoring;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.support.reflect.code.CtNewClassImpl;
import spoonhelpers.managers.SpoonFactoryManager;
import spoonhelpers.managers.SpoonMethodManager;
import util.CullinanId;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

// TODO Constants should simply be retrieved from the service instead of redefined

// TODO We don't need to add reference (shouldnt add) reference id if class cannot be instantiated. Purely static class does not need it.

// TODO We should add static blocks?

// TODO Implicit constructor... Should they also set reference id? Or just no function made at all? Could be used though...

// TODO Should not contain private function, or otherwise any implementation code such as static code blocks or variables right?
//TODO Big issue with inner classes: we are currently reusing the reference id for inner classes and generating new ones. Definitely incorrect.
// Proxy should not have a different interface from the original class. Still work to do there

// TODO Proxy can extend class... Do we want to call the super method? Maybe do we want to remove the superclass, probably cannot? Probably need to document that we have not resolved this yet
// We have seen:
//public ConnectionLogger(CullinanId referenceId) {
//        this.referenceId = referenceId.getValue();
//        }

// Which:
//public class ConnectionLogger extends BaseJdbcLogger
// But original:
//  private ConnectionLogger(Connection conn, Log statementLog, int queryStack) {
//    super(statementLog, queryStack);
//    this.connection = conn;
//  }
// Which we cannot call as this relies on state and is private and therefore not part of proxy for a good reason. Seems unlikely we want to add these functions.
// However, the superclass can have public/protected methods we can make use of, I believe?
// So maybe we still need to deal with parent (often abstract?) classes in some way
// Important that interface for proxy does not change.
// Since it can be any class that is also used and referenced otherwise, not sure how to deal with this. Not just abstract.
// Maybe composition, keeping the parent class internally...?
// Proxy file: (let's say service 1):
//public MapperBuilderAssistant(Configuration configuration, String resource) {
//        super(configuration); // Could theoretically be service 2
//        this.referenceId = client.newMapperBuilderAssistant(configuration, resource); // Could theoretically be service 3
//        }
// What should implementation of service 3 do, create superclass again? Probably not. Should proxy have knowledge?
// Super should be called in proxy, or extends should be removed. One or the other.
// Important is that super called should not be duplicated in both proxy and service... One or the other.
// Reference id for both should be set though? Different values? Think so.
// I think if possible I would like to not call super in the proxy and leave it to the implementation to resolve this? Not really possible

// Hm what about the following? Implying that the implementation created the superclass and is also responsible for its retrieval.
//      super(someconstructorthatdoesnothing); // Proxy side makes no service call to superclass
//      this.referenceId = client.newMapperBuilderAssistant(configuration, resource); // Service call to the class
//      super.setReferenceId(client.getSuperClass().getReferenceId()); // Make sure to set the now created parent (through service class) by retrieving the parent's id
// // Or: super.setReferenceId(client.getSuperClassReferenceId());
// That implies that we know this class is gonna be a proxy... Not good. Or maybe we can check if same service? Wait implementation also has setReferenceId... Kind of silly to re-set if in same service, but not wrong?

//  public BaseBuilder(UnsupportedOperationException e) {
//    // Does nothing on purpose. Used to make sure proxy does not create parent
//  }

// (Could theoretically be service 1 calling service 2, then service 2 calling service 1 for the parent, but not recursive)
// What does the above look like in the implementation?
//    super(configuration);
//    ErrorContext.instance().resource(resource);
//    this.resource = resource;

// TODO Abstract classes constructor should probably not have a service call allowed, or at least there should be no instantiation outside service
public class ProxyCreator {
    private final CtClass originalClass;
    private final CtInterface referenceInterface;
    private final CtClass client;
    private final CtClass referenceId;
    private final SerializationUtil serializationUtil;
    private CtClass serviceProxy;

    public ProxyCreator(CtClass originalClass, CtInterface referenceInterface, CtClass client, CtClass referenceId, SerializationUtil serializationUtil) { // TODO SerializationUtil should differ for monolith/location right...
        this.originalClass = originalClass;
        this.referenceInterface = referenceInterface;
        this.client = client;
        this.referenceId = referenceId;
        this.serializationUtil = serializationUtil;
    }

    // TODO OVerriddes remove?
    public CtClass build() {
        if (originalClass.isClass()) {
            serviceProxy = SpoonFactoryManager.getDefaultFactory().createClass(originalClass.getQualifiedName());
            serviceProxy.setModifiers(originalClass.getModifiers());
//            serviceProxy.addModifier(ModifierKind.PUBLIC);
            serviceProxy.setSuperclass(originalClass.getSuperclass());
            serviceProxy.setSuperInterfaces(originalClass.getSuperInterfaces());
            if (originalClass.isGenerics()) {
//                serviceProxy.setNestedTypes(originalClass.getNestedTypes());
//                serviceProxy.setTypeMembers(originalClass.getTypeMembers());
                serviceProxy.setFormalCtTypeParameters(originalClass.getFormalCtTypeParameters());
            }
        } else if (originalClass.isEnum()) {
            CtEnum ctEnum = SpoonFactoryManager.getDefaultFactory().createEnum(originalClass.getQualifiedName());
            ctEnum.addModifier(ModifierKind.PUBLIC);
            ctEnum.setSuperInterfaces(originalClass.getSuperInterfaces());
//            TODO Now add all the original variables here?

//             For the rest we can just act like the enum is a normal class (?).
            serviceProxy = ctEnum;
        } else {
            throw new IllegalStateException("Hmm, trying to create proxy for something that is not class or enum? Interesting: " + originalClass.getQualifiedName());
        }

        // Add reference interface
        serviceProxy.addSuperInterface(referenceInterface.getReference());

        addClientField();

//        Adding inner interfaces
//        TODO Should also add inner static classes?
//        List<CtTypeMember> typeMembers = originalClass.getTypeMembers();
//        for (CtTypeMember typeMember : typeMembers) {
//            if (typeMember.getDeclaringType().isInterface()) {
//                System.out.println("FOUND AN INTERFACE");
//                serviceProxy.addTypeMember(typeMember);
//            }
//        }

//        Set<CtType> nestedTypes = originalClass.getNestedTypes();
//        for (CtType nestedType : nestedTypes) {
//            // TODO Should be generated by proxy creator anyway, just add the interfaces if they exist? Probably can be required
//            if (nestedType.isInterface()) {
//                System.out.println("FOUND AN INTERFACE NESTED");
//                serviceProxy.addTypeMember(nestedType);
//            }
//
//        }


        // TODO For every method in client or original?
//        client.getMethods()
//        Set<CtType> nestedTypes = originalClass.getNestedTypes();
//        for (CtType nestedType : nestedTypes) {
        // TODO Should be generated by proxy creator anyway, just add the interfaces if they exist? Probably can be required
//            if (nestedType.isInterface()) {
//                System.out.println("FOUND AN INTERFACE NESTED");
//                serviceProxy.addTypeMember(nestedType);
//            }

//        ServiceInterfaceCreator serviceInterfaceCreator = new ServiceInterfaceCreator(originalClass);
//        CtInterface ctInterface = serviceInterfaceCreator.buildInterface();


        // TODO Add innerclasses themselves, not the methods to global. That is just the client and service part imo

        Set<CtType> nestedTypes = originalClass.getNestedTypes();
        for (CtType nestedType : nestedTypes) {
            if (nestedType.isInterface()) {
                System.out.println("FOUND AN INTERFACE NESTED, ADDING");
                serviceProxy.addTypeMember(nestedType);
            }


            if (nestedType.isClass() || nestedType.isEnum()) { // TODO Any other things we need to move?
                // TODO I think these remove the inner class interface methods for some reason? Hmm we may need to pass the interface to the ProxyCreator?

//                ServiceInterfaceCreator serviceInterfaceCreator = new ServiceInterfaceCreator((CtClass) nestedType);
//                CtInterface ctInterface = serviceInterfaceCreator.buildInterface();


//                ClientCreator clientCreator = new ClientCreator((CtClass) nestedType, ctInterface, serializationUtil.getJava());
//                CtClass client = clientCreator.build();

                //        Not possible to cast to private class. We do not allow private classes to be used outside service.
                //        PropertyParser.VariableTokenHandler objectReferenceObject = ((PropertyParser.VariableTokenHandler) (SerializationUtil.decode(objectReference)));
//                if (!nestedType.isPrivate()) {
                ProxyCreator proxyCreator = new ProxyCreator((CtClass) nestedType, referenceInterface, client, referenceId, serializationUtil);
                CtClass proxy = proxyCreator.build();
                // The following is more correct but auotimports fail all over the place...
//                proxy.setModifiers(nestedType.getModifiers());

                serviceProxy.addNestedType(proxy);
//                }


//                TODO More inner class layers
//                Set<CtMethod> allMethods = client.getAllMethods();
//                Set<CtMethod> allMethods = proxy.getMethods();
//
//                for (CtMethod method : allMethods) { // TODO GetMethod or allmethods?
//                    String newMethodName = nestedType.getSimpleName().substring(0, 1).toLowerCase() + nestedType.getSimpleName().substring(1) + "InnerClass" + method.getSimpleName().substring(0,1).toUpperCase() + method.getSimpleName().substring(1);
//                    method.setSimpleName(newMethodName);
//                    serviceProxy.addMethod(method);
//                }
            }
        }

//            TODO Check why required again... Probably a dependency. Very annoying as decomposing this as well is difficult. Maybe good to first put in own file if possible
//            if (nestedType.isClass()) {
////                System.out.println("FOUND A CLASS NESTED, adding"); We need to make sure this is also decomposed
//                ServiceInterfaceCreator serviceInterfaceCreator = new ServiceInterfaceCreator((CtClass) nestedType);
//                CtInterface ctInterface = serviceInterfaceCreator.buildInterface();
//                ClientCreator clientCreator = new ClientCreator((CtClass) nestedType, ctInterface, serializationUtil.getJava());
//                CtClass client = clientCreator.build();
//                ProxyCreator proxyCreator = new ProxyCreator((CtClass) nestedType, referenceInterface, client, referenceId, serializationUtil);
//                CtClass proxy = proxyCreator.build();
//                serviceProxy.addTypeMember(proxy);
//            }
//        }


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

            addSuperCall(constructor, body);

            callClientAndStoreReference(referenceId, constructor, body, clientCallArguments);
            proxyConstructor.setBody(body);

            serviceProxy.addConstructor(proxyConstructor);
        }

        // Add original calls and map to client calls using reference id
        Set<CtMethod> methods = originalClass.getMethods();
        for (CtMethod method : methods) { // TODO Only relevant methods, should be one definition for interface + here + everywhere (public methods, package?)
            if (!SpoonMethodManager.usedOutsideService(method)) {
                continue;
            }

//             TODO We should deal with static methods
//            if (!SpoonMethodManager.isNonStatic(method)) {
//                continue;
//            }

            CtMethod proxyCall = method.clone();

            // Abstract methods cannot have a body and can therefore not be used (at least directly, and therefore don't need to be decomposed... Right? Unless casted to the parent class?)
            if (method.isAbstract()) {
//                serviceProxy.addMethod(proxyCall);
                continue;
            }

            System.out.println(method.getSimpleName());
            if (method.hasAnnotation(Override.class)) {
                System.out.println("OVERRIDE");
//                CtAnnotation<Annotation> annotation = SpoonFactoryManager.getDefaultFactory().createAnnotation(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Override.class));
                CtAnnotation annotation = method.getAnnotation(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Override.class));
                proxyCall.removeAnnotation(annotation);
//                proxyCall.setAnnotations(Collections.EMPTY_LIST);
            }

            CtBlock body = SpoonFactoryManager.getDefaultFactory().createBlock();
            List<CtVariableAccess> clientCallArguments = new ArrayList<>();

            // Static methods do not have an object to call, otherwise we need an object with reference id
            if (!method.isStatic()) {
                clientCallArguments.add(referenceIdArgument);
            }

            // For every param, if needed encode, store in var and add to list of client call arguments

            List<CtParameter> parameters = method.getParameters();
            handleParameters(body, clientCallArguments, parameters);

            // Find client call
            List<CtParameter> parameterList = method.getParameters();
//            List<CtParameterReference> collect = parameterList.stream().map(CtParameter::getReference).collect(Collectors.toList());
            List<CtTypeReference> clientMethodArguments = new ArrayList<>();
            if (!method.isStatic()) { // Static methods do not have an object to call on
                clientMethodArguments.add(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class)); // TODO Not for constructors...?
            }
            clientMethodArguments.addAll(
                    parameterList.stream().map(ref -> {
                                return ClientCreator.objectify(ref);


//                    parameterList.stream().map(CtParameter::getReference).map(ref ->
//                    {
//                        return ClientCreator.removeGenerics(ref.getType());
//                        if (!ref.getType().isPrimitive() && !ref.getType().equals(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class))) {
//                            return SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class); // TODO No need to map if it is already string...?
//                        } else {
//                            return ref.getType();
//                        }
                            }).map(CtTypedElement::getType)
                            .collect(Collectors.toList())
            );


//            TODO We should probably skip abstract classes? We don't need API forthis right?...
//            This does not fully get rid of generics in Java, as we can
//            TODO Should be described in thesis.

//            TODO Terrible solution?
//            TODO Still requires manual work, and we probably need to do this in multiple locations

            //            TODO Just catch all types with 1 letter...? Terrible of course...
//            if (method.getType().getSimpleName().length() == 1 && !method.getType().getSimpleName().equalsIgnoreCase("?")) {
//                System.out.println("Didn't add method because we think the type might be a generic. We are not sure how to deal with this yet");
//                continue;
//            }
//
//
////            TODO Bad check for generics in parameters...
//            boolean genericFound = false;
//            for (CtTypeReference parameter : collect) {
//                if (parameter.getSimpleName().length() == 1) {
//                    genericFound = true;
//                }
//            }
//            if (genericFound) {
//                System.out.println("Didn't add method because we think the type might be a generic. We are not sure how to deal with this yet");
//                continue;
//            }

//            List<CtParameter> parameters2 = method.getParameters();
//            CtTypeReference[] references = parameters2.stream().map(CtParameter::getReference).map(CtVariableReference::getType).collect(Collectors.toList()).toArray(new CtTypeReference[0]);

//            CtExecutableReference clientCall = client.getReference().getTypeDeclaration().getMethod(method.getSimpleName(), collect.toArray(new CtTypeReference[0])).getReference();
            // We made our own algorithm to find the method for a class as the original does not seem to work well with generics(?)

            String functionName = method.getSimpleName();
            functionName = findFullMethodName(originalClass, functionName);


            List<CtMethod> methodsByName = client.getReference().getTypeDeclaration().getMethodsByName(functionName);
//            List<CtMethod> methodsByName = client.getReference().getTypeDeclaration().getMethodsByName(method.getSimpleName());
            CtMethod foundClientMethod = methodsByName.stream()
                    .filter(m -> {
                        List<CtParameter> parameters2 = m.getParameters();
                        List<CtTypeReference> params = parameters2.stream().map(CtParameter::getReference).map(CtVariableReference::getType).collect(Collectors.toList());
                        return params.equals(clientMethodArguments);
                    })
                    .findFirst().orElseThrow(IllegalStateException::new);

            CtExecutableReference clientCall = foundClientMethod.getReference();
//            CtExecutableReference clientCall = client.getReference().getTypeDeclaration().getMethod(method.getSimpleName(), collect.toArray(new CtTypeReference[0])).getReference();
//            CtExecutableReference clientCall = client.getReference().getTypeDeclaration().getMethod("new" + originalClass.getSimpleName(), collect.toArray(new CtTypeReference[0])).getReference();
//            CtExecutableReference clientCall = SpoonMethodManager.findMethod(client.getReference(), method.getSimpleName()).getReference();// TODO Multiple methods with different params should be taken into account
//            CtVariableRead referenceRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
//            referenceRead.setVariable(referenceId.getReference());
            CtInvocation clientInvocation = SpoonFactoryManager.getDefaultFactory().createInvocation(); // I feel like it should be easier to just use the arguments for createInvocation, but usage is a bit different then manually setting everything.
//            CtTypeAccess target = SpoonFactoryManager.getDefaultFactory().createTypeAccess(client.getReference());
//            clientInvocation.setTarget(target);
            CtField clientField = serviceProxy.getField("client");
            CtVariableRead fieldRead = SpoonFactoryManager.getDefaultFactory().createFieldRead();
            fieldRead.setVariable(clientField.getReference());
            clientInvocation.setTarget(fieldRead);
            clientInvocation.setExecutable(clientCall);
            clientInvocation.setArguments(clientCallArguments);

            if (method.getType().equals(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(void.class))) {
                body.addStatement(clientInvocation);
            } else {
                CtReturn returnClientCall = SpoonFactoryManager.getDefaultFactory().createReturn();
                if (!method.getType().equals(clientInvocation.getType())) {
                    clientInvocation.addTypeCast(method.getType());
                }
                returnClientCall.setReturnedExpression(clientInvocation);
                body.addStatement(returnClientCall);
            }
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


//        TODO Why did we think this was a good idea?
//        for (CtMethod method : methods) {
//            if (method.isStatic()) {
//                serviceProxy.addMethod(method);
//            }
//        }

//        TODO Don't think we want this. Implementation only in service? If we do want this, please add commnt why
//        serviceProxy.setAnonymousExecutables(originalClass.getAnonymousExecutables());

        addProxyInstanceCreatorFromReferenceId();
        addVariables();

        return serviceProxy;
    }

    private void addSuperCall(CtConstructor constructor, CtBlock body) {
        if (originalClass.getSuperclass() == null) {
            return;
        }

        CtBlock constructorBody = constructor.getBody();
        if (constructorBody == null) {
            return;
        }

        List<CtStatement> statements = constructorBody.getStatements();
        if (statements.size() == 0) {
            return;
        }
        CtStatement statement = statements.get(0);
        CtStatement superCall = statement.clone();
        body.addStatement(superCall);
    }

    private void callClientAndStoreReference(CtField referenceId, CtConstructor constructor, CtBlock body, List<CtVariableAccess> clientCallArguments) {
        List<CtParameter> parameterList = constructor.getParameters();

        List<CtTypeReference> clientMethodArguments = new ArrayList<>();
        clientMethodArguments.addAll(
                parameterList.stream().map(ref -> {
                            return ClientCreator.objectify(ref);
//                    parameterList.stream().map(CtParameter::getReference).map(ref ->
//                    {
//                        return ClientCreator.removeGenerics(ref.getType());
//                        if (!ref.getType().isPrimitive() && !ref.getType().equals(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class))) {
//                            return SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class); // TODO No need to map if it is already string...?
//                        } else {
//                            return ref.getType();
//                        }
                        }).map(CtTypedElement::getType)
                        .collect(Collectors.toList())
        );


        String functionName = "new" + originalClass.getSimpleName();
        functionName = findFullMethodName(originalClass, functionName);


        CtExecutableReference clientCall = client.getReference().getTypeDeclaration().getMethod(functionName, clientMethodArguments.toArray(new CtTypeReference[0])).getReference();
//            CtExecutableReference clientCall = client.getReference().getTypeDeclaration().getMethod("new" + originalClass.getSimpleName(), collect.toArray(new CtTypeReference[0])).getReference();
//            CtExecutableReference clientCall = SpoonMethodManager.findMethod(client.getReference(), "new" + originalClass.getSimpleName()).getReference();// TODO Multiple methods with different params should be taken into account
//            CtVariableRead referenceRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
//            referenceRead.setVariable(referenceId.getReference());
        CtInvocation clientInvocation = SpoonFactoryManager.getDefaultFactory().createInvocation(); // I feel like it should be easier to just use the arguments for createInvocation, but usage is a bit different then manually setting everything.
//            CtTypeAccess target = SpoonFactoryManager.getDefaultFactory().createTypeAccess(client.getReference()); //

        CtField clientField = serviceProxy.getField("client");
        CtVariableRead fieldRead = SpoonFactoryManager.getDefaultFactory().createFieldRead();
        fieldRead.setVariable(clientField.getReference());
        clientInvocation.setTarget(fieldRead);
        clientInvocation.setExecutable(clientCall);
        clientInvocation.setArguments(clientCallArguments);

//            CtLocalVariable newId = SpoonFactoryManager.getDefaultFactory().createLocalVariable(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class), "id", clientInvocation);
        CtAssignment assignId = SpoonFactoryManager.getDefaultFactory().createVariableAssignment(referenceId.getReference(), false, clientInvocation);


//            CtReturn returnClientCall = SpoonFactoryManager.getDefaultFactory().createReturn();
//            returnClientCall.setReturnedExpression(clientInvocation);


        body.addStatement(assignId);
    }

    public static String findFullMethodName(CtClass originalClass, String functionName) {
        System.out.println(originalClass.getSimpleName());
        if (!originalClass.isTopLevel()) { // isInnerClass
            functionName = originalClass.getSimpleName().substring(0, 1).toLowerCase() + originalClass.getSimpleName().substring(1) + "InnerClass" + functionName.substring(0, 1).toUpperCase() + functionName.substring(1);
            if (originalClass.getParent().getClass().equals(CtNewClassImpl.class)) { // Anonymous class inlined creation thing
                return functionName;
            }
//            System.out.println(functionName);
            CtClass parent = (CtClass) originalClass.getParent();
            if (!parent.isTopLevel()) { // isInnerClass
                functionName = parent.getSimpleName().substring(0, 1).toLowerCase() + parent.getSimpleName().substring(1) + "InnerClass" + functionName.substring(0, 1).toUpperCase() + functionName.substring(1);
                if (originalClass.getParent().getClass().equals(CtNewClassImpl.class)) { // Anonymous class inlined creation thing
                    return functionName;
                }
//                System.out.println(functionName);
                parent = (CtClass) parent.getParent();
                if (!parent.isTopLevel()) { // isInnerClass
                    functionName = parent.getSimpleName().substring(0, 1).toLowerCase() + parent.getSimpleName().substring(1) + "InnerClass" + functionName.substring(0, 1).toUpperCase() + functionName.substring(1);
                    if (originalClass.getParent().getClass().equals(CtNewClassImpl.class)) { // Anonymous class inlined creation thing
                        return functionName;
                    }
//                    System.out.println(functionName);
                }
            }
        }
        return functionName;
    }

    private void addClientField() {
        CtField clientField = SpoonFactoryManager.getDefaultFactory().createField();
        clientField.setType(client.getReference());
        clientField.setSimpleName("client");
        clientField.addModifier(ModifierKind.PRIVATE);
        clientField.addModifier(ModifierKind.STATIC);

        CtConstructor clientConstructor = client.getConstructor();
        CtExecutableReference constructorReference = clientConstructor.getReference();
        CtConstructorCall newClient = SpoonFactoryManager.getDefaultFactory().createConstructorCall();
        newClient.setExecutable(constructorReference);

        clientField.setAssignment(newClient);

        serviceProxy.addField(clientField);
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
        if (serviceProxy.isClass()) {
            proxyInstanceConstructor.addModifier(ModifierKind.PUBLIC);
        } // Enum constructor is private by default and should not be public TODO Wait how do we deal with SerializationUtil making use of this constructor... Not possible? Do we need a separate public static method? I guess there should never be a call to it, an enum is never going over a service right? Should be fine then.
        proxyInstanceConstructor.setType(serviceProxy.getReference());
        proxyInstanceConstructor.setSimpleName(serviceProxy.getSimpleName());

        CtParameter cullinanIdParameter = SpoonFactoryManager.getDefaultFactory().createParameter();
        cullinanIdParameter.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(CullinanId.class));
        cullinanIdParameter.setSimpleName("referenceId");
        proxyInstanceConstructor.addParameter(cullinanIdParameter);
        CtVariableReference parameterReference = cullinanIdParameter.getReference();

        CtBlock block = SpoonFactoryManager.getDefaultFactory().createBlock();

//        We leave this to user for now... Too complicated to implement
//        addSuperReference(block);

        assignReferenceId(parameterReference, block);

        proxyInstanceConstructor.setBody(block);

        serviceProxy.addConstructor(proxyInstanceConstructor);
    }

    private void addSuperReference(CtBlock block) {
        if (originalClass.getSuperclass() == null) {
            return;
        }
        CtTypeReference superclass = originalClass.getSuperclass();
        System.out.println();
        System.out.println(originalClass.getSimpleName());
        System.out.println(superclass);

        CtClass<Object> superClassInCodeBase = SpoonFactoryManager.getDefaultFactory().Class().get(superclass.getQualifiedName());
        if (superClassInCodeBase == null) { // Class is outside of code base...How do we deal with method calls in parents outside code base? Do we need to copy that data...? Not good...
            return;
        }
        CtClass superClassType = (CtClass) superclass.getTypeDeclaration();
        CtClass tempSuperClass = superClassType.clone();


        CtConstructor constructor = SpoonFactoryManager.getDefaultFactory().createConstructor();
        CtParameter cullinanId = SpoonFactoryManager.getDefaultFactory().createParameter();
        cullinanId.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(CullinanId.class));
        constructor.addParameter(cullinanId);
        tempSuperClass.addConstructor(constructor);


//        CtConstructor constructor = superClassType.getConstructor(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(CullinanId.class));
        CtExecutableReference constructorReference = constructor.getReference();

        CtInvocation superClassConstructorCall = SpoonFactoryManager.getDefaultFactory().createInvocation();
        superClassConstructorCall.setExecutable(constructorReference);
        CtField referenceIdField = serviceProxy.getField("referenceId");
        CtVariableRead variableRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
        variableRead.setVariable(referenceIdField.getReference());
        superClassConstructorCall.addArgument(variableRead);

        block.addStatement(superClassConstructorCall);
    }

    private void assignReferenceId(CtVariableReference parameterReference, CtBlock block) {
        CtVariableRead variableRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
        variableRead.setVariable(parameterReference);

        CtExecutableReference getValueExecutable = SpoonMethodManager.findMethod(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(CullinanId.class), "getValue").getReference();
        CtInvocation getValue = SpoonFactoryManager.getDefaultFactory().createInvocation(variableRead, getValueExecutable);

        // Set reference id
        CtField referenceId = serviceProxy.getField("referenceId");
        CtAssignment referenceAssignment = SpoonFactoryManager.getDefaultFactory().createVariableAssignment(referenceId.getReference(), false, getValue);
        block.addStatement(referenceAssignment);
    }

    // TODO Now just set to the right value and we are done!
    private void addVariables() {
        addEnumValues();
//        addFieldVariables();
    }

    private void addEnumValues() {
        if (originalClass.isEnum()) {
            CtEnum serviceProxyAsEnum = (CtEnum) serviceProxy;

            CtEnum originalEnum = (CtEnum) originalClass;
            List<CtEnumValue> enumValues = originalEnum.getEnumValues();
            serviceProxyAsEnum.setEnumValues(Collections.emptyList()); // TODO Should not be necessary I hope...
            for (CtEnumValue enumValue : enumValues) {
                String enumId = generateId(enumValue); // We can generate this id both in the service and proxy and this should be a unique value. Implicit coupling, generation could be done in common place.

                CtEnumValue newEnumValue = SpoonFactoryManager.getDefaultFactory().createEnumValue();
                newEnumValue.setSimpleName(enumValue.getSimpleName());

                // TODO Only the argument is used, executable doesnt matter? Doesnt make much sense to me, but alright it seems to be working now
                CtConstructor referenceIdConstructor = referenceId.getConstructor(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
                CtExecutableReference constructorReference = referenceIdConstructor.getReference();
                CtConstructorCall newCullinanId = SpoonFactoryManager.getDefaultFactory().createConstructorCall();
                newCullinanId.setExecutable(constructorReference);
                newCullinanId.addArgument(SpoonFactoryManager.getDefaultFactory().createLiteral(enumId));

                CtConstructorCall newEnumConstructorCall = SpoonFactoryManager.getDefaultFactory().createConstructorCall();
                newEnumConstructorCall.addArgument(newCullinanId);

                newEnumValue.setAssignment(newEnumConstructorCall);
                serviceProxyAsEnum.addEnumValue(newEnumValue); // God this code is ugly
                // TODO DO I have to assign to serviceProxy again? Makes no sense?
            }
        } else { // TODO Not really right... Enums can also have field variables... EnumValue check does not really exist?
            addFieldVariables();
        }
    }

    private void addFieldVariables() {
//        Set<CtMethod> interfaceMethods = client.getMethods();
//        List<CtMethod> variableRetrievalMethods = interfaceMethods.stream()
//                .filter(interfaceMethod -> interfaceMethod.getSimpleName().contains("retrieveVariable"))
//                .collect(Collectors.toList());
//
//        for (CtMethod variableRetrievalMethod : variableRetrievalMethods) {
//            addClientCall();
//            CtMethod proxyMethod = createVariableRetrievalMethod(variableRetrievalMethod);
//            serviceProxy.addMethod(proxyMethod);
//        }

        List<CtField> fields = originalClass.getFields();
        for (CtField field : fields) {
            CtField proxyField = field.clone();
            if (ServiceInterfaceCreator.shouldBeDecomposedField(field)) {
                addClientCall(proxyField);
                serviceProxy.addField(proxyField);
            }
        }
    }

//    private CtMethod createVariableRetrievalMethod(CtMethod variableRetrievalMethod) {
//        // Add client call to service
//        List<CtMethod> methodsByName = ctInterface.getReference().getTypeDeclaration().getMethodsByName(variableRetrievalMethod.getSimpleName());
//        CtMethod interfaceMethod = methodsByName.get(0);
//        CtMethod clientMethod = interfaceMethod.clone();
//
//        CtExecutableReference interfaceMethodExecutable = interfaceMethod.getReference();
//        CtBlock<Object> body = SpoonFactoryManager.getDefaultFactory().createBlock();
//
//        CtVariableRead serviceVariable = SpoonFactoryManager.getDefaultFactory().createVariableRead();
//        CtFieldReference service = result.getField("service").getReference();
//        serviceVariable.setVariable(service);
//        CtInvocation serviceCall = SpoonFactoryManager.getDefaultFactory().createInvocation(serviceVariable, interfaceMethodExecutable);
//
//        // Add argument if available in interface to client call, otherwise not
//        List<CtParameter> parameters = clientMethod.getParameters();
//        if (!parameters.isEmpty()) {
//            CtParameter objectReferenceId = parameters.get(0);
//            CtVariableRead argument = SpoonFactoryManager.getDefaultFactory().createVariableRead();
//            argument.setVariable(objectReferenceId.getReference());
//            serviceCall.addArgument(argument);
//        }
//
//        CtReturn ctReturn = SpoonFactoryManager.getDefaultFactory().createReturn();
//        ctReturn.setReturnedExpression(serviceCall);
//
//        body.addStatement(ctReturn);
//
//        CtTry tryBlock = wrapTryCatchRemoteException(body);
//
//        clientMethod.setBody(tryBlock);
//
//        return clientMethod;
//    }

    // If static target is class, otherwise add field reference id and refer to object.
    private void addClientCall(CtField proxyField) {
        CtInvocation retrieveVariable = SpoonFactoryManager.getDefaultFactory().createInvocation();

        CtField clientField = serviceProxy.getField("client");
        CtVariableRead fieldRead = SpoonFactoryManager.getDefaultFactory().createFieldRead();
        fieldRead.setVariable(clientField.getReference());

        String fullVariableName = ProxyCreator.findFullMethodName(originalClass, proxyField.getSimpleName());
        fullVariableName = fullVariableName.substring(0, 1).toUpperCase() + fullVariableName.substring(1);
        String retrieveVarMethodName = "retrieveVariable" + fullVariableName;

        List<CtMethod> clientMethods = client.getReference().getTypeDeclaration().getMethodsByName(retrieveVarMethodName);
        CtMethod retrieveVarMethod = clientMethods.get(0);

        if (!proxyField.isStatic()) {
            CtField referenceIdField = serviceProxy.getField("referenceId");
            CtVariableRead referenceId = SpoonFactoryManager.getDefaultFactory().createVariableRead();
            referenceId.setVariable(referenceIdField.getReference());
            retrieveVariable.addArgument(referenceId);

        }
        retrieveVariable.setTarget(fieldRead);
        retrieveVariable.setExecutable(retrieveVarMethod.getReference());

        proxyField.setAssignment(retrieveVariable.addTypeCast(proxyField.getType()));
    }

    private String generateId(CtEnumValue enumValue) {
        return enumValue.getType().getQualifiedName() + "::" + enumValue.getSimpleName();
    }
}

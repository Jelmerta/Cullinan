package cullinan.helpers.decomposition.javagenerators;

import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoonhelpers.managers.LiteralManager;
import spoonhelpers.managers.SpoonFactoryManager;
import spoonhelpers.managers.SpoonMethodCallManager;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collection;
import java.util.Set;

public class ServiceMainCreator {
    private final Collection<CtClass> implementations;

    public ServiceMainCreator(Collection<CtClass> implementations) {
        this.implementations = implementations;
    }

    public CtClass build() {
        return createMain(implementations);
    }

    public static CtClass createMain(Collection<CtClass> services) {
        CtClass serviceApplication = SpoonFactoryManager.getDefaultFactory().createClass("Application");
        serviceApplication.addModifier(ModifierKind.PUBLIC);

        CtLiteral literal = SpoonFactoryManager.getDefaultFactory().createLiteral();
        literal.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(int.class));
        literal.setValue(1099);

        SpoonFactoryManager.getDefaultFactory().createField(serviceApplication, Set.of(ModifierKind.PRIVATE, ModifierKind.STATIC, ModifierKind.FINAL), SpoonFactoryManager.getDefaultFactory().createCtTypeReference(int.class), "registryPort", literal);

        CtMethod mainApplication = SpoonFactoryManager.getDefaultFactory().createMethod();
        mainApplication.addModifier(ModifierKind.PUBLIC);
        mainApplication.addModifier(ModifierKind.STATIC);
        mainApplication.setSimpleName("main");

        CtArrayTypeReference stringArrayType = SpoonFactoryManager.getDefaultFactory().createArrayReference(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
        CtParameter stringArgs = SpoonFactoryManager.getDefaultFactory().createParameter();
        stringArgs.setType(stringArrayType);
        stringArgs.setSimpleName("args");

        mainApplication.addParameter(stringArgs);

        CtTypeReference type = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(void.class);
        mainApplication.setType(type);


        CtBlock mainCodeBlock = SpoonFactoryManager.getDefaultFactory().createBlock();
//        mainCodeBlock.addStatement(createSecurityPolicyCall());
//        mainCodeBlock.addStatement(createAddSecurityManager());
        // TODO Still not entirely sure if required.
        //            if (System.getSecurityManager() == null) {
//                RMISecurityManager securityManager = new RMISecurityManager();
//                System.setSecurityManager(securityManager);
//            }

        CtTry tryStatement = SpoonFactoryManager.getDefaultFactory().createTry();

        CtBlock tryBlock = SpoonFactoryManager.getDefaultFactory().createBlock();


        CtLocalVariable registryVariable = createRegistryVariable(serviceApplication, SpoonFactoryManager.getDefaultFactory());
        tryBlock.addStatement(registryVariable);

        for (CtClass classImplementation : services) {

            CtNewClass newServiceCall = SpoonFactoryManager.getDefaultFactory().createNewClass();
            newServiceCall.setType(classImplementation.getReference());

            // Uncapitalize variable name
            String serviceVariableName = Character.toLowerCase(classImplementation.getSimpleName().charAt(0)) + classImplementation.getSimpleName().substring(1);
            CtLocalVariable serviceVariable = SpoonFactoryManager.getDefaultFactory().createLocalVariable(classImplementation.getReference(), serviceVariableName, newServiceCall);

            tryBlock.addStatement(serviceVariable);

            CtVariableAccess registryRead = SpoonFactoryManager.getDefaultFactory().createVariableRead(registryVariable.getReference(), false);
            CtTypeReference registryReference = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Registry.class);

            CtMethod rebindMethod = registryReference.getTypeDeclaration().getMethod("rebind", SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class), SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Remote.class));

            String serviceClassUrlString = "//localhost/" + classImplementation.getSimpleName().replace("ServiceImplementation", ""); // TODO Name not great

            CtLiteral serviceClassUrl = SpoonFactoryManager.getDefaultFactory().createLiteral();
            serviceClassUrl.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
            serviceClassUrl.setValue(serviceClassUrlString);

            CtVariableAccess serviceVariableRead = SpoonFactoryManager.getDefaultFactory().createVariableRead(serviceVariable.getReference(), false);
            CtInvocation registryRebind = SpoonFactoryManager.getDefaultFactory().createInvocation(registryRead, rebindMethod.getReference(), serviceClassUrl, serviceVariableRead);

            tryBlock.addStatement(registryRebind);
        }
        tryStatement.setBody(tryBlock);

        CtCatch ctCatch = SpoonFactoryManager.getDefaultFactory().createCatch();
        CtCatchVariable ctCatchVariable = SpoonFactoryManager.getDefaultFactory().createCatchVariable();
        CtTypeReference exceptionType = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Exception.class);
        ctCatchVariable.setType(exceptionType);
        ctCatchVariable.setSimpleName("e");
        ctCatch.setParameter(ctCatchVariable);
        ctCatch.setBody(SpoonFactoryManager.getDefaultFactory().createBlock());
        tryStatement.addCatcher(ctCatch);

        mainCodeBlock.addStatement(tryStatement);

        mainApplication.setBody(mainCodeBlock);
        serviceApplication.addMethod(mainApplication);

        return serviceApplication;
    }

    private static CtLocalVariable createRegistryVariable(CtClass mainClass, Factory factory) {
        CtTypeReference registryType = factory.createCtTypeReference(Registry.class);

        CtInvocation newRegistryCall = factory.createInvocation();
        CtTypeReference locateRegistryTypeReference = factory.createCtTypeReference(LocateRegistry.class);
        Set<CtMethod> methods = locateRegistryTypeReference.getTypeDeclaration().getMethods();

        CtExecutableReference newRegistryMethod = null;
        for (CtMethod method : methods) {
            if (method.getSimpleName().equals("createRegistry")) {
                newRegistryMethod = method.getReference();
            }
        }
        if (newRegistryMethod == null) {
            throw new IllegalStateException("function should exist");
        }
        newRegistryCall.setExecutable(newRegistryMethod);
        CtField registryPort = mainClass.getField("registryPort");

        CtVariableAccess portVarRead = factory.createVariableRead(registryPort.getReference(), true);
        newRegistryCall.addArgument(portVarRead);

        CtTypeAccess locateRegistryClass = factory.createTypeAccess(factory.createCtTypeReference(LocateRegistry.class));
        newRegistryCall.setTarget(locateRegistryClass);

        CtLocalVariable registryVariable = factory.createLocalVariable(registryType, "registry", newRegistryCall);

        return registryVariable;
    }




    // Don't know if I prefer this to return the call or just add the security policy using state/field variables
    private static CtInvocation createSecurityPolicyCall() {
        CtLiteral securityPolicyVariable = LiteralManager.createLiteral(String.class, "java.security.policy");
        CtLiteral securityPolicyLocation = LiteralManager.createLiteral(String.class, "file:///home/jelmer/Documents/Software Engineering/Master Project/projects/dddsample-micro/client/src/main/resources/security.policy");

        CtInvocation securityPolicyCall = SpoonMethodCallManager.createClassCall(System.class, "setProperty", securityPolicyVariable, securityPolicyLocation);
        return securityPolicyCall;
    }
}

package cullinan.helpers.decomposition.generators.model;

import spoon.reflect.declaration.CtClass;

// TODO Maybe we should just put the original class? Many nullable fields...
public class GeneratedClassLevel {
    private final String originalFullyQualifiedClassname;
    private final CtClass serviceOriginalClass; // Basically the original class: but with a reference id added to refer to it in other services using their proxies. When constructed, added to an in-mem database for now.private java.util.Map<spoon.reflect.declaration.CtClass,spoon.reflect.declaration.CtInterface> interfaces;private java.util.Map<spoon.reflect.declaration.CtClass,spoon.reflect.declaration.CtClass> services;private java.util.Map<spoon.reflect.declaration.CtClass,spoon.reflect.declaration.CtClass> clients;private final java.util.Map<spoon.reflect.declaration.CtClass,spoon.reflect.declaration.CtClass> proxies; // Proxies are a little special: they need to be generated before all services are written as all services can make use of the proxy. Not used in the resulting service.
    private final GeneratedClientService generatedClientService;
    private final CtClass proxy;

    public GeneratedClassLevel(String originalFullyQualifiedClassname, CtClass serviceOriginalClass, GeneratedClientService generatedClientService, CtClass proxy) {
        this.originalFullyQualifiedClassname = originalFullyQualifiedClassname;
        this.serviceOriginalClass = serviceOriginalClass;
        this.generatedClientService = generatedClientService;
        this.proxy = proxy;
    }

    public String getOriginalFullyQualifiedClassname() {
        return originalFullyQualifiedClassname;
    }

    public GeneratedClientService getGeneratedClientService() {
        return generatedClientService;
    }

    public CtClass getProxy() {
        return proxy;
    }

    public CtClass getServiceOriginalClass() {
        return serviceOriginalClass;
    }
}
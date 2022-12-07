package cullinan.helpers.decomposition.generators.model;

import spoon.reflect.declaration.CtClass;

// TODO Generation is dependent on class level...
// Service level (different for every service)
public class GeneratedServiceLevel {
    private String serviceName;
    private CtClass serviceMain;
    private CtClass classDefinitions; // Only here because of the proxy/service class definitions. Most of serializationUtil is the same for each service.
    // TODO I see that main application's serializationUtil is wrong. How is it still working? Hope it is using proxies...

    public GeneratedServiceLevel(String serviceName) {
        this.serviceName = serviceName;
    }

    public void addServiceMain(CtClass serviceMain) {
        this.serviceMain = serviceMain;
    }

    public void addClassDefinitions(CtClass classDefinitions) {
        this.classDefinitions = classDefinitions;
    }

    public String getServiceName() {
        return serviceName;
    }

    public CtClass getClassDefinitions() {
        return classDefinitions;
    }

    public CtClass getMain() {
        return serviceMain;
    }
}
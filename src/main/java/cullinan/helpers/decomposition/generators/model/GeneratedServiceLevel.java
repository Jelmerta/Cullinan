package cullinan.helpers.decomposition.generators.model;

import generatedfiles.ClassDefinitions;
import generatedfiles.MicroserviceMain;
import generatedfiles.MicroservicePom;
import generatedfiles.Writable;

import java.util.List;

// TODO Generation is dependent on class level...
// Service level (different for every service)
public class GeneratedServiceLevel {
    private String serviceName;
    private MicroserviceMain microserviceMain;
    private MicroservicePom microservicePom;
    private ClassDefinitions classDefinitions; // Only here because of the proxy/service class definitions. Most of serializationUtil is the same for each service.
    // TODO I see that main application's serializationUtil is wrong. How is it still working? Hope it is using proxies...

    public GeneratedServiceLevel(String serviceName) {
        this.serviceName = serviceName;
    }

    public void addServiceMain(MicroserviceMain microserviceMain) {
        this.microserviceMain = microserviceMain;
    }
    public void addServicePom(MicroservicePom microservicePom) {
        this.microservicePom = microservicePom;
    }

    public void addClassDefinitions(ClassDefinitions classDefinitions) {
        this.classDefinitions = classDefinitions;
    }

    public String getServiceName() {
        return serviceName;
    }

    public ClassDefinitions getClassDefinitions() {
        return classDefinitions;
    }

    public MicroserviceMain getMain() {
        return microserviceMain;
    }
    public MicroservicePom getMicroservicePom() {
        return microservicePom;
    }

    public List<Writable> getAllWritables() {
        return List.of(classDefinitions, microserviceMain, microservicePom);
//        ArrayList<Writable2> writables = new ArrayList<>();
//        writables.add(classDefinitions);
//        writables.add(microserviceMain);
//        writables.add(microservicePom);
//        return writables;
    }
}
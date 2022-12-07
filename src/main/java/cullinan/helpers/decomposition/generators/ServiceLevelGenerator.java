package cullinan.helpers.decomposition.generators;

import cullinan.helpers.decomposition.generators.model.GeneratedData;
import cullinan.helpers.decomposition.generators.model.GeneratedServiceLevel;
import cullinan.helpers.decomposition.javagenerators.ServiceMainCreator;
import cullinan.helpers.decomposition.javagenerators.ClassDefinitionsCreator;
import input.Microservice;
import spoon.reflect.declaration.CtClass;

import java.util.ArrayList;
import java.util.List;

public class ServiceLevelGenerator {
    private final GeneratedData generatedData;
    private GeneratedServiceLevel generatedServiceLevel;

    public ServiceLevelGenerator(GeneratedData generatedData) {
        this.generatedData = generatedData;
    }

    public GeneratedServiceLevel generateMainService() {
        generatedServiceLevel = new GeneratedServiceLevel("main");
        generatedServiceLevel = generateMainServiceClassDefinitions(generatedData);
        return generatedServiceLevel;
    }

    public GeneratedServiceLevel generateMicroservice(Microservice microservice) {
        generatedServiceLevel = new GeneratedServiceLevel(microservice.getName());
        generatedServiceLevel = generateMain(generatedData, microservice);
        generatedServiceLevel = generateMicroserviceClassDefinitions(generatedData, microservice);
        return generatedServiceLevel;
    }

    private GeneratedServiceLevel generateMain(GeneratedData generatedData, Microservice microservice) {
        List<CtClass> services = new ArrayList<>();
        for (String serviceClassName : microservice.getIdentifiedClasses()) {
            CtClass serviceImplementation = generatedData.getServiceImplementation(serviceClassName);
//            GeneratedClassLevel generatedClassLevel = generatedData.getGeneratedClassLevel(serviceClassName);
//            services.add(generatedClassLevel.getGeneratedClientService().getService());
            services.add(serviceImplementation);
        }
        CtClass main = ServiceMainCreator.createMain(services);
        generatedServiceLevel.addServiceMain(main); // TODO Main needs to know about all the services that are generated
        return generatedServiceLevel;
    }

    // TODO Don't forget about main service


    // TODO Generate Poms
    // TODO Generate classefinitions

    private GeneratedServiceLevel generateMainServiceClassDefinitions(GeneratedData generatedData) {
        List<CtClass> proxies = generatedData.getProxies();

        List<String> classesInService = new ArrayList<>();
        List<String> proxyDefinitions = new ArrayList<>();

        for (CtClass proxy : proxies) {
            proxyDefinitions.add(proxy.getQualifiedName());
        }

        CtClass classDefinitions = new ClassDefinitionsCreator(classesInService, proxyDefinitions).build();

        generatedServiceLevel.addClassDefinitions(classDefinitions);
        return generatedServiceLevel;
    }

    private GeneratedServiceLevel generateMicroserviceClassDefinitions(GeneratedData generatedData, Microservice microservice) {
        List<CtClass> proxies = generatedData.getProxies();
        // For every class, decide if the class is in the microservice.
        // If it is, we want to add it the service class definitions
        // If not, we want to add it to the proxy definitions.

        List<String> classesInService = microservice.getIdentifiedClasses();
        List<String> proxyDefinitions = new ArrayList<>();

        for (CtClass proxy : proxies) {
            String currentClassName = proxy.getQualifiedName();
//            CtType classType = generatedClassLevel.getServiceOriginalClass().getReference().getTypeDeclaration();

            // TODO Data is not in GeneratedData? Unless we put monolith in as well?
            // Decide how we put the not implemented version as well, cause it should be contained....?
            if (!classesInService.contains(currentClassName)) {
                proxyDefinitions.add(currentClassName);
            }
        }

        // Proxy definitions should contain all Services proxies TODO
//        Collection<GeneratedServiceLevel> serviceLevels = generatedData.getGeneratedServiceLevels().values();

        // TODO wait they dont need to be proxies just the original class name?
//        List<CtClass> allExternalProxies = serviceLevels.stream()
//                .filter(serviceLevel -> !serviceLevel.getServiceName().equalsIgnoreCase(serviceName))
//                .map(GeneratedServiceLevel::getClassLevels)
//                .flatMap(Collection::stream)
//                .map(GeneratedClassLevel::getProxy)
//                .toList();

        CtClass classDefinitions = new ClassDefinitionsCreator(classesInService, proxyDefinitions).build();

        generatedServiceLevel.addClassDefinitions(classDefinitions);
        return generatedServiceLevel;
    }
}

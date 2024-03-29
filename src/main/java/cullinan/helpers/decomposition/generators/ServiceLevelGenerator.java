package cullinan.helpers.decomposition.generators;

import cullinan.helpers.decomposition.generators.model.GeneratedData;
import cullinan.helpers.decomposition.generators.model.GeneratedServiceLevel;
import cullinan.helpers.decomposition.writers.WriteDefinition;
import generatedfiles.*;
import input.Microservice;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class ServiceLevelGenerator {
    private final GeneratedData generatedData;
    private final Document originalPom;
    private GeneratedServiceLevel generatedServiceLevel;

    public ServiceLevelGenerator(GeneratedData generatedData, Document originalPom) {
        this.generatedData = generatedData;
        this.originalPom = originalPom;
    }

    public GeneratedServiceLevel generateMicroservice(Microservice microservice) {
        generatedServiceLevel = new GeneratedServiceLevel(microservice.getName());
        generatedServiceLevel = generatePom(microservice, originalPom);
        generatedServiceLevel = generateMicroserviceMain(generatedData, microservice);
        generatedServiceLevel = generateMicroserviceClassDefinitions(generatedData, microservice);
        return generatedServiceLevel;
    }

    private GeneratedServiceLevel generateMicroserviceMain(GeneratedData generatedData, Microservice microservice) {
        List<Service> services = new ArrayList<>();
        for (String serviceClassName : microservice.getIdentifiedClasses()) {
            Service service = generatedData.getService(serviceClassName);
            services.add(service);
        }
        MicroserviceMain main = new MicroserviceMain(services, microservice.getName());
        generatedServiceLevel.addServiceMain(main); // TODO Main needs to know about all the services that are generated
        return generatedServiceLevel;
    }

    // TODO Pom also needs generated for MAIN? Maybe do main first
    private GeneratedServiceLevel generatePom(Microservice microservice, Document originalPom) {
        List<Service> services = new ArrayList<>();
//        for (String serviceClassName : microservice.getIdentifiedClasses()) {
//            Service service = generatedData.getService(serviceClassName);
//            services.add(service);
//        }
        MicroservicePom pom = new MicroservicePom(originalPom, microservice.getName());
        generatedServiceLevel.addServicePom(pom); // TODO Main needs to know about all the services that are generated
        return generatedServiceLevel;
    }

    // TODO Don't forget about main service


    // TODO Generate Poms
    // TODO Generate classefinitions



    private GeneratedServiceLevel generateMicroserviceClassDefinitions(GeneratedData generatedData, Microservice microservice) {
        List<Proxy> proxies = generatedData.getProxies();
        // For every class, decide if the class is in the microservice.
        // If it is, we want to add it the service class definitions
        // If not, we want to add it to the proxy definitions.

        List<String> classesInService = microservice.getIdentifiedClasses();
        List<String> proxyDefinitions = new ArrayList<>();

        for (Proxy proxy : proxies) {
            String currentClassName = proxy.getJava().getQualifiedName();
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

        ClassDefinitions classDefinitions = new ClassDefinitions(WriteDefinition.THIS_MICROSERVICE, classesInService, proxyDefinitions);

        generatedServiceLevel.addClassDefinitions(classDefinitions);
        return generatedServiceLevel;
    }
}

package cullinan.helpers.decomposition.generators;

import cullinan.helpers.decomposition.generators.model.*;
import generatedfiles.OriginalJava;
import generatedfiles.UnimplementedType;
import input.Microservice;
import org.w3c.dom.Document;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;

import java.util.List;
import java.util.stream.Collectors;

public class DataGenerator {
    private final Factory factory;
    private final Document originalPom;

    private GeneratedData generatedData = new GeneratedData();

    public DataGenerator(Factory factory, Document originalPom) {
        this.factory = factory;
        this.originalPom = originalPom;
    }


    // TODO We basically need to find a way to produce all possible classes.
    // And then for each class decide how it is going to be produced in a code base.

    public GeneratedData generate(List<Microservice> microservices) {
        generatedData = generateHelperData(generatedData);
        generatedData = generateClassData(generatedData, microservices);
        generatedData = generateServiceData(generatedData, originalPom, microservices);
        generatedData = generateUnimplementedData(generatedData, microservices);

        return generatedData;
    }

    private GeneratedData generateHelperData(GeneratedData generatedData) {
        HelpersGenerator helpersGenerator = new HelpersGenerator();
        GeneratedHelperClasses generatedHelperClasses = helpersGenerator.generate();
        generatedData.addGeneratedHelpers(generatedHelperClasses);
        return generatedData;
    }

    private GeneratedData generateClassData(GeneratedData generatedData, List<Microservice> microservices) {
        for (Microservice microService : microservices) {
            generatedData = generateClassData(generatedData, microService);
        }
        return generatedData;
    }

    private GeneratedData generateClassData(GeneratedData generatedData, Microservice microservice) {
        for (String fullyQualifiedClass : microservice.getIdentifiedClasses()) { // TODO Here we have information on service, maybe store
            CtClass objectCtClass = factory.Class().get(fullyQualifiedClass); // TODO Could be enum? interface?
            OriginalJava originalJava = new OriginalJava(objectCtClass, microservice);
            generatedData = generateClassData(generatedData, originalJava);
        }
        return generatedData;
    }

    private GeneratedData generateClassData(GeneratedData generatedData, OriginalJava originalJava) {
        ClassLevelGenerator classLevelGenerator = new ClassLevelGenerator(generatedData);
        GeneratedClassLevel classLevel = classLevelGenerator.generate(originalJava);
        generatedData.addGeneratedClassLevel(classLevel);
        return generatedData;
    }

    private GeneratedData generateServiceData(GeneratedData generatedData, Document originalPom, List<Microservice> microservices) {
        generatedData = generateMainServiceData(generatedData, originalPom);
        generatedData = generateInterfaceServiceData(generatedData);
        for (Microservice microservice : microservices) {
            generatedData = generateMicroserviceData(generatedData,originalPom, microservice);
        }
        return generatedData;
    }

    private GeneratedData generateMainServiceData(GeneratedData generatedData, Document originalPom) {
        MainLevelGenerator mainLevelGenerator = new MainLevelGenerator(generatedData, originalPom);
        GeneratedMainServiceLevel generatedMainServiceLevel = mainLevelGenerator.generateMainService();
        generatedData.addGeneratedMainServiceLevel(generatedMainServiceLevel);
        return generatedData;
    }

    private GeneratedData generateInterfaceServiceData(GeneratedData generatedData) {
        InterfaceLevelGenerator interfaceLevelGenerator = new InterfaceLevelGenerator();
        GeneratedInterfaceServiceLevel generatedInterfaceServiceLevel = interfaceLevelGenerator.generate();
        generatedData.addGeneratedInterfaceServiceLevel(generatedInterfaceServiceLevel);
        return generatedData;
    }

    private GeneratedData generateMicroserviceData(GeneratedData generatedData, Document originalPom, Microservice microservice) {
        ServiceLevelGenerator serviceLevelGenerator = new ServiceLevelGenerator(generatedData, originalPom);
        GeneratedServiceLevel generatedServiceLevel = serviceLevelGenerator.generateMicroservice(microservice);
        generatedData.addGeneratedServiceLevel(generatedServiceLevel);
        return generatedData;
    }

    private GeneratedData generateUnimplementedData(GeneratedData generatedData, List<Microservice> microservices) {
        List<CtType<?>> allTypes = factory.Class().getAll(); // Data is not in Generated Data. We also go over types such as enum, interfaces, classes, and others so we probably do need the factory for this data.
        // For all service classes, we don't need an unimplemented version... Also gives some issues with duplicate proxy/unimplemented version...
        // I guess in a better version we would want unimplemented versions if a proxy is not used...
        List<String> allServiceClasses = microservices.stream()
                .map(Microservice::getIdentifiedClasses)
                .flatMap(List::stream)
                .map(String::toLowerCase)
                .toList();

        // This gets rid of all classes that are service classes: Instead we will write either the implementation or a proxy version.
        List<CtType<?>> requiredUnimplementedTypes = allTypes.stream()
                .filter(ctType -> !allServiceClasses.contains(ctType.getQualifiedName().toLowerCase()))
                .collect(Collectors.toList());

        UnimplementedTypeGenerator unimplementedTypeGenerator = new UnimplementedTypeGenerator();
        List<UnimplementedType> unimplementedTypes = unimplementedTypeGenerator.generate(requiredUnimplementedTypes);

        unimplementedTypes.forEach(generatedData::addUnimplementedData);
        return generatedData;
    }
}
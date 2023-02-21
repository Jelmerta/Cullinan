package cullinanalternativeapproach;

import cullinan.helpers.decomposition.generators.UnimplementedTypeGenerator;
import input.Microservice;
import org.w3c.dom.Document;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;

import java.util.List;
import java.util.stream.Collectors;

public class DataGenerator2 {
    private final Factory factory;
    private final Document originalPom;

    private GeneratedData2 generatedData = new GeneratedData2();

    public DataGenerator2(Factory factory, Document originalPom) {
        this.factory = factory;
        this.originalPom = originalPom;
    }


    // TODO We basically need to find a way to produce all possible classes.
    // And then for each class decide how it is going to be produced in a code base.

    public GeneratedData2 generate(List<Microservice> microservices) {
        generatedData = generateHelperData(generatedData);
        generatedData = generateClassData(generatedData, microservices);
        generatedData = generateServiceData(generatedData, originalPom, microservices);
        generatedData = generateUnimplementedData(generatedData, microservices);

        return generatedData;
    }

    private GeneratedData2 generateHelperData(GeneratedData2 generatedData) {
        HelpersGenerator2 helpersGenerator = new HelpersGenerator2();
        GeneratedHelperClasses2 generatedHelperClasses = helpersGenerator.generate();
        generatedData.addGeneratedHelpers(generatedHelperClasses);
        return generatedData;
    }

    private GeneratedData2 generateClassData(GeneratedData2 generatedData, List<Microservice> microservices) {
        for (Microservice microService : microservices) {
            generatedData = generateClassData(generatedData, microService);
        }
        return generatedData;
    }

    private GeneratedData2 generateClassData(GeneratedData2 generatedData, Microservice microservice) {
        for (String fullyQualifiedClass : microservice.getIdentifiedClasses()) { // TODO Here we have information on service, maybe store
            CtClass objectCtClass = factory.Class().get(fullyQualifiedClass); // TODO Could be enum? interface?
            OriginalJava originalJava = new OriginalJava(objectCtClass, microservice);
            generatedData = generateClassData(generatedData, originalJava);
        }
        return generatedData;
    }

    private GeneratedData2 generateClassData(GeneratedData2 generatedData, OriginalJava originalJava) {
        ClassLevelGenerator2 classLevelGenerator = new ClassLevelGenerator2(generatedData);
        GeneratedClassLevel2 classLevel = classLevelGenerator.generate(originalJava);
        generatedData.addGeneratedClassLevel(classLevel);
        return generatedData;
    }

    private GeneratedData2 generateServiceData(GeneratedData2 generatedData, Document originalPom, List<Microservice> microservices) {
        generatedData = generateMainServiceData(generatedData, originalPom);
        generatedData = generateInterfaceServiceData(generatedData);
        for (Microservice microservice : microservices) {
            generatedData = generateMicroserviceData(generatedData,originalPom, microservice);
        }
        return generatedData;
    }

    private GeneratedData2 generateMainServiceData(GeneratedData2 generatedData, Document originalPom) {
        MainLevelGenerator mainLevelGenerator = new MainLevelGenerator(generatedData, originalPom);
        GeneratedMainServiceLevel generatedMainServiceLevel = mainLevelGenerator.generateMainService();
        generatedData.addGeneratedMainServiceLevel(generatedMainServiceLevel);
        return generatedData;
    }

    private GeneratedData2 generateInterfaceServiceData(GeneratedData2 generatedData) {
        InterfaceLevelGenerator interfaceLevelGenerator = new InterfaceLevelGenerator();
        GeneratedInterfaceServiceLevel generatedInterfaceServiceLevel = interfaceLevelGenerator.generate();
        generatedData.addGeneratedInterfaceServiceLevel(generatedInterfaceServiceLevel);
        return generatedData;
    }

    private GeneratedData2 generateMicroserviceData(GeneratedData2 generatedData, Document originalPom, Microservice microservice) {
        ServiceLevelGenerator2 serviceLevelGenerator = new ServiceLevelGenerator2(generatedData, originalPom);
        GeneratedServiceLevel2 generatedServiceLevel = serviceLevelGenerator.generateMicroservice(microservice);
        generatedData.addGeneratedServiceLevel(generatedServiceLevel);
        return generatedData;
    }

    private GeneratedData2 generateUnimplementedData(GeneratedData2 generatedData, List<Microservice> microservices) {
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

        UnimplementedTypeGenerator2 unimplementedTypeGenerator = new UnimplementedTypeGenerator2();
        List<UnimplementedType> unimplementedTypes = unimplementedTypeGenerator.generate(requiredUnimplementedTypes);

        unimplementedTypes.forEach(generatedData::addUnimplementedData);
        return generatedData;
    }
}
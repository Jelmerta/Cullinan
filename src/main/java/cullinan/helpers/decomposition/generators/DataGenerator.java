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

// TODO Would it be possible to completley get rid of DataGenerator/GeneratedData?
// We have the data and the writers, now we just store it here... Probably not efficient? idk
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
        generatedData = generateParentModuleData(generatedData, microservices);
        generatedData = generateHelperData(generatedData);
        generatedData = generateClassData(generatedData, microservices);
        generatedData = generateServiceData(generatedData, originalPom, microservices);
//        generatedData = generateUnimplementedData(generatedData, microservices); // TODO Temporary disabled due to issue with anonymous classes.

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
//         TODO We need to generate more for inner classes
//         TODO How do we replace the generated classes though? A proxy for example still needs knowledge of the generated stuff? Passing multiple interfaces feels awful... Generating multiple times is awful...
//          Set<CtType> nestedTypes = originalClass.getNestedTypes();
//            for (CtType nestedType : nestedTypes) {
//                if (nestedType.isClass() || nestedType.isEnum()) { // TODO Any other things we need to move?
//                    ServiceInterfaceCreator serviceInterfaceCreator = new ServiceInterfaceCreator((CtClass) nestedType);
//                    CtInterface ctInterface = serviceInterfaceCreator.buildInterface();
//                    Set<CtMethod> allMethods = ctInterface.getAllMethods();
//                    for (CtMethod method : allMethods) { // TODO GetMethod or allmethods?
//                        result.addMethod(method);
//                    }
//                }
//            }


        for (String fullyQualifiedClass : microservice.getIdentifiedClasses()) { // TODO Here we have information on service, maybe store
            CtClass objectCtClass = factory.Class().get(fullyQualifiedClass); // TODO Could be enum? interface?

            System.out.println("Generating API classes for class/enum: " + fullyQualifiedClass);

//             TODO HOW TO DEAL WITH INNER CLASS?ENUM
            if (objectCtClass == null) {
//TODO we could improve logging if we knew the name of the class/enum in question
                throw new IllegalStateException("Did not find class. This could be the case because 1. you tried to decompose an inner class or enum. We cannot currently deal with that. If you want to do this, please make sure to first put the inner class/enum in a separate file. Or 2: You tried to decompose an interface which is not yet supported. Currently the interface is copied to all services, which is of course not a great solution...");
            }

//            TODO Just skip abstract classes? We should probably treat them like interfaces? Maybe just copy to each service?
//            Abstract cannot be used to create objects directly? Anonymous objects? Not sure...
//            if (objectCtClass.isAbstract()) {
//                System.out.println("We expect the class to not be abstract. We will continue, but not decompose this abstract class. This is not yet implemented. This might require some developer input to resolve afterwards.");
//                continue;
//            }

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
        generatedData = generateParentModuleData(generatedData, microservices);
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

    private GeneratedData generateParentModuleData(GeneratedData generatedData, List<Microservice> microservices) {
        ParentModuleGenerator parentModuleGenerator = new ParentModuleGenerator(generatedData, microservices);
        GeneratedParentModule generatedParentModule = parentModuleGenerator.generate();
        generatedData.addGeneratedParentModule(generatedParentModule);
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
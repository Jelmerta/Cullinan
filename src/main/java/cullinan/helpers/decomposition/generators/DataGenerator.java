package cullinan.helpers.decomposition.generators;

import cullinan.helpers.decomposition.generators.model.GeneratedClassLevel;
import cullinan.helpers.decomposition.generators.model.GeneratedData;
import cullinan.helpers.decomposition.generators.model.GeneratedHelperClasses;
import cullinan.helpers.decomposition.generators.model.GeneratedServiceLevel;
import input.Microservice;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;

import java.util.List;

public class DataGenerator {
    private final Factory factory;

    private GeneratedData generatedData = new GeneratedData();

    public DataGenerator(Factory factory) {
        this.factory = factory;
    }


    // TODO We basically need to find a way to produce all possible classes.
    // And then for each class decide how it is going to be produced in a code base.

    public GeneratedData generate(List<Microservice> microservices) {
        generatedData = generateHelperData(generatedData);
        generatedData = generateClassData(generatedData, microservices);
        generatedData = generateServiceData(generatedData, microservices);
        generatedData = generateUnimplementedData(generatedData);

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
        for (String fullyQualifiedClass : microservice.getIdentifiedClasses()) {
            CtClass objectCtClass = factory.Class().get(fullyQualifiedClass); // TODO Could be enum? interface?
            generatedData = generateClassData(generatedData, objectCtClass);
        }
        return generatedData;
    }

    private GeneratedData generateClassData(GeneratedData generatedData, CtClass ctClass) {
        ClassLevelGenerator classLevelGenerator = new ClassLevelGenerator(generatedData);
        GeneratedClassLevel classLevel = classLevelGenerator.generate(ctClass);
        generatedData.addGeneratedClassLevel(classLevel);
        return generatedData;
    }

    private GeneratedData generateServiceData(GeneratedData generatedData, List<Microservice> microservices) {
        generatedData = generateMainServiceData(generatedData);
        for (Microservice microservice : microservices) {
            generatedData = generateServiceData(generatedData, microservice);
        }
        return generatedData;
    }

    private GeneratedData generateMainServiceData(GeneratedData generatedData) {
        ServiceLevelGenerator serviceLevelGenerator = new ServiceLevelGenerator(generatedData);
        GeneratedServiceLevel generatedServiceLevel = serviceLevelGenerator.generateMainService();
        generatedData.addGeneratedServiceLevel(generatedServiceLevel);
        return generatedData;
    }

    private GeneratedData generateServiceData(GeneratedData generatedData, Microservice microservice) {
        ServiceLevelGenerator serviceLevelGenerator = new ServiceLevelGenerator(generatedData);
        GeneratedServiceLevel generatedServiceLevel = serviceLevelGenerator.generateMicroservice(microservice);
        generatedData.addGeneratedServiceLevel(generatedServiceLevel);
        return generatedData;
    }

    private GeneratedData generateUnimplementedData(GeneratedData generatedData) {
        List<CtType<?>> allTypes = factory.Class().getAll();
        UnimplementedTypeGenerator unimplementedTypeGenerator = new UnimplementedTypeGenerator();
        List<CtType> unimplementedTypes = unimplementedTypeGenerator.generate(allTypes);

        unimplementedTypes.forEach(generatedData::addUnimplementedData);
        return generatedData;
    }
}
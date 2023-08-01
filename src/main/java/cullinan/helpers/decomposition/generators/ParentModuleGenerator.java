package cullinan.helpers.decomposition.generators;

import cullinan.helpers.decomposition.generators.model.GeneratedData;
import cullinan.helpers.decomposition.generators.model.GeneratedParentModule;
import generatedfiles.ParentPom;
import input.Microservice;

import java.util.List;

public class ParentModuleGenerator {
    private final GeneratedData generatedData;
    private final List<Microservice> microservices;
    private GeneratedParentModule generatedParentModule;

    public ParentModuleGenerator(GeneratedData generatedData, List<Microservice> microservices) {
        this.generatedData = generatedData;
        this.microservices = microservices;
    }

    public GeneratedParentModule generate(String projectName) {
        generatedParentModule = new GeneratedParentModule();
        generatedParentModule = generateParentPom(projectName);
        return generatedParentModule;
    }

    private GeneratedParentModule generateParentPom(String projectName) {
        ParentPom pom = new ParentPom(projectName, microservices);
        generatedParentModule.addPom(pom);
        return generatedParentModule;
    }
}

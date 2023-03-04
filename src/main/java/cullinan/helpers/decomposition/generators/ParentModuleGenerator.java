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

    public GeneratedParentModule generate() {
        generatedParentModule = new GeneratedParentModule();
        generatedParentModule = generateParentPom();
        return generatedParentModule;
    }

    private GeneratedParentModule generateParentPom() {
        ParentPom pom = new ParentPom(microservices);
        generatedParentModule.addPom(pom);
        return generatedParentModule;
    }
}

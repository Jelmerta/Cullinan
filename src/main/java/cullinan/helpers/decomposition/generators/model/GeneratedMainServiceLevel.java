package cullinan.helpers.decomposition.generators.model;

import generatedfiles.ClassDefinitions;
import generatedfiles.MainServicePom;

public class GeneratedMainServiceLevel {
    private MainServicePom mainServicePom;
    private ClassDefinitions classDefinitions; // Only here because of the proxy/service class definitions. Most of serializationUtil is the same for each service.

    public GeneratedMainServiceLevel() {
    }

    public void addMainPom(MainServicePom mainServicePom) {
        this.mainServicePom =  mainServicePom;
    }

    public void addClassDefinitions(ClassDefinitions classDefinitions) {
        this.classDefinitions = classDefinitions;
    }

    public MainServicePom getMainServicePom() {
        return mainServicePom;
    }

    public ClassDefinitions getClassDefinitions() {
        return classDefinitions;
    }
}

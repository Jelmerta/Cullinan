package cullinan.helpers.decomposition.generators;

import cullinan.helpers.decomposition.generators.model.GeneratedInterfaceServiceLevel;
import generatedfiles.ServiceInterfacePom;

public class InterfaceLevelGenerator {
    private GeneratedInterfaceServiceLevel generatedInterfaceServiceLevel;

    public InterfaceLevelGenerator() {
    }

    public GeneratedInterfaceServiceLevel generate() {
        generatedInterfaceServiceLevel = new GeneratedInterfaceServiceLevel();
        generatedInterfaceServiceLevel = generateMainPom();
        return generatedInterfaceServiceLevel;
    }

    private GeneratedInterfaceServiceLevel generateMainPom() {
        ServiceInterfacePom pom = new ServiceInterfacePom();
        generatedInterfaceServiceLevel.addPom(pom);
        return generatedInterfaceServiceLevel;
    }
}

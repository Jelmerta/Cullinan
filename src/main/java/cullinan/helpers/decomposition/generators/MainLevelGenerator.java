package cullinan.helpers.decomposition.generators;

import cullinan.helpers.decomposition.generators.model.GeneratedData;
import cullinan.helpers.decomposition.generators.model.GeneratedMainServiceLevel;
import generatedfiles.ClassDefinitions;
import generatedfiles.MainServicePom;
import generatedfiles.Proxy;
import cullinan.helpers.decomposition.writers.WriteDefinition;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;

public class MainLevelGenerator {
    private final GeneratedData generatedData;
    private final Document originalPom;
    private GeneratedMainServiceLevel generatedMainServiceLevel;

    public MainLevelGenerator(GeneratedData generatedData, Document originalPom) {
        this.generatedData = generatedData;
        this.originalPom = originalPom;
    }

    public GeneratedMainServiceLevel generateMainService() {
        generatedMainServiceLevel = new GeneratedMainServiceLevel();
        generatedMainServiceLevel = generateMainPom();
        generatedMainServiceLevel = generateMainServiceClassDefinitions(generatedData);
        return generatedMainServiceLevel;
    }


    private GeneratedMainServiceLevel generateMainPom() {
        MainServicePom pom = new MainServicePom(originalPom);
        generatedMainServiceLevel.addMainPom(pom);
        return generatedMainServiceLevel;
    }

    private GeneratedMainServiceLevel generateMainServiceClassDefinitions(GeneratedData generatedData) {
        List<Proxy> proxies = generatedData.getProxies();

        List<String> classesInService = new ArrayList<>();
        List<String> proxyDefinitions = new ArrayList<>();

        for (Proxy proxy : proxies) {
            proxyDefinitions.add(proxy.getJava().getQualifiedName()); // Probably directly from proxy...?
        }

        ClassDefinitions classDefinitions = new ClassDefinitions(WriteDefinition.MAIN_SERVICE, classesInService, proxyDefinitions);

        generatedMainServiceLevel.addClassDefinitions(classDefinitions);
        return generatedMainServiceLevel;
    }
}

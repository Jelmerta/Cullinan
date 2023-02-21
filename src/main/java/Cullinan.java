import cullinan.helpers.decomposition.generators.DataGenerator;
import cullinan.helpers.decomposition.generators.model.GeneratedData;
import cullinan.helpers.decomposition.servicecreators.InterfaceModuleCreator;
import cullinan.helpers.decomposition.servicecreators.MainServiceCreator;
import cullinan.helpers.decomposition.servicecreators.MicroserviceCreator;
import cullinan.helpers.other.DirectoryCopier;
import input.DecompositionInputReader;
import input.IdentifiedServiceCut;
import input.Microservice;
import spoon.reflect.factory.Factory;
import spoonhelpers.managers.SpoonFactoryManager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Cullinan {
    private static final String DECOMPOSITION_INPUT_JSON = "decomposition_input.json";
    private static final String SOURCE_PATH_ROOT = "dddsample-core-master";
    private static final String SOURCE_PATH_JAVA = SOURCE_PATH_ROOT + "/src/main/java";
    private static final String DEFAULT_OUTPUT_PATH = SOURCE_PATH_ROOT + "_decomposed/";
    private static final String MAIN_SERVICE_OUTPUT_PATH = DEFAULT_OUTPUT_PATH + "monolith/"; // TODO Original name? no Main?
    private static final String INTERFACES_MODULE_OUTPUT_PATH = DEFAULT_OUTPUT_PATH + "serviceinterfaces/";

    public static void main(String[] args) throws IOException {
        IdentifiedServiceCut serviceCut = DecompositionInputReader.getIdentifiedServiceCut(DECOMPOSITION_INPUT_JSON);

        DirectoryCopier.copyFolder(Path.of(SOURCE_PATH_ROOT), Path.of(MAIN_SERVICE_OUTPUT_PATH));

        Factory codebaseFactory = SpoonFactoryManager.getFactory(Path.of(SOURCE_PATH_JAVA));

        DataGenerator dataGenerator = new DataGenerator(codebaseFactory);
        GeneratedData generatedData = dataGenerator.generate(serviceCut.getMicroservices());

        MainServiceCreator mainServiceCreator = new MainServiceCreator(generatedData);
        InterfaceModuleCreator interfaceModuleCreator = new InterfaceModuleCreator(generatedData);
        List<MicroserviceCreator> microserviceCreators = new ArrayList<>();
        for (Microservice microservice : serviceCut.getMicroservices()) {
            microserviceCreators.add(new MicroserviceCreator(generatedData, microservice));
        }

        // Make sure to generate all writers before writing everything to file. Maybe make a DecompositionCreator containing this fact?
        mainServiceCreator.create(Path.of(MAIN_SERVICE_OUTPUT_PATH));
        interfaceModuleCreator.create(Path.of(INTERFACES_MODULE_OUTPUT_PATH));
        for (MicroserviceCreator microserviceCreator : microserviceCreators) {
            Path servicePath = Path.of(DEFAULT_OUTPUT_PATH + microserviceCreator.getServiceName().toLowerCase());
            microserviceCreator.create(servicePath);
        }
    }
}

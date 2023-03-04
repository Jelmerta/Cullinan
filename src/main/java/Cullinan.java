import cullinan.helpers.decomposition.generators.DataGenerator;
import cullinan.helpers.decomposition.generators.model.GeneratedData;
import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.ModuleType;
import cullinan.helpers.other.DirectoryCopier;
import generatedfiles.ServiceDefinition;
import generatedfiles.Writable;
import input.DecompositionInputReader;
import input.IdentifiedServiceCut;
import input.Microservice;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoonhelpers.managers.SpoonFactoryManager;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Cullinan {
// DDDsample
// private static final String DECOMPOSITION_INPUT_JSON = "../decomposition_input.json";
    private static final String DECOMPOSITION_INPUT_JSON = "../mybatis_decomposition_input.json";
// DDDsample
//     private static final Path SOURCE_PATH_ROOT = Path.of("../dddsample-core-master");
    private static final Path SOURCE_PATH_ROOT = Path.of("../mybatis-3");
    private static final Path SOURCE_PATH_JAVA = Path.of(SOURCE_PATH_ROOT + "/src/main/java");
    private static final Path DEFAULT_OUTPUT_PATH = Path.of(SOURCE_PATH_ROOT + "_decomposed");
    private static final Path PARENT_MODULE_OUTPUT_PATH = DEFAULT_OUTPUT_PATH; // TODO just a rename...?
    private static final Path MAIN_SERVICE_OUTPUT_PATH = Path.of(DEFAULT_OUTPUT_PATH + "/monolith"); // TODO Original name? no Main?
    private static final Path INTERFACES_MODULE_OUTPUT_PATH = Path.of(DEFAULT_OUTPUT_PATH + "/serviceinterfaces");
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        System.out.println("Starting decomposition");
        long startTime = System.currentTimeMillis();
        long currentTime;

        currentTime = System.currentTimeMillis();
        System.out.println("Reading decomposition input file. Time passed: " + (currentTime - startTime) / 1000 + " seconds");
        IdentifiedServiceCut serviceCut = DecompositionInputReader.getIdentifiedServiceCut(DECOMPOSITION_INPUT_JSON);
        System.out.println();

        currentTime = System.currentTimeMillis();
        System.out.println("Defining services. Time passed: " + (currentTime - startTime) / 1000.0 + " seconds");
        // TODO Add service during generation of classes to the writable?
        List<ServiceDefinition> serviceDefinitions = new ArrayList<>();
//        TODO Not sure about name microservices?
        ServiceDefinition parent = new ServiceDefinition(ModuleType.PARENT, "microservices", PARENT_MODULE_OUTPUT_PATH, List.of());
        serviceDefinitions.add(parent);

        ServiceDefinition main = new ServiceDefinition(ModuleType.MAIN, "main", MAIN_SERVICE_OUTPUT_PATH, List.of("Application"));
        serviceDefinitions.add(main);

        ServiceDefinition interfaceModule = new ServiceDefinition(ModuleType.INTERFACE, "clientinterfaces", INTERFACES_MODULE_OUTPUT_PATH, List.of());
        serviceDefinitions.add(interfaceModule);
        for (Microservice microserviceInput : serviceCut.getMicroservices()) {
            ServiceDefinition microservice = new ServiceDefinition(ModuleType.MICROSERVICE, microserviceInput.getName().toLowerCase(), Path.of(DEFAULT_OUTPUT_PATH.toString() + "/" + microserviceInput.getName().toLowerCase() + "/"), microserviceInput.getIdentifiedClasses());
            serviceDefinitions.add(microservice);
        }
        System.out.println();

        currentTime = System.currentTimeMillis();
        System.out.println("Copying original code base to new destination. Time passed: " + (currentTime - startTime) / 1000.0 + " seconds");
        DirectoryCopier.copyFolder(SOURCE_PATH_ROOT, MAIN_SERVICE_OUTPUT_PATH);
        System.out.println();

        currentTime = System.currentTimeMillis();
        System.out.println("Reading in original code base. Time passed: " + (currentTime - startTime) / 1000.0 + " seconds");
        Factory codebaseFactory = SpoonFactoryManager.getFactory(SOURCE_PATH_JAVA);
        System.out.println();

        currentTime = System.currentTimeMillis();
        System.out.println("Reading original pom file (we assume a pom file exists! No other build automation tools currently supported). Time passed: " + (currentTime - startTime) / 1000.0 + " seconds");
        Document originalPom = pomReader();
        System.out.println();

        // TODO Make changes to original code base in order to make decomposition easier:
        // Put inner classes/interfaces to separate files
        // TODO Note: now we can add the inner classes to the decomposition json as well if we want to
        // TODO We might have to remove static for inner classes? Maybe make some private stuff public/package?
//        List<CtType<?>> allTypes = codebaseFactory.Class().getAll();
//        for (CtType type : allTypes) {
//            Set<CtType> nestedTypes = type.getNestedTypes();
//            for (CtType nestedType : nestedTypes) {
//                if (nestedType.isClass() || nestedType.isInterface() || nestedType.isEnum()) { // TODO Any other things we need to move?
//                    type.removeNestedType(nestedType);
//                    type.getPackage().addType(nestedType);
//                }
//            }
//        }


        currentTime = System.currentTimeMillis();
        System.out.println("Generating required classes and additional files for new partitions. Time passed: " + (currentTime - startTime) / 1000.0 + " seconds");
        DataGenerator dataGenerator = new DataGenerator(codebaseFactory, originalPom);
        GeneratedData generatedData = dataGenerator.generate(serviceCut.getMicroservices());
        List<Writable> writables = generatedData.getWritables();
        System.out.println();

        currentTime = System.currentTimeMillis();
        System.out.println("Generating data writers. Time passed: " + (currentTime - startTime) / 1000.0 + " seconds");
        List<DataWriter> dataWriters = new ArrayList<>();
        for (Writable writable : writables) {
            dataWriters.add(writable.createWriter());
        }
        System.out.println();

        currentTime = System.currentTimeMillis();
        System.out.println("Writing partitions. Time passed: " + (currentTime - startTime) / 1000.0 + " seconds");
        // TODO We could make a verifier before writing, no clashing: multiple writers wanting to write to same file
        for (ServiceDefinition serviceDefinition : serviceDefinitions) {
            // Implicitly adds all dependencies required for running the service. Could be files. Could be interfaces. Very likely adds unneeded dependencies as well. Not a minimal set of dependencies as we prefer running code.
            if (serviceDefinition.getServiceType().equals(ModuleType.MICROSERVICE)) {
                DirectoryCopier.copyFolder(SOURCE_PATH_ROOT, serviceDefinition.getOutputPath());
            }
            System.out.println("Writing to service: " + serviceDefinition.getName());
            for (DataWriter dataWriter : dataWriters) {
                if (dataWriter.shouldWrite(serviceDefinition)) {
                    dataWriter.write(serviceDefinition);
                }
            }
        }
        System.out.println();

        currentTime = System.currentTimeMillis();
        System.out.println("Finished decomposition of services. Time passed: " + (currentTime - startTime) / 1000.0 + " seconds");
    }

    // This solution is ugly, passing the document around...
    private static Document pomReader() throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(SOURCE_PATH_ROOT + "/pom.xml");
    }
}

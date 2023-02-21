package cullinanalternativeapproach;

import cullinan.helpers.other.DirectoryCopier;
import input.DecompositionInputReader;
import input.IdentifiedServiceCut;
import input.Microservice;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import spoon.reflect.factory.Factory;
import spoonhelpers.managers.SpoonFactoryManager;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Cullinan2 {
    private static final String DECOMPOSITION_INPUT_JSON = "../decomposition_input.json";
    private static final Path SOURCE_PATH_ROOT = Path.of("../dddsample-core-master");
    private static final Path SOURCE_PATH_JAVA = Path.of(SOURCE_PATH_ROOT + "/src/main/java");
    private static final Path DEFAULT_OUTPUT_PATH = Path.of(SOURCE_PATH_ROOT + "_decomposed");
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
        ServiceDefinition main = new ServiceDefinition(ServiceType.MAIN_SERVICE, "main", MAIN_SERVICE_OUTPUT_PATH, List.of("Application"));
        serviceDefinitions.add(main);
        ServiceDefinition interfaceModule = new ServiceDefinition(ServiceType.INTERFACE_MODULE, "clientinterfaces", INTERFACES_MODULE_OUTPUT_PATH, List.of());
        serviceDefinitions.add(interfaceModule);
        for (Microservice microserviceInput : serviceCut.getMicroservices()) {
            ServiceDefinition microservice = new ServiceDefinition(ServiceType.MICROSERVICE, microserviceInput.getName().toLowerCase(), Path.of(DEFAULT_OUTPUT_PATH.toString() + "/" + microserviceInput.getName().toLowerCase() + "/"), microserviceInput.getIdentifiedClasses());
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

        currentTime = System.currentTimeMillis();
        System.out.println("Generating required classes and additional files for new partitions. Time passed: " + (currentTime - startTime) / 1000.0 + " seconds");
        DataGenerator2 dataGenerator = new DataGenerator2(codebaseFactory, originalPom);
        GeneratedData2 generatedData = dataGenerator.generate(serviceCut.getMicroservices());
        List<Writable2> writables = generatedData.getWritables();
        System.out.println();

        currentTime = System.currentTimeMillis();
        System.out.println("Generating data writers. Time passed: " + (currentTime - startTime) / 1000.0 + " seconds");
        List<DataWriter2> dataWriters = new ArrayList<>();
        for (Writable2 writable2 : writables) {
            dataWriters.add(writable2.createWriter());
        }
        System.out.println();

        currentTime = System.currentTimeMillis();
        System.out.println("Writing partitions. Time passed: " + (currentTime - startTime) / 1000.0 + " seconds");
        // TODO We could make a verifier before writing, no clashing: multiple writers wanting to write to same file
        for (ServiceDefinition serviceDefinition : serviceDefinitions) {
            System.out.println("Writing to service: " + serviceDefinition.getName());
            for (DataWriter2 dataWriter : dataWriters) {
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

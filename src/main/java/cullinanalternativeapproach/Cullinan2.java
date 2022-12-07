package cullinanalternativeapproach;

import spoonhelpers.managers.SpoonFactoryManager;

import java.util.List;

public class Cullinan2 {

    private static final String DECOMPOSITION_INPUT_JSON = "decomposition_input.json";
    private static final String SOURCE_PATH_ROOT = "dddsample-core-master";
    private static final String SOURCE_PATH_JAVA = SOURCE_PATH_ROOT + "/src/main/java";
    private static final String DEFAULT_OUTPUT_PATH = SOURCE_PATH_ROOT + "_decomposed/";
    private static final String MAIN_SERVICE_OUTPUT_PATH = DEFAULT_OUTPUT_PATH + "monolith/";
    private static final String INTERFACES_MODULE_OUTPUT_PATH = DEFAULT_OUTPUT_PATH + "serviceinterfaces/";
    private static final String LOCATION_OUTPUT_PATH = DEFAULT_OUTPUT_PATH + "location/";

    public static void main(String[] args) {
        Writable2 proxy = new Proxy(SpoonFactoryManager.getDefaultFactory().createClass("test"));
        DataWriter2 writer = proxy.createWriter();
        List<DataWriter2> dataWriters = List.of(writer);

        ServiceDefinition main = new ServiceDefinition(ServiceType.MAIN_SERVICE, "main", MAIN_SERVICE_OUTPUT_PATH, List.of("Application"));
        ServiceDefinition interfaceModule = new ServiceDefinition(ServiceType.INTERFACE_MODULE, "clientinterfaces", INTERFACES_MODULE_OUTPUT_PATH, List.of());
        ServiceDefinition microservice = new ServiceDefinition(ServiceType.MICROSERVICE, "location", LOCATION_OUTPUT_PATH, List.of("Location"));
        List<ServiceDefinition> serviceDefinitions = List.of(main, interfaceModule, microservice);

        writer.write(main);
        writer.write(interfaceModule);
        writer.write(microservice);

         for (ServiceDefinition serviceDefinition : serviceDefinitions) {
             for (DataWriter2 dataWriter : dataWriters) {
                  dataWriter.write(serviceDefinition);
             }
         }
    }
}

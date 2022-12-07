package cullinan.helpers.decomposition.servicecreators;

import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.JavaWriter;
import cullinan.helpers.decomposition.generators.model.GeneratedData;
import input.Microservice;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MicroserviceCreator {
    private final GeneratedData generatedData;
    private final Microservice microservice;

    private final List<DataWriter> writers = new ArrayList<>();

    public MicroserviceCreator(GeneratedData generatedData, Microservice microservice) {
        this.generatedData = generatedData;
        this.microservice = microservice;

        generateWriters();
    }

    private void generateWriters() {
        JavaWriter serializationUtilWriter = new JavaWriter(generatedData.getSerializationUtil());
        writers.add(serializationUtilWriter);

        JavaWriter referenceIdWriter = new JavaWriter(generatedData.getReferenceId());
        writers.add(referenceIdWriter);

        JavaWriter referenceInterfaceWriter = new JavaWriter(generatedData.getReferenceInterface());
        writers.add(referenceInterfaceWriter);

        JavaWriter storageWriter = new JavaWriter(generatedData.getStorageClass());
        writers.add(storageWriter);

        for (String className : microservice.getIdentifiedClasses()) {
            JavaWriter originalWriter = new JavaWriter(generatedData.getOriginal(className));
            writers.add(originalWriter);

            JavaWriter classImplementationWriter = new JavaWriter(generatedData.getServiceImplementation(className));
            writers.add(classImplementationWriter);
        }

        JavaWriter mainWriter = new JavaWriter(generatedData.getMain(microservice.getName()));
        writers.add(mainWriter);

        // TODO Make sure these are generated...
        JavaWriter classDefinitionsWriter = new JavaWriter(generatedData.getClassDefinitions(microservice.getName()));
        writers.add(classDefinitionsWriter);

        generatedData.getFilteredProxies(microservice.getIdentifiedClasses()).stream()
                .map(JavaWriter::new)
                .forEach(writers::add);

        generatedData.getFilteredClients(microservice.getIdentifiedClasses()).stream()
                .map(JavaWriter::new)
                .forEach(writers::add);

        generatedData.getFilteredNotImplementedTypes(microservice.getIdentifiedClasses()).stream()
                .map(JavaWriter::new)
                .forEach(writers::add);

        // TODO POM
        // TODO Probably better ordering.
    }

    public void create(Path path) {
        // 1. Clean up anything existing. TODO

        // 2. Modify the code base according to the split we have decided upon.
        writers.forEach(writer -> writer.write(path));
    }

    public String getServiceName() {
        return microservice.getName();
    }
}

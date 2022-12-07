package cullinan.helpers.decomposition.servicecreators;

import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.JavaWriter;
import cullinan.helpers.decomposition.generators.model.GeneratedData;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Creates the "Monolith" that remains. As monolith is split up, "MonolithCreator" does not seem apt for naming.
public class MainServiceCreator {
    private final GeneratedData generatedData;
    private final List<DataWriter> writers = new ArrayList<>();
    private static final String MAIN_SERVICE_NAME = "main"; // TODO Just provide the original name...

    public MainServiceCreator(GeneratedData generatedData) {
        this.generatedData = generatedData;

        generateWriters();
    }

    // TODO These writers are pretty much identical... Maybe we need a SpoonWriter or something?
    private void generateWriters() {
        JavaWriter serializationUtilWriter = new JavaWriter(generatedData.getSerializationUtil());
        writers.add(serializationUtilWriter);

        JavaWriter referenceIdWriter = new JavaWriter(generatedData.getReferenceId());
        writers.add(referenceIdWriter);

        JavaWriter referenceInterfaceWriter = new JavaWriter(generatedData.getReferenceInterface());
        writers.add(referenceInterfaceWriter);

        // TODO Make sure these are generated...
        JavaWriter classDefinitionsWriter = new JavaWriter(generatedData.getClassDefinitions(MAIN_SERVICE_NAME));
        writers.add(classDefinitionsWriter);

        generatedData.getProxies().stream()
                .map(JavaWriter::new)
                .forEach(writers::add);

        generatedData.getClients().stream()
                .map(JavaWriter::new)
                .forEach(writers::add);
    }

    public void create(Path path) {
        // 1. Clean up anything existing. TODO WAIT HOW IS THIS DONE ALREADY? We do it in Cullinan.java?
        // 2. Initiate with a clean copy of the original code base. TODO

        // 3. Modify the code base according to the split we have decided upon.
        writers.forEach(writer -> writer.write(path));
    }
}

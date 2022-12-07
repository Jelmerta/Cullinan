package cullinan.helpers.decomposition.servicecreators;

import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.JavaWriter;
import cullinan.helpers.decomposition.generators.model.GeneratedData;
import spoon.reflect.declaration.CtInterface;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class InterfaceModuleCreator {
    private final GeneratedData generatedData;
    private final List<DataWriter> writers = new ArrayList<>();

    public InterfaceModuleCreator(GeneratedData generatedData) {
        this.generatedData = generatedData;
        generateWriters();
    }

    private void generateWriters() {
        for (CtInterface serviceInterface : generatedData.getInterfaces()) {
            JavaWriter interfaceWriter = new JavaWriter(serviceInterface);
            writers.add(interfaceWriter);
        }

        // TODO POM
    }

    public void create(Path path) {
        // 1. Clean up anything existing. TODO

        // 2. Modify the code base according to the split we have decided upon.
        writers.forEach(writer -> writer.write(path));
    }
}

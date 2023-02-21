package generatedfiles;

import cullinan.helpers.decomposition.javagenerators.ClassDefinitionsCreator;
import writers.ClassDefinitionsWriter;
import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.WriteDefinition;
import spoon.reflect.declaration.CtClass;

import java.util.List;

public class ClassDefinitions implements Writable {
    private final CtClass java;
    private final WriteDefinition writeDefinition;

    public ClassDefinitions(WriteDefinition writeDefinition, List<String> serviceClassDefinitions, List<String> proxyClassDefinitions) {
        ClassDefinitionsCreator classDefinitionsCreator = new ClassDefinitionsCreator(serviceClassDefinitions, proxyClassDefinitions);
        this.java = classDefinitionsCreator.build();
        this.writeDefinition = writeDefinition;
    }

    public CtClass getJava() {
        return java;
    }

    @Override
    public DataWriter createWriter() {
        return new ClassDefinitionsWriter(this, writeDefinition);
    }
}

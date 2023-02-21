package cullinanalternativeapproach;

import cullinan.helpers.decomposition.javagenerators.ClassDefinitionsCreator;
import spoon.reflect.declaration.CtClass;

import java.util.List;

public class ClassDefinitions implements Writable2 {
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
    public DataWriter2 createWriter() {
        return new ClassDefinitionsWriter(this, writeDefinition);
    }
}

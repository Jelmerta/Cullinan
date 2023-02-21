package cullinanalternativeapproach;

import cullinan.helpers.decomposition.writers.JavaWriter2;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

import java.util.List;

public class ServiceWritable implements Writable2 {
    public static final List<WriteDefinition> WRITE_DEFINITIONS = List.of(WriteDefinition.THIS_MICROSERVICE);
    private final CtClass java;

    public ServiceWritable(CtClass java) {
        this.java = java;
    }

    @Override
    public JavaWriter2 createWriter() {
        return new JavaWriter2(java, WRITE_DEFINITIONS);
    }

    public CtType getJava() {
        return java;
    }

    public List<WriteDefinition> getWriteDefinition() {
        return WRITE_DEFINITIONS;
    }
}

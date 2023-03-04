package generatedfiles;

import cullinan.helpers.decomposition.javagenerators.SerializationUtilCreator;
import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.SerializationUtilWriter;
import spoon.reflect.declaration.CtClass;

public class SerializationUtil implements Writable {
    private final CtClass java;

    public SerializationUtil() {
        SerializationUtilCreator serializationUtilCreator = new SerializationUtilCreator();
        this.java = serializationUtilCreator.build();
    }

    public CtClass getJava() {
        return java;
    }

    @Override
    public DataWriter createWriter() {
        return new SerializationUtilWriter(this);
    }
}

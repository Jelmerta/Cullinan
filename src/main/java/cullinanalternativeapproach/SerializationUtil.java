package cullinanalternativeapproach;

import cullinan.helpers.decomposition.javagenerators.SerializationUtilCreator;
import spoon.reflect.declaration.CtClass;

public class SerializationUtil implements Writable2 {
    private final CtClass java;

    public SerializationUtil() {
        SerializationUtilCreator serializationUtilCreator = new SerializationUtilCreator();
        this.java = serializationUtilCreator.build();
    }

    public CtClass getJava() {
        return java;
    }

    @Override
    public DataWriter2 createWriter() {
        return new SerializationUtilWriter(this);
    }
}

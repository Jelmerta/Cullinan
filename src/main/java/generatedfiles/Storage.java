package generatedfiles;

import cullinan.helpers.decomposition.javagenerators.StorageManagerCreator;
import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.StorageWriter;
import spoon.reflect.declaration.CtClass;

public class Storage implements Writable {
    private final CtClass java;

    public Storage() {
        StorageManagerCreator storageManagerCreator = new StorageManagerCreator();
        this.java = storageManagerCreator.build();
    }

    public CtClass getJava() {
        return java;
    }

    @Override
    public DataWriter createWriter() {
        return new StorageWriter(this);
    }
}

package cullinanalternativeapproach;

import cullinan.helpers.decomposition.javagenerators.StorageManagerCreator;
import spoon.reflect.declaration.CtClass;

public class Storage implements Writable2 {
    private final CtClass java;

    public Storage() {
        StorageManagerCreator storageManagerCreator = new StorageManagerCreator();
        this.java = storageManagerCreator.build();
    }

    public CtClass getJava() {
        return java;
    }

    @Override
    public DataWriter2 createWriter() {
        return new StorageWriter(this);
    }
}

package cullinanalternativeapproach;

import cullinan.helpers.decomposition.javagenerators.CullinanIdCreator;
import spoon.reflect.declaration.CtClass;

public class ReferenceId implements Writable2 {
    private final CtClass java;

    public ReferenceId() {
        CullinanIdCreator referenceIdCreator = new CullinanIdCreator();
        this.java = referenceIdCreator.build();
    }

    public CtClass getJava() {
        return java;
    }

    @Override
    public DataWriter2 createWriter() {
        return new ReferenceIdWriter(this);
    }
}

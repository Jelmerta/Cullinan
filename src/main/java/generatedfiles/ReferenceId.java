package generatedfiles;

import cullinan.helpers.decomposition.javagenerators.CullinanIdCreator;
import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.ReferenceIdWriter;
import spoon.reflect.declaration.CtClass;

public class ReferenceId implements Writable {
    private final CtClass java;

    public ReferenceId() {
        CullinanIdCreator referenceIdCreator = new CullinanIdCreator();
        this.java = referenceIdCreator.build();
    }

    public CtClass getJava() {
        return java;
    }

    @Override
    public DataWriter createWriter() {
        return new ReferenceIdWriter(this);
    }
}

package generatedfiles;

import cullinan.helpers.decomposition.javagenerators.ReferenceInterfaceCreator;
import cullinan.helpers.decomposition.writers.DataWriter;
import writers.ReferenceInterfaceWriter;
import spoon.reflect.declaration.CtInterface;

public class ReferenceInterface implements Writable {
    private final CtInterface java;

    public ReferenceInterface() {
        ReferenceInterfaceCreator referenceInterfaceCreator = new ReferenceInterfaceCreator();
        this.java = referenceInterfaceCreator.build();
    }

    public CtInterface getJava() {
        return java;
    }

    @Override
    public DataWriter createWriter() {
        return new ReferenceInterfaceWriter(this);
    }
}

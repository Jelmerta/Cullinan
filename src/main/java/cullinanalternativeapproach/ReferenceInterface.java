package cullinanalternativeapproach;

import cullinan.helpers.decomposition.javagenerators.ReferenceInterfaceCreator;
import spoon.reflect.declaration.CtInterface;

public class ReferenceInterface implements Writable2 {
    private final CtInterface java;

    public ReferenceInterface() {
        ReferenceInterfaceCreator referenceInterfaceCreator = new ReferenceInterfaceCreator();
        this.java = referenceInterfaceCreator.build();
    }

    public CtInterface getJava() {
        return java;
    }

    @Override
    public DataWriter2 createWriter() {
        return new ReferenceInterfaceWriter(this);
    }
}

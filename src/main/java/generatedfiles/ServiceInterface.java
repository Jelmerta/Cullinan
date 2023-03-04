package generatedfiles;

import cullinan.helpers.decomposition.javagenerators.ServiceInterfaceCreator;
import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.InterfaceWriter;
import spoon.reflect.declaration.CtInterface;

public class ServiceInterface implements Writable {
//    private final String microserviceName; // TODO Maybe this should be a Service Definition?
    private final CtInterface java;

    public ServiceInterface(OriginalJava original) {
        ServiceInterfaceCreator serviceInterfaceCreator = new ServiceInterfaceCreator(original.getJava());
        this.java = serviceInterfaceCreator.buildInterface();
    }

    public CtInterface getJava() {
        return java;
    }

    @Override
    public DataWriter createWriter() {
        return new InterfaceWriter(this);
    }
}

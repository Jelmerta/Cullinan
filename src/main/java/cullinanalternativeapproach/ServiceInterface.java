package cullinanalternativeapproach;

import cullinan.helpers.decomposition.javagenerators.ServiceInterfaceCreator;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;

public class ServiceInterface implements Writable2 {
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
    public DataWriter2 createWriter() {
        return new InterfaceWriter(this);
    }
}

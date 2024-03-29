package generatedfiles;

import cullinan.helpers.decomposition.javagenerators.ServiceCreator;
import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.ServiceWriter;
import spoon.reflect.declaration.CtClass;

public class Service implements Writable {
    private final CtClass java;
    private String serviceOrigin;

    public Service(OriginalJava originalJava, ServiceInterface serviceInterface, Storage storage, SerializationUtil serializationUtil, Implementation implementation) {
        ServiceCreator serviceCreator = new ServiceCreator(originalJava.getJava(), serviceInterface.getJava(), storage.getJava(), serializationUtil.getJava(), implementation.getJava());
        this.java = serviceCreator.build();
        this.serviceOrigin = originalJava.getServiceOrigin();
    }

    public CtClass getJava() {
        return java;
    }

    @Override
    public DataWriter createWriter() {
        return new ServiceWriter(this);
    }

    public String getServiceOrigin() {
        return serviceOrigin;
    }
}

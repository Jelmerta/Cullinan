package cullinanalternativeapproach;

import cullinan.helpers.decomposition.javagenerators.ClientCreator;
import spoon.reflect.declaration.CtClass;

public class Client implements Writable2 {
    private final CtClass java;
    private final String serviceOrigin;

    public Client(OriginalJava originalJava, ServiceInterface serviceInterface, SerializationUtil serializationUtil) {
        ClientCreator clientCreator = new ClientCreator(originalJava.getJava(), serviceInterface.getJava(), serializationUtil.getJava());
        this.java = clientCreator.build();
        this.serviceOrigin = originalJava.getServiceOrigin();
    }

    public CtClass getJava() {
        return java;
    }

    @Override
    public DataWriter2 createWriter() {
        return new ClientWriter(this, serviceOrigin);
    }
}

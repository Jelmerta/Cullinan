package generatedfiles;

import cullinan.helpers.decomposition.javagenerators.ClientCreator;
import cullinan.helpers.decomposition.writers.ClientWriter;
import cullinan.helpers.decomposition.writers.DataWriter;
import spoon.reflect.declaration.CtClass;

public class Client implements Writable {
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
    public DataWriter createWriter() {
        return new ClientWriter(this, serviceOrigin);
    }
}

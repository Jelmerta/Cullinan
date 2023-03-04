package generatedfiles;

import cullinan.helpers.decomposition.javagenerators.ProxyCreator;
import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.ProxyWriter;
import spoon.reflect.declaration.CtClass;

public class Proxy implements Writable {
    private final CtClass java;
    private final String serviceOrigin; // Could be ServiceDefinition?

    public Proxy(OriginalJava originalJava, ReferenceInterface referenceInterface, Client client, ReferenceId referenceId, SerializationUtil serializationUtil) {
        ProxyCreator proxyCreator = new ProxyCreator(originalJava.getJava(), referenceInterface.getJava(), client.getJava(), referenceId.getJava(), serializationUtil);
        this.java = proxyCreator.build();
        this.serviceOrigin = originalJava.getServiceOrigin();
    }

    public CtClass getJava() {
        return java;
    }

    @Override
    public DataWriter createWriter() {
        return new ProxyWriter(this);
    }

    public String getServiceOrigin() {
        return serviceOrigin;
    }
}

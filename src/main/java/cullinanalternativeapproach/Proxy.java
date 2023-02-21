package cullinanalternativeapproach;

import cullinan.helpers.decomposition.javagenerators.ProxyCreator;
import spoon.reflect.declaration.CtClass;

public class Proxy implements Writable2 {
    private final CtClass java;
    private final String serviceOrigin; // Could be ServiceDefinition?

    public Proxy(OriginalJava originalJava, ReferenceInterface referenceInterface, Client client, SerializationUtil serializationUtil) {
        ProxyCreator proxyCreator = new ProxyCreator(originalJava.getJava(), referenceInterface.getJava(), client.getJava(), serializationUtil.getJava());
        this.java = proxyCreator.build();
        this.serviceOrigin = originalJava.getServiceOrigin();
    }

    public CtClass getJava() {
        return java;
    }

    @Override
    public DataWriter2 createWriter() {
        return new ProxyWriter(this);
    }

    public String getServiceOrigin() {
        return serviceOrigin;
    }
}

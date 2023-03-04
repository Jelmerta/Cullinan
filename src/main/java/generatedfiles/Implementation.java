package generatedfiles;

import cullinan.helpers.decomposition.javagenerators.ServiceOriginalClassWithIdCreator;
import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.ImplementationWriter;
import spoon.reflect.declaration.CtClass;

// Basically the original class: but with a reference id added to refer to it in other services using their proxies. When constructed, added to an in-mem database for now.private java.util.Map<spoon.reflect.declaration.CtClass,spoon.reflect.declaration.CtInterface> interfaces;private java.util.Map<spoon.reflect.declaration.CtClass,spoon.reflect.declaration.CtClass> services;private java.util.Map<spoon.reflect.declaration.CtClass,spoon.reflect.declaration.CtClass> clients;private final java.util.Map<spoon.reflect.declaration.CtClass,spoon.reflect.declaration.CtClass> proxies; // Proxies are a little special: they need to be generated before all services are written as all services can make use of the proxy. Not used in the resulting service.
public class Implementation implements Writable {
    private final CtClass java;
    private String serviceOrigin;

    public Implementation(OriginalJava original, ReferenceInterface referenceInterface) {
        ServiceOriginalClassWithIdCreator serviceOriginalClassWithIdCreator = new ServiceOriginalClassWithIdCreator(original.getJava(), referenceInterface.getJava());
        this.java = serviceOriginalClassWithIdCreator.build();
        this.serviceOrigin = original.getServiceOrigin();
    }

    public CtClass getJava() {
        return java;
    }

    @Override
    public DataWriter createWriter() {
        return new ImplementationWriter(this);
    }

    public String getServiceOrigin() {
        return serviceOrigin;
    }
}

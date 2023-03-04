package generatedfiles;

import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.javagenerators.ServiceMainCreator;
import spoon.reflect.declaration.CtClass;
import cullinan.helpers.decomposition.writers.MicroserviceMainWriter;

import java.util.Collection;
import java.util.stream.Collectors;

public class MicroserviceMain implements Writable {
    private final CtClass java;
    private final String serviceOrigin;

    public MicroserviceMain(Collection<Service> services, String serviceOrigin) {
        ServiceMainCreator serviceMainCreator = new ServiceMainCreator(services.stream().map(Service::getJava).collect(Collectors.toList()));
        this.java = serviceMainCreator.build();
        this.serviceOrigin = serviceOrigin;
    }

    public CtClass getJava() {
        return java;
    }

    @Override
    public DataWriter createWriter() {
        return new MicroserviceMainWriter(this, serviceOrigin);
    }
}

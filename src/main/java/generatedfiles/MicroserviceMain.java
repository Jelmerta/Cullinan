package generatedfiles;

import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.javagenerators.ServiceMainCreator;
import spoon.reflect.declaration.CtClass;
import writers.MicroserviceMainWriter;

import java.util.Collection;
import java.util.stream.Collectors;

public class MicroserviceMain implements Writable {
    private final CtClass java;

    public MicroserviceMain(Collection<Service> services) {
        ServiceMainCreator serviceMainCreator = new ServiceMainCreator(services.stream().map(Service::getJava).collect(Collectors.toList()));
        this.java = serviceMainCreator.build();
    }

    public CtClass getJava() {
        return java;
    }

    @Override
    public DataWriter createWriter() {
        return new MicroserviceMainWriter(this);
    }
}

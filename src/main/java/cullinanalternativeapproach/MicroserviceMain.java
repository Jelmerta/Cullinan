package cullinanalternativeapproach;

import spoon.reflect.declaration.CtClass;

import java.util.Collection;
import java.util.stream.Collectors;

public class MicroserviceMain implements Writable2 {
    private final CtClass java;

    public MicroserviceMain(Collection<Service> services) {
        ServiceMainCreator2 serviceMainCreator = new ServiceMainCreator2(services.stream().map(Service::getJava).collect(Collectors.toList()));
        this.java = serviceMainCreator.build();
    }

    public CtClass getJava() {
        return java;
    }

    @Override
    public DataWriter2 createWriter() {
        return new MicroserviceMainWriter(this);
    }
}

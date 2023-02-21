package cullinan.helpers.decomposition.generators.model;

import generatedfiles.ServiceInterfacePom;

public class GeneratedInterfaceServiceLevel {
    private ServiceInterfacePom serviceInterfacePom;

    public GeneratedInterfaceServiceLevel() {
    }

    public void addPom(ServiceInterfacePom serviceInterfacePom) {
        this.serviceInterfacePom =  serviceInterfacePom;
    }

    public ServiceInterfacePom getServiceInterfacePom() {
        return serviceInterfacePom;
    }
}
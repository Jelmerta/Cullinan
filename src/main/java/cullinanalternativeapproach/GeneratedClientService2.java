package cullinanalternativeapproach;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;

public class GeneratedClientService2 {
    private ServiceInterface serviceInterface;
    private Client client;
    private Service service;

    public GeneratedClientService2() {
    }

    public void addServiceInterface(ServiceInterface serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public void addClient(Client client) {
        this.client = client;
    }

    public void addService(Service service) {
        this.service = service;
    }

    public ServiceInterface getInterface() {
        return serviceInterface;
    }

    public Service getService() {
        return service;
    }

    public Client getClient() {
        return client;
    }
}
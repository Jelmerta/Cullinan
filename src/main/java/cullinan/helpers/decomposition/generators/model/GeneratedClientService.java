package cullinan.helpers.decomposition.generators.model;

import generatedfiles.Client;
import generatedfiles.Service;
import generatedfiles.ServiceInterface;

public class GeneratedClientService {
    private ServiceInterface serviceInterface;
    private Client client;
    private Service service;

    public GeneratedClientService() {
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
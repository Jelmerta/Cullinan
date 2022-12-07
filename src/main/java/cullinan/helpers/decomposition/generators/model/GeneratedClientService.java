package cullinan.helpers.decomposition.generators.model;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;

import java.util.Collection;

public class GeneratedClientService {
    private CtInterface serviceInterface;
    private CtClass client;
    private CtClass service;

    public GeneratedClientService() {
    }

    public void addServiceInterface(CtInterface serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public void addClient(CtClass client) {
        this.client = client;
    }

    public void addService(CtClass service) {
        this.service = service;
    }

    public CtInterface getInterface() {
        return serviceInterface;
    }

    public CtClass getService() {
        return service;
    }

    public CtClass getClient() {
        return client;
    }
}
package cullinan.helpers.decomposition.generators.model;

import generatedfiles.Implementation;
import generatedfiles.Proxy;
import generatedfiles.Writable;

import java.util.Collection;
import java.util.List;

// TODO Maybe we should just put the original class? Many nullable fields...
public class GeneratedClassLevel {
    private final String originalFullyQualifiedClassname;
    private final Implementation implementation;
    private final GeneratedClientService generatedClientService;
    private final Proxy proxy;

    public GeneratedClassLevel(String originalFullyQualifiedClassname, Implementation implementation, GeneratedClientService generatedClientService, Proxy proxy) {
        this.originalFullyQualifiedClassname = originalFullyQualifiedClassname;
        this.implementation = implementation;
        this.generatedClientService = generatedClientService;
        this.proxy = proxy;
    }

    public String getOriginalFullyQualifiedClassname() {
        return originalFullyQualifiedClassname;
    }

    public GeneratedClientService getGeneratedClientService() {
        return generatedClientService;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public Implementation getImplementation() {
        return implementation;
    }

    public Collection<Writable> getAllWritables() {
        return List.of(implementation, generatedClientService.getInterface(), generatedClientService.getService(), generatedClientService.getClient(), proxy);
    }
}
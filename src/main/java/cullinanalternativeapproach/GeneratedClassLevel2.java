package cullinanalternativeapproach;

import cullinan.helpers.decomposition.generators.model.GeneratedClientService;

import java.util.Collection;
import java.util.List;

// TODO Maybe we should just put the original class? Many nullable fields...
public class GeneratedClassLevel2 {
    private final String originalFullyQualifiedClassname;
    private final Implementation implementation;
    private final GeneratedClientService2 generatedClientService;
    private final Proxy proxy;

    public GeneratedClassLevel2(String originalFullyQualifiedClassname, Implementation implementation, GeneratedClientService2 generatedClientService, Proxy proxy) {
        this.originalFullyQualifiedClassname = originalFullyQualifiedClassname;
        this.implementation = implementation;
        this.generatedClientService = generatedClientService;
        this.proxy = proxy;
    }

    public String getOriginalFullyQualifiedClassname() {
        return originalFullyQualifiedClassname;
    }

    public GeneratedClientService2 getGeneratedClientService() {
        return generatedClientService;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public Implementation getImplementation() {
        return implementation;
    }

    public Collection<Writable2> getAllWritables() {
        return List.of(implementation, generatedClientService.getInterface(), generatedClientService.getService(), generatedClientService.getClient(), proxy);
    }
}
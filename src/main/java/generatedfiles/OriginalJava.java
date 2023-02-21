package generatedfiles;

import input.Microservice;
import spoon.reflect.declaration.CtClass;

public class OriginalJava {
    private final CtClass java; // TODO CtType?
    private final String serviceOrigin;

    public OriginalJava(CtClass original, Microservice microservice) {
        this.java = original;
        this.serviceOrigin = microservice.getName();
    }

    public CtClass getJava() {
        return java;
    }

    public String getServiceOrigin() {
        return serviceOrigin;
    }
}

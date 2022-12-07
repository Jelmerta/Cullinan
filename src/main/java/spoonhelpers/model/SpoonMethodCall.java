package spoonhelpers.model;

import spoon.reflect.reference.CtTypeReference;

public class SpoonMethodCall {
    private CtTypeReference type;
    private String methodName;

    public CtTypeReference getType() {
        return type;
    }

    public String getMethodName() {
        return methodName;
    }
}

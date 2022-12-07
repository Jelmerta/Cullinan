package spoonhelpers.model;

import spoon.reflect.reference.CtTypeReference;

public class SpoonParameter {
    private CtTypeReference type;
    private String name;
    private boolean isServiceClass;

    private SpoonParameter(Builder builder) {
        this.type = builder.type;
        this.name = builder.name;
        this.isServiceClass = builder.isServiceClass;
    }

    public static Builder newBuilder(CtTypeReference type, String name, boolean isServiceClass) {
        return new SpoonParameter.Builder(type, name, isServiceClass);
    }

    public CtTypeReference getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setType(CtTypeReference type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isServiceClass() {
        return isServiceClass;
    }

    public static class Builder {
        private final CtTypeReference type;
        private final String name;
        public boolean isServiceClass;

        public Builder(CtTypeReference type, String name, boolean isServiceClass) {
            this.type = type;
            this.name = name;
            this.isServiceClass = isServiceClass;
        }

        public SpoonParameter build() {
            return new SpoonParameter(this);
        }
    }
}

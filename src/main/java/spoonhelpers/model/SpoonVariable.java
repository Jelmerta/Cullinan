package spoonhelpers.model;

import spoon.reflect.reference.CtTypeReference;

public class SpoonVariable {
    private String name;
    private CtTypeReference type;

    private SpoonVariable(Builder builder) {
        this.type = builder.type;
        this.name = builder.name;
    }

    public static Builder newBuilder(CtTypeReference type, String name) {
        return new Builder(type, name);
    }

    public static class Builder {
        private CtTypeReference type;
        private String name;

        private Builder(CtTypeReference type, String name) {
            this.type = type;
            this.name = name;
        }

        public SpoonVariable build() {
            return new SpoonVariable(this);
        }
    }
}

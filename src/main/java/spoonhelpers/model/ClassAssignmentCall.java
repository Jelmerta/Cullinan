package spoonhelpers.model;

import spoon.reflect.reference.CtTypeReference;

// TODO Can contain parameters
public class ClassAssignmentCall {
    private CtTypeReference className;
    private String methodName;

    private ClassAssignmentCall(Builder builder) {
        this.className = builder.className;
        this.methodName = builder.methodName;
    }

    public static Builder newBuilder(CtTypeReference className, String methodName) {
        return new Builder(className, methodName);
    }

    private static class Builder {
        private CtTypeReference className;
        private String methodName;

        private Builder(CtTypeReference className, String methodName) {
            this.className = className;
            this.methodName = methodName;
        }

        public ClassAssignmentCall build() {
            return new ClassAssignmentCall(this);
        }
    }
}

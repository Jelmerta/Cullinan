package spoonhelpers.model;

import spoon.reflect.declaration.CtField;
import spoon.reflect.reference.CtTypeReference;

// TODO Isn't this the same thing as an assignment?
public class SpoonField {
    private CtField field;

    private CtTypeReference type;
    private String name;
    private SpoonAccessModifier accessModifier;
    private boolean isStatic;

    private SpoonClass assignedClass = null;

    private SpoonField(Builder builder) {
        this.type = builder.type;
        this.name = builder.name;
        this.accessModifier = builder.accessModifier;
        this.isStatic = builder.isStatic;
    }

    public static Builder newBuilder(CtTypeReference type, String name, SpoonAccessModifier accessModifier) {
        return new Builder(type, name, accessModifier);
    }

    public SpoonAccessModifier getAccessModifier() {
        return accessModifier;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public CtTypeReference getType() {
        return type;
    }

    public String getName() {
        return name;
    }
//
//    public void setAssignedClass(SpoonClass builder) {
//
//    }
//
//    public SpoonMethodCall makeCall(String methodName, SpoonParameter spoonParameter) {
//
//
//    }

//    public CtVariableReference getReference() {
//        return ;
//    }

    public static class Builder {
        private final CtTypeReference type;
        private final String name;
        private final SpoonAccessModifier accessModifier;
        private boolean isStatic = false;

        public Builder(CtTypeReference type, String name, SpoonAccessModifier accessModifier) {
            this.type = type;
            this.name = name;
            this.accessModifier = accessModifier;
        }

        public SpoonField build() {
            return new SpoonField(this);
        }

        public void makeStatic() {
            this.isStatic = true;
        }
    }
}

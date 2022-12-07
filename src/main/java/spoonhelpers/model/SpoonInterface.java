package spoonhelpers.model;

import spoon.reflect.declaration.CtPackage;

import java.util.ArrayList;
import java.util.List;

public class SpoonInterface {
    private final String ctPackage;
    private final Class superClass;
    private final SpoonAccessModifier accessModifier;
    private String name;
    private List<SpoonMethod> methods;

    private SpoonInterface(Builder builder) {
        this.name = builder.name;
        this.methods = builder.methods;
        this.superClass = builder.superClass;
        this.accessModifier = builder.accessModifier;
        this.ctPackage = builder.ctPackage;
    }

    public static Builder builder(String ctPackage, String className, SpoonAccessModifier accessModifier) {
        return new Builder(ctPackage, className, accessModifier);
    }

    public String getName() {
        return name;
    }

    public List<SpoonMethod> getMethods() {
        return methods;
    }

    public Class getSuperclass() {
        return superClass;
    }

    public SpoonAccessModifier getModifierAccess() {
        return accessModifier;
    }

    public String getPackage() {
        return ctPackage;
    }

    public static class Builder {
        private final SpoonAccessModifier accessModifier;
        private String ctPackage;
        // Required fields
        private String name;

        // Optional fields
        private List<SpoonMethod> methods = new ArrayList<>();
        private Class superClass;

        public Builder(String ctPackage, String name, SpoonAccessModifier accessModifier) {
            this.ctPackage = ctPackage;
            this.name = name;
            this.accessModifier = accessModifier;
        }

        public SpoonInterface build() {
            return new SpoonInterface(this);
        }

        public Builder addMethod(SpoonMethod method) {
            this.methods.add(method);
            return this;
        }

        public Builder extendsClass(Class superClass) {
            this.superClass = superClass;
            return this;
        }
    }
}

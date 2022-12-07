package spoonhelpers.model;

import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoonhelpers.managers.SpoonFieldManager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SpoonClass {
    private static Factory factory;

    static {
        factory = buildProject(Path.of("./src/test/resources/SimpleClass.java"));
        setupEnvironment(factory);
    }

    private static Factory buildProject (Path project){
        Launcher launcher = new Launcher();
        launcher.addInputResource(project.toString());
        launcher.buildModel();
        return launcher.getFactory();
    }

    private static void setupEnvironment (Factory factory){
        Environment environment = factory.getEnvironment();
        environment.setAutoImports(true);
        environment.setNoClasspath(true); // ?
        environment.setCommentEnabled(true); // Required?
    }






    private final SpoonAccessModifier accessModifier;
    private final CtTypeReference superclass;
    private final List<SpoonInterface> superInterfaces;
    private String name;
    private List<SpoonMethod> methods;
    private List<CtField> fields;
    private CtConstructor constructor;

    private SpoonClass(Builder builder) {
        this.name = builder.name;
        this.methods = builder.methods;
        this.accessModifier = builder.accessModifier;
        this.superclass = builder.superclass;
        this.superInterfaces = builder.superInterfaces;
        this.fields = builder.fields;
        this.constructor = builder.constructor;
    }

    public static Builder newBuilder(String className, SpoonAccessModifier accessModifier) {
        return new Builder(className, accessModifier);
    }

    public String getName() {
        return name;
    }

    public List<SpoonMethod> getMethods() {
        return methods;
    }

    public SpoonAccessModifier getModifierAccess() {
        return accessModifier;
    }

    public CtConstructor getConstructor() {
        return constructor;
    }

    public List<CtField> getFields() {
        return fields;
    }

    public void addField(CtField spoonField) {
//        spoonField.setAssignedClass(this);
        this.fields.add(spoonField);
    }

    public static class Builder {

        private CtConstructor constructor;
        // Required fields
        private String name;
        private final SpoonAccessModifier accessModifier;

        // Optional fields
        private List<SpoonMethod> methods = new ArrayList<>();
        private CtTypeReference superclass;
        private List<SpoonInterface> superInterfaces = new ArrayList<>();
        private List<CtField> fields = new ArrayList<>();

        private Builder(String name, SpoonAccessModifier accessModifier) {
            this.name = name;
            this.accessModifier = accessModifier;
        }

        public SpoonClass build() {
            return new SpoonClass(this);
        }

        public Builder addMethod(SpoonMethod method) {
            this.methods.add(method);
            return this;
        }

        public Builder setSuperclass(CtTypeReference superclass) {
            this.superclass = superclass;
            return this;
        }

        public Builder addSuperInterface(SpoonInterface superInterface) {
            this.superInterfaces.add(superInterface);
            return this;
        }

//        public Builder addField(SpoonField field) {
//            this.fields.add(field);
//            return this;
//        }

        public void addConstructor() {
            CtConstructor constructor = factory.createConstructor();
            constructor.setSimpleName(name);
            constructor.addModifier(ModifierKind.PRIVATE);
            CtCodeSnippetStatement emptyStatement = factory.createCodeSnippetStatement("");
            constructor.setBody(emptyStatement);
            this.constructor = constructor;
        }

        public CtConstructor getConstructor() {
            return constructor;
        }
    }
}

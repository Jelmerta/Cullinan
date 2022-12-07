package spoonhelpers.model;

import spoon.Launcher;
import spoon.compiler.Environment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtReturn;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SpoonMethod {
    private static Factory factory;

    static {
        factory = buildProject(Path.of("./src/test/resources/SimpleClass.java"));
        setupEnvironment(factory);
    }

    private static Factory buildProject(Path project) {
        Launcher launcher = new Launcher();
        launcher.addInputResource(project.toString());
        launcher.buildModel();
        return launcher.getFactory();
    }

    private static void setupEnvironment(Factory factory) {
        Environment environment = factory.getEnvironment();
        environment.setAutoImports(true);
        environment.setNoClasspath(true); // ?
        environment.setCommentEnabled(true); // Required?
    }

    private final boolean isStatic;
    private final List<CtTypeReference> exceptionClasses;
    private final List<SpoonParameter> parameters;
    private final List<CtTypeReference> annotations;
    private String name;
    private SpoonAccessModifier accessModifier;
    private CtTypeReference returnType;
    private boolean isFinal;
    private boolean isConstructor;
    private CtBlock body;

    private SpoonMethod(Builder builder) {
        this.name = builder.name;
        this.accessModifier = builder.accessModifier;
        this.parameters = builder.parameters;
        this.returnType = builder.returnType;
        this.isStatic = builder.isStatic;
        this.isFinal = builder.isFinal;
        this.isConstructor = builder.isConstructor;
        this.exceptionClasses = builder.exceptionClasses;
        this.annotations = builder.annotations;
        this.body = builder.body;
    }

    // spoon.support.SpoonClassNotFoundException: cannot load class: cullinan.test.imports.SimpleClass
    // Hard to map between Class and CtTypeReference
    public static Builder newBuilder(String name, SpoonAccessModifier accessModifier, CtTypeReference returnType, boolean isConstructor) {
        return new SpoonMethod.Builder(name, accessModifier, returnType, isConstructor);
    }

    public String getName() {
        return name;
    }

    public SpoonAccessModifier getAccessModifier() {
        return accessModifier;
    }

    public CtTypeReference getReturnType() {
        return returnType;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public boolean isPublic() {
        return accessModifier.equals(SpoonAccessModifier.PUBLIC);
    }
    private boolean isPackage() {
        return accessModifier.equals(SpoonAccessModifier.PACKAGE);
    }

    public List<SpoonParameter> getParameters() {
        return parameters;
    }

    public List<CtTypeReference> getExceptionClasses() {
        return exceptionClasses;
    }

    public boolean isConstructor() {
        return isConstructor;
    }

    public CtBlock getBody() {
        return body;
    }

    public void setReturnType(CtTypeReference newType) {
        returnType = newType;
    }

    // TODO Simplified for now
    public boolean isUsedOutsideService() {
        return isPublic() || isPackage();
    }



    public static class Builder {
        private final SpoonAccessModifier accessModifier;
        private final CtTypeReference returnType;
        private String name;
        private boolean isConstructor;

        // Optional:
        private boolean isStatic = false;
        public boolean isFinal = false;
        private List<CtTypeReference> exceptionClasses = new ArrayList<>();
        private List<SpoonParameter> parameters = new ArrayList<>();
        private List<CtTypeReference> annotations = new ArrayList<>();
//        private SpoonBody body = null;

        private CtBlock body = null;

        public Builder(String name, SpoonAccessModifier accessModifier, CtTypeReference returnType, boolean constructor) {
            this.name = name;
            this.accessModifier = accessModifier;
            this.returnType = returnType;
            this.isConstructor = constructor;
        }

        public SpoonMethod build() {
            return new SpoonMethod(this);
        }

        public Builder makeStatic() {
            this.isStatic = true;
            return this;
        }

        public Builder makeFinal() {
            this.isFinal = true;
            return this;
        }

        public Builder addException(CtTypeReference exception) {
            this.exceptionClasses.add(exception);
            return this;
        }

        public Builder addParameter(SpoonParameter spoonParameter) {
            this.parameters.add(spoonParameter);
            return this;
        }

        public Builder clearAnnotations() {
            this.annotations.clear();
            return this;
        }

        public Builder addAnnotation(CtTypeReference annotation) {
            this.annotations.add(annotation);
            return this;
        }

        public Builder emptyBody() {
            this.body = factory.createBlock();
//            this.body = SpoonBody.newBuilder().build();
            return this;
        }

        public void addLine(CtReturn returnStatement) {
            this.body.addStatement(returnStatement);
        }

        public void setBody(CtBlock body) {
            this.body = body;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}

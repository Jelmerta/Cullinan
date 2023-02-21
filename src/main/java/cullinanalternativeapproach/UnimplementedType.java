package cullinanalternativeapproach;

import cullinan.helpers.decomposition.generators.UnimplementedTypeGenerator;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

import java.util.Collection;

public class UnimplementedType implements Writable2 {
    private final CtType java;

    // TODO Hmm logic is outside... Just a wrapper now.
    public UnimplementedType(CtType type) {
//        UnimplementedTypeGenerator unimplementedTypeGenerator = new UnimplementedTypeGenerator();
        this.java = type;
    }

    public CtType getJava() {
        return java;
    }

    @Override
    public DataWriter2 createWriter() {
        return new UnimplementedTypeWriter(this);
    }
}

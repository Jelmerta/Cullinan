package generatedfiles;

import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.UnimplementedTypeWriter;
import spoon.reflect.declaration.CtType;

public class UnimplementedType implements Writable {
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
    public DataWriter createWriter() {
        return new UnimplementedTypeWriter(this);
    }
}

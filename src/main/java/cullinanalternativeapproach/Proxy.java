package cullinanalternativeapproach;

import cullinan.helpers.decomposition.writers.JavaWriter2;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

import java.util.List;

// TODO Maybe define the generator here as well? Every item should have a generator and a way to be written?
public class Proxy implements Writable2 {
    CtClass javaProxy;

    public Proxy(CtClass javaProxy) {
        this.javaProxy = javaProxy;
    }

    @Override
    public JavaWriter2 createWriter() {
        return new JavaWriter2(this);
    }

    public CtType getJava() {
        return javaProxy;
    }

    public List<WriteDefinition> getWriteDefinition() {
        return List.of(WriteDefinition.MAIN_SERVICE, WriteDefinition.OTHER_MICROSERVICES);
    }
}

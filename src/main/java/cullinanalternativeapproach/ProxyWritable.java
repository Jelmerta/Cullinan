//package cullinanalternativeapproach;
//
//import spoon.reflect.declaration.CtClass;
//import spoon.reflect.declaration.CtType;
//
//// TODO Maybe define the generator here as well? Every item should have a generator and a way to be written?
//public class ProxyWritable implements Writable2 {
//    private final String originalClassName;
//    private final CtClass javaProxy;
//
//    public ProxyWritable(String originalClassName, CtClass javaProxy) {
//        this.originalClassName = originalClassName;
//        this.javaProxy = javaProxy;
//    }
//
//    @Override
//    public ProxyWriter createWriter() {
//        return new ProxyWriter(this);
//    }
//
//    public CtType getJava() {
//        return javaProxy;
//    }
//
//    public String getOriginalClassName() {
//        return originalClassName;
//    }
//}

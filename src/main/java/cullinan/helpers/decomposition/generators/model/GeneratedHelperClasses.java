package cullinan.helpers.decomposition.generators.model;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;

// Helpers are identical in every service
public class GeneratedHelperClasses {
    private CtClass referenceId;
    private CtInterface referenceInterface;
    private CtClass storageClass; // Could be an interface. Currently using an in-mem database, but could be a real database.
    private CtClass serializationUtil;

    public void addReferenceId(CtClass referenceId) {
        this.referenceId = referenceId;
    }

    public void addReferenceInterface(CtInterface referenceInterface) {
        this.referenceInterface = referenceInterface;
    }

    public void addStorageClass(CtClass storageClass) {
        this.storageClass = storageClass;
    }

    public void addSerializationUtil(CtClass serializationUtil) {
        this.serializationUtil = serializationUtil;
    }

    public CtInterface getReferenceInterface() {
        return referenceInterface;
    }

    public CtClass getStorageClass() {
        return storageClass;
    }

    public CtClass getSerializationUtil() {
        return serializationUtil;
    }

    public CtClass getReferenceId() {
        return referenceId;
    }
}
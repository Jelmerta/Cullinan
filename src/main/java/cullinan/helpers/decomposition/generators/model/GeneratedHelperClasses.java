package cullinan.helpers.decomposition.generators.model;


import generatedfiles.*;

import java.util.List;

// Helpers are identical in every service
public class GeneratedHelperClasses {
    private ReferenceId referenceId;
    private ReferenceInterface referenceInterface;
    private Storage storage; // Could be an interface. Currently using an in-mem database, but could be a real database.
    private SerializationUtil serializationUtil;

    public void addReferenceId(ReferenceId referenceId) {
        this.referenceId = referenceId;
    }

    public void addReferenceInterface(ReferenceInterface referenceInterface) {
        this.referenceInterface = referenceInterface;
    }

    public void addStorageClass(Storage storage) {
        this.storage = storage;
    }

    public void addSerializationUtil(SerializationUtil serializationUtil) {
        this.serializationUtil = serializationUtil;
    }

    public ReferenceInterface getReferenceInterface() {
        return referenceInterface;
    }

    public Storage getStorageClass() {
        return storage;
    }

    public SerializationUtil getSerializationUtil() {
        return serializationUtil;
    }

    public ReferenceId getReferenceId() {
        return referenceId;
    }

    public List<Writable> getAllWritables() {
        return List.of(referenceInterface, storage, serializationUtil, referenceId);
    }
}
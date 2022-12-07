package cullinan.helpers.decomposition.generators.model;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtType;

import java.util.*;
import java.util.stream.Collectors;

public class GeneratedData {
    private GeneratedHelperClasses generatedHelperClasses;
    private Map<String, GeneratedServiceLevel> generatedServiceLevels = new HashMap<>();
    private Map<String, GeneratedClassLevel> generatedClassLevels = new HashMap<>();
    private Map<String, CtType> unimplementedTypeMap = new HashMap<>();

    public GeneratedData() {
    }

    public void addGeneratedHelpers(GeneratedHelperClasses generatedHelperClasses) {
        this.generatedHelperClasses = generatedHelperClasses;
    }

    public void addGeneratedClassLevel(GeneratedClassLevel generatedClassLevel) {
        this.generatedClassLevels.put(generatedClassLevel.getOriginalFullyQualifiedClassname(), generatedClassLevel);
    }

    public void addGeneratedServiceLevel(GeneratedServiceLevel generatedServiceLevel) {
        this.generatedServiceLevels.put(generatedServiceLevel.getServiceName(), generatedServiceLevel);
    }

    public void addUnimplementedData(CtType unimplementedType) {
        this.unimplementedTypeMap.put(unimplementedType.getQualifiedName(), unimplementedType);

    }

//    public GeneratedHelperClasses getHelpers() {
//        return generatedHelperClasses;
//    }

//    public Map<String, GeneratedServiceLevel> getGeneratedServiceLevels() {
//        return generatedServiceLevels;
//    }

//    public GeneratedClassLevel getGeneratedClassLevel(String serviceClassName) {
//        return generatedClassLevels.get(serviceClassName);
//    }

//    public Collection<GeneratedClassLevel> getGeneratedClassLevels() {
//        return generatedClassLevels.values();
//    }

    public CtClass getSerializationUtil() {
        return generatedHelperClasses.getSerializationUtil();
    }

    public List<CtClass> getProxies() {
        return generatedClassLevels.values().stream()
                .map(GeneratedClassLevel::getProxy)
                .collect(Collectors.toList());
    }

    // The required proxies for a service are, for now, all the proxies except for the ones in the service itself. We provide the classes in the service as argument
    // We could make this easier by having a Proxy object that keeps track of information such as its service.
    public List<CtClass> getFilteredProxies(List<String> filteredClasses) {
        return generatedClassLevels.keySet().stream()
                .filter(className -> !filteredClasses.contains(className))
                .map(className -> generatedClassLevels.get(className))
                .map(GeneratedClassLevel::getProxy)
                .collect(Collectors.toList());
    }

    public List<CtClass> getClients() {
        return generatedClassLevels.values().stream()
                .map(generatedClassLevel -> generatedClassLevel.getGeneratedClientService().getClient())
                .collect(Collectors.toList());
    }

    public List<CtClass> getFilteredClients(List<String> filteredClasses) {
        return generatedClassLevels.keySet().stream()
                .filter(className -> !filteredClasses.contains(className))
                .map(className -> generatedClassLevels.get(className))
                .map(classLevel -> classLevel.getGeneratedClientService().getClient())
                .collect(Collectors.toList());
    }

    public List<CtType> getFilteredNotImplementedTypes(List<String> filteredClasses) {
        return unimplementedTypeMap.keySet().stream()
                .filter(className -> !filteredClasses.contains(className))
                .map(className -> unimplementedTypeMap.get(className))
                .collect(Collectors.toList());    }

    public CtClass getReferenceId() {
        return generatedHelperClasses.getReferenceId();
    }

    public CtInterface getReferenceInterface() {
        return generatedHelperClasses.getReferenceInterface();
    }

    public CtClass getClassDefinitions(String serviceName) {
        return generatedServiceLevels.get(serviceName).getClassDefinitions();
    }

    public CtClass getStorageClass() {
        return generatedHelperClasses.getStorageClass();
    }

    public CtClass getOriginal(String className) {
        return generatedClassLevels.get(className).getServiceOriginalClass();
    }

    public CtClass getServiceImplementation(String className) {
        return generatedClassLevels.get(className).getGeneratedClientService().getService();
    }

    public CtClass getMain(String serviceName) {
        return generatedServiceLevels.get(serviceName).getMain();
    }

    public Collection<CtInterface> getInterfaces() {
        return generatedClassLevels.values().stream()
                .map(generatedClassLevel -> generatedClassLevel.getGeneratedClientService().getInterface())
                .collect(Collectors.toList());
    }

    public GeneratedHelperClasses getHelpers() {
        return generatedHelperClasses;
    }
}

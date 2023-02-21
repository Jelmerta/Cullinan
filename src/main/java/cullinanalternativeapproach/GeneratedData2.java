package cullinanalternativeapproach;

import spoon.reflect.declaration.CtType;

import java.util.*;
import java.util.stream.Collectors;

public class GeneratedData2 {
    private GeneratedHelperClasses2 generatedHelperClasses;
    private GeneratedMainServiceLevel generatedMainServiceLevel;
    private GeneratedInterfaceServiceLevel generatedInterfaceServiceLevel;
    private Map<String, GeneratedServiceLevel2> generatedServiceLevels = new HashMap<>();
    private Map<String, GeneratedClassLevel2> generatedClassLevels = new HashMap<>();
    private Map<String, UnimplementedType> unimplementedTypeMap = new HashMap<>();

    public GeneratedData2() {
    }

    public void addGeneratedHelpers(GeneratedHelperClasses2 generatedHelperClasses) {
        this.generatedHelperClasses = generatedHelperClasses;
    }

    public void addGeneratedClassLevel(GeneratedClassLevel2 generatedClassLevel) {
        this.generatedClassLevels.put(generatedClassLevel.getOriginalFullyQualifiedClassname(), generatedClassLevel);
    }

    public void addGeneratedMainServiceLevel(GeneratedMainServiceLevel generatedMainServiceLevel) {
        this.generatedMainServiceLevel = generatedMainServiceLevel;
    }

    public void addGeneratedInterfaceServiceLevel(GeneratedInterfaceServiceLevel generatedInterfaceServiceLevel) {
        this.generatedInterfaceServiceLevel = generatedInterfaceServiceLevel;
    }

    public void addGeneratedServiceLevel(GeneratedServiceLevel2 generatedServiceLevel) {
        this.generatedServiceLevels.put(generatedServiceLevel.getServiceName(), generatedServiceLevel);
    }

    public void addUnimplementedData(UnimplementedType unimplementedType) {
        this.unimplementedTypeMap.put(unimplementedType.getJava().getQualifiedName(), unimplementedType);

    }

    public SerializationUtil getSerializationUtil() {
        return generatedHelperClasses.getSerializationUtil();
    }

    public ReferenceId getReferenceId() {
        return generatedHelperClasses.getReferenceId();
    }

    public ReferenceInterface getReferenceInterface() {
        return generatedHelperClasses.getReferenceInterface();
    }

    public ClassDefinitions getClassDefinitions(String serviceName) {
        return generatedServiceLevels.get(serviceName).getClassDefinitions();
    }

    public Storage getStorageClass() {
        return generatedHelperClasses.getStorageClass();
    }

    public Implementation getOriginal(String className) {
        return generatedClassLevels.get(className).getImplementation();
    }

    public Service getService(String className) {
        return generatedClassLevels.get(className).getGeneratedClientService().getService();
    }

    public MicroserviceMain getMain(String serviceName) {
        return generatedServiceLevels.get(serviceName).getMain();
    }

    public Collection<ServiceInterface> getInterfaces() {
        return generatedClassLevels.values().stream()
                .map(generatedClassLevel -> generatedClassLevel.getGeneratedClientService().getInterface())
                .collect(Collectors.toList());
    }

    public GeneratedHelperClasses2 getHelpers() {
        return generatedHelperClasses;
    }

    public List<Proxy> getProxies() {
        return generatedClassLevels.values().stream()
                .map(GeneratedClassLevel2::getProxy)
                .collect(Collectors.toList());
    }

    public List<Writable2> getWritables() {
        List<Writable2> writables = new ArrayList<>();

        writables.addAll(generatedHelperClasses.getAllWritables());

        writables.add(generatedMainServiceLevel.getMainServicePom());
        writables.add(generatedMainServiceLevel.getClassDefinitions());

        writables.add(generatedInterfaceServiceLevel.getServiceInterfacePom());

        for (GeneratedServiceLevel2 serviceLevel : generatedServiceLevels.values()) {
            writables.addAll(serviceLevel.getAllWritables());
        }

        for (GeneratedClassLevel2 classLevel : generatedClassLevels.values()) {
            writables.addAll(classLevel.getAllWritables());
        }

        writables.addAll(unimplementedTypeMap.values());

        return writables;
    }
}
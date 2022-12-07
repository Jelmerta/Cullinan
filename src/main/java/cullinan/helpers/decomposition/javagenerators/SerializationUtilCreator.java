package cullinan.helpers.decomposition.javagenerators;

import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoonhelpers.managers.SpoonFactoryManager;
import spoonhelpers.managers.SpoonMethodManager;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

// TODO SerializationUtil should be different for Monolith: All the classes are proxy classes
public class SerializationUtilCreator {
    private CtClass serializationUtil;

    public SerializationUtilCreator() {

    }

    public CtClass build() {
        createSerializationUtil();
//        addServiceClassDefinition(); TODO Something missing? this was still intact
        // Add Set(? Strings of the classes?) of service classes to be used for dynamic (de)serialization TODO I don't believe service should know of other service's class objects... But there might be practical reasons why it might be necessary to have this information shared... Not completely sure yet.
        // Add dynamic serialization function (either reference id for service class objects, or just byte64 serialized) TODO What if a complete object, not proxy is passed? Should we turn it in a proxy, aka make a monolith service? What does IBM do in this case? This is still a big problem to solve(?).
        // Add dynamic deserialization

        Factory templateFactory = SpoonFactoryManager.getFactory(Path.of("src/main/java/util/SerializationUtil.java")); // TODO Hardcode not good
//        Factory templateFactory = SpoonFactoryManager.getFactory(Path.of("src/main/java/cullinan/helpers/templates/SerializationUtil.java")); // TODO Hardcode not good
//        System.out.println(templateFactory.Class().getAll());
        CtClass original = templateFactory.Class().get("util.SerializationUtil");

        // We are copying original class manually because otherwise we get error messages (no declaring type, package missing, and does not seem possible to add...?)
        CtClass serializationUtil = SpoonFactoryManager.getDefaultFactory().createClass("util.SerializationUtil");
        serializationUtil.addModifier(ModifierKind.PUBLIC);










        Set<CtMethod> methods = original.getMethods();
        for (CtMethod method : methods) {
            serializationUtil.addMethod(method);
        }

        Set<CtConstructor> constructors = original.getConstructors();
        for (CtConstructor constructor : constructors) {
            serializationUtil.addConstructor(constructor);
        }

        return serializationUtil;
    }



    private void createSerializationUtil() {
        serializationUtil = SpoonFactoryManager.getDefaultFactory().createClass();
        serializationUtil.setSimpleName("util.SerializationUtil");
        serializationUtil.addModifier(ModifierKind.PUBLIC);
    }
}

package cullinan.helpers.decomposition.javagenerators;

import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoonhelpers.managers.SpoonFactoryManager;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

// TODO Perhaps the Storage class should be located in the same location as the original class, as package private constructors can otherwise not be called and initiated with data
public class StorageManagerCreator {
    public StorageManagerCreator() {

    }

    public CtClass build() {
        Factory templateFactory = SpoonFactoryManager.getFactory(Path.of("src/main/java/util/StorageManager.java")); // TODO Hardcode not good
//        Factory templateFactory = SpoonFactoryManager.getFactory(Path.of("src/main/java/cullinan/helpers/templates/StorageManager.java")); // TODO Hardcode not good

        CtClass original = templateFactory.Class().get("util.StorageManager");

        CtClass copy = SpoonFactoryManager.getDefaultFactory().createClass("util.StorageManager");
        copy.addModifier(ModifierKind.PUBLIC);

        List<CtField> fields = original.getFields();
        for (CtField field : fields) {
            copy.addField(field);
        }

        Set<CtMethod> methods = original.getMethods();
        for (CtMethod method : methods) {
            copy.addMethod(method);
        }

        Set<CtConstructor> constructors = original.getConstructors();
        for (CtConstructor constructor : constructors) {
            copy.addConstructor(constructor);
        }

        System.out.println(copy);

        return copy;
    }
}

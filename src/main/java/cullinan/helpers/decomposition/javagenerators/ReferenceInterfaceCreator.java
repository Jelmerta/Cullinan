package cullinan.helpers.decomposition.javagenerators;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoonhelpers.managers.SpoonFactoryManager;
import spoonhelpers.managers.SpoonMethodManager;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class ReferenceInterfaceCreator {
    private CtClass referenceInterface;

    public ReferenceInterfaceCreator() {
    }

    public CtInterface build() {
        Factory templateFactory = SpoonFactoryManager.getFactory(Path.of("src/main/java/util/CullinanReference.java")); // TODO Hardcode not good
//        System.out.println(templateFactory.Class().getAll());
        CtInterface original = templateFactory.Interface().get("util.CullinanReference");

        return original;

        // We are copying original class manually because otherwise we get error messages (no declaring type, package missing, and does not seem possible to add...?)
//        CtClass serializationUtil = SpoonFactoryManager.getDefaultFactory().createClass("util.CullinanReference");

//        return serializationUtil;
    }
}

package cullinan.helpers.decomposition.javagenerators;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.factory.Factory;
import spoonhelpers.managers.SpoonFactoryManager;

import java.nio.file.Path;

public class CullinanIdCreator {
    public CullinanIdCreator() {
    }

    public CtClass build() {
        Factory templateFactory = SpoonFactoryManager.getFactory(Path.of("src/main/java/util/CullinanId.java")); // TODO Hardcode not good
        CtClass original = templateFactory.Class().get("util.CullinanId");

        return original;
    }
}

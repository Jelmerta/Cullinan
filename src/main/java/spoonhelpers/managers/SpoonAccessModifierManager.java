package spoonhelpers.managers;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoonhelpers.model.SpoonAccessModifier;

import java.util.Optional;

public class SpoonAccessModifierManager {
    public static Optional<ModifierKind> to(SpoonAccessModifier spoonAccessModifier) {
        switch (spoonAccessModifier) {
            case PUBLIC -> {
                return Optional.of(ModifierKind.PUBLIC);
            }
            case PRIVATE -> {
                return Optional.of(ModifierKind.PRIVATE);
            }
            case PACKAGE -> {
                return Optional.empty();
            }
            case PROTECTED -> {
                return Optional.of(ModifierKind.PROTECTED);
            }
            default -> {
                throw new IllegalArgumentException("Modifier " + spoonAccessModifier.name() + " cannot be casted to ModifierKind");
            }
        }
    }

    public static SpoonAccessModifier to(CtMethod method) {
        if (method.isPublic()) {
            return SpoonAccessModifier.PUBLIC;
        } else if (method.isPrivate()) {
            return SpoonAccessModifier.PRIVATE;
        } else if (method.isProtected()) {
            return SpoonAccessModifier.PROTECTED;
        } else {
            return SpoonAccessModifier.PACKAGE;
        }
    }

    public static SpoonAccessModifier to(CtConstructor constructor) {
        if (constructor.isPublic()) {
            return SpoonAccessModifier.PUBLIC;
        } else if (constructor.isPrivate()) {
            return SpoonAccessModifier.PRIVATE;
        } else if (constructor.isProtected()) {
            return SpoonAccessModifier.PROTECTED;
        } else {
            return SpoonAccessModifier.PACKAGE;
        }
    }

    public static SpoonAccessModifier to(CtClass ctClass) {
        if (ctClass.isPublic()) {
            return SpoonAccessModifier.PUBLIC;
        } else if (ctClass.isPrivate()) {
            return SpoonAccessModifier.PRIVATE;
        } else if (ctClass.isProtected()) {
            return SpoonAccessModifier.PROTECTED;
        } else {
            return SpoonAccessModifier.PACKAGE;
        }
    }
}

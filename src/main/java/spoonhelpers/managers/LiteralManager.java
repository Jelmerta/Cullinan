package spoonhelpers.managers;

import spoon.reflect.code.CtLiteral;
import spoon.reflect.reference.CtTypeReference;
import spoonhelpers.managers.SpoonFactoryManager;

public class LiteralManager {
    public static CtLiteral createLiteral(Class type, Object value) {
        CtLiteral literal = SpoonFactoryManager.getDefaultFactory().createLiteral();
        literal.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(type));
        literal.setValue(value);
        return literal;
    }
}

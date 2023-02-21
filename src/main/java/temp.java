import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtTypeReference;
import spoonhelpers.managers.SpoonFactoryManager;

import java.util.Set;

public class temp {
    public static void main(String[] args) {
        CtTypeReference<Set> setReference = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Set.class);
        CtType<Set> setType = setReference.getTypeDeclaration();
        // Param 1: Return type of method, Param 2: Name of method, Param 3: The parameters of this method

        CtTypeReference<Object> containsReturnType = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(boolean.class);
        CtTypeReference<Object> containsParameter = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(Object.class);

        CtMethod<Object> contains = setType.getMethod(containsReturnType, "contains", containsParameter);

        System.out.println(contains);

//        CtLocalVariable variable = SpoonFactoryManager.getDefaultFactory().createLocalVariable();
//
//        CtTypeReference<String> stringReference = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class);
//        variable.setType(stringReference);
//
//        variable.setSimpleName("name");
//
//        CtLiteral name = SpoonFactoryManager.getDefaultFactory().createLiteral();
//        name.setType(stringReference);
//        name.setValue("David");
//        variable.setAssignment(name);
//
//        System.out.println(variable);



        CtTypeReference<String> stringReference = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class);

        CtLiteral name = SpoonFactoryManager.getDefaultFactory().createLiteral("David");

        CtLocalVariable variable = SpoonFactoryManager.getDefaultFactory().createLocalVariable(stringReference, "name", name);


        System.out.println(variable);
    }
}

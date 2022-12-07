package cullinan.helpers.decomposition.generators;

import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThrow;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoonhelpers.managers.SpoonFactoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UnimplementedTypeGenerator {
    public UnimplementedTypeGenerator() {

    }

    public List<CtType> generate(List<CtType<?>> types) {
        List<CtType> unimplementedTypes = new ArrayList<>();
        for (CtType type : types) {
            if (!type.isClass() && !type.isEnum() && !type.isInterface()) {
                continue;
            }

            unimplementedTypes.add(generate(type));
        }
        return unimplementedTypes;
    }

    // TODO We do not clone here? We should probably generate new thing?
    private CtType generate(CtType type) {
        if (type.isInterface()) {
            return type; // Interface does not require any changes
        } else if (type.isClass() || type.isEnum()) {
            return emptyMethodBodies(type);
        } else {
            throw new IllegalStateException("Unexpected type?");
        }
    }

    private static CtType emptyMethodBodies(CtType type) {
        Set<CtMethod> allMethods = type.getAllMethods();
        for (CtMethod method : allMethods) {
            if (method.isAbstract()) {
                continue;
            }

            addExceptionBody(method);
        }
        return type;
    }

    private static void addExceptionBody(CtMethod method) {
        CtNewClass newException = SpoonFactoryManager.getDefaultFactory().createNewClass();
        newException.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(IllegalStateException.class));
        CtThrow aThrow = SpoonFactoryManager.getDefaultFactory().createThrow();
        CtStatement throwException = aThrow.setThrownExpression(newException);

        method.setBody(throwException);
    }
}


// TODO Almost feel like this can be done with a processor instead for every class that is not part of the service...?

//    public GeneratedData generate(List<CtType<?>> allTypes) {
//        for (CtType type : allTypes) {
//            GeneratedUnimplementedType unimplementedType = generateUnimplementedType(type);
//        }
//    }

//    // TODO This could be done with processor?
//
//    // For now we add all the other interfaces. We might need to add other classes, and remove unnecessary interfaces to make it a minimal set.
//    private List<CtType> findRequiredExtraClasses(Microservice microserviceCut, List<CtClass> microServiceClasses) {
//        List<CtType> requiredExtraClasses = new ArrayList<>();
//        for (CtType<?> javaClass : codeBaseFactory.Class().getAll()) {
////            if (javaClass.getPackage())
//
//
//        }
//        return requiredExtraClasses;
//    }
package cullinan.helpers.decomposition.generators;

import generatedfiles.UnimplementedType;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThrow;
import spoon.reflect.declaration.*;
import spoonhelpers.managers.SpoonFactoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UnimplementedTypeGenerator {
    public UnimplementedTypeGenerator() {

    }

    public List<UnimplementedType> generate(List<CtType<?>> types) {
        List<UnimplementedType> unimplementedTypes = new ArrayList<>();
        for (CtType type : types) {
            if (!type.isClass() && !type.isEnum() && !type.isInterface()) {
                continue;
            }

            // TODO wtf didnt we clone this? does this not give issues?
            unimplementedTypes.add(generate(type));
        }
        return unimplementedTypes;
    }

    // TODO We do not clone here? We should probably generate new thing?
    private UnimplementedType generate(CtType type) {
        if (type.isInterface()) {
            return new UnimplementedType(type); // Interface does not require any changes
//        } else if (type.isClass() || type.isEnum()) {
        } else if (type.isClass() || type.isEnum()) {
            return new UnimplementedType(emptyMethodBodies(removeAnonymousTypes(type)));
        } else {
            throw new IllegalStateException("Unexpected type?");
        }
    }

//    TODO Might need to be recursive for inner classes?
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

    //    Unimplemented versions do not need anonymous types.
    private static CtType removeAnonymousTypes(CtType type) {
        Set<CtType> nestedTypes = type.getNestedTypes();
        for (CtType innerType : nestedTypes) {
            if (innerType.isAnonymous()) {
                type.removeNestedType(innerType);
            }
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
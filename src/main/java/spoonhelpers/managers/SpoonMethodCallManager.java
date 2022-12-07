package spoonhelpers.managers;

import org.eclipse.jdt.internal.compiler.ast.Invocation;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;

import java.rmi.registry.LocateRegistry;

public class SpoonMethodCallManager {
    public static CtInvocation createClassCall(Class systemClass, String methodName, CtExpression<?>... arguments) {
        CtTypeReference classType = SpoonFactoryManager.getDefaultFactory().createCtTypeReference(systemClass);

        CtTypeAccess classTarget = SpoonFactoryManager.getDefaultFactory().createTypeAccess(classType);
        CtExecutableReference methodExecutable = SpoonMethodManager.findMethod(classType, methodName).getReference();

        CtInvocation classCall = SpoonFactoryManager.getDefaultFactory().createInvocation(classTarget, methodExecutable);
        for (CtExpression<?> argument : arguments) {
            classCall.addArgument(argument);
        }

        return classCall;
    }
}

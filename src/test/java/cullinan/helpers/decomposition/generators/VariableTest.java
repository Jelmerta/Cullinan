package cullinan.helpers.decomposition.generators;

import cullinan.helpers.decomposition.generators.model.GeneratedClassLevel;
import cullinan.helpers.decomposition.generators.model.GeneratedData;
import cullinan.helpers.decomposition.generators.model.GeneratedHelperClasses;
import generatedfiles.OriginalJava;
import generatedfiles.ServiceInterface;
import input.Microservice;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoonhelpers.managers.SpoonFactoryManager;

import static org.junit.jupiter.api.Assertions.*;

class VariableTest {

    @Test
    void testVariable() {
        Microservice microservice = new Microservice();
        GeneratedData generatedData = new GeneratedData();
        HelpersGenerator helpersGenerator = new HelpersGenerator();
        GeneratedHelperClasses helpers = helpersGenerator.generate();
        generatedData.addGeneratedHelpers(helpers);
        ClassLevelGenerator classLevelGenerator = new ClassLevelGenerator(generatedData);

        CtClass javaWithVariable = Launcher.parseClass("class A { public static final int CONSTANT_VAR = 3;}");
        OriginalJava original = new OriginalJava(javaWithVariable, microservice);
        GeneratedClassLevel classLevel = classLevelGenerator.generate(original);

        ServiceInterface variableInterface = classLevel.getInterface();
        assertNotNull(variableInterface.getJava().getMethod("retrieveVariableCONSTANT_VAR"));

        System.out.println(classLevel.getImplementation().getJava());
    }

}
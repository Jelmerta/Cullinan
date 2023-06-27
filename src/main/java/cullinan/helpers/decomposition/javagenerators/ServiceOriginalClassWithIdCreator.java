package cullinan.helpers.decomposition.javagenerators;

import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.*;
import spoonhelpers.managers.SpoonFactoryManager;

import java.util.Set;

public class ServiceOriginalClassWithIdCreator {
    private final CtClass originalClass;
    private final CtInterface referenceInterface;
    private CtClass result;

    // TODO CtType original?
    public ServiceOriginalClassWithIdCreator(CtClass originalClass, CtInterface referenceInterface) {
        this.originalClass = originalClass;
        this.referenceInterface = referenceInterface;
    }

    public CtClass build() {

        // Cloning gets rid of the type declaration for some reason it seems... So we add it again. Is this good practice?
//
        this.result = originalClass.clone();
        this.originalClass.getPackage().addType(this.result);

        this.result.addSuperInterface(referenceInterface.getReference());

        addReferenceField();
        addGetReference();
        addSetReference(); // TODO This should be implemented both in the service and client right?

        Set<CtType> nestedTypes = originalClass.getNestedTypes();
        for (CtType nestedType : nestedTypes) {
            if (nestedType.isClass() || nestedType.isEnum()) {
                ServiceOriginalClassWithIdCreator serviceOriginalClassWithIdCreator = new ServiceOriginalClassWithIdCreator((CtClass) nestedType, referenceInterface);
                CtClass build = serviceOriginalClassWithIdCreator.build();
                result.addNestedType(build);
                result.removeNestedType(nestedType);
//                nestedType = build;
            }
        }
        // TODO Add get function for retrieval of reference ids
        // TODO How do we initialize this value? Do we need a setter?

        return this.result;
    }

    private void addReferenceField() {
        CtField referenceIdField = SpoonFactoryManager.getDefaultFactory().createField();
        referenceIdField.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
        referenceIdField.setSimpleName("referenceId");
        referenceIdField.addModifier(ModifierKind.PRIVATE);
        this.result.addField(referenceIdField);
    }

    private void addGetReference() {
        CtMethod getReferenceMethod = SpoonFactoryManager.getDefaultFactory().createMethod();
        getReferenceMethod.setSimpleName("getReferenceId");
        getReferenceMethod.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
        getReferenceMethod.addModifier(ModifierKind.PUBLIC);

        CtField referenceId = this.result.getField("referenceId");
        CtVariableRead referenceRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
        referenceRead.setVariable(referenceId.getReference());

        CtReturn returnStatement = SpoonFactoryManager.getDefaultFactory().createReturn();
        returnStatement.setReturnedExpression(referenceRead);

        getReferenceMethod.setBody(returnStatement);

        this.result.addMethod(getReferenceMethod);
    }

    private void addSetReference() {
        CtMethod setReference = SpoonFactoryManager.getDefaultFactory().createMethod();
        setReference.setSimpleName("setReferenceId");
        setReference.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(void.class));
        setReference.addModifier(ModifierKind.PUBLIC);

        CtParameter referenceIdParameter = SpoonFactoryManager.getDefaultFactory().createParameter();
        referenceIdParameter.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
        referenceIdParameter.setSimpleName("referenceIdParam");
        setReference.addParameter(referenceIdParameter);

//        CtField referenceId = SpoonFactoryManager.getDefaultFactory().createField();
//        referenceId.setType(SpoonFactoryManager.getDefaultFactory().createCtTypeReference(String.class));
//        referenceId.setSimpleName("referenceId");

        CtField referenceId = result.getField("referenceId");
        CtVariableRead referenceRead = SpoonFactoryManager.getDefaultFactory().createVariableRead();
        referenceRead.setVariable(referenceIdParameter.getReference());
        CtAssignment referenceIdAssignment = SpoonFactoryManager.getDefaultFactory().createVariableAssignment(referenceId.getReference(), true, referenceRead);

        setReference.setBody(referenceIdAssignment);

        this.result.addMethod(setReference);
    }
}

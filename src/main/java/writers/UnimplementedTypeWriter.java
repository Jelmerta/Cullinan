package writers;

import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.JavaWriter;
import cullinan.helpers.decomposition.writers.ServiceWriteDefinition;
import cullinan.helpers.decomposition.writers.WriteDefinition;
import generatedfiles.ServiceDefinition;
import generatedfiles.UnimplementedType;
import spoon.reflect.code.CtComment;
import spoonhelpers.managers.SpoonFactoryManager;

import java.util.List;

public class UnimplementedTypeWriter implements DataWriter {
    private static final List<WriteDefinition> WRITE_DEFINITIONS = List.of(WriteDefinition.THIS_MICROSERVICE); // Class-based? TODO All classes not in this microservice?
    private static final JavaWriter javaWriter = new JavaWriter();
    private final UnimplementedType unimplementedType;
    private final ServiceWriteDefinition serviceWriteDefinition;

    public UnimplementedTypeWriter(UnimplementedType unimplementedType) {
        this.unimplementedType = unimplementedType;
        this.serviceWriteDefinition = new ServiceWriteDefinition(WRITE_DEFINITIONS);
    }

    @Override
    public void write(ServiceDefinition serviceDefinition) {
        if (shouldWrite(serviceDefinition)) {
            unimplementedType.getJava().addComment(SpoonFactoryManager.getDefaultFactory().createComment("Unimplemented", CtComment.CommentType.INLINE));
//            System.out.println(unimplementedType.getJava());
            javaWriter.write(serviceDefinition.getOutputPath(), unimplementedType.getJava());
        } else {
            throw new IllegalArgumentException("Service should not be written to"); // TODO... DO we want this
        }
    }

    // We have already filtered out places we do not want to write to by simply not generating the unimplemented type for the class.
    // This is the case when the class is a service class: meaning we will either write a proxy or a service implementation instead.
    public boolean shouldWrite(ServiceDefinition serviceDefinition) {
        return serviceWriteDefinition.shouldWrite(serviceDefinition);
//                && !serviceDefinition.getClassNames().contains(unimplementedType.getJava().getQualifiedName().toLowerCase()); // Make sure to not overwrite implementation files. Only java files outside of this service. might be helpful to verify that we do not write twice to the same file. TODO Not necessary
    }
}

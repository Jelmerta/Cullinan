package cullinan.helpers.decomposition.writers;

import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.JavaWriter;
import cullinan.helpers.decomposition.writers.ServiceWriteDefinition;
import cullinan.helpers.decomposition.writers.WriteDefinition;
import generatedfiles.Implementation;
import generatedfiles.ServiceDefinition;
import spoon.reflect.code.CtComment;
import spoonhelpers.managers.SpoonFactoryManager;

import java.util.List;

public class ImplementationWriter implements DataWriter {
    private static final List<WriteDefinition> WRITE_DEFINITIONS = List.of(WriteDefinition.THIS_MICROSERVICE);
    private static final JavaWriter javaWriter = new JavaWriter();
    private final Implementation implementation;
    private final ServiceWriteDefinition serviceWriteDefinition;

    public ImplementationWriter(Implementation implementation) {
        this.implementation = implementation;
        this.serviceWriteDefinition = new ServiceWriteDefinition(WRITE_DEFINITIONS, implementation.getServiceOrigin());
    }

    @Override
    public void write(ServiceDefinition serviceDefinition) {
        if (shouldWrite(serviceDefinition)) {
            implementation.getJava().addComment(SpoonFactoryManager.getDefaultFactory().createComment("Implementation", CtComment.CommentType.INLINE));
            javaWriter.write(serviceDefinition.getOutputPath(), implementation.getJava());
        } else {
            throw new IllegalArgumentException("Service should not be written to"); // TODO... DO we want this
        }
    }

    public boolean shouldWrite(ServiceDefinition serviceDefinition) {
        return serviceWriteDefinition.shouldWrite(serviceDefinition);
        // TODO Separate Microservice check?
    }
}

package cullinanalternativeapproach;

import spoon.reflect.code.CtComment;
import spoonhelpers.managers.SpoonFactoryManager;

import java.util.List;

public class ServiceWriter implements DataWriter2 {
    private static final List<WriteDefinition> WRITE_DEFINITIONS = List.of(WriteDefinition.THIS_MICROSERVICE);
    private static final JavaWriter3 javaWriter = new JavaWriter3();
    private final Service service;
    private final ServiceWriteDefinition serviceWriteDefinition;

    public ServiceWriter(Service service) {
        this.service = service;
        this.serviceWriteDefinition = new ServiceWriteDefinition(WRITE_DEFINITIONS, service.getServiceOrigin());
    }

    @Override
    public void write(ServiceDefinition serviceDefinition) {
        if (shouldWrite(serviceDefinition)) {
            service.getJava().addComment(SpoonFactoryManager.getDefaultFactory().createComment("Service", CtComment.CommentType.INLINE));
            javaWriter.write(serviceDefinition.getOutputPath(), service.getJava());
        } else {
            throw new IllegalArgumentException("Service should not be written to"); // TODO... DO we want this
        }
    }

    public boolean shouldWrite(ServiceDefinition serviceDefinition) {

        return serviceWriteDefinition.shouldWrite(serviceDefinition);
        // TODO Separate Microservice check?
    }
}

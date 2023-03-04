package cullinan.helpers.decomposition.writers;

import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.JavaWriter;
import cullinan.helpers.decomposition.writers.ServiceWriteDefinition;
import cullinan.helpers.decomposition.writers.WriteDefinition;
import generatedfiles.Proxy;
import generatedfiles.ServiceDefinition;
import spoon.reflect.code.CtComment;
import spoonhelpers.managers.SpoonFactoryManager;

import java.util.List;

public class ProxyWriter implements DataWriter {
    private static final List<WriteDefinition> WRITE_DEFINITIONS = List.of(WriteDefinition.MAIN_SERVICE, WriteDefinition.OTHER_MICROSERVICES);
    private static final JavaWriter javaWriter = new JavaWriter();
    private final Proxy proxy;
    private final ServiceWriteDefinition serviceWriteDefinition;

    public ProxyWriter(Proxy proxy) {
        this.proxy = proxy;
         serviceWriteDefinition = new ServiceWriteDefinition(WRITE_DEFINITIONS, proxy.getServiceOrigin());
    }

    @Override
    public boolean shouldWrite(ServiceDefinition serviceDefinition) {
//        if (serviceDefinition.getServiceType().equals(ServiceType.MAIN_SERVICE)) {
//            System.out.println();
//            System.out.println(proxy.getServiceOrigin());
//            System.out.println(proxy.getJava().getSimpleName());
//            System.out.println(serviceWriteDefinition.shouldWrite(serviceDefinition));
//        }

        return serviceWriteDefinition.shouldWrite(serviceDefinition);
//                && !serviceDefinition.getClassNames().contains(unimplementedType.getJava().getQualifiedName().toLowerCase()); // Make sure to not overwrite implementation files. Only java files outside of this service
    }

    @Override
    public void write(ServiceDefinition serviceDefinition) {
        if (shouldWrite(serviceDefinition)) {
//            if (serviceDefinition.getServiceType().equals(ServiceType.MAIN_SERVICE)) {
//                System.out.println();
//                System.out.println(serviceDefinition.getClassNames());
//                System.out.println(serviceDefinition.getServiceType());
//                System.out.println(serviceWriteDefinition);
//                System.out.println(proxy.getServiceOrigin());
//                System.out.println(proxy.getJava().getSimpleName());
//                System.out.println(serviceWriteDefinition.shouldWrite(serviceDefinition));
//                System.out.println(proxy.getJava());
//            }
            proxy.getJava().addComment(SpoonFactoryManager.getDefaultFactory().createComment("Proxy", CtComment.CommentType.INLINE));
            javaWriter.write(serviceDefinition.getOutputPath(), proxy.getJava());
        }
    }
}

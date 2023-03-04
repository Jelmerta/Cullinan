package cullinan.helpers.decomposition.writers;

import generatedfiles.ParentPom;
import generatedfiles.ServiceDefinition;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ParentPomWriter implements DataWriter {
    private static final List<WriteDefinition> WRITE_DEFINITIONS = List.of(WriteDefinition.PARENT);
    private final ParentPom parentPom;
    private final ServiceWriteDefinition serviceWriteDefinition;

    public ParentPomWriter(ParentPom parentPom) {
        this.parentPom = parentPom;
        this.serviceWriteDefinition = new ServiceWriteDefinition(WRITE_DEFINITIONS);
    }

    @Override
    public boolean shouldWrite(ServiceDefinition serviceDefinition) {
        return serviceWriteDefinition.shouldWrite(serviceDefinition);
    }

    @Override
    public void write(ServiceDefinition serviceDefinition) {
        try {
            FileWriter myWriter = new FileWriter(serviceDefinition.getOutputPath() + "/pom.xml");
            myWriter.write(parentPom.getPom());
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred writing parent pom file.");
            e.printStackTrace();
        }



//        try (FileOutputStream output = new FileOutputStream(serviceDefinition.getOutputPath() + "/pom.xml")) {
//
////            Document doc = parentPom.getPom();
////
////            TransformerFactory transformerFactory = TransformerFactory.newInstance();
////            Transformer transformer = transformerFactory.newTransformer();
////            DOMSource source = new DOMSource(doc);
////            StreamResult result = new StreamResult(output);
////            doc.normalizeDocument();
////            doc.normalize();
////
////            transformer.transform(source, result);
//        } catch (IOException | TransformerException e) {
//            throw new RuntimeException(e);
//        }
    }
}

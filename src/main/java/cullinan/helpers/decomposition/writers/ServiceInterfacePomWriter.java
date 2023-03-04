package cullinan.helpers.decomposition.writers;

import generatedfiles.ServiceDefinition;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ServiceInterfacePomWriter implements DataWriter {
    private final Document pom;

    public ServiceInterfacePomWriter(Document pom) {
        this.pom = pom;
    }

    @Override
    public boolean shouldWrite(ServiceDefinition serviceDefinition) {
        return serviceDefinition.getServiceType() == ModuleType.INTERFACE;
    }

    // Has issues with formatting still... Text nodes have line breaks causing inconsistent formatting.
    @Override
    public void write(ServiceDefinition serviceDefinition) {
        // TODO Add dependency on the right modules (probably only client?) We could, if time allows remove unnecessary dependencies.
        File file = new File(serviceDefinition.getOutputPath() + "/pom.xml");
        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (FileOutputStream output = new FileOutputStream(file)) {

//        doc.normalize(); is this supposed to remove white space?

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(pom);
            StreamResult result = new StreamResult(output);
            pom.normalizeDocument();
            pom.normalize();

//        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
//        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            transformer.transform(source, result);
        } catch (IOException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}

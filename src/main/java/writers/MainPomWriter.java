package writers;

import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.ServiceType;
import generatedfiles.ServiceDefinition;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainPomWriter implements DataWriter {
    private final Document pom;

    public MainPomWriter(Document pom) {
        this.pom = pom;
    }

    @Override
    public boolean shouldWrite(ServiceDefinition serviceDefinition) {
        return serviceDefinition.getServiceType() == ServiceType.MAIN_SERVICE;
    }

    // Has issues with formatting still... Text nodes have line breaks causing inconsistent formatting.
    @Override
    public void write(ServiceDefinition serviceDefinition) {
        // TODO Add dependency on the right modules (probably only client?) We could, if time allows remove unnecessary dependencies.
        try (FileOutputStream output = new FileOutputStream(serviceDefinition.getOutputPath() + "/pom.xml")) {

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

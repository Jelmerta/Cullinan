package writers;

import cullinan.helpers.decomposition.writers.DataWriter;
import generatedfiles.MicroservicePom;
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

public class MicroservicePomWriter implements DataWriter {
    private final MicroservicePom microservicePom;

    public MicroservicePomWriter(MicroservicePom microservicePom) {
        this.microservicePom = microservicePom;
    }

    @Override
    public boolean shouldWrite(ServiceDefinition serviceDefinition) {
        return serviceDefinition.getServiceType() == ServiceType.MICROSERVICE;
    }

    // Has issues with formatting still... Text nodes have line breaks causing inconsistent formatting.
    @Override
    public void write(ServiceDefinition serviceDefinition) {
        // TODO Add dependency on the right modules (probably only client?) We could, if time allows remove unnecessary dependencies.
        try (FileOutputStream output = new FileOutputStream(serviceDefinition.getOutputPath() + "/pom.xml")) {

//        doc.normalize(); is this supposed to remove white space?
            Document doc = microservicePom.getPom();

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(output);
            doc.normalizeDocument();
            doc.normalize();

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

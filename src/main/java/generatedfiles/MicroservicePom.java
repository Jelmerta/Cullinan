package generatedfiles;

import cullinan.helpers.decomposition.javagenerators.MicroservicePomCreator;
import cullinan.helpers.decomposition.writers.DataWriter;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import cullinan.helpers.decomposition.writers.MicroservicePomWriter;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class MicroservicePom implements Writable {
    private final Document pom;
    private final String originService;

    public MicroservicePom(Document originalPom, String microserviceName) {
        MicroservicePomCreator microservicePomCreator = new MicroservicePomCreator(originalPom, microserviceName);
        originService = microserviceName;
        try {
            this.pom = microservicePomCreator.buildMicroservicePom();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataWriter createWriter() {
        return new MicroservicePomWriter(this, originService);
    }

    public Document getPom() {
        return pom;
    }
}

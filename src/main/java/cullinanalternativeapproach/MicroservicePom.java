package cullinanalternativeapproach;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class MicroservicePom implements Writable2 {
    private final Document pom;


    public MicroservicePom(Document originalPom, String microserviceName) {
        MicroservicePomCreator microservicePomCreator = new MicroservicePomCreator(originalPom, microserviceName);
        try {
            this.pom = microservicePomCreator.buildMicroservicePom();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataWriter2 createWriter() {
        return new MicroservicePomWriter(this);
    }

    public Document getPom() {
        return pom;
    }
}

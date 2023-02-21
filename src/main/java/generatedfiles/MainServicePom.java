package generatedfiles;

import cullinan.helpers.decomposition.javagenerators.MainPomCreator;
import cullinan.helpers.decomposition.writers.DataWriter;
import org.w3c.dom.Document;
import writers.MainPomWriter;

public class MainServicePom implements Writable {
    private final Document pom;

    public MainServicePom(Document originalPom) {
        MainPomCreator mainPomCreator = new MainPomCreator(originalPom);
        this.pom = mainPomCreator.build();
    }

    @Override
    public DataWriter createWriter() {
        return new MainPomWriter(pom);
    }

    public Document getPom() {
        return pom;
    }
}

package cullinanalternativeapproach;

import org.w3c.dom.Document;

public class MainServicePom implements Writable2 {
    private final Document pom;

    public MainServicePom(Document originalPom) {
        MainPomCreator mainPomCreator = new MainPomCreator(originalPom);
        this.pom = mainPomCreator.build();
    }

    @Override
    public DataWriter2 createWriter() {
        return new MainPomWriter(pom);
    }

    public Document getPom() {
        return pom;
    }
}

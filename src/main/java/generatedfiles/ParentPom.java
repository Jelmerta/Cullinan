package generatedfiles;

import cullinan.helpers.decomposition.javagenerators.ParentPomCreator;
import cullinan.helpers.decomposition.writers.DataWriter;
import cullinan.helpers.decomposition.writers.ParentPomWriter;
import input.Microservice;
import org.w3c.dom.Document;

import java.util.List;

public class ParentPom implements Writable {
    private final String pom;
//    private final Document pom;

    public ParentPom(List<Microservice> microservices) {
        ParentPomCreator parentPomCreator = new ParentPomCreator(microservices);
        pom = parentPomCreator.build();
    }

    @Override
    public DataWriter createWriter() {
        return new ParentPomWriter(this);
    }

    public String getPom() {
        return pom;
    }
}

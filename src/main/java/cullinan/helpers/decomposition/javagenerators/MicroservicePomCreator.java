package cullinan.helpers.decomposition.javagenerators;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class MicroservicePomCreator {
    private static final String DEPENDENCY_ARTIFACT_ID = "client";
    private static final String DEPENDENCY_VERSION = "1.0-SNAPSHOT";
    private static final String DEPENDENCY_SCOPE = "compile";
    private final Document originalPom;
    private final String microserviceName;

    public MicroservicePomCreator(Document originalPom, String microserviceName) {
        Element documentElement = originalPom.getDocumentElement();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document clonedDocument = db.newDocument();
        Node copiedRoot = clonedDocument.importNode(documentElement, true);
        clonedDocument.appendChild(copiedRoot);

        this.originalPom = clonedDocument;
        this.microserviceName = microserviceName;

        try {
            buildMicroservicePom();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO Add output path?
    public Document buildMicroservicePom() throws ParserConfigurationException, SAXException, IOException {
//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        DocumentBuilder db = dbf.newDocumentBuilder();
//        Document doc = db.parse(originalPom);

        originalPom.getDocumentElement().normalize();

        Node project = originalPom.getElementsByTagName("project").item(0);

        NodeList projectElements = project.getChildNodes();

        String groupId = null;
        for (int temp = 0; temp < projectElements.getLength(); temp++) {
            Node node = projectElements.item(temp);
            node.normalize();

            if (node.getNodeName().equalsIgnoreCase("name")) {
                node.setTextContent(microserviceName);
            }

            if (node.getNodeName().equalsIgnoreCase("artifactId")) {
                node.setTextContent(microserviceName.toLowerCase());
            }

//            What do we do here again? Is this necessary?
            if (node.getNodeName().equalsIgnoreCase("groupId")) {
                groupId = node.getTextContent(); // TODO Normalize?
            }
        }

        addDependency(originalPom, groupId);
        return originalPom;
    }

    // White space is a bit hacky rn...
    private static void addDependency(Document document, String dependencyGroupId) {
        Node project = document.getElementsByTagName("project").item(0);
        NodeList childNodes = project.getChildNodes();
        for (int temp = 0; temp < childNodes.getLength(); temp++) {
            Node item = childNodes.item(temp);
            if (item.getNodeName().equalsIgnoreCase("dependencies")) {
                item.appendChild(document.createTextNode("\t"));

                Element dependency = document.createElement("dependency");
                dependency.setTextContent("\n\t\t\t");

                Node groupId = document.createElement("groupId");
                groupId.setTextContent(dependencyGroupId);
                dependency.appendChild(groupId);
                dependency.appendChild(document.createTextNode("\n\t\t\t"));

                Node artifactId = document.createElement("artifactId");
                artifactId.setTextContent(DEPENDENCY_ARTIFACT_ID);
                dependency.appendChild(artifactId);
                dependency.appendChild(document.createTextNode("\n\t\t\t"));


                Node version = document.createElement("version");
                version.setTextContent(DEPENDENCY_VERSION);
                dependency.appendChild(version);
                dependency.appendChild(document.createTextNode("\n\t\t\t"));

                Node scope = document.createElement("scope");
                scope.setTextContent(DEPENDENCY_SCOPE);
                dependency.appendChild(scope);
                dependency.appendChild(document.createTextNode("\n\t\t"));

                item.appendChild(dependency);
                item.appendChild(document.createTextNode("\n\t"));
            }
        }
    }
}

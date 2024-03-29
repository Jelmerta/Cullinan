package cullinan.helpers.decomposition.javagenerators;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainPomCreator {
    private static final String DEPENDENCY_ARTIFACT_ID = "serviceinterfaces";
    private static final String DEPENDENCY_VERSION = "1.0-SNAPSHOT";
    private static final String DEPENDENCY_SCOPE = "compile";
    private final Document pom;
    private static final String RELATIVE_PATH = "../pom.xml";

    public MainPomCreator(Document pom) {
        Element documentElement = pom.getDocumentElement();

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

        this.pom = clonedDocument;
    }

    public Document build() {
        createPom();
        return pom;
    }

    private void createPom() {
        pom.getDocumentElement().normalize();

        Node project = pom.getElementsByTagName("project").item(0);

        NodeList projectElements = project.getChildNodes();

        String groupId = null;
        for (int temp = 0; temp < projectElements.getLength(); temp++) {
            Node node = projectElements.item(temp);
            node.normalize();

            if (node.getNodeName().equalsIgnoreCase("groupId")) {
                groupId = node.getTextContent(); // TODO Normalize?
            }
        }

//        addInterfaceDependency(groupId);
        addParent(project);
        addInterfaceDependency("MyMicroservicesProjects");
    }

    private void addInterfaceDependency(String dependencyGroupId) {
        Node project = pom.getElementsByTagName("project").item(0);
        NodeList childNodes = project.getChildNodes();


//        First make sure that dependencies exist
        boolean dependenciesFound = false;
        for (int temp = 0; temp < childNodes.getLength(); temp++) {
            Node item = childNodes.item(temp);
            if (item.getNodeName().equalsIgnoreCase("dependencies")) {
                dependenciesFound = true;
            }
        }

        if (!dependenciesFound) {
            Element dependencies = pom.createElement("dependencies");
            dependencies.setTextContent("\n\t\t\t");
            dependencies.appendChild(pom.createTextNode("\t"));
            project.appendChild(dependencies);
        }

        for (int temp = 0; temp < childNodes.getLength(); temp++) {
            Node item = childNodes.item(temp);
            if (item.getNodeName().equalsIgnoreCase("dependencies")) {
                item.appendChild(pom.createTextNode("\t"));

                Element dependency = pom.createElement("dependency");
                dependency.setTextContent("\n\t\t\t");

                Node groupId = pom.createElement("groupId");
                groupId.setTextContent(dependencyGroupId);
                dependency.appendChild(groupId);
                dependency.appendChild(pom.createTextNode("\n\t\t\t"));

                Node artifactId = pom.createElement("artifactId");
                artifactId.setTextContent(DEPENDENCY_ARTIFACT_ID);
                dependency.appendChild(artifactId);
                dependency.appendChild(pom.createTextNode("\n\t\t\t"));


                Node version = pom.createElement("version");
                version.setTextContent(DEPENDENCY_VERSION);
                dependency.appendChild(version);
                dependency.appendChild(pom.createTextNode("\n\t\t\t"));

                Node scope = pom.createElement("scope");
                scope.setTextContent(DEPENDENCY_SCOPE);
                dependency.appendChild(scope);
                dependency.appendChild(pom.createTextNode("\n\t\t"));

                item.appendChild(dependency);
                item.appendChild(pom.createTextNode("\n\t"));
            }
        }
    }

    private void addParent(Node project) {
        Node parent = pom.createElement("parent");
        parent.appendChild(pom.createTextNode("\t"));

        parent.setTextContent("\n\t\t\t");

        Node groupId = pom.createElement("groupId");
        groupId.setTextContent("MyMicroservicesProjects");
        parent.appendChild(groupId);
        parent.appendChild(pom.createTextNode("\n\t\t\t"));

        Node artifactId = pom.createElement("artifactId");
        artifactId.setTextContent("microservices");
        parent.appendChild(artifactId);
        parent.appendChild(pom.createTextNode("\n\t\t\t"));

        Node version = pom.createElement("version");
        version.setTextContent(DEPENDENCY_VERSION);
        parent.appendChild(version);
        parent.appendChild(pom.createTextNode("\n\t\t\t"));

        Node relativePath = pom.createElement("relativePath");
        relativePath.setTextContent(RELATIVE_PATH);
        parent.appendChild(relativePath);
        parent.appendChild(pom.createTextNode("\n\t\t\t"));

        project.appendChild(parent);
        project.appendChild(pom.createTextNode("\n\t"));

    }
}

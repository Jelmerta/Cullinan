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
    private static final String DEPENDENCY_ARTIFACT_ID = "serviceinterfaces";
    private static final String DEPENDENCY_VERSION = "1.0-SNAPSHOT";
    private static final String DEPENDENCY_SCOPE = "compile";
    private static final String RELATIVE_PATH = "../pom.xml";

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

//            Jar does not seem to resolve dependencies correctly... Not sure yet why
//            https://stackoverflow.com/questions/1677473/maven-doesnt-recognize-sibling-modules-when-running-mvn-dependencytree
//            TODO Do we need to add this if not available?
            if (node.getNodeName().equalsIgnoreCase("packaging")) {
                node.setTextContent("pom");
            }

//             TODO There could be none... Maybe just first remove then add at end?
            if (node.getNodeName().equalsIgnoreCase("parent")) {
                project.removeChild(node);
            }

//            What do we do here again? Is this necessary?
//            TODO Can be empty, obtained through parent... Maybe define a default... Can we find it elsewhere?
//            if (node.getNodeName().equalsIgnoreCase("groupId")) {
//                groupId = node.getTextContent(); // TODO Normalize?
//            }
            groupId = "MyMicroservicesProjects";
        }

        addParent(project);
        addDependency(originalPom, groupId);
        return originalPom;
    }

//    <parent>
//        <groupId>MyMicroservicesProjects</groupId>
//        <artifactId>microservices</artifactId>
//        <version>1.0-SNAPSHOT</version>
//        <relativePath>../pom.xml</relativePath>
//    </parent>
    private void addParent(Node project) {
        Node parent = originalPom.createElement("parent");
        parent.appendChild(originalPom.createTextNode("\t"));

        parent.setTextContent("\n\t\t\t");

        Node groupId = originalPom.createElement("groupId");
        groupId.setTextContent("MyMicroservicesProjects");
        parent.appendChild(groupId);
        parent.appendChild(originalPom.createTextNode("\n\t\t\t"));

        Node artifactId = originalPom.createElement("artifactId");
        artifactId.setTextContent("microservices");
        parent.appendChild(artifactId);
        parent.appendChild(originalPom.createTextNode("\n\t\t\t"));

        Node version = originalPom.createElement("version");
        version.setTextContent(DEPENDENCY_VERSION);
        parent.appendChild(version);
        parent.appendChild(originalPom.createTextNode("\n\t\t\t"));

        Node relativePath = originalPom.createElement("relativePath");
        relativePath.setTextContent(RELATIVE_PATH);
        parent.appendChild(relativePath);
        parent.appendChild(originalPom.createTextNode("\n\t\t\t"));

        project.appendChild(parent);
        project.appendChild(originalPom.createTextNode("\n\t"));

    }

    // White space is a bit hacky rn...
    private static void addDependency(Document document, String dependencyGroupId) {
        Node project = document.getElementsByTagName("project").item(0);
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
            Element dependencies = document.createElement("dependencies");
            dependencies.setTextContent("\n\t\t\t");
            dependencies.appendChild(document.createTextNode("\t"));
            project.appendChild(dependencies);
        }



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

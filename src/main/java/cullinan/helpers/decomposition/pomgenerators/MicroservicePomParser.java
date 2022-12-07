package cullinan.helpers.decomposition.pomgenerators;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

public class MicroservicePomParser {
    // TODO Hardcoded right now
    private static final String pomPath = "/home/jelmer/Documents/Software Engineering/Master Project/projects/Cullinan/Cullinan/dddsample-core-master/pom.xml";
    private static final String resultPath = "/home/jelmer/Documents/Software Engineering/Master Project/projects/Cullinan/Cullinan/dddsample-core-master/pom2.xml";
    private static final String DEPENDENCY_ARTIFACT_ID = "client";
    private static final String DEPENDENCY_VERSION = "1.0-SNAPSHOT";
    private static final String DEPENDENCY_SCOPE = "compile";
    private static String dependencyGroupId;
    private final File originalPom;
    private final String microserviceName;

    public static void main(String[] args) throws Exception {
        MicroservicePomParser locationPomParser = new MicroservicePomParser(new File(pomPath), "se.citerus", "Location");
        locationPomParser.buildMicroservicePom();
    }

    public MicroservicePomParser(File originalPom, String packageRoot, String microserviceName) {
        this.originalPom = originalPom;
        this.microserviceName = microserviceName;
        this.dependencyGroupId = packageRoot;

        try {
            buildMicroservicePom();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    // TODO Add output path?
    public void buildMicroservicePom() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder db = dbf.newDocumentBuilder();

        Document doc = db.parse(originalPom);

        doc.getDocumentElement().normalize();

        Node project = doc.getElementsByTagName("project").item(0);

        NodeList projectElements = project.getChildNodes();

        for (int temp = 0; temp < projectElements.getLength(); temp++) {
            Node node = projectElements.item(temp);
            node.normalize();

            if (node.getNodeName().equalsIgnoreCase("name")) {
                node.setTextContent(microserviceName);
            }

            if (node.getNodeName().equalsIgnoreCase("artifactId")) {
                node.setTextContent(microserviceName.toLowerCase());
            }
        }

        addDependency(doc);

        // TODO Add dependency on the right modules (probably only client?) We could, if time allows remove unnecessary dependencies.

        try (FileOutputStream output = new FileOutputStream(resultPath)) {
            writeXml(doc, output);
        } catch (IOException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    // White space is a bit hacky rn...
    private static void addDependency(Document document) {
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

    // Has issues with formatting still... Text nodes have line breaks causing inconsistent formatting.
    private static void writeXml(Document doc, OutputStream output) throws TransformerException {
//        doc.normalize(); is this supposed to remove white space?

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
    }
}
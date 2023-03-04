package cullinan.helpers.decomposition.javagenerators;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

// TODO Maybe we need a parent pom in order to resolve each others microservices...
public class ServiceInterfacePomCreator {
//     TODO HARDCODED GROUPID?

    private static final String INTERFACE_POM = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
            "    <modelVersion>4.0.0</modelVersion>\n" +
            "\n" +
            "    <parent>\n" +
            "        <groupId>MyMicroservicesProjects</groupId>\n" +
            "        <artifactId>microservices</artifactId>\n" +
            "        <version>1.0-SNAPSHOT</version>\n" +
            "        <relativePath>../pom.xml</relativePath>\n" +
            "    </parent>\n" +
            "\n" +
            "    <groupId>MyMicroservicesProjects</groupId>\n" +
            "    <artifactId>serviceinterfaces</artifactId>\n" +
            "    <name>ServiceInterfaces</name>\n" +
            "    <version>1.0-SNAPSHOT</version>\n" +
            "    <url>http://dddsample.sourceforge.net</url>\n" +
            "    <properties>\n" +
            "        <java.version>17</java.version>\n" +
            "        <maven.compiler.source>17</maven.compiler.source>\n" +
            "        <maven.compiler.target>17</maven.compiler.target>\n" +
            "    </properties>\n" +
            "    <dependencies>\n" +
            "    </dependencies>\n" +
            "</project>";

//     TODO Group ID should be dynamically set for each project
//TODO What version do we use?
//        <properties>
//        <maven.compiler.source>1.8</maven.compiler.source>
//        <maven.compiler.target>1.8</maven.compiler.target>
//    </properties>
    public ServiceInterfacePomCreator() {

    }

    public Document build() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputStream inputStream = new ByteArrayInputStream(INTERFACE_POM.getBytes());
            return db.parse(inputStream);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}

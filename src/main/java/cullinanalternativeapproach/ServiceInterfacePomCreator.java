package cullinanalternativeapproach;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ServiceInterfacePomCreator {
    private static final String INTERFACE_POM = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
            "    <modelVersion>4.0.0</modelVersion>\n" +
            "    <groupId>se.citerus</groupId>\n" +
            "    <artifactId>client</artifactId>\n" +
            "    <name>Client</name>\n" +
            "    <version>1.0-SNAPSHOT</version>\n" +
            "    <url>http://dddsample.sourceforge.net</url>\n" +
            "    <properties>\n" +
            "        <java.version>1.8</java.version>\n" +
            "    </properties>\n" +
            "    <dependencies>\n" +
            "    </dependencies>\n" +
            "</project>";
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

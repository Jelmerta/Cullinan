package cullinan.helpers.decomposition.javagenerators;

import generatedfiles.ServiceDefinition;
import input.Microservice;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class ParentPomCreator {
    private String parentPom = "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
            "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0\n" +
            "                             http://maven.apache.org/maven-v4_0_0.xsd\">\n" +
            "    <modelVersion>4.0.0</modelVersion>\n" +
            "\n" +
            "    <properties>\n" +
            "        <java.version>17</java.version>\n" +
            "        <java.release.version>17</java.release.version>\n" +
            "        <maven.compiler.source>17</maven.compiler.source>\n" +
            "        <maven.compiler.target>17</maven.compiler.target>\n" +
            "    </properties>\n" +
            "\n" +
            "    <groupId>MyMicroservicesProjects</groupId>\n" +
            "    <artifactId>microservices</artifactId>\n" +
            "    <packaging>pom</packaging>\n" +
            "    <version>1.0-SNAPSHOT</version>\n" +
            "    <name>Microservices</name>\n" +
            "\n" +
            "    <modules>\n" +
            "        <module>serviceinterfaces</module>\n" +
            "<SERVICESHERE>" +
            "    </modules>\n" +
            "</project>";

    private final List<String> serviceNames;

    public ParentPomCreator(List<Microservice> microservices) {
        this.serviceNames = microservices.stream()
                .map(Microservice::getName)
                .collect(Collectors.toList());
    }

    public String build() {
        StringBuilder serviceModules = new StringBuilder();
        for (String serviceName : serviceNames) {
            String serviceModule = "    <module>" + serviceName+ "</module>\n";
            serviceModules.append(serviceModule);
        }
        parentPom = parentPom.replace("<SERVICESHERE>", serviceModules.toString());
        return parentPom;

//        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        try {
//            DocumentBuilder db = dbf.newDocumentBuilder();
//            InputStream inputStream = new ByteArrayInputStream(parentPom.getBytes());
//            return db.parse(inputStream);
//        } catch (ParserConfigurationException | IOException | SAXException e) {
//            throw new RuntimeException(e);
//        }
    }
}

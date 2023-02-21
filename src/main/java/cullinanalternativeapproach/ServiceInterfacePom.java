package cullinanalternativeapproach;

import org.w3c.dom.Document;


//<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
//         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
//    <modelVersion>4.0.0</modelVersion>
//    <groupId>se.citerus</groupId>
//    <artifactId>client</artifactId>
//    <name>Client</name>
//    <version>1.0-SNAPSHOT</version>
//    <url>http://dddsample.sourceforge.net</url>
//    <properties>
//        <java.version>1.8</java.version>
//    </properties>
//    <dependencies>
//        <dependency>
//            <groupId>org.seleniumhq.selenium</groupId>
//            <artifactId>htmlunit-driver</artifactId>
//        </dependency>
//    </dependencies>
//</project>
public class ServiceInterfacePom implements Writable2 {
    private final Document pom;

    public ServiceInterfacePom() {
        ServiceInterfacePomCreator serviceInterfacePomCreator = new ServiceInterfacePomCreator();
        this.pom = serviceInterfacePomCreator.build();
    }

    @Override
    public DataWriter2 createWriter() {
        return new ServiceInterfacePomWriter(pom);
    }

    public Document getPom() {
        return pom;
    }
}

package dev.aftermath.cli.attach;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

public class PomXmlInjector {

    public boolean injectSdkDependency(File pomXml) throws Exception {
        if (pomXml == null || !pomXml.exists()) {
            throw new IllegalArgumentException("pom.xml file does not exist");
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        // Secure XML Parsing to prevent XXE (XML External Entity) Injection
        dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(pomXml);
        doc.getDocumentElement().normalize();

        if (hasAftermathSdk(doc)) {
            return false; // Already present
        }

        NodeList dependenciesList = doc.getElementsByTagName("dependencies");
        Element dependenciesNode;

        if (dependenciesList.getLength() > 0) {
            dependenciesNode = (Element) dependenciesList.item(0);
        } else {
            dependenciesNode = doc.createElement("dependencies");
            doc.getDocumentElement().appendChild(dependenciesNode);
        }

        Element dependencyNode = doc.createElement("dependency");

        Element groupId = doc.createElement("groupId");
        groupId.appendChild(doc.createTextNode("dev.aftermath"));
        dependencyNode.appendChild(groupId);

        Element artifactId = doc.createElement("artifactId");
        artifactId.appendChild(doc.createTextNode("aftermath-sdk"));
        dependencyNode.appendChild(artifactId);

        Element version = doc.createElement("version");
        version.appendChild(doc.createTextNode("0.1.0-SNAPSHOT"));
        dependencyNode.appendChild(version);

        dependenciesNode.appendChild(dependencyNode);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(pomXml);
        transformer.transform(source, result);

        return true;
    }

    private boolean hasAftermathSdk(Document doc) {
        NodeList depList = doc.getElementsByTagName("dependency");
        for (int i = 0; i < depList.getLength(); i++) {
            Node node = depList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                NodeList artifactIdList = elem.getElementsByTagName("artifactId");
                if (artifactIdList.getLength() > 0 && "aftermath-sdk".equals(artifactIdList.item(0).getTextContent().trim())) {
                    return true;
                }
            }
        }
        return false;
    }
}

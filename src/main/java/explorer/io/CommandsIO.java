/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package explorer.io;

import general.bl.GlobalParamter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author Anna Lechner
 */
public class CommandsIO {

    /**
     * Method to read all command types out of types.conf
     * @return
     * @throws IOException 
     */
    public static List<String> readCommandTypes() throws IOException {
        Path typePath = Paths.get(GlobalParamter.getInstance().getExplorerResPath().toString(), "types.conf");
        return Files.readAllLines(typePath);
    }

    /**
     * Method to add a new command type to types.conf
     * @param newType
     * @throws IOException 
     */
    public static void addToTypes(String newType) throws IOException {
        Path typePath = Paths.get(GlobalParamter.getInstance().getExplorerResPath().toString(), "types.conf");
        BufferedWriter bw = new BufferedWriter(new FileWriter(typePath.toFile(), true));
        bw.newLine();
        bw.write(newType);
        bw.close();
    }

    /**
     * Method to add a newly added command to commands.xml
     * @param classname
     * @param displayName
     * @param subnodes
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws TransformerException
     * @throws XPathExpressionException 
     */
    public static void addNewCommand(String classname, String displayName, Map<String, String> subnodes) 
            throws ParserConfigurationException, SAXException, IOException, TransformerException, XPathExpressionException {
        Path commandsPath = Paths.get(GlobalParamter.getInstance().getExplorerResPath().toString(), "commands.xml");
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document document = docBuilder.parse(commandsPath.toFile());

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        XPathExpression expression = xpath.compile("/commands");
        Node root = (Node) expression.evaluate(document, XPathConstants.NODE);
//        root.appendChild(document.createTextNode("\n    "));

        Element command = document.createElement("command");
        command.setAttribute("class", classname);
        command.setAttribute("displayname", displayName);
        for (String string : subnodes.keySet()) {
            Element element = document.createElement(string);
            String[] attributes = subnodes.get(string).split(";");
            element.setAttribute("displayname", attributes[0]);
            element.setAttribute("type", attributes[1]);
            element.appendChild(document.createTextNode(" "));
            command.appendChild(element);
        }
        root.appendChild(command);

        writeXml(document, new StreamResult(commandsPath.toFile()));
    }
    
    /**
     * Outsourced method to write an xml file to the handed over (file)
     * destination
     *
     * @param document
     * @param streamResult
     * @throws TransformerException
     */
    private static void writeXml(Document document, StreamResult streamResult) throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        DOMSource domSource = new DOMSource(document);
        transformer.transform(domSource, streamResult);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author flori
 */
public class IOChecker {

    /**
     * Method that checks if a bon is valid
     *
     * @param path
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static boolean checkValidBon(Path path) throws ParserConfigurationException, SAXException, IOException {
        if (new File(path.toString()).getAbsolutePath().endsWith(".xml")) {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(path.toFile());
            NodeList listNode = doc.getElementsByTagName("bon");
            try {
                NodeList nodes = listNode.item(0).getChildNodes();
            } catch (NullPointerException ex) {
                return false;
            }

            NodeList sysCommands = doc.getElementsByTagName("controlkey");
            for (int i = 0; i < sysCommands.getLength(); i++) {
                if (sysCommands.item(i).getAttributes().getNamedItem("function").
                        getNodeValue().toLowerCase().equals("bonstorno")) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Method that filters the marker name of a bon
     *
     * @param path
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static Node getMarkerName(Path path) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(path.toFile());
        Node markerNode = doc.getElementsByTagName("mark").item(0).getAttributes().getNamedItem("name");

        return markerNode;
    }

    /**
     * Method that checks if a bon contains an ELoading article
     *
     * @param path
     * @return
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     */
    public static boolean containsEloadingArticle(Path path) throws SAXException, ParserConfigurationException, IOException {
        boolean returnValue = false;

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(path.toFile());
        NodeList sysCommands = doc.getElementsByTagName("item");
        for (int i = 0; i < sysCommands.getLength(); i++) {
            if (sysCommands.item(i).getAttributes().getNamedItem("eloading_func") != null) {
                returnValue = true;
            }
        }
        return returnValue;
    }
}

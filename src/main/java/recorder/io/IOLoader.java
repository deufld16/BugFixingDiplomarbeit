/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.io;

import recorder.beans.Article;
import recorder.beans.Command;
import recorder.beans.DynamicFunction;
import recorder.guiOperations.GUIOperations;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import recorder.beans.AndereZahlungsmittel;
import recorder.beans.DynamicValue;
import recorder.beans.ListItem;

/**
 *
 * @author Maxi
 */
public class IOLoader {

    public static Map<String, DynamicFunction> functions = new HashMap<>();

    /**
     * Method to load an icon
     *
     * @param imageName
     * @param width
     * @param height
     * @return eingelesenes Icon
     * @throws IOException Methode, welche die Images mit der entsprechenden
     * Höhe und Breite aus dem res/img Verzeichnis ladet
     */
    public static ImageIcon loadImage(String imageName, int width, int height) throws IOException {
        String imagePathStr = GUIOperations.getResPath().toString() + File.separator + "img"
                + File.separator + imageName;
        Image img = ImageIO.read(new File(imagePathStr));
        img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(img);
        return icon;
    }

    /**
     * Method to load all articles from itemmapper.xml
     *
     * @return
     * @throws Exception
     */
    public static List<Article> loadArtikel() throws Exception {
        List<Article> returnArtikel = new LinkedList<>();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(Paths.get(GUIOperations.getResPath().toString(), "itemmapper.xml").toFile());
        NodeList listNode = doc.getElementsByTagName("item");

        for (int i = 0; i < listNode.getLength(); i++) {
            if (listNode.item(i).getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) listNode.item(i);
                returnArtikel.add(new Article(el.getAttribute("article"), el.getAttribute("d_c_minus").equals("true"), el.getAttribute("d_size").equals("true"),
                        el.getAttribute("ean"), el.getAttribute("name"), el.getAttribute("pfand").equals("true"), el.getAttribute("pfandart"), Double.parseDouble(el.getAttribute("preis")),
                        el.getAttribute("rabatt").equals("true"), Integer.parseInt(el.getAttribute("ust")),
                        el.getAttribute("weight").equals("true"), el.getAttribute("category"), el.getAttribute("mwd").equals("true"), el.getAttribute("serialnr").equals("true")));
            }
        }
        return returnArtikel;
    }

    /**
     * Method to load all dynamic functions from functionmapper.xml
     *
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static void loadFunctionMapperXML() throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(Paths.get(GUIOperations.getResPath().toString(), "functionmapper.xml").toFile());
        NodeList listNode = doc.getElementsByTagName("items");
        NodeList nodes = listNode.item(0).getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            DynamicFunction dynfun = new DynamicFunction();
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                try {
                    dynfun.setBtnName(nodes.item(i).getAttributes().getNamedItem("buttonName").getNodeValue());
                    dynfun.setViewText(nodes.item(i).getAttributes().getNamedItem("displayText").getNodeValue());
                    HashMap<String, Object> attributes = new HashMap<>();
                    NodeList subnodes = nodes.item(i).getChildNodes();
                    for (int k = 0; k < subnodes.getLength(); k++) {
                        if (subnodes.item(k).getNodeType() == Node.ELEMENT_NODE) {
                            if (subnodes.item(k).getAttributes().getNamedItem("valueType").getNodeValue().equals("LIST")) {
                                List<ListItem> items = new ArrayList<>();
                                NodeList itemList = subnodes.item(k).getChildNodes();
                                for (int j = 0; j < itemList.getLength(); j++) {
                                    if (itemList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                                        HashMap<String, Object> subAs = new HashMap<>();
                                        NodeList atrs = itemList.item(j).getChildNodes();
                                        for (int l = 0; l < atrs.getLength(); l++) {
                                            if (atrs.item(l).getNodeType() == Node.ELEMENT_NODE) {
                                                subAs.put(atrs.item(l).getNodeName(), new DynamicValue(atrs.item(l).getAttributes().getNamedItem("displayText").getNodeValue(), atrs.item(l).getAttributes().getNamedItem("valueType").getNodeValue()));
                                            }
                                        }
                                        ListItem it = new ListItem(itemList.item(j).getAttributes().getNamedItem("valueType").getNodeValue(),itemList.item(j).getAttributes().getNamedItem("displayText").getNodeValue(), subAs);
                                        items.add(it);

                                    }
                                }
                                attributes.put(subnodes.item(k).getNodeName(), new DynamicValue(subnodes.item(k).getAttributes().getNamedItem("displayText").getNodeValue(), items));
                            } else {
                                attributes.put(subnodes.item(k).getNodeName(),
                                        new DynamicValue(subnodes.item(k).getAttributes().getNamedItem("displayText").getNodeValue(), subnodes.item(k).getAttributes().getNamedItem("valueType").getNodeValue()));
                            }
                        }
                        dynfun.setCommand(new Command(nodes.item(i).getAttributes().getNamedItem("xml").getNodeValue(), attributes));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    dynfun.setBtnName("functionmapper.xml fehlerhaft");
                }
                functions.put(nodes.item(i).getNodeName(), dynfun);
            }
        }
    }

    /**
     * Method to load all authorization limits
     *
     * @return
     * @throws IOException
     */
    public static List<Double> getLimits() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(GUIOperations.getResPath().toString(), "limits.conf"));
        List<Double> res = new ArrayList<>();
        try {
            for (String line : lines) {
                res.add(Double.parseDouble(line.split("=")[1]));
            }
        } catch (ArrayIndexOutOfBoundsException a) {

        }
        return res;
    }

    /**
     * Method to load the other means of payment
     *
     * @return
     * @throws IOException
     */
    public static List<AndereZahlungsmittel> getAndereZahlungsmittel() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(GUIOperations.getResPath().toString(), "andere_zahlungsmittel.csv"));
        List<AndereZahlungsmittel> res = new ArrayList<>();
        int cnt = 0;
        for (String line : lines) {
            if (cnt != 0) {
                String[] parts = line.split(";");
                res.add(new AndereZahlungsmittel(parts[3].trim() + " (" + parts[0] + "_" + parts[1] + ")", Integer.parseInt(parts[0]), Integer.parseInt(parts[1])));
            }
            cnt++;
        }
        return res;
    }

    /**
     * Method that loads all descriptions of the existing simulation file types
     *
     * @return
     * @throws IOException
     */
    public static List<String> getExtensionsDescription() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(GUIOperations.getResPath().toString(), "simulationtyp.conf"));
        List<String> res = new ArrayList<>();
        try {
            for (String line : lines) {
                res.add(line.split("=")[1]);
            }
        } catch (ArrayIndexOutOfBoundsException a) {

        }
        return res;
    }

    /**
     * Method that loads all existing simulation file types
     *
     * @return
     * @throws IOException
     */
    public static List<String> getExtensions() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(GUIOperations.getResPath().toString(), "simulationtyp.conf"));
        List<String> res = new ArrayList<>();
        try {
            for (String line : lines) {
                res.add(line.split("=")[0]);
            }
        } catch (ArrayIndexOutOfBoundsException a) {

        }
        return res;
    }

    /**
     * Method that loads all authorization limits for "Sofortstorno"
     *
     * @return
     */
    public static int getSSTOLim() {
        try {
            return (int) (getLimits().get(0) * 100);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    /**
     * Method that loads all authorization limits for "Zeilenstorno"
     *
     * @return
     */
    public static int getMSTOLim() {
        try {
            return (int) (getLimits().get(1) * 100);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    /**
     * Method that loads all authorization limits for "Geschenkkarten"
     *
     * @return
     */
    public static int getGeschenkkarteLimit() {
        try {
            return (int) (getLimits().get(3) * 100);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    /**
     * Method that loads all authorization limit for "Bonstorno"
     *
     * @return
     */
    public static int getBonSTOLim() {
        try {
            return (int) (getLimits().get(2) * 100);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return -1;
    }
    
    public static Map<String, DynamicFunction> getFunctions() {
        return functions;
    }

    public static void setFunctions(Map<String, DynamicFunction> functions) {
        IOLoader.functions = functions;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.io;

import analyzer.beans.WhitelistEntry;
import analyzer.enums.ResultFileType;
import dashboard.bl.DatabaseGlobalAccess;
import general.bl.GlobalParamter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import simulator.beans.Backoffice;
import simulator.beans.Kasse;
import simulator.beans.Testsystem;

/**
 * Class that handles the storage process of different configuration files
 *
 * @author Lukas Krobath
 * @author Maximilian Strohmaier
 */
public class Saver {

    /**
     * *
     * Method that stores the current whitelist configuration at
     * settings/res/whitelist.xml
     */
    public static void saveWhitelistXml() {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.newDocument();
            Element elRoot = document.createElement("whitelist");

            List<WhitelistEntry> entries = GlobalParamter.getInstance().getWhitelistEntries();

            for (WhitelistEntry entry : entries) {
                Element elEntry = document.createElement("entry");

                Element elTitle = document.createElement("title");
                elTitle.setTextContent(entry.getDescription());
                elEntry.appendChild(elTitle);

                Element elRegex = document.createElement("regex");
                elRegex.setTextContent(entry.getRegex());
                elEntry.appendChild(elRegex);

                for (ResultFileType applicationType : entry.getApplicationTypes()) {
                    Element elType = document.createElement("type");
                    elType.setTextContent(applicationType.name());
                    elEntry.appendChild(elType);
                }

                elRoot.appendChild(elEntry);
            }

            document.appendChild(elRoot);
            writeXml(document, new StreamResult(Paths.get(
                    System.getProperty("user.dir"), "src", "main",
                    "java", "settings", "res", "whitelist.xml").toFile()));

        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (TransformerException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method to save all configured cashpoints
     *
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public static void saveCashpoints() throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element root = document.createElement("kassen");
        for (Kasse kassa : GlobalParamter.getInstance().getKassen()) {
            Element kasse = document.createElement("kassa");

            Element ip = document.createElement("ip");
            ip.setTextContent(kassa.getStrIpAdr());
            kasse.appendChild(ip);

            Element id = document.createElement("id");
            id.setTextContent(kassa.getiRegId() + "");
            kasse.appendChild(id);

            Element group = document.createElement("group");
            group.setTextContent(kassa.getiRegGrp() + "");
            kasse.appendChild(group);

            Element type = document.createElement("type");
            type.setTextContent(kassa.getType() + "");
            kasse.appendChild(type);

            root.appendChild(kasse);

        }
        document.appendChild(root);
        writeXml(document, new StreamResult(Paths.get(System.getProperty("user.dir"), "src", "main", "java", "settings", "res", "kassen.xml").toFile()));
    }

    /**
     * Method to save all configured test systems
     *
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public static void saveTestsysteme() throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element root = document.createElement("testsysteme");
        for (Testsystem test : GlobalParamter.getInstance().getTestsysteme()) {
            Element testsystem = document.createElement("testsystem");

            Element backoffice = document.createElement("backoffice");
            backoffice.setTextContent(test.getBackoffice().getStrIpAdr());
            testsystem.appendChild(backoffice);

            for (Kasse kassa : test.getKassen()) {
                Element kasse = document.createElement("kassa");

                Element ip = document.createElement("ip");
                ip.setTextContent(kassa.getStrIpAdr());
                kasse.appendChild(ip);

                Element id = document.createElement("id");
                id.setTextContent(kassa.getiRegId() + "");
                kasse.appendChild(id);

                Element group = document.createElement("group");
                group.setTextContent(kassa.getiRegGrp() + "");
                kasse.appendChild(group);

                Element type = document.createElement("type");
                type.setTextContent(kassa.getType() + "");
                kasse.appendChild(type);

                testsystem.appendChild(kasse);
            }

            Element active = document.createElement("active");
            active.setTextContent(test.isActive() + "");
            testsystem.appendChild(active);

            Element name = document.createElement("name");
            name.setTextContent(test.getName());
            testsystem.appendChild(name);

            root.appendChild(testsystem);

        }
        document.appendChild(root);
        writeXml(document, new StreamResult(Paths.get(System.getProperty("user.dir"), "src", "main", "java", "settings", "res", "testsysteme.xml").toFile()));
    }

    /**
     * Method to save all configured backoffices
     *
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public static void saveBackoffices() throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Element root = document.createElement("backoffices");
        for (Backoffice back : GlobalParamter.getInstance().getBackoffices()) {
            Element backoffice = document.createElement("backoffice");

            Element ip = document.createElement("ip");
            ip.setTextContent(back.getStrIpAdr());
            backoffice.appendChild(ip);

            root.appendChild(backoffice);

        }
        document.appendChild(root);
        writeXml(document, new StreamResult(Paths.get(System.getProperty("user.dir"), "src", "main", "java", "settings", "res", "backoffices.xml").toFile()));
    }

    /**
     * Method to save the last user
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void saveLastUser() throws FileNotFoundException, IOException {
//        if (GlobalParamter.getInstance().getSelected_user() != null) {
//            FileOutputStream fos = new FileOutputStream(Paths.get(GlobalParamter.getInstance().getGeneralResPath().toString(), "lastUser.ser").toFile());
//            ObjectOutputStream oos = new ObjectOutputStream(fos);
//            oos.writeObject(GlobalParamter.getInstance().getSelected_user());
//            oos.flush();
//            oos.close();
//        }
        if (DatabaseGlobalAccess.getInstance().getCurrentNutzer() != null) {
            FileOutputStream fos = new FileOutputStream(Paths.get(GlobalParamter.getInstance().getGeneralResPath().toString(), "lastUser.ser").toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(DatabaseGlobalAccess.getInstance().getCurrentNutzer());
            oos.flush();
            oos.close();
        }
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
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");
        DOMSource domSource = new DOMSource(document);
        transformer.transform(domSource, streamResult);
    }
}

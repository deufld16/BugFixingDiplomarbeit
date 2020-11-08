/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package explorer.io;

import dashboard.bl.DatabaseGlobalAccess;
import dashboard.database.DB_Access;
import dashboard.database.DB_Access_Manager;
import general.beans.io_objects.ExplorerLayer;
import general.beans.io_objects.ProjectRun;
import general.beans.io_objects.TestCaseRun;
import general.beans.io_objects.TestGroupRun;
import general.bl.GlobalParamter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
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
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Anna Lechner
 */
public class ExplorerIO {

    /**
     * Method to create a new project within the "projekt" folder and add the
     * appendant run.xml to it
     *
     * @param pro
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public static void addProject(ProjectRun pro) throws IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        //Create TG folder
        if (!pro.getPath().toFile().exists()) {
            Files.createDirectories(pro.getPath());
        }
        //Create erg, ref and run
        if (!Paths.get(pro.getPath().toString(), "erg").toFile().exists()) {
            Files.createDirectory(Paths.get(pro.getPath().toString(), "erg"));
            Files.createDirectory(Paths.get(pro.getPath().toString(), "ref"));
            Files.createDirectory(Paths.get(pro.getPath().toString(), "run"));
        }
        //Create run.xml for TG
        Element root = document.createElement("run");
        document.appendChild(root);

        createSubNodes(document, root, "description", pro.getDescription());
        writeXml(document, new StreamResult(Paths.get(pro.getPath().toString(), "run", "run.xml").toFile()));
    }

    /**
     * Method to create a new TG folder and add the appendant run.xml to it
     *
     * @param tg
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public static void addTestGroup(TestGroupRun tg) throws IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        //Create TG folder
        Files.createDirectory(tg.getPath());
        //Create run.xml for TG
        Element root = document.createElement("run");
        document.appendChild(root);

        createSubNodes(document, root, "description", tg.getDescription());
        createSubNodes(document, root, "empid", "201565");
        createSubNodes(document, root, "emppass", "1234");
        createSubNodes(document, root, "tllid", "12");
        writeXml(document, new StreamResult(Paths.get(tg.getPath().toString(), "run.xml").toFile()));
    }

    /**
     * Method to create a new TC folder and add the appendant run.xml to it
     *
     * @param tc
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public static void addTestCase(TestCaseRun tc) throws IOException, ParserConfigurationException, TransformerConfigurationException, TransformerException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        if (!tc.getPath().toFile().exists()) {
            //Create TC folder
            Files.createDirectory(tc.getPath());
        }
        //Create run.xml for TC
        Element root = document.createElement("run");
        document.appendChild(root);

        createSubNodes(document, root, "description", tc.getDescription());
        createSubNodes(document, root, "commands", "\n");
        writeXml(document, new StreamResult(Paths.get(tc.getPath().toString(), "run.xml").toFile()));
    }

    /**
     * Outsourced method to create a direct child of a root node with text
     *
     * @param document
     * @param root
     * @param nodeName
     * @param attr
     */
    private static void createSubNodes(Document document, Element root, String nodeName, String attr) {
        Element bsp = document.createElement(nodeName);
        bsp.appendChild(document.createTextNode(attr));
        root.appendChild(bsp);
    }

    /**
     * Method to remove a folder and all of its subfolders and -files
     *
     * @param f
     * @throws FileNotFoundException
     */
    public static void removeNode(File f) throws FileNotFoundException {
        if (f.exists()) {
            if (f.isDirectory()) {
                for (File c : f.listFiles()) {
                    removeNode(c);
                }
            }
            if (!f.delete()) {
                throw new FileNotFoundException("Failed to delete file: " + f);
            }
        }
    }

    /**
     * Method to remove a command from its appendant run.xml
     *
     * @param path
     * @param commandText
     * @throws TransformerException
     * @throws XPathExpressionException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static void removeCommandFromXML(Path path, String commandText) throws TransformerException, XPathExpressionException, ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document document = dbf.newDocumentBuilder().parse(path.toFile());

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        XPathExpression expression = xpath.compile("/run/commands/command/command_text[string(.)='" + commandText + "']");

        Node remNode = (Node) expression.evaluate(document, XPathConstants.NODE);
        remNode.getParentNode().getParentNode().removeChild(remNode.getParentNode());

        writeXml(document, new StreamResult(path.toFile()));
    }

    /**
     * Method to rename a file or directory
     *
     * @param oldFile
     * @param newFile
     */
    public static void renameFile(File oldFile, File newFile) {
        oldFile.renameTo(newFile);
    }

    /**
     * Method to change the project name in the init and the cleanup testgroup
     *
     * @param path
     * @param nodeName
     * @param newDescription
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws TransformerException
     */
    public static void changeDescriptionOfInitAndCleanup(Path path, String nodeName, String newDescription) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document document = dbf.newDocumentBuilder().parse(path.toFile());
        Node description = document.getElementsByTagName(nodeName).item(0);
        String existing = description.getTextContent().substring(description.getTextContent().indexOf("-") - 1);
        //update description attribute
        description.setTextContent(newDescription + existing);
        writeXml(document, new StreamResult(path.toFile()));
    }

    /**
     * Method to change a node of a run.xml of a testgroup Possible nodes:
     * description, empid, emppass, tllid
     *
     * @param path
     * @param nodeName
     * @param newDescription
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws TransformerException
     */
    public static void changeNodeOfTG(Path path, String nodeName, String newDescription) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(path.toFile());
        // Get the description element
        Node description = doc.getElementsByTagName(nodeName).item(0);
        //update description attribute
        description.setTextContent(newDescription);
        writeXml(doc, new StreamResult(path.toFile()));
    }

    /**
     * Method to change a node of a run.xml of a project
     *
     * @param path
     * @param nodeName
     * @param newDescription
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws TransformerException
     */
    public static void changeNodeOfProject(Path path, String nodeName, String newDescription) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException, InterruptedException {
        //change path to new path
        File toBeRenamed = path.toFile().getParentFile().getParentFile();
        Files.move(toBeRenamed.toPath(), Paths.get(toBeRenamed.getParent(), newDescription.toLowerCase()));
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        path = Paths.get(toBeRenamed.getParent(), newDescription.toLowerCase(), "run", "run.xml");
        Document doc = docBuilder.parse(path.toFile());
        // Get the description element
        Node description = doc.getElementsByTagName(nodeName).item(0);
        //update description attribute
        description.setTextContent(newDescription);
        writeXml(doc, new StreamResult(path.toFile()));
    }

    /**
     * Method to remove a command from the appendant run.xml
     *
     * @param path
     * @param index
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static Node removeFromXML(Path path, int index) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(path.toFile());
        NodeList listNode = doc.getElementsByTagName("command");
        List<Node> nodesToWrite_oldFile = new LinkedList<>();
        Node movedNode = null;
        for (int i = 0; i < listNode.getLength(); i++) {
            if (i == index) {
                movedNode = listNode.item(i);
            } else {
                nodesToWrite_oldFile.add(listNode.item(i));
            }
        }
        try {
            Files.delete(path);
            createXMLFile(nodesToWrite_oldFile, path, doc.getElementsByTagName("description").item(0));
        } catch (TransformerConfigurationException ex) {
            ex.printStackTrace();
        } catch (TransformerException ex) {
            ex.printStackTrace();
        }

        return movedNode;
    }

    /**
     * Method to create a new run.xml
     *
     * @param nodes
     * @param path
     * @param description
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public static void createXMLFile(List<Node> nodes, Path path, Node description) throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();

        Element root = document.createElement("run");
        Element commands = document.createElement("commands");
        Element desc = document.createElement("description");
        desc.appendChild(document.createTextNode(description.getTextContent()));
        root.appendChild(desc);
        root.appendChild(commands);
        document.appendChild(root);

        for (Node node : nodes) {
            Node help = document.adoptNode(node);
            commands.appendChild(help);
        }
        writeXml(document, new StreamResult(path.toFile()));
    }

    /**
     * Method to rename all TCs when a TGs is dragged & dropped / renamed
     *
     * @param path
     * @param allBeansToRename
     */
    public static void renameTestCases(Path path, List<DefaultMutableTreeNode> allBeansToRename) {
        File[] directoriesToRename = new File(path.toString()).listFiles(File::isDirectory);
        ExplorerLayer beansToRename = null;
        if (directoriesToRename != null) {
            int testCaseStartIndex = 1;
            for (File directory : directoriesToRename) {
                for (DefaultMutableTreeNode node_to_rename : allBeansToRename) {
                    ExplorerLayer tmp = (ExplorerLayer) node_to_rename.getUserObject();
                    if (tmp.getPath().toString().equalsIgnoreCase(directory.getPath())) {
                        beansToRename = tmp;
                    }
                }
                if (directory.getName().toUpperCase().contains("INIT")) {
                    if (!directory.getName().equalsIgnoreCase("000_INIT")) {
                        directory.renameTo(Paths.get(directory.getParent(), "000_INIT").toFile());
                        beansToRename.setPath(Paths.get(directory.getParent(), "000_INIT"));
                    }
                } else if (directory.getName().toUpperCase().contains("CLEANUP")) {
                    if (!directory.getName().equalsIgnoreCase("999_CLEANUP")) {
                        directory.renameTo(Paths.get(directory.getParent(), "999_CLEANUP").toFile());
                        beansToRename.setPath(Paths.get(directory.getParent(), "999_CLEANUP"));
                    }
                } else {
                    try {
                        String preperationForParse = directory.getName().split("_")[0];
                        if (preperationForParse.charAt(0) == '0') {
                            preperationForParse = preperationForParse.substring(1, preperationForParse.length());
                        }
                        if (preperationForParse.charAt(0) == '0') {
                            preperationForParse = preperationForParse.substring(1, preperationForParse.length());
                        }

                        if (Integer.parseInt(preperationForParse) != testCaseStartIndex) {
                            directory.renameTo(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TC").toFile());
                            beansToRename.setPath(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TC"));
                        }
                    } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                        directory.renameTo(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TC").toFile());
                        beansToRename.setPath(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TC"));
                    } finally {
                        testCaseStartIndex++;
                    }
                }
            }
            directoriesToRename = new File(path.toString()).listFiles(File::isDirectory);
            rectifyTCDescription(directoriesToRename, allBeansToRename);
        }
    }

    /**
     * Method which renames all the directories so that the TC can be inserted
     * without any kind of problem
     *
     * @param index
     * @param path
     * @param allBeansToRename
     */
    public static void prepareFolderForTransfer(int index, Path path, List<DefaultMutableTreeNode> allBeansToRename) {
        File[] directoriesToRename = new File(path.toString()).listFiles(File::isDirectory);
        ExplorerLayer beansToRename = null;
        if (directoriesToRename != null) {
            int testCaseStartIndex = 1;
            for (File directory : directoriesToRename) {
                for (DefaultMutableTreeNode node_to_rename : allBeansToRename) {
                    ExplorerLayer tmp = (ExplorerLayer) node_to_rename.getUserObject();
                    if (tmp.getPath().toString().equalsIgnoreCase(directory.getPath())) {
                        beansToRename = tmp;
                    }
                }
                if (directory.getName().toUpperCase().contains("INIT")) {
                    if (!directory.getName().equalsIgnoreCase("000_INIT")) {
                        directory.renameTo(Paths.get(directory.getParent(), "000_INIT").toFile());
                        beansToRename.setPath(Paths.get(directory.getParent(), "000_INIT"));
                    }
                } else if (directory.getName().toUpperCase().contains("CLEANUP")) {
                    if (!directory.getName().equalsIgnoreCase("999_CLEANUP")) {
                        directory.renameTo(Paths.get(directory.getParent(), "999_CLEANUP").toFile());
                        beansToRename.setPath(Paths.get(directory.getParent(), "999_CLEANUP"));
                    }
                } else {
                    try {
                        if (index == testCaseStartIndex) {
                            testCaseStartIndex++;
                        }

                        String preperationForParse = directory.getName().split("_")[0];
                        if (preperationForParse.charAt(0) == '0') {
                            preperationForParse = preperationForParse.substring(1, preperationForParse.length());
                        }
                        if (preperationForParse.charAt(0) == '0') {
                            preperationForParse = preperationForParse.substring(1, preperationForParse.length());
                        }

                        if (Integer.parseInt(preperationForParse) != testCaseStartIndex) {
                            if (!directory.renameTo(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TC").toFile())) {
                                directory.renameTo(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TC_tmp").toFile());
                            }
                            beansToRename.setPath(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TC"));
                        }

                    } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                        if (!directory.renameTo(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TC").toFile())) {
                            directory.renameTo(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TC_tmp").toFile());
                        }
                        beansToRename.setPath(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TC"));
                    } finally {
                        testCaseStartIndex++;
                    }
                }
            }

            directoriesToRename = new File(path.toString()).listFiles(File::isDirectory);
            for (File rectification : directoriesToRename) {
                if (rectification.getName().contains("tmp")) {
                    String name = rectification.getName().replace("_tmp", "");
                    rectification.renameTo(Paths.get(rectification.getParent(), name).toFile());
                }
            }
            directoriesToRename = new File(path.toString()).listFiles(File::isDirectory);
            rectifyTCDescription(directoriesToRename, allBeansToRename);
        }
    }

    /**
     * Method which is being used to transfer a TC within the same folder
     *
     * @param index_before
     * @param index_after
     * @param path
     * @param allBeansToRename
     */
    public static void transferInSameFolder(int index_before, int index_after, Path path, List<DefaultMutableTreeNode> allBeansToRename) {
        boolean isChanged = false;
        File[] directoriesToRename = new File(path.toString()).listFiles(File::isDirectory);
        ExplorerLayer beansToRename = null;
        if (directoriesToRename != null) {
            int testCaseStartIndex = 1;

            for (File directory : directoriesToRename) {
                for (DefaultMutableTreeNode node_to_rename : allBeansToRename) {
                    ExplorerLayer tmp = (ExplorerLayer) node_to_rename.getUserObject();
                    if (tmp.getPath().toString().equalsIgnoreCase(directory.getPath())) {
                        beansToRename = tmp;
                    }
                }

                if (directory.getName().toUpperCase().contains("INIT")) {
                    if (!directory.getName().equalsIgnoreCase("000_INIT")) {
                        directory.renameTo(Paths.get(directory.getParent(), "000_INIT").toFile());
                        beansToRename.setPath(Paths.get(directory.getParent(), "000_INIT"));
                    }
                } else if (directory.getName().toUpperCase().contains("CLEANUP")) {
                    if (!directory.getName().equalsIgnoreCase("999_CLEANUP")) {
                        directory.renameTo(Paths.get(directory.getParent(), "999_CLEANUP").toFile());
                        beansToRename.setPath(Paths.get(directory.getParent(), "999_CLEANUP"));
                    }
                } else {
                    try {
                        if (index_before == testCaseStartIndex && !isChanged) {
                            String preperationForParse = directory.getName().split("_")[0];
                            if (preperationForParse.charAt(0) == '0') {
                                preperationForParse = preperationForParse.substring(1, preperationForParse.length());
                            }
                            if (preperationForParse.charAt(0) == '0') {
                                preperationForParse = preperationForParse.substring(1, preperationForParse.length());
                            }

                            if (!directory.renameTo(Paths.get(directory.getParent(), String.format("%03d", index_after) + "_TC").toFile())) {
                                directory.renameTo(Paths.get(directory.getParent(), String.format("%03d", index_after) + "_TC_tmp").toFile());
                                beansToRename.setPath(Paths.get(directory.getParent(), String.format("%03d", index_after) + "_TC_tmp"));
                            } else {
                                beansToRename.setPath(Paths.get(directory.getParent(), String.format("%03d", index_after) + "_TC"));
                            }
                            isChanged = true;
                            testCaseStartIndex--;
                        } else {
                            if (index_after == testCaseStartIndex) {
                                testCaseStartIndex++;
                            }

                            String preperationForParse = directory.getName().split("_")[0];
                            if (preperationForParse.charAt(0) == '0') {
                                preperationForParse = preperationForParse.substring(1, preperationForParse.length());
                            }
                            if (preperationForParse.charAt(0) == '0') {
                                preperationForParse = preperationForParse.substring(1, preperationForParse.length());
                            }

                            if (Integer.parseInt(preperationForParse) != testCaseStartIndex) {
                                if (!directory.renameTo(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TC").toFile())) {
                                    directory.renameTo(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TC_tmp").toFile());
                                    beansToRename.setPath(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TC_tmp"));
                                } else {
                                    beansToRename.setPath(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TC"));
                                }
                            }
                        }
                    } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                        if (!directory.renameTo(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TC").toFile())) {
                            directory.renameTo(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TC_tmp").toFile());
                            beansToRename.setPath(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TC_tmp"));
                        } else {
                            beansToRename.setPath(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TC"));
                        }
                    } finally {
                        testCaseStartIndex++;
                    }
                }
            }

            for (DefaultMutableTreeNode defaultMutableTreeNode : allBeansToRename) {
                if (((ExplorerLayer) defaultMutableTreeNode.getUserObject()).getPath().toString().contains("tmp")) {
                    String tmp_path = ((ExplorerLayer) defaultMutableTreeNode.getUserObject()).getPath().toString();
                    tmp_path = tmp_path.replace("_tmp", "");
                    ((ExplorerLayer) defaultMutableTreeNode.getUserObject()).setPath(Paths.get(tmp_path));
                }
            }

            directoriesToRename = new File(path.toString()).listFiles(File::isDirectory);
            rectifyTCDescription(directoriesToRename, allBeansToRename);
        }
    }

    /**
     * Method that updates the description of TCs after being moved
     *
     * @param directoriesToRename
     * @param allBeansToRename
     */
    private static void rectifyTCDescription(File[] directoriesToRename, List<DefaultMutableTreeNode> allBeansToRename) {
        ExplorerLayer beansToRename = null;
        for (File rectification : directoriesToRename) {
            try {
                Path helpPath = null;
                if (rectification.getName().contains("tmp")) {
                    String name = rectification.getName().replace("_tmp", "");
                    rectification.renameTo(Paths.get(rectification.getParent(), name).toFile());
                    helpPath = Paths.get(rectification.getParent(), name);
                } else {
                    helpPath = Paths.get(rectification.toPath().toString());
                }
                if (helpPath.toString().toUpperCase().contains("TC")) {
                    int tmp_index = Integer.parseInt(rectification.getName().split("_")[0].replaceAll(Pattern.quote("0"), ""));
                    for (DefaultMutableTreeNode node_to_rename : allBeansToRename) {
                        ExplorerLayer tmp = (ExplorerLayer) node_to_rename.getUserObject();
                        if (tmp.getPath().toString().equalsIgnoreCase(helpPath.toString())) {
                            beansToRename = tmp;
                        }
                    }
                    changeTCDescription(Paths.get(helpPath.toString(), "run.xml"), (TestCaseRun) beansToRename, tmp_index);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
            DB_Access_Manager.getInstance().updateData();
        }
    }

    /**
     * Method which is used in the command rearange process
     *
     * @param index
     * @param path
     * @param movedNode
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public static void createXMLFile(int index, Path path, Node movedNode) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(path.toFile());

        List<Node> nodes_to_write_in_new_file = new LinkedList<>();

        NodeList listNode = doc.getElementsByTagName("command");
        for (int i = 0; i < listNode.getLength(); i++) {
            if (i == index) {
                nodes_to_write_in_new_file.add(movedNode);
            }
            nodes_to_write_in_new_file.add(listNode.item(i));
        }
        if (index == listNode.getLength()) {
            nodes_to_write_in_new_file.add(movedNode);
        }
        createXMLFile(nodes_to_write_in_new_file, path, doc.getElementsByTagName("description").item(0));
    }

    /**
     * Method which is used in the command rearange process
     *
     * @param prevIndex
     * @param afterIndex
     * @param path
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public static void createXMLFile(int prevIndex, int afterIndex, Path path) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(path.toFile());
        NodeList listNode = doc.getElementsByTagName("command");
        List<Node> nodes_to_write_in_new_file = new LinkedList<>();
        Node tmp_node = null;
        for (int i = 0; i < listNode.getLength(); i++) {
            if (i == prevIndex) {
                tmp_node = listNode.item(i);
            } else {
                nodes_to_write_in_new_file.add(listNode.item(i));
            }
        }
        //    
        nodes_to_write_in_new_file.add(afterIndex, doc.adoptNode(tmp_node));

        createXMLFile(nodes_to_write_in_new_file, path, doc.getElementsByTagName("description").item(0));
    }

    /**
     * Method that moves any files that are associated with the command that is
     * being moved
     *
     * @param dest
     * @param source
     * @param template_name
     * @throws IOException
     */
    public static void moveAssociatedFiles(Path dest, Path source, String template_name) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(source.toString())));
        boolean move = true;
        String newLine = "";

        while ((newLine = reader.readLine()) != null) {
            if (newLine.toUpperCase().contains(template_name.toUpperCase())) {
                move = false;
            }
        }

        reader.close();
        if (move) {
            Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
        } else {
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * This method is used when adding a new command to a TC
     *
     * @param inputPerSubnode
     * @param commandTyp
     * @param path
     * @param displayName
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws TransformerException
     */
    public static NodeList addNodeToRun(Map<Node, String> inputPerSubnode, String commandTyp, Path path, String displayName)
            throws ParserConfigurationException, SAXException, IOException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(path.toFile());

        Element commands = (Element) doc.getElementsByTagName("commands").item(0);
//        commands.appendChild(doc.createTextNode("\n        "));
        Element command = doc.createElement("command");
        command.setAttribute("class", commandTyp);
        command.setAttribute("displayname", displayName);

        for (Node node : inputPerSubnode.keySet()) {
            Element subnode = doc.createElement(node.getNodeName());
            for (int i = 0; i < node.getAttributes().getLength(); i++) {
                subnode.setAttribute(node.getAttributes().item(i).getNodeName(), node.getAttributes().item(i).getNodeValue());
            }
            subnode.appendChild(doc.createTextNode(inputPerSubnode.get(node)));
            command.appendChild(subnode);
        }
        commands.appendChild(command);
        NodeList nl = command.getChildNodes();
        writeXml(doc, new StreamResult(path.toFile()));

        return nl;
    }

    /**
     * Method to return all subnodes of a node in form of a NodeList
     *
     * @param path
     * @param description
     * @return
     * @throws NullPointerException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws XPathExpressionException
     */
    public static NodeList getCurrentNodeList(Path path, String description) throws NullPointerException, SAXException, ParserConfigurationException, IOException, XPathExpressionException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document document = dbf.newDocumentBuilder().parse(path.toFile());

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        XPathExpression expression = xpath.compile("/run/commands/command/command_text[string(.)='" + description + "']");

        Node node = (Node) expression.evaluate(document, XPathConstants.NODE);
        node = node.getParentNode();
        return node.getChildNodes();
    }

    /**
     * This method is used to rename the testGroups after a TestGroup has been
     * f.e. deleted
     *
     * @param path
     * @param allBeansToRename
     */
    public static void renameTestGroups(Path path, List<DefaultMutableTreeNode> allBeansToRename) {
        File[] directoriesToRename = new File(path.toString()).listFiles(File::isDirectory);
        ExplorerLayer beansToRename = null;
        List<ExplorerLayer> adaptedTreeNodes = new LinkedList<>();
        if (directoriesToRename != null) {
            int testCaseStartIndex = 1;
            for (File directory : directoriesToRename) {
                for (DefaultMutableTreeNode node_to_rename : allBeansToRename) {
                    ExplorerLayer tmp = (ExplorerLayer) node_to_rename.getUserObject();
                    if (tmp.getPath().toString().equalsIgnoreCase(directory.getPath())) {
                        beansToRename = tmp;
                        adaptedTreeNodes.add(beansToRename);
                    }
                }
                if (directory.getName().toUpperCase().contains("INIT")) {
                    if (!directory.getName().equalsIgnoreCase("000_INIT")) {
                        directory.renameTo(Paths.get(directory.getParent(), "000_INIT").toFile());
                        beansToRename.setPath(Paths.get(directory.getParent(), "000_INIT"));
                    }
                } else if (directory.getName().toUpperCase().contains("CLEANUP")) {
                    if (!directory.getName().equalsIgnoreCase("999_CLEANUP")) {
                        directory.renameTo(Paths.get(directory.getParent(), "999_CLEANUP").toFile());
                        beansToRename.setPath(Paths.get(directory.getParent(), "999_CLEANUP"));
                    }
                } else {
                    try {
                        String preperationForParse = directory.getName().split("_")[0];
                        if (preperationForParse.charAt(0) == '0') {
                            preperationForParse = preperationForParse.substring(1, preperationForParse.length());
                        }
                        if (preperationForParse.charAt(0) == '0') {
                            preperationForParse = preperationForParse.substring(1, preperationForParse.length());
                        }

                        if (Integer.parseInt(preperationForParse) != testCaseStartIndex) {
                            directory.renameTo(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TG").toFile());
                            beansToRename.setPath(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TG"));
                        }
                    } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                        directory.renameTo(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TG").toFile());
                        beansToRename.setPath(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TG"));
                    } finally {
                        testCaseStartIndex++;
                    }
                }
            }
            directoriesToRename = new File(path.toString()).listFiles(File::isDirectory);
            for (File rectification : directoriesToRename) {
                if (rectification.getName().toUpperCase().contains("TG")) {
                    int tmp_index = Integer.parseInt(rectification.getName().split("_")[0].replaceAll(Pattern.quote("0"), ""));
                    for (DefaultMutableTreeNode node_to_rename : allBeansToRename) {
                        ExplorerLayer tmp = (ExplorerLayer) node_to_rename.getUserObject();
                        if (tmp.getPath().toString().equalsIgnoreCase(rectification.toPath().toString())) {
                            beansToRename = tmp;
                        }
                    }
                    try {
                        renameInitAndCleanup(new File(rectification.toPath().toString()).listFiles(), tmp_index, beansToRename, adaptedTreeNodes);
                    } catch (SAXException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (ParserConfigurationException ex) {
                        ex.printStackTrace();
                    } catch (TransformerException ex) {
                        ex.printStackTrace();
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                }

            }

        }
    }

    /**
     * This method is being used when the TGs have been moved to rename the TG
     * and adapt their descritpion as needed
     *
     * @param index_before
     * @param index_after
     * @param path
     * @param allBeansToRename
     */
    public static void renameAfterMovingTG(int index_before, int index_after, Path path, List<DefaultMutableTreeNode> allBeansToRename) {
        boolean isChanged = false;
        List<ExplorerLayer> adaptedTreeNodes = new LinkedList<>();
        File[] directoriesToRename = new File(path.toString()).listFiles(File::isDirectory);
        ExplorerLayer beansToRename = null;
        if (directoriesToRename != null) {
            int testCaseStartIndex = 1;

            for (File directory : directoriesToRename) {
                for (DefaultMutableTreeNode node_to_rename : allBeansToRename) {
                    ExplorerLayer tmp = (ExplorerLayer) node_to_rename.getUserObject();
                    if (tmp.getPath().toString().equalsIgnoreCase(directory.getPath())) {
                        beansToRename = tmp;
                    }
                }
                if (directory.getName().toUpperCase().contains("INIT")) {
                    if (!directory.getName().equalsIgnoreCase("000_INIT")) {
                        directory.renameTo(Paths.get(directory.getParent(), "000_INIT").toFile());
                        beansToRename.setPath(Paths.get(directory.getParent(), "000_INIT"));
                    }
                } else if (directory.getName().toUpperCase().contains("CLEANUP")) {
                    if (!directory.getName().equalsIgnoreCase("999_CLEANUP")) {
                        directory.renameTo(Paths.get(directory.getParent(), "999_CLEANUP").toFile());
                        beansToRename.setPath(Paths.get(directory.getParent(), "999_CLEANUP"));
                    }
                } else {
                    try {
                        if ((index_before == testCaseStartIndex && !isChanged) /*|| (index_before < testCaseStartIndex && !isChanged)*/) {
                            String preperationForParse = directory.getName().split("_")[0];
                            if (preperationForParse.charAt(0) == '0') {
                                preperationForParse = preperationForParse.substring(1, preperationForParse.length());
                            }
                            if (preperationForParse.charAt(0) == '0') {
                                preperationForParse = preperationForParse.substring(1, preperationForParse.length());
                            }

                            if (!directory.renameTo(Paths.get(directory.getParent(), String.format("%03d", index_after) + "_TG").toFile())) {
                                directory.renameTo(Paths.get(directory.getParent(), String.format("%03d", index_after) + "_TG_tmp").toFile());
                                beansToRename.setPath(Paths.get(directory.getParent(), String.format("%03d", index_after) + "_TG_tmp"));
                            } else {
                                beansToRename.setPath(Paths.get(directory.getParent(), String.format("%03d", index_after) + "_TG"));
                            }

                            isChanged = true;
                            testCaseStartIndex--;
                        } else {
                            if (index_after == testCaseStartIndex) {
                                testCaseStartIndex++;
                            }

                            String preperationForParse = directory.getName().split("_")[0];
                            if (preperationForParse.charAt(0) == '0') {
                                preperationForParse = preperationForParse.substring(1, preperationForParse.length());
                            }
                            if (preperationForParse.charAt(0) == '0') {
                                preperationForParse = preperationForParse.substring(1, preperationForParse.length());
                            }

                            if (Integer.parseInt(preperationForParse) != testCaseStartIndex) {
                                if (!directory.renameTo(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TG").toFile())) {
                                    directory.renameTo(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TG_tmp").toFile());
                                    beansToRename.setPath(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TG_tmp"));
                                } else {
                                    beansToRename.setPath(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TG"));
                                }

                            }
                        }
                    } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                        if (!directory.renameTo(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TG").toFile())) {
                            directory.renameTo(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TG_tmp").toFile());
                            beansToRename.setPath(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TG_tmp"));
                        } else {
                            beansToRename.setPath(Paths.get(directory.getParent(), String.format("%03d", testCaseStartIndex) + "_TG"));
                        }
                    } finally {
                        testCaseStartIndex++;
                    }
                }
                adaptedTreeNodes.add(beansToRename);
            }
            directoriesToRename = new File(path.toString()).listFiles(File::isDirectory);
            Path help_path = null;
            for (DefaultMutableTreeNode defaultMutableTreeNode : allBeansToRename) {
                if (((ExplorerLayer) defaultMutableTreeNode.getUserObject()).getPath().toString().contains("tmp")) {
                    String tmp_path = ((ExplorerLayer) defaultMutableTreeNode.getUserObject()).getPath().toString();
                    tmp_path = tmp_path.replace("_tmp", "");
                    ((ExplorerLayer) defaultMutableTreeNode.getUserObject()).setPath(Paths.get(tmp_path));
                }
            }

            for (File rectification : directoriesToRename) {
                if (rectification.getName().contains("tmp")) {
                    String name = rectification.getName().replace("_tmp", "");
                    rectification.renameTo(Paths.get(rectification.getParent(), name).toFile());
                    help_path = Paths.get(rectification.getParent(), name);
                } else {
                    help_path = rectification.toPath();
                }
                if (rectification.getName().toUpperCase().contains("TG")) {
                    int tmp_index = Integer.parseInt(rectification.getName().split("_")[0].replaceAll(Pattern.quote("0"), ""));
                    for (DefaultMutableTreeNode node_to_rename : allBeansToRename) {
                        ExplorerLayer tmp = (ExplorerLayer) node_to_rename.getUserObject();
                        if (tmp.getPath().toString().equalsIgnoreCase(help_path.toString())) {
                            beansToRename = tmp;
                        }
                    }
                    try {
                        renameInitAndCleanup(new File(help_path.toString()).listFiles(), tmp_index, beansToRename, adaptedTreeNodes);
                    } catch (SAXException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (ParserConfigurationException ex) {
                        ex.printStackTrace();
                    } catch (TransformerException ex) {
                        ex.printStackTrace();
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                }

            }
        }
        //Pfade umsetzen in Beans klassen DONE
        //Description in Beans klassse umsetzen DONE
        //Reihenfolge in liste ändern
        //am System verschieben DONE
    }

    /**
     * Method which adapts the description of the TCs after the TG has been
     * moved or another TG has been deleted
     *
     * @param path
     * @param tc
     * @param index
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    public static void changeTCDescription(Path path, TestCaseRun tc, int index) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(path.toFile());
        String oldDesc = tc.getDescription();
        Node descNode = doc.getElementsByTagName("description").item(0);
        String text = descNode.getTextContent();
        text = text.replaceAll("Testcase_[0-9]+", "Testcase_" + index);

        tc.setDescription(text);
        descNode.setTextContent(text);
//        try {
//            if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                DB_Access.getInstance().updateEntry(tc, oldDesc);
//                if (GlobalParamter.getInstance().getSelected_user() != null) {
//                    if (!oldDesc.equalsIgnoreCase(text)) {
//                        //DB_Access.getInstance().addChangeEntry(tc, ChangeType.CHANGED);
//                    }
//                }
//            }
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }

        writeXml(doc, new StreamResult(path.toFile()));
    }

    /**
     * This method is used to adapt the INIT and CLEANUP directory of the moved
     * TG to fit the right description
     *
     * @param allDirectoriesToRename
     * @param index
     * @param layer
     * @param allTGs
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    private static void renameInitAndCleanup(File[] allDirectoriesToRename, int index, ExplorerLayer layer, List<ExplorerLayer> allTGs) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        for (File dir : allDirectoriesToRename) {
            if (dir.getName().equalsIgnoreCase("000_INIT")) {
                TestCaseRun for_init = getExplorerLayer(dir, allTGs);
                adaptDescription(Paths.get(dir.getAbsolutePath(), "run.xml"), index, for_init);
            } else if (dir.getName().equalsIgnoreCase("999_CLEANUP")) {
                TestCaseRun for_cleanup = getExplorerLayer(dir, allTGs);
                adaptDescription(Paths.get(dir.getAbsolutePath(), "run.xml"), index, for_cleanup);
            } else if (dir.getName().equalsIgnoreCase("run.xml")) {
                adaptDescriptionOfTG(Paths.get(dir.getAbsolutePath()), index, layer);
            }
        }
        if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
            DB_Access_Manager.getInstance().updateData();
        }
    }

    /**
     * This method returns the TC that is associated with the dir parameter that
     * is needed in this methods
     *
     * @param dir
     * @param allTGs
     * @return
     */
    private static TestCaseRun getExplorerLayer(File dir, List<ExplorerLayer> allTGs) {
        for (ProjectRun workingProject : GlobalParamter.getInstance().getWorkingProjects()) {
            for (TestGroupRun tg : workingProject.getTestgroups()) {
                TestGroupRun tmp = (TestGroupRun) tg;
                for (TestCaseRun tc : tmp.getTestCases()) {
                    if (tc.getPath().toString().equalsIgnoreCase(dir.toString())) {
                        return tc;
                    }
                }
            }
        }
        return null;
    }

    /**
     * This method is used to rename the description of a TC after being moved
     *
     * @param path
     * @param index
     * @param tc
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    private static void adaptDescription(Path path, int index, TestCaseRun tc) throws SAXException, IOException, ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(path.toFile());

        Node descNode = doc.getElementsByTagName("description").item(0);
        String text = descNode.getTextContent();
        text = text.replaceAll("\\d\\d", String.format("%02d", index));
        descNode.setTextContent(text);
        tc.setDescription(text);
        writeXml(doc, new StreamResult(path.toFile()));
    }

    /**
     * Method to change the description of a testgroup
     *
     * @param path
     * @param index
     * @param layer
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws TransformerException
     */
    private static void adaptDescriptionOfTG(Path path, int index, ExplorerLayer layer) throws SAXException, IOException, ParserConfigurationException, TransformerException {
//        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(path.toFile());
            String oldDesc = layer.getDescription();
            Node descNode = doc.getElementsByTagName("description").item(0);
            String text = descNode.getTextContent();
            text = text.replaceAll("Testgruppe_[0-9]+", "Testgruppe_" + index);
            layer.setDescription(text);
//            if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                System.out.println("DSFSFSADFD");
//                System.out.println("no exception wixxer");
//                if (!oldDesc.equalsIgnoreCase(text)) {
//                    DB_Access.getInstance().updateEntry(layer, oldDesc);
//                }
//            }
            descNode.setTextContent(text);

            writeXml(doc, new StreamResult(path.toFile()));
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
    }

    /**
     * Method to retrieve the appendant recorder of an
     * ExecuteFileRecorderCommand
     *
     * @param path
     * @param description
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     */
    public static String readRecFromCommand(Path path, String description) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document document = dbf.newDocumentBuilder().parse(path.toFile());
        String recName = "";

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        XPathExpression expression = xpath.compile("/run/commands/command/command_text[string(.)='" + description + "']");

        Node node = (Node) expression.evaluate(document, XPathConstants.NODE);
        node = node.getParentNode();
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            if (node.getChildNodes().item(i).getNodeName().equals("pathtoxml")) {
                recName = node.getChildNodes().item(i).getTextContent();
            }
        }
        return recName;
    }

    /**
     * Method to add init and cleanup to a newly added testgroup
     *
     * @param testCase
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws TransformerException
     */
    public static void addInitOrCleanupToNewTG(TestCaseRun testCase) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        //Init DOM
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        //Create folder
        Files.createDirectory(testCase.getPath());
        //Create run.xml for TG
        Element root = doc.createElement("run");
        doc.appendChild(root);
        createSubNodes(doc, root, "description", testCase.getDescription());
        createSubNodes(doc, root, "commands", "\n");

        writeXml(doc, new StreamResult(Paths.get(testCase.getPath().toString(), "run.xml").toFile()));
    }

    /**
     * Method to add TGs init and cleanup to a newly added project
     *
     * @param testGroup
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws TransformerException
     */
    public static void addInitOrCleanupToNewProject(TestGroupRun testGroup) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        //Init DOM
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        //Create folder
        if (!testGroup.getPath().toFile().exists()) {
            Files.createDirectory(testGroup.getPath());
        }
        //Create run.xml for TG
        Element root = doc.createElement("run");
        doc.appendChild(root);
        createSubNodes(doc, root, "description", testGroup.getDescription());
        createSubNodes(doc, root, "empid", testGroup.getEmpId() + "");
        createSubNodes(doc, root, "emppass", testGroup.getPassword() + "");
        createSubNodes(doc, root, "tllid", testGroup.getTllId() + "");

        writeXml(doc, new StreamResult(Paths.get(testGroup.getPath().toString(), "run.xml").toFile()));
    }

    /**
     * Method to change the "command_text" node of a specific command in run.xml
     *
     * @param path
     * @param oldDescription
     * @param newDescription
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws XPathExpressionException
     * @throws TransformerException
     */
    public static void changeCommandText(Path path, String oldDescription, String newDescription) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document document = dbf.newDocumentBuilder().parse(path.toFile());

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        XPathExpression expression = xpath.compile("/run/commands/command/command_text[string(.)='" + oldDescription + "']");

        Node node = (Node) expression.evaluate(document, XPathConstants.NODE);
        node.setTextContent(newDescription);
        writeXml(document, new StreamResult(path.toFile()));
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

    /**
     * This method returns the ammount of valid recorders within the folder that
     * is identified by the path
     *
     * @param path
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static int getAmountOfRecordersInFolder(Path path) throws ParserConfigurationException, SAXException, IOException {
        File allFiles[] = new File(path.toString()).listFiles(File::isFile);
        int amount = 0;
        for (File fileToCheck : allFiles) {
            if (recorder.io.IOChecker.checkValidBon(fileToCheck.toPath())) {
                amount++;
            }
        }
        amount++;
        return amount;
    }

    /**
     * Method to load a combobox for the CtrlCommandV2
     */
    public static void loadCtrlCommandV2ComboBoxContent() {
        Path path = Paths.get(GlobalParamter.getInstance().getExplorerResPath().toString(), "ctrl_command_v2_comboBox_content.conf");

        Map<String, Integer> ctrl_command_v2_mapping = new HashMap();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path.toString())));
            String zeile = "";
            while ((zeile = reader.readLine()) != null) {
                if (!zeile.contains("#")) {
                    try {
                        ctrl_command_v2_mapping.put(zeile.split("\\=")[0], Integer.parseInt(zeile.split("\\=")[1]));
                    } catch (IndexOutOfBoundsException | NumberFormatException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        GlobalParamter.getInstance().setCtrl_command_v2_mapping(ctrl_command_v2_mapping);
    }

    /**
     * Method to load the global commands
     *
     * @param path
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static String commandsInXml(Path path) throws SAXException, IOException, ParserConfigurationException {
        String command = "";
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(path.toFile());

        NodeList nl = doc.getElementsByTagName("description");
//
//        for (int i = 1; i < nl.getLength(); i += 2) {
//            for (int j = 1; j < nl.item(i).getChildNodes().getLength(); j += 2) {
//                System.out.println(nl.item(i).getChildNodes().item(j).getNodeName());
//                if (nl.item(i).getChildNodes().item(j).getNodeName().equalsIgnoreCase("command_text")) {
//                    allCommands.add(nl.item(i).getChildNodes().item(j).getTextContent());
//                    break;
//                }
//                if (j == nl.item(i).getChildNodes().getLength() - 1) {
//                    allCommands.add("kein command_text gefunden");
//                }
//            }
//        }
        if (nl != null) {
            if (nl.getLength() != 0) {
                command = nl.item(0).getTextContent();
            }
        }
        return command;
    }

}

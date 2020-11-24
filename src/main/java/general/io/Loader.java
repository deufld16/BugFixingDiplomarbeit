/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.io;

import analyzer.beans.WhitelistEntry;
import analyzer.enums.ResultFileType;
import dashboard.beans.Nutzer;
import general.bl.GlobalAccess;
import general.bl.GlobalParamter;
import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import remote.bl.RemoteExecutionManager;
import simulator.beans.Backoffice;
import simulator.beans.Kasse;
import simulator.beans.Testsystem;
import simulator.bl.ExecutionManager;

/**
 * Class that handles the loading process of different configuration files
 *
 * @author Florian Deutschmann
 * @author Maximilian Strohmaier
 */
public class Loader {

    private static boolean whitelistLoaded = false;

    /**
     * Method that loads the ImageIcon for the frame and the dialogs
     *
     * @param imageName
     * @param width
     * @param height
     * @return
     * @throws IOException
     */
    public static ImageIcon loadImage(String imageName, int width, int height) throws IOException {
        String imagePathStr = GlobalAccess.getInstance().getGeneralResPath() + File.separator + "img"
                + File.separator + imageName;
        Image img = ImageIO.read(new File(imagePathStr));
        img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(img);
        return icon;
    }

    /**
     * Method that loads the ImageIcon for the frame and the dialogs (Explorer)
     *
     * @param imageName
     * @param width
     * @param height
     * @return
     * @throws IOException
     */
    public static ImageIcon loadImageExplorer(String imageName, int width, int height) throws IOException {
        String imagePathStr = GlobalParamter.getInstance().getExplorerResPath() + File.separator + "img" + File.separator + imageName;
        Image img = ImageIO.read(new File(imagePathStr));
        img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(img);
        return icon;
    }

    public static ImageIcon loadImageDashboard(String imageName, int width, int height) throws IOException {
        String imagePathStr = GlobalParamter.getInstance().getDashboardResPath() + File.separator + "img" + File.separator + imageName;
        Image img = ImageIO.read(new File(imagePathStr));
        img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(img);
        return icon;
    }

    /**
     * Method that loads the ImageIcon for the frame and the dialogs (Settings)
     *
     * @param imageName
     * @param width
     * @param height
     * @return
     * @throws IOException
     */
    public static ImageIcon loadImageSettings(String imageName, int width, int height) throws IOException {
        String imagePathStr = GlobalParamter.getInstance().getSettingsResPath() + File.separator + "img" + File.separator + imageName;
        Image img = ImageIO.read(new File(imagePathStr));
        img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(img);
        return icon;
    }

    /**
     * Method that loads the leaf icons for the JTree in the Explorer
     *
     * @param imageName
     * @param width
     * @param height
     * @return
     * @throws IOException
     */
    public static ImageIcon loadLeafIcon(String imageName, int width, int height) throws IOException {
        String imagePathStr = GlobalParamter.getInstance().getExplorerResPath() + File.separator + "img" + File.separator + imageName;
        Image img = ImageIO.read(new File(imagePathStr));
        img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon icon = new ImageIcon(img);
        return icon;
    }

    /**
     * *
     * This method is used to load the configured whitelist from
     * settings/res/whitelist.xml
     */
    public static void loadWhitelistXml() {
        List<WhitelistEntry> entries = new LinkedList<>();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(Paths.get(System.getProperty("user.dir"), "src", "main", "java", "settings", "res", "whitelist.xml").toFile());
            doc.getDocumentElement().normalize();

            NodeList nlEntries = doc.getElementsByTagName("entry");

            for (int i = 0; i < nlEntries.getLength(); i++) {
                String text = "";
                String regex = "";
                List<ResultFileType> types = new LinkedList<>();

                NodeList nlChildren = nlEntries.item(i).getChildNodes();
                for (int j = 0; j < nlChildren.getLength(); j++) {
                    Node node = nlChildren.item(j);
                    switch (node.getNodeName()) {
                        case "title":
                            text = node.getTextContent();
                            break;
                        case "regex":
                            regex = node.getTextContent();
                            break;
                        case "type":
                            types.add(ResultFileType.valueOf(node.getTextContent()));
                            break;
                    }
                }

                entries.add(new WhitelistEntry(text, regex, types));
            }

            GlobalParamter.getInstance().setWhitelistEntries(entries);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            GlobalParamter.getInstance().setWhitelistEntries(new LinkedList<>());
        }
        whitelistLoaded = true;
    }

    /**
     * Method to load all cashpoints
     */
    public static void loadCashpoints() {
        List<Kasse> res = new ArrayList<>();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(Paths.get(System.getProperty("user.dir"), "src", "main", "java", "settings", "res", "kassen.xml").toFile());
            doc.getDocumentElement().normalize();

            NodeList kassen = doc.getElementsByTagName("kassa");
            for (int i = 0; i < kassen.getLength(); i++) {
                String ip = "";
                int id = -1;
                int grp = -1;
                Kasse.TYP type = Kasse.TYP.RB4;
                for (int j = 0; j < kassen.item(i).getChildNodes().getLength(); j++) {
                    switch (kassen.item(i).getChildNodes().item(j).getNodeName()) {
                        case "ip":
                            ip = kassen.item(i).getChildNodes().item(j).getTextContent();
                            break;
                        case "id":
                            id = Integer.parseInt(kassen.item(i).getChildNodes().item(j).getTextContent());
                            break;
                        case "group":
                            grp = Integer.parseInt(kassen.item(i).getChildNodes().item(j).getTextContent());
                            break;
                        case "type":
                            type = Kasse.TYP.valueOf(kassen.item(i).getChildNodes().item(j).getTextContent());
                            break;
                    }
                }
                res.add(new Kasse(id, grp, ip, type));
            }
            GlobalParamter.getInstance().setKassen(res);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that loads all test systems
     */
    public static void loadTestsysteme() {
        List<Testsystem> res = new ArrayList<>();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(Paths.get(System.getProperty("user.dir"), "src", "main", "java", "settings", "res", "testsysteme.xml").toFile());
            doc.getDocumentElement().normalize();

            NodeList testsysteme = doc.getElementsByTagName("testsystem");
            for (int i = 0; i < testsysteme.getLength(); i++) {
                List<Kasse> kassen = new ArrayList<>();
                Backoffice back = null;
                boolean act = false;
                String name = "";
                for (int j = 0; j < testsysteme.item(i).getChildNodes().getLength(); j++) {

                    switch (testsysteme.item(i).getChildNodes().item(j).getNodeName()) {
                        case "backoffice":
                            back = new Backoffice(testsysteme.item(i).getChildNodes().item(j).getTextContent());
                            break;
                        case "active":
                            act = testsysteme.item(i).getChildNodes().item(j).getTextContent().equals("true");
                            break;
                        case "name":
                            name = testsysteme.item(i).getChildNodes().item(j).getTextContent();
                            break;
                        case "kassa":
                            String ip = "";
                            int id = -1;
                            int grp = -1;
                            Kasse.TYP type = Kasse.TYP.RB4;
                            for (int k = 0; k < testsysteme.item(i).getChildNodes().item(j).getChildNodes().getLength(); k++) {
                                switch (testsysteme.item(i).getChildNodes().item(j).getChildNodes().item(k).getNodeName()) {
                                    case "ip":
                                        ip = testsysteme.item(i).getChildNodes().item(j).getChildNodes().item(k).getTextContent();
                                        break;
                                    case "id":
                                        id = Integer.parseInt(testsysteme.item(i).getChildNodes().item(j).getChildNodes().item(k).getTextContent());
                                        break;
                                    case "group":
                                        grp = Integer.parseInt(testsysteme.item(i).getChildNodes().item(j).getChildNodes().item(k).getTextContent());
                                        break;
                                    case "type":
                                        type = Kasse.TYP.valueOf(testsysteme.item(i).getChildNodes().item(j).getChildNodes().item(k).getTextContent());
                                        break;
                                }
                            }
                            kassen.add(new Kasse(id, grp, ip, type));
                            break;
                    }
                }
                res.add(new Testsystem(kassen, back, act, name));
            }
            GlobalParamter.getInstance().setTestsysteme(res);
            RemoteExecutionManager.getInstance().getPanel().updateTestsysteme();
            for (Testsystem re : res) {
                if (re.isActive()) {
                    ExecutionManager.getInstance().setActiveSystem(re);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that loads all backoffices
     */
    public static void loadBackoffices() {
        List<Backoffice> res = new ArrayList<>();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(Paths.get(System.getProperty("user.dir"), "src", "main", "java", "settings", "res", "backoffices.xml").toFile());
            doc.getDocumentElement().normalize();

            NodeList backoffice = doc.getElementsByTagName("backoffice");
            for (int i = 0; i < backoffice.getLength(); i++) {
                String ip = "";
                for (int j = 0; j < backoffice.item(i).getChildNodes().getLength(); j++) {
                    switch (backoffice.item(i).getChildNodes().item(j).getNodeName()) {
                        case "ip":
                            ip = backoffice.item(i).getChildNodes().item(j).getTextContent();
                            break;

                    }
                }
                res.add(new Backoffice(ip));
            }
            GlobalParamter.getInstance().setBackoffices(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to load the last user
     *
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static void loadLastUser() throws IOException, ClassNotFoundException, SQLException {
        System.out.println(Files.exists(Paths.get(GlobalParamter.getInstance().getGeneralResPath().toString(), "lastUser.ser")));
        System.out.println(Paths.get(GlobalParamter.getInstance().getGeneralResPath().toString(), "lastUser.ser"));
        if (Files.exists(Paths.get(GlobalParamter.getInstance().getGeneralResPath().toString(), "lastUser.ser"))) {
            FileInputStream fis = new FileInputStream(Paths.get(GlobalParamter.getInstance().getGeneralResPath().toString(), "lastUser.ser").toFile());
            ObjectInputStream ois = new ObjectInputStream(fis);

            Nutzer nutzer = (Nutzer) ois.readObject();
            if (dashboard.bl.DatabaseGlobalAccess.getInstance().isDbReachable()) {
                if (dashboard.bl.DatabaseGlobalAccess.getInstance().getAllUsers().contains(nutzer)) {
                    int index = dashboard.bl.DatabaseGlobalAccess.getInstance().getAllUsers().indexOf(nutzer);
                    dashboard.bl.DatabaseGlobalAccess.getInstance().
                            setCurrentNutzer(dashboard.bl.DatabaseGlobalAccess.getInstance().getAllUsers().get(index));
                }
            } else {
                dashboard.bl.DatabaseGlobalAccess.getInstance().setCurrentNutzer(dashboard.bl.DatabaseGlobalAccess.getInstance().getAllUsers()
                        .get(dashboard.bl.DatabaseGlobalAccess.getInstance().getAllUsers().indexOf(new Nutzer("Default User"))));
            }
        }
    }

    public static boolean isWhitelistLoaded() {
        return whitelistLoaded;
    }

    public static void setWhitelistLoaded(boolean whitelistLoaded) {
        Loader.whitelistLoaded = whitelistLoaded;
    }

}

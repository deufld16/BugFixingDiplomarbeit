/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.xml;

import recorder.beans.Article;
import recorder.beans.Command;
import recorder.beans.Operation;
import recorder.bl.DisplayListModel;
import recorder.gui.dlg.SquashDlg;
import recorder.guiOperations.GUIOperations;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JOptionPane;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import recorder.beans.Karte;

/**
 *
 * @author annalechner
 */
public class RecorderXML {

    private DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder docBuilder;
    private Document doc;
    private Element rootElement;
    private String squashText;
    private boolean eloading = false;
    private boolean eloading_canceled = false;
    private String gewinnspielFile = "";
    private int bonStornoType = -1;
    private boolean isPayment = true;
    private boolean geschenkskarte = false;
    private List<String> sysCommandsStart = new LinkedList<>();
    private List<String> sysCommandsEnd = new LinkedList<>();

    /**
     * Constructor of RecorderXML
     */
    public RecorderXML() {
        try {
            docBuilder = docFactory.newDocumentBuilder();
            squashText = "";
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        }
        doc = docBuilder.newDocument();

    }

    public void setEloading(boolean eloading) {
        this.eloading = eloading;
    }

    public void setGewinnspielFile(String gewinnspielFile) {
        this.gewinnspielFile = gewinnspielFile;
    }

    public void setIsPayment(boolean isPayment) {
        this.isPayment = isPayment;
    }

    public boolean isIsPayment() {
        return isPayment;
    }

    /**
     * Entry point to create the XML file
     *
     * @param userEntry
     * @throws Exception
     */
    public void master(List<Command> userEntry) throws Exception {
        docBuilder = docFactory.newDocumentBuilder();
        doc = docBuilder.newDocument();
        rootElement = doc.createElement("bon");
        doc.appendChild(rootElement);
        List<Command> all = createRecorderStart();
        if (userEntry != null) {
            all.addAll(userEntry);
        }
        all.addAll(createRecorderEnd());
        for (Command command : all) {
            rootElement.appendChild(XMLPreparator.parse(doc, command));
        }
        File file = Paths.get(settings.io.Loader.getSpecificParameter(false).get("recorderPath")).toFile();
        boolean chosen = false;
        if (!file.toString().endsWith("Documents\\rec.xml")) {
            chosen = true;
        } else {
            chosen = false;
        }
        if (GUIOperations.isIsWorkflow()) {
            writeXML(GUIOperations.getSaveLocationIfWorkflow().toFile(), chosen);
        } else {
            writeXML(file, chosen);
        }
    }

    /**
     * Outsourced method that depicts an XML tag and returns a command Shortenes
     * the following code lines:
     *
     * map = Stream.of(new Object[][]{ {"article", o.getArticle()}, {"function",
     * "SSTO"},}).collect(Collectors.toMap(data -> (String) data[0], data ->
     * (Object) data[1])); command.add(new Command(nodeName, new
     * HashMap<>(map)));
     *
     * @param nodeName
     * @param value
     * @return
     */
    public static Command createCommand(String nodeName, String... value) {
        Map<String, Object> map = new LinkedHashMap<>();
        int cnt = 0;
        for (int i = 0; i < value.length / 2; i++) {
            map.put(value[cnt], value[cnt + 1]);
            cnt += 2;
        }

        return new Command(nodeName, new HashMap<>(map));
    }

    /**
     * Method to return the marker name from the InitDlg
     *
     * @return
     */
    private String handleMarkername() {
        String marker = "";
        try {
            marker = GUIOperations.getInitParams().get("Markername").toString();
        } catch (NullPointerException ex) {
            marker = "leer";
        }
        return marker;
    }

    /**
     * Method to return the squash text from the InitDlg
     *
     * @return
     */
    private String handleSquashText() {
        String squashText = "";
        try {
            squashText = GUIOperations.getInitParams().get("Squashtext").toString();
        } catch (NullPointerException ex) {
            squashText = "";
        }
        return squashText;
    }

    /**
     * Method that creates the static begin of the XML file
     *
     * @param marker
     * @return
     * @throws Exception
     */
    private List<Command> createRecorderStart() throws Exception {
        List<Command> command = new ArrayList<>();
        String marker = handleMarkername();
        String squashText = handleSquashText();
        if (squashText.trim().isEmpty()) {
            command.add(createCommand("mark", "name", marker));
        } else {
            command.add(createCommand("mark", "name", marker, "description", squashText));
        }
        if (eloading) {
            command.add(createCommand("syscommand", "command", "/home/npos/mockup/starteloading.sh"));
            if (bonStornoType != -1) {
                switch (bonStornoType) {
                    case 0:
                        break;
                    case 1:
                        command.add(createCommand("syscommand", "command", "touch /home/npos/mockup/answers/eload_storno_ok"));
                        break;
                    case 2:
                        command.add(createCommand("syscommand", "command", "touch /home/npos/mockup/answers/eload_storno_nok"));
                        break;
                }
            }
        }
        if (geschenkskarte) {
            command.add(createCommand("syscommand", "command", "/home/npos/mockup/startexperian.sh"));
        }

        for (String sysCommand : sysCommandsStart) {
            command.add(createCommand("syscommand", "command", sysCommand));
        }

        List<File> simFiles = (List<File>) GUIOperations.getInitParams().get("Simulationsdateien");
        for (File simFile : simFiles) {
            String simu = simFile.toString().substring(simFile.toString().lastIndexOf("\\") + 1);
            if (simu.endsWith(".pb")) {
                command.add(createCommand("mockup", "name", "payback", "answer", simu));
            } else if (simu.endsWith(".ebon")) {
                command.add(createCommand("mockup", "name", "ebon", "answer", simu));
            } else if (simu.endsWith(".on") || simu.endsWith(".xml")) {
                command.add(createCommand("mockup", "name", "barzahlen", "answer", simu));
            } else if (simu.endsWith(".txt") && simFile.getName().contains("experian")) {
                command.add(createCommand("mockup", "name", "experian", "answer", simu));
            } else if (simu.matches(".*([.])\\d") && simFile.getName().contains("cashguard")) {
                command.add(createCommand("mockup", "name", "cashguard", "answer", simu));
            } else if (simu.endsWith(".json")) {
                command.add(createCommand("mockup", "name", "fakturierung", "answer", simu));
            }
        }
        if (!gewinnspielFile.equals("")) {
            command.add(createCommand("syscommand", "command", gewinnspielFile));
        }
        command.add(createCommand("hwcommand", "device", "drawer", "value", "0"));
        command.add(createCommand("hwcommand", "device", "key", "value", "1"));
        if (GUIOperations.isLogin() && !GUIOperations.isPwChange()) {
            command.add(new Command("login"));
        } else if (eloading_canceled) {
            command.add(createCommand("hwcommand", "device", "drawer", "value", "0"));
        }
        return command;
    }

    /**
     * Method that creates the squash text for the objects of the
     * DisplayListModel
     *
     * @param dlm
     * @param money
     */
    public void createSquashText(DisplayListModel dlm, double money) {
        String marker = handleMarkername();
        squashText = "Rekorder Dateiname: " + marker + "\n";
        for (Object obj : dlm.getDisplayedArtikel()) {
            if (obj instanceof Article) {
                Article art = (Article) obj;
                String erfassung = "";
                if (((Article) obj).isKeyboard()) {
                    erfassung = "über das Keyboard";
                } else {
                    erfassung = "über den Scanner";
                }
                String rabatt = "rabattfähig";
                if (!art.isRabatt()) {
                    rabatt = "nicht " + rabatt;
                }
                squashText += String.format("%dx (Menge) Artikel %-18s %-17s erfassen (EAN: %13s, Preis: %dx %5.2f EUR, Ust: %2d %% %s)\n",
                        art.getAmount(), "\"" + art.getArticleName() + "\"", erfassung, art.getEan(), art.getAmount(), art.getPreis(), art.getUst(), rabatt);
                if (art.isJugendSchutz()) {
                    squashText += "Jugendschutzabfrage ";
                    if (art.isJugendSchutzOk()) {
                        squashText += "positiv\n";
                    } else {
                        squashText += "negativ\n";
                    }
                }
            } else if (obj instanceof Operation) {
                Map<String, Object> map;
                Operation o = (Operation) obj;
                switch (o.getXmlText()) {
                    case WARENRUECKNAHME:
                        squashText += "Warenrücknahme durchführen\n";
                        break;
                    case GEWINNSPIEL_OK:
                        squashText += "Gewinnspiel OK durchführen\n";
                        break;
                    case GEWINNSPIEL_NOK:
                        squashText += "Gewinnspiel NOK durchführen\n";
                        break;
                    case SOFORTSTORNO:
                        squashText += "Sofortstorno durchführen\n";
                        break;
                    case ZEILENSTORNO:
                        squashText += "Zeilenstorno auf Artikel \"" + o.getArticle().getArticleName() + "\"\n";
                        break;
                    case ZUBEZAHLEN:
                        squashText += "Summe\n";
                        break;
                    case SUMME:
                        //squashText += "Den offenen Betrag mit BAR " + String.format("%.2f", money / 100.) + " EUR begleichen\n";
                        squashText += "Rückgeld: " + o.getAdditionalText() + " EUR\n";
                        squashText += "Löschen\n";
                        break;
                    case TEILZAHLUNG:
                        squashText += "Teilzahlung mit BAR " + o.getAdditionalText() + " EUR\n";
                        break;
                    case NACHTRAEGLICHE_AENDERUNG:
                        String description = o.getAdditionalText();
                        String[] descParts = description.split("\\(");
                        squashText += descParts[0] + "mit folgenden Werten vermerkt: ";
                        String content = descParts[1].substring(0, descParts[1].length() - 1);
                        String[] contentParts = content.split("\\|");
                        for (String contentPart : contentParts) {
                            squashText += contentPart;
                        }
                        squashText += "\n";
                        break;
                }
            } else if (obj instanceof Command) {
                if (((Command) obj).getCommandName().equals("card")) {
                    if (((Command) obj).getAttributes().get("id").toString().startsWith("2409")) {
                        squashText += "Payback Nummer/Karte " + ((Command) obj).getAttributes().get("id") + "\n";
                    } else {
                        squashText += "Nummer/Karte " + ((Command) obj).getAttributes().get("id") + " erfassen\n";
                    }
                } else if (((Command) obj).getCommandName().equals("login")) {
                    squashText += "Passwort ändern";
                } else {
                    squashText += "Funktion \"" + (Command) obj + "\" wurde ausgeführt\n";
                }
            }
        }
    }

    /**
     * Outsourced method to transform the articles of the DisplayListModel in
     * Commands
     *
     * @param art
     * @param map
     * @param obj
     * @param command
     */
    private void handleArticles(Map<String, Object> map, Object obj, List<Command> command) {
        Article art = (Article) obj;
        boolean special_weight = false;
        if (art.isEloading()) {
            if (!art.isJugendSchutz() || (art.isJugendSchutz() && art.isJugendSchutzOk())) {
                if (art.getAmount() <= 1) {
                    eloading = true;
                }
            }
            eloading_canceled = true;
        }
        if (((Article) obj).isKeyboard()) {
            map = Stream.of(new Object[][]{
                {"article", art},
                {"input", "keyboard"},}).collect(Collectors.toMap(data -> (String) data[0], data -> (Object) data[1]));
        } else {
            String type = "scan";
            for (Article article : GUIOperations.getAtm().getAllArtikels()) {
                if (article.getArticleName().equalsIgnoreCase(art.getArticleName())) {
                    if (String.format("%.2f", article.getPreis()).equalsIgnoreCase("0,00")) {
                        type = "fix";
                    }
                }
            }
            map = Stream.of(new Object[][]{
                {"article", art},
                {"input", type},}).collect(Collectors.toMap(data -> (String) data[0], data -> (Object) data[1]));
        }
        if (art.isJugendSchutz()) {
            map.put("youthprot", art.isJugendSchutzOk());
        }
        if (art.getAmount() > 1) {
            map.put("quantity", art.getAmount() * 1000);
        }
        if (art.isEloading()) {
            if (!art.getEloadingState().isEmpty()) {
                map.put("eloading_func", art.getEloadingState());
            }
        }
        if (art.isSerialNrRequired()) {
            if ((art.isJugendSchutzOk() && art.isJugendSchutz()) || !art.isJugendSchutz()) {
                map.put("serial_nr", art.getSerialNr());
            }
        }

        if (art.getGewichts_ean_menge() != -1) {
            map.put("quantity", art.getGewichts_ean_menge());
        }

        if (art.isWeight()) {
            if (art.getWeigthArticles() != null) {
                if (art.getWeigthArticles() instanceof Path) {
                    map.put("scale", ((Path) art.getWeigthArticles()).toString());
                } else {
                    special_weight = true;
                }
            }
        }

        command.add(new Command("item", new HashMap<>(map)));
        if (special_weight) {
            HashMap<String, Object> tmp_map = new HashMap<>();
            tmp_map.put("value", 2000);
            command.add(new Command("sync", tmp_map));
            tmp_map = new HashMap<>();
            tmp_map.put("function", "CLR");
            command.add(new Command("controlkey", tmp_map));
            tmp_map = new HashMap<>();
            tmp_map.put("value", (Integer) art.getWeigthArticles());
            command.add(new Command("keyboard", tmp_map));
        }
    }

    /**
     * Method that transforms all objects in Commands
     *
     * @param dlm
     */
    public void getArticlesForXml(DisplayListModel dlm) {
        List<Command> command = new ArrayList<>();
        Object vorObj = null;
        Article lastArt = null;
        for (Object obj : dlm.getDisplayedArtikel()) {
            if (obj instanceof Article) {
                Article art = (Article) obj;
                lastArt = art;
                if (!art.isInXml()) {
                    continue;
                }
                Map<String, Object> map = null;
                handleArticles(map, obj, command);
            } else if (obj instanceof Karte) {
                Karte karte = (Karte) obj;
                command.add(createCommand("card", "id", karte.getId(), karte.getType(), "scan"));
                //command.add(createCommand("keyboard", "value", karte.getAufladung() + ""));
            } else if (obj instanceof Operation) {
                Map<String, Object> map = null;
                Operation o = (Operation) obj;
                switch (o.getXmlText()) {
                    case WARENRUECKNAHME:
                        command.add(createCommand("controlkey", "function", "WRUE"));
                        command.add(createCommand("sync", "value", "200"));
                        //Autorisierung
                        command.add(createCommand("keyboard", "value", "4040"));
                        //Passwort
                        command.add(createCommand("keyboard", "value", "1234"));
                        break;
                    case SOFORTSTORNO:
                        command.add(createCommand("storno", "article", o.getArticle().getXmlArticleName(), "function", "SSTO"));
                        if (o.isNeedsAutorisation()) {
                            command.add(createCommand("sync", "value", "200"));
                            //Autorisierung
                            command.add(createCommand("keyboard", "value", "4040"));
                            //Passwort
                            command.add(createCommand("keyboard", "value", "1234"));
                        }
                        break;
                    case ZUBEZAHLEN:
                        command.add(createCommand("controlkey", "function", "SUMME"));
                        break;
                    case ZEILENSTORNO:
                        command.add(createCommand("storno", "article", o.getArticle().getXmlArticleName(), "input", "scan", "function", "MSTO"));
                        if (o.getArticle().isSerialNrRequired()) {
                            command.add(createCommand("card", "id", o.getArticle().getSerialNr(), "input", "scan"));
                        }
                        if (o.isNeedsAutorisation()) {
                            command.add(createCommand("sync", "value", "200"));
                            //Autorisierung
                            command.add(createCommand("keyboard", "value", "4040"));
                            //Passwort
                            command.add(createCommand("keyboard", "value", "1234"));
                        }
                        break;
                    case SUMME:
                        if (vorObj instanceof Operation) {
                            Operation op = (Operation) vorObj;
                            if (vorObj == null) {
                                JOptionPane.showMessageDialog(null, "Bitte wählen sie vor dem Zahlen mindestens einen Artikel aus");
                                break;
                            }
//                        switch (op.getXmlText()) {
//                            case ZUBEZAHLEN:
//                                break;
//                            default:
//                                command.add(createCommand("controlkey", "function", "SUMME"));
//                        }
                        }
                        String payAm = o.getAdditionalValue() + "";
                        command.add(createCommand("payment", "pay_amount", payAm, "pay_id", "1", "pay_sub", "0", "type", "retour"));
                        break;
                    case TEILZAHLUNG:
                        Operation p = (Operation) obj;

                        String paym = p.getAdditionalValue() + "";
                        if (!GUIOperations.isTraining()) {
                            command.add(createCommand("payment", "pay_amount", paym,
                                    "pay_id", "1", "pay_sub", "0"));
                        } else {
                            command.add(createCommand("payment", "pay_amount", paym,
                                    "pay_id", "1", "pay_sub", "0", "training", "true"));
                        }
                        break;
                    case GESCHENKKARTE:
                    case GESCHENKKARTE_TOTAL:
                        Operation geschenkKarteOP = (Operation) obj;
                        if (geschenkKarteOP.getCommand() != null) {
                            command.add(geschenkKarteOP.getCommand());
                            if (geschenkKarteOP.isNeedsAutorisation()) {
                                command.add(createCommand("sync", "value", "200"));
                                //Autorisierung
                                command.add(createCommand("keyboard", "value", "4040"));
                                //Passwort
                                command.add(createCommand("keyboard", "value", "1234"));
                            }

                        }
                        break;
                }
//                if (obj instanceof Operation && o.getXmlText() == XMLOperations.WARENRUECKNAHME) {
//                    break;
//                }
            } else if (obj instanceof Command) {
                Command tmp_command = (Command) obj;
                command.add(tmp_command);
                if (tmp_command.getCommandName().equalsIgnoreCase("login")) {
                    if (tmp_command.getAttributes().get("changepwd") != null) {
                        if (!tmp_command.getAttributes().get("changepwd").toString().equalsIgnoreCase("false")) {
                            command.add(createCommand("hwcommand", "device", "drawer", "value", "0"));
                        }
                    }
                }
            }
            vorObj = obj;
        }
        try {
            master(command);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that formats the change
     *
     * @param payAm
     * @return
     */
    private String xmlMoney(String payAm) {
        if (payAm.charAt(0) == '0' && payAm.length() > 1) {
            if (payAm.charAt(1) == '0') {
                payAm = payAm.substring(2);
            } else {
                payAm = payAm.substring(1);
            }
        }
        return payAm;
    }

    /**
     * Method that creates the static end of the XML file
     *
     * @return
     * @param marker
     */
    private List<Command> createRecorderEnd() {
        String marker = "";
        try {
            marker = GUIOperations.getInitParams().get("Markername").toString();
        } catch (NullPointerException ex) {
            marker = "leer";
        }
        List<Command> command = new ArrayList<>();
        Map<String, Object> map = null;
        if (eloading_canceled) {
            HashMap<String, Object> tmp_map = new HashMap<>();
            tmp_map.put("function", "CLR");
            command.add(new Command("controlkey", tmp_map));
        }
        if (GUIOperations.isLogout()) {
            command.add(new Command("logout"));
        }
        for (Command overloadCmd : GUIOperations.getOverloadElements()) {
            command.add(overloadCmd);
        }
        if (eloading) {
            command.add(createCommand("syscommand", "command", "/home/npos/mockup/stopeloading.sh"));
        }
        if (geschenkskarte) {
            command.add(createCommand("syscommand", "command", "/home/npos/mockup/stopexperian.sh"));
        }

        for (String sysCommand : sysCommandsEnd) {
            command.add(createCommand("syscommand", "command", sysCommand));
        }

        command.add(createCommand("checkpoint", "name", marker + "_end"));
        command.add(new Command("close"));
        return command;
    }

    /**
     * Method that calls the SquashDlg
     *
     * @param ziel
     */
    private void createJOptionPane(File ziel) {
        SquashDlg dlg = new SquashDlg(null, true, ziel, squashText);
        dlg.setVisible(true);
    }

    /**
     * Method that copies the selected simulation files in the same directory as
     * the recorder
     *
     * @param ziel
     * @throws IOException
     */
    private void copySimulationFiles(String ziel) throws IOException {
        for (File simFile : (List<File>) GUIOperations.getInitParams().get("Simulationsdateien")) {
            String destSimulation = ziel + File.separator + simFile.toString().substring(simFile.toString().lastIndexOf("\\") + 1);
            Path sourcepath = simFile.toPath();
            Files.copy(sourcepath, Paths.get(destSimulation), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Method that checks if a bon is valid
     *
     * @param path
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public boolean isBon(Path path) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(Paths.get(GUIOperations.getResPath().toString(), "itemmapper.xml").toFile());
        NodeList listNode = doc.getElementsByTagName("item");

        for (int i = 0; i < listNode.getLength(); i++) {
            if (listNode.item(i).getNodeType() == Node.ELEMENT_NODE) {
            }
        }
        return true;
    }

    /**
     * Method that writes the XML file
     *
     * @throws TransformerException
     */
    private void writeXML(File ziel, boolean chosen) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            DOMSource source = new DOMSource(doc);
            String line = "";
            StreamResult result = null;
            //if (GUIOperations.getMoney() != 0) {
            if (ziel.toString().endsWith("Documents\\rec.xml") && !chosen) {
                result = new StreamResult(ziel);
                copySimulationFiles(ziel.toString().substring(0, ziel.toString().lastIndexOf("\\") + 1));
            } else {
                if (!GUIOperations.isIsWorkflow()) {
                    try {
                        do {
                            line = (String) JOptionPane.showInputDialog(null, "Geben Sie den gewünschten Speichernamen ein!\nEs sind nur Kleinbuchstaben von a-z, Ziffern von 0-9 und _ erlaubt.", "Eingabe", JOptionPane.INFORMATION_MESSAGE, null, null, "rec");
                        } while (line.isEmpty() || !line.replaceAll("[a-z0-9_]", "").equals(""));
                    } catch (NullPointerException ex) {
                        JOptionPane.showMessageDialog(null, "Da kein Speichername eingegeben wurde,\n"
                                + "wird der Rekorder als \"rec.xml\" gespeichert.", "Kein Speichername", JOptionPane.INFORMATION_MESSAGE);
                        line = "rec";
                    }
                } else {
                    line = GUIOperations.getTextForInitDialog();
                }
                result = new StreamResult(ziel + File.separator + line + ".xml");
                if (GUIOperations.isIsWorkflow()) {
                    GUIOperations.getAllCreatedBons().add(Paths.get(ziel.toString(), line + ".xml"));
                }
                copySimulationFiles(ziel.toString());
            }
            transformer.transform(source, result);
            createJOptionPane(ziel);
            GUIOperations.setMoney(0);
            GUIOperations.setPaid(true);
            //}
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setBonStornoType(int bonStornoType) {
        this.bonStornoType = bonStornoType;
    }

    public int getBonStornoType() {
        return bonStornoType;
    }

    public boolean isGeschenkskarte() {
        return geschenkskarte;
    }

    public void setGeschenkskarte(boolean geschenkskarte) {
        this.geschenkskarte = geschenkskarte;
    }

    public List<String> getSysCommandsStart() {
        return sysCommandsStart;
    }

    public void setSysCommandsStart(List<String> sysCommandsStart) {
        this.sysCommandsStart = sysCommandsStart;
    }

    public List<String> getSysCommandsEnd() {
        return sysCommandsEnd;
    }

    public void setSysCommandsEnd(List<String> sysCommandsEnd) {
        this.sysCommandsEnd = sysCommandsEnd;
    }

}

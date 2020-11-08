package simulator.commands.impl;

import analyzer.bl.AnalyzerManager;
import analyzer.bl.Whitelist;
import analyzer.enums.ResultFileType;
import general.io.Mapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import simulator.commands.ACommand;
import simulator.communication.DTFConnection;
import simulator.beans.RuntimeEnv;
import simulator.bl.ExecutionManager;
import simulator.interfaces.BackofficeAccess;
import simulator.recorder.adapter.CheckpointAdapter;
import simulator.recorder.adapter.ControllKeyAdapter;
import simulator.recorder.adapter.HWCommandAdapter;
import simulator.recorder.adapter.ItemAdapter;
import simulator.recorder.adapter.LoginAdapter;
import simulator.recorder.adapter.LogoutAdapter;
import simulator.recorder.adapter.PauseAdapter;
import simulator.recorder.adapter.PaymentAdapter;
import simulator.recorder.adapter.PwdAdapter;
import simulator.recorder.adapter.input.DtfScanCommand;
import simulator.recorder.adapter.input.DtfTextInputCommand;
import simulator.recorder.util.RecElementType;

public class ExcecuteRecorderFileCommand
        extends ACommand {

    /**
     * Recorder File
     */
    private File xmlRecFile = null;

    /**
     * Verbindung zur Kasse
     */
    private DTFConnection dtfCon;

    /**
     * Verbindung zur Kasse
     */
    private static final String CONST_PARAM_PATHTOXML = "pathtoxml";

    /**
     * Wenn true: Bediener meldet sich ab --> Schließen der Verbindung zur Kasse
     */
    private boolean isLogout = false;

    private int localStep;

    private Path testCasePath;

    private int kassaNr;
    private String strRecFileName;

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");

    private LocalDateTime timestamp;

    private boolean first = true;

    public ExcecuteRecorderFileCommand(RuntimeEnv env, int startStep, Path testCasePath, int kassaNr, LocalDateTime timestamp) {
        objRuntimeEnv = env;
        dtfCon = new DTFConnection(objRuntimeEnv);
        this.testCasePath = testCasePath;
        this.kassaNr = kassaNr;
        this.timestamp = timestamp;
        dtfCon.connectToCashDesk();
        dtfCon.start();
    }

    public int getLocalStep() {
        return localStep;
    }

    public void setLocalStep(int localStep) {
        this.localStep = localStep;
    }

    public File getXmlRecFile() {
        return xmlRecFile;
    }

    /**
     * Arbeits-Schnittstelle fuer das Command
     * 'ExcecuteRecorderFileCommand'<br />
     * Diese Command führt pro Kasse einen Recorder aus <br />
     *
     * @return boolean ... true/false
     *
     * @throws Exception
     *
     * @author MSkergeth
     *
     * @version 1.0
     */
    @Override
    public boolean doWork()
            throws Exception {
        strRecFileName = getValueForKey(CONST_PARAM_PATHTOXML);

        String strRecorderPath
                = objRuntimeEnv.getObjActTG().getObjActTC().getStrTestCasePath() + File.separator + strRecFileName;

        //System.out.println("\n Excecute Recorder File: " + strRecorderPath);
        excecuteRecorderFile(strRecorderPath);

        return true;
    }

    @Override
    public boolean doCheck()
            throws Exception {
        Path erg = testCasePath.getParent().getParent().getParent().resolve("erg").resolve(dtf.format(timestamp)).resolve(testCasePath.getParent().toFile().getName()).resolve(testCasePath.toFile().getName());
        Properties props = Mapper.getCashpointMapping(erg.resolve("erg_ref_mapping.properties").toString());
        List<File> references = Mapper.getDirectoriesOfDirectory(testCasePath.getParent().getParent().getParent().resolve("ref").toFile());
        Path ref = testCasePath.getParent().getParent().getParent().resolve("ref").resolve(references.get(references.size() - 1).getName()).resolve(testCasePath.getParent().toFile().getName()).resolve(testCasePath.toFile().getName()).resolve(props.getProperty("kasse" + kassaNr));

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(xmlRecFile);
        doc.getDocumentElement().normalize();
        String marker = ((Element) doc.getElementsByTagName("mark").item(0)).getAttribute("name");

        boolean error = false;

        try {
            if (!AnalyzerManager.getInstance().trimLines(Whitelist.removeIgnoreSections(Whitelist.applyWhitelist(Files.readAllLines(erg.resolve(Mapper.getDirectoriesOfDirectory(erg.toFile()).get(kassaNr - 1).getName()).resolve("gserver_" + marker + ".txt")), ResultFileType.GSERVER)), ResultFileType.GSERVER)
                    .equals(AnalyzerManager.getInstance().trimLines(Whitelist.removeIgnoreSections(Whitelist.applyWhitelist(Files.readAllLines(ref.resolve("gserver_" + marker + ".txt")), ResultFileType.GSERVER)), ResultFileType.GSERVER))) {
                logToArea("GSERVER", 1);
                error = true;
            } else {
                logToArea("GSERVER", 0);
            }
        } catch (IOException i) {
            if (Files.readAllLines(ref.resolve("gserver_" + marker + ".txt")) == null) {
                logToArea("GSERVER", 2);
            }
        }
        try {
            if (!AnalyzerManager.getInstance().trimLines(Whitelist.removeIgnoreSections(Whitelist.applyWhitelist(Files.readAllLines(erg.resolve(Mapper.getDirectoriesOfDirectory(erg.toFile()).get(kassaNr - 1).getName()).resolve("printer_" + marker + ".txt")), ResultFileType.PRINTER)), ResultFileType.PRINTER)
                    .equals(AnalyzerManager.getInstance().trimLines(Whitelist.removeIgnoreSections(Whitelist.applyWhitelist(Files.readAllLines(ref.resolve("printer_" + marker + ".txt")), ResultFileType.PRINTER)), ResultFileType.PRINTER))) {
                logToArea("PRINTER", 1);
                error = true;
            } else {
                logToArea("PRINTER", 0);
            }
        } catch (IOException i) {
            if (Files.readAllLines(ref.resolve("printer_" + marker + ".txt")) == null) {
                logToArea("PRINTER", 2);
            }
        }
        try {
            if (!AnalyzerManager.getInstance().trimLines(Whitelist.removeIgnoreSections(Whitelist.applyWhitelist(Files.readAllLines(erg.resolve(Mapper.getDirectoriesOfDirectory(erg.toFile()).get(kassaNr - 1).getName()).resolve("display_" + marker + ".txt")), ResultFileType.DISPLAY)), ResultFileType.DISPLAY)
                    .equals(AnalyzerManager.getInstance().trimLines(Whitelist.removeIgnoreSections(Whitelist.applyWhitelist(Files.readAllLines(ref.resolve("display_" + marker + ".txt")), ResultFileType.DISPLAY)), ResultFileType.DISPLAY))) {
                logToArea("DISPLAY", 1);
                error = true;
            } else {
                logToArea("DISPLAY", 0);
            }
        } catch (IOException i) {
            if (Files.readAllLines(ref.resolve("display_" + marker + ".txt")) == null) {
                logToArea("DISPLAY", 2);
            }
        }
        try {

            if (!AnalyzerManager.getInstance().trimLines(Whitelist.removeIgnoreSections(Whitelist.applyWhitelist(Files.readAllLines(erg.resolve(Mapper.getDirectoriesOfDirectory(erg.toFile()).get(kassaNr - 1).getName()).resolve("drawer_" + marker + ".txt")), ResultFileType.DRAWER)), ResultFileType.DRAWER)
                    .equals(AnalyzerManager.getInstance().trimLines(Whitelist.removeIgnoreSections(Whitelist.applyWhitelist(Files.readAllLines(ref.resolve("drawer_" + marker + ".txt")), ResultFileType.DRAWER)), ResultFileType.DRAWER))) {
                logToArea("DRAWER", 1);
                error = true;
            } else {

                logToArea("DRAWER", 0);
            }
        } catch (IOException i) {
            if (Files.readAllLines(ref.resolve("drawer_" + marker + ".txt")) == null) {
                logToArea("DRAWER", 2);
            }
        }
        return !error;
    }

    private void logToArea(String fileN, int status) {
        if (status == 0) {
            ExecutionManager.getInstance().log(String.format("Kassa %d - <b>%s</b> von Rekorder <b>%s</b> in der <b>Testgruppe %s (Testcase %s)</b> ist fehlerfrei", kassaNr, fileN, strRecFileName, testCasePath.getParent().toFile().getName(), testCasePath.toFile().getName()), ExecutionManager.LOGLEVEL.CHECK_OK);
        } else if (status == 1) {
            ExecutionManager.getInstance().log(String.format("Kassa %d - <b>%s</b> von Rekorder <b>%s</b> in der <b>Testgruppe %s (Testcase %s)</b> ist fehlerhaft", kassaNr, fileN, strRecFileName, testCasePath.getParent().toFile().getName(), testCasePath.toFile().getName()), ExecutionManager.LOGLEVEL.CHECK_NOK);
        } else {
            ExecutionManager.getInstance().log(String.format("Kassa %d - <b>%s</b> von Rekorder <b>%s</b> in der <b>Testgruppe %s (Testcase %s)</b> ist neu", kassaNr, fileN, strRecFileName, testCasePath.getParent().toFile().getName(), testCasePath.toFile().getName()), ExecutionManager.LOGLEVEL.CHECK_NEW);
        }
    }

    /**
     * Ließt das Recorder File, erstellt die entsprechenden DTF Commands und
     * sendet diese laufend an die Kasse
     *
     * @param String strXMLPath : Pfad des Recorder Files
     */
    public void excecuteRecorderFile(String strXMLPath) {
        xmlRecFile = new File(strXMLPath);

        if (xmlRecFile.exists()) {
            NodeList recNodes = getNodeListFromFile(xmlRecFile, "bon");

            for (int i = 0; i < recNodes.getLength(); i++) {
                Node node = recNodes.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    String nodeName = node.getNodeName();

                    RecElementType rectype = RecElementType.getRecElementType(nodeName);

                    NamedNodeMap nodeMapAttributes = null;
                    String strCommand = null;

                    switch (rectype) {
                        case MARKER:
                            // Beispiel: <mark name="tg2_rec1" description="EAN 13" />
                            nodeMapAttributes = getNodeAttr("mark", node);
                            String strMarkerName = nodeMapAttributes.getNamedItem("name").getNodeValue();
                            dtfCon.updateCurrentMarkerName(strMarkerName);
                            break;
                        case SYSCOMMAND:
                            // Beispiel: <syscommand command="/home/npos/mockup/starteloading.sh"/>
                            nodeMapAttributes = getNodeAttr("syscommand", node);
                            String strSysCommand = nodeMapAttributes.getNamedItem("command").getNodeValue();
                            strCommand = "<syscommand><command>" + strSysCommand + "</command></syscommand>\n";
                            break;
                        case LOGIN:
                            // Beispiel: <login />
                            nodeMapAttributes = getNodeAttr("login", node);
                            LoginAdapter loginAdapter = new LoginAdapter(objRuntimeEnv.getObjActTG().getStrEmpId(),
                                    objRuntimeEnv.getObjActTG().getStrPasswd());
                            strCommand = loginAdapter.createLoginCommand(nodeMapAttributes);
                            isLogout = false;
                            break;
                        case HWCOMMAND:
                            // Beispiel: <hwcommand device="drawer" value="0" /> (Lade schließen)
                            nodeMapAttributes = getNodeAttr("hwcommand", node);
                            HWCommandAdapter hwCommandAdapter = new HWCommandAdapter();
                            strCommand = hwCommandAdapter.createHWCommand(nodeMapAttributes);
                            break;
                        case PAUSE:
                            // Beispiel: <pause />
                            PauseAdapter pauseAdapter = new PauseAdapter();
                            strCommand = pauseAdapter.createPauseCommand();
                            break;
                        case PWD:
                            // Beispiel: <pwd value="1234" />
                            nodeMapAttributes = getNodeAttr("pwd", node);
                            PwdAdapter pwdAdapter = new PwdAdapter();
                            strCommand = pwdAdapter.createPauseCommand(nodeMapAttributes);
                            break;
                        case ITEM:
                            // Beispiel: <item article="EAN_13_POWER_CAPS_COLOR" input="keyboard" />
                            nodeMapAttributes = getNodeAttr("item", node);
                            ItemAdapter itemAdapter = new ItemAdapter();
                            itemAdapter.createItemCommand(nodeMapAttributes);

                            // Eload Infos muessen vor Artikel an Kasse geschickt werden
                            if (itemAdapter.isEloadingArtikel()) {
                                String strEloadingCommand = itemAdapter.getEloadingCommand();
                                parseAndSendCommand(strEloadingCommand);
                            }

                            String strArticleCommand = itemAdapter.getArticleCommand();
                            parseAndSendCommand(strArticleCommand);

                            if (itemAdapter.isEloadingArtikel()) {

                                String strSerialNumber = itemAdapter.getEloadingInfo().getStrSeriennummer();
                                if (!strSerialNumber.isEmpty()) {
                                    String strSerialNumberCommand = new DtfScanCommand(strSerialNumber).getXml();
                                    parseAndSendCommand(strSerialNumberCommand);
                                }

                            }

                            strCommand = null;
                            break;
                        case CONTROLKEY:
                            // Beispiel: <controlkey function="SUMME" />
                            nodeMapAttributes = getNodeAttr("controlkey", node);
                            ControllKeyAdapter ctrlKeyAdapter = new ControllKeyAdapter();
                            strCommand = ctrlKeyAdapter.createControllKeyCommand(nodeMapAttributes);
                            break;
                        case KEYBOARD:
                            nodeMapAttributes = getNodeAttr("keyboard", node);
                            String strKeyboardValue = nodeMapAttributes.getNamedItem("value").getNodeValue();
                            strCommand = new DtfTextInputCommand(strKeyboardValue).getXml();
                            break;
                        case PAYMENT:
                            // Beispiel: <payment pay_id="1" pay_sub="0" />
                            nodeMapAttributes = getNodeAttr("payment", node);
                            PaymentAdapter paymentAdaper = new PaymentAdapter();
                            strCommand = paymentAdaper.createPaymentCommand(nodeMapAttributes);
                            break;
                        case CHECKPOINT:
                            // Beispiel: <checkpoint name="tg2_rec1_end" />
                            nodeMapAttributes = getNodeAttr("checkpoint", node);
                            CheckpointAdapter checkpointAdapter
                                    = new CheckpointAdapter(objRuntimeEnv.getObjActTG().getStrEmpId());
                            strCommand = checkpointAdapter.createCheckpointCommand(nodeMapAttributes);
                            break;
                        case CLOSE:
                            // Beipsiel: <close />
                            //if (isLogout) {
                            dtfCon.closeConnection();
                            // }
                            break;
                        case LOGOUT:
              try {
                            Thread.sleep(2000);// Warten bis die Kasse umschaltet
                        } catch (InterruptedException e) {
                            System.out.println(e.getLocalizedMessage());
                        }
                        // Beispiel: <logout />
                        LogoutAdapter logoutAdapter = new LogoutAdapter();
                        strCommand = logoutAdapter.createLogoutCommand();
                        isLogout = true;
                        break;
                        default:
                            System.out.println("<" + node.getNodeName() + "/>" + " not supported yet");
                            break;
                    }

                    if (strCommand != null) {
                        parseAndSendCommand(strCommand);
                    }

                }
            }
        } else {
            //System.out.println("excecuteRecorderFile(): Recorder File nicht gefunden.");
            dtfCon.closeConnection();
        }
    }

    /**
     * Sendet ein Recorder Command an die Kasse
     *
     * @param String command : Command welches an die Kasse gesendet werden soll
     */
    public void parseAndSendCommand(String command) {
        if (ExecutionManager.getInstance().isStepLockActivated()) {
            while (localStep == ExecutionManager.getInstance().getStepLock()) {
                if (ExecutionManager.getInstance().getStepLock() == -1) {
                    break;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {

                }
            }
            ExecutionManager.getInstance().log(command, ExecutionManager.LOGLEVEL.XML);
        }
        if (first) {
            ExecutionManager.getInstance().log("                                             ", ExecutionManager.LOGLEVEL.SPEZIAL);
            first = false;
        }
        dtfCon.sendDTFXmlCommand(command);
        try {
            localStep++;
            Thread.sleep(400);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * returns the attribute list of a given element
     *
     * @param tagName ... element name
     * @param node ... node element
     * @return attribute list
     */
    public static NamedNodeMap getNodeAttr(String tagName, Node node) {
        NamedNodeMap attrList = null;
        if (node.getNodeName().equals(tagName)) {
            // element found - read the attributes
            Element element = (Element) node;
            attrList = element.getAttributes();
        }

        return attrList;
    }

    /**
     * parses a xml and returns the Nodelist for the given tag name
     *
     * @param xmlfile
     * @param tagName
     * @return NodeList
     */
    public static NodeList getNodeListFromFile(File xmlfile, String strTagName) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(xmlfile);
            doc.getDocumentElement().normalize();

            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            // NodeList rootList = doc.getDocumentElement().getChildNodes();
            NodeList rootList = doc.getElementsByTagName(strTagName).item(0).getChildNodes();

            return rootList;

        } catch (SAXException saxe) {
            throw new RuntimeException(saxe.getMessage());
        } catch (IOException ioe) {
            throw new RuntimeException(ioe.getMessage());
        } catch (ParserConfigurationException pce) {

            throw new RuntimeException(pce.getMessage());
        }
    }

    public DTFConnection getDtfCon() {
        return dtfCon;
    }

}

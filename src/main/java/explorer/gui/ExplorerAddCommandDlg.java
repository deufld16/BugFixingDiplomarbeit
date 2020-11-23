/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package explorer.gui;

import dashboard.beans.Command;
import dashboard.bl.DatabaseGlobalAccess;
import explorer.beans.Database;
import explorer.beans.Field;
import explorer.beans.Table;
import general.beans.io_objects.CommandRun;
import general.beans.io_objects.TestCaseRun;
import general.bl.GlobalAccess;
import general.bl.GlobalParamter;
import general.io.Mapper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import recorder.guiOperations.GUIOperations;
import explorer.enums.DBNames_Display;
import explorer.io.ExplorerIO;
import java.awt.event.KeyAdapter;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

/**
 *
 * @author Anna Lechner & Florian Deutschmann
 */
public class ExplorerAddCommandDlg extends javax.swing.JDialog {

    private static final Font DEFAULT_FONT = new Font("Tahoma", Font.BOLD, 18);
    private static final Font DEFAULT_FONT_SMALLER = new Font("Tahoma", Font.BOLD, 16);
    private static final Font DEFAULT_FONT_SMALLER_NO_BOLD = new Font("Tahoma", Font.PLAIN, 16);

    private boolean ok;
    private CommandRun command;
    private DefaultComboBoxModel<String> dcbm;
    private String commandTyp;
    private String displayName;
    private NodeList nodeList;
    private Map<String, List<String>> subnodesProCommand;
    private Map<String, List<String>> displayNameProCommand = new HashMap<>();
    private Map<String, String> subNodeToType = new HashMap<>();
    private Map<Node, String> inputPerSubnode;
    private List<Map<Node, String>> inputPerSubnodeList = new LinkedList<>();
    private List<CommandRun> multipleCommandRuns = new LinkedList<>();
    private TestCaseRun parentTC;
    private String recorderName;
    private DefaultMutableTreeNode parentNode;
    private boolean createdBon = false;
    private List<Path> allCreatedBons = new LinkedList<>();
    private File selectedFile = null;
    private String currentTypeForShellCommand = "file";
    private List<String> filesToCopy = new LinkedList<>();
    private Map<String, String> pathToGlobalDir = new HashMap<>();

    /**
     * Default Constructor for the DLG
     *
     * @param parent
     * @param modal
     */
    public ExplorerAddCommandDlg(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public CommandRun getCommand() {
        return command;
    }

    public void setCommand(CommandRun command) {
        this.command = command;
    }

    public String getCommandTyp() {
        return commandTyp;
    }

    public void setCommandTyp(String commandTyp) {
        this.commandTyp = commandTyp;
    }

    public Map<Node, String> getInputPerSubnode() {
        return inputPerSubnode;
    }

    public void setInputPerSubnode(Map<Node, String> inputPerSubnode) {
        this.inputPerSubnode = inputPerSubnode;
    }

    public void setRecorderName(String recorderName) {
        this.recorderName = recorderName;
    }

    /**
     * Creates new form ExplorerAddCommandDlg this constructor is an overwritten
     * version of the default constrcuture
     *
     * @param parent
     * @param modal
     * @param tc which is the associated object of the selected tree node
     * @param dmtn the selected tree node
     */
    public ExplorerAddCommandDlg(java.awt.Frame parent, boolean modal, TestCaseRun tc, DefaultMutableTreeNode dmtn) {
        super(parent, modal);
        try {
            initComponents();
            dcbm = new DefaultComboBoxModel<>();
            lbPicture.setIcon(general.io.Loader.loadLeafIcon("command.png", 40, 40));
            subnodesProCommand = new LinkedHashMap<String, List<String>>();
            inputPerSubnode = new LinkedHashMap<Node, String>();
            setModelOnComboBox(0);
            commandTyp = cbCommandTyp.getItemAt(0).substring(cbCommandTyp.getItemAt(0).indexOf("(") + 1, cbCommandTyp.getItemAt(0).indexOf(")"));
            displayName = cbCommandTyp.getItemAt(0).substring(0, cbCommandTyp.getItemAt(0).indexOf("("))
                    .trim().replaceAll("[(]", "").replaceAll("[)]", displayName);
            setSize(new Dimension(600, 400));
            setResizable(false);
            loadDynamicLabels();
            setLocationRelativeTo(null);
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            this.parentTC = tc;
            this.parentNode = dmtn;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * This method is being used adapt the currently selected
     * commandTyp/nodeList after selecting a different item in the ComboBox
     *
     * @param currentCommand index of the currently selected item in the
     * combobox
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    private void setModelOnComboBox(int currentCommand) throws SAXException, IOException, ParserConfigurationException {
        dcbm.removeAllElements();
        int cnt = 0;
        for (CommandRun mapCom : Mapper.mapCommandsToBeans(GlobalParamter.getInstance().getGeneralCommandsPath())) {
            if (cnt == currentCommand) {
                commandTyp = mapCom.getClassName();
                displayName = mapCom.getDisplayName();
            }
            dcbm.addElement(mapCom.getDisplayName() + " (" + mapCom.getClassName() + ")");
            List<String> subnodes = new ArrayList<>();
            List<String> display_name = new ArrayList<>();
            for (int i = 1; i < mapCom.getNodeList().getLength(); i += 2) {
                subnodes.add(mapCom.getNodeList().item(i).getNodeName());
                display_name.add(mapCom.getNodeList().item(i).getAttributes().getNamedItem("displayname").getNodeValue());
                if (mapCom.getClassName().equalsIgnoreCase("ExportTableContentCommand") && !GlobalAccess.getInstance().isDatabaseReachable()) {
                    subNodeToType.put(mapCom.getNodeList().item(i).getNodeName(), "string");
                } else {
                    subNodeToType.put(mapCom.getNodeList().item(i).getNodeName(), mapCom.getNodeList().item(i).getAttributes().getNamedItem("type").getNodeValue());
                }
            }
            subnodesProCommand.put(mapCom.getClassName(), subnodes);
            displayNameProCommand.put(mapCom.getClassName(), display_name);
            if (mapCom.getClassName().equals(commandTyp)) {
                nodeList = mapCom.getNodeList();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    if (!nodeList.item(i).getNodeName().equals("#text")) {
                        Node node = nodeList.item(i);
                    }
                }
            }
            cnt++;
        }
        subNodeToType.put("global_dir", "special_1");
        cbCommandTyp.setModel(dcbm);
        dcbm.setSelectedItem(displayName + " (" + commandTyp + ")");
    }

    /**
     * Method which is being used to dynamically load the GUI depending on which
     * Command is currently selected
     */
    private void loadDynamicLabels() {
        paColumns.removeAll();
        paTextFields.removeAll();
        paColumns.setLayout(new GridLayout(subnodesProCommand.get(commandTyp).size(), 1));
        paTextFields.setLayout(new BorderLayout());

        for (int i = 0; i < subnodesProCommand.get(commandTyp).size(); i++) {
            if (subnodesProCommand.get(commandTyp).get(i).equalsIgnoreCase("dynamic")) {
                try {
                    JLabel tmp_lb = new JLabel();
                    for (int j = 1; j < nodeList.getLength(); j += 2) {
                        if (nodeList.item(j).getNodeName().equalsIgnoreCase("dynamic")) {
                            String parts[] = nodeList.item(j).getAttributes().getNamedItem("items").getNodeValue().split(";");
                            for (String part : parts) {
                                if (part.split("-")[0].equalsIgnoreCase(currentTypeForShellCommand)) {
                                    tmp_lb.setText(part.split("-")[1]);
                                }
                            }
                        }
                    }
                    tmp_lb.setFont(DEFAULT_FONT);
                    paColumns.add(tmp_lb);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                JLabel tmp_lb = new JLabel(displayNameProCommand.get(commandTyp).get(i));
                tmp_lb.setFont(DEFAULT_FONT);
                paColumns.add(tmp_lb);
            }
        }

        paTextFields.add(returnPanelForType(subnodesProCommand.get(commandTyp)));
        (((JTextField) ((JPanel) paTextFields.getComponent(0)).getComponent(((JPanel) paTextFields.getComponent(0)).getComponentCount() - 1)))
                .addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            doOkAction();
                        }
                    }
                });

        if (commandTyp.equalsIgnoreCase("ExportTableContentCommand")) {
            setSize(new Dimension(1000, 1000));
            setLocationRelativeTo(null);
        } else if (commandTyp.equalsIgnoreCase("GlobalCommand")) {
            setSize(new Dimension(850, 400));
            setLocationRelativeTo(null);
        } else {
            setSize(new Dimension(600, 400));
            setLocationRelativeTo(null);
        }

        paColumns.updateUI();
        paTextFields.updateUI();
        paEast.updateUI();
    }

    /**
     * This method return a JPanel which contains all requried input fields
     * depending on the type attribute which is being set in the nodes of each
     * command that are being retrieved from the commands.xml file
     *
     * @param allIdentifiers list of strings, which contains all the child-node
     * names of a command
     * @return
     */
    public JPanel returnPanelForType(List<String> allIdentifiers) {
        JPanel returnPanel = new JPanel(new GridLayout(allIdentifiers.size(), 1));
        for (String tmp_identifier : allIdentifiers) {
            String identifier = subNodeToType.get(tmp_identifier);
            switch (identifier) {
                case "combobox":
                    JComboBox tmp_cb = new JComboBox();
                    tmp_dcbm_global = new DefaultComboBoxModel();
                    fillComboBox(tmp_identifier, tmp_dcbm_global);
                    tmp_cb.setFont(DEFAULT_FONT);
                    tmp_cb.setModel(tmp_dcbm_global);
                    if (tmp_identifier.equalsIgnoreCase("shell_cmd_type")) {
                        tmp_cb.addActionListener(this::shellCommandOnChangEvent);
                    } else if (tmp_identifier.equalsIgnoreCase("db")) {
                        tmp_cb.addActionListener(this::exportTableOnChangeEvent);
                    }
                    returnPanel.add(tmp_cb);
                    break;
                case "special_1":
                    JPanel help = new JPanel(new GridLayout(1, 2));
                    JPanel speical_sub_panel_1 = new JPanel(new BorderLayout());
                    JPanel speical_sub_panel_2 = new JPanel(new BorderLayout());
                    //JLabel lbSpecial_1 = new JLabel("Filter: ");
                    cb_special_1 = new JComboBox();
                    DefaultComboBoxModel<String> tmp_dcbm_2 = new DefaultComboBoxModel();
                    fillComboBox(tmp_identifier, tmp_dcbm_2);
                    cb_special_1.setFont(DEFAULT_FONT);
                    cb_special_1.setModel(tmp_dcbm_2);
                    speical_sub_panel_1.add(cb_special_1);
                    speical_sub_panel_1.setBorder(new TitledBorder("Verfügbare Directories"));
                    help.add(speical_sub_panel_1);
                    tf_special_1 = new JTextField();
                    if (tmp_identifier.equalsIgnoreCase("table")) {
                        speical_sub_panel_1.setBorder(new TitledBorder("Verfügbare Tabellen"));
                        cb_special_1.addActionListener(this::exportTableOnChangeTableEvent);
                        tf_special_1.addKeyListener(new KeyAdapter() {
                            @Override
                            public void keyReleased(KeyEvent e) {
                                filter_text_db_tables(e, tf_special_1, cb_special_1);
                            }
                        });
                    } else {
                        tf_special_1.addKeyListener(new KeyAdapter() {
                            @Override
                            public void keyReleased(KeyEvent e) {
                                filter_text_special_1(e, tf_special_1, cb_special_1);
                            }
                        });
                    }
                    speical_sub_panel_2.add(tf_special_1);
                    speical_sub_panel_2.setBorder(new TitledBorder("Filter"));
                    help.add(speical_sub_panel_2);
                    returnPanel.add(help);
                    break;
                case "special2":
                    JPanel helpPanel2 = new JPanel(new BorderLayout());
                    if (createdBon || allCreatedBons.size() > 0) {
                        JPanel helpPanel = new JPanel(new GridLayout(allCreatedBons.size() + 1, 1));
                        JLabel lb_help_2 = new JLabel("Selektierte Rekorder: ");
                        lb_help_2.setFont(DEFAULT_FONT);
                        lb_help_2.setForeground(Color.red);
                        helpPanel.add(lb_help_2);
                        for (int j = 0; j < allCreatedBons.size(); j++) {
                            JLabel tmp_lb = new JLabel(allCreatedBons.get(j).getFileName().toString());
                            tmp_lb.setBorder(new EmptyBorder(0, 40, 0, 0));
                            tmp_lb.setFont(DEFAULT_FONT_SMALLER);
                            helpPanel.add(tmp_lb);
                        }
                        helpPanel2.add(helpPanel, BorderLayout.CENTER);
                        createPanelForNewRecorder(helpPanel2, createdBon);
                        returnPanel.add(helpPanel2);
                    } else {
                        createPanelForNewRecorder(helpPanel2, createdBon);
                        returnPanel.add(helpPanel2);
                    }
                    break;
                case "special_3":
                    PaFelderUI paFelder = new PaFelderUI();
                    for (Field field : GlobalAccess.getInstance().getAllDatabases().get(db_index).getAllTables().get(db_table_index).getAllFields()) {
                        paFelder.getDlm_available().addElement(field);
                    }
                    returnPanel.add(paFelder);
                    break;
                case "string":
                    JTextField tmp_tf = new JTextField();
                    tmp_tf.setFont(DEFAULT_FONT);
                    if (tmp_identifier.equalsIgnoreCase("order_by") && GlobalAccess.getInstance().isDatabaseReachable()) {
                        tmp_tf.setEditable(false);
                        tf_exportTableCommandSortCriteria = tmp_tf;
                    }
                    returnPanel.add(tmp_tf);
                    break;
                case "int":
                    JTextField tmp_int_tf = new JTextField();
                    tmp_int_tf.setFont(DEFAULT_FONT);
                    tmp_int_tf.addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyPressed(KeyEvent e) {
                            removeUnwantedInput(e);
                        }

                        @Override
                        public void keyReleased(KeyEvent e) {
                            removeUnwantedInput(e);
                        }

                        private void removeUnwantedInput(KeyEvent e) {
                            JTextField tf = (JTextField) e.getSource();
                            String input = tf.getText();
                            String output = "";
                            for (int i = 0; i < input.length(); i++) {
                                if (input.charAt(i) <= '9' && input.charAt(i) >= '0') {
                                    output += input.charAt(i) + "";
                                }
                            }
                            tf.setText(output);
                        }
                    });
                    returnPanel.add(tmp_int_tf);
                    break;
                case "boolean":
                    JPanel tmp_bool_panel = new JPanel(new GridLayout(1, 2));
                    JRadioButton rb_true = new JRadioButton("True");
                    rb_true.setFont(DEFAULT_FONT);
                    JRadioButton rb_false = new JRadioButton("False");
                    ButtonGroup bg = new ButtonGroup();
                    bg.add(rb_true);
                    bg.add(rb_false);
                    rb_false.setFont(DEFAULT_FONT);
                    tmp_bool_panel.add(rb_true);
                    tmp_bool_panel.add(rb_false);
                    returnPanel.add(tmp_bool_panel);
                    break;
                case "file":
                    JPanel file_panel_help = new JPanel(new GridLayout(1, 2));
                    JPanel file_panel_1 = new JPanel(new BorderLayout());
                    JPanel file_panel_2 = new JPanel(new BorderLayout());

                    lbSelectedFile = new JLabel();
                    lbSelectedFile.setFont(DEFAULT_FONT);
                    file_panel_1.add(lbSelectedFile);
                    file_panel_1.setBorder(new TitledBorder("Ausgewählte Datei"));

                    JButton btnSelectFile = new JButton("Datei auswählen");
                    btnSelectFile.addActionListener(this::onSelectFile);
                    file_panel_2.add(btnSelectFile);

                    file_panel_help.add(file_panel_1);
                    file_panel_help.add(file_panel_2);
                    returnPanel.add(file_panel_help);
                    break;
                case "shell_command":
                    if (currentTypeForShellCommand.equalsIgnoreCase("file")) {
                        JPanel shell_command_help = new JPanel(new GridLayout(1, 2));
                        JPanel shell_command_panel_1 = new JPanel(new BorderLayout());
                        JPanel shell_command_panel_2 = new JPanel(new BorderLayout());

                        lbSelectedFile = new JLabel();
                        lbSelectedFile.setFont(DEFAULT_FONT);
                        shell_command_panel_1.add(lbSelectedFile);
                        shell_command_panel_1.setBorder(new TitledBorder("Ausgewählte Datei"));

                        JButton btnSelectFile2 = new JButton("Datei auswählen");
                        btnSelectFile2.addActionListener(this::onSelectFile);
                        shell_command_panel_2.add(btnSelectFile2);

                        shell_command_help.add(shell_command_panel_1);
                        shell_command_help.add(shell_command_panel_2);
                        returnPanel.add(shell_command_help);
                        break;
                    } else {
                        JTextField tf_shell_command = new JTextField();
                        tf_shell_command.setFont(DEFAULT_FONT);
                        returnPanel.add(tf_shell_command);
                    }
                    break;
            }
        }
        return returnPanel;
    }

    /**
     * Method for the command "ShellCommand" which is being callled whenever the
     * combobox in the dynamic GUI of this component is changed to adapt one
     * input field dynamically on changing the combobox item
     *
     * @param evt
     */
    private void shellCommandOnChangEvent(ActionEvent evt) {
        switch (((JComboBox<String>) (evt.getSource())).getSelectedItem().toString()) {
            case "shell_type_command":
                currentTypeForShellCommand = "string";
                break;
            case "shell_type_script":
                currentTypeForShellCommand = "file";
                break;
        }
        loadDynamicLabels();
    }

    /**
     * Method to export all available tables
     *
     * @param evt
     */
    private void exportTableOnChangeEvent(ActionEvent evt) {
        if (GlobalAccess.getInstance().isDatabaseReachable()) {
            for (Database allDatabase : GlobalAccess.getInstance().getAllDatabases()) {
                if (allDatabase.getDatabaseName().equalsIgnoreCase(((JComboBox<String>) evt.getSource()).getSelectedItem().toString())) {
                    db_index = GlobalAccess.getInstance().getAllDatabases().indexOf(allDatabase);
                }
            }
            db_table_index = 0;
            JPanel specialPanel = (JPanel) ((JPanel) paTextFields.getComponent(0)).getComponent(1);
            JPanel help_panel = (JPanel) specialPanel.getComponent(0);
            JComboBox<String> cbSpecial = (JComboBox<String>) help_panel.getComponent(0);
            fillComboBox("table", (DefaultComboBoxModel) cbSpecial.getModel());
        }
    }

    /**
     * Method to export a table
     *
     * @param evt
     */
    private void exportTableOnChangeTableEvent(ActionEvent evt) {
        if (((JComboBox<String>) evt.getSource()).getSelectedItem() != null) {
            for (Table table : GlobalAccess.getInstance().getAllDatabases().get(db_index).getAllTables()) {
                if (table.getTableName().equalsIgnoreCase(((JComboBox<String>) evt.getSource()).getSelectedItem().toString())) {
                    db_table_index = GlobalAccess.getInstance().getAllDatabases().get(db_index).getAllTables().indexOf(table);
                }
            }
            (((PaFelderUI) ((JPanel) paTextFields.getComponent(0)).
                    getComponent(2))).getDlm_available().removeAllElements();

            (((PaFelderUI) ((JPanel) paTextFields.getComponent(0)).
                    getComponent(2))).getDlm_selected().removeAllElements();
            for (Field field : GlobalAccess.getInstance().getAllDatabases().get(db_index).getAllTables().get(db_table_index).getAllFields()) {
                (((PaFelderUI) ((JPanel) paTextFields.getComponent(0)).
                        getComponent(2))).getDlm_available().addElement(field);
            }
        } else {
            (((PaFelderUI) ((JPanel) paTextFields.getComponent(0)).
                    getComponent(2))).getDlm_available().removeAllElements();

            (((PaFelderUI) ((JPanel) paTextFields.getComponent(0)).
                    getComponent(2))).getDlm_selected().removeAllElements();
        }
    }

    /**
     * Method to set a specific text for a text fields
     *
     * @param field
     * @param type
     */
    public void setTextForTextField(Field field, int type) {
        if (!tf_exportTableCommandSortCriteria.getText().contains(field.getFieldName()) || type == 2) {
            switch (type) {
                case 0:
                    if (tf_exportTableCommandSortCriteria.getText().isEmpty()) {
                        tf_exportTableCommandSortCriteria.setText(field.getFieldName());
                    } else {
                        tf_exportTableCommandSortCriteria.setText(tf_exportTableCommandSortCriteria.getText() + ", " + field.getFieldName());
                    }
                    break;
                case 1:
                    if (tf_exportTableCommandSortCriteria.getText().isEmpty()) {
                        tf_exportTableCommandSortCriteria.setText(field.getFieldName() + " DESC");
                    } else {
                        tf_exportTableCommandSortCriteria.setText(tf_exportTableCommandSortCriteria.getText() + ", " + field.getFieldName() + " DESC");
                    }
                    break;
                case 2:
                    tf_exportTableCommandSortCriteria.setText(tf_exportTableCommandSortCriteria.getText().replace(field.getFieldName() + " DESC, ", ""));
                    tf_exportTableCommandSortCriteria.setText(tf_exportTableCommandSortCriteria.getText().replace(field.getFieldName() + " DESC", ""));
                    tf_exportTableCommandSortCriteria.setText(tf_exportTableCommandSortCriteria.getText().replace(field.getFieldName() + ", ", ""));
                    tf_exportTableCommandSortCriteria.setText(tf_exportTableCommandSortCriteria.getText().replace(field.getFieldName(), ""));
                    break;
            }
        }
    }

    /**
     * Method which is being used for any kind of node that has they attribute
     * type="file". This method is called when the JButton in the corresponding
     * GUI is being pressed to open a JFileChooser where a file can be selected
     *
     * @param evt
     */
    private void onSelectFile(ActionEvent evt) {
        JFileChooser fc = new JFileChooser(System.getProperty("user.home") + File.separator + "Documents");
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = new File(fc.getSelectedFile().toString());
            lbSelectedFile.setText(selectedFile.getName());
            filesToCopy.add(selectedFile.toString() + ";" + Paths.get(parentTC.getPath().toString(), selectedFile.getName()).toString());
        }
    }

    /**
     * This method is for the command "GlobalCommand". It is used for a special
     * component that the GUI of this command uses which allows the filtering of
     * a combox.
     *
     * @param evt
     * @param tf_filter the textField which is used for the filtering purpose
     * @param cbToFilter the comboBox which should be filtered
     */
    private void filter_text_special_1(KeyEvent evt, JTextField tf_filter, JComboBox cbToFilter) {
        DefaultComboBoxModel<String> tmp_dcbm = (DefaultComboBoxModel<String>) cbToFilter.getModel();
        tmp_dcbm.removeAllElements();
        fillComboBox("global_dir", tmp_dcbm);
        List<String> filteredComoboBoxContent = new LinkedList<>();
        for (int i = 0; i < tmp_dcbm.getSize(); i++) {
            if (tmp_dcbm.getElementAt(i).toString().toUpperCase().contains(tf_filter.getText().toUpperCase().trim())) {
                filteredComoboBoxContent.add(tmp_dcbm.getElementAt(i).toString());
            }
        }
        tmp_dcbm.removeAllElements();
        for (String element : filteredComoboBoxContent) {
            tmp_dcbm.addElement(element);
        }
    }

    private void filter_text_db_tables(KeyEvent evt, JTextField tf_filter, JComboBox cbToFilter) {
        db_filter_table = tf_filter.getText();
        fillComboBox("table", (DefaultComboBoxModel) cbToFilter.getModel());
    }

    /**
     * This method is used to fill the combox for the diffrent commands with the
     * right content
     *
     * @param identifier identifies the combobox which should be filled
     * @param tmp_dcbm the combobox that has to be filled
     */
    private void fillComboBox(String identifier, DefaultComboBoxModel tmp_dcbm) {
        switch (identifier) {
            case "global_type":
                tmp_dcbm.addElement("activate");
                tmp_dcbm.addElement("deactivate");
                break;
            case "global_dir": {
                try {
                    if (Files.exists(Paths.get(settings.io.Loader.getSpecificParameter(false).get("globalPath")))) {
                        File[] allSubDir = new File(Paths.get(settings.io.Loader.
                                getSpecificParameter(false).get("globalPath")).toString()).listFiles(File::isDirectory);
                        for (File sub_dir : allSubDir) {
                            for (File sub_sub_dir : new File(sub_dir.toString()).listFiles(File::isDirectory)) {
                                if (Files.exists(Paths.get(sub_sub_dir.toString(), "run.xml"))) {
                                    String commandInXml = ExplorerIO.commandsInXml(Paths.get(sub_sub_dir.toString(), "run.xml"));
                                    tmp_dcbm.addElement(commandInXml);
                                    pathToGlobalDir.put(commandInXml, Paths.get(sub_sub_dir.toString(), "run.xml").toString());
                                }
                            }
                        }
                    }
                    if (tmp_dcbm.getSize() == 0) {
                        pathToGlobalDir.put("No Elements found", "Pfad muss manuell eingegeben werden");
                        tmp_dcbm.addElement("No Elements found");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            break;

            case "command_batch":
                for (String element : GlobalParamter.getInstance().getCtrl_command_v2_mapping().keySet()) {
                    tmp_dcbm.addElement(element.toLowerCase());
                }
                break;
            case "shell_cmd_type":
                for (int i = 0; i < nodeList.getLength(); i++) {
                    if (nodeList.item(i).getNodeName().equalsIgnoreCase("shell_cmd_type")) {
                        String value = nodeList.item(i).getAttributes().getNamedItem("items").getNodeValue();
                        for (String combobox_entry : value.split(";")) {
                            tmp_dcbm.addElement(combobox_entry);
                        }
                    }
                }
                String selectedItem = currentTypeForShellCommand.equalsIgnoreCase("file") ? "shell_type_script" : "shell_type_command";
                tmp_dcbm.setSelectedItem(selectedItem);
                break;
            case "command_method":
                tmp_dcbm.addElement("get");
                tmp_dcbm.addElement("mget");
                tmp_dcbm.addElement("put");
                tmp_dcbm.addElement("mput");
                tmp_dcbm.addElement("delete");
                break;
            case "db":
                String itemToSelect = null;
                int index = 0;
                for (DBNames_Display value : DBNames_Display.values()) {
                    if (index == db_index) {
                        itemToSelect = value.toString();
                    }
                    index++;
                    tmp_dcbm.addElement(value.toString());
                }
                tmp_dcbm.setSelectedItem(itemToSelect);
                break;
            case "table":
                boolean contains = false;
                String selectedTableItem = GlobalAccess.getInstance().getAllDatabases().get(db_index).
                        getAllTables().get(db_table_index).getTableName();
                tmp_dcbm.removeAllElements();

                for (Table table : GlobalAccess.getInstance().getAllDatabases().get(db_index).getAllTables()) {
                    if (table.getTableName().toUpperCase().contains(db_filter_table.toUpperCase())) {
                        tmp_dcbm.addElement(table);
                        if (table.getTableName().equalsIgnoreCase(selectedTableItem)) {
                            contains = true;
                        }
                    }
                }
                if (contains) {
                    tmp_dcbm.setSelectedItem(selectedTableItem);
                } else {
                    Table table = (Table) tmp_dcbm.getElementAt(0);
                    if (table != null) {
                        db_table_index = GlobalAccess.getInstance().getAllDatabases().get(db_index).getAllTables().indexOf(table);
                    } else {
                        db_table_index = 0;
                    }
                }
                break;
            case "field":
                tmp_dcbm.removeAllElements();
                break;
        }
    }

    /**
     * This method adds the required buttons to the "CreateExecuteableRecorder"
     * GUI in the center of the BorderLayout if not Bon.xml has yet been created
     * or selected or places them in the South part of the BorderLayout if
     * x-many bons have already been created
     *
     * @param panel
     * @param center indicates if a bon has already been created/selected
     */
    private void createPanelForNewRecorder(JPanel panel, boolean center) {
        JButton btnNewRecFile = new JButton("Neuen Rekorder erstellen");
        btnNewRecFile.setFont(DEFAULT_FONT_SMALLER);
        btnNewRecFile.addActionListener(this::onCreateNewRecorder);

        JButton btnSelectExistingBon = new JButton("Vorhandenen Rekorder auswählen");
        btnSelectExistingBon.setFont(DEFAULT_FONT_SMALLER);
        btnSelectExistingBon.addActionListener(this::onSelectExistingBon);

        JPanel helpPanel = new JPanel(new GridLayout(1, 2));
        helpPanel.add(btnNewRecFile);
        helpPanel.add(btnSelectExistingBon);

        btnSelectExistingBon.setMinimumSize(new Dimension(250, 50));
        btnNewRecFile.setMinimumSize(new Dimension(250, 50));
        if (center) {
            panel.add(helpPanel, BorderLayout.SOUTH);
        } else {
            panel.add(helpPanel, BorderLayout.CENTER);
        }
    }

    /**
     * Method which is associated with the command "CreateExecuteableRecorder".
     * This method is used when a new Recorder should be created. This method
     * sets all required PArameters in the GuiOperations method and then changes
     * to the subtool recorder to record the needed and wanted bon.xml file.
     *
     * @param event
     */
    private void onCreateNewRecorder(ActionEvent event) {
        this.setVisible(false);
        GUIOperations.setIsWorkflow(true);
        if (!allCreatedBons.isEmpty()) {
            GUIOperations.setAllCreatedBons(allCreatedBons);
        }
        GUIOperations.setSaveLocationIfWorkflow(parentTC.getPath());
        GlobalParamter.getInstance().setDlg(this);
        GUIOperations.setTextForInitDialog(recorderName);
        GlobalAccess.getInstance().getTest_ide_main_frame().changeTool("recorder");
    }

    public TestCaseRun getParentTC() {
        return parentTC;
    }

    /**
     * This method is being called when the user decides to return from the
     * recorder tool (if the user has created enough bons for the TestCase) This
     * method sets the List<Path> to all the List of commands that has been
     * created in the recorder tool. Furthermore it updates the GUI to display
     * all the added xmls and changes the Textfield to an recommended
     * command_text.
     *
     * @param createdBons this is the list of all bons that were created in the
     * recorder
     */
    public void returnFromCreatingRecorder(List<Path> createdBons) {
        createdBon = true;
        allCreatedBons = new LinkedList<>(createdBons);
        String parts[] = GUIOperations.getTextForInitDialog().split("_");
        loadDynamicLabels();
        if (allCreatedBons.size() == 1) {
            (((JTextField) ((JPanel) paTextFields.getComponent(0)).getComponent(1))).setText("Erstelle Kassenrekorder "
                    + recorderName.replaceAll("rec_tg", "").replaceAll("tc", ""));
        } else {
            (((JTextField) ((JPanel) paTextFields.getComponent(0)).getComponent(1))).setText("Erstelle mehrere Kassenrekorder");
        }
        recorderName = parts[0] + "_" + parts[1] + "_" + parts[2] + "_" + (Integer.parseInt(parts[3]) + 1);
    }

    /**
     * This method is being used to select an existing command in the
     * CreateExecuteableRecorder command. It tests if the selected file is
     * actually a bon.xml and moves the file into the required directory within
     * our structure. Then it also adds the path of the file to the List<Path>
     * which are being used in the event where the run.xml has to been modified
     * to also contain the newly added commands
     *
     * @param event
     */
    private void onSelectExistingBon(ActionEvent event) {
        GlobalParamter.getInstance().setDlg(this);
        JFileChooser fc = new JFileChooser(System.getProperty("user.home") + File.separator + "Documents");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("XML-Dateien", "xml");
        fc.setFileFilter(filter);
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                if (recorder.io.IOChecker.checkValidBon(fc.getSelectedFile().toPath())) {
                    filesToCopy.add(fc.getSelectedFile().toString() + ";" + Paths.get(parentTC.getPath().toString(), recorderName + ".xml").toString());
                    //Files.copy(fc.getSelectedFile().toPath(), Paths.get(parentTC.getPath().toString(), recorderName + ".xml"));
                    allCreatedBons.add(Paths.get(parentTC.getPath().toString(), recorderName + ".xml"));
                    String parts[] = recorderName.split("_");
                    String recorderNumber = recorderName.replaceAll("rec_tg", "").replaceAll("tc", "");
                    createdBon = true;
                    loadDynamicLabels();
                    if (allCreatedBons.size() == 1) {
                        (((JTextField) ((JPanel) paTextFields.getComponent(0)).getComponent(1))).setText("Erstelle Kassenrekorder "
                                + recorderNumber);
                    } else {
                        (((JTextField) ((JPanel) paTextFields.getComponent(0)).getComponent(1))).setText("Erstelle mehrere Kassenrekorder");
                    }
                    recorderName = parts[0] + "_" + parts[1] + "_" + parts[2] + "_" + (Integer.parseInt(parts[3]) + 1);
                } else {
                    JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Bei der Ausgewählten Datei handelt es sich um keinen gültigen Bon");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paHeadline = new javax.swing.JPanel();
        lbPicture = new javax.swing.JLabel();
        lbHeadLine = new javax.swing.JLabel();
        paWest = new javax.swing.JPanel();
        lbCommandTyp = new javax.swing.JLabel();
        paColumns = new javax.swing.JPanel();
        paEast = new javax.swing.JPanel();
        cbCommandTyp = new javax.swing.JComboBox<>();
        paTextFields = new javax.swing.JPanel();
        paButtons = new javax.swing.JPanel();
        btOk = new javax.swing.JButton();
        btAbbrechen = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        paHeadline.setLayout(new java.awt.BorderLayout());

        lbPicture.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbPicture.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5));
        paHeadline.add(lbPicture, java.awt.BorderLayout.WEST);

        lbHeadLine.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lbHeadLine.setText("Command hinzufügen");
        lbHeadLine.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        paHeadline.add(lbHeadLine, java.awt.BorderLayout.CENTER);

        getContentPane().add(paHeadline, java.awt.BorderLayout.NORTH);

        paWest.setLayout(new java.awt.BorderLayout());

        lbCommandTyp.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbCommandTyp.setText("Commandart:");
        lbCommandTyp.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        lbCommandTyp.setPreferredSize(new java.awt.Dimension(126, 42));
        paWest.add(lbCommandTyp, java.awt.BorderLayout.NORTH);

        paColumns.setLayout(new java.awt.GridLayout(1, 1));
        paWest.add(paColumns, java.awt.BorderLayout.CENTER);

        getContentPane().add(paWest, java.awt.BorderLayout.WEST);

        paEast.setLayout(new java.awt.BorderLayout());

        cbCommandTyp.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cbCommandTyp.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        cbCommandTyp.setPreferredSize(new java.awt.Dimension(39, 42));
        cbCommandTyp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeCommandType(evt);
            }
        });
        paEast.add(cbCommandTyp, java.awt.BorderLayout.NORTH);

        paTextFields.setLayout(new java.awt.GridLayout(1, 1));
        paEast.add(paTextFields, java.awt.BorderLayout.CENTER);

        getContentPane().add(paEast, java.awt.BorderLayout.CENTER);

        paButtons.setLayout(new java.awt.GridLayout(1, 2));

        btOk.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btOk.setText("OK");
        btOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onOk(evt);
            }
        });
        paButtons.add(btOk);

        btAbbrechen.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btAbbrechen.setText("Abbrechen");
        btAbbrechen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onCancel(evt);
            }
        });
        paButtons.add(btAbbrechen);

        getContentPane().add(paButtons, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * Method which cancels the command creation
     *
     * @param evt
     */
    private void onCancel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onCancel
        ok = false;
        dispose();
    }//GEN-LAST:event_onCancel
    /**
     * Method which finishes the command creation and sets all the data which is
     * needed to modify the run.xml to also contain the newly added command It
     * checks which kind of dynamic input fields are being used and fetches the
     * data as needed. If multiple bons are created it also offers the
     * possiblity to change the command_text of each bon to the recommended
     * format.
     *
     * @param evt
     */
    private void onOk(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onOk
        doOkAction();
    }//GEN-LAST:event_onOk

    private void doOkAction() {
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeName().equals("#text")) {
                nodeList.item(i).getParentNode().removeChild(nodeList.item(i));
            }
        }
        String description = "";
        JPanel relevantPanel = (JPanel) paTextFields.getComponent(0);
        try {
            if (allCreatedBons.size() == 0 && !commandTyp.equalsIgnoreCase("ExecuteRecorderFileCommand")) {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    if (!nodeList.item(i).getNodeName().equals("#text")) {
                        switch (nodeList.item(i).getAttributes().getNamedItem("type").getNodeValue()) {
                            case "combobox":
                                JComboBox cb = (JComboBox) relevantPanel.getComponent(i);
                                inputPerSubnode.put(nodeList.item(i), cb.getSelectedItem().toString());
                                break;
                            case "special_1":
                                if (commandTyp.equalsIgnoreCase("ExportTableContentCommand") && !GlobalAccess.getInstance().isDatabaseReachable()) {
                                    JTextField tf_tmp = (JTextField) relevantPanel.getComponent(i);
                                    if (tf_tmp.getText().trim().isEmpty()) {
                                        JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Input Parameter sind "
                                                + "inkorrekt, Kommando konnte nicht erstellt werden");
                                        return;
                                    }
                                    inputPerSubnode.put(nodeList.item(i), tf_tmp.getText().trim());
                                    if (nodeList.item(i).getNodeName().equalsIgnoreCase("command_text")) {
                                        description = tf_tmp.getText().trim();
                                    }
                                } else if (commandTyp.equalsIgnoreCase("GlobalCommand")) {
                                    JPanel specialPanel = (JPanel) relevantPanel.getComponent(i);
                                    JPanel help_panel = (JPanel) specialPanel.getComponent(0);
                                    JComboBox<String> cbSpecial = (JComboBox<String>) help_panel.getComponent(0);
                                    String pathToXml = pathToGlobalDir.get(cbSpecial.getSelectedItem());
                                    inputPerSubnode.put(nodeList.item(i), pathToXml);
                                } else {
                                    JPanel specialPanel = (JPanel) relevantPanel.getComponent(i);
                                    JPanel help_panel = (JPanel) specialPanel.getComponent(0);
                                    JComboBox<String> cbSpecial = (JComboBox<String>) help_panel.getComponent(0);
                                    inputPerSubnode.put(nodeList.item(i), cbSpecial.getSelectedItem().toString());
                                }
                                break;
                            case "string":
                                JTextField tf = (JTextField) relevantPanel.getComponent(i);
                                if (tf.getText().trim().isEmpty()) {
                                    JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Input Parameter sind "
                                            + "inkorrekt, Kommando konnte nicht erstellt werden");
                                    return;
                                }
                                inputPerSubnode.put(nodeList.item(i), tf.getText().trim());
                                if (nodeList.item(i).getNodeName().equalsIgnoreCase("command_text")) {
                                    description = tf.getText().trim();
                                }
                                break;
                            case "boolean":
                                JPanel booleanPanel = (JPanel) relevantPanel.getComponent(i);
                                JRadioButton rbTrue = (JRadioButton) booleanPanel.getComponent(0);
                                String tmp_text = rbTrue.isSelected() ? "true" : "false";
                                inputPerSubnode.put(nodeList.item(i), tmp_text);
                                break;
                            case "int":
                                JTextField tf_int = (JTextField) relevantPanel.getComponent(i);
                                inputPerSubnode.put(nodeList.item(i), tf_int.getText().trim());
                                break;
                            case "shell_command":
                                if (currentTypeForShellCommand.equalsIgnoreCase("file")) {
                                    inputPerSubnode.put(nodeList.item(i), selectedFile.toString());
                                } else {
                                    JTextField tf_2 = (JTextField) relevantPanel.getComponent(i);
                                    inputPerSubnode.put(nodeList.item(i), tf_2.getText().trim());
                                }
                                break;
                            case "file":
                                inputPerSubnode.put(nodeList.item(i), selectedFile.getName());
                                break;
                            case "special_3":
                                if (commandTyp.equalsIgnoreCase("ExportTableContentCommand") && !GlobalAccess.getInstance().isDatabaseReachable()) {
                                    JTextField tf_tmp = (JTextField) relevantPanel.getComponent(i);
                                    if (tf_tmp.getText().trim().isEmpty()) {
                                        JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Input Parameter sind "
                                                + "inkorrekt, Kommando konnte nicht erstellt werden");
                                        return;
                                    }
                                    inputPerSubnode.put(nodeList.item(i), tf_tmp.getText().trim());
                                    if (nodeList.item(i).getNodeName().equalsIgnoreCase("command_text")) {
                                        description = tf_tmp.getText().trim();
                                    }
                                } else {
                                    String result = "";
                                    PaFelderUI paFelderUI = (PaFelderUI) relevantPanel.getComponent(i);
                                    for (int j = 0; j < paFelderUI.getDlm_selected().getSize(); j++) {
                                        result += ((Field) paFelderUI.getDlm_selected().getElementAt(j)).toString() + ",";
                                    }
                                    result = result.substring(0, result.length() - 1);
                                    inputPerSubnode.put(nodeList.item(i), result);
                                    break;
                                }
                        }
                    }
                }
                command = new CommandRun(commandTyp, nodeList, description, parentTC.getPath());
                if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
                    dashboard.beans.Command db_com = new dashboard.beans.Command(description, LocalDate.now(), 0);
                    command.setDurchlauf_gegenstand(db_com);
                }
                command.setDisplayName(displayName);
            } else {
                removeDoubles();
                boolean automateName = false;
                if (allCreatedBons.size() > 1) {
                    ExplorerConfirmationDialog ecd = new ExplorerConfirmationDialog(GlobalAccess.getInstance().getTest_ide_main_frame(),
                            true, "<html>Es wurden mehrere Rekorder ausgewählt, soll die "
                            + "Beschreibung dieser Rekorder dynamisch<br/> auf \"Erstelle Kassenrekorder TG_TC_x\" gesetzt werden?</html>");
                    ecd.setVisible(true);
                    if (ecd.isIsOk()) {
                        automateName = true;
                    }
                }
                for (Path path_to_bon : allCreatedBons) {
                    inputPerSubnode = new HashMap<>();
                    inputPerSubnode.put(nodeList.item(0), path_to_bon.getFileName().toString());
                    //inputPerSubnode.put(nodeList.item(2), ((JTextField) paTextFields.getComponent(2)).getText());
                    if (automateName) {
                        description = "Erstelle Kassenrekorder " + path_to_bon.getFileName().toString().replace("tg", "").
                                replace("tc", "").replace(".xml", "").replace("rec_", "");
                        inputPerSubnode.put(nodeList.item(1), "Erstelle Kassenrekorder " + path_to_bon.getFileName().toString().
                                replace("tg", "").replace("tc", "").replace(".xml", "").replace("rec_", ""));
                    } else {
                        description = ((JTextField) (((JPanel) paTextFields.getComponent(0)).getComponent(1))).getText();
                        inputPerSubnode.put(nodeList.item(1), ((JTextField) (((JPanel) paTextFields.getComponent(0)).getComponent(1))).getText());
                    }
                    inputPerSubnodeList.add(inputPerSubnode);
                    CommandRun cr = new CommandRun(commandTyp, nodeList, description, parentTC.getPath());
                    if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
                        dashboard.beans.Command db_com = new dashboard.beans.Command(description, LocalDate.now(), 0);
                        cr.setDurchlauf_gegenstand(db_com);
                    }
                    multipleCommandRuns.add(cr);
                }
            }
            ok = true;
            for (String element : filesToCopy) {
                try {
                    Files.copy(Paths.get(element.split(";")[0]), Paths.get(element.split(";")[1]), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            GlobalAccess.getInstance().getPaExplorer().finishDLG();
            GlobalAccess.getInstance().getmFrameRecorder().onRestartWithoutDLG();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Input Parameter sind "
                    + "inkorrekt, Kommando konnte nicht erstellt werden");
            return;
        }
    }

    /**
     * Method which is being used to remove any kind of bon.xmls which have been
     * selected more then one time (should that have happened)
     */
    private void removeDoubles() {
        int amount = 0;
        for (Path bon_path : new LinkedList<>(allCreatedBons)) {
            for (Path help_bon_path : new LinkedList<>(allCreatedBons)) {
                if (bon_path.equals(help_bon_path)) {
                    amount++;
                }
            }
            if (amount > 1) {
                for (int i = 0; i < amount - 1; i++) {
                    allCreatedBons.remove(bon_path);
                }
            }
            amount = 0;
        }
    }

    /**
     * Event which is being called when the selected item of the main ComboBox
     * is being changed
     *
     * @param evt
     */
    private void onChangeCommandType(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onChangeCommandType
        try {
            setModelOnComboBox(cbCommandTyp.getSelectedIndex());
            loadDynamicLabels();
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_onChangeCommandType

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ExplorerAddCommandDlg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ExplorerAddCommandDlg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ExplorerAddCommandDlg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ExplorerAddCommandDlg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ExplorerAddCommandDlg dialog = new ExplorerAddCommandDlg(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    public DefaultMutableTreeNode getParentNode() {
        return parentNode;
    }

    public List<Map<Node, String>> getInputPerSubnodeList() {
        return inputPerSubnodeList;
    }

    public void setInputPerSubnodeList(List<Map<Node, String>> inputPerSubnodeList) {
        this.inputPerSubnodeList = inputPerSubnodeList;
    }

    public List<CommandRun> getMultipleCommandRuns() {
        return multipleCommandRuns;
    }

    public void setMultipleCommandRuns(List<CommandRun> multipleCommandRuns) {
        this.multipleCommandRuns = multipleCommandRuns;
    }

    public boolean isCreatedBon() {
        return createdBon;
    }

    public void setCreatedBon(boolean createdBon) {
        this.createdBon = createdBon;
    }

    public List<Path> getAllCreatedBons() {
        return allCreatedBons;
    }

    public void setAllCreatedBons(List<Path> allCreatedBons) {
        this.allCreatedBons = allCreatedBons;
    }

    private JTextField tf_special_1;
    private JTextField tf_exportTableCommandSortCriteria;
    private JComboBox cb_special_1;
    private JLabel lbSelectedFile;
    private DefaultComboBoxModel<String> tmp_dcbm_global;
    private String db_filter_table = "";
    private int db_index = 0;
    private int db_table_index = 0;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAbbrechen;
    private javax.swing.JButton btOk;
    private javax.swing.JComboBox<String> cbCommandTyp;
    private javax.swing.JLabel lbCommandTyp;
    private javax.swing.JLabel lbHeadLine;
    private javax.swing.JLabel lbPicture;
    private javax.swing.JPanel paButtons;
    private javax.swing.JPanel paColumns;
    private javax.swing.JPanel paEast;
    private javax.swing.JPanel paHeadline;
    private javax.swing.JPanel paTextFields;
    private javax.swing.JPanel paWest;
    // End of variables declaration//GEN-END:variables
}

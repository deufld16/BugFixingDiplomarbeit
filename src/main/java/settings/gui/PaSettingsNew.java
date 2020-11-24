/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package settings.gui;

import analyzer.beans.WhitelistEntry;
import analyzer.enums.ResultFileType;
import dashboard.beans.Nutzer;
import dashboard.bl.DatabaseGlobalAccess;
import dashboard.database.DB_Access_Manager;
import explorer.io.CommandsIO;
import general.bl.GlobalAccess;
import general.bl.GlobalMethods;
import general.bl.GlobalParamter;
import general.io.Loader;
import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;
import settings.bl.TableCellRenderer;
import settings.bl.TableHeaderRenderer;
import settings.bl.TableModelBackoffice;
import settings.bl.TableModelKasse;
import settings.bl.TableModelTestsysteme;
import settings.bl.TableModelWhitelist;
import settings.gui.components.SimilarityRateSettingsPanel;
import simulator.beans.Kasse;
import simulator.beans.Testsystem;

/**
 * This class is being used to set global paths and general settings
 *
 * @author Florian Deutschmann
 * @author Maximilian Strohmaier
 */
public class PaSettingsNew extends javax.swing.JPanel {

    private TableModelKasse tmk;
    private TableModelBackoffice tmb;
    private TableModelTestsysteme tmt;
    private TableModelWhitelist tmw;
    private JFrame parent;
    private int characters = 60;
    private DefaultComboBoxModel<Nutzer> dcbm = new DefaultComboBoxModel<>();

    /**
     * Creates new form PaSettingsNew
     *
     * @param parent
     */
    public PaSettingsNew(JFrame parent) {
        initComponents();
        this.parent = parent;
        jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);
        paAnalysatorSettings.setVisible(false);
        paDashboardSettings.setVisible(false);
        paExplorerSettings.setVisible(false);
        paSimulatorSettings.setVisible(false);
        paRecorderSettings.setVisible(false);
        paCommandSettings.setVisible(false);

        try {
            btAddCommand.setIcon(Loader.loadImageSettings("white_plus.png", 70, 70));
            btAddCommand.setHorizontalAlignment(SwingConstants.CENTER);
            btAddType.setIcon(Loader.loadImageSettings("white_plus.png", 70, 70));
            btAddType.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Loader.loadCashpoints();
        Loader.loadBackoffices();
        Loader.loadTestsysteme();
        tmk = new TableModelKasse(GlobalParamter.getInstance().getKassen());
        tbKassen.setModel(tmk);
        spTable.getViewport().setBackground(Color.WHITE);
        for (int i = 0; i < tbKassen.getColumnCount(); i++) {
            tbKassen.getColumnModel().getColumn(i).setHeaderRenderer(new TableHeaderRenderer());

        }
        tbKassen.setDefaultRenderer(String.class, new TableCellRenderer());
        tbKassen.setDefaultRenderer(Integer.class, new TableCellRenderer());
        tbKassen.setDefaultRenderer(Kasse.TYP.class, new TableCellRenderer());
        //tbKassen.setDefaultRenderer(Boolean.class, new TableCellRenderer());
        tbKassen.setShowHorizontalLines(true);
        tbKassen.setRowHeight(30);

        tmb = new TableModelBackoffice(GlobalParamter.getInstance().getBackoffices());
        tbBackoffice.setModel(tmb);
        spTableB.getViewport().setBackground(Color.WHITE);
        for (int i = 0; i < tbBackoffice.getColumnCount(); i++) {
            tbBackoffice.getColumnModel().getColumn(i).setHeaderRenderer(new TableHeaderRenderer());

        }
        tbBackoffice.setDefaultRenderer(String.class, new TableCellRenderer());
        tbBackoffice.setShowHorizontalLines(true);
        tbBackoffice.setRowHeight(30);

        tmt = new TableModelTestsysteme(GlobalParamter.getInstance().getTestsysteme());
        tbTestsysteme.setModel(tmt);
        spTableT.getViewport().setBackground(Color.WHITE);
        for (int i = 0; i < tbTestsysteme.getColumnCount(); i++) {
            if (i == 0) {
                tbTestsysteme.getColumnModel().getColumn(i).setMaxWidth(200);
            }
            tbTestsysteme.getColumnModel().getColumn(i).setHeaderRenderer(new TableHeaderRenderer());

        }
        tbTestsysteme.setDefaultRenderer(String.class, new TableCellRenderer());
        tbTestsysteme.setShowHorizontalLines(true);
        tbTestsysteme.setRowHeight(30);

        //Analyzer-Section-Start
        GlobalMethods gm = GlobalMethods.getInstance();
        if (gm.getDiffProperties() == null) {
            gm.loadCustomDiffSettings();
        }
        String weighting = GlobalParamter.getInstance().getErgRefWeighting();
        if (weighting.equals("ref")) {
            rbWeightRef.setSelected(true);
        } else if (weighting.equals("erg")) {
            rbWeightErg.setSelected(true);
        }
        if (GlobalParamter.getInstance().isDiffAlignmentRestriction()) {
            rbRestrictedTrue.setSelected(true);
        } else {
            rbRestrictedFalse.setSelected(true);
        }

        initSimRateSettings();

        spWhitelist.getViewport().setBackground(Color.WHITE);
        tmw = new TableModelWhitelist();
        tbWhitelist.setModel(tmw);
        for (int i = 0; i < tbWhitelist.getColumnCount(); i++) {
            tbWhitelist.getColumnModel().getColumn(i).setHeaderRenderer(new TableHeaderRenderer());
        }
        tbKassen.setDefaultRenderer(String.class, new TableCellRenderer());
        tbWhitelist.setShowHorizontalLines(true);
        tbWhitelist.setRowHeight(30);
        //Analyzer-Section-End

        updateUI();
        try {
            if (Files.exists(Paths.get(GlobalParamter.getInstance().getSettingsResPath().toString(), "parameter.conf"))) {
                setTextOfPanels(settings.io.Loader.getSpecificParameter(false));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        cbNutzer.setModel(dcbm);
//        try {
//            if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                GlobalParamter.getInstance().setAllUser(dashboard.database.DB_Access.getInstance().selectAllUsers());
//            }
//        } catch (SQLException ex) {
//            ex.printStackTrace();
//        }
        updateUserCB();
    }

    public JScrollPane getjScrollPane1() {
        return jScrollPane1;
    }

    /**
     * This method is being used to set the text of the JLables. It is used
     * whenever the text of the panels might have changed
     *
     * @param allParameters
     */
    public void setTextOfPanels(Map<String, String> allParameters) {
        lbSaveLocationRecorder.setText(applyFormat(allParameters.get("recorderPath")));
        lbDynFunk.setText(applyFormat(allParameters.get("dynamicFunc")));
        lbArtikelconfig.setText(applyFormat(allParameters.get("artConfig")));
        lbSaveProjectExplorer.setText(applyFormat(allParameters.get("projectPath")));
        lbSaveGlobalExplorer.setText(applyFormat(allParameters.get("globalPath")));
    }

    /**
     * Method which is used to format the text with linebreaks if the the text
     * which should be displayed in the JLables is too long This method even
     * supports scalability.
     *
     * @param input
     * @return
     */
    private String applyFormat(String input) {
        String output = "<html>";
        for (int i = 0; i < input.length(); i++) {
            output += input.charAt(i);
            if (i != 0 && i % characters == 0) {
                output += "<br>";
            }
        }

        output += "</html>";
        return output;
    }

    /**
     * This method is used to initialize the settings for the similarity rates
     */
    private void initSimRateSettings() {
        for (ResultFileType type : ResultFileType.values()) {
            paSimiliarityRates.add(new SimilarityRateSettingsPanel(type));
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

        bgDiffWeights = new javax.swing.ButtonGroup();
        bgDiffRestriction = new javax.swing.ButtonGroup();
        jScrollPane1 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        lbDashboard = new javax.swing.JLabel();
        paDashboardSettings = new javax.swing.JPanel();
        paUsers = new javax.swing.JPanel();
        lbUser = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        btChangeUser = new javax.swing.JButton();
        btAddNewUser = new javax.swing.JButton();
        cbNutzer = new javax.swing.JComboBox<>();
        lbAnalysator = new javax.swing.JLabel();
        paAnalysatorSettings = new javax.swing.JPanel();
        paDiffAdjustments = new javax.swing.JPanel();
        paWhitelistMain = new javax.swing.JPanel();
        paWhitelistActions = new javax.swing.JPanel();
        lbWhitelist = new javax.swing.JLabel();
        paWhitelistEntryActions = new javax.swing.JPanel();
        btRemove = new javax.swing.JButton();
        btAdd = new javax.swing.JButton();
        spWhitelist = new javax.swing.JScrollPane();
        tbWhitelist = new javax.swing.JTable();
        paOtherAdjustments = new javax.swing.JPanel();
        paWeights = new javax.swing.JPanel();
        paWeightsInner = new javax.swing.JPanel();
        paWeightsInfo = new javax.swing.JPanel();
        lbWeightsTitle = new javax.swing.JLabel();
        lbWeightsDescription = new javax.swing.JLabel();
        rbWeightRef = new javax.swing.JRadioButton();
        rbWeightErg = new javax.swing.JRadioButton();
        paRestriction = new javax.swing.JPanel();
        paRestrictionInner = new javax.swing.JPanel();
        paRestrictionInfo = new javax.swing.JPanel();
        lbRestrictionTitle = new javax.swing.JLabel();
        lbRestrictionDescription = new javax.swing.JLabel();
        rbRestrictedFalse = new javax.swing.JRadioButton();
        rbRestrictedTrue = new javax.swing.JRadioButton();
        paSimiliarityRates = new javax.swing.JPanel();
        lbExplorer = new javax.swing.JLabel();
        paExplorerSettings = new javax.swing.JPanel();
        lbProject = new javax.swing.JLabel();
        lbSaveProjectExplorer = new javax.swing.JLabel();
        btSaveProjectExplorer = new javax.swing.JButton();
        lbGlobal = new javax.swing.JLabel();
        lbSaveGlobalExplorer = new javax.swing.JLabel();
        btSaveGlobalExplorer = new javax.swing.JButton();
        lbRecorder = new javax.swing.JLabel();
        paRecorderSettings = new javax.swing.JPanel();
        lbRec = new javax.swing.JLabel();
        lbSaveLocationRecorder = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        lbDynamic = new javax.swing.JLabel();
        lbDynFunk = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        lbArticle = new javax.swing.JLabel();
        lbArtikelconfig = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        lbSimulator = new javax.swing.JLabel();
        paSimulatorSettings = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        btRemoveCashpoints = new javax.swing.JButton();
        btAddCashpoint = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        spTable = new javax.swing.JScrollPane();
        tbKassen = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        btRemoveBackoffice = new javax.swing.JButton();
        btAddBackoffice = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        spTableB = new javax.swing.JScrollPane();
        tbBackoffice = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        btRemoveTestsystem = new javax.swing.JButton();
        btAddTestsystem = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        spTableT = new javax.swing.JScrollPane();
        tbTestsysteme = new javax.swing.JTable();
        lbCommands = new javax.swing.JLabel();
        paCommandSettings = new javax.swing.JPanel();
        btAddCommand = new javax.swing.JButton();
        btAddType = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 0));
        setPreferredSize(new java.awt.Dimension(764, 1600));
        addHierarchyBoundsListener(new java.awt.event.HierarchyBoundsListener() {
            public void ancestorMoved(java.awt.event.HierarchyEvent evt) {
            }
            public void ancestorResized(java.awt.event.HierarchyEvent evt) {
                onAncestorResized(evt);
            }
        });
        setLayout(new java.awt.GridLayout(1, 0));

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(null);
        jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(764, 1000));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        lbDashboard.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbDashboard.setText("Dashboard ▼");
        lbDashboard.setName("dashboardSettings"); // NOI18N
        lbDashboard.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onExpand(evt);
            }
        });

        paDashboardSettings.setBackground(new java.awt.Color(255, 255, 255));
        paDashboardSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 35, 0, 0));
        paDashboardSettings.setPreferredSize(new java.awt.Dimension(823, 50));
        paDashboardSettings.setLayout(new java.awt.GridLayout(1, 1));

        paUsers.setBackground(new java.awt.Color(255, 255, 255));
        paUsers.setLayout(new java.awt.BorderLayout(8, 0));

        lbUser.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbUser.setText("Benutzer:");
        paUsers.add(lbUser, java.awt.BorderLayout.WEST);

        jPanel13.setBackground(new java.awt.Color(255, 255, 255));
        jPanel13.setLayout(new java.awt.GridLayout(1, 2, 10, 0));

        btChangeUser.setBackground(new java.awt.Color(0, 0, 0));
        btChangeUser.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btChangeUser.setForeground(new java.awt.Color(255, 255, 255));
        btChangeUser.setText("Benutzer wechseln");
        btChangeUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeUser(evt);
            }
        });
        jPanel13.add(btChangeUser);

        btAddNewUser.setBackground(new java.awt.Color(0, 0, 0));
        btAddNewUser.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btAddNewUser.setForeground(new java.awt.Color(255, 255, 255));
        btAddNewUser.setText("Benutzer Hinzufügen");
        btAddNewUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onAddUser(evt);
            }
        });
        jPanel13.add(btAddNewUser);

        paUsers.add(jPanel13, java.awt.BorderLayout.EAST);

        cbNutzer.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        paUsers.add(cbNutzer, java.awt.BorderLayout.CENTER);

        paDashboardSettings.add(paUsers);

        lbAnalysator.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbAnalysator.setText("Analysator ▼");
        lbAnalysator.setToolTipText("");
        lbAnalysator.setName("analyzerSettings"); // NOI18N
        lbAnalysator.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onExpand(evt);
            }
        });

        paAnalysatorSettings.setBackground(new java.awt.Color(255, 255, 255));
        paAnalysatorSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 35, 0, 0));
        paAnalysatorSettings.setLayout(new java.awt.GridLayout(2, 1, 0, 10));

        paDiffAdjustments.setBackground(new java.awt.Color(255, 255, 255));
        paDiffAdjustments.setLayout(new java.awt.GridLayout(2, 1, 0, 20));

        paWhitelistMain.setBackground(new java.awt.Color(255, 255, 255));
        paWhitelistMain.setLayout(new java.awt.BorderLayout(0, 5));

        paWhitelistActions.setBackground(new java.awt.Color(255, 255, 255));
        paWhitelistActions.setLayout(new java.awt.BorderLayout());

        lbWhitelist.setBackground(new java.awt.Color(255, 255, 255));
        lbWhitelist.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbWhitelist.setText("Whitelist:");
        lbWhitelist.setOpaque(true);
        paWhitelistActions.add(lbWhitelist, java.awt.BorderLayout.CENTER);

        paWhitelistEntryActions.setBackground(new java.awt.Color(255, 255, 255));
        paWhitelistEntryActions.setLayout(new java.awt.GridLayout(1, 2));

        btRemove.setBackground(new java.awt.Color(0, 0, 0));
        btRemove.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btRemove.setForeground(new java.awt.Color(255, 255, 255));
        btRemove.setText("Entfernen");
        btRemove.setPreferredSize(new java.awt.Dimension(170, 60));
        btRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onRemoveWhitelistEntries(evt);
            }
        });
        paWhitelistEntryActions.add(btRemove);

        btAdd.setBackground(new java.awt.Color(0, 0, 0));
        btAdd.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btAdd.setForeground(new java.awt.Color(255, 255, 255));
        btAdd.setText("Hinzufügen");
        btAdd.setPreferredSize(new java.awt.Dimension(170, 60));
        btAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onAddWhitelistEntry(evt);
            }
        });
        paWhitelistEntryActions.add(btAdd);

        paWhitelistActions.add(paWhitelistEntryActions, java.awt.BorderLayout.EAST);

        paWhitelistMain.add(paWhitelistActions, java.awt.BorderLayout.NORTH);

        spWhitelist.setBackground(new java.awt.Color(255, 255, 255));
        spWhitelist.setPreferredSize(new java.awt.Dimension(452, 202));

        tbWhitelist.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tbWhitelist.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbWhitelist.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onWhitelistEntryClick(evt);
            }
        });
        spWhitelist.setViewportView(tbWhitelist);

        paWhitelistMain.add(spWhitelist, java.awt.BorderLayout.CENTER);

        paDiffAdjustments.add(paWhitelistMain);

        paOtherAdjustments.setBackground(new java.awt.Color(255, 255, 255));
        paOtherAdjustments.setLayout(new java.awt.GridLayout(1, 2, 10, 0));

        paWeights.setBackground(new java.awt.Color(255, 255, 255));
        paWeights.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        paWeights.setLayout(new java.awt.BorderLayout());

        paWeightsInner.setBackground(new java.awt.Color(255, 255, 255));
        paWeightsInner.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10));
        paWeightsInner.setLayout(new java.awt.GridLayout(3, 1));

        paWeightsInfo.setBackground(new java.awt.Color(255, 255, 255));
        paWeightsInfo.setLayout(new java.awt.GridLayout(2, 1));

        lbWeightsTitle.setBackground(new java.awt.Color(255, 255, 255));
        lbWeightsTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbWeightsTitle.setText("<html><u>Verschiebegewichtung:</u></html>");
        paWeightsInfo.add(lbWeightsTitle);

        lbWeightsDescription.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbWeightsDescription.setText("<html>Welche Zeilen sollen zuerst auf weitere <br/>Vorkommen im Gegenstück geprüft werden?</html>");
        paWeightsInfo.add(lbWeightsDescription);

        paWeightsInner.add(paWeightsInfo);

        rbWeightRef.setBackground(new java.awt.Color(255, 255, 255));
        bgDiffWeights.add(rbWeightRef);
        rbWeightRef.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        rbWeightRef.setSelected(true);
        rbWeightRef.setText("ref");
        rbWeightRef.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeRefErgWeight(evt);
            }
        });
        paWeightsInner.add(rbWeightRef);

        rbWeightErg.setBackground(new java.awt.Color(255, 255, 255));
        bgDiffWeights.add(rbWeightErg);
        rbWeightErg.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        rbWeightErg.setText("erg");
        rbWeightErg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeRefErgWeight(evt);
            }
        });
        paWeightsInner.add(rbWeightErg);

        paWeights.add(paWeightsInner, java.awt.BorderLayout.CENTER);

        paOtherAdjustments.add(paWeights);

        paRestriction.setBackground(new java.awt.Color(255, 255, 255));
        paRestriction.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        paRestriction.setLayout(new java.awt.BorderLayout());

        paRestrictionInner.setBackground(new java.awt.Color(255, 255, 255));
        paRestrictionInner.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10));
        paRestrictionInner.setLayout(new java.awt.GridLayout(3, 0));

        paRestrictionInfo.setBackground(new java.awt.Color(255, 255, 255));
        paRestrictionInfo.setLayout(new java.awt.GridLayout(2, 1));

        lbRestrictionTitle.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbRestrictionTitle.setText("<html><u>Verschiebebeschränkung:</u></html>");
        paRestrictionInfo.add(lbRestrictionTitle);

        lbRestrictionDescription.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbRestrictionDescription.setText("<html>Darf die Verschiebung einer Zeile eine korrekte <br/>Gegenüberstellung weiterer Zeilen auflösen?</html>");
        paRestrictionInfo.add(lbRestrictionDescription);

        paRestrictionInner.add(paRestrictionInfo);

        rbRestrictedFalse.setBackground(new java.awt.Color(255, 255, 255));
        bgDiffRestriction.add(rbRestrictedFalse);
        rbRestrictedFalse.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        rbRestrictedFalse.setText("Ja");
        rbRestrictedFalse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeDiffAlignmentRestriction(evt);
            }
        });
        paRestrictionInner.add(rbRestrictedFalse);

        rbRestrictedTrue.setBackground(new java.awt.Color(255, 255, 255));
        bgDiffRestriction.add(rbRestrictedTrue);
        rbRestrictedTrue.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        rbRestrictedTrue.setSelected(true);
        rbRestrictedTrue.setText("Nein");
        rbRestrictedTrue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeDiffAlignmentRestriction(evt);
            }
        });
        paRestrictionInner.add(rbRestrictedTrue);

        paRestriction.add(paRestrictionInner, java.awt.BorderLayout.CENTER);

        paOtherAdjustments.add(paRestriction);

        paDiffAdjustments.add(paOtherAdjustments);

        paAnalysatorSettings.add(paDiffAdjustments);

        paSimiliarityRates.setBackground(new java.awt.Color(255, 255, 255));
        paSimiliarityRates.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ähnlichkeitsraten:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 18))); // NOI18N
        paSimiliarityRates.setLayout(new java.awt.GridLayout(4, 0));
        paAnalysatorSettings.add(paSimiliarityRates);

        lbExplorer.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbExplorer.setText("Explorer ▼");
        lbExplorer.setName("explorerSettings"); // NOI18N
        lbExplorer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onExpand(evt);
            }
        });

        paExplorerSettings.setBackground(new java.awt.Color(255, 255, 255));
        paExplorerSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 35, 0, 0));
        paExplorerSettings.setMinimumSize(new java.awt.Dimension(782, 93));
        paExplorerSettings.setPreferredSize(new java.awt.Dimension(2, 210));
        paExplorerSettings.setRequestFocusEnabled(false);
        paExplorerSettings.setLayout(new java.awt.GridLayout(2, 3));

        lbProject.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lbProject.setText("Speicherort der Projekte:");
        lbProject.setName("Speicherort der Projekte:"); // NOI18N
        paExplorerSettings.add(lbProject);

        lbSaveProjectExplorer.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        lbSaveProjectExplorer.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 1, 10, 1));
        paExplorerSettings.add(lbSaveProjectExplorer);

        btSaveProjectExplorer.setBackground(new java.awt.Color(0, 0, 0));
        btSaveProjectExplorer.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btSaveProjectExplorer.setForeground(new java.awt.Color(255, 255, 255));
        btSaveProjectExplorer.setText("Ändern");
        btSaveProjectExplorer.setName("projectPath"); // NOI18N
        btSaveProjectExplorer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeValueExplorer(evt);
            }
        });
        paExplorerSettings.add(btSaveProjectExplorer);

        lbGlobal.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lbGlobal.setText("<html>Speicherort der<br/>globalen Commands:</html>");
        lbGlobal.setName("Speicherort der globalen Commands:"); // NOI18N
        paExplorerSettings.add(lbGlobal);

        lbSaveGlobalExplorer.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        lbSaveGlobalExplorer.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 1, 10, 1));
        paExplorerSettings.add(lbSaveGlobalExplorer);

        btSaveGlobalExplorer.setBackground(new java.awt.Color(0, 0, 0));
        btSaveGlobalExplorer.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btSaveGlobalExplorer.setForeground(new java.awt.Color(255, 255, 255));
        btSaveGlobalExplorer.setText("Ändern");
        btSaveGlobalExplorer.setName("globalPath"); // NOI18N
        btSaveGlobalExplorer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeValueExplorer(evt);
            }
        });
        paExplorerSettings.add(btSaveGlobalExplorer);

        lbRecorder.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbRecorder.setText("Recorder ▼");
        lbRecorder.setName("recorderSettings"); // NOI18N
        lbRecorder.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onExpand(evt);
            }
        });

        paRecorderSettings.setBackground(new java.awt.Color(255, 255, 255));
        paRecorderSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 35, 0, 0));
        paRecorderSettings.setPreferredSize(new java.awt.Dimension(2, 210));
        paRecorderSettings.setLayout(new java.awt.GridLayout(3, 3));

        lbRec.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lbRec.setText("Speicherort für Rekorder:");
        lbRec.setName("Speicherort für Rekorder:"); // NOI18N
        paRecorderSettings.add(lbRec);

        lbSaveLocationRecorder.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        paRecorderSettings.add(lbSaveLocationRecorder);

        jButton1.setBackground(new java.awt.Color(0, 0, 0));
        jButton1.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Ändern");
        jButton1.setName("recorderSavePath"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeValueRecorder(evt);
            }
        });
        paRecorderSettings.add(jButton1);

        lbDynamic.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lbDynamic.setText("Dynamische Funktionsbuttons:");
        lbDynamic.setToolTipText("");
        lbDynamic.setName("Dynamische Funktionsbuttons:"); // NOI18N
        paRecorderSettings.add(lbDynamic);

        lbDynFunk.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        lbDynFunk.setToolTipText("");
        paRecorderSettings.add(lbDynFunk);

        jButton2.setBackground(new java.awt.Color(0, 0, 0));
        jButton2.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Ändern");
        jButton2.setName("dynFunk"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeValueRecorder(evt);
            }
        });
        paRecorderSettings.add(jButton2);

        lbArticle.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lbArticle.setText("Artikelkonfigurationsfile:");
        lbArticle.setName("Artikelkonfigurationsfile:"); // NOI18N
        paRecorderSettings.add(lbArticle);

        lbArtikelconfig.setFont(new java.awt.Font("Tahoma", 0, 19)); // NOI18N
        paRecorderSettings.add(lbArtikelconfig);

        jButton3.setBackground(new java.awt.Color(0, 0, 0));
        jButton3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Ändern");
        jButton3.setName("artikelconfig"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeValueRecorder(evt);
            }
        });
        paRecorderSettings.add(jButton3);

        lbSimulator.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbSimulator.setText("Simulator ▼");
        lbSimulator.setName("simulatorSettings"); // NOI18N
        lbSimulator.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onExpand(evt);
            }
        });

        paSimulatorSettings.setBackground(new java.awt.Color(255, 255, 255));
        paSimulatorSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 35, 0, 0));
        paSimulatorSettings.setMinimumSize(new java.awt.Dimension(782, 93));
        paSimulatorSettings.setPreferredSize(new java.awt.Dimension(2, 400));
        paSimulatorSettings.setLayout(new java.awt.GridLayout(1, 1));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setPreferredSize(new java.awt.Dimension(747, 1000));
        jPanel2.setLayout(new java.awt.GridLayout(2, 1, 10, 10));

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setPreferredSize(new java.awt.Dimension(747, 400));
        jPanel9.setLayout(new java.awt.GridLayout(1, 0, 10, 10));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setMinimumSize(new java.awt.Dimension(383, 300));
        jPanel5.setPreferredSize(new java.awt.Dimension(452, 400));
        jPanel5.setLayout(new java.awt.BorderLayout(10, 10));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(383, 50));
        jPanel3.setLayout(new java.awt.GridLayout(1, 3, 10, 10));

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel6.setText("Kassa:");
        jPanel3.add(jLabel6);

        btRemoveCashpoints.setBackground(new java.awt.Color(0, 0, 0));
        btRemoveCashpoints.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btRemoveCashpoints.setForeground(new java.awt.Color(255, 255, 255));
        btRemoveCashpoints.setText("Entfernen");
        btRemoveCashpoints.setName("recorderSavePath"); // NOI18N
        btRemoveCashpoints.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onKassaRemove(evt);
            }
        });
        jPanel3.add(btRemoveCashpoints);

        btAddCashpoint.setBackground(new java.awt.Color(0, 0, 0));
        btAddCashpoint.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btAddCashpoint.setForeground(new java.awt.Color(255, 255, 255));
        btAddCashpoint.setText("Hinzufügen");
        btAddCashpoint.setName("recorderSavePath"); // NOI18N
        btAddCashpoint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                omKassaAdd(evt);
            }
        });
        jPanel3.add(btAddCashpoint);

        jPanel5.add(jPanel3, java.awt.BorderLayout.NORTH);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setLayout(new java.awt.GridLayout(1, 3));

        spTable.setBackground(new java.awt.Color(255, 255, 255));
        spTable.setPreferredSize(new java.awt.Dimension(452, 400));

        tbKassen.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 50, 0, 50));
        tbKassen.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbKassen.setPreferredSize(new java.awt.Dimension(150, 450));
        spTable.setViewportView(tbKassen);

        jPanel4.add(spTable);

        jPanel5.add(jPanel4, java.awt.BorderLayout.CENTER);

        jPanel9.add(jPanel5);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setMinimumSize(new java.awt.Dimension(383, 300));
        jPanel6.setLayout(new java.awt.BorderLayout(10, 10));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));
        jPanel7.setPreferredSize(new java.awt.Dimension(383, 50));
        jPanel7.setLayout(new java.awt.GridLayout(1, 3, 10, 10));

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel7.setText("Backoffice:");
        jPanel7.add(jLabel7);

        btRemoveBackoffice.setBackground(new java.awt.Color(0, 0, 0));
        btRemoveBackoffice.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btRemoveBackoffice.setForeground(new java.awt.Color(255, 255, 255));
        btRemoveBackoffice.setText("Entfernen");
        btRemoveBackoffice.setName("recorderSavePath"); // NOI18N
        btRemoveBackoffice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onRemoveCashpoint(evt);
            }
        });
        jPanel7.add(btRemoveBackoffice);

        btAddBackoffice.setBackground(new java.awt.Color(0, 0, 0));
        btAddBackoffice.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btAddBackoffice.setForeground(new java.awt.Color(255, 255, 255));
        btAddBackoffice.setText("Hinzufügen");
        btAddBackoffice.setName("recorderSavePath"); // NOI18N
        btAddBackoffice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onAddBackoffice(evt);
            }
        });
        jPanel7.add(btAddBackoffice);

        jPanel6.add(jPanel7, java.awt.BorderLayout.NORTH);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));
        jPanel8.setLayout(new java.awt.GridLayout(1, 3));

        spTableB.setBackground(new java.awt.Color(255, 255, 255));
        spTableB.setPreferredSize(new java.awt.Dimension(452, 400));

        tbBackoffice.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 50, 0, 50));
        tbBackoffice.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbBackoffice.setPreferredSize(new java.awt.Dimension(150, 450));
        tbBackoffice.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        spTableB.setViewportView(tbBackoffice);

        jPanel8.add(spTableB);

        jPanel6.add(jPanel8, java.awt.BorderLayout.CENTER);

        jPanel9.add(jPanel6);

        jPanel2.add(jPanel9);

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));
        jPanel10.setMinimumSize(new java.awt.Dimension(383, 300));
        jPanel10.setPreferredSize(new java.awt.Dimension(452, 400));
        jPanel10.setLayout(new java.awt.BorderLayout(10, 10));

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));
        jPanel11.setPreferredSize(new java.awt.Dimension(383, 50));
        jPanel11.setLayout(new java.awt.GridLayout(1, 3, 10, 10));

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel8.setText("Testsysteme:");
        jPanel11.add(jLabel8);

        btRemoveTestsystem.setBackground(new java.awt.Color(0, 0, 0));
        btRemoveTestsystem.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btRemoveTestsystem.setForeground(new java.awt.Color(255, 255, 255));
        btRemoveTestsystem.setText("Entfernen");
        btRemoveTestsystem.setName("recorderSavePath"); // NOI18N
        btRemoveTestsystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onRemoveTestsystem(evt);
            }
        });
        jPanel11.add(btRemoveTestsystem);

        btAddTestsystem.setBackground(new java.awt.Color(0, 0, 0));
        btAddTestsystem.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btAddTestsystem.setForeground(new java.awt.Color(255, 255, 255));
        btAddTestsystem.setText("Hinzufügen");
        btAddTestsystem.setName("recorderSavePath"); // NOI18N
        btAddTestsystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onAddTestsystem(evt);
            }
        });
        jPanel11.add(btAddTestsystem);

        jPanel10.add(jPanel11, java.awt.BorderLayout.NORTH);

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.setLayout(new java.awt.GridLayout(1, 3));

        spTableT.setBackground(new java.awt.Color(255, 255, 255));
        spTableT.setPreferredSize(new java.awt.Dimension(452, 400));

        tbTestsysteme.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 50, 0, 50));
        tbTestsysteme.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tbTestsysteme.setPreferredSize(new java.awt.Dimension(150, 450));
        spTableT.setViewportView(tbTestsysteme);

        jPanel12.add(spTableT);

        jPanel10.add(jPanel12, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel10);

        paSimulatorSettings.add(jPanel2);

        lbCommands.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbCommands.setText("Commands ▼");
        lbCommands.setName("commandSettings"); // NOI18N
        lbCommands.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onExpand(evt);
            }
        });

        paCommandSettings.setBackground(new java.awt.Color(255, 255, 255));
        paCommandSettings.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 35, 0, 0));
        paCommandSettings.setMinimumSize(new java.awt.Dimension(782, 93));
        paCommandSettings.setPreferredSize(new java.awt.Dimension(2, 100));
        paCommandSettings.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onExpand(evt);
            }
        });
        paCommandSettings.setLayout(new java.awt.GridLayout(3, 5));

        btAddCommand.setBackground(new java.awt.Color(0, 0, 0));
        btAddCommand.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btAddCommand.setForeground(new java.awt.Color(255, 255, 255));
        btAddCommand.setText("<html>Neuen Command<br/>definieren</html>");
        btAddCommand.setMaximumSize(new java.awt.Dimension(89, 31));
        btAddCommand.setMinimumSize(new java.awt.Dimension(89, 31));
        btAddCommand.setPreferredSize(new java.awt.Dimension(89, 31));
        btAddCommand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDefineNewCommand(evt);
            }
        });
        paCommandSettings.add(btAddCommand);

        btAddType.setBackground(new java.awt.Color(0, 0, 0));
        btAddType.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btAddType.setForeground(new java.awt.Color(255, 255, 255));
        btAddType.setText("<html>Neuen Command-<br/>typen definieren");
        btAddType.setMaximumSize(new java.awt.Dimension(89, 31));
        btAddType.setMinimumSize(new java.awt.Dimension(89, 31));
        btAddType.setPreferredSize(new java.awt.Dimension(89, 31));
        btAddType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDefineNewType(evt);
            }
        });
        paCommandSettings.add(btAddType);
        paCommandSettings.add(jLabel11);
        paCommandSettings.add(jLabel12);
        paCommandSettings.add(jLabel13);
        paCommandSettings.add(jLabel1);
        paCommandSettings.add(jLabel14);
        paCommandSettings.add(jLabel2);
        paCommandSettings.add(jLabel3);
        paCommandSettings.add(jLabel4);
        paCommandSettings.add(jLabel5);
        paCommandSettings.add(jLabel9);
        paCommandSettings.add(jLabel10);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(paDashboardSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbAnalysator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(paAnalysatorSettings, javax.swing.GroupLayout.DEFAULT_SIZE, 1025, Short.MAX_VALUE)
                    .addComponent(lbExplorer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(paExplorerSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbRecorder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(paRecorderSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbSimulator, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(paSimulatorSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbCommands, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(paCommandSettings, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbDashboard, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paDashboardSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbAnalysator, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paAnalysatorSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbExplorer, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paExplorerSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbRecorder, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paRecorderSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbSimulator, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paSimulatorSettings, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbCommands, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paCommandSettings, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(392, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(jPanel1);

        add(jScrollPane1);
    }// </editor-fold>//GEN-END:initComponents
    /**
     * This method is used to expand/collapse the diffrent setting-panels for
     * the diffrent subtools
     *
     * @param evt
     */
    private void onExpand(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onExpand
        String down_or_up = "";
        switch (evt.getComponent().getName()) {
            case "dashboardSettings":
                down_or_up = paDashboardSettings.isVisible() ? "▼" : "▲";
                lbDashboard.setText(lbDashboard.getText().substring(0, lbDashboard.getText().length() - 1) + down_or_up);
                paDashboardSettings.setVisible(!paDashboardSettings.isVisible());
                break;
            case "explorerSettings":
                down_or_up = paExplorerSettings.isVisible() ? "▼" : "▲";
                lbExplorer.setText(lbExplorer.getText().substring(0, lbExplorer.getText().length() - 1) + down_or_up);
                paExplorerSettings.setVisible(!paExplorerSettings.isVisible());
                break;
            case "analyzerSettings":
                down_or_up = paAnalysatorSettings.isVisible() ? "▼" : "▲";
                lbAnalysator.setText(lbAnalysator.getText().substring(0, lbAnalysator.getText().length() - 1) + down_or_up);
                paAnalysatorSettings.setVisible(!paAnalysatorSettings.isVisible());
                break;
            case "simulatorSettings":
                down_or_up = paSimulatorSettings.isVisible() ? "▼" : "▲";
                lbSimulator.setText(lbSimulator.getText().substring(0, lbSimulator.getText().length() - 1) + down_or_up);
                paSimulatorSettings.setVisible(!paSimulatorSettings.isVisible());
                break;
            case "recorderSettings":
                down_or_up = paRecorderSettings.isVisible() ? "▼" : "▲";
                lbRecorder.setText(lbRecorder.getText().substring(0, lbRecorder.getText().length() - 1) + down_or_up);
                paRecorderSettings.setVisible(!paRecorderSettings.isVisible());
                break;
            case "commandSettings":
                down_or_up = paCommandSettings.isVisible() ? "▼" : "▲";
                lbCommands.setText(lbCommands.getText().substring(0, lbCommands.getText().length() - 1) + down_or_up);
                paCommandSettings.setVisible(!paCommandSettings.isVisible());
                break;
        }
    }//GEN-LAST:event_onExpand
    /**
     * Method which is used to display diffrent JFileChoosers for the settings
     * for the recorder This method is also used to write the newly configured
     * settings into the config file in the right place
     *
     * @param evt
     */
    private void onChangeValueRecorder(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onChangeValueRecorder
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = null;
        try {
            switch (((Component) evt.getSource()).getName()) {
                case "recorderSavePath":
                    fc.setCurrentDirectory(GlobalParamter.getInstance().getGeneralDevelopPath().toFile());
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        settings.io.Loader.changeParameters(fc.getSelectedFile().toPath(), "recorderPath");
                    }
                    break;
                case "dynFunk":
                    fc.setCurrentDirectory(GlobalParamter.getInstance().getGeneralResPath().toFile());
                    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    filter = new FileNameExtensionFilter("Config-Files", "conf");
                    fc.setFileFilter(filter);
                    if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        settings.io.Loader.changeParameters(fc.getSelectedFile().toPath(), "dynamicFunctions");
                    }
                    break;
                case "artikelconfig":
                    fc.setCurrentDirectory(GlobalParamter.getInstance().getGeneralResPath().toFile());
                    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    filter = new FileNameExtensionFilter("Config-Files", "conf");
                    fc.setFileFilter(filter);
                    if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        settings.io.Loader.changeParameters(fc.getSelectedFile().toPath(), "articleConfig");
                    }
                    break;
            }
            setTextOfPanels(settings.io.Loader.getSpecificParameter(true));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_onChangeValueRecorder
    /**
     * Method which is used to display diffrent JFileChoosers for the settings
     * for the explorer This method is also used to write the newly configured
     * settings into the config file in the right place
     *
     * @param evt
     */
    private void onChangeValueExplorer(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onChangeValueExplorer
        JFileChooser fc = new JFileChooser();
        FileNameExtensionFilter filter = null;
        try {
            switch (((Component) evt.getSource()).getName()) {
                case "projectPath":
                    fc.setCurrentDirectory(GlobalParamter.getInstance().getGeneralDevelopPath().toFile());
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        settings.io.Loader.changeParameters(fc.getSelectedFile().toPath(), "projectPath");
                    }
                    break;
                case "globalPath":
                    fc.setCurrentDirectory(GlobalParamter.getInstance().getGeneralDevelopPath().toFile());
                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        settings.io.Loader.changeParameters(fc.getSelectedFile().toPath(), "globalPath");
                    }
                    break;
            }
            setTextOfPanels(settings.io.Loader.getSpecificParameter(true));
            GlobalAccess.getInstance().getPaExplorer().updateTree();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_onChangeValueExplorer
    /**
     * Method to add a cash point
     *
     * @param evt
     */
    private void omKassaAdd(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_omKassaAdd
        AddCashpointDlg dlg = new AddCashpointDlg(parent, true);
        dlg.setVisible(true);
        if (dlg.isOkay()) {
            tmk.addElement(dlg.getResult());
        }
    }//GEN-LAST:event_omKassaAdd
    /**
     * Method to remove a cashpoint
     *
     * @param evt
     */
    private void onKassaRemove(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onKassaRemove
        try {
            tmk.removeElement(tbKassen.getSelectedRow());
            tbKassen.clearSelection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_onKassaRemove
    /**
     * Method to add a new backoffice
     *
     * @param evt
     */
    private void onAddBackoffice(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onAddBackoffice
        AddBackofficeDlg dlg = new AddBackofficeDlg(parent, true);
        dlg.setVisible(true);
        if (dlg.isOkay()) {
            tmb.addElement(dlg.getResult());
        }
    }//GEN-LAST:event_onAddBackoffice
    /**
     * Method to remove a backoffice
     *
     * @param evt
     */
    private void onRemoveCashpoint(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onRemoveCashpoint
        try {
            tmb.removeElement(tbBackoffice.getSelectedRow());
            tbBackoffice.clearSelection();
        } catch (Exception e) {
        }
    }//GEN-LAST:event_onRemoveCashpoint

    /**
     * Method to change the scaling of the panel whenever it is resized
     *
     * @param evt
     */
    private void onAncestorResized(java.awt.event.HierarchyEvent evt) {//GEN-FIRST:event_onAncestorResized
        try {
            if (getParent().getSize().width <= 1100) {
                makeBrForLabel(lbProject, "der", "");
                makeBrForLabel(lbGlobal, "", "");
                makeBrForLabel(lbRec, "für", "");
                makeBrForLabel(lbDynamic, "Funk", "-");
                makeBrForLabel(lbArticle, "konfi", "-");
                displayListTwoFiles(lbSaveProjectExplorer);
                displayListTwoFiles(lbSaveGlobalExplorer);
                displayListTwoFiles(lbSaveLocationRecorder);
                displayListTwoFiles(lbDynFunk);
                displayListTwoFiles(lbArtikelconfig);
                characters = 25;
            } else if (getParent().getSize().width <= 1350) {
                characters = 35;
                setTextOfPanels(settings.io.Loader.getSpecificParameter(true));
                setNormalText(lbProject);
                makeBrForLabel(lbGlobal, "", "");
                setNormalText(lbRec);
                setNormalText(lbDynamic);
                setNormalText(lbArticle);
                displayListTwoFiles(lbSaveProjectExplorer);
                displayListTwoFiles(lbSaveGlobalExplorer);
                displayListTwoFiles(lbSaveLocationRecorder);
                displayListTwoFiles(lbDynFunk);
                displayListTwoFiles(lbArtikelconfig);
            } else if (getParent().getSize().width <= 1800) {
                onFullScreenPanel();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_onAncestorResized

    /**
     * Method to remove a test system
     *
     * @param evt
     */
    private void onRemoveTestsystem(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onRemoveTestsystem
        try {
            tmt.removeElement(tbTestsysteme.getSelectedRow());
            tbTestsysteme.clearSelection();
        } catch (Exception e) {
        }
    }//GEN-LAST:event_onRemoveTestsystem

    /**
     * Method to add a new test system
     *
     * @param evt
     */
    private void onAddTestsystem(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onAddTestsystem
        if (tbKassen.getSelectedRowCount() > 0 && tbBackoffice.getSelectedRowCount() > 0) {
            List<Kasse> kassen = new ArrayList<>();
            for (int selectedRow : tbKassen.getSelectedRows()) {
                kassen.add(GlobalParamter.getInstance().getKassen().get(selectedRow));
            }
            tmt.addElement(new Testsystem(kassen, GlobalParamter.getInstance().getBackoffices().get(tbBackoffice.getSelectedRow()), false));
        } else {

            Object[] options = {"OK"};
            int n = JOptionPane.showOptionDialog(this,
                    "Es wurden nicht alle erforderlichen Komponenten ausgewählt!\nBitte wählen Sie ein Backoffice und mindestens eine Kasse aus.", "Fehler",
                    JOptionPane.PLAIN_MESSAGE,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    options,
                    options[0]);
        }
    }//GEN-LAST:event_onAddTestsystem

    /**
     * Method to define a new command
     *
     * @param evt
     */
    private void onDefineNewCommand(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onDefineNewCommand
        AddNewCommandDlg addNewComm = new AddNewCommandDlg(null, true);
        addNewComm.setVisible(true);
        if (addNewComm.isOk()) {
            try {
                String classname = addNewComm.getClassName();
                String displayName = addNewComm.getDisplayName();
                CommandsIO.addNewCommand(classname, displayName, addNewComm.getSubnodes());
            } catch (ParserConfigurationException | SAXException | IOException | TransformerException | XPathExpressionException ex) {
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_onDefineNewCommand

    /**
     * Method to define a new command type
     *
     * @param evt
     */
    private void onDefineNewType(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onDefineNewType
        try {
            String newTyp = JOptionPane.showInputDialog(null, "Geben Sie den Namen des neuen Typs (für die Eingabe eines Commands) ein: ", "Neuer Commandtyp", JOptionPane.PLAIN_MESSAGE);
            if (newTyp != null) {
                if (newTyp.trim() != null && !newTyp.trim().isEmpty()) {
                    CommandsIO.addToTypes(newTyp);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_onDefineNewType

    /**
     * Method that is called when the user clicks on the button to remove a
     * whitelist entry
     *
     * @param evt
     */
    private void onRemoveWhitelistEntries(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onRemoveWhitelistEntries
        List<WhitelistEntry> entries = new LinkedList<>();
        for (int selectedRow : tbWhitelist.getSelectedRows()) {
            entries.add(tmw.getElementAt(selectedRow));
        }
        tmw.removeElements(entries);
    }//GEN-LAST:event_onRemoveWhitelistEntries

    /**
     * Method that is called when the user clicks on the button to add a
     * whitelist entry
     *
     * @param evt
     */
    private void onAddWhitelistEntry(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onAddWhitelistEntry
        AddWhitelistEntryDlg dlg = new AddWhitelistEntryDlg(parent, true, null);
        dlg.setVisible(true);
        if (dlg.isOk()) {
            tmw.addElement(dlg.getEntry());
        }
    }//GEN-LAST:event_onAddWhitelistEntry

    /**
     * Method that is called when the user clicks on a whitelist entry
     *
     * @param evt
     */
    private void onWhitelistEntryClick(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onWhitelistEntryClick
        if (evt.getClickCount() == 2) {
            int position = tbWhitelist.getSelectedRow();
            WhitelistEntry slctdEntry = tmw.getElementAt(position);
            AddWhitelistEntryDlg dlg = new AddWhitelistEntryDlg(parent, true, slctdEntry);
            dlg.setVisible(true);
            if (dlg.isOk()) {
                tmw.setEntry(dlg.getEntry(), position);
            }
        }
    }//GEN-LAST:event_onWhitelistEntryClick

    /**
     * Method that is called when the user changes the ref-erg-weighting
     * settings
     *
     * @param evt
     */
    private void onChangeRefErgWeight(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onChangeRefErgWeight
        if (rbWeightRef.isSelected()) {
            GlobalParamter.getInstance().setErgRefWeighting("ref");
            GlobalMethods.getInstance().saveCustomDiffSettings();
        } else if (rbWeightErg.isSelected()) {
            GlobalParamter.getInstance().setErgRefWeighting("erg");
            GlobalMethods.getInstance().saveCustomDiffSettings();
        }
    }//GEN-LAST:event_onChangeRefErgWeight

    /**
     * Method that is called when the user changes the alignment-restriction
     * settings
     *
     * @param evt
     */
    private void onChangeDiffAlignmentRestriction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onChangeDiffAlignmentRestriction
        GlobalParamter.getInstance().setDiffAlignmentRestriction(rbRestrictedTrue.isSelected());
        GlobalMethods.getInstance().saveCustomDiffSettings();
    }//GEN-LAST:event_onChangeDiffAlignmentRestriction

    private void onChangeUser(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onChangeUser
        Nutzer nutzer = (Nutzer) dcbm.getSelectedItem();
        if (nutzer != null) {
            for (Nutzer user : DatabaseGlobalAccess.getInstance().getAllUsers()) {
                if (user == nutzer) {
                    DatabaseGlobalAccess.getInstance().setCurrentNutzer(nutzer);
                }
            }
        }
    }//GEN-LAST:event_onChangeUser

    private void onAddUser(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onAddUser
        //if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
        if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
            String username = JOptionPane.showInputDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Geben Sie den Namen des Benutzers ein");
            if (username == null) {
                return;
            }

            if (username.trim().isEmpty()) {
                JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Ein leerer Nutzername ist nicht möglich!");
                return;
            }

//            try {
//                Nutzer nutzer = dashboard.database.DB_Access.getInstance().addUser(username);
//                GlobalParamter.getInstance().getAllUser().add(nutzer);
//                updateUserCB();
//                GlobalParamter.getInstance().setSelected_user(nutzer);
//
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
            DB_Access_Manager.getInstance().addUser(username);
            updateUserCB();
        }

    }//GEN-LAST:event_onAddUser

    public void updateUserCB() {
        System.out.println("Update Content");
        Nutzer nutzer = (Nutzer) dcbm.getSelectedItem();
        System.out.println(nutzer);
        dcbm.removeAllElements();

//        System.out.println(GlobalParamter.getInstance().getAllUser());
//        for (Nutzer user : GlobalParamter.getInstance().getAllUser()) {
//            dcbm.addElement(user);
//        }
        System.out.println(DatabaseGlobalAccess.getInstance().getAllUsers());
        for (Nutzer user : DatabaseGlobalAccess.getInstance().getAllUsers()) {
            dcbm.addElement(user);
        }
        if (nutzer != null) {
            dcbm.setSelectedItem(nutzer);
        }
        cbNutzer.updateUI();
    }

    /**
     * Method that is called when the windows is maximized to its full size
     */
    public void onFullScreenPanel() {
        try {
            characters = 45;
            setTextOfPanels(settings.io.Loader.getSpecificParameter(true));
            setNormalText(lbProject);
            setNormalText(lbRec);
            setNormalText(lbDynamic);
            setNormalText(lbArticle);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Set the description for the JLabels of the first column
     *
     * @param label
     */
    private void setNormalText(JLabel label) {
        label.setText(label.getName());
        validate();
    }

    /**
     * Set the description for the JLabels of the first column with a line break
     *
     * @param label
     * @param breakPoint
     * @param breakSign
     */
    private void makeBrForLabel(JLabel label, String breakPoint, String breakSign) {
        label.setText("<html>" + label.getName().substring(0, label.getName().indexOf(breakPoint) + (breakPoint).length()) + breakSign + "<br/>"
                + label.getName().substring(label.getName().indexOf(breakPoint) + (breakPoint).length()) + "</html>");
        validate();
    }

    /**
     * Method to get the n-th last index of a special character in a string
     *
     * @param nth
     * @param ch
     * @param string
     * @return
     */
    private int nthLastIndexOf(int nth, String ch, String string) {
        if (nth <= 0) {
            return string.length();
        }
        return nthLastIndexOf(--nth, ch, string.substring(0, string.lastIndexOf(ch)));
    }

    /**
     * Method to display the last two directories / files of a path when the
     * frame is below 1100px wide
     *
     * @param label
     */
    private void displayListTwoFiles(JLabel label) {
        String text = label.getText().replaceAll("<html>", "").replaceAll("</html>", "").replaceAll("<br>", "");
        label.setText(applyFormat("..." + text.substring(nthLastIndexOf(2, "\\", text))));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgDiffRestriction;
    private javax.swing.ButtonGroup bgDiffWeights;
    private javax.swing.JButton btAdd;
    private javax.swing.JButton btAddBackoffice;
    private javax.swing.JButton btAddCashpoint;
    private javax.swing.JButton btAddCommand;
    private javax.swing.JButton btAddNewUser;
    private javax.swing.JButton btAddTestsystem;
    private javax.swing.JButton btAddType;
    private javax.swing.JButton btChangeUser;
    private javax.swing.JButton btRemove;
    private javax.swing.JButton btRemoveBackoffice;
    private javax.swing.JButton btRemoveCashpoints;
    private javax.swing.JButton btRemoveTestsystem;
    private javax.swing.JButton btSaveGlobalExplorer;
    private javax.swing.JButton btSaveProjectExplorer;
    private javax.swing.JComboBox<Nutzer> cbNutzer;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbAnalysator;
    private javax.swing.JLabel lbArticle;
    private javax.swing.JLabel lbArtikelconfig;
    private javax.swing.JLabel lbCommands;
    private javax.swing.JLabel lbDashboard;
    private javax.swing.JLabel lbDynFunk;
    private javax.swing.JLabel lbDynamic;
    private javax.swing.JLabel lbExplorer;
    private javax.swing.JLabel lbGlobal;
    private javax.swing.JLabel lbProject;
    private javax.swing.JLabel lbRec;
    private javax.swing.JLabel lbRecorder;
    private javax.swing.JLabel lbRestrictionDescription;
    private javax.swing.JLabel lbRestrictionTitle;
    private javax.swing.JLabel lbSaveGlobalExplorer;
    private javax.swing.JLabel lbSaveLocationRecorder;
    private javax.swing.JLabel lbSaveProjectExplorer;
    private javax.swing.JLabel lbSimulator;
    private javax.swing.JLabel lbUser;
    private javax.swing.JLabel lbWeightsDescription;
    private javax.swing.JLabel lbWeightsTitle;
    private javax.swing.JLabel lbWhitelist;
    private javax.swing.JPanel paAnalysatorSettings;
    private javax.swing.JPanel paCommandSettings;
    private javax.swing.JPanel paDashboardSettings;
    private javax.swing.JPanel paDiffAdjustments;
    private javax.swing.JPanel paExplorerSettings;
    private javax.swing.JPanel paOtherAdjustments;
    private javax.swing.JPanel paRecorderSettings;
    private javax.swing.JPanel paRestriction;
    private javax.swing.JPanel paRestrictionInfo;
    private javax.swing.JPanel paRestrictionInner;
    private javax.swing.JPanel paSimiliarityRates;
    private javax.swing.JPanel paSimulatorSettings;
    private javax.swing.JPanel paUsers;
    private javax.swing.JPanel paWeights;
    private javax.swing.JPanel paWeightsInfo;
    private javax.swing.JPanel paWeightsInner;
    private javax.swing.JPanel paWhitelistActions;
    private javax.swing.JPanel paWhitelistEntryActions;
    private javax.swing.JPanel paWhitelistMain;
    private javax.swing.JRadioButton rbRestrictedFalse;
    private javax.swing.JRadioButton rbRestrictedTrue;
    private javax.swing.JRadioButton rbWeightErg;
    private javax.swing.JRadioButton rbWeightRef;
    private javax.swing.JScrollPane spTable;
    private javax.swing.JScrollPane spTableB;
    private javax.swing.JScrollPane spTableT;
    private javax.swing.JScrollPane spWhitelist;
    private javax.swing.JTable tbBackoffice;
    private javax.swing.JTable tbKassen;
    private javax.swing.JTable tbTestsysteme;
    private javax.swing.JTable tbWhitelist;
    // End of variables declaration//GEN-END:variables

}

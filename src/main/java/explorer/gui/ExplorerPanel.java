/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package explorer.gui;

import dashboard.beans.ChangeNew;
import dashboard.beans.Command;
import dashboard.beans.DurchlaufgegenstandNew;
import dashboard.beans.Projekt;
import dashboard.beans.TestCase;
import dashboard.beans.Testgruppe;
import dashboard.bl.DatabaseGlobalAccess;
import dashboard.database.DB_Access;
import dashboard.database.DB_Access_Manager;
import dashboard.enums.ChangeType;
import explorer.bl.ExplorerKeyListener;
import explorer.bl.ExplorerPopupMenuListener;
import explorer.bl.ExplorerTransferHandler;
import explorer.bl.ExplorerTreeCellRenderer;
import explorer.bl.ExplorerTreeModel;
import explorer.io.ExplorerIO;
import general.beans.io_objects.CommandRun;
import general.beans.io_objects.ExplorerLayer;
import general.beans.io_objects.ProjectRun;
import general.beans.io_objects.TestCaseRun;
import general.beans.io_objects.TestGroupRun;
import general.bl.GlobalAccess;
import general.bl.GlobalParamter;
import general.io.Loader;
import general.io.Mapper;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.Desktop;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.Box;
import javax.swing.DropMode;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import remote.bl.RemoteExecutionManager;
import simulator.bl.ExecutionManager;

/**
 *
 * @author Anna Lechner & Florian Deutschmann
 */
public class ExplorerPanel extends javax.swing.JPanel {

    private ExplorerTreeModel etm;
    private ExplorerTreeCellRenderer etcr;
    private DefaultMutableTreeNode root;
    private Path rootDir;
    private Path globalDir;
    private ExplorerTransferHandler etth = new ExplorerTransferHandler();
    private ExplorerKeyListener ekl = new ExplorerKeyListener();
    private ExplorerPopupMenuListener epml = new ExplorerPopupMenuListener();

    private static final Font DEFAULT_BOLD_FONT = new Font("Tahoma", Font.BOLD, 18);
    private static final Font DEFAULT_FONT = new Font("Tahoma", Font.PLAIN, 18);

    /**
     * Creates new form ExplorerPanel
     */
    public ExplorerPanel() {
        updateRootDir();
        initComponents();
        etcr = new ExplorerTreeCellRenderer();
        updateModel();
        trFileTree.setCellRenderer(etcr);
        trFileTree.setShowsRootHandles(true);
        trFileTree.addTreeSelectionListener((TreeSelectionEvent e) -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
            insertPanels(node);
        });
        trFileTree.setDragEnabled(true);
        trFileTree.setDropMode(DropMode.ON_OR_INSERT);
        trFileTree.setTransferHandler(etth);
        trFileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        setParameter();
        try {
            btReload.setIcon(Loader.loadImageExplorer("refresh.png", 70, 70));
            btReload.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        pmMenu.addPopupMenuListener(epml);
        addKeyListener();
        explorer.io.ExplorerIO.loadCtrlCommandV2ComboBoxContent();
        UIManager.put("OptionPane.messageFont", new Font("Tahoma", Font.PLAIN, 18));
    }

    public ExplorerTransferHandler getEtth() {
        return etth;
    }

    public JPopupMenu getPmMenu() {
        return pmMenu;
    }

    /**
     * Method to add key listener on specific components and make them focusable
     */
    private void addKeyListener() {
        btReload.addKeyListener(ekl);
        paEast.addKeyListener(ekl);
        paExplorer.addKeyListener(ekl);
        pmMenu.addKeyListener(ekl);
        spTree.addKeyListener(ekl);
        trFileTree.addKeyListener(ekl);

        btReload.setFocusable(true);
        paEast.setFocusable(true);
        paExplorer.setFocusable(true);
        pmMenu.setFocusable(true);
        spTree.setFocusable(true);
        trFileTree.setFocusable(true);
    }

    /**
     * Method to clear the JPanel on the right side
     */
    public void clearRightPanel() {
        paEast.removeAll();
        paEast.revalidate();
        paEast.updateUI();
    }

    /**
     * Method to set the JTree on a global variable
     */
    private void setParameter() {
        GlobalParamter.getInstance().setTrExplorer(trFileTree);
    }

    /**
     * This method is called when a new project path is chosen in the settings
     * or a node is renamed
     */
    public void updateTree() {
        updateRootDir();
        updateModel();
    }

    /**
     * Method to update the root dir after it was changed in the settings and
     * map the changed file structure into beans objects
     */
    private void updateRootDir() {
        try {
            rootDir = Paths.get(settings.io.Loader.getSpecificParameter(false).get("projectPath"));
            GlobalParamter.getInstance().setWorkingProjects(Mapper.mapFilesToBeans(rootDir));
            RemoteExecutionManager.getInstance().getPanel().updateTest();
            globalDir = Paths.get(settings.io.Loader.getSpecificParameter(false).get("globalPath"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method to update the ExplorerTreeModel with the beans objects of the
     * changed file structure
     */
    private void updateModel() {
        etm = new ExplorerTreeModel(addNodes(rootDir.toFile()));
        trFileTree.setModel(etm);
    }

    /**
     * Method to build the tree structure with the beans objects of the file
     * structure
     *
     * @param dir path/file that points to the root directory
     * @return
     */
    private DefaultMutableTreeNode addNodes(File dir) {
        root = new DefaultMutableTreeNode(dir);
        List<String> allDurchlaufItemsName = new LinkedList<>();
        try {
            for (ProjectRun workingProject : GlobalParamter.getInstance().getWorkingProjects()) {
                DefaultMutableTreeNode proj = new DefaultMutableTreeNode(workingProject);
                root.add(proj);
                for (TestGroupRun testgroup : workingProject.getTestgroups()) {
                    DefaultMutableTreeNode tg = new DefaultMutableTreeNode(testgroup);
                    proj.add(tg);
                    for (TestCaseRun testCase : testgroup.getTestCases()) {
                        DefaultMutableTreeNode tc = new DefaultMutableTreeNode(testCase);
                        tg.add(tc);
                        for (CommandRun command : testCase.getCommands()) {
                            DefaultMutableTreeNode com = new DefaultMutableTreeNode(command);
                            tc.add(com);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return root;
    }

    /**
     * Method to insert the needed panel depending on what level of the JTree is
     * clicked
     *
     * @param node which was clicked
     */
    private void insertPanels(DefaultMutableTreeNode node) {
        if (!(node.getUserObject() instanceof File)) {
            ExplorerLayer nodeExpl = (ExplorerLayer) node.getUserObject();
            if (nodeExpl instanceof ProjectRun) {
                insertSpecificPanels(new ExplorerProjectPanel(((ProjectRun) nodeExpl)));
            } else if (nodeExpl instanceof TestGroupRun) {
                insertSpecificPanels(new ExplorerTestgroupPanel(((TestGroupRun) nodeExpl), etm));
            } else if (nodeExpl instanceof TestCaseRun) {
                insertSpecificPanels(new ExplorerTestcasePanel(((TestCaseRun) nodeExpl), etm));
            } else if (nodeExpl instanceof CommandRun) {
                insertSpecificPanels(new ExplorerCommandPanel(((CommandRun) nodeExpl), etm));
            } else {
                paEast.removeAll();
            }
            updateUI();
        }
    }

    /**
     * Outsourced method to either add an ExplorerTestgroupPanel, an
     * ExplorerTestcasePanel or an ExplorerCommandPanel
     *
     * @param explorerPanel
     */
    private void insertSpecificPanels(JPanel explorerPanel) {
        paEast.removeAll();
        paEast.add(explorerPanel);
    }

    /**
     * Method to generate a dynamic JPopupMenu depending on what level of the
     * JTree is clicked
     *
     * @param node which was clicked
     */
    public void generateTablePopupMenu(DefaultMutableTreeNode node) {
        pmMenu.removeAll();
        if (trFileTree.getSelectionRows().length == 1) {
            if (!(node.getUserObject() instanceof File)) {
                ExplorerLayer nodeExpl = (ExplorerLayer) node.getUserObject();
                if (nodeExpl instanceof ProjectRun) {
                    pmMenu.add(miAddTG);
                    pmMenu.add(miRemove);
                    pmMenu.add(miRename);
                    pmMenu.add(miClose);
                } else if (nodeExpl instanceof TestGroupRun) {
                    if (!((TestGroupRun) nodeExpl).getDescription().contains("Init") && !((TestGroupRun) nodeExpl).getDescription().contains("Cleanup")) {
                        pmMenu.add(miRename);
                    }
                    pmMenu.add(miRemove);
                    pmMenu.add(miAddTC);
                } else if (nodeExpl instanceof TestCaseRun) {
                    if (!((TestCaseRun) nodeExpl).getDescription().contains("Init") && !((TestCaseRun) nodeExpl).getDescription().contains("Cleanup")) {
                        pmMenu.add(miRename);
                    }
                    pmMenu.add(miRemove);
                    pmMenu.add(miAddCom);
                } else if (nodeExpl instanceof CommandRun) {
                    pmMenu.add(miRename);
                    pmMenu.add(miRemove);
                }
                pmMenu.add(miSimulator);
                pmMenu.revalidate();
            } else {
                pmMenu.add(miAddProject);
            }
        } else {
            pmMenu.add(miRemove);
            pmMenu.add(miSimulator);
            pmMenu.revalidate();
        }
    }

    /**
     * Method to determine the closest row of a JTree item to be able to select
     * an entire row of JTree
     *
     * @param evt
     */
    private void getClosestRowOfTree(MouseEvent evt) {
        if (!GlobalParamter.getInstance().isCtrl_key_down() && !GlobalParamter.getInstance().isShift_key_down()) {
            int closestRow = trFileTree.getClosestRowForLocation(
                    evt.getX(), evt.getY());
            Rectangle closestRowBounds = trFileTree.getRowBounds(closestRow);
            if (evt.getY() >= closestRowBounds.getY()
                    && evt.getY() < closestRowBounds.getY()
                    + closestRowBounds.getHeight()) {
                if (evt.getX() > closestRowBounds.getX()
                        && closestRow < trFileTree.getRowCount()) {
                    trFileTree.setSelectionRow(closestRow);
                }
            } else {
                trFileTree.setSelectionRow(-1);
                if (SwingUtilities.isRightMouseButton(evt)) {
                    pmMenu.removeAll();
                }
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

        pmMenu = new javax.swing.JPopupMenu();
        miRename = new javax.swing.JMenuItem();
        miRemove = new javax.swing.JMenuItem();
        miAddTG = new javax.swing.JMenuItem();
        miAddTC = new javax.swing.JMenuItem();
        miAddCom = new javax.swing.JMenuItem();
        miSimulator = new javax.swing.JMenuItem();
        miAddProject = new javax.swing.JMenuItem();
        miClose = new javax.swing.JMenuItem();
        paExplorer = new javax.swing.JPanel();
        paSouth = new javax.swing.JPanel();
        spTree = new javax.swing.JScrollPane();
        trFileTree = new javax.swing.JTree();
        jPanel1 = new javax.swing.JPanel();
        btReload = new javax.swing.JButton();
        btReload1 = new javax.swing.JButton();
        paRight = new javax.swing.JPanel();
        paEast = new javax.swing.JPanel();

        pmMenu.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        miRename.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        miRename.setText("Umbenennen");
        miRename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onRename(evt);
            }
        });
        pmMenu.add(miRename);

        miRemove.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        miRemove.setText("Löschen");
        miRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onRemove(evt);
            }
        });
        pmMenu.add(miRemove);

        miAddTG.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        miAddTG.setText("Testgruppe hinzufügen");
        miAddTG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onAddTestGroup(evt);
            }
        });

        miAddTC.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        miAddTC.setText("Testcase hinzufügen");
        miAddTC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onAddTestCase(evt);
            }
        });

        miAddCom.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        miAddCom.setText("Command hinzufügen");
        miAddCom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onAddCommand(evt);
            }
        });

        miSimulator.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        miSimulator.setText("Im Simulator abspielen");
        miSimulator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onSendToSimulator(evt);
            }
        });

        miAddProject.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        miAddProject.setText("Projekt hinzufügen");
        miAddProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onAddProject(evt);
            }
        });

        miClose.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        miClose.setText("Schließen");
        miClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onClose(evt);
            }
        });

        setLayout(new java.awt.BorderLayout());

        paExplorer.setLayout(new java.awt.GridLayout(1, 2));

        paSouth.setLayout(new java.awt.BorderLayout());

        spTree.setPreferredSize(new java.awt.Dimension(600, 389));

        trFileTree.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        trFileTree.setComponentPopupMenu(pmMenu);
        trFileTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                onCallPopup(evt);
            }
        });
        spTree.setViewportView(trFileTree);

        paSouth.add(spTree, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridLayout());

        btReload.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btReload.setText("Reload Baum");
        btReload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onReloadTree(evt);
            }
        });
        jPanel1.add(btReload);

        btReload1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btReload1.setText("In Datenbank importieren");
        btReload1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onImportProject(evt);
            }
        });
        jPanel1.add(btReload1);

        paSouth.add(jPanel1, java.awt.BorderLayout.SOUTH);

        paExplorer.add(paSouth);

        paRight.setPreferredSize(new java.awt.Dimension(100, 389));
        paRight.setLayout(new java.awt.BorderLayout());

        paEast.setLayout(new java.awt.GridLayout(3, 0));
        paRight.add(paEast, java.awt.BorderLayout.CENTER);

        paExplorer.add(paRight);

        add(paExplorer, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Method that is called when a JPopupMenu is called
     *
     * @param evt
     */
    private void onCallPopup(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_onCallPopup
        try {
            if (SwingUtilities.isLeftMouseButton(evt)) {
                getClosestRowOfTree(evt);
            }
            if (SwingUtilities.isRightMouseButton(evt)) {
                TreePath trPath = trFileTree.getSelectionPath();
                if (trPath != null) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) trPath.getLastPathComponent();
                    if (node.getUserObject() instanceof ExplorerLayer) {
                        generateTablePopupMenu(node);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_onCallPopup

    /**
     * Method to get the needed RegEx for the appendant ExplorerLayer
     *
     * @param explo
     * @return
     */
    private String getRegExForExplorerLayer(ExplorerLayer explo) {
        String regEx = "";
        if (explo instanceof TestGroupRun) {
            regEx = "Testgruppe_[0-9]+ - ";
        } else if (explo instanceof TestCaseRun) {
            regEx = "Testcase_[0-9]+ - ";
        } else if (explo instanceof CommandRun) {
            regEx = "[0-9]+_[0-9]+_[0-9]+";
        }
        return regEx;
    }

    /**
     * Method that is called when the JMenuItem "Umbenennen" was clicked in the
     * JPopupMenu
     *
     * @param evt
     */
    private void onRename(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onRename
        try {
            DefaultMutableTreeNode node = null;
            try {
                node = (DefaultMutableTreeNode) trFileTree.getSelectionPath().getLastPathComponent();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Bitte wählen Sie einen Eintrag im Tree aus!", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (node.getUserObject() instanceof ExplorerLayer) {
                ExplorerLayer nodeExpl = (ExplorerLayer) node.getUserObject();
                String oldDescription = nodeExpl.getDescription();
                //only allow rename if description doesn't contain init or cleanup
                if (!oldDescription.contains("Init") && !oldDescription.contains("Cleanup")) {
                    File toBeRenamed = nodeExpl.getPath().toFile();
                    //define regex for each type
                    String regEx = getRegExForExplorerLayer(nodeExpl);
                    //only make specific part of description editable (not the sequential numbers)
                    String displayStr = oldDescription.replaceAll(regEx, "").trim();
                    String newName = (String) JOptionPane.showInputDialog(null, "Neuer Name",
                            "Umbenennen", JOptionPane.QUESTION_MESSAGE, null, null,
                            oldDescription.replaceAll(regEx, "").trim());
                    //if the newly chosen name is okay -> change description
                    if (newName != null) {
                        if (newName.trim() != null && !newName.trim().isEmpty()) {
                            if (nodeExpl instanceof ProjectRun) {
                                //change description and rename folder
                                nodeExpl.setDescription(newName);
                                ExplorerIO.changeNodeOfProject(Paths.get(toBeRenamed.toString(), "run", "run.xml"), "description", nodeExpl.getDescription());
                                nodeExpl.setPath(Paths.get(toBeRenamed.getParent(), newName.toLowerCase()));
                                //change description of init
                                ExplorerIO.changeDescriptionOfInitAndCleanup(Paths.get(nodeExpl.getPath().toString(), "run", "000_INIT", "run.xml"),
                                        "description", nodeExpl.getDescription());
                                TestGroupRun tgInit = ((TestGroupRun) ((DefaultMutableTreeNode) node.getChildAt(0)).getUserObject());
                                tgInit.setDescription(newName + tgInit.getDescription().substring(tgInit.getDescription().indexOf("-") - 1));
                                etm.nodeChanged(node.getChildAt(0));
                                //change description of cleanup
                                ExplorerIO.changeDescriptionOfInitAndCleanup(Paths.get(nodeExpl.getPath().toString(), "run", "999_CLEANUP", "run.xml"),
                                        "description", nodeExpl.getDescription());
                                TestGroupRun tgCleanup = ((TestGroupRun) ((DefaultMutableTreeNode) node.getChildAt(node.getChildCount() - 1)).getUserObject());
                                tgCleanup.setDescription(newName + tgCleanup.getDescription().substring(tgCleanup.getDescription().indexOf("-") - 1));
                                etm.nodeChanged(node.getChildAt(node.getChildCount() - 1));
                            } else if (nodeExpl instanceof TestGroupRun || nodeExpl instanceof TestCaseRun) {
                                //get sequential number from existing description
                                String oldTxtDescription = oldDescription.replaceAll(regEx, "");
                                String prefix = oldDescription.replaceAll(oldTxtDescription, "");
                                //change description
                                nodeExpl.setDescription(prefix + newName);
                                ExplorerIO.changeNodeOfTG(Paths.get(toBeRenamed.toString(), "run.xml"), "description", nodeExpl.getDescription());
                            } else if (nodeExpl instanceof CommandRun) {
                                //get sequential number from existing description
                                String existingNumbers = oldDescription.replaceAll(displayStr, "").trim();
                                //change description
                                nodeExpl.setDescription(newName + " " + existingNumbers);
                                ExplorerIO.changeCommandText(Paths.get(toBeRenamed.toString(), "run.xml"), oldDescription, nodeExpl.getDescription());
                            }
                            //update right panel and node
                            insertPanels(node);
                            etm.nodeChanged(node);
//                            if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                                DB_Access.getInstance().updateEntry(nodeExpl, oldDescription);
//                                if (GlobalParamter.getInstance().getSelected_user() != null) {
//                                    DB_Access.getInstance().addChangeEntry(nodeExpl, ChangeType.CHANGED);
//                                } else {
//                                    JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Kein Nutzer ausgewählt mit welchem diese Änderung in der Datenbank"
//                                            + " assoziert werden kann. Um dies sicherzustellen sollte ein Nutzer in den Einstellungen ausgewählt werden");
//                                }
//                            }
                            if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
                                if (DatabaseGlobalAccess.getInstance().getCurrentNutzer() != null) {
                                    DB_Access_Manager.getInstance().addChangeEntry(nodeExpl, "CHANGED");
                                }
                            }
                            paEast.updateUI();
                        } else {
                            JOptionPane.showMessageDialog(null, "Init und Cleanup können nicht umbenannt werden!", "Nicht erlaubt", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException | XPathExpressionException
                | TransformerException | InterruptedException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_onRename

    /**
     * Method that is called when the JMenuItem "Testgruppe hinzufügen" was
     * clicked in the JPopupMenu
     *
     * @param evt
     */
    private void onAddTestGroup(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onAddTestGroup
        try {
            DefaultMutableTreeNode node = null;
            try {
                node = (DefaultMutableTreeNode) trFileTree.getSelectionPath().getLastPathComponent();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Bitte wählen Sie einen Eintrag im Tree aus!", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!(node.getUserObject() instanceof File)) {
                ExplorerAddTestgroupDlg tgd = new ExplorerAddTestgroupDlg(null, true);
                tgd.setVisible(true);
                if (tgd.isOk()) {
                    TestGroupRun addTg = tgd.getTg();
                    //get number of last testgroup and set new number (old number +1)
                    int num = getNumberOfLastTestgroup(((ProjectRun) (ExplorerLayer) node.getUserObject()));
//                    int num = getNumberOfLastTestgroup(GlobalParamter.getInstance().getWorkingProjects().get(GlobalParamter.getInstance().getWorkingProjects().size() - 1)) + 1;
                    //set path and description with new number
                    addTg.setPath(Paths.get(((ProjectRun) node.getUserObject()).getPath().toString(), "run", String.format("%03d_TG", num + 1)));
                    addTg.setDescription("Testgruppe_" + (num + 1) + " - " + addTg.getDescription());
                    ExplorerIO.addTestGroup(addTg);
                    //create TreeNode for new TG and take all necessary actions
                    DefaultMutableTreeNode testGroupNode = new DefaultMutableTreeNode(addTg);
                    createInitAndCleanupForTG(addTg, num, "Initialisierung", "000_INIT", testGroupNode);
                    createInitAndCleanupForTG(addTg, num, "Cleanup", "999_CLEANUP", testGroupNode);
                    //invoke update events
                    ((ProjectRun) node.getUserObject()).getTestgroups().add(((ProjectRun) node.getUserObject()).getTestgroups().size() - 1, addTg);
                    node.add(testGroupNode);
                    //insert node before cleanup
                    etm.insertNodeInto(testGroupNode, node, getDirectChildren(node).size() - 2);
//                    if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                        DB_Access.getInstance().addTestGroup(addTg, DB_Access.getInstance().getIdOfDurchlaufgegenstand((ProjectRun) node.getUserObject(),
//                                (ProjectRun) node.getUserObject()));
//                        if (GlobalParamter.getInstance().getSelected_user() != null) {
//                            DB_Access.getInstance().addChangeEntry(addTg, ChangeType.ADDED);
//                        } else {
//                            JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Kein Nutzer ausgewählt mit welchem diese Änderung in der Datenbank"
//                                    + " assoziert werden kann. Um dies sicherzustellen sollte ein Nutzer in den Einstellungen ausgewählt werden");
//                        }
//
//                    }
                    if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
                        ((Testgruppe)addTg.getDurchlauf_gegenstand()).setProjekt((Projekt) ((ExplorerLayer) node.getUserObject()).getDurchlauf_gegenstand());
                        if (DatabaseGlobalAccess.getInstance().getCurrentNutzer() != null) {
                            DB_Access_Manager.getInstance().addChangeEntry(addTg, "ADDED");
                        } else {
                            DB_Access_Manager.getInstance().updateData();
                            JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Kein Nutzer ausgewählt mit welchem diese Änderung in der Datenbank"
                                    + " assoziert werden kann. Um dies sicherzustellen sollte ein Nutzer in den Einstellungen ausgewählt werden");
                        }
                    }
                }
            }
        } catch (IOException | ParserConfigurationException | TransformerException | SAXException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_onAddTestGroup

    /**
     * Method to determine all direct children of a TreeNode
     *
     * @param parent parent node from which the children should be identified
     * and returned
     * @return
     */
    private List<DefaultMutableTreeNode> getDirectChildren(DefaultMutableTreeNode parent) {
        List<DefaultMutableTreeNode> allNodes = new LinkedList<>();
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (((DefaultMutableTreeNode) parent.getChildAt(i)).getLevel() - 1 == parent.getLevel()) {
                allNodes.add(((DefaultMutableTreeNode) parent.getChildAt(i)));
            }
        }
        return allNodes;
    }

    /**
     * Method to create the init and cleanup for a new testgroup and adds the
     * objects to the node of the testgroup
     *
     * @param tg
     * @param num
     * @param description
     * @param dirName
     * @param testGroupNode
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws TransformerException
     */
    private void createInitAndCleanupForTG(TestGroupRun tg, int num, String description, String dirName, DefaultMutableTreeNode testGroupNode) throws ParserConfigurationException, SAXException, IOException, TransformerException {
        //create init & cleanup for new TG
        TestCaseRun tc = new TestCaseRun(String.format("TG%02d - %s", num, description), Paths.get(tg.getPath().toString(), dirName));
        ExplorerIO.addInitOrCleanupToNewTG(tc);
        tg.getTestCases().add(tc);
        testGroupNode.add(new DefaultMutableTreeNode(tc));
    }

    /**
     * Method to retrieve the highest TG number
     *
     * @param projects
     * @return
     */
    private int getNumberOfLastTestgroup(ProjectRun project) {
        for (TestGroupRun testgroup : project.getTestgroups()) {
            System.out.println("------------" + testgroup);
        }
        TestGroupRun lastTg = project.getTestgroups().get(project.getTestgroups().size() - 2);
        String lastNum = String.format("%1d", Integer.parseInt(lastTg.getPath().toString().substring(lastTg.getPath().toString().lastIndexOf("\\") + 1)
                .replaceAll("[^0-9]", "")));
        if (lastNum.isEmpty()) {
            lastNum = "1";
        }
        return Integer.parseInt(lastNum);
    }

    /**
     * Method to retrieve the highest TC number
     *
     * @param tg the testgroup run of which the number of testcases should be
     * identified
     * @return
     */
    private int getNumberOfLastTestcase(TestGroupRun tg) {
        String lastNum = "";
        TestCaseRun lastTc = null;
        try {
            if (tg.getTestCases().size() >= 2) {
                if (tg.getTestCases().size() == 1) {
                    lastTc = tg.getTestCases().get(tg.getTestCases().size() - 1);
                } else {
                    List<TestCaseRun> tcs = tg.getTestCases();
                    tcs.sort(Comparator.comparing(TestCaseRun::getDescription));
                    lastTc = tg.getTestCases().get(tcs.size() - 1);
                }
            }
            lastNum = String.format("%1d", Integer.parseInt(lastTc.getPath().toString().substring(lastTc.getPath().toString().lastIndexOf("\\") + 1)
                    .replaceAll("[^0-9]", "")));
            if (lastNum.isEmpty()) {
                lastNum = "0";
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            lastNum = "0";
        }
        return Integer.parseInt(lastNum);
    }

    /**
     * Method that is called when the JMenuItem "Testcase hinzufügen" was
     * clicked in the JPopupMenu
     *
     * @param evt
     */
    private void onAddTestCase(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onAddTestCase
        try {
            DefaultMutableTreeNode node = null;
            try {
                node = (DefaultMutableTreeNode) trFileTree.getSelectionPath().getLastPathComponent();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Bitte wählen Sie einen Eintrag im Tree aus!", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!(node.getUserObject() instanceof File)) {
                ExplorerLayer nodeExpl = (ExplorerLayer) node.getUserObject();
                ExplorerAddTestcaseDlg eatd = new ExplorerAddTestcaseDlg(null, true);
                eatd.setVisible(true);
                if (eatd.isOk()) {
                    String description = eatd.getDescription();
                    //get number of testgroup
                    TestGroupRun tg = (TestGroupRun) nodeExpl;
                    //am Ende ist immer Cleanup
                    int num = 0;
                    //get number of last testcase and set new number (old number +1)
                    num = getNumberOfLastTestcase(tg) + 1;
                    //create new TC
                    TestCaseRun addTc = new TestCaseRun("Testcase_" + num + " - " + description, Paths.get(nodeExpl.getPath().toString(), String.format("%03d_TC", num)));
                    ExplorerIO.addTestCase(addTc);
                    if (!tg.getTestCases().isEmpty()) {
                        tg.getTestCases().add(tg.getTestCases().size() - 1, addTc);
                    } else {
                        tg.getTestCases().add(tg.getTestCases().size(), addTc);
                    }

                    DefaultMutableTreeNode testCaseNode = new DefaultMutableTreeNode(addTc);
                    node.add(testCaseNode);
                    //insert node before cleanup
                    if (((DefaultMutableTreeNode) node.getChildAt(0)).getUserObject().toString().contains("Init")) {
                        etm.insertNodeInto(testCaseNode, node, getDirectChildren(node).size() - 2);
                    } else {
                        etm.insertNodeInto(testCaseNode, node, getDirectChildren(node).size() - 1);
                    }

                    if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
                        TestCase db_tc = new TestCase(addTc.getDescription(), LocalDate.now(), 0);
                        addTc.setDurchlauf_gegenstand(db_tc);
                        db_tc.setTestGruppe((Testgruppe) tg.getDurchlauf_gegenstand());
                        if (DatabaseGlobalAccess.getInstance().getCurrentNutzer() != null) {
                            DB_Access_Manager.getInstance().addChangeEntry(addTc, "ADDED");
                        } else {
                            DB_Access_Manager.getInstance().updateData();
                            JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Kein Nutzer ausgewählt mit welchem diese Änderung in der Datenbank"
                                    + " assoziert werden kann. Um dies sicherzustellen sollte ein Nutzer in den Einstellungen ausgewählt werden");
                        }
                    }
//                    if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                        DB_Access.getInstance().addTestCase(addTc, DB_Access.getInstance().getIdOfDurchlaufgegenstand((TestGroupRun) node.getUserObject(), (TestGroupRun) node.getUserObject()));
//                        if (GlobalParamter.getInstance().getSelected_user() != null) {
//                            DB_Access.getInstance().addChangeEntry(addTc, ChangeType.ADDED);
//                        } else {
//                            JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Kein Nutzer ausgewählt mit welchem diese Änderung in der Datenbank"
//                                    + " assoziert werden kann. Um dies sicherzustellen sollte ein Nutzer in den Einstellungen ausgewählt werden");
//                        }
//                    }
                }
            }
        } catch (IOException | ParserConfigurationException | TransformerException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_onAddTestCase

    /**
     * Method that is called when the JMenuItem "Command hinzufügen" was clicked
     * in the JPopupMenu
     *
     * @param evt
     */
    private void onAddCommand(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onAddCommand
        try {
            DefaultMutableTreeNode treeNode = null;
            try {
                treeNode = (DefaultMutableTreeNode) trFileTree.getSelectionPath().getLastPathComponent();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Bitte wählen Sie einen Eintrag im Tree aus!", "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!(treeNode.getUserObject() instanceof File)) {
                ExplorerAddCommandDlg addComm = new ExplorerAddCommandDlg(null, false, (TestCaseRun) treeNode.getUserObject(), treeNode);
                int getAmountOfBons = ExplorerIO.getAmountOfRecordersInFolder(((ExplorerLayer) treeNode.getUserObject()).getPath());
                int indexOfTC = treeNode.getParent().getIndex(treeNode);
                int indexOfTG = treeNode.getParent().getParent().getIndex(treeNode.getParent());
                addComm.setRecorderName("rec_tg" + indexOfTG + "_tc" + indexOfTC + "_" + getAmountOfBons);
                addComm.setVisible(true);
                GlobalParamter.getInstance().setDlg(addComm);
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_onAddCommand

    /**
     * Method that is called when the ExplorerAddCommandDlg is finished
     */
    public void finishDLG() {
        ExplorerAddCommandDlg addComm = GlobalParamter.getInstance().getDlg();
        CommandRun comm = null;
        try {
            if (addComm.getMultipleCommandRuns().isEmpty()) {
                comm = addComm.getCommand();
                String commandTyp = addComm.getCommandTyp();
                String displayName = addComm.getDisplayName();
                Map<Node, String> inputPerSubnode = addComm.getInputPerSubnode();
                NodeList nl = ExplorerIO.addNodeToRun(inputPerSubnode, commandTyp, Paths.get(comm.getPath().toString(), "run.xml"), displayName);
                comm.setNodeList(nl);
                addComm.getParentTC().getCommands().add(comm);
                addComm.getParentNode().add(new DefaultMutableTreeNode(comm));
            } else if (addComm.getMultipleCommandRuns().size() > 0 && addComm.isCreatedBon()) {
                int command_index = 0;
                for (Map<Node, String> tmp_map : addComm.getInputPerSubnodeList()) {
                    comm = addComm.getMultipleCommandRuns().get(command_index);
                    command_index++;
                    NodeList nl = ExplorerIO.addNodeToRun(tmp_map, addComm.getCommandTyp(), Paths.get(comm.getPath().toString(), "run.xml"), addComm.getDisplayName());
                    comm.setNodeList(nl);
                    addComm.getParentTC().getCommands().add(comm);
                    addComm.getParentNode().add(new DefaultMutableTreeNode(comm));
                }
            }
            etm.nodeStructureChanged(addComm.getParentNode());

            if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
                ((Command)comm.getDurchlauf_gegenstand()).setTestCase((TestCase) addComm.getParentTC().getDurchlauf_gegenstand());
                if (DatabaseGlobalAccess.getInstance().getCurrentNutzer() != null) {
                    DB_Access_Manager.getInstance().addChangeEntry(comm, "ADDED");
                } else {
                    DB_Access_Manager.getInstance().updateData();
                    JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Kein Nutzer ausgewählt mit welchem diese Änderung in der Datenbank"
                            + " assoziert werden kann. Um dies sicherzustellen sollte ein Nutzer in den Einstellungen ausgewählt werden");
                }
            }

//            if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                DB_Access.getInstance().addCommand(comm, DB_Access.getInstance().getIdOfDurchlaufgegenstand((TestCaseRun) addComm.getParentNode().getUserObject(), (TestCaseRun) addComm.getParentNode().getUserObject()));
//                if (GlobalParamter.getInstance().getSelected_user() != null) {
//                    DB_Access.getInstance().addChangeEntry(comm, ChangeType.ADDED);
//                } else {
//                    JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Kein Nutzer ausgewählt mit welchem diese Änderung in der Datenbank"
//                            + " assoziert werden kann. Um dies sicherzustellen sollte ein Nutzer in den Einstellungen ausgewählt werden");
//                }
//            }
        } catch (ParserConfigurationException | SAXException | IOException | TransformerException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Method that is called when the JMenuItem "Löschen" was clicked in the
     * JPopupMenu
     *
     * @param evt
     */
    private void onRemove(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onRemove
        removeMultiSelection();
    }//GEN-LAST:event_onRemove

    /**
     * Outsourced method to remove multiple selected items from the JTree and in
     * the file system
     */
    public void removeMultiSelection() {
        TreePath[] treePath = null;
        try {
            treePath = trFileTree.getSelectionPaths();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Bitte wählen Sie einen Eintrag im Tree aus!", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String remNodes = "";
        String oldDesc = "";
        for (TreePath tp : treePath) {
            remNodes += "- " + tp.getLastPathComponent() + "\n";
        }
        int option = JOptionPane.showConfirmDialog(null, "<html><b>Sind Sie sich sicher, dass Sie folgende Elemente löschen wollen?</b>\n" + remNodes,
                "Sind Sie sich sicher?", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            List<DefaultMutableTreeNode> removeElements = new ArrayList<>();
            DefaultMutableTreeNode saved = null;
            removeElements.add((DefaultMutableTreeNode) treePath[0].getLastPathComponent());
            for (int i = 1; i < treePath.length; i++) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) treePath[i].getLastPathComponent();
                for (DefaultMutableTreeNode remNode : removeElements) {
                    saved = remNode;
                }
                if (node.getParent() != saved && node.getParent().getParent() != saved) {
                    removeElements.add(node);
                }
            }
            for (DefaultMutableTreeNode node : removeElements) {
                if (!node.getUserObject().toString().contains("Init") && !node.getUserObject().toString().contains("Cleanup")) {
                    if (node.getUserObject() instanceof ExplorerLayer) {
                        paEast.removeAll();
                        try {
                            if (!(node.getUserObject() instanceof File)) {
                                ExplorerLayer nodeExpl = (ExplorerLayer) node.getUserObject();
                                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                                if (nodeExpl instanceof ProjectRun) {
                                    ExplorerIO.removeNode(nodeExpl.getPath().toFile());
                                }
                                if (nodeExpl instanceof TestGroupRun) {
                                    oldDesc = nodeExpl.getDescription();
                                    ((ProjectRun) parent.getUserObject()).getTestgroups().remove((TestGroupRun) nodeExpl);
                                    ExplorerIO.removeNode(nodeExpl.getPath().toFile());
                                    ExplorerIO.renameTestGroups(Paths.get(((ExplorerLayer) parent.getUserObject()).getPath().toString(), "run"), getDirectChildren(parent));
                                } else if (nodeExpl instanceof TestCaseRun) {
                                    int index = parent.getIndex(node);
//                                ((TestGroupRun) parent.getUserObject()).getTestCases().remove((TestCaseRun) nodeExpl);
                                    oldDesc = nodeExpl.getDescription();
                                    ((TestGroupRun) parent.getUserObject()).getTestCases().remove(index);
                                    ExplorerIO.removeNode(nodeExpl.getPath().toFile());
                                    ExplorerIO.renameTestCases(((ExplorerLayer) parent.getUserObject()).getPath(), getDirectChildren(parent));
                                } else if (nodeExpl instanceof CommandRun) {
                                    ((TestCaseRun) parent.getUserObject()).getCommands().remove((CommandRun) nodeExpl);
                                    ExplorerIO.removeCommandFromXML(Paths.get(nodeExpl.getPath().toString(), "run.xml"), node.getUserObject().toString());
                                }

                                if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
                                    DB_Access_Manager.getInstance().deleteNode(nodeExpl.getDurchlauf_gegenstand());
                                    if (DatabaseGlobalAccess.getInstance().getCurrentNutzer() != null) {                                        
                                        DB_Access_Manager.getInstance().addChangeEntry(nodeExpl, "DELETED");
                                        if (parent.getUserObject() instanceof ExplorerLayer) {
                                            DB_Access_Manager.getInstance().addChangeEntry((ExplorerLayer) parent.getUserObject(), "STATECHANGED");
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Kein Nutzer ausgewählt mit welchem diese Änderung in der Datenbank"
                                                + " assoziert werden kann. Um dies sicherzustellen sollte ein Nutzer in den Einstellungen ausgewählt werden");
                                    }
                                }

//                                if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                                    DB_Access.getInstance().removeEntry(nodeExpl);
//                                    if (GlobalParamter.getInstance().getSelected_user() != null) {
//                                        DB_Access.getInstance().addChangeEntry(nodeExpl, ChangeType.DELETED);
//                                        if (parent.getUserObject() instanceof ExplorerLayer) {
//                                            DB_Access.getInstance().addChangeEntry((ExplorerLayer) parent.getUserObject(), ChangeType.CHILD_STATECHANGED);
//                                        }
//                                    } else {
//                                        JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Kein Nutzer ausgewählt mit welchem diese Änderung in der Datenbank"
//                                                + " assoziert werden kann. Um dies sicherzustellen sollte ein Nutzer in den Einstellungen ausgewählt werden");
//                                    }
//                                }
                            }
                        } catch (ParserConfigurationException | SAXException | IOException | TransformerException | XPathExpressionException ex) {
                            ex.printStackTrace();
                        }
                        etm.removeNodeFromParent(node);
                        paEast.removeAll();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Init und Cleanup können nicht gelöscht werden!");
                }
            }
        }
    }

    private void removeSingleSelection() {
        DefaultMutableTreeNode node
                = (DefaultMutableTreeNode) trFileTree.getSelectionPaths()[0].getLastPathComponent();
        if (node.getUserObject() instanceof ExplorerLayer) {
            paEast.removeAll();
            try {
                if (!(node.getUserObject() instanceof File)) {
                    ExplorerLayer nodeExpl = (ExplorerLayer) node.getUserObject();
                    DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                    if (nodeExpl instanceof TestGroupRun) {
                        ((ProjectRun) parent.getUserObject()).getTestgroups().remove((TestGroupRun) nodeExpl);
                        ExplorerIO.removeNode(nodeExpl.getPath().toFile());
                        ExplorerIO.renameTestGroups(Paths.get(((ExplorerLayer) parent.getUserObject()).getPath().toString(), "run"), getDirectChildren(parent));
                    } else if (nodeExpl instanceof TestCaseRun) {
                        ((TestGroupRun) parent.getUserObject()).getTestCases().remove((TestCaseRun) nodeExpl);
                        ExplorerIO.removeNode(nodeExpl.getPath().toFile());
                        ExplorerIO.renameTestCases(((ExplorerLayer) parent.getUserObject()).getPath(), getDirectChildren(parent));
                    } else if (nodeExpl instanceof CommandRun) {
                        ((TestCaseRun) parent.getUserObject()).getCommands().remove((CommandRun) nodeExpl);
                        ExplorerIO.removeCommandFromXML(Paths.get(nodeExpl.getPath().toString(), "run.xml"), node.getUserObject().toString());
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException | TransformerException | XPathExpressionException ex) {
                ex.printStackTrace();
            }
            etm.removeNodeFromParent(node);
        }
    }

    /**
     * Method that is called when the JMenuItem "Im Simulator abspielen" was
     * clicked in the JPopupMenu
     *
     * @param evt
     */
    private void onSendToSimulator(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onSendToSimulator
        TreePath[] paths = null;
        try {
            paths = trFileTree.getSelectionPaths();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Bitte wählen Sie einen Eintrag im Tree aus!", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<ExplorerLayer> selectedNodes = new ArrayList<>();
        for (TreePath path : paths) {
            selectedNodes.add(((ExplorerLayer) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject()));
        }
        ExecutionManager.getInstance().addTargets(selectedNodes);
        GlobalAccess.getInstance().getTest_ide_main_frame().changeTool("simulator");
    }//GEN-LAST:event_onSendToSimulator

    /**
     * Outsourced method to display the appendant recorder
     */
    public void displayRecorder() {
        DefaultMutableTreeNode node = null;
        try {
            node = (DefaultMutableTreeNode) trFileTree.getSelectionPath().getLastPathComponent();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Bitte wählen Sie einen Eintrag im Tree aus!", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (node.getUserObject() instanceof ExplorerLayer) {
            try {
                ExplorerLayer explo = ((ExplorerLayer) node.getUserObject());
                if (explo instanceof CommandRun) {
                    if ((((CommandRun) explo).getClassName().equals("ExecuteRecorderFileCommand"))) {
                        String rec = ExplorerIO.readRecFromCommand(Paths.get(explo.getPath().toString(), "run.xml"), explo.getDescription()).toLowerCase();
                        Desktop.getDesktop().open(new File(explo.getPath().toString() + File.separator + rec));
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Method to reload entire JTree (f.e. when user wants to reload tree
     * because file structure changed in background)
     *
     * @param evt
     */
    private void onReloadTree(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onReloadTree
        reloadTree();
    }//GEN-LAST:event_onReloadTree

    /**
     * Method to add a new project to the JTree
     *
     * @param evt
     */
    private void onAddProject(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onAddProject
        createProject();
    }//GEN-LAST:event_onAddProject

    /**
     * Method to import the projects with their children into the database
     *
     * @param evt
     */
    private void onImportProject(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onImportProject
//        if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//            try {
//                dashboard.database.DB_Access.getInstance().forcefullyImportProject();
//            } catch (SQLException ex) {
//                ex.printStackTrace();
//            }
//        }
        if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
            DB_Access_Manager.getInstance().importProject(GlobalParamter.getInstance().getWorkingProjects());
        }
    }//GEN-LAST:event_onImportProject

    /**
     * Method to handle "Schließen" on a project on the PopupMenu
     *
     * @param evt
     */
    private void onClose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onClose
        closeProject();
    }//GEN-LAST:event_onClose

    /**
     * Outsourced method to close a project in the JTree until the next reload
     */
    public void closeProject() {
        DefaultMutableTreeNode node = null;
        try {
            node = (DefaultMutableTreeNode) trFileTree.getSelectionPath().getLastPathComponent();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Bitte wählen Sie einen Eintrag im Tree aus!", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (((ExplorerLayer) node.getUserObject()) instanceof ProjectRun) {
            etm.removeNodeFromParent(node);
        }
    }

    /**
     * Outsourced method to create a new project
     *
     * @return
     */
    public boolean createProject() {
        try {
            DefaultMutableTreeNode node = null;
            try {
                node = (DefaultMutableTreeNode) trFileTree.getSelectionPath().getLastPathComponent();
            } catch (NullPointerException ex) {
                node = (DefaultMutableTreeNode) etm.getRoot();
            }
            ExplorerAddProjectDlg tgp = new ExplorerAddProjectDlg(null, true);
            tgp.setVisible(true);
            if (tgp.isOk()) {
                String name = tgp.getName();
                //set path and description with new number
                ProjectRun addPro = new ProjectRun(name, Paths.get(node.getUserObject().toString(), name.toLowerCase()));
                ExplorerIO.addProject(addPro);
                List<ProjectRun> tmpProj = GlobalParamter.getInstance().getWorkingProjects();
                tmpProj.add(addPro);
                GlobalParamter.getInstance().setWorkingProjects(tmpProj);
                //create TreeNode for new TG and take all necessary actions
                DefaultMutableTreeNode projectNode = new DefaultMutableTreeNode(addPro);
                //create JOptionPane to let the user input the bedienernr, the ladennr and the password for init & cleanup
                String[] array = createCustomJOptionPane();
                if (array != null) {
                    createInitAndCleanupForProject(addPro, "Projekt-Initialisierung", "000_INIT", projectNode, array);
                    createInitAndCleanupForProject(addPro, "Projekt-Cleanup", "999_CLEANUP", projectNode, array);
                    //invoke update events
//                ((ProjectRun) node.getUserObject()).getTestgroups().add(((ProjectRun) node.getUserObject()).getTestgroups().size() - 1, addTg);
                    node.add(projectNode);
                    etm.nodeStructureChanged(node);

                    if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
                        Projekt db_pro = new Projekt(addPro.getDescription(), LocalDate.now(), 0);
                        addPro.setDurchlauf_gegenstand(db_pro);
                        DatabaseGlobalAccess.getInstance().getAllProjects().add(db_pro);
                        DatabaseGlobalAccess.getInstance().getEm().persist(db_pro);
                        if (DatabaseGlobalAccess.getInstance().getCurrentNutzer() != null) {
                            DB_Access_Manager.getInstance().addChangeEntry(addPro, "ADDED");
                        } else {
                            DB_Access_Manager.getInstance().updateData();
                            JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Kein Nutzer ausgewählt mit welchem diese Änderung in der Datenbank"
                                    + " assoziert werden kann. Um dies sicherzustellen sollte ein Nutzer in den Einstellungen ausgewählt werden");
                        }
                    }
//
//                    if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                        DB_Access.getInstance().addProject(addPro);
//                        System.out.println("test");
//                        if (GlobalParamter.getInstance().getSelected_user() != null) {
//                            DB_Access.getInstance().addChangeEntry(addPro, ChangeType.ADDED);
//                        } else {
//                            JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Kein Nutzer ausgewählt mit welchem diese Änderung in der Datenbank"
//                                    + " assoziert werden kann. Um dies sicherzustellen sollte ein Nutzer in den Einstellungen ausgewählt werden");
//                        }
//
//                    }
                    return true;
                }
            }
        } catch (IOException | ParserConfigurationException | TransformerException | SAXException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Method to create a custom JOptionPane for entering all necessary
     * information for Init & Cleanup
     *
     * @return
     */
    private String[] createCustomJOptionPane() {
        JTextField empId = new JTextField("202020");
        empId.setFont(DEFAULT_FONT);
        JTextField tllId = new JTextField("12");
        tllId.setFont(DEFAULT_FONT);
        JTextField password = new JTextField("1234");
        password.setFont(DEFAULT_FONT);

        JLabel bed = new JLabel("Bedienernr.:");
        bed.setFont(DEFAULT_BOLD_FONT);
        JLabel lad = new JLabel("Ladennr.:");
        lad.setFont(DEFAULT_BOLD_FONT);
        JLabel pass = new JLabel("Passwort:");
        pass.setFont(DEFAULT_BOLD_FONT);

        JPanel myPanel = new JPanel();
        myPanel.add(bed);
        myPanel.add(empId);
        myPanel.add(Box.createHorizontalStrut(15));
        myPanel.add(lad);
        myPanel.add(tllId);
        myPanel.add(Box.createHorizontalStrut(15));
        myPanel.add(pass);
        myPanel.add(password);

        int result = JOptionPane.showConfirmDialog(null, myPanel,
                "Eingabe notwendig", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String[] array = {empId.getText(), tllId.getText(), password.getText()};
            return array;
        }
        return null;
    }

    /**
     * Method to create the init and cleanup for a new testgroup and adds the
     * objects to the node of the testgroup
     *
     * @param tg
     * @param num
     * @param description
     * @param dirName
     * @param testGroupNode
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws TransformerException
     */
    private void createInitAndCleanupForProject(ProjectRun pro, String description, String dirName, DefaultMutableTreeNode projNode, String[] array)
            throws ParserConfigurationException, SAXException, IOException, TransformerException {
        //create init & cleanup for new TG
        TestGroupRun tg = new TestGroupRun(pro.getDescription().trim() + " - " + description, Paths.get(pro.getPath().toString(), "run", dirName));
        tg.setEmpId(Integer.parseInt(array[0]));
        tg.setTllId(Integer.parseInt(array[1]));
        tg.setPassword(Integer.parseInt(array[2]));
        ExplorerIO.addInitOrCleanupToNewProject(tg);
        pro.getTestgroups().add(tg);
        projNode.add(new DefaultMutableTreeNode(tg));
    }

    /**
     * Outsourced method to reload the JTree and remove all panels on the right
     * side
     */
    public void reloadTree() {
        trFileTree.setSelectionRow(-1);
        paEast.removeAll();
        paEast.revalidate();
        paEast.updateUI();
        updateTree();
        etm.updateStructure(root);
    }

    /**
     * Outsourced method to display the appendant run.xml
     */
    public void displayRun() {
        DefaultMutableTreeNode node = null;
        try {
            node = (DefaultMutableTreeNode) trFileTree.getSelectionPath().getLastPathComponent();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Bitte wählen Sie einen Eintrag im Tree aus!", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (node.getUserObject() instanceof ExplorerLayer) {
            try {
                ExplorerLayer explo = ((ExplorerLayer) node.getUserObject());
                if (explo instanceof CommandRun) {
                    Desktop.getDesktop().open(new File(explo.getPath().toString() + File.separator + "run.xml"));
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btReload;
    private javax.swing.JButton btReload1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JMenuItem miAddCom;
    private javax.swing.JMenuItem miAddProject;
    private javax.swing.JMenuItem miAddTC;
    private javax.swing.JMenuItem miAddTG;
    private javax.swing.JMenuItem miClose;
    private javax.swing.JMenuItem miRemove;
    private javax.swing.JMenuItem miRename;
    private javax.swing.JMenuItem miSimulator;
    private javax.swing.JPanel paEast;
    private javax.swing.JPanel paExplorer;
    private javax.swing.JPanel paRight;
    private javax.swing.JPanel paSouth;
    private javax.swing.JPopupMenu pmMenu;
    private javax.swing.JScrollPane spTree;
    private javax.swing.JTree trFileTree;
    // End of variables declaration//GEN-END:variables
}

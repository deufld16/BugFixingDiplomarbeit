/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package explorer.bl;

import dashboard.beans.ChangeNew;
import dashboard.bl.DatabaseGlobalAccess;
import dashboard.database.DB_Access_Manager;
import dashboard.enums.ChangeType;
import general.beans.io_objects.CommandRun;
import general.beans.io_objects.ExplorerLayer;
import general.beans.io_objects.ProjectRun;
import general.beans.io_objects.TestCaseRun;
import general.beans.io_objects.TestGroupRun;
import general.bl.GlobalAccess;
import general.bl.GlobalParamter;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import static javax.swing.TransferHandler.COPY_OR_MOVE;
import static javax.swing.TransferHandler.MOVE;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class which is resposible for handling all kind of movements of the TGs, TCs
 * as well as commands
 *
 * @author Florian Deutschmann
 */
public class ExplorerTransferHandler extends TransferHandler {

    DataFlavor nodesFlavor;
    DataFlavor[] flavors = new DataFlavor[1];
    DefaultMutableTreeNode[] nodesToRemove;
    private DefaultMutableTreeNode new_parent;
    private String transferType;
    private TreeNode child;
    private Node movedNode;

    public ExplorerTransferHandler() {
        try {
            String mimeType = DataFlavor.javaJVMLocalObjectMimeType
                    + ";class=\""
                    + javax.swing.tree.DefaultMutableTreeNode[].class.getName()
                    + "\"";
            nodesFlavor = new DataFlavor(mimeType);
            flavors[0] = nodesFlavor;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method which checks weather or not the location where the new data should
     * be dropped is allowed
     *
     * @param support
     * @return
     */
    public boolean canImport(TransferHandler.TransferSupport support) {
        if (!support.isDrop()) {
            return false;
        }
        support.setShowDropLocation(true);
        if (!support.isDataFlavorSupported(nodesFlavor)) {
            return false;
        }

        JTree.DropLocation dl
                = (JTree.DropLocation) support.getDropLocation();
        JTree tree = (JTree) support.getComponent();
        int dropRow = tree.getRowForPath(dl.getPath());
        int[] selRows = tree.getSelectionRows();
        for (int i = 0; i < selRows.length; i++) {
            if (selRows[i] == dropRow) {
                return false;
            }
        }

        int action = support.getDropAction();

        if (action == MOVE) {
            return isMoveable(tree, support);
        }
        return true;
    }

    /**
     * Submethod of canImport checks if the current intended move process is
     * allowed. All kind of not allowed movement scenarios like move a TC before
     * the init TC or moving a TG into a TC are prohibited in this method
     *
     * @param tree
     * @param support
     * @return
     */
    private boolean isMoveable(JTree tree, TransferHandler.TransferSupport support) {
        int[] selRows = tree.getSelectionRows();

        TreePath path = tree.getPathForRow(selRows[0]);
        DefaultMutableTreeNode first
                = (DefaultMutableTreeNode) path.getLastPathComponent();
        JTree.DropLocation dl
                = (JTree.DropLocation) support.getDropLocation();
        TreePath dest = dl.getPath();
        DefaultMutableTreeNode destination_node
                = (DefaultMutableTreeNode) dest.getLastPathComponent();
        DefaultMutableTreeNode source_node
                = (DefaultMutableTreeNode) path.getLastPathComponent();
        if ((destination_node.getLevel() == 1 && destination_node.getIndex((TreeNode) first) == -1) || destination_node.getLevel() == 0) {
            return false;
        }
        if (selRows.length > 1) {
            return false;
        }
        if (destination_node.getLevel() + 1 != source_node.getLevel()) {
            return false;
        }

        if (first.getUserObject() instanceof ProjectRun) {
            return false;
        }

        if (dl.getChildIndex() == source_node.getParent().getIndex(source_node)) {
            return false;
        }

        if (dl.getChildIndex() == 0) {
            if (((ExplorerLayer) source_node.getUserObject()) instanceof CommandRun) {
            } else {
                return false;
            }
        }

        if (dl.getPath().equals(path.getParentPath()) && dl.getChildIndex() == -1) {
            return false;
        }

        if (((ExplorerLayer) source_node.getUserObject()).getPath().toFile().getName().contains("INIT")
                || ((ExplorerLayer) source_node.getUserObject()).getPath().toFile().getName().contains("CLEANUP")) {
            if (((ExplorerLayer) source_node.getUserObject()) instanceof CommandRun || ((ExplorerLayer) source_node.getUserObject()) instanceof TestGroupRun) {
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * Method which is responsible for creating a List with the nodes that
     * should be moved as well as a list of nodes that cannot be moved
     *
     * @param c
     * @return
     */
    protected Transferable createTransferable(JComponent c) {
        JTree tree = (JTree) c;
        TreePath[] paths = tree.getSelectionPaths();
        if (paths != null) {
            List<DefaultMutableTreeNode> copies
                    = new ArrayList<DefaultMutableTreeNode>();
            List<DefaultMutableTreeNode> toRemove
                    = new ArrayList<DefaultMutableTreeNode>();
            DefaultMutableTreeNode node
                    = (DefaultMutableTreeNode) paths[0].getLastPathComponent();
            DefaultMutableTreeNode copy = copy(node);
            if (node.getUserObject() instanceof TestCaseRun) {
                for (int i = 0; i < node.getChildCount(); i++) {
                    copy.add(copy(node.getChildAt(i)));
                }
            } else if (node.getUserObject() instanceof TestGroupRun) {
                for (DefaultMutableTreeNode direct_child : getDirectChildrend(node)) {
                    DefaultMutableTreeNode help_copy = copy(direct_child);
                    for (int i = 0; i < direct_child.getChildCount(); i++) {
                        help_copy.add(copy((direct_child.getChildAt(i))));
                    }
                    copy.add(help_copy);
                }
            }
            copies.add(copy);
            toRemove.add(node);

            DefaultMutableTreeNode[] nodes
                    = copies.toArray(new DefaultMutableTreeNode[copies.size()]);
            nodesToRemove
                    = toRemove.toArray(new DefaultMutableTreeNode[toRemove.size()]);
            return new NodesTransferable(nodes);
        }
        return null;
    }

    /**
     * Method that is responsible for extracting as well as inserting the moved
     * nodes into the right place in the JTree
     *
     * @param support
     * @return
     */
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }
        DefaultMutableTreeNode[] nodes = null;
        try {
            Transferable t = support.getTransferable();
            nodes = (DefaultMutableTreeNode[]) t.getTransferData(nodesFlavor);
            child = nodes[0];
        } catch (UnsupportedFlavorException ufe) {
            ufe.printStackTrace();
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        }
        JTree.DropLocation dl
                = (JTree.DropLocation) support.getDropLocation();
        int childIndex = dl.getChildIndex();
        TreePath dest = dl.getPath();
        DefaultMutableTreeNode parent
                = (DefaultMutableTreeNode) dest.getLastPathComponent();
        JTree tree = (JTree) support.getComponent();
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        int index = childIndex;
        if (childIndex == -1) {
            index = parent.getChildCount();
        }
        DefaultMutableTreeNode destination_node = (DefaultMutableTreeNode) dl.getPath().getLastPathComponent();
        if (nodes[0].getUserObject() instanceof CommandRun) {

        } else if (nodes[0].getUserObject() instanceof TestGroupRun) {
        } else {
            if (index == new File(((ExplorerLayer) destination_node.getUserObject()).getPath().toString()).listFiles(File::isDirectory).length) {
                index = new File(((ExplorerLayer) destination_node.getUserObject()).getPath().toString()).listFiles(File::isDirectory).length - 1;
            }
        }
        for (int i = 0; i < nodes.length; i++) {
            model.insertNodeInto(nodes[i], parent, index++);
        }
        onMove(support);
        return true;
    }

    /**
     * This is the method which makes sure, that everything that is moved in the
     * JTree also happens in the background in the file system. This method f.e.
     * renames all folders accordingly if they are moved and also makes sure
     * that the beans structure which is mapped with the nodes of the JTree is
     * addapted (new Paths, descriptions, etc...) as needed
     *
     * @param support
     * @return
     */
    private boolean onMove(TransferHandler.TransferSupport support) {
        JTree tree = (JTree) support.getComponent();
        TreePath[] paths = tree.getSelectionPaths();
        JTree.DropLocation dl
                = (JTree.DropLocation) support.getDropLocation();
        TreePath destination_path = dl.getPath();

        if (paths != null) {
            DefaultMutableTreeNode source_node
                    = (DefaultMutableTreeNode) paths[0].getLastPathComponent();
            DefaultMutableTreeNode desination_node
                    = (DefaultMutableTreeNode) destination_path.getLastPathComponent();

            if (source_node.getUserObject() instanceof CommandRun) {
//                if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                    if (GlobalParamter.getInstance().getSelected_user() == null) {
//                        JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Kein Nutzer ausgewählt, mit welchem diese Änderung in der Datenbank"
//                                + " assoziiert werden kann.\nUm dies sicherzustellen, sollte ein Nutzer in den Einstellungen ausgewählt werden.");
//                    }
//                }
                if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
                    if (DatabaseGlobalAccess.getInstance().getCurrentNutzer() == null) {
                        JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Kein Nutzer ausgewählt, mit welchem diese Änderung in der Datenbank"
                                + " assoziiert werden kann.\nUm dies sicherzustellen, sollte ein Nutzer in den Einstellungen ausgewählt werden.");
                    }
                }
                transferType = "commandRun";
                new_parent = desination_node;
                String descOld = ((ExplorerLayer) source_node.getUserObject()).getDescription();
                Path pathOfXMLToRemove = Paths.get(((ExplorerLayer) source_node.getUserObject()).getPath().toString(), "run.xml");
                try {
                    if (!source_node.getParent().equals(desination_node)) {
                        movedNode = explorer.io.ExplorerIO.removeFromXML(pathOfXMLToRemove, source_node.getParent().getIndex(source_node));
                        NodeList movedNode_children = movedNode.getChildNodes();
                        if (movedNode_children.getLength() != 0) {
                            for (int i = 0; i < movedNode_children.getLength(); i++) {
                                Node tmp_node = movedNode_children.item(i);
                                if (tmp_node.getNodeName().equalsIgnoreCase("pathToXML") || tmp_node.getNodeName().equalsIgnoreCase("file")) {
                                    Path sourcePath = Paths.get(((ExplorerLayer) source_node.getUserObject()).getPath().toString(), tmp_node.getTextContent().toLowerCase());
                                    Path destPath = Paths.get(((ExplorerLayer) new_parent.getUserObject()).getPath().toString(), tmp_node.getTextContent().toLowerCase());
                                    explorer.io.ExplorerIO.moveAssociatedFiles(destPath, sourcePath, tmp_node.getTextContent());
                                }
                            }
                        }
                        ((ExplorerLayer) source_node.getUserObject()).setPath(((ExplorerLayer) desination_node.getUserObject()).getPath());

                        //if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
                        if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
//                            dashboard.database.DB_Access.getInstance().updateEntry((ExplorerLayer) source_node.getUserObject(),
//                                    (ExplorerLayer) ((DefaultMutableTreeNode) source_node.getParent()).getUserObject(), descOld);
                            //if (GlobalParamter.getInstance().getSelected_user() != null) {
                            if (DatabaseGlobalAccess.getInstance().getCurrentNutzer() != null) {
                                DB_Access_Manager.getInstance().addChangeEntry((ExplorerLayer) source_node.getUserObject(), "MOVED");
                                DB_Access_Manager.getInstance().addChangeEntry((ExplorerLayer) desination_node.getUserObject(), "STATECHANGED");
                                DB_Access_Manager.getInstance().addChangeEntry((ExplorerLayer) ((DefaultMutableTreeNode) source_node.getParent()).getUserObject(),
                                        "STATECHANGED");
//                                dashboard.database.DB_Access.getInstance().addChangeEntry((ExplorerLayer) source_node.getUserObject(), ChangeType.MOVED);
//                                dashboard.database.DB_Access.getInstance().addChangeEntry((ExplorerLayer) desination_node.getUserObject(), ChangeType.CHILD_STATECHANGED);
//                                dashboard.database.DB_Access.getInstance().addChangeEntry((ExplorerLayer) ((DefaultMutableTreeNode) source_node.getParent()).getUserObject(), ChangeType.CHILD_STATECHANGED);
                            }
//                            dashboard.database.DB_Access.getInstance().updateEntry((ExplorerLayer) source_node.getUserObject(),
//                                    (ExplorerLayer) ((DefaultMutableTreeNode) desination_node).getUserObject(), descOld);
                        }
                        ((TestCaseRun) ((DefaultMutableTreeNode) source_node.getParent()).getUserObject()).getCommands().remove((CommandRun) source_node.getUserObject());
                        ((TestCaseRun) desination_node.getUserObject()).getCommands().add((CommandRun) source_node.getUserObject());

                    } else {
                        int index_before = source_node.getParent().getIndex(source_node);
                        int index_after = dl.getChildIndex();
                        if (index_before > index_after) {
                            index_before--;
                        }

                        if (index_after > index_before) {
                            index_after--;
                        }
                        explorer.io.ExplorerIO.createXMLFile(index_before, index_after, pathOfXMLToRemove);
//                        if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                            if (GlobalParamter.getInstance().getSelected_user() != null) {
//                                dashboard.database.DB_Access.getInstance().addChangeEntry((ExplorerLayer) source_node.getUserObject(), ChangeType.CHANGED);
//                            }
//                        }
                        if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
                            if (DatabaseGlobalAccess.getInstance().getCurrentNutzer() != null) {
                                DB_Access_Manager.getInstance().addChangeEntry((ExplorerLayer) source_node.getUserObject(), "CHANGED");
                            }
                        }
                    }
                } catch (ParserConfigurationException ex) {
                    ex.printStackTrace();
                } catch (SAXException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (TransformerException ex) {
                    ex.printStackTrace();
                }
            } else if (source_node.getUserObject() instanceof TestCaseRun) {
                DefaultMutableTreeNode tmp = (DefaultMutableTreeNode) tree.getSelectionPaths()[0].getLastPathComponent();
                transferType = "testCaseRun";
                try {
                    int index = desination_node.getIndex(child);
//                    if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                        if (GlobalParamter.getInstance().getSelected_user() == null) {
//                            JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Kein Nutzer ausgewählt mit welchem diese Änderung in der Datenbank"
//                                    + " assoziert werden kann. Um dies sicherzustellen sollte ein Nutzer in den Einstellungen ausgewählt werden");
//                        }
//                    }
                    if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
                        if (DatabaseGlobalAccess.getInstance().getCurrentNutzer() == null) {
                            JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Kein Nutzer ausgewählt, mit welchem diese Änderung in der Datenbank"
                                    + " assoziiert werden kann.\nUm dies sicherzustellen, sollte ein Nutzer in den Einstellungen ausgewählt werden.");
                        }
                    }
                    if (desination_node.getIndex(child) == new File(((ExplorerLayer) desination_node.getUserObject()).getPath().toString()).listFiles(File::isDirectory).length) {
                        index = new File(((ExplorerLayer) desination_node.getUserObject()).getPath().toString()).listFiles(File::isDirectory).length - 1;
                    }

                    if (desination_node.getIndex(child) == 0) {
                        index = 0;
                    }

                    if (index == 0) {
                        JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "TestCase kann nicht vor die Initalisierung der Testgruppe geschoben werden", "Informationsmeldung", JOptionPane.INFORMATION_MESSAGE);
                        return false;
                    } else {

                        if (desination_node.equals((DefaultMutableTreeNode) source_node.getParent())) {
                            int index_before = source_node.getParent().getIndex(source_node);
                            List<DefaultMutableTreeNode> listeAllerNodes = getDirectChildrend((DefaultMutableTreeNode) source_node.getParent());

                            listeAllerNodes.remove(index);

                            if (index_before < index) {
                                index--;
                            }

                            explorer.io.ExplorerIO.transferInSameFolder(index_before, index,
                                    ((ExplorerLayer) ((DefaultMutableTreeNode) source_node.getParent()).getUserObject()).getPath(),
                                    listeAllerNodes);
                            //dashboard.database.DB_Access.getInstance().addChangeEntry((TestCaseRun) source_node.getUserObject(), ChangeType.MOVED);
                            if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
                                if (DatabaseGlobalAccess.getInstance().getCurrentNutzer() != null) {
                                    DB_Access_Manager.getInstance().addChangeEntry((TestCaseRun) source_node.getUserObject(), "MOVED");
                                }
                            }
                        } else {
                            explorer.io.ExplorerIO.prepareFolderForTransfer(index, ((ExplorerLayer) desination_node.getUserObject()).getPath(), getDirectChildrend(desination_node));
                            Files.move(((ExplorerLayer) source_node.getUserObject()).getPath(), Paths.get(((ExplorerLayer) desination_node.getUserObject()).getPath().toString(),
                                    String.format("%03d_TC", index)));
                            List<DefaultMutableTreeNode> allNodes = getDirectChildrend((DefaultMutableTreeNode) source_node.getParent());
                            allNodes.remove(source_node);
                            explorer.io.ExplorerIO.renameTestCases(((ExplorerLayer) source_node.getUserObject()).getPath().getParent(), allNodes);

                            ExplorerLayer beans_obj_moved = (ExplorerLayer) tmp.getUserObject();
                            beans_obj_moved.setPath(Paths.get(((ExplorerLayer) desination_node.getUserObject()).getPath().toString(),
                                    String.format("%03d_TC", index)));
                            explorer.io.ExplorerIO.changeTCDescription(Paths.get(((ExplorerLayer) source_node.getUserObject()).getPath().toString(), "run.xml"),
                                    (TestCaseRun) source_node.getUserObject(), index);

//                            if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                                if (GlobalParamter.getInstance().getSelected_user() != null) {
//                                    dashboard.database.DB_Access.getInstance().addChangeEntry((TestCaseRun) source_node.getUserObject(), ChangeType.MOVED);
//                                    dashboard.database.DB_Access.getInstance().addChangeEntry((TestGroupRun) ((DefaultMutableTreeNode) source_node.getParent()).getUserObject(),
//                                            ChangeType.CHILD_STATECHANGED);
//                                    dashboard.database.DB_Access.getInstance().addChangeEntry((TestGroupRun) desination_node.getUserObject(), ChangeType.CHILD_STATECHANGED);
//                                }
//                            }
                            if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
//                            dashboard.database.DB_Access.getInstance().updateEntry((ExplorerLayer) source_node.getUserObject(),
//                                    (ExplorerLayer) ((DefaultMutableTreeNode) source_node.getParent()).getUserObject(), descOld);
                                //if (GlobalParamter.getInstance().getSelected_user() != null) {
                                if (DatabaseGlobalAccess.getInstance().getCurrentNutzer() != null) {
                                    DB_Access_Manager.getInstance().addChangeEntry((TestCaseRun) source_node.getUserObject(), "MOVED");
                                    DB_Access_Manager.getInstance().addChangeEntry((TestGroupRun) ((DefaultMutableTreeNode) source_node.getParent()).getUserObject(), "STATECHANGED");
                                    DB_Access_Manager.getInstance().addChangeEntry((TestGroupRun) desination_node.getUserObject(), "STATECHANGED");
//                                dashboard.database.DB_Access.getInstance().addChangeEntry((ExplorerLayer) source_node.getUserObject(), ChangeType.MOVED);
//                                dashboard.database.DB_Access.getInstance().addChangeEntry((ExplorerLayer) desination_node.getUserObject(), ChangeType.CHILD_STATECHANGED);
//                                dashboard.database.DB_Access.getInstance().addChangeEntry((ExplorerLayer) ((DefaultMutableTreeNode) source_node.getParent()).getUserObject(), ChangeType.CHILD_STATECHANGED);
                                }
//                            dashboard.database.DB_Access.getInstance().updateEntry((ExplorerLayer) source_node.getUserObject(),
//                                    (ExplorerLayer) ((DefaultMutableTreeNode) desination_node).getUserObject(), descOld);
                            }
                            if (((TestGroupRun) desination_node.getUserObject()).getTestCases()
                                    .add((TestCaseRun) source_node.getUserObject())) {
                            }
                            if (((TestGroupRun) ((DefaultMutableTreeNode) source_node.getParent()).getUserObject()).getTestCases()
                                    .remove((TestCaseRun) source_node.getUserObject())) {
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (source_node.getUserObject() instanceof TestGroupRun) {
//                if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                    if (GlobalParamter.getInstance().getSelected_user() == null) {
//                        JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Kein Nutzer ausgewählt mit welchem diese Änderung in der Datenbank"
//                                + " assoziert werden kann. Um dies sicherzustellen sollte ein Nutzer in den Einstellungen ausgewählt werden");
//                    }
//                }
                if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
                    if (DatabaseGlobalAccess.getInstance().getCurrentNutzer() == null) {
                        JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Kein Nutzer ausgewählt, mit welchem diese Änderung in der Datenbank"
                                + " assoziiert werden kann.\nUm dies sicherzustellen, sollte ein Nutzer in den Einstellungen ausgewählt werden.");
                    }
                }
                transferType = "testGroupRun";
                int index_before = source_node.getParent().getIndex(source_node);
                int index_after = dl.getChildIndex();

                List<DefaultMutableTreeNode> listeAllerNodes = getDirectChildrend((DefaultMutableTreeNode) source_node.getParent());

                listeAllerNodes.remove(index_after);
                if (index_after > index_before) {
                    index_after--;
                }
                explorer.io.ExplorerIO.renameAfterMovingTG(index_before, index_after, Paths.get(((ExplorerLayer) desination_node.getUserObject()).getPath().toString(), "run"),
                        listeAllerNodes);

//                if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                    if (GlobalParamter.getInstance().getSelected_user() != null) {
//                        try {
//                            dashboard.database.DB_Access.getInstance().addChangeEntry((ExplorerLayer) source_node.getUserObject(), ChangeType.MOVED);
//                        } catch (SQLException ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//                }
                if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
                    if (DatabaseGlobalAccess.getInstance().getCurrentNutzer() != null) {
                        DB_Access_Manager.getInstance().addChangeEntry((ExplorerLayer) source_node.getUserObject(), "MOVED");
                    }
                }
            }
        }
        if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
            DB_Access_Manager.getInstance().updateData();
        }
        return true;
    }

    /**
     * Method which returns all direct children of a parentNode.
     *
     * @param parent
     * @return List of DefaultMuteableTreeNode
     */
    private List<DefaultMutableTreeNode> getDirectChildrend(DefaultMutableTreeNode parent) {
        List<DefaultMutableTreeNode> allNodes = new LinkedList<>();
        DefaultMutableTreeNode copy = null;
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (((DefaultMutableTreeNode) parent.getChildAt(i)).getLevel() - 1 == parent.getLevel()) {

                allNodes.add(((DefaultMutableTreeNode) parent.getChildAt(i)));
            }
        }
        return allNodes;
    }

    /**
     * Defensive copy used in createTransferable.
     */
    private DefaultMutableTreeNode copy(TreeNode node) {
        return new DefaultMutableTreeNode(((DefaultMutableTreeNode) node).getUserObject());
    }

    /**
     * This method is being used to remove the nodes that should be removed
     * after the movement action has sucesfully finished This method also makes
     * some minor adjustment in the acutal filesystem if a command is being
     * moved
     *
     * @param source
     * @param data
     * @param action
     */
    protected void exportDone(JComponent source, Transferable data, int action) {
        if ((action & MOVE) == MOVE) {
            JTree tree = (JTree) source;
            DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
            // Remove nodes saved in nodesToRemove in createTransferable.
            for (int i = 0; i < nodesToRemove.length; i++) {
                model.removeNodeFromParent(nodesToRemove[i]);
            }

            if (transferType.equalsIgnoreCase("commandRun")) {
                Path pathOfXMLToAdd = Paths.get(((ExplorerLayer) new_parent.getUserObject()).getPath().toString(), "run.xml");
                try {
                    if (movedNode != null) {
                        explorer.io.ExplorerIO.createXMLFile(new_parent.getIndex(child), pathOfXMLToAdd, movedNode);
                    } else {
                    }
                } catch (SAXException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (ParserConfigurationException ex) {
                    ex.printStackTrace();
                } catch (TransformerException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    /**
     * Inner class which is being used to prepare and create the Data that
     * should be moved/transfered
     */
    public class NodesTransferable implements Transferable {

        DefaultMutableTreeNode[] nodes;

        public NodesTransferable(DefaultMutableTreeNode[] nodes) {
            this.nodes = nodes;
        }

        public Object getTransferData(DataFlavor flavor)
                throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return nodes;
        }

        public DataFlavor[] getTransferDataFlavors() {
            return flavors;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return nodesFlavor.equals(flavor);
        }
    }

}

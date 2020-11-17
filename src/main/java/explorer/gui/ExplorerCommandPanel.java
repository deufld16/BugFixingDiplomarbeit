/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package explorer.gui;

import dashboard.beans.ChangeNew;
import dashboard.bl.DatabaseGlobalAccess;
import dashboard.database.DB_Access;
import dashboard.database.DB_Access_Manager;
import dashboard.enums.ChangeType;
import explorer.bl.ExplorerTreeModel;
import explorer.io.ExplorerIO;
import general.beans.io_objects.CommandRun;
import general.bl.GlobalAccess;
import general.bl.GlobalParamter;
import general.io.Mapper;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author Anna Lechner
 */
public class ExplorerCommandPanel extends javax.swing.JPanel {

    private static final Font DEFAULT_FONT = new Font("Tahoma", Font.PLAIN, 18);
    private static final Font DEFAULT_BOLD_FONT = new Font("Tahoma", Font.BOLD, 18);
    private CommandRun com;
    private String regEx = "[0-9]+_[0-9]+_[0-9]+";
    private ExplorerTreeModel etm;
    private NodeList nodeList;
    private String commandText;

    /**
     * Creates new form ExplorerCommandPanel
     *
     * @param com
     * @param etm
     */
    public ExplorerCommandPanel(CommandRun com, ExplorerTreeModel etm) {
        try {
            initComponents();
            this.com = com;
            this.etm = etm;
//        nodeList = com.getNodeList();
            nodeList = ExplorerIO.getCurrentNodeList(Paths.get(com.getPath().toString(), "run.xml"), com.getDescription());
            setObjectValuesOnTf();
        } catch (SAXException | ParserConfigurationException | IOException | XPathExpressionException ex) {
            ex.printStackTrace();
        } catch(NullPointerException e){
            removeAll();
            revalidate();
            updateUI();
            GlobalAccess.getInstance().getPaExplorer().clearRightPanel();
        }
    }

    private void setObjectValuesOnTf() {
        tfCommand.setText(com.getDisplayName() + " (" + com.getClassName() + ")");
        loadDynamicSubnodes();
    }

    private void loadDynamicSubnodes() {
        try {
            Mapper.mapCommandsToBeans(GlobalParamter.getInstance().getGeneralCommandsPath());
            GridLayout gridLeft = (GridLayout) paLeft.getLayout();
            GridLayout gridRight = (GridLayout) paRight.getLayout();
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (!nodeList.item(i).getNodeName().equals("#text")) {
                    boolean editable = false;
                    Node node = nodeList.item(i);
                    if (node.getNodeName().equals("command_text")) {
                        editable = true;
                        commandText = node.getTextContent();
                    }
                    gridLeft.setRows(gridLeft.getRows() + 1);
                    paLeft.add(createNewComponent("JLabel", ((Element) node).getAttribute("displayname")));
                    paRight.add(createNewComponent("JTextField", node.getTextContent(), editable));
                    gridRight.setRows(gridRight.getRows() + 1);
                }
            }
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            ex.printStackTrace();
        }
    }

    private JComponent createNewComponent(String component, String text, boolean... editable) {
        if (component.equalsIgnoreCase("JLabel")) {
            JLabel label = new JLabel();
            label.setFont(DEFAULT_BOLD_FONT);
            label.setText(text);
            return label;
        } else {
            JTextField tf = new JTextField();
            tf.setFont(DEFAULT_FONT);
            tf.setText(text);
            tf.setEditable(editable[0]);
            return tf;
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

        paLeft = new javax.swing.JPanel();
        lbCommand = new javax.swing.JLabel();
        paRight = new javax.swing.JPanel();
        tfCommand = new javax.swing.JTextField();
        paSouth = new javax.swing.JPanel();
        btOk = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new java.awt.BorderLayout());

        paLeft.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        paLeft.setPreferredSize(new java.awt.Dimension(150, 0));
        paLeft.setLayout(new java.awt.GridLayout(1, 0, 0, 5));

        lbCommand.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbCommand.setText("Command");
        paLeft.add(lbCommand);

        add(paLeft, java.awt.BorderLayout.WEST);

        paRight.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        paRight.setLayout(new java.awt.GridLayout(1, 0, 0, 5));

        tfCommand.setEditable(false);
        tfCommand.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        paRight.add(tfCommand);

        add(paRight, java.awt.BorderLayout.CENTER);

        paSouth.setLayout(new java.awt.GridLayout(1, 2, 10, 10));

        btOk.setFont(new java.awt.Font("Tahoma", 1, 17)); // NOI18N
        btOk.setText("OK");
        btOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onOk(evt);
            }
        });
        paSouth.add(btOk);

        btCancel.setFont(new java.awt.Font("Tahoma", 1, 17)); // NOI18N
        btCancel.setText("Änderungen verwerfen");
        btCancel.setBorder(javax.swing.BorderFactory.createEmptyBorder(40, 1, 40, 1));
        btCancel.setPreferredSize(new java.awt.Dimension(49, 35));
        btCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onCancel(evt);
            }
        });
        paSouth.add(btCancel);

        add(paSouth, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Method that is called when the user clicks on the "Cancel" button
     *
     * @param evt
     */
    private void onCancel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onCancel
        for (int i = 0; i < paRight.getComponents().length; i++) {
            if (((JLabel) paLeft.getComponents()[i]).getText().equals("Beschreibung")) {
                ((JTextField) paRight.getComponents()[i]).setText(commandText);
            }
        }
        updateUI();
    }//GEN-LAST:event_onCancel

    /**
     * Method that is called when the user clicks on the "OK" button
     *
     * @param evt
     */
    private void onOk(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onOk
        try {
            File toBeRenamed = com.getPath().toFile();
            String newName = "";
            for (int i = 0; i < paLeft.getComponents().length; i++) {
                if (((JLabel) paLeft.getComponents()[i]).getText().equals("Beschreibung")) {
                    newName = ((JTextField) paRight.getComponents()[i]).getText();
                }
            }
            if (newName.trim() != null && !newName.trim().isEmpty() && !newName.equals(commandText)) {
                ExplorerIO.changeCommandText(Paths.get(toBeRenamed.toString(), "run.xml"), commandText, newName);
                String oldDescription = com.getDescription();
                com.setDescription(newName);
//                if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                    DB_Access.getInstance().updateEntry(com, oldDescription);
//                    DB_Access.getInstance().addChangeEntry(com, ChangeType.CHANGED);
//                }
                System.out.println("ich bin hier");
                if(DatabaseGlobalAccess.getInstance().isDbReachable()){
                    for (dashboard.beans.ChangeType type : DatabaseGlobalAccess.getInstance().getAllChangeTypes()) {
                        if(type.getBezeichnung() == "CHANGED"){
                            ChangeNew change = new ChangeNew(LocalDateTime.now());
                            change.setChangeType(type);
                            change.setGegenstand(com.getDurchlauf_gegenstand());
                            DB_Access_Manager.getInstance().updateData();
                        }
                    }
                }
                nodeList = ExplorerIO.getCurrentNodeList(Paths.get(com.getPath().toString(), "run.xml"), com.getDescription());
                commandText = newName;
                etm.fireTreeStructureChanged();
            }else{
                System.out.println("neu");
            }
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException | TransformerException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_onOk


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btOk;
    private javax.swing.JLabel lbCommand;
    private javax.swing.JPanel paLeft;
    private javax.swing.JPanel paRight;
    private javax.swing.JPanel paSouth;
    private javax.swing.JTextField tfCommand;
    // End of variables declaration//GEN-END:variables
}

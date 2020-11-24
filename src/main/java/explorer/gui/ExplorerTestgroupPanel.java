/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package explorer.gui;

import dashboard.bl.DatabaseGlobalAccess;
import dashboard.database.DB_Access_Manager;
import explorer.bl.ExplorerTreeModel;
import explorer.io.ExplorerIO;
import general.beans.io_objects.TestGroupRun;
import general.bl.GlobalAccess;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;

/**
 *
 * @author Anna Lechner
 */
public class ExplorerTestgroupPanel extends javax.swing.JPanel {

    private TestGroupRun tg;
    private String regEx = "Testgruppe_[0-9]+ - ";
    private ExplorerTreeModel etm;

    /**
     * Creates new form ExplorerTestgroupPanel
     *
     * @param testGroup
     * @param etm
     */
    public ExplorerTestgroupPanel(TestGroupRun testGroup, ExplorerTreeModel etm) {
        initComponents();
        this.tg = testGroup;
        this.etm = etm;
        setObjectValuesOnTf();
    }

    /**
     * Method to add all passed object values on the JTextFields
     */
    private void setObjectValuesOnTf() {
        tfGroupName.setText(tg.getDescription().replaceAll(regEx, ""));
        tfEmpId.setText(tg.getEmpId() + "");
        tfTllId.setText(tg.getTllId() + "");
        tfPassword.setText(tg.getPassword() + "");
        if (tfGroupName.getText().contains("Init") || tfGroupName.getText().contains("Cleanup")) {
            tfGroupName.setEditable(false);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paLeft = new javax.swing.JPanel();
        lbGroupName = new javax.swing.JLabel();
        lbEmpId = new javax.swing.JLabel();
        lbTllId = new javax.swing.JLabel();
        lbPassword = new javax.swing.JLabel();
        paRight = new javax.swing.JPanel();
        tfGroupName = new javax.swing.JTextField();
        tfEmpId = new javax.swing.JTextField();
        tfTllId = new javax.swing.JTextField();
        tfPassword = new javax.swing.JTextField();
        paSouth = new javax.swing.JPanel();
        btOk = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new java.awt.BorderLayout());

        paLeft.setPreferredSize(new java.awt.Dimension(150, 290));
        paLeft.setLayout(new java.awt.GridLayout(4, 0, 0, 5));

        lbGroupName.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbGroupName.setText("Groupname:");
        lbGroupName.setPreferredSize(new java.awt.Dimension(0, 40));
        paLeft.add(lbGroupName);

        lbEmpId.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbEmpId.setText("Bedienernr:");
        lbEmpId.setPreferredSize(new java.awt.Dimension(0, 40));
        paLeft.add(lbEmpId);

        lbTllId.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbTllId.setText("Ladennr:");
        lbTllId.setPreferredSize(new java.awt.Dimension(0, 40));
        paLeft.add(lbTllId);

        lbPassword.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbPassword.setText("Passwort:");
        lbPassword.setPreferredSize(new java.awt.Dimension(0, 40));
        paLeft.add(lbPassword);

        add(paLeft, java.awt.BorderLayout.WEST);

        paRight.setLayout(new java.awt.GridLayout(4, 0, 0, 5));

        tfGroupName.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tfGroupName.setMinimumSize(new java.awt.Dimension(6, 40));
        paRight.add(tfGroupName);

        tfEmpId.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tfEmpId.setMinimumSize(new java.awt.Dimension(6, 40));
        paRight.add(tfEmpId);

        tfTllId.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tfTllId.setMinimumSize(new java.awt.Dimension(6, 40));
        paRight.add(tfTllId);

        tfPassword.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tfPassword.setMinimumSize(new java.awt.Dimension(6, 40));
        paRight.add(tfPassword);

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
        setObjectValuesOnTf();
    }//GEN-LAST:event_onCancel

    /**
     * Method that is called when the user clicks on the "OK" button
     *
     * @param evt
     */
    private void onOk(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onOk
        try {
            File toBeRenamed = null;
            if (!tfGroupName.getText().contains("Init") && !tfGroupName.getText().contains("Cleanup")) {
                toBeRenamed = tg.getPath().toFile();
                String newName = tfGroupName.getText();
                if (newName.trim() != null && !newName.trim().isEmpty()) {
                    String oldTxtDescription = tg.getDescription().replaceAll(regEx, "");
                    String prefix = "";
                    if (!oldTxtDescription.trim().equalsIgnoreCase("Test") || oldTxtDescription.trim().equals("group")) {
                        prefix = tg.getDescription().replaceAll(oldTxtDescription, "");
                    } else {
                        prefix = tg.getDescription().substring(0, tg.getDescription().indexOf("-") + 2);
                    }
                    //change run.xml description
                    String oldDescription = tg.getDescription();
                    tg.setDescription(prefix + newName);
                    if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
                        if (DatabaseGlobalAccess.getInstance().getCurrentNutzer() != null) {
                            DB_Access_Manager.getInstance().addChangeEntry(tg, "CHANGED");
                        } else {
                            DB_Access_Manager.getInstance().updateData();
                            JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Kein Nutzer ausgewählt mit welchem diese Änderung in der Datenbank"
                                    + " assoziert werden kann. Um dies sicherzustellen sollte ein Nutzer in den Einstellungen ausgewählt werden");
                        }
                    }

//                    if (GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                        DB_Access.getInstance().updateEntry(tg, oldDescription);
//                        DB_Access.getInstance().addChangeEntry(tg, ChangeType.CHANGED);
//                    }
                    tg.setEmpId(Integer.parseInt(tfEmpId.getText()));
                    tg.setPassword(Integer.parseInt(tfPassword.getText()));
                    tg.setTllId(Integer.parseInt(tfTllId.getText()));
                    ExplorerIO.changeNodeOfTG(Paths.get(toBeRenamed.toString(), "run.xml"), "description", tg.getDescription());
                    ExplorerIO.changeNodeOfTG(Paths.get(toBeRenamed.toString(), "run.xml"), "empid", tg.getEmpId() + "");
                    ExplorerIO.changeNodeOfTG(Paths.get(toBeRenamed.toString(), "run.xml"), "emppass", tg.getPassword() + "");
                    ExplorerIO.changeNodeOfTG(Paths.get(toBeRenamed.toString(), "run.xml"), "tllid", tg.getTllId() + "");
                    etm.fireTreeStructureChanged();
                }
            } else {
                toBeRenamed = tg.getPath().toFile();
                tg.setEmpId(Integer.parseInt(tfEmpId.getText()));
                tg.setPassword(Integer.parseInt(tfPassword.getText()));
                tg.setTllId(Integer.parseInt(tfTllId.getText()));
                ExplorerIO.changeNodeOfTG(Paths.get(toBeRenamed.toString(), "run.xml"), "empid", tg.getEmpId() + "");
                ExplorerIO.changeNodeOfTG(Paths.get(toBeRenamed.toString(), "run.xml"), "emppass", tg.getPassword() + "");
                ExplorerIO.changeNodeOfTG(Paths.get(toBeRenamed.toString(), "run.xml"), "tllid", tg.getTllId() + "");
                etm.fireTreeStructureChanged();
            }
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException | TransformerException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_onOk


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btOk;
    private javax.swing.JLabel lbEmpId;
    private javax.swing.JLabel lbGroupName;
    private javax.swing.JLabel lbPassword;
    private javax.swing.JLabel lbTllId;
    private javax.swing.JPanel paLeft;
    private javax.swing.JPanel paRight;
    private javax.swing.JPanel paSouth;
    private javax.swing.JTextField tfEmpId;
    private javax.swing.JTextField tfGroupName;
    private javax.swing.JTextField tfPassword;
    private javax.swing.JTextField tfTllId;
    // End of variables declaration//GEN-END:variables
}

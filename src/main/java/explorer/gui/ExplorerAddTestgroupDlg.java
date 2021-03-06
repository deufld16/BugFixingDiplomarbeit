/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package explorer.gui;

import dashboard.beans.Testgruppe;
import dashboard.bl.DatabaseGlobalAccess;
import general.beans.io_objects.TestGroupRun;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 *
 * @author Anna Lechner
 */
public class ExplorerAddTestgroupDlg extends javax.swing.JDialog {

    private TestGroupRun tg;
    private boolean ok;

    /**
     * Creates new form ExplorerAddTestgroupDialog
     *
     * @param parent
     * @param modal
     */
    public ExplorerAddTestgroupDlg(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        try {
            lbPicture.setIcon(general.io.Loader.loadLeafIcon("testgroup.png", 40, 40));
        } catch (IOException ex) {
            Logger.getLogger(ExplorerAddTestgroupDlg.class.getName()).log(Level.SEVERE, null, ex);
        }
        setSize(new Dimension(600, 400));
        setLocationRelativeTo(null);
        setTitle("Testgruppe hinzufügen");
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ex) {
//            Logger.getLogger(InitDlg.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public TestGroupRun getTg() {
        return tg;
    }

    public boolean isOk() {
        return ok;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lbGroupName = new javax.swing.JLabel();
        lbEmpId = new javax.swing.JLabel();
        lbEmpPass = new javax.swing.JLabel();
        lbTllId = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        tfGroupName = new javax.swing.JTextField();
        tfEmpId = new javax.swing.JTextField();
        tfEmpPass = new javax.swing.JTextField();
        tfTllId = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        lbPicture = new javax.swing.JLabel();
        lbHeadLine = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        btOk = new javax.swing.JButton();
        btAbbrechen = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setLayout(new java.awt.GridLayout(4, 2));

        lbGroupName.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbGroupName.setText("Group Name:");
        jPanel1.add(lbGroupName);

        lbEmpId.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbEmpId.setText("Bedienernr:");
        jPanel1.add(lbEmpId);

        lbEmpPass.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbEmpPass.setText("Passwort:");
        jPanel1.add(lbEmpPass);

        lbTllId.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbTllId.setText("Ladennr:");
        jPanel1.add(lbTllId);

        getContentPane().add(jPanel1, java.awt.BorderLayout.WEST);

        jPanel2.setLayout(new java.awt.GridLayout(4, 2));

        tfGroupName.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel2.add(tfGroupName);

        tfEmpId.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel2.add(tfEmpId);

        tfEmpPass.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jPanel2.add(tfEmpPass);

        tfTllId.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tfTllId.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                onEnter(evt);
            }
        });
        jPanel2.add(tfTllId);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.BorderLayout());

        lbPicture.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbPicture.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5));
        jPanel3.add(lbPicture, java.awt.BorderLayout.WEST);

        lbHeadLine.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lbHeadLine.setText("Testgruppe hinzufügen");
        lbHeadLine.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 10));
        jPanel3.add(lbHeadLine, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel3, java.awt.BorderLayout.NORTH);

        jPanel4.setLayout(new java.awt.GridLayout(1, 2));

        btOk.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btOk.setText("Ok");
        btOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onOk(evt);
            }
        });
        jPanel4.add(btOk);

        btAbbrechen.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btAbbrechen.setText("Abbrechen");
        btAbbrechen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onCancel(evt);
            }
        });
        jPanel4.add(btAbbrechen);

        getContentPane().add(jPanel4, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Method that is called when the user presses "Cancel" when adding a new TG
     *
     * @param evt
     */
    private void onCancel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onCancel
        ok = false;
        dispose();
    }//GEN-LAST:event_onCancel

    /**
     * Method that is called when the user presses "Ok" when adding a new TG
     *
     * @param evt
     */
    private void onOk(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onOk
        doOkAction();
    }//GEN-LAST:event_onOk

    private void onEnter(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_onEnter
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            doOkAction();
        }
    }//GEN-LAST:event_onEnter

    private void doOkAction() {
        try {
            if (!tfEmpId.getText().trim().isEmpty() && !tfTllId.getText().trim().isEmpty()
                    && !tfEmpPass.getText().trim().isEmpty() && !tfGroupName.getText().trim().isEmpty()) {
                tg = new TestGroupRun(Integer.parseInt(tfEmpId.getText()), Integer.parseInt(tfTllId.getText()), Integer.parseInt(tfEmpPass.getText()), new LinkedList<>(), tfGroupName.getText(), null);
                if (DatabaseGlobalAccess.getInstance().isDbReachable()) {
                    Testgruppe db_tg = new Testgruppe(tg.getDescription(), LocalDate.now(), 0);
                    tg.setDurchlauf_gegenstand(db_tg);
                }
                ok = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Bitte füllen Sie alle Felder aus!", "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Bitte geben Sie für die Bedienernr.,\ndas Passwort und die Ladennr.\nnur Zahlen ein!",
                    "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

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
            java.util.logging.Logger.getLogger(ExplorerAddTestgroupDlg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ExplorerAddTestgroupDlg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ExplorerAddTestgroupDlg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ExplorerAddTestgroupDlg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ExplorerAddTestgroupDlg dialog = new ExplorerAddTestgroupDlg(new javax.swing.JFrame(), true);
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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btAbbrechen;
    private javax.swing.JButton btOk;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lbEmpId;
    private javax.swing.JLabel lbEmpPass;
    private javax.swing.JLabel lbGroupName;
    private javax.swing.JLabel lbHeadLine;
    private javax.swing.JLabel lbPicture;
    private javax.swing.JLabel lbTllId;
    private javax.swing.JTextField tfEmpId;
    private javax.swing.JTextField tfEmpPass;
    private javax.swing.JTextField tfGroupName;
    private javax.swing.JTextField tfTllId;
    // End of variables declaration//GEN-END:variables
}

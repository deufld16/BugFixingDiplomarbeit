/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dashboard.gui;

import general.bl.GlobalParamter;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 *
 * @author A180953
 */
public class LoadingDLG extends javax.swing.JDialog {

    private int maximum = 0;
    private int currValue = 0;
    private Thread checkThread = null;
    private LoadingDLG instance;
    private String progressText;

    /**
     * Creates new form LoadingDLG
     */
    public LoadingDLG(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        
        initComponents();
    }

    public LoadingDLG(java.awt.Frame parent, boolean modal, int maximum, String displayText, 
            String progressText) {
        super(parent, modal);
        initComponents();
        paImageIcon.add(new JLabel("", new ImageIcon(new ImageIcon(
                GlobalParamter.getInstance().getDashboardResPath() + File.separator + "img" + File.separator + "loading.gif").
                getImage().getScaledInstance(50, 50, Image.SCALE_DEFAULT)),
                JLabel.CENTER));
        lbDisplayText.setText(displayText);
        lbCurrProcess.setText(String.format(progressText, 0, maximum));
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        
        this.progressText = progressText;
        this.maximum = maximum;
        checkThread = new Thread(new Checker());
        checkThread.start();
        instance = this;
    }

    public void increaseCurrValue() {
        currValue++;
    }


    private class Checker implements Runnable {

        private int lastCurrValue = 0;

        @Override
        public void run() {
            while (currValue != maximum && !Thread.interrupted()) {
                if (lastCurrValue != currValue) {
                    lastCurrValue = currValue;
                    lbCurrProcess.setText(String.format(progressText, currValue, maximum));
                }
                //System.out.println("test");
            }
            //System.out.println(currValue + "-" + maximum);
            instance.dispose();
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

        paImageIcon = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lbDisplayText = new javax.swing.JLabel();
        lbCurrProcess = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(500, 146));

        paImageIcon.setOpaque(false);
        paImageIcon.setPreferredSize(new java.awt.Dimension(125, 142));
        paImageIcon.setLayout(new java.awt.BorderLayout());
        getContentPane().add(paImageIcon, java.awt.BorderLayout.LINE_START);

        jPanel2.setLayout(new java.awt.GridLayout(2, 0));

        lbDisplayText.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbDisplayText.setText("Importing Data into Database");
        lbDisplayText.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 15, 0, 0));
        jPanel2.add(lbDisplayText);

        lbCurrProcess.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbCurrProcess.setText("10 von 65 importiert");
        lbCurrProcess.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 15, 0, 0));
        jPanel2.add(lbCurrProcess);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(LoadingDLG.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoadingDLG.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoadingDLG.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoadingDLG.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                LoadingDLG dialog = new LoadingDLG(new javax.swing.JFrame(), true);
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
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lbCurrProcess;
    private javax.swing.JLabel lbDisplayText;
    private javax.swing.JPanel paImageIcon;
    // End of variables declaration//GEN-END:variables
}

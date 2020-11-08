/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.gui.dlg;

import recorder.beans.Article;
import java.awt.event.MouseListener;
import java.util.EventListener;
import javax.swing.JCheckBox;
import javax.swing.JLabel;

/**
 *
 * @author Florian
 */
public class ArticleInformationDlg extends javax.swing.JDialog {

    /**
     * Creates new form ArticleInformationDlg
     */
    private Article art;

    public ArticleInformationDlg(java.awt.Frame parent, boolean modal, Article art) {
        super(parent, modal);
        if (!art.isPfand()) {
            initComponents();
        } else {
            initComponentsWithPfand();
            lbPfandart.setText(preparePfandArt(art.getPfandArtikel()));
        }
        this.art = art;
        lbProduktname.setText(art.getArticleName());
        lbEAN.setText(art.getEan());
        lbUST.setText(art.getUst() + " %");
        lbPreis.setText(String.format("%.2f", art.getPreis()));

        cbGewichtartikel.setSelected(art.isWeight());
        cbJugendschutz.setSelected(art.isJugendSchutz());
        cbLeergut.setSelected(art.isLeergut());
        cbMWD.setSelected(art.isEloading());
        cbPfand.setSelected(art.isPfand());
        cbRabattfaehig.setSelected(art.isRabatt());
        cbSeriennummer.setSelected(art.isSerialNrRequired());

        setLocationRelativeTo(null);
        removeListeners();
    }

    /**
     * Method that, if needed, inserts a new line for the text field "Pfandart"
     * @param pfandArt
     * @return 
     */
    private String preparePfandArt(String pfandArt){
        String output = "<html>";
        for (int i = 0; i < pfandArt.length(); i++) {
            output += pfandArt.charAt(i) + "";
            if(i % 55 == 0 && i != 0){
                output += "<br/>";
            }
        }
        output += "</html>";
        return output;
    }
    
    public ArticleInformationDlg(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }

    private void initComponentsWithPfand() {
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        cbRabattfaehig = new javax.swing.JCheckBox();
        cbLeergut = new javax.swing.JCheckBox();
        cbJugendschutz = new javax.swing.JCheckBox();
        cbPfand = new javax.swing.JCheckBox();
        cbSeriennummer = new javax.swing.JCheckBox();
        cbGewichtartikel = new javax.swing.JCheckBox();
        cbMWD = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lbProduktname = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lbEAN = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lbUST = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lbPreis = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(975, 525));

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel1.setText("  Produkteigenschaften");
        jLabel1.setOpaque(true);
        jLabel1.setPreferredSize(new java.awt.Dimension(33, 45));
        getContentPane().add(jLabel1, java.awt.BorderLayout.PAGE_START);
        jLabel1.getAccessibleContext().setAccessibleName("");

        jPanel3.setPreferredSize(new java.awt.Dimension(416, 140));
        jPanel3.setLayout(new java.awt.GridLayout(3, 3));

        cbRabattfaehig.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        cbRabattfaehig.setText("Rabattfähig");
        jPanel3.add(cbRabattfaehig);

        cbLeergut.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        cbLeergut.setText("Leergut");
        jPanel3.add(cbLeergut);

        cbJugendschutz.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        cbJugendschutz.setText("Jugendschutz");
        jPanel3.add(cbJugendschutz);

        cbPfand.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        cbPfand.setText("Pfand");
        jPanel3.add(cbPfand);

        cbSeriennummer.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        cbSeriennummer.setText("Seriennummer");
        jPanel3.add(cbSeriennummer);

        cbGewichtartikel.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        cbGewichtartikel.setText("Gewichtartikel");
        jPanel3.add(cbGewichtartikel);

        cbMWD.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        cbMWD.setText("Eloading");
        jPanel3.add(cbMWD);

        getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_END);

        jPanel1.setPreferredSize(new java.awt.Dimension(400, 190));
        jPanel1.setLayout(new java.awt.GridLayout(5, 3));

        jLabel2.setFont(new java.awt.Font("Arial", 1, 17)); // NOI18N
        jLabel2.setText("  Produktname:");
        jPanel1.add(jLabel2);

        lbProduktname.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        jPanel1.add(lbProduktname);

        jLabel4.setFont(new java.awt.Font("Arial", 1, 17)); // NOI18N
        jLabel4.setText("  EAN:");
        jLabel4.setToolTipText("");
        jPanel1.add(jLabel4);

        lbEAN.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        jPanel1.add(lbEAN);

        jLabel8.setFont(new java.awt.Font("Arial", 1, 17)); // NOI18N
        jLabel8.setText("  Ust.:");
        jLabel8.setToolTipText("");
        jPanel1.add(jLabel8);

        lbUST.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        jPanel1.add(lbUST);

        //
        JLabel lbHelp = new JLabel("  Pfandart");
        lbHelp.setFont(new java.awt.Font("Arial", 1, 17)); // NOI18N
        lbHelp.setToolTipText("");
        jPanel1.add(lbHelp);

        lbPfandart.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        jPanel1.add(lbPfandart);
        //
        jLabel6.setFont(new java.awt.Font("Arial", 1, 17)); // NOI18N
        jLabel6.setText("  Preis:");
        jPanel1.add(jLabel6);

        lbPreis.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        jPanel1.add(lbPreis);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }

    /**
     * Method that removes all listeners from the checkboxes
     */
    private void removeListeners() {
        JCheckBox[] checkBox = {cbGewichtartikel, cbJugendschutz, cbLeergut, cbMWD, cbPfand, cbRabattfaehig, cbSeriennummer};
        for (JCheckBox check : checkBox) {
            EventListener[] listeners = check.getListeners(MouseListener.class);
            for (EventListener eventListener : listeners) {
                check.removeMouseListener((MouseListener) eventListener);
                check.setFocusable(false);
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

        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        cbRabattfaehig = new javax.swing.JCheckBox();
        cbLeergut = new javax.swing.JCheckBox();
        cbJugendschutz = new javax.swing.JCheckBox();
        cbPfand = new javax.swing.JCheckBox();
        cbSeriennummer = new javax.swing.JCheckBox();
        cbGewichtartikel = new javax.swing.JCheckBox();
        cbMWD = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lbProduktname = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lbEAN = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lbUST = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        lbPreis = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        jLabel1.setText("  Produkteigenschaften");
        jLabel1.setOpaque(true);
        jLabel1.setPreferredSize(new java.awt.Dimension(33, 45));
        getContentPane().add(jLabel1, java.awt.BorderLayout.PAGE_START);
        jLabel1.getAccessibleContext().setAccessibleName("");

        jPanel3.setPreferredSize(new java.awt.Dimension(416, 140));
        jPanel3.setLayout(new java.awt.GridLayout(3, 3));

        cbRabattfaehig.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        cbRabattfaehig.setText("Rabattfähig");
        jPanel3.add(cbRabattfaehig);

        cbLeergut.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        cbLeergut.setText("Leergut");
        jPanel3.add(cbLeergut);

        cbJugendschutz.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        cbJugendschutz.setText("Jugendschutz");
        jPanel3.add(cbJugendschutz);

        cbPfand.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        cbPfand.setText("Pfand");
        jPanel3.add(cbPfand);

        cbSeriennummer.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        cbSeriennummer.setText("Seriennummer");
        jPanel3.add(cbSeriennummer);

        cbGewichtartikel.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        cbGewichtartikel.setText("Gewichtartikel");
        jPanel3.add(cbGewichtartikel);

        cbMWD.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        cbMWD.setText("Eloading");
        jPanel3.add(cbMWD);

        getContentPane().add(jPanel3, java.awt.BorderLayout.PAGE_END);

        jPanel1.setPreferredSize(new java.awt.Dimension(400, 190));
        jPanel1.setLayout(new java.awt.GridLayout(4, 3));

        jLabel2.setFont(new java.awt.Font("Arial", 1, 17)); // NOI18N
        jLabel2.setText("  Produktname:");
        jPanel1.add(jLabel2);

        lbProduktname.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        jPanel1.add(lbProduktname);

        jLabel4.setFont(new java.awt.Font("Arial", 1, 17)); // NOI18N
        jLabel4.setText("  EAN:");
        jLabel4.setToolTipText("");
        jPanel1.add(jLabel4);

        lbEAN.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        jPanel1.add(lbEAN);

        jLabel8.setFont(new java.awt.Font("Arial", 1, 17)); // NOI18N
        jLabel8.setText("  Ust.:");
        jLabel8.setToolTipText("");
        jPanel1.add(jLabel8);

        lbUST.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        jPanel1.add(lbUST);

        jLabel6.setFont(new java.awt.Font("Arial", 1, 17)); // NOI18N
        jLabel6.setText("  Preis:");
        jPanel1.add(jLabel6);

        lbPreis.setFont(new java.awt.Font("Arial", 0, 17)); // NOI18N
        jPanel1.add(lbPreis);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

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
            java.util.logging.Logger.getLogger(ArticleInformationDlg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ArticleInformationDlg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ArticleInformationDlg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ArticleInformationDlg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ArticleInformationDlg dialog = new ArticleInformationDlg(new javax.swing.JFrame(), true);
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

    private JLabel lbPfandart = new JLabel();
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox cbGewichtartikel;
    private javax.swing.JCheckBox cbJugendschutz;
    private javax.swing.JCheckBox cbLeergut;
    private javax.swing.JCheckBox cbMWD;
    private javax.swing.JCheckBox cbPfand;
    private javax.swing.JCheckBox cbRabattfaehig;
    private javax.swing.JCheckBox cbSeriennummer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel lbEAN;
    private javax.swing.JLabel lbPreis;
    private javax.swing.JLabel lbProduktname;
    private javax.swing.JLabel lbUST;
    // End of variables declaration//GEN-END:variables
}

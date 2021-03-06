/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.gui.dlg;

import recorder.beans.Article;
import recorder.guiOperations.GUIOperations;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import recorder.gui.PaArticleTable;

/**
 *
 * @author Maxi
 */
public class ArticleConfigSettingsDlg extends javax.swing.JDialog {

    private PaArticleTable at;
    private JButton btSrc;
    private Map<String, String> articleMap = new HashMap<>();
    private Map<String, String> disabledArticleMap = new HashMap<>();
    private JLabel lbAlternative = new JLabel("Bitte auf Artikel setzen klicken um Artikelliste zu sehen");

    /**
     * Creates new form ArticleConfigSettingsDlg
     * @param parent
     * @param modal
     */
    public ArticleConfigSettingsDlg(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        at = new PaArticleTable(null, true, this);
        at.getTaArtikel().setColumnModel(GUIOperations.getTaArtikel().getColumnModel());
        lbAlternative.setHorizontalAlignment(SwingConstants.CENTER);
        lbAlternative.setFont(new Font("Tahoma", Font.PLAIN, 14));
        paArticleTable.add(lbAlternative);
        paArticleTable.updateUI();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paContainer = new javax.swing.JPanel();
        paMain = new javax.swing.JPanel();
        paBtn1 = new javax.swing.JPanel();
        cb1 = new javax.swing.JCheckBox();
        paSelection1 = new javax.swing.JPanel();
        lbBtnInfo1 = new javax.swing.JLabel();
        btSetArticle1 = new javax.swing.JButton();
        paBtn2 = new javax.swing.JPanel();
        cb2 = new javax.swing.JCheckBox();
        paSelection2 = new javax.swing.JPanel();
        lbBtnInfo2 = new javax.swing.JLabel();
        btSetArticle2 = new javax.swing.JButton();
        paBtn3 = new javax.swing.JPanel();
        cb3 = new javax.swing.JCheckBox();
        paSelection3 = new javax.swing.JPanel();
        lbBtnInfo3 = new javax.swing.JLabel();
        btSetArticle3 = new javax.swing.JButton();
        paArticleTable = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        paContainer.setLayout(new java.awt.BorderLayout());

        paMain.setLayout(new java.awt.GridLayout(10, 1));

        paBtn1.setLayout(new java.awt.BorderLayout());

        cb1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cb1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeState1(evt);
            }
        });
        paBtn1.add(cb1, java.awt.BorderLayout.WEST);

        paSelection1.setLayout(new java.awt.GridLayout(1, 2));

        lbBtnInfo1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbBtnInfo1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbBtnInfo1.setText("Button_1");
        lbBtnInfo1.setEnabled(false);
        paSelection1.add(lbBtnInfo1);

        btSetArticle1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btSetArticle1.setText("Artikel setzen");
        btSetArticle1.setActionCommand("Button_1");
        btSetArticle1.setEnabled(false);
        btSetArticle1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onSelectArticle(evt);
            }
        });
        paSelection1.add(btSetArticle1);

        paBtn1.add(paSelection1, java.awt.BorderLayout.CENTER);

        paMain.add(paBtn1);

        paBtn2.setLayout(new java.awt.BorderLayout());

        cb2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cb2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeState2(evt);
            }
        });
        paBtn2.add(cb2, java.awt.BorderLayout.WEST);

        paSelection2.setLayout(new java.awt.GridLayout(1, 2));

        lbBtnInfo2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbBtnInfo2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbBtnInfo2.setText("Button_2");
        lbBtnInfo2.setEnabled(false);
        paSelection2.add(lbBtnInfo2);

        btSetArticle2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btSetArticle2.setText("Artikel setzen");
        btSetArticle2.setActionCommand("Button_2");
        btSetArticle2.setEnabled(false);
        btSetArticle2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onSelectArticle(evt);
            }
        });
        paSelection2.add(btSetArticle2);

        paBtn2.add(paSelection2, java.awt.BorderLayout.CENTER);

        paMain.add(paBtn2);

        paBtn3.setLayout(new java.awt.BorderLayout());

        cb3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cb3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeState3(evt);
            }
        });
        paBtn3.add(cb3, java.awt.BorderLayout.WEST);

        paSelection3.setLayout(new java.awt.GridLayout(1, 2));

        lbBtnInfo3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbBtnInfo3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbBtnInfo3.setText("Button_3");
        lbBtnInfo3.setEnabled(false);
        paSelection3.add(lbBtnInfo3);

        btSetArticle3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btSetArticle3.setText("Artikel setzen");
        btSetArticle3.setActionCommand("Button_2");
        btSetArticle3.setEnabled(false);
        btSetArticle3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onSelectArticle(evt);
            }
        });
        paSelection3.add(btSetArticle3);

        paBtn3.add(paSelection3, java.awt.BorderLayout.CENTER);

        paMain.add(paBtn3);

        paContainer.add(paMain, java.awt.BorderLayout.CENTER);

        paArticleTable.setPreferredSize(new java.awt.Dimension(600, 550));
        paArticleTable.setLayout(new java.awt.BorderLayout());
        paContainer.add(paArticleTable, java.awt.BorderLayout.EAST);

        getContentPane().add(paContainer, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Method that calls paArticleTable when an article is to be set on a button
     *
     * @param evt
     */
    private void onSelectArticle(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onSelectArticle
        btSrc = (JButton) evt.getSource();
        btSrc.setEnabled(false);
        paArticleTable.remove(lbAlternative);
        paArticleTable.add(at);
        paArticleTable.updateUI();
    }//GEN-LAST:event_onSelectArticle
    
    /**
     * Method that can set an article on the first button
     * @param evt
     */
    private void onChangeState1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onChangeState1
        if (cb1.isSelected()) {
//            restoreView();
            String key = btSetArticle1.getActionCommand();
            restoreArticle(key);
            lbBtnInfo1.setEnabled(true);
            btSetArticle1.setEnabled(true);
        } else {
            restoreView();
            lbBtnInfo1.setEnabled(false);
            btSetArticle1.setEnabled(false);
            String key = btSetArticle1.getActionCommand();
            saveArticle(key);
        }
    }//GEN-LAST:event_onChangeState1

    /**
     * Method that can set an article on the second button
     * @param evt
     */
    private void onChangeState2(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onChangeState2
        // TODO add your handling code here:
        if (cb2.isSelected()) {
            String key = btSetArticle2.getActionCommand();
            restoreArticle(key);
            lbBtnInfo2.setEnabled(true);
            btSetArticle2.setEnabled(true);
        } else {
            restoreView();
            lbBtnInfo2.setEnabled(false);
            btSetArticle2.setEnabled(false);
            String key = btSetArticle2.getActionCommand();
            saveArticle(key);
        }
    }//GEN-LAST:event_onChangeState2

    /**
     * Method that can set an article on the third button
     * @param evt
     */
    private void onChangeState3(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onChangeState3
        // TODO add your handling code here:
        changeState(cb3, lbBtnInfo3, btSetArticle3);
    }//GEN-LAST:event_onChangeState3

    /**
     * Method that changes the state of a button (occupied or not)
     * @param cb
     * @param lbBtnInfo
     * @param btSetArticle 
     */
    private void changeState(JCheckBox cb, JLabel lbBtnInfo, JButton btSetArticle) {
        if (cb.isSelected()) {
            String key = btSetArticle.getActionCommand();
            restoreArticle(key);
            lbBtnInfo.setEnabled(true);
            btSetArticle.setEnabled(true);
        } else {
            restoreView();
            lbBtnInfo.setEnabled(false);
            btSetArticle.setEnabled(false);
            String key = btSetArticle.getActionCommand();
            saveArticle(key);
        }
    }

    /**
     * Method that sets an article on an article button
     * @param selArt 
     */
    public void setFavoriteArticle(Article selArt) {
        restoreView();
        articleMap.put(btSrc.getActionCommand(), selArt.getEan());
        btSrc.setEnabled(true);
        btSrc.setText(selArt.getArticleName());
        btSrc.setSelected(false);
    }

    /**
     * Method that hides paArticleTable
     */
    private void restoreView() {
        paArticleTable.remove(at);
        paArticleTable.add(lbAlternative);
        paArticleTable.updateUI();
    }

    /**
     * Method that saves all set articles
     * @param key 
     */
    private void saveArticle(String key) {
        String value = articleMap.get(key);
        articleMap.remove(key);
        disabledArticleMap.put(key, value);
    }

    /**
     * Method that restores the set articles
     * @param key 
     */
    private void restoreArticle(String key) {
        String value = disabledArticleMap.get(key);
        disabledArticleMap.remove(key);
        articleMap.put(key, value);
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
            java.util.logging.Logger.getLogger(ArticleConfigSettingsDlg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ArticleConfigSettingsDlg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ArticleConfigSettingsDlg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ArticleConfigSettingsDlg.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ArticleConfigSettingsDlg dialog = new ArticleConfigSettingsDlg(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btSetArticle1;
    private javax.swing.JButton btSetArticle2;
    private javax.swing.JButton btSetArticle3;
    private javax.swing.JCheckBox cb1;
    private javax.swing.JCheckBox cb2;
    private javax.swing.JCheckBox cb3;
    private javax.swing.JLabel lbBtnInfo1;
    private javax.swing.JLabel lbBtnInfo2;
    private javax.swing.JLabel lbBtnInfo3;
    private javax.swing.JPanel paArticleTable;
    private javax.swing.JPanel paBtn1;
    private javax.swing.JPanel paBtn2;
    private javax.swing.JPanel paBtn3;
    private javax.swing.JPanel paContainer;
    private javax.swing.JPanel paMain;
    private javax.swing.JPanel paSelection1;
    private javax.swing.JPanel paSelection2;
    private javax.swing.JPanel paSelection3;
    // End of variables declaration//GEN-END:variables
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.gui;

import java.awt.Font;
import recorder.gui.dlg.ArticleConfigSettingsDlg;
import recorder.gui.dlg.ArticleInformationDlg;
import recorder.beans.Article;
import recorder.bl.ArtikelTableModel;
import recorder.bl.DisplayListModel;
import recorder.guiOperations.GUIOperations;
import recorder.io.IOSaver;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 *
 * @author Florian
 */
public class PaArticleTable extends javax.swing.JPanel {

    private ArtikelTableModel atm = new ArtikelTableModel();
    private DisplayListModel dlm;
    private JTextField tfSumme = null;
    private boolean jfi = false;
    private Article selArticle;
    private ArticleConfigSettingsDlg acsdlg;

    /**
     * Constructor of PaArticleTable and initialization of all shortcuts
     *
     * @param parent
     */
    public PaArticleTable(java.awt.Frame parent) {
        initComponents();
        taArtikel.setModel(atm);
        try {
            atm.loadData();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Ein fehler beim Einlesen der Artikel ist aufgetreten");
        }
        cbAuswahl.setModel(new DefaultComboBoxModel<>(new Vector(atm.fillComboBox())));
        setGUIOperationParameter();

        taArtikel.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_C) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    GUIOperations.getTfDigitField().setText(
                            GUIOperations.getAtm().getFilteredArtikels().get(taArtikel.getSelectedRow()).getEan());
                }

                if ((e.getKeyCode() == KeyEvent.VK_ADD || e.getKeyCode() == 521) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    String buttonNumber = JOptionPane.showInputDialog("Geben Sie den Position des zu belegenden Buttons ein");
                    Article art = GUIOperations.getAtm().getFilteredArtikels().get(taArtikel.getSelectedRow());
                    try {
                        JButton btn = GUIOperations.getPaArticle().getArticleButtons().get(Integer.parseInt(buttonNumber) - 1);
                        btn.setText(art.getArticleName());
                        btn.setActionCommand(art.getEan());
                        if (!IOSaver.saveArticleConfigFile(GUIOperations.getPaArticle().getArticleButtons())) {
                            return;
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Beim Schreiben der Article.conf ist ein Fehler aufgetreten");
                    } catch (NumberFormatException | IndexOutOfBoundsException ex) {
                        JOptionPane.showMessageDialog(null, "Bitte geben Sie eine gültige Zahl/Position ein!");
                    }
                }

                if ((e.getKeyCode() == KeyEvent.VK_I) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    Article art = GUIOperations.getAtm().getFilteredArtikels().get(taArtikel.getSelectedRow());
                    ArticleInformationDlg infoDlg = new ArticleInformationDlg(GUIOperations.getMainframe(), true, art);
                    infoDlg.setVisible(true);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        btAddArticle.setFont(new Font("Tahoma", Font.BOLD, 18));
        cbAuswahl.setFont(new Font("Tahoma", Font.BOLD, 16));
        tfFilter.setFont(new Font("Tahoma", Font.BOLD, 16));
    }

    /**
     * Second constructor
     * @param parent
     * @param jfi
     * @param acsdlg 
     */
    public PaArticleTable(java.awt.Frame parent, boolean jfi, ArticleConfigSettingsDlg acsdlg) {
        this.jfi = jfi;
        this.acsdlg = acsdlg;
        initComponents();
        taArtikel.setModel(atm);
        try {
            atm.loadData();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Ein fehler beim Einlesen der Artikel ist aufgetreten");
        }
        cbAuswahl.setModel(new DefaultComboBoxModel<>(new Vector(atm.fillComboBox())));
        if (!jfi) {
            setGUIOperationParameter();
        }
        btAddArticle.setFont(new Font("Tahoma", Font.BOLD, 18));
        cbAuswahl.setFont(new Font("Tahoma", Font.BOLD, 16));
        tfFilter.setFont(new Font("Tahoma", Font.BOLD, 16));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        cbAuswahl = new javax.swing.JComboBox<>();
        jPanel5 = new javax.swing.JPanel();
        tfFilter = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        lbSelArticle = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        taArtikel = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        btAddArticle = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(800, 550));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(800, 75));
        jPanel1.setLayout(new java.awt.GridLayout(1, 3));

        cbAuswahl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onComboBoxChange(evt);
            }
        });
        jPanel1.add(cbAuswahl);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Filter"));
        jPanel5.setLayout(new java.awt.BorderLayout());

        tfFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onChangeCombobox(evt);
            }
        });
        tfFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                onFilter(evt);
            }
        });
        jPanel5.add(tfFilter, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel5);

        jPanel4.setLayout(new java.awt.BorderLayout());

        lbSelArticle.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lbSelArticle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel4.add(lbSelArticle, java.awt.BorderLayout.CENTER);

        jPanel1.add(jPanel4);

        add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel2.setPreferredSize(new java.awt.Dimension(800, 402));
        jPanel2.setLayout(new java.awt.BorderLayout());

        taArtikel.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null},
                    {null, null, null, null}
                },
                new String[]{
                    "Title 1", "Title 2", "Title 3", "Title 4"
                }
        ));
        taArtikel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                onSelectArticleByClick(evt);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                onSelectArticle(evt);
            }
        });
        jScrollPane1.setViewportView(taArtikel);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel3.setPreferredSize(new java.awt.Dimension(800, 75));
        jPanel3.setLayout(new java.awt.GridLayout(1, 2));

        btAddArticle.setText("Artikel hinzufügen");
        btAddArticle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onAddArticle(evt);
            }
        });
        jPanel3.add(btAddArticle);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 75, Short.MAX_VALUE)
        );

        jPanel3.add(jPanel6);

        add(jPanel3, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>                        

    /**
     * Method to add an article of the table
     *
     * @param evt
     */
    private void onAddArticle(java.awt.event.ActionEvent evt) {
        removeArticleName();
        if (jfi) {
            int selRow = taArtikel.getSelectedRow();
            selArticle = atm.getFilteredArtikels().get(selRow);
            acsdlg.setFavoriteArticle(selArticle);
        } else {
            GUIOperations.addArticle();
        }
    }

    /**
     * Method to filter the articles by their article name
     *
     * @param evt
     */
    private void onFilter(java.awt.event.KeyEvent evt) {
        removeArticleName();
        String filter = tfFilter.getText();
        atm.setFilter(filter.toUpperCase());
        atm.filter();
    }

    /**
     * Method to filter the articles with a specific article group
     *
     * @param evt
     */
    private void onChangeCombobox(java.awt.event.ActionEvent evt) {
        removeArticleName();
        String cbContent = cbAuswahl.getSelectedItem().toString();
        atm.setArtikelGruppe(cbContent);
        atm.filter();
    }

    /**
     * see above
     *
     * @param evt
     */
    private void onComboBoxChange(java.awt.event.ActionEvent evt) {
        removeArticleName();
        String cbContent = cbAuswahl.getSelectedItem().toString();
        atm.setArtikelGruppe(cbContent);
        atm.filter();
    }

    /**
     * Method to add articles by a double-click of the user
     *
     * @param evt
     */
    private void onSelectArticleByClick(java.awt.event.MouseEvent evt) {
        displayArticleName();
        if (evt.getButton() != 3) {
            if (evt.getClickCount() == 2) {
                if (jfi) {
                    int selRow = taArtikel.getSelectedRow();
                    selArticle = atm.getFilteredArtikels().get(selRow);
                    acsdlg.setFavoriteArticle(selArticle);
                } else {
                    GUIOperations.addArticle();
                }
            }
        }
    }

    /**
     * Method to display the selected article in a JLabel
     *
     * @param evt
     */
    private void onSelectArticle(java.awt.event.MouseEvent evt) {
        displayArticleName();
    }

    /**
     * Method to set the selected article name on a JLabel
     * 
     */
    private void displayArticleName() {
        lbSelArticle.setText(atm.getValueAt(taArtikel.getSelectedRow(), 0) + "");
    }

    /**
     * Method to clear the JLabel
     */
    private void removeArticleName() {
        lbSelArticle.setText("");
    }

    public void setJfi(boolean jfi) {
        this.jfi = jfi;
    }

    /**
     * Method to set all parameters for GUIOperations
     */
    private void setGUIOperationParameter() {
        GUIOperations.setTaArtikel(taArtikel);
        GUIOperations.setAtm(atm);
    }

    public Article getSelArticle() {
        return selArticle;
    }

    public JTable getTaArtikel() {
        return taArtikel;
    }

    // Variables declaration - do not modify                     
    private javax.swing.JComboBox<String> cbAuswahl;
    private javax.swing.JButton btAddArticle;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbSelArticle;
    private javax.swing.JTable taArtikel;
    private javax.swing.JTextField tfFilter;
    // End of variables declaration                   
}
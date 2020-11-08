/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.gui;

import recorder.gui.dlg.AuthLimitsDlg;
import general.bl.GlobalAccess;
import general.enums.Tools;
import java.awt.Dimension;
import recorder.beans.Article;
import recorder.bl.DisplayListModel;
import recorder.guiOperations.GUIOperations;
import recorder.guiRenderer.ArticleTableRenderer;
import recorder.guiRenderer.DisplayListRenderer;
import recorder.io.ConfigManager;
import recorder.io.IOLoader;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import recorder.beans.Command;

/**
 *
 * @author
 */
public class MainFrame extends javax.swing.JFrame {

    private boolean switchPanels = false;
    private DisplayListModel dlm = new DisplayListModel();
    private PaArticle paArticle;
    //private Map<String, Object> initParams = new HashMap<>();

    /**
     * Creates new form mainFrame
     *
     * @param parent
     */
    public MainFrame(java.awt.Frame parent) {
        initComponents();
        this.setIconImage(new ImageIcon(Paths.get(System.getProperty("user.dir"), "src", "res", "img", "logo.png").toString()).getImage());
        paArticle = new PaArticle(this, dlm, liDisplay, tfSumme);
        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        setGUIOperationParameter();
        liDisplay.setModel(dlm);
        try {
            loadAndSetIcons();
            IOLoader.loadArtikel();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error");
        }

        GUIOperations.setPayProcessStarted(false);
        PaArticleTable art = new PaArticleTable(null);
        jPanel16.add(art);
        tfSumme.setText("0,00");
        initTable();
        GUIOperations.getTaArtikel().setDefaultRenderer(Integer.class, new ArticleTableRenderer());
        GUIOperations.getTaArtikel().setDefaultRenderer(Double.class, new ArticleTableRenderer());
        liDisplay.setCellRenderer(new DisplayListRenderer());

        try {
            IOLoader.loadFunctionMapperXML();
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace();
        }
        onSwitchActionPanels(null);
        try {
            GUIOperations.getRecorderXml().isBon(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Method to set the column size and the head lines of taArticle
     *
     */
    public void initTable() {
        DefaultTableColumnModel dtcm = new DefaultTableColumnModel();
        List<String> allHeadLineNames = Arrays.asList("Artikeltext", "EAN", "Preis", "USt.");
        List<Integer> allHeadLineSizes = Arrays.asList(175, 165, 80, 60);
        for (int i = 0; i < allHeadLineNames.size(); i++) {
            TableColumn tc = new TableColumn(i, allHeadLineSizes.get(i));
            tc.setHeaderValue(allHeadLineNames.get(i));
            dtcm.addColumn(tc);
        }
        GUIOperations.getTaArtikel().setFont(new Font("Tahoma", Font.BOLD, 14));
        GUIOperations.getTaArtikel().setRowHeight(30);
        GUIOperations.getTaArtikel().setColumnModel(dtcm);
    }

    /**
     * Method to open the InitDlg
     *
     * @throws IOException
     */
    public void openInitDialog() throws IOException {
        while (true) {
            InitDlg idlg = new InitDlg(this, true);
            idlg.setVisible(true);
            idlg.setSize(new Dimension(500, 500));
            if (idlg.isOk()) {
                GUIOperations.setInitParams(idlg.getInitParams());
                if (GUIOperations.getArticleConfigFile() != null) {
                    GUIOperations.setEanMap(recorder.io.ConfigManager.loadFile(GUIOperations.getArticleConfigFile().toPath()));
                    paArticle.setButtons(GUIOperations.getEanMap());
                }
                if (GUIOperations.getFuntionConfigFile() != null) {
                    GUIOperations.setDynFuncMap(recorder.io.ConfigManager.loadFile(GUIOperations.getFuntionConfigFile().toPath()));
                    paArticle.addDynamicFunctionalities(GUIOperations.getDynFuncMap());
                }
                break;
            } else {
                JOptionPane.showMessageDialog(null, "Die Init Daten müssen eingegeben werden!");
            }
//            break;
        }
        GUIOperations.setNeedsInit(false);
    }

    /**
     * Method to set all necessary parameter in GUIOperations
     *
     */
    private void setGUIOperationParameter() {
        GUIOperations.setPaArticle(paArticle);
        GUIOperations.setDlm(dlm);
        GUIOperations.setSwitchPanel(SwitchPanel);
        GUIOperations.setTfSumme(tfSumme);
        GUIOperations.setTfDigitField(tfDigitField);
        GUIOperations.setBtnSwitch(btnSwitch);
        GUIOperations.setMainframe(this);
    }

    /**
     * Method to load and set the icons
     *
     * @throws IOException
     */
    private void loadAndSetIcons() throws IOException {
        jButton12.setIcon(IOLoader.loadImage("pfeil_links.png", 40, 40));
        btnSwitch.setIcon(IOLoader.loadImage("turn.png", 40, 40));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        displayDigitPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        tfSumme = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        liDisplay = new javax.swing.JList<>();
        digitPanel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        tfDigitField = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        actionPanel = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        btnSwitch = new javax.swing.JButton();
        SwitchPanel = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridLayout(1, 3));

        displayDigitPanel.setLayout(new java.awt.GridLayout(2, 1));

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setPreferredSize(new java.awt.Dimension(400, 70));
        jPanel2.setLayout(new java.awt.GridLayout(1, 3));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Summe:");
        jPanel2.add(jLabel1);

        tfSumme.setEditable(false);
        tfSumme.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        tfSumme.setToolTipText("");
        jPanel2.add(tfSumme);

        jPanel1.add(jPanel2, java.awt.BorderLayout.PAGE_END);

        liDisplay.setFont(new java.awt.Font("Courier New", 1, 18)); // NOI18N
        jScrollPane1.setViewportView(liDisplay);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        displayDigitPanel.add(jPanel1);

        digitPanel.setMinimumSize(new java.awt.Dimension(154, 51));
        digitPanel.setLayout(new java.awt.BorderLayout());

        jPanel3.setPreferredSize(new java.awt.Dimension(101, 300));
        jPanel3.setLayout(new java.awt.BorderLayout(0, 5));

        jPanel10.setMinimumSize(new java.awt.Dimension(97, 25));
        jPanel10.setPreferredSize(new java.awt.Dimension(101, 250));
        jPanel10.setLayout(new java.awt.GridLayout(3, 1, 0, 2));

        jButton12.setBackground(new java.awt.Color(255, 153, 0));
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onBackspace(evt);
            }
        });
        jPanel10.add(jButton12);

        jButton13.setBackground(new java.awt.Color(255, 51, 51));
        jButton13.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jButton13.setText("<html>Anzeige<br/>leeren</html>");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onRemoveText(evt);
            }
        });
        jPanel10.add(jButton13);

        jButton19.setBackground(new java.awt.Color(255, 51, 51));
        jButton19.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jButton19.setText("CLR");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onPrintCLR(evt);
            }
        });
        jPanel10.add(jButton19);

        jPanel3.add(jPanel10, java.awt.BorderLayout.NORTH);

        jButton14.setBackground(new java.awt.Color(0, 153, 51));
        jButton14.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jButton14.setText("Eingabe");
        jButton14.setMaximumSize(new java.awt.Dimension(97, 25));
        jButton14.setMinimumSize(new java.awt.Dimension(101, 250));
        jButton14.setPreferredSize(new java.awt.Dimension(101, 250));
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onEnterArticle(evt);
            }
        });
        jPanel3.add(jButton14, java.awt.BorderLayout.CENTER);

        digitPanel.add(jPanel3, java.awt.BorderLayout.LINE_END);

        jPanel4.setLayout(new java.awt.BorderLayout());

        tfDigitField.setEditable(false);
        tfDigitField.setFont(new java.awt.Font("Tahoma", 1, 20)); // NOI18N
        tfDigitField.setPreferredSize(new java.awt.Dimension(69, 50));
        tfDigitField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                onTypeNumber(evt);
            }
        });
        jPanel4.add(tfDigitField, java.awt.BorderLayout.PAGE_START);

        jPanel5.setPreferredSize(new java.awt.Dimension(334, 335));
        jPanel5.setLayout(new java.awt.GridLayout(4, 1, 2, 2));

        jPanel6.setLayout(new java.awt.GridLayout(1, 3, 2, 2));

        jButton1.setBackground(new java.awt.Color(0, 0, 0));
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("7");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDigitPressed(evt);
            }
        });
        jPanel6.add(jButton1);

        jButton2.setBackground(new java.awt.Color(0, 0, 0));
        jButton2.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("8");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDigitPressed(evt);
            }
        });
        jPanel6.add(jButton2);

        jButton3.setBackground(new java.awt.Color(0, 0, 0));
        jButton3.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("9");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDigitPressed(evt);
            }
        });
        jPanel6.add(jButton3);

        jPanel5.add(jPanel6);

        jPanel7.setLayout(new java.awt.GridLayout(1, 3, 2, 2));

        jButton4.setBackground(new java.awt.Color(0, 0, 0));
        jButton4.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("4");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDigitPressed(evt);
            }
        });
        jPanel7.add(jButton4);

        jButton5.setBackground(new java.awt.Color(0, 0, 0));
        jButton5.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("5");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDigitPressed(evt);
            }
        });
        jPanel7.add(jButton5);

        jButton6.setBackground(new java.awt.Color(0, 0, 0));
        jButton6.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("6");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDigitPressed(evt);
            }
        });
        jPanel7.add(jButton6);

        jPanel5.add(jPanel7);

        jPanel8.setLayout(new java.awt.GridLayout(1, 0));

        jButton7.setBackground(new java.awt.Color(0, 0, 0));
        jButton7.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("1");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDigitPressed(evt);
            }
        });
        jPanel8.add(jButton7);

        jButton8.setBackground(new java.awt.Color(0, 0, 0));
        jButton8.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jButton8.setForeground(new java.awt.Color(255, 255, 255));
        jButton8.setText("2");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDigitPressed(evt);
            }
        });
        jPanel8.add(jButton8);

        jButton9.setBackground(new java.awt.Color(0, 0, 0));
        jButton9.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jButton9.setForeground(new java.awt.Color(255, 255, 255));
        jButton9.setText("3");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDigitPressed(evt);
            }
        });
        jPanel8.add(jButton9);

        jPanel5.add(jPanel8);

        jPanel9.setLayout(new java.awt.GridLayout(1, 0));

        jButton10.setBackground(new java.awt.Color(0, 0, 0));
        jButton10.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jButton10.setForeground(new java.awt.Color(255, 255, 255));
        jButton10.setText("0");
        jButton10.setPreferredSize(new java.awt.Dimension(263, 25));
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDigitPressed(evt);
            }
        });
        jPanel9.add(jButton10);

        jButton11.setBackground(new java.awt.Color(0, 0, 0));
        jButton11.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        jButton11.setForeground(new java.awt.Color(255, 255, 255));
        jButton11.setText("00");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDigitPressed(evt);
            }
        });
        jPanel9.add(jButton11);

        jPanel5.add(jPanel9);

        jPanel4.add(jPanel5, java.awt.BorderLayout.CENTER);

        digitPanel.add(jPanel4, java.awt.BorderLayout.CENTER);

        displayDigitPanel.add(digitPanel);

        getContentPane().add(displayDigitPanel);

        actionPanel.setLayout(new java.awt.BorderLayout());

        jPanel11.setPreferredSize(new java.awt.Dimension(400, 75));
        jPanel11.setLayout(new java.awt.GridLayout(1, 1, 0, 2));

        jPanel13.setLayout(new java.awt.GridLayout(1, 5, 2, 0));

        jButton15.setBackground(new java.awt.Color(255, 255, 255));
        jButton15.setEnabled(false);
        jPanel13.add(jButton15);

        jButton16.setBackground(new java.awt.Color(255, 255, 255));
        jButton16.setEnabled(false);
        jPanel13.add(jButton16);

        jButton17.setBackground(new java.awt.Color(255, 255, 255));
        jButton17.setEnabled(false);
        jPanel13.add(jButton17);

        jButton18.setBackground(new java.awt.Color(255, 255, 255));
        jButton18.setEnabled(false);
        jPanel13.add(jButton18);

        btnSwitch.setBackground(new java.awt.Color(255, 255, 255));
        btnSwitch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onSwitchActionPanels(evt);
            }
        });
        jPanel13.add(btnSwitch);

        jPanel11.add(jPanel13);

        actionPanel.add(jPanel11, java.awt.BorderLayout.PAGE_START);

        SwitchPanel.setLayout(new java.awt.BorderLayout());
        actionPanel.add(SwitchPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(actionPanel);

        jPanel16.setLayout(new java.awt.BorderLayout());
        getContentPane().add(jPanel16);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * OnClick Event for the Numpad to display the pressed digits in
     * tfDigitField
     *
     * @param evt
     */
    private void onDigitPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onDigitPressed
        tfDigitField.setText(tfDigitField.getText() + evt.getActionCommand());
    }//GEN-LAST:event_onDigitPressed
    /**
     * Method to remove the last digit in tfDigitField
     *
     * @param evt
     */
    private void onBackspace(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onBackspace
        if (tfDigitField.getText().length() >= 1) {
            tfDigitField.setText(tfDigitField.getText().substring(0, tfDigitField.getText().length() - 1));
        }
    }//GEN-LAST:event_onBackspace

    /**
     * Method to switch the action panel Boolean switchPanels = true -
     * Displaying paFinance Boolean switchPanels = false - Displaying paArticle
     *
     * @param evt
     */
    private void onSwitchActionPanels(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onSwitchActionPanels
        GUIOperations.onSwitch();
    }//GEN-LAST:event_onSwitchActionPanels
    /**
     * Method to input articles via their EAN with the keyboard
     *
     * @param evt
     */
    private void onEnterArticle(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onEnterArticle
        String ean = tfDigitField.getText();
        GUIOperations.setBonStarted(true);
        Article a = GUIOperations.onEnterArticle(ean);
        if (a != null) {
            a.setKeyboard(true);
            GUIOperations.addArticle(a);
//            GUIOperations.getDlm().addArtikel(a);
            GUIOperations.setPrice();
            tfDigitField.setText("");
        } else {
            if (!ean.trim().isEmpty()) {
                Command keyBoardEntry = GUIOperations.getRecorderXml().createCommand("keyboard", "value", ean);
                keyBoardEntry.setDisplayText("Tastatureingabe - " + ean);
                GUIOperations.getDlm().addObject(keyBoardEntry);
                tfDigitField.setText("");
            } else {
                Command cmdEnter = GUIOperations.getRecorderXml().createCommand("controlkey", "function", "ENTER");
                cmdEnter.setDisplayText("ENTER");
                GUIOperations.getDlm().addObject(cmdEnter);
            }
        }
    }//GEN-LAST:event_onEnterArticle
    /**
     * Method to remove text from tfDigitFeld
     *
     * @param evt
     */
    private void onRemoveText(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onRemoveText
        tfDigitField.setText("");
    }//GEN-LAST:event_onRemoveText

    /**
     * Method to restart the Recorder process
     */
    public void onRestart() {
        dlm.clear();
        dlm = new DisplayListModel();
        GUIOperations.setDlm(dlm);
        GUIOperations.setPayProcessStarted(false);
        GUIOperations.setPaid(false);
        GUIOperations.setPwChange(false);
        getContentPane().removeAll();
        //setVisible(false);
        dlm.clear();
        initComponents();
        GUIOperations.getRecorderXml().setBonStornoType(-1);
        paArticle = new PaArticle(this, dlm, liDisplay, tfSumme);
        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        setGUIOperationParameter();
        liDisplay.setModel(dlm);
        try {
            loadAndSetIcons();
            recorder.io.IOLoader.loadArtikel();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error");
        }

        PaArticleTable art = new PaArticleTable(null);
        jPanel16.add(art);
        tfSumme.setText("0,00");
        initTable();
        GUIOperations.getTaArtikel().setDefaultRenderer(Integer.class, new ArticleTableRenderer());
        GUIOperations.getTaArtikel().setDefaultRenderer(Double.class, new ArticleTableRenderer());
        liDisplay.setCellRenderer(new DisplayListRenderer());
        GlobalAccess.getInstance().getTest_ide_main_frame().resizeComponents();
        try {
            openInitDialog();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Fehler bei den Init Parameter");
        }
        GUIOperations.setSwitchPanels(false);
        onSwitchActionPanels(null);
        //setVisible(true);
        //validate();
        //GUIOperations.getBtnSwitch().setEnabled(false);
        paArticle.setButtons(GUIOperations.getEanMap());
        paArticle.addDynamicFunctionalities(GUIOperations.getDynFuncMap());
        GUIOperations.setTraining(false);
        GUIOperations.setBonStarted(false);
        ((JCheckBoxMenuItem)GlobalAccess.getInstance().getDynamicHeadlineMap().
                        get(Tools.RECORDER).get(2).getMenuComponent(0)).setSelected(false);
        GUIOperations.getRecorderXml().setEloading(false);
        GUIOperations.getRecorderXml().setIsPayment(true);
        GUIOperations.setOverloadElements(new LinkedList<>());
        GUIOperations.getRecorderXml().setGeschenkskarte(false);
        GUIOperations.getRecorderXml().setSysCommandsEnd(new LinkedList<>());
        GUIOperations.getRecorderXml().setSysCommandsStart(new LinkedList<>());
    }

    /**
     * Method to restart the Recorder without prompting the InitDlg
     */
    public void onRestartWithoutDLG() {
        dlm.clear();
        dlm = new DisplayListModel();
        GUIOperations.setDlm(dlm);
        GUIOperations.setPayProcessStarted(false);
        GUIOperations.setPaid(false);
        GUIOperations.setPwChange(false);
        getContentPane().removeAll();
        //setVisible(false);
        dlm.clear();
        initComponents();
        GUIOperations.getRecorderXml().setBonStornoType(-1);
        paArticle = new PaArticle(this, dlm, liDisplay, tfSumme);
        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        setGUIOperationParameter();
        liDisplay.setModel(dlm);
        try {
            loadAndSetIcons();
            recorder.io.IOLoader.loadArtikel();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error");
        }

        PaArticleTable art = new PaArticleTable(null);
        jPanel16.add(art);
        tfSumme.setText("0,00");
        initTable();
        GUIOperations.getTaArtikel().setDefaultRenderer(Integer.class, new ArticleTableRenderer());
        GUIOperations.getTaArtikel().setDefaultRenderer(Double.class, new ArticleTableRenderer());
        liDisplay.setCellRenderer(new DisplayListRenderer());
//        try {
//            openInitDialog();
//        } catch (IOException ex) {
//            JOptionPane.showMessageDialog(null, "Fehler bei den Init Parameter");
//        }
        GUIOperations.setSwitchPanels(false);
        GUIOperations.setTraining(false);
        GUIOperations.setBonStarted(false);
        ((JCheckBoxMenuItem)GlobalAccess.getInstance().getDynamicHeadlineMap().
                        get(Tools.RECORDER).get(2).getMenuComponent(0)).setSelected(false);
        onSwitchActionPanels(null);
        //setVisible(true);
        //validate();
        //GUIOperations.getBtnSwitch().setEnabled(false);
        paArticle.setButtons(GUIOperations.getEanMap());
        paArticle.addDynamicFunctionalities(GUIOperations.getDynFuncMap());
        GUIOperations.getRecorderXml().setEloading(false);
        GUIOperations.getRecorderXml().setIsPayment(true);
        GUIOperations.setOverloadElements(new LinkedList<>());
        GUIOperations.getRecorderXml().setGeschenkskarte(false);
        GUIOperations.getRecorderXml().setSysCommandsEnd(new LinkedList<>());
        GUIOperations.getRecorderXml().setSysCommandsStart(new LinkedList<>());
    }

    /**
     * Method to allow inputting an EAN with the keyboard
     *
     * @param evt
     */
    private void onTypeNumber(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_onTypeNumber
        if (evt.getKeyChar() <= '9' && evt.getKeyChar() >= '0') {
            tfDigitField.setText(tfDigitField.getText() + evt.getKeyChar() + "");
        } else if (evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (!tfDigitField.getText().isEmpty()) {
                tfDigitField.setText(tfDigitField.getText().substring(0, tfDigitField.getText().length() - 1));
            }
        }
    }//GEN-LAST:event_onTypeNumber

    /**
     * Method to add "CLR" to the bon
     *
     * @param evt
     */
    private void onPrintCLR(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onPrintCLR
        GUIOperations.setBonStarted(true);
        Command cmdClear = GUIOperations.getRecorderXml().createCommand("controlkey", "function", "CLR");
        cmdClear.setDisplayText("CLR");
        GUIOperations.getDlm().addObject(cmdClear);
    }//GEN-LAST:event_onPrintCLR

    /**
     * Method to load the file for the yellow config buttons (= article buttons)
     *
     * @param evt
     */
    public void onLoadArticleConfigFile(java.awt.event.ActionEvent evt) {
        JFileChooser fc = new JFileChooser();
        if (fc.showDialog(null, "Config-File auswählen") == JFileChooser.APPROVE_OPTION) {
            GUIOperations.setEanMap(ConfigManager.loadFile(fc.getSelectedFile().getAbsoluteFile().toPath()));
        }
        paArticle.setButtons(GUIOperations.getEanMap());
        paArticle.updateUI();
    }

    /**
     * Method to open the dialog to set the authorization limits
     *
     * @param evt
     */
    public void onSetLimits(java.awt.event.ActionEvent evt) {
        AuthLimitsDlg ald = new AuthLimitsDlg(this, true);
        ald.setVisible(true);
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
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainFrame dialog = new MainFrame(new javax.swing.JFrame());
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
    private javax.swing.JPanel SwitchPanel;
    private javax.swing.JPanel actionPanel;
    private javax.swing.JButton btnSwitch;
    private javax.swing.JPanel digitPanel;
    private javax.swing.JPanel displayDigitPanel;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<Object> liDisplay;
    private javax.swing.JTextField tfDigitField;
    private javax.swing.JTextField tfSumme;
    // End of variables declaration//GEN-END:variables

    private void setIcons() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

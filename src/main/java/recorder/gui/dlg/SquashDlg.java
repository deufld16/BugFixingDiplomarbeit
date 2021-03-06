/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.gui.dlg;

import general.bl.GlobalAccess;
import general.bl.GlobalParamter;
import recorder.guiOperations.GUIOperations;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.nio.file.Paths;
import java.util.LinkedList;
import javax.swing.ImageIcon;

/**
 *
 * @author annalechner
 */
public class SquashDlg extends javax.swing.JDialog {

    private File ziel;
    private String squashText;

    /**
     * Default constructor
     * @param frame
     * @param bln 
     */
    public SquashDlg(Frame frame, boolean bln) {
        super(frame, bln);
    }
    
    /**
     * Constructor of SquashDlg
     *
     * @param parent
     * @param modal
     * @param ziel
     * @param squashText
     */
    public SquashDlg(java.awt.Frame parent, boolean modal, File ziel, String squashText) {
        super(parent, modal);
        this.ziel = ziel;
        this.squashText = squashText;
        initComponents();
        taSquash.setText(squashText);
        lbSpeicherort.setText(lbSpeicherort.getText() + " " + ziel + " gespeichert");
        setIconImage(new ImageIcon(Paths.get(System.getProperty("user.dir"), "src", "res", "img", "logo.png").toString()).getImage());
        setTitle("Squash-Text & Rekorder-Speicherort");
        setSize(new Dimension(1500, 700));
        setLocationRelativeTo(null);
        if (GUIOperations.isIsWorkflow()) {
            btCancel.setText("Zurück zum Explorer");
            btNextBon.setText("Weiteren Bon zum TestCase hinzufügen");
        } else {
            btCancel.setText("Abbrechen");
            btNextBon.setText("Nächster Bon");
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

        paCenter = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        lbSpeicherort = new javax.swing.JLabel();
        btCopyAll = new javax.swing.JButton();
        spOutput = new javax.swing.JScrollPane();
        taSquash = new javax.swing.JTextArea();
        paSouth = new javax.swing.JPanel();
        btCancel = new javax.swing.JButton();
        btNextBon = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        paCenter.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        lbSpeicherort.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lbSpeicherort.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbSpeicherort.setText("Der Rekorder wurde unter");
        lbSpeicherort.setPreferredSize(new java.awt.Dimension(1000, 100));
        jPanel1.add(lbSpeicherort, java.awt.BorderLayout.PAGE_END);

        btCopyAll.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btCopyAll.setText("Alles kopieren");
        btCopyAll.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        btCopyAll.setPreferredSize(new java.awt.Dimension(50, 41));
        btCopyAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onCopyAll(evt);
            }
        });
        jPanel1.add(btCopyAll, java.awt.BorderLayout.CENTER);

        paCenter.add(jPanel1, java.awt.BorderLayout.SOUTH);

        taSquash.setEditable(false);
        taSquash.setColumns(20);
        taSquash.setFont(new java.awt.Font("Courier New", 0, 18)); // NOI18N
        taSquash.setRows(5);
        spOutput.setViewportView(taSquash);

        paCenter.add(spOutput, java.awt.BorderLayout.CENTER);

        getContentPane().add(paCenter, java.awt.BorderLayout.CENTER);

        paSouth.setBorder(javax.swing.BorderFactory.createEmptyBorder(25, 250, 25, 250));
        paSouth.setPreferredSize(new java.awt.Dimension(1000, 100));
        paSouth.setLayout(new java.awt.GridLayout(1, 2, 40, 40));

        btCancel.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btCancel.setText("Abbrechen");
        btCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onAbbrechen(evt);
            }
        });
        paSouth.add(btCancel);

        btNextBon.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        btNextBon.setText("Nächster Bon");
        btNextBon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onNextBon(evt);
            }
        });
        paSouth.add(btNextBon);

        getContentPane().add(paSouth, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    /**
     * Method that is called when the user presses "Cancel"
     * Event, das ausgeführt wird, wenn auf Abbrechen gedrückt wurde
     *
     * @param evt
     */
    private void onAbbrechen(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onAbbrechen
        if (btCancel.getText().equalsIgnoreCase("Abbrechen")) {
            dispose();
        } else {
            GlobalAccess.getInstance().getTest_ide_main_frame().changeTool("explorer");
            GUIOperations.setIsWorkflow(false);
            GUIOperations.setSaveLocationIfWorkflow(null);
            dispose();
            GlobalParamter.getInstance().getDlg().setVisible(true);
            GlobalParamter.getInstance().getDlg().returnFromCreatingRecorder(GUIOperations.getAllCreatedBons());
            GUIOperations.setAllCreatedBons(new LinkedList<>());
            GUIOperations.setTextForInitDialog(null);
            GUIOperations.getMainframe().onRestartWithoutDLG();
        }
    }//GEN-LAST:event_onAbbrechen
    
    /**
     * Method that is called when "Nächster Bon" is pressed
     * Event, das ausgeführt wird, wenn auf Nächster Bon gedruckt wurde
     *
     * @param evt
     */
    private void onNextBon(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onNextBon
        dispose();
        if (!btNextBon.getText().equalsIgnoreCase("Nächster Bon")) {
            String parts[] = GUIOperations.getTextForInitDialog().split("_");
            String text_for_init = parts[0] + "_" + parts[1] + "_" + parts[2] + "_" + (Integer.parseInt(parts[3]) + 1);
            GUIOperations.setTextForInitDialog(text_for_init);
        }
        GUIOperations.setPaid(false);
        GUIOperations.onRestart();
    }//GEN-LAST:event_onNextBon

    /**
     * Method that copies the text from the text area
     *
     * @param evt
     */
    private void onCopyAll(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onCopyAll
        StringSelection stringSelection = new StringSelection(taSquash.getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }//GEN-LAST:event_onCopyAll

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                SquashDlg dialog = new SquashDlg(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btCopyAll;
    private javax.swing.JButton btNextBon;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lbSpeicherort;
    private javax.swing.JPanel paCenter;
    private javax.swing.JPanel paSouth;
    private javax.swing.JScrollPane spOutput;
    private javax.swing.JTextArea taSquash;
    // End of variables declaration//GEN-END:variables
}

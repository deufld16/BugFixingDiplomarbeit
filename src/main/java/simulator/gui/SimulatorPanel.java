/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator.gui;

import analyzer.bl.AnalyzerManager;
import general.beans.io_objects.CommandRun;
import general.beans.io_objects.ExplorerLayer;
import general.beans.io_objects.ProjectRun;
import general.beans.io_objects.TestCaseRun;
import general.beans.io_objects.TestGroupRun;
import general.bl.GlobalAccess;
import general.bl.GlobalParamter;
import general.io.Mapper;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ScrollPaneConstants;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;
import simulator.beans.Kasse;
import simulator.bl.CommandExecutionManager;
import simulator.bl.ExecutionManager;
import simulator.bl.SimulatorActionListener;

/**
 *
 * @author Lukas Krobath
 */
public class SimulatorPanel extends javax.swing.JPanel {

    private DefaultListModel<ExplorerLayer> dlm;
    private static final Color[] SLICECOLOR = new Color[]{new Color(255, 0, 0), new Color(100, 100, 100), new Color(0, 255, 0), new Color(14, 36, 204)};
    private PieChart ges;

    /**
     * Creates a new SimulatorPanel
     */
    public SimulatorPanel() {
        initComponents();
        dlm = new DefaultListModel<>();
        ExecutionManager.getInstance().setDlm(dlm);
        liTestvorgang.setModel(dlm);
        ExecutionManager.getInstance().setEpLog(epLog);
        ExecutionManager.getInstance().setParent(this);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        liTestvorgang.setCellRenderer(new GegenstandRenderer());

        ges = new PieChartBuilder().theme(Styler.ChartTheme.XChart).title("Gesamtübersicht").build();
        ges.addSeries("Fehlgeschlagen", 1);
        ges.addSeries("Wird bearbeitet", 1);
        ges.addSeries("Erfolgreich", 1);
        ges.addSeries("Ausstehend", 1);
        ges.getStyler().setSeriesColors(ExecutionManager.getSLICECOLOR());
        paGeneral.add(new XChartPanel(ges));
        ExecutionManager.getInstance().setLiTestvorgang(liTestvorgang);

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
        paLeft = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        liTestvorgang = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        paLog = new javax.swing.JPanel();
        paGeneral = new javax.swing.JPanel();
        paEinzel = new javax.swing.JPanel();
        paS = new javax.swing.JPanel();
        paSouth = new javax.swing.JPanel();
        btTVStart = new javax.swing.JButton();
        btTVStop = new javax.swing.JButton();
        btNextStep = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        epLog = new javax.swing.JEditorPane();

        setName(""); // NOI18N
        setPreferredSize(new java.awt.Dimension(1000, 580));
        setLayout(new java.awt.BorderLayout());

        paCenter.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        paCenter.setPreferredSize(new java.awt.Dimension(550, 263));
        paCenter.setLayout(new java.awt.BorderLayout(25, 25));

        paLeft.setPreferredSize(new java.awt.Dimension(350, 100));
        paLeft.setLayout(new java.awt.BorderLayout(25, 25));

        liTestvorgang.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jScrollPane1.setViewportView(liTestvorgang);

        paLeft.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel1.setText("Testgegenstände:");
        paLeft.add(jLabel1, java.awt.BorderLayout.NORTH);

        paCenter.add(paLeft, java.awt.BorderLayout.WEST);

        paLog.setLayout(new java.awt.GridLayout(1, 2));

        paGeneral.setLayout(new java.awt.BorderLayout());
        paLog.add(paGeneral);

        paEinzel.setLayout(new java.awt.GridLayout(2, 2));
        paLog.add(paEinzel);

        paCenter.add(paLog, java.awt.BorderLayout.CENTER);

        add(paCenter, java.awt.BorderLayout.CENTER);

        paS.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        paS.setPreferredSize(new java.awt.Dimension(600, 340));
        paS.setLayout(new java.awt.BorderLayout(25, 25));

        paSouth.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
        paSouth.setPreferredSize(new java.awt.Dimension(350, 185));
        paSouth.setLayout(new java.awt.GridLayout(3, 1, 25, 10));

        btTVStart.setBackground(new java.awt.Color(51, 153, 0));
        btTVStart.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btTVStart.setForeground(new java.awt.Color(255, 255, 255));
        btTVStart.setText("Testvorgang starten");
        btTVStart.setPreferredSize(new java.awt.Dimension(215, 50));
        btTVStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onStart(evt);
            }
        });
        paSouth.add(btTVStart);

        btTVStop.setBackground(new java.awt.Color(255, 0, 0));
        btTVStop.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btTVStop.setForeground(new java.awt.Color(255, 255, 255));
        btTVStop.setText("Testvorgang abbrechen");
        btTVStop.setEnabled(false);
        btTVStop.setPreferredSize(new java.awt.Dimension(247, 80));
        btTVStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onStop(evt);
            }
        });
        paSouth.add(btTVStop);

        btNextStep.setBackground(new java.awt.Color(0, 0, 255));
        btNextStep.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btNextStep.setForeground(new java.awt.Color(255, 255, 255));
        btNextStep.setText("Nächster Schritt >");
        btNextStep.setEnabled(false);
        btNextStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onNextStep(evt);
            }
        });
        paSouth.add(btNextStep);

        paS.add(paSouth, java.awt.BorderLayout.WEST);

        scrollPane.setPreferredSize(new java.awt.Dimension(111, 250));

        epLog.setContentType("text/html"); // NOI18N
        epLog.setText("<html>\r\n  <body>\r\n    <h1>Log Nachrichten</h1>\n  </body>\r\n</html>\r\n");
        epLog.setFocusable(false);
        scrollPane.setViewportView(epLog);

        paS.add(scrollPane, java.awt.BorderLayout.CENTER);

        add(paS, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Starts a simulation process
     *
     * @param evt : Action Event of calling
     */
    private void onStart(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onStart
        try {
            for (ExplorerLayer layer : ExecutionManager.getInstance().getTargets()) {
                System.out.println(layer.getDurchlauf_gegenstand());
            }
            ExecutionManager.getInstance().startSimulation();
            btTVStop.setEnabled(true);
            btTVStart.setEnabled(false);
        } catch (Exception e) {
            Object[] options = {"OK"};
            int n = JOptionPane.showOptionDialog(this,
                    "Es wurde kein Testsystem ausgewählt!\nSie werden automatisch in die Einstellungen weitergeleitet", "Fehler",
                    JOptionPane.PLAIN_MESSAGE,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    options,
                    options[0]);
            GlobalAccess.getInstance().getTest_ide_main_frame().changeTool("settings");
        }
    }//GEN-LAST:event_onStart
    /**
     * Stops a simulation process
     *
     * @param evt : Action Event of calling
     */
    private void onStop(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onStop
        ExecutionManager.getInstance().stopSimulation();
        btTVStop.setEnabled(false);
        btTVStart.setEnabled(true);
    }//GEN-LAST:event_onStop

    private void onNextStep(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onNextStep
        ExecutionManager.getInstance().setStepLock(ExecutionManager.getInstance().getStepLock() + 1);
        btNextStep.setEnabled(ExecutionManager.getInstance().isStepLockActivated());
    }//GEN-LAST:event_onNextStep

    public void tvFinished() {
        btTVStop.setEnabled(false);
        btTVStart.setEnabled(true);
    }

    public JPanel getPaEinzel() {
        return paEinzel;
    }

    public void setPaEinzel(JPanel paEinzel) {
        this.paEinzel = paEinzel;
    }

    public PieChart getGes() {
        return ges;
    }

    public void setGes(PieChart ges) {
        this.ges = ges;
    }

    public JButton getBtNextStep() {
        return btNextStep;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btNextStep;
    private javax.swing.JButton btTVStart;
    private javax.swing.JButton btTVStop;
    private javax.swing.JEditorPane epLog;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList<ExplorerLayer> liTestvorgang;
    private javax.swing.JPanel paCenter;
    private javax.swing.JPanel paEinzel;
    private javax.swing.JPanel paGeneral;
    private javax.swing.JPanel paLeft;
    private javax.swing.JPanel paLog;
    private javax.swing.JPanel paS;
    private javax.swing.JPanel paSouth;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}

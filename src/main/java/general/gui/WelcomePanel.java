/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.gui;

import general.bl.GlobalAccess;
import java.awt.BorderLayout;
import java.nio.file.Paths;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * Panel that is displayed on startup
 * 
 * @author Maximilian Strohmaier
 */
public class WelcomePanel extends javax.swing.JPanel {

    private Test_IDE_MainFrame mainFrame = GlobalAccess.getInstance().getTest_ide_main_frame();
    
    /**
     * Creates new form DashboardPanel
     */
    public WelcomePanel() {
        initComponents();
        
        ImageIcon icon = new ImageIcon(
                Paths.get(
                    System.getProperty("user.dir"), "src", "main", "java", 
                        "general", "res", "img", "logo.png")
                .toString());
        lbIcon.setIcon(icon);
    }
    
    /**
     * Method to resize necessary components in the IDE 
     * after the user decides to proceed
     */
    private void resizeOnContinue() {        
        mainFrame.setKeyListener();
        
        GlobalAccess.getInstance().setHidden(false);
        if (mainFrame.getContentPane().getSize().width < 1250) {
            mainFrame.getPaSideBar().setSize(50,
            mainFrame.getContentPane().getSize().height);
        } else {
            mainFrame.getPaSideBar().setSize(75,
            mainFrame.getContentPane().getSize().height);
        }
        mainFrame.add(BorderLayout.WEST, mainFrame.getPaSideBar());
    }
    
    /**
     * Method to initiate the creation of a new project
     */
    public void createProject() {
        boolean projectCreated = GlobalAccess.getInstance().getPaExplorer().createProject();
        if(projectCreated) {
            mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            resizeOnContinue();
            mainFrame.changeTool("explorer");
        }
    }
    
    /**
     * Method to initiate the creation of a new recorder file
     */
    public void createRecorder() {
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        resizeOnContinue();
        mainFrame.changeTool("recorder");
    }
    
    /**
     * Method to initiate the whole execution
     */
    public void startExecution() {
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        resizeOnContinue();
        mainFrame.changeTool("remote");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        lbIcon = new javax.swing.JLabel();
        btNewProject = new javax.swing.JButton();
        btNewRecorder = new javax.swing.JButton();
        btRun = new javax.swing.JButton();
        btStats = new javax.swing.JButton();
        lbTitle = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(1100, 800));
        setLayout(new java.awt.GridBagLayout());

        lbIcon.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbIcon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(lbIcon, gridBagConstraints);

        btNewProject.setBackground(new java.awt.Color(255, 255, 255));
        btNewProject.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btNewProject.setText("Neues Projekt erstellen");
        btNewProject.setPreferredSize(new java.awt.Dimension(295, 39));
        btNewProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onCreateProject(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(49, 0, 7, 0);
        add(btNewProject, gridBagConstraints);

        btNewRecorder.setBackground(new java.awt.Color(255, 255, 255));
        btNewRecorder.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btNewRecorder.setText("Neuen Rekorder erstellen");
        btNewRecorder.setPreferredSize(new java.awt.Dimension(295, 39));
        btNewRecorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onCreateRecorder(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 7, 0);
        add(btNewRecorder, gridBagConstraints);

        btRun.setBackground(new java.awt.Color(255, 255, 255));
        btRun.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btRun.setText("Gesamtdurchlauf starten");
        btRun.setPreferredSize(new java.awt.Dimension(295, 39));
        btRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onRun(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 7, 0);
        add(btRun, gridBagConstraints);

        btStats.setBackground(new java.awt.Color(255, 255, 255));
        btStats.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        btStats.setText("Weiter zum Dashboard");
        btStats.setPreferredSize(new java.awt.Dimension(295, 39));
        btStats.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onShowStats(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 7, 0);
        add(btStats, gridBagConstraints);

        lbTitle.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        lbTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbTitle.setText("Rewe DStore Test IDE");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(24, 0, 0, 0);
        add(lbTitle, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Method that is called when the user clicks on the create-project-button
     * 
     * @param evt 
     */
    private void onCreateProject(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onCreateProject
        createProject();
    }//GEN-LAST:event_onCreateProject

    /**
     * Method that is called when the user clicks on the create-recorder-button
     * 
     * @param evt 
     */
    private void onCreateRecorder(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onCreateRecorder
        createRecorder();
    }//GEN-LAST:event_onCreateRecorder

    /**
     * Method that is called when the user clicks on the run-all-button
     * 
     * @param evt 
     */
    private void onRun(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onRun
        startExecution();
    }//GEN-LAST:event_onRun

    /**
     * Method that is called when the user clicks on the continue-to-dashboard-button
     * 
     * @param evt 
     */
    private void onShowStats(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onShowStats
        // TODO add your handling code here:
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        resizeOnContinue();
        mainFrame.changeTool("dashboard");
    }//GEN-LAST:event_onShowStats


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btNewProject;
    private javax.swing.JButton btNewRecorder;
    private javax.swing.JButton btRun;
    private javax.swing.JButton btStats;
    private javax.swing.JLabel lbIcon;
    private javax.swing.JLabel lbTitle;
    // End of variables declaration//GEN-END:variables
}

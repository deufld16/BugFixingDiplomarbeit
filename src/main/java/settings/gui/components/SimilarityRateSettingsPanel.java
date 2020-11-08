/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package settings.gui.components;

import analyzer.enums.ResultFileType;
import analyzer.io.ResultsIO;
import general.bl.GlobalParamter;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 * Panel that enables the user to set the similarity rate for
 * a specific Result File Type
 * 
 * @author Maximilian Strohmaier
 */
public class SimilarityRateSettingsPanel extends javax.swing.JPanel {

    private ResultFileType type;
    private GlobalParamter gp = GlobalParamter.getInstance();
    
    /**
     * Creates new form SimilarityRateSettingsPanel
     * @param type
     */
    public SimilarityRateSettingsPanel(ResultFileType type) {
        initComponents();
        this.type = type;
        lbType.setText(type.getDescription());
        slRate.setValue((int) (type.getSimilarityRate() * 100));
        tfRate.setText(slRate.getValue()+"");
    }
    
    /***
     * This method is to save the changed similarity rate settings to the 
     * corresponding .properties file
     */
    private void saveSettings() {
        Properties props = new Properties();
        for (ResultFileType type : ResultFileType.values()) {
            props.setProperty(type.getDescription(), type.getSimilarityRate()+"");
        } 
        ResultsIO.saveProperties(gp.getSIMILARITY_RATES_FILE(), props);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        paInfo = new javax.swing.JPanel();
        lbType = new javax.swing.JLabel();
        paSelection = new javax.swing.JPanel();
        tfRate = new javax.swing.JTextField();
        lbProz = new javax.swing.JLabel();
        slRate = new javax.swing.JSlider();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new java.awt.BorderLayout(1, 0));

        paInfo.setBackground(new java.awt.Color(255, 255, 255));
        paInfo.setLayout(new java.awt.GridLayout(1, 2, 8, 0));

        lbType.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbType.setText("Printer:");
        lbType.setPreferredSize(new java.awt.Dimension(170, 22));
        paInfo.add(lbType);

        paSelection.setBackground(new java.awt.Color(255, 255, 255));
        paSelection.setLayout(new java.awt.GridLayout(1, 2));

        tfRate.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        tfRate.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        tfRate.setText("jTextField1");
        tfRate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                onTfRateFocusLost(evt);
            }
        });
        tfRate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                onEnterRate(evt);
            }
        });
        paSelection.add(tfRate);

        lbProz.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        lbProz.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbProz.setText("%");
        paSelection.add(lbProz);

        paInfo.add(paSelection);

        add(paInfo, java.awt.BorderLayout.WEST);

        slRate.setBackground(new java.awt.Color(255, 255, 255));
        slRate.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        slRate.setMajorTickSpacing(10);
        slRate.setMinorTickSpacing(1);
        slRate.setPaintLabels(true);
        slRate.setPaintTicks(true);
        slRate.setSnapToTicks(true);
        slRate.setValue(90);
        slRate.setPreferredSize(new java.awt.Dimension(200, 35));
        slRate.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                onChange(evt);
            }
        });
        add(slRate, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Method that is called when the value of the slider changes
     * 
     * @param evt 
     */
    private void onChange(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_onChange
        // TODO add your handling code here:
        tfRate.setText(slRate.getValue()+"");
        type.setSimilarityRate(slRate.getValue() / 100.);
        saveSettings();
    }//GEN-LAST:event_onChange

    /**
     * Method that is called when the user enters a rate via the keyboard
     * 
     * @param evt 
     */
    private void onEnterRate(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_onEnterRate
        // TODO add your handling code here:
        if(!evt.isActionKey()) {
            try {
                String text = tfRate.getText();
                if(text == null || text.equals("")) {
                    return;
                }
                int value = Integer.parseInt(text);
                if(value < 0 || value > 100) {
                    throw new NumberFormatException();
                }
                slRate.setValue(value);
                type.setSimilarityRate(slRate.getValue() / 100.);
                saveSettings();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Nur ganzzahlige Werte zwischen 0 und 100 erlaubt!");
                tfRate.setText(slRate.getValue()+"");
            }
        }
    }//GEN-LAST:event_onEnterRate

    /**
     * Method that is called when rate-input-field looses its focus
     * (e.g., when the user clicks somewhere else)
     * 
     * @param evt 
     */
    private void onTfRateFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_onTfRateFocusLost
        // TODO add your handling code here:
       if(tfRate.getText() == null || tfRate.getText().equals("")) {
            tfRate.setText(slRate.getValue()+"");
            type.setSimilarityRate(slRate.getValue() / 100.);
            saveSettings();
        } 
    }//GEN-LAST:event_onTfRateFocusLost


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lbProz;
    private javax.swing.JLabel lbType;
    private javax.swing.JPanel paInfo;
    private javax.swing.JPanel paSelection;
    private javax.swing.JSlider slRate;
    private javax.swing.JTextField tfRate;
    // End of variables declaration//GEN-END:variables
}

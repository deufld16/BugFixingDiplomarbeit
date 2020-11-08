/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.gui;

import recorder.gui.dlg.GewinnspielDlg;
import recorder.gui.dlg.OverloadValuesDlg;
import recorder.gui.dlg.OverloadDlg;
import recorder.gui.dlg.GeschenkkarteDlg;
import recorder.beans.Operation;
import recorder.enums.XMLOperations;
import recorder.guiOperations.GUIOperations;
import recorder.io.IOLoader;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import recorder.beans.Command;
import recorder.beans.AndereZahlungsmittel;

/**
 *
 * @author Florian
 */
public class PaFinance extends javax.swing.JPanel {

    private JButton bt10;
    private JButton bt20;
    private JButton bt5;
    private JButton bt50;
    private JButton btFinanceOption6;
    private JButton btFinanceOptionBig1;
    private JButton btFinanceOption7;
    private JButton btFinanceOption8;
    private JButton btFinanceOption9;
    private JButton btFinanceOption10;
    private JButton btFinanceOption11;
    private JButton btFinanceOption12;
    private JButton btFinanceOptionBig2;
    private JButton btFinanceOption1;
    private JButton btFinanceOption2;
    private JButton btFinanceOption3;
    private JButton btFinanceOption4;
    private JButton btFinanceOption5;
    private JButton btEmpty1;
    private JButton btEmpty2;
    private JPanel paCashSymbols;
    private JPanel paCenter;
    private JPanel paCenter1;
    private JPanel paCenter2;
    private JPanel paEmpty;
    private JPanel paEmpty1;
    private JPanel paEmpty2;
    private JPanel paFinanceOptionBtns1;
    private JPanel paFinanceOptionBtns2;
    private JPanel paFinanceOptionBtns3;
    private JPanel paFinanceOptionBtns4;
    private JPanel jPanel1;
    private JPanel paNorth;
    private boolean paid = false;

    /**
     * Creates new form PaFinance
     */
    public PaFinance() {
        initComponents();
        try {
            loadCashSymbols();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Error while loading cash symbols");
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

        paNorth = new javax.swing.JPanel();
        paEmpty = new javax.swing.JPanel();
        btEmpty1 = new javax.swing.JButton();
        btEmpty2 = new javax.swing.JButton();
        paCashSymbols = new javax.swing.JPanel();
        bt5 = new javax.swing.JButton();
        bt20 = new javax.swing.JButton();
        bt10 = new javax.swing.JButton();
        bt50 = new javax.swing.JButton();
        paCenter = new javax.swing.JPanel();
        paCenter1 = new javax.swing.JPanel();
        paFinanceOptionBtns1 = new javax.swing.JPanel();
        btFinanceOption1 = new javax.swing.JButton();
        btFinanceOption2 = new javax.swing.JButton();
        btFinanceOption5 = new javax.swing.JButton();
        btFinanceOption3 = new javax.swing.JButton();
        paFinanceOptionBtns2 = new javax.swing.JPanel();
        btFinanceOptionBig1 = new javax.swing.JButton();
        paCenter2 = new javax.swing.JPanel();
        paFinanceOptionBtns3 = new javax.swing.JPanel();
        btFinanceOption7 = new javax.swing.JButton();
        btFinanceOption8 = new javax.swing.JButton();
        btFinanceOption11 = new javax.swing.JButton();
        btFinanceOption12 = new javax.swing.JButton();
        paFinanceOptionBtns4 = new javax.swing.JPanel();
        btFinanceOptionBig2 = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        paNorth.setPreferredSize(new java.awt.Dimension(443, 150));
        paNorth.setLayout(new java.awt.BorderLayout());

        paEmpty.setLayout(new java.awt.GridLayout(2, 1, 0, 4));

        btEmpty1.setBackground(new java.awt.Color(255, 255, 255));
        btEmpty1.setEnabled(false);
        btEmpty1.setPreferredSize(new java.awt.Dimension(100, 73));
        paEmpty.add(btEmpty1);

        btEmpty2.setBackground(new java.awt.Color(255, 255, 255));
        btEmpty2.setEnabled(false);
        btEmpty2.setPreferredSize(new java.awt.Dimension(100, 73));
        paEmpty.add(btEmpty2);

        paNorth.add(paEmpty, java.awt.BorderLayout.LINE_END);

        paCashSymbols.setLayout(new java.awt.GridLayout(2, 2, 3, 3));

        bt5.setBackground(new java.awt.Color(255, 255, 255));
        bt5.setForeground(new java.awt.Color(255, 255, 255));
        bt5.setToolTipText("");
        bt5.setName("5"); // NOI18N
        bt5.setOpaque(false);
        bt5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onPay(evt);
            }
        });
        paCashSymbols.add(bt5);

        bt20.setBackground(new java.awt.Color(255, 255, 255));
        bt20.setForeground(new java.awt.Color(255, 255, 255));
        bt20.setName("20"); // NOI18N
        bt20.setOpaque(false);
        bt20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onPay(evt);
            }
        });
        paCashSymbols.add(bt20);

        bt10.setBackground(new java.awt.Color(255, 255, 255));
        bt10.setForeground(new java.awt.Color(255, 255, 255));
        bt10.setName("10"); // NOI18N
        bt10.setOpaque(false);
        bt10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onPay(evt);
            }
        });
        paCashSymbols.add(bt10);

        bt50.setBackground(new java.awt.Color(255, 255, 255));
        bt50.setForeground(new java.awt.Color(255, 255, 255));
        bt50.setName("50"); // NOI18N
        bt50.setOpaque(false);
        bt50.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onPay(evt);
            }
        });
        paCashSymbols.add(bt50);

        paNorth.add(paCashSymbols, java.awt.BorderLayout.CENTER);

        add(paNorth, java.awt.BorderLayout.PAGE_START);

        paCenter.setLayout(new java.awt.GridLayout(2, 1));

        paCenter1.setLayout(new java.awt.BorderLayout());

        paFinanceOptionBtns1.setPreferredSize(new java.awt.Dimension(293, 165));
        paFinanceOptionBtns1.setLayout(new java.awt.GridLayout(2, 2, 0, 3));

        btFinanceOption1.setBackground(new java.awt.Color(0, 102, 0));
        btFinanceOption1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btFinanceOption1.setForeground(new java.awt.Color(255, 255, 255));
        btFinanceOption1.setText("<html>Andere Zah-<br/>lungsmittel</html>");
        btFinanceOption1.setName(""); // NOI18N
        btFinanceOption1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onAndereZahlungsmittel(evt);
            }
        });
        paFinanceOptionBtns1.add(btFinanceOption1);

        btFinanceOption2.setBackground(new java.awt.Color(187, 0, 0));
        btFinanceOption2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btFinanceOption2.setForeground(new java.awt.Color(255, 255, 255));
        btFinanceOption2.setText("<html><p>Gewinn-<br/>spiel</p></html>");
        btFinanceOption2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onGewinnspiel(evt);
            }
        });
        paFinanceOptionBtns1.add(btFinanceOption2);

        btFinanceOption5.setBackground(new java.awt.Color(0, 102, 0));
        btFinanceOption5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btFinanceOption5.setForeground(new java.awt.Color(255, 255, 255));
        btFinanceOption5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onNotSupportedYet(evt);
            }
        });
        paFinanceOptionBtns1.add(btFinanceOption5);

        btFinanceOption3.setBackground(new java.awt.Color(187, 0, 0));
        btFinanceOption3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btFinanceOption3.setForeground(new java.awt.Color(255, 255, 255));
        btFinanceOption3.setText("Übersteuern");
        btFinanceOption3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onOverload(evt);
            }
        });
        paFinanceOptionBtns1.add(btFinanceOption3);

        paCenter1.add(paFinanceOptionBtns1, java.awt.BorderLayout.LINE_START);

        paFinanceOptionBtns2.setPreferredSize(new java.awt.Dimension(150, 165));
        paFinanceOptionBtns2.setLayout(new java.awt.BorderLayout());

        btFinanceOptionBig1.setBackground(new java.awt.Color(187, 0, 0));
        btFinanceOptionBig1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btFinanceOptionBig1.setForeground(new java.awt.Color(255, 255, 255));
        btFinanceOptionBig1.setText("Bar");
        btFinanceOptionBig1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onBarExact(evt);
            }
        });
        paFinanceOptionBtns2.add(btFinanceOptionBig1, java.awt.BorderLayout.CENTER);

        paCenter1.add(paFinanceOptionBtns2, java.awt.BorderLayout.CENTER);

        paCenter.add(paCenter1);

        paCenter2.setLayout(new java.awt.BorderLayout());

        paFinanceOptionBtns3.setPreferredSize(new java.awt.Dimension(293, 165));
        paFinanceOptionBtns3.setLayout(new java.awt.GridLayout(2, 2, 0, 3));

        btFinanceOption7.setBackground(new java.awt.Color(0, 102, 0));
        btFinanceOption7.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btFinanceOption7.setForeground(new java.awt.Color(255, 255, 255));
        btFinanceOption7.setActionCommand("<html>\n<p>Zeilen-</p>Storno\n</html>");
        btFinanceOption7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onNotSupportedYet(evt);
            }
        });
        paFinanceOptionBtns3.add(btFinanceOption7);

        btFinanceOption8.setBackground(new java.awt.Color(187, 0, 0));
        btFinanceOption8.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btFinanceOption8.setForeground(new java.awt.Color(255, 255, 255));
        btFinanceOption8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onNotSupportedYet(evt);
            }
        });
        paFinanceOptionBtns3.add(btFinanceOption8);

        btFinanceOption11.setBackground(new java.awt.Color(0, 102, 0));
        btFinanceOption11.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btFinanceOption11.setForeground(new java.awt.Color(255, 255, 255));
        btFinanceOption11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onNotSupportedYet(evt);
            }
        });
        paFinanceOptionBtns3.add(btFinanceOption11);

        btFinanceOption12.setBackground(new java.awt.Color(187, 0, 0));
        btFinanceOption12.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btFinanceOption12.setForeground(new java.awt.Color(255, 255, 255));
        btFinanceOption12.setText("<html>Lade<br/>schließen</html>");
        btFinanceOption12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onCloseLade(evt);
            }
        });
        paFinanceOptionBtns3.add(btFinanceOption12);

        paCenter2.add(paFinanceOptionBtns3, java.awt.BorderLayout.LINE_START);

        paFinanceOptionBtns4.setLayout(new java.awt.BorderLayout());

        btFinanceOptionBig2.setBackground(new java.awt.Color(187, 0, 0));
        btFinanceOptionBig2.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btFinanceOptionBig2.setForeground(new java.awt.Color(255, 255, 255));
        btFinanceOptionBig2.setText("<html>Ohne Zahlung<br/>abschließen</html>");
        btFinanceOptionBig2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OnBarExactOhneTZ(evt);
            }
        });
        paFinanceOptionBtns4.add(btFinanceOptionBig2, java.awt.BorderLayout.CENTER);

        paCenter2.add(paFinanceOptionBtns4, java.awt.BorderLayout.CENTER);

        paCenter.add(paCenter2);

        add(paCenter, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Method to execute the payment
     *
     * @param evt
     */
    private void onPay(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onPay
        executePayment(Integer.parseInt(((JButton) evt.getSource()).getName()) * 100, false);
    }//GEN-LAST:event_onPay

    /**
     * Method to handle an exact payment in cash
     *
     * @param evt
     */
    private void onBarExact(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onBarExact
        try {
            if (GUIOperations.isPaid()) {
                JOptionPane.showMessageDialog(null, "Summe wurde bereits beglichen!");
                return;
            } else if (GUIOperations.getTfSumme().getText().equalsIgnoreCase("0,00")) {
                executePayment(0, true);
                return;
            }
            int moneyInCents = Integer.parseInt(GUIOperations.getTfDigitField().getText());
            if (moneyInCents != 0) {
                executePayment(moneyInCents, false);
            } else {
                JOptionPane.showMessageDialog(null, "Bitte einen Geldwert > 0 € eingeben!");
            }
        } catch (NumberFormatException ex) {
            List<Object> list = GUIOperations.getDlm().getDisplayedArtikel();
            for (Object object : list) {
                if (object instanceof Operation) {
                    if (((Operation) object).getXmlText().toString().equals("WARENRUECKNAHME")) {
                        executePayment(0, false);
                        return;
                    }
                }
            }
            JOptionPane.showMessageDialog(null, "Bitte zuerst den Geldwert über die Bildschirmtastatur eingeben!");
        }
    }//GEN-LAST:event_onBarExact

    /**
     * Method to display that some features are not supported yet
     *
     * @param evt
     */
    private void onNotSupportedYet(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onNotSupportedYet
        JOptionPane.showMessageDialog(null, "This feature is not supported yet.");
    }//GEN-LAST:event_onNotSupportedYet

    /**
     * Method to participate in a "Gewinnspiel"
     *
     * @param evt
     */
    private void onGewinnspiel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onGewinnspiel
        GewinnspielDlg gdlg = new GewinnspielDlg(null, true);
        gdlg.setVisible(true);
        String responseFileCmd = gdlg.getResponseFileCmd();
        if (responseFileCmd != null) {
            GUIOperations.getRecorderXml().setEloading(true);
            GUIOperations.getRecorderXml().setGewinnspielFile(responseFileCmd);
            GUIOperations.getDlm().addObject(new Operation(
                    responseFileCmd.endsWith("nok")
                    ? XMLOperations.GEWINNSPIEL_NOK
                    : XMLOperations.GEWINNSPIEL_OK));
        }
    }//GEN-LAST:event_onGewinnspiel

    /**
     * Method to take the necessary actions for a "Übersteuerung"
     *
     * @param evt
     */
    private void onOverload(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onOverload
        String status = null;
        OverloadDlg odlg = new OverloadDlg(null, true);
        odlg.setVisible(true);
        status = odlg.getStatus();
        if (status == null) {
            return;
        }

        boolean subIdRequired = true;
        XMLOperations xmlOperation = XMLOperations.NACHTRAEGLICHE_AENDERUNG;
        String cmd = "";
        String cmdStr = "";
        String subIdAppendix = "";
        switch (status) {
            case "kab":
                subIdRequired = false;
                cmdStr = "KAB";
                cmd = "kab";
                break;
            case "stat":
                cmdStr = "Statistik-Wert";
                cmd = "stat";
                break;
            case "stat;1":
                cmdStr = "Statistik-Anzahl";
                cmd = "stat";
                subIdAppendix = ";1";
                break;
            case "subtotal":
                cmdStr = "Zahlungsmittel";
                cmd = "subtotal";
                break;
            default:
                return;
        }

        OverloadValuesDlg ovDlg = new OverloadValuesDlg(null, true, subIdRequired);
        ovDlg.setVisible(true);

        String id = ovDlg.getId();
        String subId = subIdRequired ? ovDlg.getSubId() : "";
        if (id == null || subId == null) {
            JOptionPane.showMessageDialog(null, "Die Daten sind unumgänglich einzugeben!");
            return;
        }
        double value = ovDlg.getValue();

        String addText = String.format("%s (ID: %s |%s Wert: %.2f)",
                cmdStr,
                id,
                subIdRequired
                        ? String.format(" Sub-ID: %s |",
                                subId)
                        : "",
                value);

        Operation operation = new Operation(xmlOperation, addText);
        GUIOperations.getDlm().addObject(operation);
        GUIOperations.addOverloadElement(cmd, id, subId + subIdAppendix, value);
    }//GEN-LAST:event_onOverload

    /**
     * Method to pay the exact sum in cash
     * @param evt 
     */
    private void OnBarExactOhneTZ(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OnBarExactOhneTZ
        GUIOperations.getRecorderXml().setIsPayment(false);
        GUIOperations.getRecorderXml().createSquashText(GUIOperations.getDlm(), 0);
        GUIOperations.getRecorderXml().getArticlesForXml(GUIOperations.getDlm());
    }//GEN-LAST:event_OnBarExactOhneTZ

    /**
     * Method to pay with an alternative mean of payment
     * @param evt 
     */
    private void onAndereZahlungsmittel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onAndereZahlungsmittel
        GeschenkkarteDlg gkdlg = new GeschenkkarteDlg(GUIOperations.getMainframe(), true);
        gkdlg.setVisible(true);

        if (gkdlg.isOk()) {
            AndereZahlungsmittel selected_item = gkdlg.getSelected_element();
            Command command = GUIOperations.getRecorderXml().createCommand("payment", "pay_id", selected_item.getPay_id() + "", "pay_sub", selected_item.getPay_sub() + "");
            if (gkdlg.getCbCardID().isSelected()) {
                command.getAttributes().put("card", gkdlg.getTfCardID().getText());
            }

            if (gkdlg.getCbValid().isSelected()) {
                String tmp = !gkdlg.getRb_valid_true().isSelected() ? "true" : "false";
                command.getAttributes().put("invalid", tmp);
            }

            if (gkdlg.getCbLimit().isSelected()) {
                String tmp = gkdlg.getRbLimit_true().isSelected() ? "true" : "false";
                command.getAttributes().put("limit", tmp);
            }

            if (gkdlg.getCbOverpay().isSelected()) {
                String tmp = gkdlg.getRb_overpay_true().isSelected() ? "true" : "false";
                command.getAttributes().put("overpay", tmp);
            }

            if (gkdlg.getCbCurrency().isSelected()) {
                command.getAttributes().put("currency", gkdlg.getTfCurrency().getText());
            }

            if (gkdlg.getCbDebitor() != null) {
                if (gkdlg.getCbDebitor().isSelected()) {
                    command.getAttributes().put("debitor", gkdlg.getTfDebitor().getText());
                }

            }
            if (gkdlg.getCbNowawi() != null) {
                if (gkdlg.getCbNowawi().isSelected()) {
                    String tmp = gkdlg.getRbNowawiTrue().isSelected() ? "true" : "false";
                    command.getAttributes().put("nowawi", tmp);
                }

            }
            if (gkdlg.getCbPayAmound().isSelected()) {
                command.getAttributes().put("pay_amount", Integer.parseInt(gkdlg.getTfPayAmount().getText()));
                command.setDisplayText("Teilzahlung mit Karte " + String.format("%.2f €", Integer.parseInt(gkdlg.getTfPayAmount().getText()) / 100.0));
                Operation op = new Operation(XMLOperations.GESCHENKKARTE);
                op.setIsError(gkdlg.getCbValid().isSelected());
                op.setCommand(command);
                op.setGeschenkkarte_preis(Integer.parseInt(gkdlg.getTfPayAmount().getText()));
                if (Integer.parseInt(gkdlg.getTfPayAmount().getText()) > recorder.io.IOLoader.getGeschenkkarteLimit()) {
                    op.setNeedsAutorisation(true);
                }
                GUIOperations.getDlm().addObject(op);
                if (gkdlg.getRb_valid_true().isSelected() || !gkdlg.getCbValid().isSelected()) {
                    GUIOperations.setMoney(GUIOperations.getMoney() + Integer.parseInt(gkdlg.getTfPayAmount().getText()));
                    if (checkIsPaid()) {
                        completePayingProcess();
                        double money = GUIOperations.getMoney();
                        if (JOptionPane.showConfirmDialog(GUIOperations.getMainframe(), "Zahlung mit Karte beendet, wollen Sie noch irgendwelche Funktionen einfügen?\n"
                                + "Bei Nein wird der Bonerstellungsvorgang beendet") == 0) {
                        } else {
                            GUIOperations.getRecorderXml().createSquashText(GUIOperations.getDlm(), money);
                            GUIOperations.getRecorderXml().getArticlesForXml(GUIOperations.getDlm());
                            GUIOperations.setPaid(true);
                        }
                    }
                }
            } else {
                String displayText = (int) (Double.parseDouble(GUIOperations.getTfSumme().getText().replace(",", ".")) * 100) - GUIOperations.getMoney() / 100.0 + "";
                command.setDisplayText("Restbetrag mit Karte beglichen");
                Operation op = new Operation(XMLOperations.GESCHENKKARTE_TOTAL);
                op.setIsError(gkdlg.getCbValid().isSelected());
                op.setGeschenkkarte_preis((int) (Double.parseDouble(GUIOperations.getTfSumme().getText().replace(",", ".")) * 100) - GUIOperations.getMoney());
                op.setCommand(command);
                if ((int) (Double.parseDouble(GUIOperations.getTfSumme().getText().replace(",", ".")) * 100) - GUIOperations.getMoney() > 
                        recorder.io.IOLoader.getGeschenkkarteLimit()) {
                    op.setNeedsAutorisation(true);
                }
                GUIOperations.getDlm().addObject(op);
                if (gkdlg.getRb_valid_true().isSelected() || !gkdlg.getCbValid().isSelected()) {
                    GUIOperations.setMoney(Integer.parseInt(GUIOperations.getTfSumme().getText().replace(",", "")));
                    completePayingProcess();
                    double money = GUIOperations.getMoney();
                    if (JOptionPane.showConfirmDialog(GUIOperations.getMainframe(), "Zahlung mit Karte beendet, wollen Sie noch irgendwelche Funktionen einfügen?\n"
                            + "Bei Nein wird der Bonerstellungsvorgang beendet") == 0) {
                    } else {
                        GUIOperations.getRecorderXml().createSquashText(GUIOperations.getDlm(), money);
                        GUIOperations.getRecorderXml().getArticlesForXml(GUIOperations.getDlm());
                        GUIOperations.setPaid(true);
                    }
                }
            }
        }
    }//GEN-LAST:event_onAndereZahlungsmittel

    /**
     * Method to close the drawer
     * @param evt 
     */
    private void onCloseLade(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onCloseLade
        Command cmdCloseLade = GUIOperations.getRecorderXml().createCommand("hwcommand", "device", "drawer", "value", "0");
        cmdCloseLade.setDisplayText("Lade schließen");
        GUIOperations.getDlm().addObject(cmdCloseLade);
    }//GEN-LAST:event_onCloseLade

    /**
     * Method that invokes a payment in cash
     *
     * @param moneyInput --> Geldbetrag für Begleichen der Summe
     */
    private void executePayment(int moneyInput, boolean ignore) {
        if (GUIOperations.getDlm().getSize() != 0) {
            GUIOperations.setMoney(moneyInput + GUIOperations.getMoney());
            Operation teilzahlung = new Operation(XMLOperations.TEILZAHLUNG, String.format("%.2f €", moneyInput * 1.0 / 100), moneyInput);
            GUIOperations.getDlm().addObject(teilzahlung);
            GUIOperations.setPayProcessStarted(true);
            if (checkIsPaid()) {
                completePayingProcess();
                if (JOptionPane.showConfirmDialog(GUIOperations.getMainframe(), "Zahlung beendet, wollen Sie noch irgendwelche Funktionen einfügen?\n"
                        + "Bei Nein wird der Bonerstellungsvorgang beendet") != 0) {
                    double money = GUIOperations.getMoney();
                    GUIOperations.getRecorderXml().createSquashText(GUIOperations.getDlm(), money);
                    GUIOperations.getRecorderXml().getArticlesForXml(GUIOperations.getDlm());
                    GUIOperations.setPaid(true);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Bitte wählen sie zumindestens einen Artikel vor dem Zahlen aus!");
        }
    }

    /**
     * Method to check if the full sum has been paid
     * @return 
     */
    private boolean checkIsPaid() {
        return Math.round(Double.parseDouble(GUIOperations.getTfSumme().getText().replace(",", ".")) * 100)
                <= GUIOperations.getMoney() && !GUIOperations.isPaid();
    }

    /**
     * Method to complete the paying process
     */
    private void completePayingProcess() {
        GUIOperations.getDlm().addObject(
                new Operation(
                        XMLOperations.SUMME,
                        String.format("%.2f",
                                GUIOperations.getMoney() / 100.
                                - Double.parseDouble(
                                        GUIOperations.getTfSumme()
                                                .getText()
                                                .replace(",", "."))),
                        (int) (GUIOperations.getMoney()
                        - Double.parseDouble(
                                GUIOperations.getTfSumme()
                                        .getText()
                                        .replace(",", ".")) * 100)));
    }

    /*
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bt10;
    private javax.swing.JButton bt20;
    private javax.swing.JButton bt5;
    private javax.swing.JButton bt50;
    private javax.swing.JButton btEmpty1;
    private javax.swing.JButton btEmpty2;
    private javax.swing.JButton btFinanceOption1;
    private javax.swing.JButton btFinanceOption11;
    private javax.swing.JButton btFinanceOption12;
    private javax.swing.JButton btFinanceOption2;
    private javax.swing.JButton btFinanceOption3;
    private javax.swing.JButton btFinanceOption5;
    private javax.swing.JButton btFinanceOption7;
    private javax.swing.JButton btFinanceOption8;
    private javax.swing.JButton btFinanceOptionBig1;
    private javax.swing.JButton btFinanceOptionBig2;
    private javax.swing.JPanel paCashSymbols;
    private javax.swing.JPanel paCenter;
    private javax.swing.JPanel paCenter1;
    private javax.swing.JPanel paCenter2;
    private javax.swing.JPanel paEmpty;
    private javax.swing.JPanel paFinanceOptionBtns1;
    private javax.swing.JPanel paFinanceOptionBtns2;
    private javax.swing.JPanel paFinanceOptionBtns3;
    private javax.swing.JPanel paFinanceOptionBtns4;
    private javax.swing.JPanel paNorth;
    // End of variables declaration//GEN-END:variables
*/
    /**
     * Ladet die Cashsymbole mit den angegebenen Proportionen -Ressourcen aus
     * res/img
     *
     * @throws IOException
     */
    private void loadCashSymbols() throws IOException {
        bt5.setIcon(IOLoader.loadImage("euro_5.png", 128, 61));
        bt10.setIcon(IOLoader.loadImage("euro_10.png", 128, 61));
        bt20.setIcon(IOLoader.loadImage("euro_20.png", 128, 61));
        bt50.setIcon(IOLoader.loadImage("euro_50.png", 128, 61));
    }
}

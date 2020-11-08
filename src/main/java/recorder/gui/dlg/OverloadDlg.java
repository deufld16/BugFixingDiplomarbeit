/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.gui.dlg;

/**
 *
 * @author Maxi
 */
public class OverloadDlg extends ELoadingDlg {

    /**
     * Constructor that sets all texts and ActionCommands
     * @param parent
     * @param modal 
     */
    public OverloadDlg(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        getBtOption1().setText("KAB");
        getBtOption1().setActionCommand("kab");

        getBtOption2().setText("Statistik-Wert");
        getBtOption2().setActionCommand("stat");

        getBtOption3().setText("Statistik-Anzahl");
        getBtOption3().setActionCommand("stat;1");

        getBtOption4().setText("Zahlungsmittel");
        getBtOption4().setActionCommand("subtotal");
    }

}

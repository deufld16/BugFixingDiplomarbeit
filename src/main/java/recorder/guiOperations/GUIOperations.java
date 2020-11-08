/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.guiOperations;

import recorder.beans.Article;
import recorder.beans.Command;
import recorder.bl.ArtikelTableModel;
import recorder.bl.DisplayListModel;
import recorder.gui.dlg.ELoadingDlg;
import recorder.gui.dlg.JugendschutzDlg;
import recorder.gui.MainFrame;
import recorder.gui.PaArticle;
import recorder.gui.PaFinance;
import recorder.gui.dlg.WarenruecknahmePreisDlg;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import recorder.beans.Karte;
import recorder.xml.RecorderXML;

/**
 * s
 *
 * @author Florian
 */
public class GUIOperations {

    /**
     * Globally accessable variables
     */
    private static boolean switchPanels = false;
    private static DisplayListModel dlm;
    private static PaArticle paArticle;
    private static JPanel switchPanel;
    private static ArtikelTableModel atm;
    private static JTable taArtikel;
    private static JTextField tfSumme;
    private static JTextField tfDigitField;
    private static int money;
    private static Map<String, Object> initParams = new HashMap<>();
    private static List<String> eanList = new LinkedList<>();
    private static Map<String, String> eanMap = new HashMap();
    private static Map<String, String> dynFuncMap = new HashMap();
    private static File articleConfigFile;
    private static File funtionConfigFile;
    private static boolean payProcessStarted;
    private static boolean paid;
    private static JButton btnSwitch;
    private static Path inputPath = null;
    private static MainFrame mainframe = null;
    private static boolean login = false;
    private static boolean logout = false;
    private static RecorderXML recorderXml = new RecorderXML();
    private static List<Command> overloadElements = new LinkedList<>();
    private static Path resPath = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "recorder", "res");
    private static boolean needsInit = true;
    private static boolean isWorkflow = false;
    private static Path saveLocationIfWorkflow = null;
    private static List<Path> allCreatedBons = new LinkedList<>();
    private static String textForInitDialog = null;
    private static boolean pwChange = false;
    private static boolean training = false;
    private static boolean bonStarted = false;

    /**
     * Method that switches between paFinance and paArticle
     */
    public static void onSwitch() {
        if (switchPanels) {
            //Insert Finanzpanel
            switchPanel.removeAll();
            PaFinance f = new PaFinance();
            switchPanel.add(f);
            switchPanel.updateUI();
            switchPanels = false;
        } else {
            //insert ActionPanel
            switchPanel.removeAll();
            switchPanel.add(paArticle);
            switchPanel.updateUI();
            switchPanels = true;
            //btnSwitch.setEnabled(false);
        }
    }

    /**
     * Method that adds an article from the article table
     */
    public static void addArticle() {
        double summe = 0.0;
        if (!payProcessStarted) {
            int[] indexes = taArtikel.getSelectedRows();
            for (int index : indexes) {
                GUIOperations.getRecorderXml().setIsPayment(true);
                //Artikel, der eingefügt wird
                Article tmp = atm.getFilteredArtikels().get(index);
                double price = 0.0;
                if (tmp.getPreis() == 0.0) {
                    try {
                        int help = Integer.parseInt(
                                JOptionPane.showInputDialog(mainframe.getParent(),
                                        "Bitte geben Sie den Preis für das Produkt " + tmp.getArticleName() + " ein (in Cent)"));
                        price = help / 100.0;
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(mainframe.getParent(), "Artikel konnte nicht eingefügt werden, "
                                + "da keine valide Zahl eingegeben worden ist");
                        continue;
                    }
                } else {
                    price = tmp.getPreis();
                }
                if (recorderXml.getBonStornoType() != -1) {
                    WarenruecknahmePreisDlg wpdlg = new WarenruecknahmePreisDlg(mainframe, true, tmp.getPreis());
                    wpdlg.setVisible(true);
                    price = wpdlg.getPreis();
                    price *= -1;
                }

                if (tmp.getCategory().equalsIgnoreCase("Preis EAN")) {
                    price = -1;
                    do {
                        try {
                            price = Integer.parseInt(JOptionPane.showInputDialog(mainframe, "Geben Sie den Preis für diesen Artikel mit Preis EAN ein"));
                        } catch (NumberFormatException ex) {
                            int cancel = JOptionPane.showConfirmDialog(mainframe, "Es wurde keine gültige Zahl eingeben! Wollen Sie den Kassiervorgang dieses Artikels abbrechen?");
                            if (cancel == 0) {
                                return;
                            }
                        }
                    } while (price == -1);
                    price = price / 100;
                }

                int amount = -1;
                if (tmp.getCategory().equalsIgnoreCase("Gewichts EAN")) {
                    do {
                        try {
                            amount = Integer.parseInt(JOptionPane.showInputDialog(mainframe, "Geben Sie die Menge für diesen Artikel mit Gewichts EAN ein (in Gramm)"));
                        } catch (NumberFormatException ex) {
                            int cancel = JOptionPane.showConfirmDialog(mainframe, "Es wurde keine gültige Zahl eingeben! Wollen Sie den Kassiervorgang dieses Artikels abbrechen?");
                            if (cancel == 0) {
                                return;
                            }
                        }
                    } while (amount == -1);
                }

                Article art = new Article(tmp.getXmlArticleName(), tmp.isLeergut(),
                        tmp.isJugendSchutz(), tmp.getEan(), tmp.getArticleName(),
                        tmp.isPfand(), tmp.getPfandArtikel(), price, tmp.isRabatt(), tmp.getUst(),
                        tmp.isWeight(), tmp.getCategory(), tmp.isEloading(), tmp.isSerialNrRequired());
                art.setIsAbfrage(tmp.isIsAbfrage());
                if (amount != -1) {
                    art.setGewichts_ean_menge(amount);
                }
                JugendschutzDlg jdlg = null;
                if (tmp.isJugendSchutz() && !tmp.isIsAbfrage()) {
                    jdlg = new JugendschutzDlg(mainframe, true);
                    jdlg.setVisible(true);
                    if (jdlg.isOk()) {
                        art.setJugendSchutzOk(true);
                    }
                }
                if (tmp.isEloading()) {
                    if (dlm.getAmount() <= 1) {
                        if (tmp.isJugendSchutz() && !tmp.isIsAbfrage()) {
                            if (jdlg.isOk()) {
                                String status = null;
                                do {
                                    ELoadingDlg edlg = new ELoadingDlg(mainframe, true);
                                    edlg.setVisible(true);
                                    status = edlg.getStatus();
                                } while (status == null);
                                art.setEloadingState(status);
                                if (status.equalsIgnoreCase("server_offline") || status.equalsIgnoreCase("aufladung_nok")) {
                                    art.setPriceZero(true);
                                }
                            }
                        } else {
                            String status = null;
                            if (!tmp.isIsAbfrage()) {
                                do {
                                    ELoadingDlg edlg = new ELoadingDlg(mainframe, true);
                                    edlg.setVisible(true);
                                    status = edlg.getStatus();

                                } while (status == null);
                                art.setEloadingState(status);
                                if (status.equalsIgnoreCase("server_offline") || status.equalsIgnoreCase("aufladung_nok")) {
                                    art.setPriceZero(true);
                                }
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(mainframe, "E-Loading Artikel können nicht mit einer Menge > 1 eingefügt werden");
                        art.setEloadingAmmountOk(false);
                    }
                }

                int weight = -1;
                Path weight_path = null;
                if (tmp.isWeight() && !tmp.isIsAbfrage()) {
                    int result = JOptionPane.showConfirmDialog(mainframe, "Es wurde ein Gewichtsartikel ausgewählt. Wollen sie diesen "
                            + "Artikel mithilfe einer Tastatureingabe abwiegen?");
                    if (result == 0) {
                        do {
                            try {
                                weight = Integer.parseInt(JOptionPane.showInputDialog(mainframe, "Geben Sie bitte das Gewicht für den Artikel ein"));
                            } catch (NumberFormatException ex) {
                                int cancel = JOptionPane.showConfirmDialog(mainframe, "Es wurde keine gültige Zahl eingeben! Wollen Sie den Kassiervorgang dieses Artikels abbrechen?");
                                if (cancel == 0) {
                                    return;
                                }
                            }
                        } while (weight == -1);
                    } else {
                        JFileChooser fc = new JFileChooser(System.getProperty("user.home") + File.separator + "Documents");
                        FileNameExtensionFilter filter = new FileNameExtensionFilter("txt-Dateien", "txt");
                        fc.setFileFilter(filter);
                        if (fc.showOpenDialog(mainframe) == JFileChooser.APPROVE_OPTION) {
                            weight_path = fc.getSelectedFile().toPath();
                        } else {
                            JOptionPane.showMessageDialog(mainframe, "Es wurde keine Datei ausgewählt! Der Kassiervorgang dieses Artikels wird abgebrochen");
                            return;
                        }
                    }
                }

                if (tmp.isSerialNrRequired() && !tmp.isIsAbfrage()) {
                    //Seriennummer einlesen
                    if ((tmp.isJugendSchutzOk() && tmp.isJugendSchutz()) || !tmp.isJugendSchutz()) {
                        String serNr;
                        do {
                            serNr = JOptionPane.showInputDialog(null,
                                    "Bitte geben Sie eine Seriennummer ein:");
                        } while (serNr == null || serNr.equals("") || !serNr.matches("[0-9]+"));
                        art.setSerialNr(serNr);
                    }
                }
                if (tmp.isWeight()) {
                    if (weight == -1) {
                        art.setWeigthArticles(weight_path);
                    } else {
                        art.setWeigthArticles(weight);
                    }
                }
                int tmp_pfand_amount = -1;
                if (tmp.isPfand()) {
                    tmp_pfand_amount = dlm.getAmount();
                }
                dlm.addArtikel(art);
                if (tmp.isPfand() && (art.isJugendSchutzOk() || !tmp.isJugendSchutz())) {
                    for (Article arti : atm.getAllArtikels()) {
                        if (arti.getXmlArticleName().equals(art.getPfandArtikel())) {
                            Article newA = new Article(arti.getXmlArticleName(), arti.isLeergut(), arti.isJugendSchutz(), arti.getEan(), arti.getArticleName(), arti.isPfand(), arti.getPfandArtikel(), arti.getPreis(), arti.isRabatt(), arti.getUst(), arti.isWeight(), arti.getCategory(), arti.isEloading());
                            dlm.setAmount(tmp_pfand_amount);
                            newA.setInXml(false);
                            dlm.addArtikel(newA);
                            tmp_pfand_amount = -1;
                        }
                    }
                }
                JButton btBonSt = paArticle.getBtBonstorno();
                if (btBonSt.isEnabled()) {
                    btBonSt.setEnabled(false);
                }

                JButton btWrue = paArticle.getBtWRUE();
                if (btWrue.isEnabled()) {
                    btWrue.setEnabled(false);
                }
            }

            setPrice();
            setBonStarted(true);
            tfDigitField.setText("");
            money = 0;
        }
    }

    /**
     * Method that adds an article via the numpad / the yellow buttons
     *
     * @param article
     */
    public static void addArticle(Article article) {
        double summe = 0.0;
        if (!payProcessStarted) {
//            int[] indexes = taArtikel.getSelectedRows();
//            for (int index : indexes) {
            GUIOperations.getRecorderXml().setIsPayment(true);
            //Artikel, der eingefügt wird
            Article tmp = article;
            double price = 0.0;
            if (tmp.getPreis() == 0.0) {
                try {
                    int help = Integer.parseInt(
                            JOptionPane.showInputDialog(mainframe.getParent(),
                                    "Bitte geben Sie den Preis für das Produkt " + tmp.getArticleName() + " ein (in Cent)"));
                    price = help / 100.0;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(mainframe.getParent(), "Artikel konnte nicht eingefügt werden, "
                            + "da keine valide Zahl eingegeben worden ist");
//                    continue;
                }
            } else {
                price = tmp.getPreis();
            }
            if (recorderXml.getBonStornoType() != -1) {
                WarenruecknahmePreisDlg wpdlg = new WarenruecknahmePreisDlg(mainframe, true, tmp.getPreis());
                wpdlg.setVisible(true);
                price = wpdlg.getPreis();
                price *= -1;
            }

            if (tmp.getCategory().equalsIgnoreCase("Preis EAN")) {
                price = -1;
                do {
                    try {
                        price = Integer.parseInt(JOptionPane.showInputDialog(mainframe, "Geben Sie den Preis für diesen Artikel mit Preis EAN ein"));
                    } catch (NumberFormatException ex) {
                        int cancel = JOptionPane.showConfirmDialog(mainframe, "Es wurde keine gültige Zahl eingeben! Wollen Sie den Kassiervorgang dieses Artikels abbrechen?");
                        if (cancel == 0) {
                            return;
                        }
                    }
                } while (price == -1);
                price = price / 100;
            }

            int amount = -1;
            if (tmp.getCategory().equalsIgnoreCase("Gewichts EAN")) {
                do {
                    try {
                        amount = Integer.parseInt(JOptionPane.showInputDialog(mainframe, "Geben Sie die Menge für diesen Artikel mit Gewichts EAN ein (in Gramm)"));
                    } catch (NumberFormatException ex) {
                        int cancel = JOptionPane.showConfirmDialog(mainframe, "Es wurde keine gültige Zahl eingeben! Wollen Sie den Kassiervorgang dieses Artikels abbrechen?");
                        if (cancel == 0) {
                            return;
                        }
                    }
                } while (amount == -1);
            }

            Article art = new Article(tmp.getXmlArticleName(), tmp.isLeergut(),
                    tmp.isJugendSchutz(), tmp.getEan(), tmp.getArticleName(),
                    tmp.isPfand(), tmp.getPfandArtikel(), price, tmp.isRabatt(), tmp.getUst(),
                    tmp.isWeight(), tmp.getCategory(), tmp.isEloading(), tmp.isSerialNrRequired());
            art.setIsAbfrage(tmp.isIsAbfrage());
            if (amount != -1) {
                art.setGewichts_ean_menge(amount);
            }
            JugendschutzDlg jdlg = null;
            if (tmp.isJugendSchutz() && !tmp.isIsAbfrage()) {
                jdlg = new JugendschutzDlg(mainframe, true);
                jdlg.setVisible(true);
                if (jdlg.isOk()) {
                    art.setJugendSchutzOk(true);
                }
            }
            if (tmp.isEloading()) {
                if (dlm.getAmount() <= 1) {
                    if (tmp.isJugendSchutz() && !tmp.isIsAbfrage()) {
                        if (jdlg.isOk()) {
                            String status = null;
                            do {
                                ELoadingDlg edlg = new ELoadingDlg(mainframe, true);
                                edlg.setVisible(true);
                                status = edlg.getStatus();
                            } while (status == null);
                            art.setEloadingState(status);
                            if (status.equalsIgnoreCase("server_offline") || status.equalsIgnoreCase("aufladung_nok")) {
                                art.setPriceZero(true);
                            }
                        }
                    } else {
                        String status = null;
                        if (!tmp.isIsAbfrage()) {
                            do {
                                ELoadingDlg edlg = new ELoadingDlg(mainframe, true);
                                edlg.setVisible(true);
                                status = edlg.getStatus();

                            } while (status == null);
                            art.setEloadingState(status);
                            if (status.equalsIgnoreCase("server_offline") || status.equalsIgnoreCase("aufladung_nok")) {
                                art.setPriceZero(true);
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(mainframe, "E-Loading Artikel können nicht mit einer Menge > 1 eingefügt werden");
                    art.setEloadingAmmountOk(false);
                }
            }

            int weight = -1;
            Path weight_path = null;
            if (tmp.isWeight() && !tmp.isIsAbfrage()) {
                int result = JOptionPane.showConfirmDialog(mainframe, "Es wurde ein Gewichtsartikel ausgewählt. Wollen sie diesen "
                        + "Artikel mithilfe einer Tastatureingabe abwiegen?");
                if (result == 0) {
                    do {
                        try {
                            weight = Integer.parseInt(JOptionPane.showInputDialog(mainframe, "Geben Sie bitte das Gewicht für den Artikel ein"));
                        } catch (NumberFormatException ex) {
                            int cancel = JOptionPane.showConfirmDialog(mainframe, "Es wurde keine gültige Zahl eingeben! Wollen Sie den Kassiervorgang dieses Artikels abbrechen?");
                            if (cancel == 0) {
                                return;
                            }
                        }
                    } while (weight == -1);
                } else {
                    JFileChooser fc = new JFileChooser(System.getProperty("user.home") + File.separator + "Documents");
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("txt-Dateien", "txt");
                    fc.setFileFilter(filter);
                    if (fc.showOpenDialog(mainframe) == JFileChooser.APPROVE_OPTION) {
                        weight_path = fc.getSelectedFile().toPath();
                    } else {
                        JOptionPane.showMessageDialog(mainframe, "Es wurde keine Datei ausgewählt! Der Kassiervorgang dieses Artikels wird abgebrochen");
                        return;
                    }
                }
            }

            if (tmp.isSerialNrRequired() && !tmp.isIsAbfrage()) {
                //Seriennummer einlesen
                if ((tmp.isJugendSchutzOk() && tmp.isJugendSchutz()) || !tmp.isJugendSchutz()) {
                    String serNr;
                    do {
                        serNr = JOptionPane.showInputDialog(null,
                                "Bitte geben Sie eine Seriennummer ein:");
                    } while (serNr == null || serNr.equals("") || !serNr.matches("[0-9]+"));
                    art.setSerialNr(serNr);
                }
            }
            if (tmp.isWeight()) {
                if (weight == -1) {
                    art.setWeigthArticles(weight_path);
                } else {
                    art.setWeigthArticles(weight);
                }
            }
            int tmp_pfand_amount = -1;
            if (tmp.isPfand()) {
                tmp_pfand_amount = dlm.getAmount();
            }
            dlm.addArtikel(art);
            if (tmp.isPfand() && (art.isJugendSchutzOk() || !tmp.isJugendSchutz())) {
                for (Article arti : atm.getAllArtikels()) {
                    if (arti.getXmlArticleName().equals(art.getPfandArtikel())) {
                        Article newA = new Article(arti.getXmlArticleName(), arti.isLeergut(), arti.isJugendSchutz(), arti.getEan(), arti.getArticleName(), arti.isPfand(), arti.getPfandArtikel(), arti.getPreis(), arti.isRabatt(), arti.getUst(), arti.isWeight(), arti.getCategory(), arti.isEloading());
                        dlm.setAmount(tmp_pfand_amount);
                        newA.setInXml(false);
                        dlm.addArtikel(newA);
                        tmp_pfand_amount = -1;
                    }
                }
            }
            JButton btBonSt = paArticle.getBtBonstorno();
            if (btBonSt.isEnabled()) {
                btBonSt.setEnabled(false);
            }

            JButton btWrue = paArticle.getBtWRUE();
            if (btWrue.isEnabled()) {
                btWrue.setEnabled(false);
            }
//            }

            setPrice();
            setBonStarted(true);
            tfDigitField.setText("");
            money = 0;
        }
    }

    /**
     * Method that add an OverloadElement
     *
     * @param cmd
     * @param id
     * @param subId
     * @param value
     */
    public static void addOverloadElement(String cmd, String id, String subId, double value) {
        HashMap<String, Object> attributes = new HashMap<>();
        String idStr = id;
        if (!subId.equals("")) {
            idStr += ":" + subId;
        }
        attributes.put("pos", idStr);
        attributes.put("value", value);

        overloadElements.add(new Command(cmd, attributes));
    }

    /**
     * Method that sets the subtotal
     */
    public static void setPrice() {
        double summe = 0.0;
        for (Object obj : dlm.getDisplayedArtikel()) {
            if (obj instanceof Article) {
                Article art = (Article) obj;
                if (!art.isIsAbfrage()) {
                    if (((art.isJugendSchutz() && art.isJugendSchutzOk())) || !art.isJugendSchutz()) {
                        if (art.isEloadingAmmountOk() && !art.isPriceZero()) {
                            summe += ((Article) ((obj))).getPreis() * ((Article) ((obj))).getAmount();
                        }
                    }
                }
            } else if (obj instanceof Karte) {
                Karte card = (Karte) obj;
                summe += card.getAufladung() / 100;
            }
        }
        for (Object obj : dlm.getRemovedObjects()) {
            if (obj instanceof Article) {
                if (!((Article) ((obj))).isStorno_error()) {
                    summe -= ((Article) ((obj))).getPreis() * ((Article) ((obj))).getAmount();
                }
            } else if (obj instanceof Karte) {
                Karte card = (Karte) obj;
                summe -= card.getAufladung() / 100;
            }
        }
        if (Math.round(summe) == 0) {
            summe = 0;
        }
        if (GUIOperations.isTraining()) {
            summe += 1000;
        }
        tfSumme.setText(String.format("%.2f", summe));
    }

    /**
     * Method that adds an article via the keyboard einzufügen
     *
     * @param ean
     * @return
     */
    public static Article onEnterArticle(String ean) {
        for (Article allArtikel : atm.getAllArtikels()) {
            if (allArtikel.getEan().equalsIgnoreCase(ean)) {

                return allArtikel;
            }
        }
        return null;
    }

    public static void setDlm(DisplayListModel dlm) {
        GUIOperations.dlm = dlm;
    }

    public static void setPaArticle(PaArticle paArticle) {
        GUIOperations.paArticle = paArticle;
    }

    public static ArtikelTableModel getAtm() {
        return atm;
    }

    public static List<String> getEanList() {
        return eanList;
    }

    public static void setSwitchPanel(JPanel switchPanel) {
        GUIOperations.switchPanel = switchPanel;
    }

    public static void setAtm(ArtikelTableModel atm) {
        GUIOperations.atm = atm;
    }

    public static void setTaArtikel(JTable taArtikel) {
        GUIOperations.taArtikel = taArtikel;
    }

    public static void setTfSumme(JTextField tfSumme) {
        GUIOperations.tfSumme = tfSumme;
    }

    public static JTextField getTfSumme() {
        return tfSumme;
    }

    public static JTextField getTfDigitField() {
        return tfDigitField;
    }

    public static void setTfDigitField(JTextField tfDigitField) {
        GUIOperations.tfDigitField = tfDigitField;
    }

    public static DisplayListModel getDlm() {
        return dlm;
    }

    public static PaArticle getPaArticle() {
        return paArticle;
    }

    public static int getMoney() {
        return money;
    }

    public static void setMoney(int money) {
        GUIOperations.money = money;
    }

    public static Map<String, Object> getInitParams() {
        if (initParams == null) {
            initParams = new HashMap<>();
        }
        return initParams;
    }

    public static JTable getTaArtikel() {
        return taArtikel;
    }

    public static void setInitParams(Map<String, Object> initParams) {
        GUIOperations.initParams = initParams;
    }

    public static void setEanList(List<String> eanList) {
        GUIOperations.eanList = eanList;
    }

    public static File getArticleConfigFile() {
        return articleConfigFile;
    }

    public static void setArticleConfigFile(File articleConfigFile) {
        GUIOperations.articleConfigFile = articleConfigFile;
    }

    public static File getFuntionConfigFile() {
        return funtionConfigFile;
    }

    public static void setFuntionConfigFile(File funtionConfigFile) {
        GUIOperations.funtionConfigFile = funtionConfigFile;
    }

    public static Map<String, String> getEanMap() {
        return eanMap;
    }

    public static void setEanMap(Map<String, String> eanMap) {
        GUIOperations.eanMap = eanMap;
    }

    public static void setSwitchPanels(boolean switchPanels) {
        GUIOperations.switchPanels = switchPanels;
    }

    public static void setPayProcessStarted(boolean payProcessStarted) {
        GUIOperations.payProcessStarted = payProcessStarted;
    }

    public static boolean isPayProcessStarted() {
        return payProcessStarted;
    }

    public static boolean isPaid() {
        return paid;
    }

    public static void setPaid(boolean paid) {
        GUIOperations.paid = paid;
    }

    public static void setBtnSwitch(JButton btnSwitch) {
        GUIOperations.btnSwitch = btnSwitch;
    }

    public static JButton getBtnSwitch() {
        return btnSwitch;
    }

    public static Path getInputPath() {
        return inputPath;
    }

    public static void setInputPath(Path inputPath) {
        GUIOperations.inputPath = inputPath;
    }

    public static Map<String, String> getDynFuncMap() {
        return dynFuncMap;
    }

    public static void setDynFuncMap(Map<String, String> dynFuncMap) {
        GUIOperations.dynFuncMap = dynFuncMap;
    }

    public static boolean isLogin() {
        return login;
    }

    public static void setLogin(boolean login) {
        GUIOperations.login = login;
    }

    public static boolean isLogout() {
        return logout;
    }

    public static void setLogout(boolean logout) {
        GUIOperations.logout = logout;
    }

    public static RecorderXML getRecorderXml() {
        return recorderXml;
    }

    public static void setRecorderXML(RecorderXML recorderXml) {
        GUIOperations.recorderXml = recorderXml;
    }
    
    public static boolean isBonStarted() {
        return bonStarted;
    }

    public static void setBonStarted(boolean bonStarted) {
        GUIOperations.bonStarted = bonStarted;
    }

    public static boolean isTraining() {
        return training;
    }

    public static void setTraining(boolean training) {
        GUIOperations.training = training;
    }

    public static String getTextForInitDialog() {
        return textForInitDialog;
    }

    public static void setTextForInitDialog(String textForInitDialog) {
        GUIOperations.textForInitDialog = textForInitDialog;
    }

    public static void onRestart() {
        mainframe.onRestart();
    }

    public static boolean isPwChange() {
        return pwChange;
    }

    public static void setPwChange(boolean pwChange) {
        GUIOperations.pwChange = pwChange;
    }

    public static List<Path> getAllCreatedBons() {
        return allCreatedBons;
    }

    public static void setAllCreatedBons(List<Path> allCreatedBons) {
        GUIOperations.allCreatedBons = allCreatedBons;
    }

    public static MainFrame getMainframe() {
        return mainframe;
    }

    public static void setMainframe(MainFrame mainframe) {
        GUIOperations.mainframe = mainframe;
    }

    public static List<Command> getOverloadElements() {
        return overloadElements;
    }

    public static void setOverloadElements(List<Command> overloadElements) {
        GUIOperations.overloadElements = overloadElements;
    }

    public static Path getResPath() {
        return resPath;
    }

    public static void setResPath(Path resPath) {
        GUIOperations.resPath = resPath;
    }

    public static boolean isNeedsInit() {
        return needsInit;
    }

    public static void setNeedsInit(boolean needsInit) {
        GUIOperations.needsInit = needsInit;
    }

    public static boolean isIsWorkflow() {
        return isWorkflow;
    }

    public static void setIsWorkflow(boolean isWorkflow) {
        GUIOperations.isWorkflow = isWorkflow;
    }

    public static Path getSaveLocationIfWorkflow() {
        return saveLocationIfWorkflow;
    }

    public static void setSaveLocationIfWorkflow(Path saveLocationIfWorkflow) {
        GUIOperations.saveLocationIfWorkflow = saveLocationIfWorkflow;
    }
}

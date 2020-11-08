package simulator.recorder.adapter;

import org.w3c.dom.NamedNodeMap;

import simulator.recorder.adapter.input.DTFEloadingCommand;
import simulator.recorder.adapter.input.DtfMotKeyCommand;
import simulator.recorder.adapter.input.DtfScanCommand;
import simulator.recorder.adapter.input.DtfTextInputCommand;
import simulator.recorder.util.ELoadingInfo;
import simulator.util.ItemUtil;
import simulator.recorder.adapter.AbstractAdapter;

/**
 * this class generates for one item all necessary recorder lines
 *
 * e.g. this xml line will be casted to the recorder lines below
 * <item article=“Artikel1“ quantity=“2000“ input=“keyboard“ condrecall="true">
 *
 *
 * @author BKornsteiner
 *
 */
public class ItemAdapter
    extends AbstractAdapter {

  private String strCommand = "";

  private boolean isEloadingArtikel = false;
  private String strEloadingCommand = "";
  private ELoadingInfo objEloadingInfo = null;

  /**
   * constructor
   *
   * @param testResult
   */
  public ItemAdapter() {

  }

  /**
   * creates the recorder line for the given item attributes are: name, quantity, price, input, youthprot
   *
   * @param itemAttrList ... attribute list of the item
   * @param nodeList
   *
   */
  public synchronized void createItemCommand(NamedNodeMap itemAttrList) {
    attributes = itemAttrList;

    // quantity
    if (containsAttr(attributes, "quantity")) {

      String strquanValue = attributes.getNamedItem("quantity").getNodeValue();
      Long lquantity = Long.parseLong(strquanValue);

      if ((lquantity % 1000) == 0) {
        lquantity = lquantity / 1000;
      }
      strCommand += new DtfTextInputCommand(lquantity.toString()).getXml();
      strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.MENGE).getXml();
    }

    // article Ean auslesen
    String strArtikelName = attributes.getNamedItem("article").getNodeValue();
    String ean = "";
    try {
      ean = ItemUtil.getTheInstance().getEANforArticleName(strArtikelName);
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
    }

    // input Typ ermitteln
    String inputValue = attributes.getNamedItem("input").getNodeValue();
    if (inputValue.equalsIgnoreCase("scan")) {
      strCommand += new DtfScanCommand(ean).getXml();
    }
    else if (inputValue.equalsIgnoreCase("keyboard")) {
      strCommand += new DtfTextInputCommand(ean).getXml();
      strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.EINGABE).getXml();
    }

    if (ean.startsWith("51"))// Preiseingabe erforderlich
    {
      String strPrice = attributes.getNamedItem("price").getNodeValue();
      strCommand += new DtfTextInputCommand(strPrice).getXml();
      strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.EINGABE).getXml();

    }

    // Jugendschutz abfrage bestätigen
    if (containsAttr(attributes, "youthprot")) {
      boolean isYouthprot = Boolean.parseBoolean(attributes.getNamedItem("youthprot").getNodeValue());

      if (isYouthprot) {
        strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.EINGABE).getXml();// Ok (Eingabe)
      }
      else {
        strCommand += new DtfTextInputCommand(true).getXml();// False (Löschen)
      }
    }

    if (containsAttr(attributes, "eloading_func")) {
      isEloadingArtikel = true;

      objEloadingInfo = new ELoadingInfo();
      objEloadingInfo.setStrEAN(ean);

      String strVorgang = attributes.getNamedItem("eloading_func").getNodeValue();
      objEloadingInfo.setStrVorgang(strVorgang);

      if (containsAttr(attributes, "serial_nr")) {
        String strSeriennummer = attributes.getNamedItem("serial_nr").getNodeValue();
        if (strSeriennummer != null) {
          objEloadingInfo.setStrSeriennummer(strSeriennummer);
        }
      }
      strEloadingCommand = new DTFEloadingCommand(objEloadingInfo).getXml();
    }

    // TODO - Restliche Fälle implementieren

  }

  public String getArticleCommand() {
    return strCommand;
  }

  public boolean isEloadingArtikel() {
    return isEloadingArtikel;
  }

  public String getEloadingCommand() {
    return strEloadingCommand;
  }

  public ELoadingInfo getEloadingInfo() {
    return objEloadingInfo;
  }

}

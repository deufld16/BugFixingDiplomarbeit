package simulator.recorder.adapter;

import org.w3c.dom.NamedNodeMap;

import simulator.recorder.adapter.input.DTFPaymentCommand;
import simulator.recorder.adapter.input.DtfTextInputCommand;

public class PaymentAdapter
    extends AbstractAdapter {

  private String strCommand = "";

  public PaymentAdapter() {

  }

  public synchronized String createPaymentCommand(NamedNodeMap itemAttrList) {
    attributes = itemAttrList;

    // Betrag eingeben falls vorgegeben
    if (containsAttr(attributes, "pay_amount")) {
      String strPayAmount = attributes.getNamedItem("pay_amount").getNodeValue();
      strCommand += new DtfTextInputCommand(strPayAmount).getXml();
    }

    String strPayId = attributes.getNamedItem("pay_id").getNodeValue();
    String strPaySubId = attributes.getNamedItem("pay_sub").getNodeValue();

    strCommand += new DTFPaymentCommand(strPayId).getXml();// Zahlungsmittel Taste
System.out.println("Maxi liebt JComponents");
    return strCommand;
  }

}

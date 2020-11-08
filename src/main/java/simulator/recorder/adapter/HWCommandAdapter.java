package simulator.recorder.adapter;

import org.w3c.dom.NamedNodeMap;

import simulator.recorder.adapter.input.DTFKeyLockCommand;
import simulator.recorder.adapter.input.DtfDrawerCommand;

public class HWCommandAdapter
    extends AbstractAdapter {
  private String strCommand = "";

  public HWCommandAdapter() {

  }

  public synchronized String createHWCommand(NamedNodeMap itemAttrList) {
    attributes = itemAttrList;

    // get type
    String typeValue = attributes.getNamedItem("device").getNodeValue();

    if (typeValue.equalsIgnoreCase("drawer")) {
      String strDrawerPos = attributes.getNamedItem("value").getNodeValue();
      int idrawerPos = Integer.parseInt(strDrawerPos);

      strCommand += new DtfDrawerCommand(idrawerPos).getXml();

    }
    else if (typeValue.equalsIgnoreCase("key")) {
      String strKeyLockPos = attributes.getNamedItem("value").getNodeValue();
      int ikeylockPos = Integer.parseInt(strKeyLockPos);

      strCommand += new DTFKeyLockCommand(ikeylockPos).getXml();
    }

    // TODO - Restliche Fälle implementieren

    return strCommand;
  }

}

package simulator.recorder.adapter;

import org.w3c.dom.NamedNodeMap;

import simulator.recorder.adapter.input.DtfMotKeyCommand;
import simulator.recorder.adapter.input.DtfTextInputCommand;

public class PwdAdapter
    extends AbstractAdapter {

  private String strCommand = "";

  public PwdAdapter() {

  }

  public synchronized String createPauseCommand(NamedNodeMap itemAttrList) {
    attributes = itemAttrList;

    String strPwd = attributes.getNamedItem("value").getNodeValue();

    strCommand += new DtfTextInputCommand(strPwd).getXml();
    strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.EINGABE).getXml();

    return strCommand;
  }

}

package simulator.recorder.adapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.w3c.dom.NamedNodeMap;

public class CheckpointAdapter
    extends AbstractAdapter {

  private String strCommand = "";

  private String strRegUser = "";

  public CheckpointAdapter(String regUser) {
    strRegUser = regUser;
  }

  public synchronized String createCheckpointCommand(NamedNodeMap itemAttrList) {
    attributes = itemAttrList;

    String strCheckpointName = attributes.getNamedItem("name").getNodeValue();

    LocalDate date = LocalDate.now();
    String strDate = date.format(DateTimeFormatter.ofPattern("ddMMyyyy"));

    String strData = strCheckpointName + "_" + strDate + "_" + strRegUser;

    strCommand = "<checkpoint><data>" + strData + "</data></checkpoint>";

    return strCommand;
  }

}

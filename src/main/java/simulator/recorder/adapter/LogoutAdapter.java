package simulator.recorder.adapter;

import simulator.recorder.adapter.input.DtfMotKeyCommand;
import simulator.recorder.adapter.input.DtfTextInputCommand;

public class LogoutAdapter
    extends AbstractAdapter {

  private String strCommand = "";

  public LogoutAdapter() {

  }

  public synchronized String createLogoutCommand() {
    strCommand += new DtfTextInputCommand("2").getXml();
    strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.CODE).getXml();

    return strCommand;
  }

}

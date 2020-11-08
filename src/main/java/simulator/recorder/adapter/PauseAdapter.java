package simulator.recorder.adapter;

import simulator.recorder.adapter.input.DtfMotKeyCommand;
import simulator.recorder.adapter.input.DtfTextInputCommand;

public class PauseAdapter
    extends AbstractAdapter {

  private String strCommand = "";

  public PauseAdapter() {

  }

  public synchronized String createPauseCommand() {

    strCommand += new DtfTextInputCommand("1").getXml();// Codefunktion 1
    strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.CODE).getXml();

    return strCommand;
  }

}

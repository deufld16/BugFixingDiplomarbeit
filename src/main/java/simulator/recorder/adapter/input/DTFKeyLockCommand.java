package simulator.recorder.adapter.input;

public class DTFKeyLockCommand
    implements IDTFCommand {

  int iKeyLockPos = 0;

  public DTFKeyLockCommand(int pos) {
    iKeyLockPos = pos;
  }

  @Override
  public String getXml() {
    return "<keylock><pos>" + iKeyLockPos + "</pos></keylock>\n";
  }

}

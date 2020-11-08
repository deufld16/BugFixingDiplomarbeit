package simulator.recorder.adapter.input;

public class DtfDrawerCommand
    implements IDTFCommand {

  int iDrawerPos = 0;

  public DtfDrawerCommand(int pos) {
    iDrawerPos = pos;
  }

  @Override
  public String getXml() {
    return "<drawer><pos>" + iDrawerPos + "</pos></drawer>\n";
  }

}

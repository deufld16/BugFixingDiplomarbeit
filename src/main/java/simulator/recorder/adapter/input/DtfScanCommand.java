package simulator.recorder.adapter.input;

public class DtfScanCommand
    implements IDTFCommand {

  private String strScanData = "";

  public DtfScanCommand(String scanData) {
    strScanData = scanData;
  }

  @Override
  public String getXml() {
    return "<scanner><data>" + strScanData + "</data></scanner>\n";
  }

}

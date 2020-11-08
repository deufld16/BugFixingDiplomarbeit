package simulator.recorder.adapter.input;

import simulator.recorder.util.ELoadingInfo;

public class DTFEloadingCommand
    implements IDTFCommand {

  private ELoadingInfo objELoadingInfo;

  public DTFEloadingCommand(ELoadingInfo objELoadingInfo) {
    this.objELoadingInfo = objELoadingInfo;
  }

  @Override
  public String getXml() {
    return "<eloadinginfo>" + "<ean>" + objELoadingInfo.getStrEAN() + "</ean>" + "<vorgangscode>"
        + objELoadingInfo.getVorgangscode() + "</vorgangscode>" + "</eloadinginfo>\n";
  }

}

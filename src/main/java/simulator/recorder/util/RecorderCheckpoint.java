package simulator.recorder.util;

/**
 * Klassen beinhaltet den MarkerNamen des aktuellen Recorders, sowie die ID des Checkpoint Commands.
 * 
 * @author mSkergeth
 */
public class RecorderCheckpoint {

  /** Marker-Name */
  private String strMarkerName;

  /** Letzte CommandID eines Recorders */
  private Long lLastCheckpointID;

  public RecorderCheckpoint(String strMarkerName, Long lLastCheckpointID) {
    this.strMarkerName = strMarkerName;
    this.lLastCheckpointID = lLastCheckpointID;
  }

  public String getStrMarkerName() {
    return strMarkerName;
  }

  public Long getlLastCheckpointID() {
    return lLastCheckpointID;
  }

  @Override
  public String toString() {
    return "RecorderCheckpoint [strMarkerName=" + strMarkerName + ", lLastCheckpointID=" + lLastCheckpointID
        + "]";
  }

}

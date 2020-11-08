package simulator.recorder.util;

/**
 * Zuordnung der Recorder Kommandos
 */
public enum RecElementType {
  LOGIN("login"), CARD("card"), ITEM("item"), STORNO("storno"), CONTROLKEY("controlkey"), SCALE("scale"),
  RETITEMTOKEN("retitem_token"), PAYMENT("payment"), LOGOUT("logout"), CLOSE("close"), PAUSE("pause"),
  MOCKUP("mockup"), PWD("pwd"), HWCOMMAND("hwcommand"), SYNC("sync"), BONID("bonid"), KEYBOARD("keyboard"),
  MARKER("mark"), CHECKPOINT("checkpoint"), LEERGUTLABEL("leergutbc"), UNKNOWN("unknown"),
  COMMISSION("commission"), SCANNER("scanner"), GSERVER("gserver"), KAB("kab"), STAT("stat"),
  SUBTOTAL("subtotal"), PROMOTIONS("promotions"), SYSCOMMAND("syscommand");

  private String value;

  private RecElementType(String value) {
    this.value = value;
  }

  /**
   * Gibt des RecElementType des NodeNamen zurück
   * 
   * @param nodeName .. element name
   * @return the element type
   */
  public static RecElementType getRecElementType(String nodeName) {

    RecElementType retValue = RecElementType.UNKNOWN;
    for (RecElementType element : RecElementType.values()) {
      if (element.value.equals(nodeName)) {
        // found - stop iterator
        retValue = element;
        break;
      }
    }
    return retValue;
  }
}

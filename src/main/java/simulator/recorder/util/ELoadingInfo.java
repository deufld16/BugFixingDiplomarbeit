package simulator.recorder.util;

public class ELoadingInfo {

  private String strEAN;
  private String strVorgang;
  private String strSeriennummer;

  private static final String SERVER_OFFLINE = "server_offline";
  private static final String VERKAUF_NOK = "verkauf_nok";
  private static final String AUFLADUNG_NOK = "aufladung_nok";
  private static final String AUFLADUNG_OK = "aufladung_ok";

  public ELoadingInfo() {
    strEAN = "";
    strVorgang = "";
    strSeriennummer = "";

  }

  public String getStrEAN() {
    return strEAN;
  }

  public void setStrEAN(String strEAN) {
    this.strEAN = strEAN;
  }

  public String getStrVorgang() {
    return strVorgang;
  }

  public void setStrVorgang(String strVorgang) {
    this.strVorgang = strVorgang;
  }

  public String getStrSeriennummer() {
    return strSeriennummer;
  }

  public void setStrSeriennummer(String strSeriennummer) {
    this.strSeriennummer = strSeriennummer;
  }

  public int getVorgangscode() {
    switch (strVorgang) {
      case SERVER_OFFLINE:
        return 1;
      case VERKAUF_NOK:
        return 2;
      case AUFLADUNG_NOK:
        return 3;
      case AUFLADUNG_OK:
        return 4;
      default:
        return 0;
    }
  }

}

package simulator.recorder.adapter.input;

public class DtfMotKeyCommand
    implements IDTFCommand {
  public final static int EINGABE = 1;
  public final static int SUMME = 35;
  public final static int ZEILENSTORNO = 12;
  public final static int SOFORTSTORNO = 0;
  public final static int MENGE = 10;
  public final static int CODE = 2;
  public final static int PREIS_ABFRAGE = 6;

  int mKey = EINGABE;

  public DtfMotKeyCommand(int Key) {
    mKey = Key;
  }

  @Override
  public String getXml() {
    return "<keyboard>\n <code>" + mKey + "</code>\n <type>3</type>\n</keyboard>\n";
  }

}

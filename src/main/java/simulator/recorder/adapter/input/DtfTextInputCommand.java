package simulator.recorder.adapter.input;

public class DtfTextInputCommand
    implements IDTFCommand {
  private boolean isClearKey = false;
  private String text = "";

  public DtfTextInputCommand(boolean ClearKey) {
    isClearKey = ClearKey;
  }

  public DtfTextInputCommand(String input) {
    text = new String(input);
  }

  @Override
  public String getXml() {
    StringBuffer sb = new StringBuffer(1024);
    if (isClearKey) {
      sb.append("<keyboard>\n <code>28</code>\n <type>1</type>\n</keyboard>\n");
    }
    else {
      for (int i = 0; i < text.length(); i++ ) {
        sb.append("<keyboard>\n <code>");
        sb.append(text.getBytes()[i] - 47);
        sb.append("</code>\n <type>1</type>\n</keyboard>\n");
      }
    }
    return sb.toString();
  }

}

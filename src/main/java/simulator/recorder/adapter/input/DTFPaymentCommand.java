package simulator.recorder.adapter.input;

public class DTFPaymentCommand
    implements IDTFCommand {

  String ipay_id = "";

  public DTFPaymentCommand(String pay_id) {
    ipay_id = pay_id;
  }

  @Override
  public String getXml() {
    return "<keyboard> <code>" + ipay_id + "</code><type>4</type></keyboard>\n";
  }

}

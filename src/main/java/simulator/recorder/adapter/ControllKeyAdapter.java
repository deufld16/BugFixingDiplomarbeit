package simulator.recorder.adapter;

import org.w3c.dom.NamedNodeMap;

import simulator.recorder.adapter.input.DtfMotKeyCommand;
import simulator.recorder.adapter.input.DtfTextInputCommand;

public class ControllKeyAdapter
    extends AbstractAdapter {

  private String strCommand = "";

  public ControllKeyAdapter() {

  }

  public synchronized String createControllKeyCommand(NamedNodeMap itemAttrList) {
    attributes = itemAttrList;

    String strFunctionValue = attributes.getNamedItem("function").getNodeValue();

    if (strFunctionValue.equalsIgnoreCase("summe")) {
      strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.SUMME).getXml();
    }
    else if (strFunctionValue.equalsIgnoreCase("CLR")) {
      strCommand += new DtfTextInputCommand(true).getXml();
    }
    else if (strFunctionValue.equalsIgnoreCase("ABFR")) {
      strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.PREIS_ABFRAGE).getXml();
    }
    else if (strFunctionValue.equalsIgnoreCase("ENTER")) {
        strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.EINGABE).getXml();
      }
    else if(strFunctionValue.equals("BONSTORNO")){
    	strCommand += new DtfTextInputCommand("25").getXml();//25
    	strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.CODE).getXml();//Code
    	
//    	//------------Authentifizierung---------------
//    	strCommand += new DtfTextInputCommand("4040").getXml();//Benutzer
//    	strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.EINGABE).getXml();//Eingabe  	
//    	strCommand += new DtfTextInputCommand("1988").getXml();//Passwort   
//    	strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.EINGABE).getXml();//Eingabe
//    	//------------Authentifizierung---------------
    	
    	String strBonnummer = attributes.getNamedItem("parameter").getNodeValue();
    	strCommand += new DtfTextInputCommand(strBonnummer).getXml();//Bonnummer
    	strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.EINGABE).getXml();//Eingabe
    	strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.EINGABE).getXml();//Eingabe
    	
    	
    	
    }

    // TODO - Restliche function Values implementieren

    return strCommand;
  }

}
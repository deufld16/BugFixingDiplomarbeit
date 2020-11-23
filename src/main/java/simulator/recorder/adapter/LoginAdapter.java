package simulator.recorder.adapter;

import org.w3c.dom.NamedNodeMap;

import simulator.recorder.adapter.input.DtfMotKeyCommand;
import simulator.recorder.adapter.input.DtfTextInputCommand;

public class LoginAdapter
        extends AbstractAdapter {

    private String strCommand = "";
    private String strEmpID = "";
    private String strPasswd = "";

    public LoginAdapter(String emp_id, String passwd) {
        strEmpID = emp_id;
        strPasswd = passwd;
    }

    public synchronized String createLoginCommand(NamedNodeMap loginAttrList) {

        attributes = loginAttrList;

        strCommand += new DtfTextInputCommand(strEmpID).getXml();// EmpID Eingeben

        strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.EINGABE).getXml();// Eingabe Taste

        boolean isChangePassword = false;
        // Prüfen ob Passwort Änderung erforderlich
        if (containsAttr(attributes, "changepwd")) {
            if (attributes.getNamedItem("changepwd").getNodeValue().equalsIgnoreCase("true")) {
                isChangePassword = true;
            }
        }

        if (isChangePassword) {
            strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.EINGABE).getXml();// Eingabe Taste
            strCommand += new DtfTextInputCommand(strPasswd).getXml(); // Altes Passwort eingeben
            strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.EINGABE).getXml();// Eingabe Taste

            String strNewPasswd = attributes.getNamedItem("pwd").getNodeValue();
            strCommand += new DtfTextInputCommand(strNewPasswd).getXml();// Neues Passwort eingeben
            strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.EINGABE).getXml();// Eingabe Taste

        } else {
          // strCommand += new DtfTextInputCommand(true).getXml(); // Löschen Taste
System.out.println("richtig");
            strCommand += new DtfTextInputCommand(strPasswd).getXml(); // Passwd Eingeben
            strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.EINGABE).getXml();// Eingabe Taste

        }
        System.out.println(strEmpID);
         System.out.println(strPasswd);
         System.out.println(strCommand);
        strCommand += new DtfMotKeyCommand(DtfMotKeyCommand.CODE).getXml();// Code Taste

        return strCommand;
    }

}

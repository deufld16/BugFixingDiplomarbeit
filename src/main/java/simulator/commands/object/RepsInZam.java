package simulator.commands.object;

import org.w3c.dom.Document;

public class RepsInZam {

  private int iTllID;
  private long lRevNr;
  String strXMLRepZAM;
  String strXMLRepHDB;
  Document objXMLDocZAM;
  Document objXMLDocHDB;

  public int getiTllID() {
    return iTllID;
  }

  public void setiTllID(int iTllID) {
    this.iTllID = iTllID;
  }

  public long getlRevNr() {
    return lRevNr;
  }

  public void setlRevNr(long lRevNr) {
    this.lRevNr = lRevNr;
  }

  public String getStrXMLRepZAM() {
    return strXMLRepZAM;
  }

  public void setStrXMLRepZAM(String strXMLRepZAM) {
    this.strXMLRepZAM = strXMLRepZAM;
  }

  public String getStrXMLRepHDB() {
    return strXMLRepHDB;
  }

  public void setStrXMLRepHDB(String strXMLRepHDB) {
    this.strXMLRepHDB = strXMLRepHDB;
  }

  public Document getObjXMLDocZAM() {
    return objXMLDocZAM;
  }

  public void setObjXMLDocZAM(Document objXMLDocZAM) {
    this.objXMLDocZAM = objXMLDocZAM;
  }

  public Document getObjXMLDocHDB() {
    return objXMLDocHDB;
  }

  public void setObjXMLDocHDB(Document objXMLDocHDB) {
    this.objXMLDocHDB = objXMLDocHDB;
  }

  @Override
  public String toString() {
    return "RepsInZam [iTllID=" + iTllID + ", lRevNr=" + lRevNr + ", strXMLRepZAM=" + strXMLRepZAM
        + ", strXMLRepHDB=" + strXMLRepHDB + "]";
  }

}

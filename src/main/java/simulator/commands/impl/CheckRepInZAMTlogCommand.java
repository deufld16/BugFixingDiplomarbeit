package simulator.commands.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import simulator.commands.object.RepsInZam;
import simulator.constants.DBNames;
import simulator.database.DBAccess;
import simulator.beans.RuntimeEnv;
import simulator.util.UserTokenUtil;

public class CheckRepInZAMTlogCommand
    extends simulator.commands.ACommand {

  /** The logger. */
  private static final Logger logger = LoggerFactory.getLogger(CheckRepInZAMTlogCommand.class);

  // private static final String CONST_HTTP_WEBSERVICE =
  // "http://10.93.31.64:8080/UtilService/ZamToReportService/getReportXMLFromZAM";
  private static final String CONST_HTTP_WEBSERVICE_FUNC =
      "/UtilService/ZamToReportService/getReportXMLFromZAM";

  private static final String CONST_PARAM_STRREPTYP = "strreptyp";

  private int iRepTyp;
  private List<RepsInZam> lstReps = null;

  public CheckRepInZAMTlogCommand(RuntimeEnv env) {
    objRuntimeEnv = env;
  }

  /**
   * Arbeits-Schnittstelle fuer das Command 'CheckRepInZAMTlogCommand'<br />
   * Diese Command prueft ob ein Bericht korrekt in der ZAM2KOS-Tabelle abgelegt wurde.<br />
   * Das Command wird "pro" Kasse aufgerufen, selektiert jedoch Daten aus der BO-Tabelle.<br />
   * 
   * @return boolean ... true/false
   * 
   * @throws Exception
   * 
   * @author DMaier
   * 
   * @version 1.0
   */

  @Override
  public boolean doWork()
      throws Exception {

    logger.debug("CheckRepInZAMTlogCommand->doWork()");

    // Parameter lesen
    iRepTyp = Integer.parseInt(getValueForKey(CONST_PARAM_STRREPTYP));

    lstReps = new ArrayList<RepsInZam>();

    // 1. Bericht ermitteln
    String strRevNr = getLastRevNrForRepForTill(objRuntimeEnv.getObjActTG().getiTllId());
    if ((strRevNr == null) || strRevNr.isEmpty()) {
      logger.error("Fehler beim Ermitteln der Revisionsnummer (RepTyp: " + iRepTyp + ", Tll_ID: "
          + objRuntimeEnv.getObjActTG().getiTllId() + ") ");

      logger.debug("Return CheckRepInZAMTlogCommand->doWork() :: false");
      return false;
    }

    // 3. Bericht ermitteln
    String strBerichtZAM = getReportFromZAM(strRevNr);
    if ((strBerichtZAM == null) || strBerichtZAM.isEmpty()) {
      logger.error("Fehler beim Ermitteln des Berichts aus dem ZAMTlog (RepTyp: " + iRepTyp + ", Tll_ID: "
          + objRuntimeEnv.getObjActTG().getiTllId() + ") ");

      logger.debug("Return CheckRepInZAMTlogCommand->doWork() :: false");
      return false;
    }

    // 4. XML ermitteln
    String strBerichtHDB = getReportFromHDB(strRevNr);
    if ((strBerichtHDB == null) || strBerichtHDB.isEmpty()) {
      logger.error("Fehler beim Ermitteln des Berichts aus der HDB (RepTyp: " + iRepTyp + ", Tll_ID: "
          + objRuntimeEnv.getObjActTG().getiTllId() + ") ");

      logger.debug("Return CheckRepInZAMTlogCommand->doWork() :: false");
      return false;
    }

    RepsInZam objRepsInZam = new RepsInZam();
    objRepsInZam.setiTllID(objRuntimeEnv.getObjActTG().getiTllId());
    objRepsInZam.setlRevNr(new Long(strRevNr).longValue());
    objRepsInZam.setStrXMLRepHDB(strBerichtHDB);
    objRepsInZam.setStrXMLRepZAM(strBerichtZAM);

    lstReps.add(objRepsInZam);

    logger.debug("Return CheckRepInZAMTlogCommand->doWork() :: true");
    return true;

  }

  @Override
  public boolean doCheck() {
    // Diff durchfuehren
    logger.debug("CheckRepInZAMTlogCommand->check()");

    boolean bRet = false;

    for (RepsInZam objEleRepsInZam : lstReps) {
      String strBerichtHDB = objEleRepsInZam.getStrXMLRepHDB();
      String strBerichtZAM = objEleRepsInZam.getStrXMLRepZAM();

      if ((strBerichtHDB.length() <= 0) || (strBerichtZAM.length() <= 0)) {
        logger.error("Es wurde keine Berichte ermittelt !!");
        logger.debug("Return CheckRepInZAMTlogCommand->check() :: false");
        return false;
      }

      logger.debug("CheckRepInZAMTlogCommand->check() :: Lade =" + objEleRepsInZam.getiTllID());
      logger.debug("CheckRepInZAMTlogCommand->check() :: RevNr=" + objEleRepsInZam.getlRevNr());
      logger.debug("CheckRepInZAMTlogCommand->check() :: strBerichtZAM=" + strBerichtZAM);
      logger.debug("CheckRepInZAMTlogCommand->check() :: strBerichtHDB=" + strBerichtHDB);

      // Manche Dinge muessen leider ersetzt werden
      strBerichtHDB = replaceTextInXML(strBerichtHDB);
      strBerichtZAM = replaceTextInXML(strBerichtZAM);

      // String in XML umwandeln
      objEleRepsInZam.setObjXMLDocHDB(getXMLDoc(strBerichtHDB));
      objEleRepsInZam.setObjXMLDocZAM(getXMLDoc(strBerichtZAM));

      // XML vergleichen
      bRet = compareXML(objEleRepsInZam);
      if (!bRet) {
        logErrXML(objEleRepsInZam);

        logger.debug("Return CheckRepInZAMTlogCommand->check() :: false");
        return false;
      }
    }

    logger.debug("Return CheckRepInZAMTlogCommand->check() :: " + bRet);
    return bRet;
  }

  /**
   * Ermittelt den Bericht über den Webservice
   * 
   * @param String strRevNR ... Revisionsnummer des Berichtes
   * 
   * @return String ... Bericht XML in String Format
   * 
   * @author DMaier
   * 
   * @version dtf-runtime-2.20_060
   */
  private String getReportFromZAM(String strRevNR)
      throws Exception {

    logger.debug("getReportFromZAM(strRevNR:" + strRevNR + ")");

    boolean bError = false;

    HttpURLConnection objConnection = null;
    URL objURL = null;
    StringBuilder sbResponse = null;
    BufferedReader brReader = null;

    String strReqBody = "";
    String strSessionID = "";
    String strParams = "";
    String strResponse = "";

    strSessionID = UserTokenUtil.generateUserToken(objRuntimeEnv);
    if ((strSessionID == null) || strSessionID.isEmpty()) {
      logger.error("Error generating SessionID");
      bError = true;
    }

    if (!bError) {
      strParams = "\"rep_id\":" + "\"" + iRepTyp + "\", ";
      strParams += "\"rev_nr\":" + "\"" + strRevNR + "\", ";
      strParams += "\"sessionID\":" + "\"" + strSessionID + "\"";

      strReqBody = "{ " + strParams + " }";

      String strWebServce =
          "http://" + objRuntimeEnv.getStrServerAddr() + ":8080" + CONST_HTTP_WEBSERVICE_FUNC;

      // objURL = new URL(CONST_HTTP_WEBSERVICE);
      objURL = new URL(strWebServce);
      objConnection = (HttpURLConnection) objURL.openConnection();
      objConnection.setDoOutput(true);
      objConnection.setDoInput(true);
      objConnection.setConnectTimeout(5000);
      objConnection.setRequestProperty("Content-Type", "application/json");
      objConnection.setRequestProperty("Accept", "application/json");
      objConnection.setRequestProperty("Connection", "keep-alive");
      objConnection.setRequestMethod("POST");

      OutputStreamWriter wr = new OutputStreamWriter(objConnection.getOutputStream());
      wr.write(strReqBody);
      wr.flush();
      wr.close();

      int iHttpResult = objConnection.getResponseCode();
      if (iHttpResult != HttpStatus.SC_OK) {
        logger.error("Could not establish Http Connection! Error Code: " + iHttpResult);
        bError = true;
      }

      if (!bError) {
        sbResponse = new StringBuilder();
        brReader = new BufferedReader(new InputStreamReader(objConnection.getInputStream(), "utf-8"));
        String line = null;

        while ((line = brReader.readLine()) != null) {
          sbResponse.append(line);
        }
        brReader.close();

        logger.debug("Response=" + sbResponse.toString());
        strResponse = sbResponse.toString();
      }
    }
    logger.debug("Return getReportFromZAM(" + strResponse + ")");
    return strResponse;
  }

  /**
   * Ermittelt den letzten Bericht fuer eine Lade
   * 
   * @param String strRevNR ... Revisionsnummer des Berichtes
   * 
   * @return String ... Bericht XML in String Format
   * 
   * @author DMaier
   * 
   * @version dtf-runtime-2.20_060
   */

  private String getLastRevNrForRepForTill(int iActTllID)
      throws Exception {

    logger.debug("getLastRevNrForRepForTill()");

    String strRevNr = null;

    DBAccess objDBA = DBAccess.getInstance();
    Connection objCon = null;
    Statement objStmt = null;
    ResultSet rs = null;

    try {
      objCon = objDBA.getDBCon(DBNames.DB_NAME_NPOS);
      objStmt = objCon.createStatement();

      String strSQL = "SELECT MAX(countnr) AS MaxRevNr FROM bo_repcnt WHERE reptyp = " + iRepTyp + " ";

      if (iActTllID > 0) {
        strSQL += " AND tll_id = " + iActTllID + " ";
      }

      logger.debug("getLastRevNrForRepForTill() :: strSQL=" + strSQL);

      rs = objStmt.executeQuery(strSQL);

      Long lRevNr = null;
      if (rs.next()) {
        lRevNr = rs.getLong("MaxRevNr");
      }
      strRevNr = lRevNr.toString();
    }
    catch (SQLException e) {
      logger.error(
          this.getClass().getSimpleName() + " Fehler beim Ermitteln der Revisionsnummer. " + e.getMessage());
    }
    finally {
      if (rs != null) {
        rs.close();
      }
      if (objStmt != null) {
        objStmt.close();
      }
      if (objDBA != null) {
        objDBA.closeConnection(DBNames.DB_NAME_NPOS);
      }
    }

    logger.debug("Return getLastRevNrForRepForTill(" + strRevNr + ")");
    return strRevNr;
  }

  /**
   * Ermittelt den Bericht aus der HDB
   * 
   * @param String strRevNR ... Revisionsnummer des Berichtes
   * 
   * @return String ... Bericht XML in String Format
   * 
   * @author DMaier
   * 
   * @version dtf-runtime-2.20_060
   */
  private String getReportFromHDB(String strRevNR)
      throws Exception {

    logger.debug("getReportFromHDB()");

    String strRepData = null;

    DBAccess objDBA = DBAccess.getInstance();
    Connection objCon = null;
    Statement objStmt = null;
    ResultSet rs = null;

    try {
      objCon = objDBA.getDBCon(DBNames.DB_NAME_HDB);
      objStmt = objCon.createStatement();

      String strSQL =
          "SELECT repdata FROM b_repxml WHERE REPTYP = " + iRepTyp + " AND countnr = " + strRevNR + " ";

      logger.debug("getReportFromHDB() :: strSQL=" + strSQL);

      rs = objStmt.executeQuery(strSQL);

      if (rs.next()) {
        strRepData = rs.getString("repdata");
      }
    }
    catch (SQLException e) {
      logger.error(this.getClass().getSimpleName() + " Fehler beim Ermitteln der Berichts aus der HDB. "
          + e.getMessage());
    }
    finally {
      if (rs != null) {
        rs.close();
      }
      if (objStmt != null) {
        objStmt.close();
      }
      if (objDBA != null) {
        objDBA.closeConnection(DBNames.DB_NAME_HDB);
      }
    }

    logger.debug("Return getReportFromHDB(" + strRepData + ")");
    return strRepData;
  }

  /**
   * Konvertiert ein XML-Doc. nach String
   * 
   * @param Document objDocXML ... zu konvertierendes XML-Dokument
   * 
   * @return String ... das XML-Dokument als Text
   * 
   * @author DMaier
   * 
   * @version dtf-runtime-2.20_060
   */

  private static String convertXMLDocToString(Document objDocXML) {

    String strOutput = null;

    try {
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer transformer;
      transformer = tf.newTransformer();
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      StringWriter writer = new StringWriter();
      transformer.transform(new DOMSource(objDocXML), new StreamResult(writer));
      strOutput = writer.getBuffer().toString().replaceAll("\n|\r", "");
    }
    catch (Exception e) {
      logger.error(" convertXMLDocToString()->Fehler beim Konvertieren (XML->String). " + e.getMessage());
    }

    return strOutput;
  }

  /**
   * Handelt das Loggen der fehlerhaften XMLs auf die HDD
   * 
   * @param String strBerichtHDBDoc .. Bericht aus HDB
   * @param String strBerichtZAMDoc .. Beicht aus ZAM
   * 
   * @return -
   * 
   * @author DMaier
   * 
   * @version dtf-runtime-2.20_060
   */

  private void logErrXML(RepsInZam objRepsInZam) {
    writeErrXML("hdb", objRepsInZam.getiTllID(), objRepsInZam.getlRevNr(), objRepsInZam.getObjXMLDocHDB());
    writeErrXML("zam", objRepsInZam.getiTllID(), objRepsInZam.getlRevNr(), objRepsInZam.getObjXMLDocZAM());
  }

  /**
   * Schreibt die XMLs die nicht gleich sind auf die HDD
   * 
   * @param String strBerichtHDBDoc .. Bericht aus HDB
   * @param String strBerichtZAMDoc .. Beicht aus ZAM
   * 
   * @return -
   * 
   * @author DMaier
   * 
   * @version dtf-runtime-2.20_060
   */

  private void writeErrXML(String strType, int iTllId, long lRevNr, Document objDocXML) {

    String strXMLConvert = convertXMLDocToString(objDocXML);

    PrintWriter pWriterErr = null;

    String strFolder = objRuntimeEnv.getObjActTG().getObjActTC().getStrErgPath();
    new File(strFolder).mkdirs();

    String strFile = strFolder + "RepsInZam_" + lRevNr + "_" + iTllId + "_" + strType + ".xml";

    try {
      pWriterErr = new PrintWriter(new BufferedWriter(new FileWriter(strFile, true)));
      pWriterErr.println(strXMLConvert);

    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }
    finally {
      if (pWriterErr != null) {
        pWriterErr.flush();
        pWriterErr.close();
      }
    }
  }

  /**
   * Handelt das Diffen von zwei Bericht-XMLs
   * 
   * @param Document objDocZAM ... XML als Dokument von ZAM
   * @param Document objDocHDB ... XML als Dokument von HDB
   * 
   * @return int ... Lade für den aktuellen Bediener
   * 
   * @author DMaier
   * 
   * @version dtf-runtime-2.20_060
   */

  private boolean compareXML(RepsInZam objEleRepsInZam) {

    boolean bResult = false;
    // if (objDocZAM.isEqualNode(objDocHDB)) {
    if (objEleRepsInZam.getObjXMLDocHDB().isEqualNode(objEleRepsInZam.getObjXMLDocZAM())) {
      bResult = true;
    }

    return bResult;

  }

  /**
   * Ersetzt Texte im XML bevor sie in ein DOM-Object konvertiert werden
   * 
   * @param String strXML ... XML welches erstezt werden soll
   * 
   * @return String ... XML als String welches ersetzt wurde
   * 
   * @author DMaier
   * 
   * @version dtf-runtime-2.20_060
   */

  private String replaceTextInXML(String strXML) {

    logger.debug("replaceTextInXML(strXML:" + strXML + ")");

    String strReturn = strXML;

    String strToSearch = ">   </"; // Leerzeilen andruck. In der HDB aber weggenommen
    String strToReplace = "></"; // Leerzeilen andruck. In der HDB aber weggenommen

    strReturn = strReturn.replace(strToSearch, strToReplace);

    logger.debug("Return replaceTextInXML: " + strReturn);

    return strReturn;
  }

  /**
   * Erzeugt aus dem String ein XML-Dokument
   * 
   * @param String strXML ... XML welches erstellt werden soll
   * 
   * @return Document ... XML-Dokument
   * 
   * @author DMaier
   * 
   * @version dtf-runtime-2.20_060
   */

  private Document getXMLDoc(String strXML) {

    logger.debug("getXMLDoc(strXML:" + strXML + ")");

    Document objXMLDoc = null;

    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      dbf.setNamespaceAware(true);
      dbf.setCoalescing(true);
      dbf.setIgnoringElementContentWhitespace(true);
      dbf.setIgnoringComments(true);
      DocumentBuilder db = dbf.newDocumentBuilder();

      InputSource src = new InputSource();
      src.setCharacterStream(new StringReader(strXML));
      objXMLDoc = db.parse(src);
      objXMLDoc.normalizeDocument();
    }
    catch (Exception e) {
      logger.error(
          this.getClass().getSimpleName() + " Fehler beim Umwandeln des XML-Dokuments. " + e.getMessage());
    }

    logger.debug("Return replaceTextInXML: Document");

    return objXMLDoc;
  }
}

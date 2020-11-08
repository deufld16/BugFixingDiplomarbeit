package simulator.commands.impl;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.constants.DBNames;
import simulator.database.DBAccess;
import simulator.beans.RuntimeEnv;
import simulator.util.FileUtilities;

public class ExportTableContentCommand
    extends simulator.commands.ACommand {

  /** The logger. */
  private static final Logger logger = LoggerFactory.getLogger(ExportTableContentCommand.class);

  private static final String CONST_PARAM_DB = "db";
  private static final String CONST_PARAM_TABLE = "table";
  private static final String CONST_PARAM_FIELD = "field";
  private static final String CONST_PARAM_FILENAME = "filename";
  private static final String CONST_PARAM_ORDER_BY = "order_by";

  String strDb = "";
  String strTable = "";
  String strField = "";
  String strFilename = "";
  String strOrderBy = "";

  public ExportTableContentCommand(RuntimeEnv env) {
    objRuntimeEnv = env;
  }

  /**
   * Arbeits-Schnittstelle fuer das Command 'EljoCommandV2'<br />
   * Diese Command exportiert eine angegebene Tabelle bzw. deren angegebenen Felder in eine Datei.<br />
   * Das Command wird "pro" Kasse aufgerufen, selektiert jedoch Daten aus der BO-DB.<br />
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

    logger.debug("ExportTableContentCommand->doWork()");

    // Parameter lesen
    strDb = getValueForKey(CONST_PARAM_DB);
    strTable = getValueForKey(CONST_PARAM_TABLE);
    strField = getValueForKey(CONST_PARAM_FIELD);
    strOrderBy = getValueForKey(CONST_PARAM_ORDER_BY);
    strFilename = getValueForKey(CONST_PARAM_FILENAME);

    List<String> resultContent = new ArrayList<String>();

    DBAccess objDBA = DBAccess.getInstance();
    Connection objCon = null;
    Statement objStmt = null;
    ResultSet rs = null;

    String strSQL = "";

    objCon = objDBA.getDBCon(DBNames.DB_NAME_NPOS);
    objStmt = objCon.createStatement();

    strSQL = "Select " + strField + " from " + strTable + " where pos_id = "
        + objRuntimeEnv.getObjActKasse().getiRegId() + " order by " + strOrderBy + " ";

    System.out.println("ExportTableContentCommand()->strSQL=" + strSQL);

    rs = objStmt.executeQuery(strSQL);

    while (rs.next()) {
      resultContent.add(rs.getString(1).replaceAll("\n", ""));
    }

    String strPathWithFile = objRuntimeEnv.getObjActTG().getObjActTC().getStrTestCasePath();
    strPathWithFile += File.separator + strFilename;

    FileUtilities.writeFile(resultContent, strPathWithFile);

    if (rs != null) {
      rs.close();
    }
    if (objStmt != null) {
      objStmt.close();
    }
    if (objDBA != null) {
      objDBA.closeConnection(DBNames.DB_NAME_NPOS);
    }

    return true;
  }

  @Override
  public boolean doCheck() {
    String strPathWithFile = objRuntimeEnv.getObjActTG().getObjActTC().getStrTestCasePath();
    strPathWithFile += File.separator + strFilename;

    if (new File(strPathWithFile).exists()) {
      return true;
    }

    return false;
  }
}

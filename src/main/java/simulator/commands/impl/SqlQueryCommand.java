package simulator.commands.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.commands.helper.VarReplace;
import simulator.constants.DBNames;
import simulator.database.DBAccess;
import simulator.beans.RuntimeEnv;

public class SqlQueryCommand
    extends simulator.commands.ACommand {

  /** The logger. */
  private static final Logger logger = LoggerFactory.getLogger(SqlQueryCommand.class);

  private static final String CONST_PARAM_SQL = "sql";
  private static final String CONST_PARAM_RESULT = "result";
  private static final String CONST_PARAM_DB = "db";
  private static final String CONST_PARAM_REPEAT = "repeat";
  private static final String CONST_PARAM_TIMEOUT = "timeout";

  private String strSql;
  private String strResult;
  private String strDB;
  private String strRepeat;
  private String strTimeout;

  public SqlQueryCommand(RuntimeEnv env) {
    objRuntimeEnv = env;
  }

  /**
   * Arbeits-Schnittstelle fuer das Command 'SqlQueryCommand'<br />
   * Diese Command führt eine SQL-Abfrage durch und vergleicht es mit einem erwarteten Ergebnis in der BO-DB.
   * <br />
   * Das Command wird im BO ausgeführt.<bR>
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

    // Parameter lesen
    strSql = getValueForKey(CONST_PARAM_SQL);
    strResult = getValueForKey(CONST_PARAM_RESULT);
    strDB = getValueForKey(CONST_PARAM_DB);
    strRepeat = getValueForKey(CONST_PARAM_REPEAT);
    strTimeout = getValueForKey(CONST_PARAM_TIMEOUT);

    // Variablen ersetzen
    VarReplace objVarRepl = new VarReplace();
    strSql = objVarRepl.replacePlaceholder(strSql);
    strResult = objVarRepl.replacePlaceholder(strResult);

    int iCounter = 0;
    do {
      iCounter++ ;

      String strCurrentResult = executeQuery();

      if (strResult.equals(strCurrentResult)) {
        return true;
      }
      else {
        logger.debug("desired result: " + strResult);
      }

      Thread.sleep(Integer.valueOf(strTimeout) * 1000);

    }
    while (iCounter <= Integer.valueOf(strRepeat));

    return false;

  }

  private String executeQuery()
      throws Exception {

    logger.debug("executeQuery()");

    String strReturn = null;

    DBAccess objDBA = DBAccess.getInstance();
    Connection objCon = null;
    Statement objStmt = null;
    ResultSet rs = null;

    try {
      objCon = objDBA.getDBCon(strDB);
      objStmt = objCon.createStatement();

      logger.debug("executeQuery() :: strSql=" + strSql);

      rs = objStmt.executeQuery(strSql);

      if (rs.next()) {
        strReturn = rs.getString(1);
      }
    }
    catch (SQLException e) {
      return null;
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

    return strReturn;
  }

  @Override
  public boolean doCheck() {
    return true;
  }

}

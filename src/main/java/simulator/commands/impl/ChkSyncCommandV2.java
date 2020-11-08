package simulator.commands.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.constants.DBNames;
import simulator.database.DBAccess;
import simulator.beans.RuntimeEnv;

public class ChkSyncCommandV2
    extends simulator.commands.ACommand {

  /** The logger. */
  private static final Logger logger = LoggerFactory.getLogger(ChkSyncCommandV2.class);

  private static final String CONST_PARAM_TIMEOUT = "timeout";

  private int iXMLParamTimeout = 60;

  public ChkSyncCommandV2(RuntimeEnv env) {
    objRuntimeEnv = env;
  }

  /**
   * Arbeits-Schnittstelle fuer das Command 'ChkSyncCommandV2'<br />
   * Diese Command prueft alle Kassen aussyncronisiert sind<br />
   * Das Command sollte nur einmal am BO-PC ausgefuerht werden.<br />
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

    // Parameter ermitteln
    iXMLParamTimeout = Integer.parseInt(getValueForKey(CONST_PARAM_TIMEOUT));

    int iCashCount = objRuntimeEnv.getiRegCount();
    logger.debug("iCashCount=" + iCashCount);

    Integer iMaxSynclog = getMaxSynclog();
    logger.debug("iMaxSynclog=" + iMaxSynclog);

    DBAccess objDBA = DBAccess.getInstance();
    Connection objCon = null;
    Statement objStmt = null;
    ResultSet rs = null;

    objCon = objDBA.getDBCon(DBNames.DB_NAME_NPOS);
    objStmt = objCon.createStatement();

    for (int iCnt = 0; iCnt < iXMLParamTimeout; iCnt++ ) {
      int iSyncedCashes = 0;

      String strSQL = "SELECT COUNT(1) AS GesAnz FROM reg_d WHERE reg_adr <> 0 AND sy_ptr >=" + iMaxSynclog;
      logger.debug("doWork() :: strSQL=" + strSQL);

      rs = objStmt.executeQuery(strSQL);

      if (rs.next()) {
        iSyncedCashes = rs.getInt("GesAnz");
      }

      if (iSyncedCashes >= iCashCount) {
        logger.debug("all cashes synced");
        return true;
      }

      Thread.sleep(10000);
    }

    if (rs != null) {
      rs.close();
    }
    if (objStmt != null) {
      objStmt.close();
    }
    if (objDBA != null) {
      objDBA.closeConnection(DBNames.DB_NAME_NPOS);
    }

    logger.debug("error syncing cashes");
    return false;
  }

  @Override
  public boolean doCheck() {
    // TODO Auto-generated method stub
    return true;
  }

  /**
   * sets the max synclog variable
   */
  private int getMaxSynclog()
      throws Exception {
    DBAccess objDBA = DBAccess.getInstance();
    Connection objCon = null;
    Statement objStmt = null;
    ResultSet rs = null;

    objCon = objDBA.getDBCon(DBNames.DB_NAME_NPOS);
    objStmt = objCon.createStatement();

    String strSQL = "Select max(id) AS MaxSyncID from synclog";
    logger.debug("doWork() :: strSQL=" + strSQL);

    rs = objStmt.executeQuery(strSQL);
    int iMaxSync = 0;

    if (rs.next()) {
      iMaxSync = rs.getInt("MaxSyncID");
    }

    if (rs != null) {
      rs.close();
    }
    if (objStmt != null) {
      objStmt.close();
    }
    if (objDBA != null) {
      objDBA.closeConnection(DBNames.DB_NAME_NPOS);
    }

    logger.debug("maxSynclog= " + iMaxSync);

    return iMaxSync;

  }
}

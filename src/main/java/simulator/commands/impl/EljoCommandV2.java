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

public class EljoCommandV2
    extends simulator.commands.ACommand {

  /** The logger. */
  private static final Logger logger = LoggerFactory.getLogger(EljoCommandV2.class);

  private static final String CONST_PARAM_SALEIDFROM = "saleidfrom";
  private static final String CONST_PARAM_SALEIDTO = "saleidto";

  private int iSaleIdFrom;
  private int iSaleIdTo;

  private static final int CONST_SLEEP_TIME = 2000;

  public EljoCommandV2(RuntimeEnv env) {
    objRuntimeEnv = env;
  }

  /**
   * Arbeits-Schnittstelle fuer das Command 'EljoCommandV2'<br />
   * Diese Command exportiert das elektronische Journal aus der BO-DB in eine Datei.<br />
   * Das Command wird "pro" Kasse aufgerufen.<br />
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

    logger.debug("EljoCommandV2->doWork()");

    // Parameter lesen
    iSaleIdFrom = Integer.parseInt(getValueForKey(CONST_PARAM_SALEIDFROM));
    iSaleIdTo = Integer.parseInt(getValueForKey(CONST_PARAM_SALEIDTO));

    for (int iBonNr = iSaleIdFrom; iBonNr <= iSaleIdTo; iBonNr++ ) {
      List<String> lstBonData = loadBonData(iBonNr);
      if (lstBonData.size() > 0) {
        logger.debug("write Bondata for Bon " + iBonNr);

        FileUtilities.writeFile(lstBonData, objRuntimeEnv.getObjActTG().getObjActTC().getStrErgPath()
            + File.separator + "bon" + iBonNr + ".txt");
      }
    }

    return true;
  }

  private List<String> loadBonData(int iBonNr)
      throws Exception {

    logger.debug("loadBonData(iBonNr=" + iBonNr + ")");
    List<String> lstBonData = new ArrayList<String>();

    DBAccess objDBA = DBAccess.getInstance();
    Connection objCon = null;
    Statement objStmt = null;
    ResultSet rs = null;

    try {
      objCon = objDBA.getDBCon(DBNames.DB_NAME_NPOS);
      objStmt = objCon.createStatement();

      String[] arrStartMarker = loadBonStart(iBonNr);

      String[] arrEndMarker = new String[2];
      for (int iCnt = 0; iCnt < 20; iCnt++ ) {
        arrEndMarker = loadBonEnd(iBonNr);
        if ((arrEndMarker[0] != null) && (arrEndMarker[0].length() > 0)) {
          break;
        }

        logger.debug("loadBonEnd - no data for BonID " + iBonNr + " found try " + iCnt + "/20");
        try {
          Thread.sleep(CONST_SLEEP_TIME);
        }
        catch (InterruptedException e) {
          logger.error(e.getLocalizedMessage());
        }
      }

      String strQuery =
          "SELECT zusdat FROM t_log1 WHERE pos_id = " + objRuntimeEnv.getObjActKasse().getiRegId()
              + " AND kat = 74 AND ((T_id = " + arrStartMarker[0] + " AND T_FF >= " + arrStartMarker[1]
              + ") OR (T_ID > " + arrStartMarker[0] + " AND T_ID < " + arrEndMarker[0] + ") OR (T_ID = "
              + arrEndMarker[0] + " AND T_FF <= " + arrEndMarker[1] + ")) ORDER BY t_id, t_ff";

      logger.debug("strQuery=" + strQuery);

      rs = objStmt.executeQuery(strQuery);

      while (rs.next()) {
        logger.debug(rs.getString(1));
        lstBonData.add(rs.getString(1));
      }
    }
    catch (Exception e) {
      logger.error(e.getLocalizedMessage());
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

    return lstBonData;

  }

  private String[] loadBonStart(int iBonNr)
      throws Exception {
    logger.debug("loadBonStart(iBonNr=" + iBonNr + ")");
    String[] arrTidTff = new String[2];

    DBAccess objDBA = DBAccess.getInstance();
    Connection objCon = null;
    Statement objStmt = null;
    ResultSet rs = null;

    try {
      objCon = objDBA.getDBCon(DBNames.DB_NAME_NPOS);
      objStmt = objCon.createStatement();

      String query =
          "SELECT t_id, t_ff FROM t_log1 WHERE pos_id = " + objRuntimeEnv.getObjActKasse().getiRegId()
              + " and kat = 74 AND ZUSDAT LIKE '§#§:" + iBonNr + ":%' ORDER BY t_id, t_ff";

      rs = objStmt.executeQuery(query);

      if (rs.next()) {
        arrTidTff[0] = rs.getString(1);
        arrTidTff[1] = rs.getString(2);
      }
    }
    catch (Exception e) {
      logger.error(e.getLocalizedMessage());
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

    logger.debug("loadBonStart return " + arrTidTff.toString());
    return arrTidTff;
  }

  private String[] loadBonEnd(int iBonNr)
      throws Exception {
    logger.debug("loadBonEnd(iBonNr=" + iBonNr + ")");
    String[] arrTidTff = new String[2];

    DBAccess objDBA = DBAccess.getInstance();
    Connection objCon = null;
    Statement objStmt = null;
    ResultSet rs = null;

    try {
      objCon = objDBA.getDBCon(DBNames.DB_NAME_NPOS);
      objStmt = objCon.createStatement();

      String query =
          "SELECT t_id, t_ff FROM t_log1 WHERE pos_id = " + objRuntimeEnv.getObjActKasse().getiRegId()
              + " and kat = 74 AND ZUSDAT LIKE '§@§:" + iBonNr + ":%' ORDER BY t_id, t_ff";

      rs = objStmt.executeQuery(query);

      if (rs.next()) {
        arrTidTff[0] = rs.getString(1);
        arrTidTff[1] = rs.getString(2);
      }
    }
    catch (Exception e) {
      logger.error(e.getLocalizedMessage());
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

    logger.debug("loadBonEnd return " + arrTidTff.toString());
    return arrTidTff;
  }

  @Override
  public boolean doCheck() {
    return true;
  }
}

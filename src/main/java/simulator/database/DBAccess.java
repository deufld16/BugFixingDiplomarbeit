package simulator.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import simulator.constants.DBNames;

public class DBAccess {

  private static DBAccess theInstance;

  private Map<DBNames, Connection> mapDBCons = new HashMap<DBNames, Connection>();

  private static final String CONST_DBNAME_NPOS = "npos";
  private static final String CONST_DBNAME_HDB = "historyd";
  private static final String CONST_DBNAME_ETILAG = "etilag";
  private static final String CONST_DBNAME_ESLLOG = "esllog";
  private static final String CONST_DBNAME_PRO_TEST = "prometheus_test";
  private static final String CONST_DBNAME_KAFEIN_PROD = "karstadt_prod";
  private static final String CONST_DBNAME_KAFEIN_TEST = "karstadt_test";
  private static final String CONST_DBNAME_PRO_PROD = "prometheus_prod";

  private DBAccess() {

  }

  public static DBAccess getInstance() {
    if (theInstance == null) {
      theInstance = new DBAccess();
    }
    return theInstance;
  }

  public Connection getDBCon(DBNames objDBNames) {
    if (!mapDBCons.containsKey(objDBNames)) {
      createDBCon(objDBNames);
    }

    Connection objCon = mapDBCons.get(objDBNames);
    try {
      if ((objCon == null) || objCon.isClosed()) {
        createDBCon(objDBNames);
      }
    }
    catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }

    return mapDBCons.get(objDBNames);
  }

  public Connection getDBCon(String strDBName) {

    DBNames objDBNames = null;
    objDBNames = DBNames.DB_NAME_NPOS;

    switch (strDBName) {
      case CONST_DBNAME_NPOS:
        objDBNames = DBNames.DB_NAME_NPOS;
        break;
      case CONST_DBNAME_HDB:
        objDBNames = DBNames.DB_NAME_HDB;
        break;
      case CONST_DBNAME_ETILAG:
        objDBNames = DBNames.DB_NAME_ETILAG;
        break;
      case CONST_DBNAME_ESLLOG:
        objDBNames = DBNames.DB_NAME_ESLLOG;
        break;
      case CONST_DBNAME_PRO_TEST:
        objDBNames = DBNames.PROMETHEUS_TEST;
        break;
      case CONST_DBNAME_KAFEIN_PROD:
        objDBNames = DBNames.KARSTADT_PROD;
        break;
      case CONST_DBNAME_KAFEIN_TEST:
        objDBNames = DBNames.KARSTADT_TEST;
        break;
      case CONST_DBNAME_PRO_PROD:
        objDBNames = DBNames.PROMETHEUS_PROD;
        break;
      default:
        break;
    }

    if (strDBName.equals(DBNames.DB_NAME_NPOS)) {
      objDBNames = DBNames.DB_NAME_NPOS;
    }

    return getDBCon(objDBNames);

  }

  private void createDBCon(DBNames objDBNames) {
    // ---------------DB-Properties lesen------------------
    DBProp objDBProp = new DBProp(
        "C:\\Develop\\dstore_Testframework\\dtf-project-suite\\trunk\\DTFCommands\\res\\dbprop.properties",
        objDBNames);

    Connection objCon;

    // ---------------Verbindung aufbauen------------------
    try {
      Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    try {
      String strUrl = "jdbc:db2://" + objDBProp.getStrHostname() + ":" + objDBProp.getlPort() + "/"
          + objDBProp.getStrDBName();

      System.out.println("strUrl: " + strUrl);
      System.out.println("User: " + objDBProp.getStrUser());
      System.out.println("Pwd: " + objDBProp.getStrPwd());

      objCon = DriverManager.getConnection(strUrl, objDBProp.getStrUser(), objDBProp.getStrPwd());
      mapDBCons.put(objDBNames, objCon);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void closeConnection(DBNames objDBNames) {
    Connection objCon = mapDBCons.get(objDBNames);
    try {
      if ((objCon == null) || objCon.isClosed()) {
        objCon.close();
      }
    }
    catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}

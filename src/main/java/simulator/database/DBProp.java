package simulator.database;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.constants.DBNames;

public class DBProp {

  /** The logger. */
  private static final Logger logger = LoggerFactory.getLogger(DBProp.class);

  private static final String CONST_DEFAULT_PROP_FILE = "/opt/oracle/dstore/dtf/config/dbcon.properties";

  private static final String CONST_DBPROP_DBHOST = "dbhost";
  private static final String CONST_DBPROP_DBPORT = "dbport";
  private static final String CONST_DBPROP_DBNAME = "dbname";
  private static final String CONST_DBPROP_DBUSER = "dbuser";
  private static final String CONST_DBPROP_DBPASSWD = "dbpasswd";

  private String strHostname = null;
  private Long lPort = null;
  private String strDBName = null;
  private String strUser = null;
  private String strPwd = null;

  public DBProp(DBNames objDBNames) {
    readPropsFromFile(CONST_DEFAULT_PROP_FILE, objDBNames);
  }

  public DBProp(String strFilePath, DBNames objDBNames) {
    readPropsFromFile(strFilePath, objDBNames);
  }

  private void readPropsFromFile(String strFilePath, DBNames objDBNames) {
    File objPropFile = new File(strFilePath);
    Properties objProp = new Properties();

    try (BufferedInputStream objBufInpStream = new BufferedInputStream(new FileInputStream(objPropFile))) {
      objProp.load(objBufInpStream);
    }
    catch (Exception ex) {
      strHostname = null;
      lPort = null;
      strDBName = null;
      strUser = null;

      strPwd = null;
    }

    System.out.println("----------------------------------");
    System.out.println("Prop:" + objProp.toString());
    System.out.println("----------------------------------");

    strHostname = objProp.getProperty(objDBNames + "_" + CONST_DBPROP_DBHOST);
    lPort = Long.parseLong(objProp.getProperty(objDBNames + "_" + CONST_DBPROP_DBPORT));
    strDBName = objProp.getProperty(objDBNames + "_" + CONST_DBPROP_DBNAME);
    strUser = objProp.getProperty(objDBNames + "_" + CONST_DBPROP_DBUSER);
    strPwd = objProp.getProperty(objDBNames + "_" + CONST_DBPROP_DBPASSWD);
  }

  public String getStrHostname() {
    return strHostname;
  }

  public void setStrHostname(String strHostname) {
    this.strHostname = strHostname;
  }

  public long getlPort() {
    return lPort;
  }

  public void setlPort(long lPort) {
    this.lPort = lPort;
  }

  public String getStrDBName() {
    return strDBName;
  }

  public void setStrDBName(String strDBName) {
    this.strDBName = strDBName;
  }

  public String getStrUser() {
    return strUser;
  }

  public void setStrUser(String strUser) {
    this.strUser = strUser;
  }

  public String getStrPwd() {
    return strPwd;
  }

  public void setStrPwd(String strPwd) {
    this.strPwd = strPwd;
  }

  @Override
  public String toString() {
    return "DBProp [strHostname=" + strHostname + ", lPort=" + lPort + ", strDBName=" + strDBName
        + ", strUser=" + strUser + ", strPwd=" + strPwd + "]";
  }

}

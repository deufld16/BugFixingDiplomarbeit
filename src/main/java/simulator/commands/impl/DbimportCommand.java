package simulator.commands.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.beans.RuntimeEnv;
import simulator.interfaces.BackofficeAccess;

@BackofficeAccess(access=true)
public class DbimportCommand
    extends simulator.commands.ACommand {

  /** The logger. */
  private static final Logger logger = LoggerFactory.getLogger(DbimportCommand.class);

  private static final String CONST_INSERT_SCRIPT = "dtf_insert.sh";

  private static final String CONST_PARAM_INSERTFILE = "insertfile";

  private String strImportFile;

  public DbimportCommand(RuntimeEnv env) {
    objRuntimeEnv = env;
  }

  /**
   * Arbeits-Schnittstelle fuer das Command 'DbimportCommand'<br />
   * Dieses Command import mittesl dbimport am BO-PC eine Insert-Datei<br />
   * Das Command wird am BO-PC ausgefuhert.<br />
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
  public boolean doWork() {

    logger.debug("DbimportCommand->doWork()");

    // Parameter ermitteln
    strImportFile = getValueForKey(CONST_PARAM_INSERTFILE);

    if (!importFile()) {
      return false;
    }

    return true;
  }

  /**
   * checks if destination file exists
   */

  @Override
  public boolean doCheck() {
    return true;
  }

  private boolean importFile() {
    String strSHFile = objRuntimeEnv.getObjActTG().getObjActTC().getStrErgPath();
    strSHFile += File.separator + new Date().getTime() + CONST_INSERT_SCRIPT;

    String strInserFileWithPath = objRuntimeEnv.getObjActTG().getObjActTC().getStrTestCasePath();
    strInserFileWithPath += File.separator + "" + strImportFile;

    String strCommand = "";

    BufferedWriter bw = null;
    FileWriter fw = null;

    File objFile = new File(strSHFile);
    try {
      fw = new FileWriter(objFile);

      bw = new BufferedWriter(fw);

      bw.write("#!/bin/bash\n");
      bw.write(". /home/npos/.bash_profile\n");
      strCommand =
          "/opt/oracle/dstore/bo-basis-utils/bin/dbimport 1 < " + strInserFileWithPath + " 2> /dev/null";

      bw.write(strCommand + "\n");

      objFile.setExecutable(true);
    }
    catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return false;
    }
    finally {
      try {
        if (bw != null) {
          bw.close();
        }

        if (fw != null) {
          fw.close();
        }
      }
      catch (IOException ex) {
        logger.warn("Error closing resources! " + ex.getMessage());
      }
    }
    return true;
  }
}
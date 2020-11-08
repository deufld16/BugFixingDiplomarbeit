package simulator.commands.impl;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.commands.helper.VarReplace;
import simulator.beans.RuntimeEnv;

public class CopyCommand
    extends simulator.commands.ACommand {

  /** The logger. */
  private static final Logger logger = LoggerFactory.getLogger(CopyCommand.class);

  private static final String CONST_PARAM_SCRFILE = "srcfile";
  private static final String CONST_PARAM_DESTFILE = "destfile";
  private static final String CONST_PARAM_ISDIRECTORY = "isdirectory";

  private int iCounter = 0;

  private String strSrcFile;
  private String strDestFile;
  private boolean bIsDirectory;

  public CopyCommand(RuntimeEnv env) {
    objRuntimeEnv = env;
  }

  /**
   * Arbeits-Schnittstelle fuer das Command 'CopyCommand'<br />
   * Diese Command kopiert eine Datei<br />
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

    logger.debug("CopyCommand->doWork()");

    // Parameter ermitteln
    strSrcFile = getValueForKey(CONST_PARAM_SCRFILE);
    strDestFile = getValueForKey(CONST_PARAM_DESTFILE);
    bIsDirectory = Boolean.parseBoolean(getValueForKey(CONST_PARAM_ISDIRECTORY));

    // TODO: richtigen Replaces einfuegen
    VarReplace objVarRepl = new VarReplace();
    strSrcFile = objVarRepl.replacePlaceholder(strSrcFile);
    strDestFile = objVarRepl.replacePlaceholder(strDestFile);

    if ((strSrcFile == null) || (strDestFile == null)) {
      return false;
    }
    else if (new File(strSrcFile).isDirectory()) {
      try {
        FileUtils.copyDirectory(new File(strSrcFile), new File(strDestFile));
      }
      catch (IOException ioe) {
        logger.error(" unable to copy file: " + ioe.getMessage());
      }
      return true;

    }
    else {
      try {
        if (new File(strDestFile).getName().startsWith("v")
            || new File(strDestFile).getName().startsWith("z")) {
          strDestFile = new File(strDestFile).getParent() + File.separator
              + new File(strDestFile).getName().toUpperCase();
        }
        logger.debug("srcFile: " + strSrcFile + ", destFile: " + strDestFile);

        if (!new File(strSrcFile).getParentFile().exists()) {
          logger.debug("srcFile parent folder existiert nicht: " + new File(strSrcFile).getParent());
        }

        if (!new File(strSrcFile).exists()) {
          logger.debug("srcFile existiert nicht: " + new File(strSrcFile).getAbsolutePath());
        }

        if (!new File(strDestFile).getParentFile().exists()) {
          logger.debug("destFile parent folder existstiert nicht: "
              + new File(strDestFile).getParentFile().getAbsolutePath());
          new File(strDestFile).getParentFile().mkdirs();
        }

        FileUtils.copyFile(new File(strSrcFile), new File(strDestFile));
      }
      catch (IOException ioe) {

        logger.error(" unable to copy file: " + ioe.getMessage());

        ioe.printStackTrace();

        while (iCounter < 20) {

          logger.debug("IOException loop: " + iCounter);

          try {
            logger.debug("wait 20 seconds");
            Thread.sleep(TimeUnit.SECONDS.toMillis(20));
          }
          catch (InterruptedException e) {
            e.printStackTrace();
          }

          try {
            FileUtils.copyFile(new File(strSrcFile), new File(strDestFile));
            logger.debug("break loop");
            break;
          }
          catch (IOException e) {

            logger.debug("again an exception: " + e.getMessage());
          }

          iCounter++ ;

          logger.debug("try it ounce again");

        }

      }
      return true;
    }

  }

  /**
   * checks if destination file exists
   */

  @Override
  public boolean doCheck() {
    logger.debug(" check if destination file exists: " + strDestFile);
    if (strDestFile != null) {
      if (new File(strDestFile).exists()) {
        return true;
      }
    }
    return false;
  }
}

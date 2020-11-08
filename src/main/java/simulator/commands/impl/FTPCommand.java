package simulator.commands.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.commands.helper.VarReplace;
import simulator.beans.RuntimeEnv;
import simulator.util.NetworkUtilities;

public class FTPCommand
    extends simulator.commands.ACommand {

  /** The logger. */
  private static final Logger logger = LoggerFactory.getLogger(FTPCommand.class);

//  private static final String CONST_PARAM_HOST = "host";
  private static final String CONST_PARAM_USERNAME = "username";
  private static final String CONST_PARAM_PASSWORD = "password";
  private static final String CONST_PARAM_COMMANDMETHOD = "command_method";
  private static final String CONST_PARAM_FILES = "files";
  private static final String CONST_PARAM_FOLDER = "folder";

//  private String strHost = "";
  private String strCommand = "";
  private String strUsername = "";
  private String strPassword = "";
  private String strFilename = "";
  private String strFolder = "";
  private String strFiles = "";

  public FTPCommand(RuntimeEnv env) {
    objRuntimeEnv = env;
  }

  private String[] filesArray;

  private static final int WAITTIME = 20000;

  /**
   * opens a ftp connection to a desired client and either get or put some files to the client
   * 
   * limitation, you can only get or put files from or to one directory
   */

  /**
   * Arbeits-Schnittstelle fuer das Command 'FTPCommand'<br />
   * Diese Command bietet pro Kasse eine FTP-Schnittstelle an die Kasse.<br />
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

    logger.debug("FTPCommand->doWork()");

    // Parameter lesen
//    strHost = getValueForKey(CONST_PARAM_HOST);
    strUsername = getValueForKey(CONST_PARAM_USERNAME);
    strPassword = getValueForKey(CONST_PARAM_PASSWORD);
    strCommand = getValueForKey(CONST_PARAM_COMMANDMETHOD);
    strFiles = getValueForKey(CONST_PARAM_FILES);
    strFolder = getValueForKey(CONST_PARAM_FOLDER);

    NetworkUtilities objNetworkUtil = new NetworkUtilities();
    VarReplace objVarRepl = new VarReplace();

    if (!strCommand.equalsIgnoreCase("get") && !strCommand.equalsIgnoreCase("mget")
        && !strCommand.equalsIgnoreCase("mput") && !strCommand.equalsIgnoreCase("put")
        && !strCommand.equalsIgnoreCase("delete")) {
      throw new IllegalArgumentException("invalid command argument: " + strCommand);
    }

    filesArray = strFiles.split(";");
    for (int i = 0; i < filesArray.length; i++ ) {
      filesArray[i] = objVarRepl.replacePlaceholder(filesArray[i]);
      logger.debug(this.getClass().getSimpleName() + " after substitution: " + filesArray[i]);
    }

//    strHost = objVarRepl.replacePlaceholder(strHost);
    strFolder = objVarRepl.replacePlaceholder(strFolder);
    strFiles = objVarRepl.replacePlaceholder(strFiles);

    if (strCommand.equalsIgnoreCase("get") || strCommand.equalsIgnoreCase("put")) {
      strCommand = "M" + strCommand;
    }

    if (strCommand.equalsIgnoreCase("delete")) {
      return deleteFileOnRegister();
    }
    FTPClient client = new FTPClient();

    int counter = 1;

    while (!objNetworkUtil.isReachable(objRuntimeEnv.getObjActKasse().getStrIpAdr(), CONST_FTP_PORT)) {
      if (counter < 1) {
        logger.debug("cash isn't reachable: trial " + counter);
        Thread.sleep(WAITTIME);
      }
      else {
        return false;
      }
      counter++ ;
    }

    client.connect(objRuntimeEnv.getObjActKasse().getStrIpAdr());

    boolean login = client.login(strUsername, strPassword);

    if (login) {
      logger.info(this.getClass().getSimpleName() + " Login success");

      int reply = client.getReplyCode();

      if (client.changeWorkingDirectory(strFolder)) {
        logger.debug(this.getClass().getSimpleName() + " after changing to folder: " + strFolder);
      }
      else {
        createDirectoryTree(client);
      }

      if (strCommand.equalsIgnoreCase("GET")) {
        singleFileFtpGet(client, strFilename, reply);
      }
      else if (strCommand.equalsIgnoreCase("MGET")) {
        multipleFileFtpGet(client, filesArray, reply);
      }
      else if (strCommand.equalsIgnoreCase("PUT")) {
        singleFileFtpPut(client, strFilename, reply);
      }
      else if (strCommand.equalsIgnoreCase("MPUT")) {
        multipleFileFtpPut(client, filesArray, reply);
      }

      boolean logout = client.logout();
      if (logout) {
        logger.info(getClass().getSimpleName() + "Logout from FTP server ...");
      }
      else {
        logger.error(getClass().getSimpleName() + "Login fail ...");
      }

    }
    else {
      throw new FTPConnectionClosedException();
    }

    return true;
  }

  private boolean deleteFileOnRegister() {
    FTPClient ftpClient = new FTPClient();
    try {

      ftpClient.connect(objRuntimeEnv.getObjActKasse().getStrIpAdr());

      int replyCode = ftpClient.getReplyCode();
      if (!FTPReply.isPositiveCompletion(replyCode)) {
        logger.debug(this.getClass().getSimpleName() + " unable to connect to: "
            + objRuntimeEnv.getObjActKasse().getStrIpAdr());
        return false;
      }

      boolean success = ftpClient.login(strUsername, strPassword);

      if (!success) {
        logger.debug(this.getClass().getSimpleName() + " could not login to the register: "
            + objRuntimeEnv.getObjActKasse().getStrIpAdr());
        return false;
      }
      String file2delete = "";
      file2delete = strFolder + "/" + filesArray[0];
      logger.debug(this.getClass().getSimpleName() + " file to delete: " + file2delete);
      boolean deleted = ftpClient.deleteFile(file2delete);
      if (deleted) {
        logger.debug(this.getClass().getSimpleName() + " the file was deleted successfully.");
      }
      else {
        logger.debug(this.getClass().getSimpleName() + " could not delete the file, it may not exist.");
      }

    }
    catch (IOException ex) {
      logger.error(this.getClass().getSimpleName() + " error while deleting ev file on the register occured: "
          + ex.getMessage());
      return false;
    }
    finally {
      // logs out and disconnects from server
      try {
        if (ftpClient.isConnected()) {
          ftpClient.logout();
          ftpClient.disconnect();
        }
      }
      catch (IOException ex) {
        logger.error(this.getClass().getSimpleName()
            + " error while deleting ev file on the register occured: " + ex.getMessage());
        return false;
      }
    }
    return true;
  }

  /**
   * creates the directory hierarchy on the remote ftp server
   * @param client
   * @throws IOException
   */
  private boolean createDirectoryTree(FTPClient client)
      throws IOException {
    boolean dirExists = true;
    // filter /home/nos
    if (strFolder.startsWith("/home/npos/")) {
      strFolder = strFolder.substring(11);
      logger.debug(this.getClass().getSimpleName() + " folder is set to: " + strFolder);
    }
    String[] directories = strFolder.split("/");
    for (String dir : directories) {
      logger.debug(this.getClass().getSimpleName() + " check sub dir: " + dir);
      if (!dir.isEmpty()) {
        if (dirExists) {
          dirExists = client.changeWorkingDirectory(dir);
        }
        if (!dirExists) {
          logger.debug(this.getClass().getSimpleName() + " folder does not exist - create it: " + dir);
          if (!client.makeDirectory(dir)) {
            logger.debug(this.getClass().getSimpleName() + " unable to create directory: " + dir + " error="
                + client.getReplyString() + " code=" + client.getReplyCode());
            return false;
          }
          else {
            logger.debug(this.getClass().getSimpleName() + " successfully created.");
          }
          if (!client.changeWorkingDirectory(dir)) {
            return false;
          }
          else {
            logger.debug(
                this.getClass().getSimpleName() + " changed to directory: " + client.printWorkingDirectory());
          }
        }
      }

    }

    return true;

  }

  @Override
  public boolean doCheck() {
    return true;
  }

  /**
   * puts a single file to a directory on a ftp server
   * 
   * @param client
   * @param filename
   * @param reply
   * @return
   * @throws IOException
   */

  private boolean singleFileFtpPut(FTPClient client, String filename, int reply)
      throws IOException {

    boolean retValue = false;

    if (FTPReply.isPositiveCompletion(reply)) {
      File file = new File(filename);
      logger.debug("file exists: " + file.exists());
      logger.debug("absolutePath: " + file.getAbsolutePath());
      FileInputStream input = new FileInputStream(file);
      // always transfer files in binary mode
      client.setFileType(FTP.BINARY_FILE_TYPE);

      retValue = client.storeFile(file.getName(), input);

      if (!retValue) {
        logger.error(getClass().getSimpleName() + " upload failed for file: " + file.getAbsolutePath()
            + " on client: " + client.getRemoteAddress());
      }

      input.close();
    }
    return retValue;
  }

  /**
   * puts multiple files to the same directory on a ftp server
   * 
   * @param client
   * @param files
   * @param reply
   * @return
   * @throws IOException
   */

  private boolean multipleFileFtpPut(FTPClient client, String[] files, int reply)
      throws IOException {

    boolean retValue = false;

    for (String filename : files) {

      filename = filename.replace("\\", "/");

      retValue = singleFileFtpPut(client, filename, reply);
    }

    return retValue;
  }

  /**
   * gets one file from one directory on an ftp server
   * 
   * @param client
   * @param filename
   * @param reply
   * @return
   * @throws IOException
   */

  private boolean singleFileFtpGet(FTPClient client, String filename, int reply)
      throws IOException {

    boolean retValue = false;

    if (!FTPReply.isNegativeTransient(reply)) {
      logger.debug(this.getClass().getSimpleName() + " file to store: " + filename);
      File file = new File(filename);
      if (!file.exists()) {
        File parent = file.getParentFile();
        parent.mkdirs();
        file.createNewFile();
      }

      // PMR 2014-02-20 don't overwrite File, if there is any content
      FileOutputStream output = new FileOutputStream(file, true);
      client.setFileType(FTP.BINARY_FILE_TYPE);

      // if file already exists don't get it
      if (file.exists() && (file.length() > 0)) {
        output.close();
        return true;
      }

      String getFile = file.getName();

      logger.debug("File to get: " + getFile);
      logger.debug("File to store: " + file.getAbsolutePath());

      retValue = client.retrieveFile(getFile, output);

      // PMR 2012-01-15
      // R2012/241
      // advanced logging functionalities

      if (!retValue) {
        logger.error(getClass().getSimpleName() + " download failed! For file: " + getFile + " Return value: "
            + client.getReplyCode());
      }
      else {
        logger.info("file " + file.getAbsolutePath() + " downloaded");
      }

      output.close();
    }
    else {
      logger.error(this.getClass().getSimpleName() + " unable to retrieve data from the client: "
          + client.getRemoteAddress());
    }
    return retValue;
  }

  /**
   * gets multiple files from one directory on an ftp server
   * 
   * @param client
   * @param files
   * @param reply
   * @return
   * @throws IOException
   */

  private boolean multipleFileFtpGet(FTPClient client, String[] files, int reply)
      throws IOException {

    boolean retValue = false;
    logger.debug(this.getClass().getSimpleName() + " in multipleFileFtpGet");

    for (String filename : files) {
      logger.debug(this.getClass().getSimpleName() + " file to get is: " + filename);
      retValue = singleFileFtpGet(client, filename, reply);
    }

    return retValue;
  }
}

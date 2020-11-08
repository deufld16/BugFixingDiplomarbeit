package simulator.commands.impl;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.beans.RuntimeEnv;
import simulator.util.register.ControlCall;

public class CtrlCommandV2
    extends simulator.commands.ACommand {

  /** The logger. */
  private static final Logger logger = LoggerFactory.getLogger(CtrlCommandV2.class);

  private ControlCall cc;

  private String strCommand;
  private String strArgument;
  private int timeout;

  private static final int BLOCKLOGIN = 3;
  private static final int UNBLOCKLOGIN = 4;
  private static final int FORCELOGOUT = 5;
  private static final int BLOCKKEYBOARD = 6;
  private static final int UNBLOCKKEYBOARD = 7;
  private static final int MONITORON = 8;
  private static final int MONITOROFF = 9;
  private static final int RECSTART = 10;
  private static final int BATCHSTART = 11;
  private static final int RESETDATA = 12;
  private static final int CLOSEFILES = 13;
  private static final int OPENFILES = 14;
  private static final int FISCALCOMMAND = 15;

  private static final String CONST_PARAM_COMMAND = "command";
  private static final String CONST_PARAM_ARGUMENT = "argument";
  private static final String CONST_PARAM_TIMEOUT = "timeout";

  public CtrlCommandV2(RuntimeEnv env) {
    objRuntimeEnv = env;
  }

  /**
   * Arbeits-Schnittstelle fuer das Command 'CtrlCommandV2'<br />
   * Diese fuhert ein CtrCommand an einer Kasse aus.<br />
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

    logger.debug("CtrlCommandV2->doWork()");

    // Parameter ermitteln
    strCommand = getValueForKey(CONST_PARAM_COMMAND);
    strArgument = getValueForKey(CONST_PARAM_ARGUMENT);
    timeout = Integer.parseInt(getValueForKey(CONST_PARAM_TIMEOUT));

    createControllCall(objRuntimeEnv.getStrServerAddr());

    int workCommand = checkCommandParameter();

    handleCommands(workCommand);

    return true;
  }

  @Override
  public boolean doCheck() {
    return true;
  }

  @SuppressWarnings("serial")
  private static final HashMap<String, String> fiscalCommands = new HashMap<String, String>() {
    {
      put("reload", "R");
      put("boot", "B");
      put("stop", "S");
      put("reread", "P");
      put("resend", "A");
      put("status", "K");
      put("chkrec", "C");
      put("quicksync", "y");
    };
  };

  private void handleCommands(int workCommand) {

    if (workCommand == 0) {
      throw new IllegalArgumentException("invalid command parameter set for CtrlCommand");
    }
    else if (isCommandWithParameter(workCommand)) {
      logger.debug(this.getClass().getSimpleName() + " CtrlCommand with parameter is used.");
      checkNoArgument();
      setArgumentToFiscalCommand();
      handleStatusFiscalCommand(workCommand);
      handleOtherFiscalCommand(workCommand);
      // handleFiscalCommandWithoutArgument(workCommand);
    }
    else {
      handleFiscalCommandWithoutArgument(workCommand);
    }

    recstartWait(workCommand);

  }

  private void handleFiscalCommandWithoutArgument(int workCommand) {
    logger.debug(this.getClass().getSimpleName() + " fisccmd without argument.");

    doCmd(workCommand);

    logger.debug(this.getClass().getSimpleName() + " waiting");
    sleep(timeout);

  }

  private void recstartWait(int workCommand) {
    if (workCommand == RECSTART) {
      sleep(timeout);
    }
  }

  private void handleOtherFiscalCommand(int workCommand) {
    logger.debug(this.getClass().getSimpleName() + " other than fisccmd status. " + strArgument);

    logger.debug(this.getClass().getSimpleName() + " execute command " + workCommand + " argument: "
        + strArgument + " for: " + objRuntimeEnv.getObjActKasse().getiRegId());
    doCmd(strArgument, workCommand);

    logger.debug(this.getClass().getSimpleName() + " waiting");
    sleep(timeout);

  }

  private void handleStatusFiscalCommand(int workCommand) {

    if (strArgument.equals(fiscalCommands.get("status"))) {
      checkSingleCashStatus(workCommand);
    }
  }

  private void checkSingleCashStatus(int workCommand) {
    int retval = 0;

    while (retval != 1) {
      sleep(timeout);
      retval = doCmd(strArgument, workCommand);
    }
  }

  private void sleep(int seconds) {
    try {
      Thread.sleep(seconds * 1000L);
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void setArgumentToFiscalCommand() {
    if (fiscalCommands.containsKey(strArgument.toLowerCase())) {
      strArgument = fiscalCommands.get(strArgument);
    }
  }

  private void checkNoArgument() {
    if (strArgument.length() == 0) {
      throw new RuntimeException("no argument set to Ctrlcmd");
    }
  }

  private boolean isCommandWithParameter(int workCommand) {
    return (workCommand == BATCHSTART) || (workCommand == RECSTART) || (workCommand == FISCALCOMMAND);
  }

  private int checkCommandParameter() {

    int workCommand = 0;

    if (strCommand.toLowerCase().equals("blocklogin")) {
      workCommand = BLOCKLOGIN;
    }
    else if (strCommand.toLowerCase().equals("unblocklogin")) {
      workCommand = UNBLOCKLOGIN;
    }
    else if (strCommand.toLowerCase().equals("forcelogout")) {
      workCommand = FORCELOGOUT;
    }
    else if (strCommand.toLowerCase().equals("blockkeyboard")) {
      workCommand = BLOCKKEYBOARD;
    }
    else if (strCommand.toLowerCase().equals("unblockkeyboard")) {
      workCommand = UNBLOCKKEYBOARD;
    }
    else if (strCommand.toLowerCase().equals("monitoron")) {
      workCommand = MONITORON;
    }
    else if (strCommand.toLowerCase().equals("monitoroff")) {
      workCommand = MONITOROFF;
    }
    else if (strCommand.toLowerCase().equals("recstart")) {
      workCommand = RECSTART;
    }
    else if (strCommand.toLowerCase().equals("batchstart")) {
      workCommand = BATCHSTART;
    }
    else if (strCommand.toLowerCase().equals("resetdata")) {
      workCommand = RESETDATA;
    }
    else if (strCommand.toLowerCase().equals("closefiles")) {
      workCommand = CLOSEFILES;
    }
    else if (strCommand.toLowerCase().equals("openfiles")) {
      workCommand = OPENFILES;
    }
    else if (strCommand.toLowerCase().equals("fiscalcommand")) {
      workCommand = FISCALCOMMAND;
    }

    return workCommand;
  }

  private int doCmd(int message) {
    if (cc.sendCmd((byte) message, objRuntimeEnv.getObjActKasse().getsLastSecOfIPAdr(), null, 0)) {
      return 1;
    }
    return 0;
  }

  private int doCmd(String arg, int message) {
    if (cc.sendCmd((byte) message, objRuntimeEnv.getObjActKasse().getsLastSecOfIPAdr(), arg.getBytes(),
        arg.length())) {
      return 1;
    }
    return 0;
  }

  private void createControllCall(String boAddr) {
    try {
      cc = new ControlCall(boAddr);
    }
    catch (UnknownHostException uhe) {
      uhe.printStackTrace();
    }
    catch (SocketException se) {
      se.printStackTrace();
    }
  }
}

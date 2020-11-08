package simulator.commands.impl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simulator.beans.RuntimeEnv;

import simulator.commands.ACommand;
import simulator.interfaces.BackofficeAccess;

@BackofficeAccess(access=true)
public class ShellCommandLX
    extends ACommand {

  /** The logger. */
  private static final Logger logger = LoggerFactory.getLogger(ShellCommandLX.class);

  private static final String CONST_PARAM_CMD_TYPE = "shell_cmd_type";
  private static final String CONST_PARAM_DYNAMIC = "dynamic";

  private static final String SHELL_TYPE_SCRIPT = "shell_type_script";
  private static final String SHELL_TYPE_COMMAND = "shell_type_command";

  String strCmdType = null;
  String strDynamic = null;

  public ShellCommandLX(RuntimeEnv env) {
    objRuntimeEnv = env;
  }

  @Override
  public boolean doWork()
      throws Exception {

    logger.debug("WaitCommand->doWork()");

    // Parameter lesen
    strCmdType = getValueForKey(CONST_PARAM_CMD_TYPE);
    strDynamic = getValueForKey(CONST_PARAM_DYNAMIC);


    // TODO Logik implementieren
    return true;
  }

  @Override
  public boolean doCheck()
      throws Exception {
    // TODO Logik implementieren
    return true;
  }

}

package simulator.commands.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simulator.beans.RuntimeEnv;

public class WaitCommand
    extends simulator.commands.ACommand {

  /** The logger. */
  private static final Logger logger = LoggerFactory.getLogger(WaitCommand.class);

  private static final String CONST_PARAM_TIMEOUT = "timeout";

  String strTimeout;

  public WaitCommand(RuntimeEnv env) {
    objRuntimeEnv = env;
  }

  /**
   * Arbeits-Schnittstelle fuer das Command 'WaitCommand'<br />
   * Dieses Command wartet<br />
   * *
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

    logger.debug("WaitCommand->doWork()");

    // Parameter lesen
    strTimeout = getValueForKey(CONST_PARAM_TIMEOUT);

    Thread.sleep(Integer.valueOf(strTimeout) * 1000);

    return true;
  }

  @Override
  public boolean doCheck() {

    return true;
  }
}

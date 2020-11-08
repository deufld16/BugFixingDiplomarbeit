package simulator.commands.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallGlobalCommand
    extends simulator.commands.ACommand {

  /** The logger. */
  private static final Logger logger = LoggerFactory.getLogger(CallGlobalCommand.class);

  /**
   * Arbeits-Schnittstelle fuer das Command 'CallGlobalCommand'<br />
   * Das CallGlobalCommand ruft globale Commands auf
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
      
    // TODO Auto-generated method stub
    return true;
  }

  @Override
  public boolean doCheck()
      throws Exception {
    // TODO Auto-generated method stub
    return true;
  }

}

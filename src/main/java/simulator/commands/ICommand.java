package simulator.commands;

/**
 * base interface for all commands
 * 
 * @author dmaier
 *
 */

public interface ICommand {

  boolean doWork()
      throws Exception;

  boolean doCheck()
      throws Exception;

}

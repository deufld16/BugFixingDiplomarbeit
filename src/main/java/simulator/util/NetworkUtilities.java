package simulator.util;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkUtilities {

  /** The logger. */
  private static final Logger logger = LoggerFactory.getLogger(NetworkUtilities.class);

  public boolean isReachable(String ip, int port)
      throws IOException {
    try {
      Socket sock = new Socket(ip, port);
      sock.close();
      return true;
    }
    catch (Exception e) {
      return false;
    }

  }

}

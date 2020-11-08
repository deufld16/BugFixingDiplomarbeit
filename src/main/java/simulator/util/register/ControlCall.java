package simulator.util.register;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControlCall {

  /** The logger. */
  private static final Logger logger = LoggerFactory.getLogger(ControlCall.class);

  // Befehle
  public final static byte WRT_STAT = 1;

  // Implementation
  private final byte ACK = 5;
  @SuppressWarnings("unused")
  private final byte NAK = 6;

  private final short CTRL_SERVERPORT = 2720;
  private DatagramSocket sock;
  private InetAddress ctrladdr;

  public ControlCall(String boIP)
      throws SocketException, UnknownHostException {
    try {
      ctrladdr = InetAddress.getByName(boIP);
    }
    catch (Exception e) {
      ctrladdr = InetAddress.getLocalHost();
    }
    sock = new DatagramSocket();
  }

  public ControlCall(InetAddress ctrladdr)
      throws SocketException {
    this.ctrladdr = ctrladdr;
    sock = new DatagramSocket();
  }

  public void sendStatus(byte message, short kasse, byte[] data, int len) {
    byte buff[] = new byte[len + 3];
    buff[2] = message;
    buff[0] = (byte) (kasse & 255);
    buff[1] = (byte) (kasse >> 8);

    // logger.debug(this.getClass().getSimpleName() + " sending udpPacket length: "+ len+3
    // +", "+this.ctrladdr+":" + CTRL_SERVERPORT);

    for (int i = 0; i < len; i++ ) {
      buff[i + 3] = data[i];
    }
    logger.debug("ctrladdr: " + ctrladdr + ", ctrl_serverport: " + CTRL_SERVERPORT);
    DatagramPacket dp = new DatagramPacket(buff, len + 3, ctrladdr, CTRL_SERVERPORT);
    try {
      sock.send(dp);
    }
    catch (IOException ex) {
      logger.warn(this.getClass().getSimpleName() + " problem sending udp packet ", ex);
    }
  }

  public boolean sendCmd(byte message, short kasse, byte[] data, int len) {
    sendStatus(message, kasse, data, len);

    byte buff[] = new byte[1];
    DatagramPacket rp = new DatagramPacket(buff, 1);
    try {
      sock.setSoTimeout(5000);
      sock.receive(rp);
    }
    catch (IOException ex) {
      return false;
    }
    if (buff[0] == ACK) {
      return true;
    }
    return false;
  }

  public String sendQuery(byte message, short kasse, byte[] data, int len) {
    byte buff[] = new byte[1500];
    DatagramPacket rp = new DatagramPacket(buff, 1500);
    try {
      sock.setSoTimeout(5000);
      sock.receive(rp);
    }
    catch (IOException ex) {
      return null;
    }
    String s = new String(buff, 0, rp.getLength());
    return s;
  }

}

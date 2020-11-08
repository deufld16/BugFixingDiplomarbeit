package simulator.commands;

import java.util.HashMap;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import simulator.beans.RuntimeEnv;

public abstract class ACommand
    implements simulator.commands.ICommand {

  protected static final int CONST_FTP_PORT = 21;

  public RuntimeEnv objRuntimeEnv = null;
  
  private HashMap<String, String> mapParams = new HashMap<String, String>();

  public HashMap<String, String> getMapParams() {
    return mapParams;
  }

    public RuntimeEnv getObjRuntimeEnv() {
        return objRuntimeEnv;
    }
  

  public void setNodeParams(NodeList objNodeParams) {
    mapParams.clear();
    if ((objNodeParams != null) && (objNodeParams.getLength() > 0)) {
      for (int iLoop = 0; iLoop < objNodeParams.getLength(); iLoop++ ) {
        Node objNode = objNodeParams.item(iLoop);

        // System.out.println(objNode.toString());

        if (objNode.getNodeType() == Node.ELEMENT_NODE) {
          String strNodeName = objNode.getNodeName();

          String strNodeValue = objNode.getFirstChild().getTextContent();

          mapParams.put(strNodeName, strNodeValue);

          // System.out.println("strNodeName:" + strNodeName);
          // System.out.println("strNodeValue:" + strNodeValue);
        }
      }
    }

   // System.out.println("mapParams:" + mapParams.toString());
  }

  protected String getValueForKey(String strKey) {
    if (mapParams.containsKey(strKey)) {
      return mapParams.get(strKey);

    }

    return null;

  }

}

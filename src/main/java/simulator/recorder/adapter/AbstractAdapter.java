package simulator.recorder.adapter;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;

/**
 * abstract class for the Adapter Classes
 */
public abstract class AbstractAdapter {

  /**
   * Liste der Attribute eines RecorderCommands (AbstractAdapter)
   */
  public NamedNodeMap attributes;

  /**
   * checks if the specified attribute is in the attribute list and delivers false if not
   * @param list of attributes
   * @param attrName
   * @return
   */
  protected boolean containsAttr(NamedNodeMap attributes, String attributeName) {

    int numAttrs = attributes.getLength();
    for (int i = 0; i < numAttrs; i++ ) {
      Attr attr = (Attr) attributes.item(i);
      String attrName = attr.getNodeName();

      if (attrName.equals(attributeName)) {
        return true;
      }
    }
    return false;
  }

}

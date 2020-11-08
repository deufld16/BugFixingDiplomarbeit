package simulator.main;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import simulator.commands.impl.ExcecuteRecorderFileCommand;
import simulator.beans.Kasse;
import simulator.beans.RuntimeEnv;
import simulator.beans.Testcase;
import simulator.beans.Testgroup;

public class DTFCommands {

  /**
   * Spielt ein Recorder File an der Kasse ab
   * 
   * @param strErgebnisPath: Pfad in dem die Ergebnisse gespeichert werden sollen
   * @param strRecorderPath: Absoluter Pfad des Recorders
   */
  public void excecuteSingleRecorderFile(RuntimeEnv env, String strRecorderPath) {

    ExcecuteRecorderFileCommand recCommander = new ExcecuteRecorderFileCommand(env,0,Paths.get(""),0,LocalDateTime.now());

    recCommander.excecuteRecorderFile(strRecorderPath);
  }

  /**
   * Spielt das run.xml einer Testgruppe ab
   * 
   * @param strErgebnisPath: Pfad in dem die Ergebnisse gespeichert werden sollen
   * @param strRecorderPath: Absoluter Pfad des Recorders
   */
  public void excecuteRunXML(RuntimeEnv env, String strRunXMLPath) {

    ExcecuteRecorderFileCommand recCommander = new ExcecuteRecorderFileCommand(env,0,Paths.get(""),0,LocalDateTime.now());

    File strXmlFile = new File(strRunXMLPath);

    if (strXmlFile.exists()) {

      DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder;
      Document doc = null;
      try {
        docBuilder = docFactory.newDocumentBuilder();
        doc = docBuilder.parse(strXmlFile);
        doc.getDocumentElement().normalize();
      }
      catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      NodeList objNodeListCommand = doc.getElementsByTagName("command");
      System.out.println("============================");
      System.out.println("Size: " + objNodeListCommand.getLength());
      for (int iLoopCommands = 0; iLoopCommands < objNodeListCommand.getLength(); iLoopCommands++ ) {
        Node objNodeCommand = objNodeListCommand.item(iLoopCommands);

        NamedNodeMap objAttr = objNodeCommand.getAttributes();
        System.out.println("objAttr.getLength():" + objAttr.getLength());
        if (objAttr != null) {
          if (objAttr.getLength() > 0) {
            String strAttrClass = objAttr.getNamedItem("class").getNodeValue();
            // System.out.println("strAttrClass:" + strAttrClass);

            if (strAttrClass.equals("ExecuteRecorderFileCommand")) {
              if (objNodeCommand.getNodeType() == Node.ELEMENT_NODE) {

                if (objNodeCommand.hasChildNodes()) {
                  NodeList objNodeListParams = objNodeCommand.getChildNodes();

                  recCommander.setNodeParams(objNodeListParams);

                  try {
                    System.out.println("doWork für Command: " + ExcecuteRecorderFileCommand.class.getName());
                    boolean bDoWork = recCommander.doWork();
                    // System.out.println("Return doWork für Command: " + bDoWork);

                    System.out.println("doCheck für Command: " + ExcecuteRecorderFileCommand.class.getName());
                    boolean bDoCheck = recCommander.doCheck();
                    // System.out.println("Return doCheck für Command: " + bDoCheck);
                  }
                  catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  }
                }
              }
            }
          }
        }
      }
    }
    else {
      System.out.println("excecuteRunXML(): run.xml konnte nicht gefunden werden. \n Pfad: " + strRunXMLPath);
    }
    recCommander.getDtfCon().closeConnection();

  }

  public static void main(String[] args) {
/*
    DTFCommands objdtfCommands = new DTFCommands();

    // ------------------Definition------------------
    Kasse kasse1 = new Kasse(1, 96, "10.0.0.1");

    String strErgebnisPath = "C:/Users/A17226C/Documents/Work/DTF Runtime anpassen/Ergebnisse/";
    String strTestCasePath =
        "C:/Develop/packages/dtf/dtf-project-suite/trunk/__HTL_Vorbereitung/__NEUE_STRUKTUR__/projekte/basisregressionstest/run/001_TG/001_TC";
    Testcase objTestCase = new Testcase();
    objTestCase.setStrActErgPath(strErgebnisPath);
    objTestCase.setStrTestCasePath(strTestCasePath);

    Testgroup objTestGrp = new Testgroup("101010", "1234", 10);
    objTestGrp.setObjActTC(objTestCase);

    RuntimeEnv env = new RuntimeEnv();
    env.setObjActKasse(kasse1);
    env.setObjActTG(objTestGrp);

    // ----------------Einzelnes Recorder File abspielen----------------
    String strRecorderPath = "C:/Users/A17226C/Documents/Work/DTF Runtime anpassen/TestFiles/rec2_1.xml";
    objdtfCommands.excecuteSingleRecorderFile(env, strRecorderPath);

    // ----------------Run XML einer Testgruppe abspielen----------------

    // String strRunXMLPath = strTestCasePath + "/run.xml";
    // objdtfCommands.excecuteRunXML(env, strRunXMLPath);

    // -----------------------------DEBUG-------------------------------
    // ExcecuteRecorderFileCommand recCommander = new ExcecuteRecorderFileCommand(env);
    //
    // String strPath1 = "C:/Users/A17226C/Documents/Work/DTF Runtime anpassen/TestFiles/rec2_1.xml";
    // String strPath2 = "C:/Users/A17226C/Documents/Work/DTF Runtime anpassen/TestFiles/test_rec2.xml";
    // String strPath3 = "C:/Users/A17226C/Documents/Work/DTF Runtime anpassen/TestFiles/test_rec3.xml";
    //
    // long lstart = System.currentTimeMillis();
    // recCommander.excecuteRecorderFile(strPath1);
    // recCommander.excecuteRecorderFile(strPath2);
    // recCommander.excecuteRecorderFile(strPath3);
    //
    // long lfinish = System.currentTimeMillis();
    // long ltime = lfinish - lstart;
    //
    // String strResult = String.format("Zeit: %d min, %d sec", TimeUnit.MILLISECONDS.toMinutes(ltime),
    // TimeUnit.MILLISECONDS.toSeconds(ltime)
    // - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ltime)));
    //
    // System.out.println("\n" + strResult);

    // ----------------------Lade Schließen---------------------------
    //
    // DTFConnection dtfCon = new DTFConnection(env);
    // dtfCon.connectToCashDesk();
    // dtfCon.sendDTFXmlCommand("<drawer><pos>0</pos></drawer>");
    // dtfCon.closeConnection();
    // ----------------------------------------------------
*/
  }

}

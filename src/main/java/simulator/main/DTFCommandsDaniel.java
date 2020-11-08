package simulator.main;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import simulator.commands.ACommand;
import simulator.commands.impl.CheckRepInZAMTlogCommand;
import simulator.commands.impl.ChkSyncCommandV2;
import simulator.commands.impl.CopyCommand;
import simulator.commands.impl.CtrlCommandV2;
import simulator.commands.impl.EljoCommandV2;
import simulator.commands.impl.ExportTableContentCommand;
import simulator.commands.impl.FTPCommand;
import simulator.commands.impl.SqlQueryCommand;
import simulator.commands.impl.WaitCommand;
import simulator.beans.Kasse;
import simulator.beans.RuntimeEnv;
import simulator.beans.Testcase;
import simulator.beans.Testgroup;

public class DTFCommandsDaniel {

  public static void main(String[] args) {

    String strCommand = null;
    String strCommandFile = null;

    // --------------------------------------------------------------------------------
    // strCommand = "CheckRepInZAMTlogCommand";
    // strCommandFile =
    // "C:\Develop\dstore_Testframework\dtf-project-suite\trunk\__HTL_Vorbereitung\Testgruppen\\TG01_Grundlegende
    // Funktionalitaet\\teststep\\bedienerbericht1.xml";
    // call_Command(strCommand, strCommandFile);

    // --------------------------------------------------------------------------------
    // strCommand = "ChkSyncCommandV2";
    // strCommandFile =
    // "C:\Develop\dstore_Testframework\dtf-project-suite\trunk\__HTL_Vorbereitung\Testgruppen\\TG01_Grundlegende
    // Funktionalitaet\\__global__\\teststep\\sync_n_reload_new.xml";
    // call_Command(strCommand, strCommandFile);

    // --------------------------------------------------------------------------------
    // strCommand = "CopyCommand";
    // strCommandFile =
    // "C:\Develop\dstore_Testframework\dtf-project-suite\trunk\__HTL_Vorbereitung\Testgruppen\\CopyCommand.xml";
    // call_Command(strCommand, strCommandFile);

    // --------------------------------------------------------------------------------
    // strCommand = "ExportTableContentCommand";
    // strCommandFile =
    // "C:\Develop\dstore_Testframework\dtf-project-suite\trunk\__HTL_Vorbereitung\Testgruppen\\TG02_Artikelverkauf\\teststep\\compare_result_zam_bo.xml";
    // call_Command(strCommand, strCommandFile);

    // // --------------------------------------------------------------------------------
    // strCommand = "WaitCommand";
    // strCommandFile =
    // "C:\Develop\dstore_Testframework\dtf-project-suite\trunk\__HTL_Vorbereitung\Testgruppen\\TG01_Grundlegende
    // Funktionalitaet\\__global__\\teststep\\sync_n_reload_new.xml";
    // call_Command(strCommand, strCommandFile);

    // // --------------------------------------------------------------------------------
    // strCommand = "EljoCommandV2";
    // strCommandFile =
    // "C:\\Develop\\dstore_Testframework\\dtf-project-suite\\trunk\\__HTL_Vorbereitung\\Testgruppen\\TG02_Artikelverkauf\\teststep\\create_eljo_bons_2_1.xml";
    // call_Command(strCommand, strCommandFile);

    // // --------------------------------------------------------------------------------
    // strCommand = "SqlQueryCommand";
    // strCommandFile =
    // "C:\Develop\dstore_Testframework\dtf-project-suite\trunk\__HTL_Vorbereitung\Testgruppen\\SqlQueryCommand.xml";
    // call_Command(strCommand, strCommandFile);

    // // --------------------------------------------------------------------------------
    // strCommand = "FTPCommand";
    // strCommandFile =
    // "C:\Develop\dstore_Testframework\dtf-project-suite\trunk\__HTL_Vorbereitung\Testgruppen\\FTPCommand.xml";
    // call_Command(strCommand, strCommandFile);

    // --------------------------------------------------------------------------------
    strCommand = "CtrlCommandV2";
    strCommandFile =
        "C:\\Develop\\dstore_Testframework\\dtf-project-suite\\trunk\\__HTL_Vorbereitung\\Testgruppen\\TG01_Grundlegende Funktionalitaet\\__global__\\teststep\\sync_n_reload_new.xml";
    call_Command(strCommand, strCommandFile);

  }

  private static void call_Command(String strCommand, String strCommandFile) {

    // ------------------- Test-Objecte setzen -------------------
/*    RuntimeEnv objRuntimeEnv = new RuntimeEnv();
    Testgroup objTestGrp = new Testgroup("200634", "1234", 500);
    Testcase objTestC = new Testcase();
    Kasse objKasse = new Kasse(1, 96, "10.0.0.2");

    objTestC.setStrTestCasePath(
        "C:\\Develop\\dstore_Testframework\\dtf-project-suite\\trunk\\__HTL_Vorbereitung\\CommandTest\\act_tc_path");
    objTestC.setStrActErgPath(
        "C:\\Develop\\dstore_Testframework\\dtf-project-suite\\trunk\\__HTL_Vorbereitung\\CommandTest\\act_erg_path");

    objTestGrp.setObjActTC(objTestC);
    objRuntimeEnv.setObjActTG(objTestGrp);
    objRuntimeEnv.setStrServerAddr("10.0.0.32");
    objRuntimeEnv.setiRegCount(3);
    objRuntimeEnv.setObjActKasse(objKasse);

    ACommand objCommand = null;

    if (strCommand.equals("CheckRepInZAMTlogCommand")) {
      objCommand = new CheckRepInZAMTlogCommand(objRuntimeEnv);
    }
    else if (strCommand.equals("ChkSyncCommandV2")) {
      objCommand = new ChkSyncCommandV2(objRuntimeEnv);
    }
    else if (strCommand.equals("CopyCommand")) {
      objCommand = new CopyCommand(objRuntimeEnv);
    }
    else if (strCommand.equals("ExportTableContentCommand")) {
      objCommand = new ExportTableContentCommand(objRuntimeEnv);
    }
    else if (strCommand.equals("WaitCommand")) {
      objCommand = new WaitCommand(objRuntimeEnv);
    }
    else if (strCommand.equals("EljoCommandV2")) {
      objCommand = new EljoCommandV2(objRuntimeEnv);
    }
    else if (strCommand.equals("SqlQueryCommand")) {
      objCommand = new SqlQueryCommand(objRuntimeEnv);
    }
    else if (strCommand.equals("FTPCommand")) {
      objCommand = new FTPCommand(objRuntimeEnv);
    }
    else if (strCommand.equals("CtrlCommandV2")) {
      objCommand = new CtrlCommandV2(objRuntimeEnv);
    }

    File strXmlFile = new File(strCommandFile);

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

      for (int iLoopCommands = 0; iLoopCommands < objNodeListCommand.getLength(); iLoopCommands++ ) {
        Node objNodeCommand = objNodeListCommand.item(iLoopCommands);

        NamedNodeMap objAttr = objNodeCommand.getAttributes();
        System.out.println("objAttr.getLength():" + objAttr.getLength());
        if (objAttr != null) {
          if (objAttr.getLength() > 0) {
            String strAttrClass = objAttr.getNamedItem("class").getNodeValue();
            System.out.println("strAttrClass:" + strAttrClass);

            if (strAttrClass.equals(strCommand)) {
              if (objNodeCommand.getNodeType() == Node.ELEMENT_NODE) {

                if (objNodeCommand.hasChildNodes()) {
                  NodeList objNodeListParams = objNodeCommand.getChildNodes();

                  objCommand.setNodeParams(objNodeListParams);

                  try {
                    System.out.println("++++ doWork für Command: " + strCommand);
                    boolean bDoWork = objCommand.doWork();
                    System.out.println("++++ Return doWork für Command: " + bDoWork);

                    System.out.println("++++ doCheck für Command: " + strCommand);
                    boolean bDoCheck = objCommand.doCheck();
                    System.out.println("++++ Return doCheck für Command: " + bDoCheck);
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
  }
*/

  // private static void call_ChkSyncCommandV2() {
  //
  // RuntimeEnv objRuntimeEnv = new RuntimeEnv();
  // Testgroup objTestGrp = new Testgroup("200634", "1234", 500);
  // Testcase objTestC = new Testcase();
  //
  // objTestGrp.setObjActTC(objTestC);
  // objRuntimeEnv.setObjActTG(objTestGrp);
  // objRuntimeEnv.setStrServerAddr("10.93.31.64");
  // objRuntimeEnv.setiRegCount(3);
  // ChkSyncCommandV2 objChkSyncCmdV2 = new ChkSyncCommandV2(objRuntimeEnv);
  //
  // File strXmlFile = new File(
  // "C:\\Develop\\dstore_Testframework\\dtf-ide\\trunk\\Dokument_fuer_HTL\\__HTL_Vorbereitung\\TG01_Grundlegende
  // Funktionalitaet\\__global__\\teststep\\sync_n_reload_new.xml");
  //
  // if (strXmlFile.exists()) {
  //
  // DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
  // DocumentBuilder docBuilder;
  // Document doc = null;
  // try {
  // docBuilder = docFactory.newDocumentBuilder();
  // doc = docBuilder.parse(strXmlFile);
  // doc.getDocumentElement().normalize();
  // }
  // catch (Exception e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  //
  // NodeList objNodeListCommand = doc.getElementsByTagName("command");
  // System.out.println("============================");
  //
  // for (int iLoopCommands = 0; iLoopCommands < objNodeListCommand.getLength(); iLoopCommands++ ) {
  // Node objNodeCommand = objNodeListCommand.item(iLoopCommands);
  //
  // NamedNodeMap objAttr = objNodeCommand.getAttributes();
  // System.out.println("objAttr.getLength():" + objAttr.getLength());
  // if (objAttr != null) {
  // if (objAttr.getLength() > 0) {
  // String strAttrClass = objAttr.getNamedItem("class").getNodeValue();
  // System.out.println("strAttrClass:" + strAttrClass);
  //
  // if (strAttrClass.equals("ChkSyncCommandV2")) {
  // if (objNodeCommand.getNodeType() == Node.ELEMENT_NODE) {
  //
  // if (objNodeCommand.hasChildNodes()) {
  // NodeList objNodeListParams = objNodeCommand.getChildNodes();
  //
  // objChkSyncCmdV2.setNodeParams(objNodeListParams);
  //
  // try {
  // System.out.println("++++ doWork für Command: " + ChkSyncCommandV2.class.getName());
  // boolean bDoWork = objChkSyncCmdV2.doWork();
  // System.out.println("++++ Return doWork für Command: " + bDoWork);
  //
  // System.out.println("++++ doCheck für Command: " + ChkSyncCommandV2.class.getName());
  // boolean bDoCheck = objChkSyncCmdV2.doCheck();
  // System.out.println("++++ Return doCheck für Command: " + bDoCheck);
  // }
  // catch (Exception e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // }
  // }
  // }
  // }
  // }
  // }
  // }
  // }

  // private static void call_CheckRepInZAMTlogCommand() {
  //
  // RuntimeEnv objRuntimeEnv = new RuntimeEnv();
  // Testgroup objTestGrp = new Testgroup("200634", "1234", 500);
  // Testcase objTestC = new Testcase();
  //
  // objTestGrp.setObjActTC(objTestC);
  // objRuntimeEnv.setObjActTG(objTestGrp);
  // objRuntimeEnv.setStrServerAddr("10.93.31.64");
  // CheckRepInZAMTlogCommand objReps = new CheckRepInZAMTlogCommand(objRuntimeEnv);
  //
  // File strXmlFile = new File(
  // "C:\\Develop\\dstore_Testframework\\dtf-ide\\trunk\\Dokument_fuer_HTL\\__HTL_Vorbereitung\\TG01_Grundlegende
  // Funktionalitaet\\teststep\\bedienerbericht1.xml");
  //
  // if (strXmlFile.exists()) {
  //
  // DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
  // DocumentBuilder docBuilder;
  // Document doc = null;
  // try {
  // docBuilder = docFactory.newDocumentBuilder();
  // doc = docBuilder.parse(strXmlFile);
  // doc.getDocumentElement().normalize();
  // }
  // catch (Exception e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  //
  // NodeList objNodeListCommand = doc.getElementsByTagName("command");
  // System.out.println("============================");
  //
  // for (int iLoopCommands = 0; iLoopCommands < objNodeListCommand.getLength(); iLoopCommands++ ) {
  // Node objNodeCommand = objNodeListCommand.item(iLoopCommands);
  //
  // NamedNodeMap objAttr = objNodeCommand.getAttributes();
  // System.out.println("objAttr.getLength():" + objAttr.getLength());
  // if (objAttr != null) {
  // if (objAttr.getLength() > 0) {
  // String strAttrClass = objAttr.getNamedItem("class").getNodeValue();
  // System.out.println("strAttrClass:" + strAttrClass);
  //
  // if (strAttrClass.equals("CheckRepInZAMTlogCommand")) {
  // if (objNodeCommand.getNodeType() == Node.ELEMENT_NODE) {
  //
  // if (objNodeCommand.hasChildNodes()) {
  // NodeList objNodeListParams = objNodeCommand.getChildNodes();
  //
  // objReps.setNodeParams(objNodeListParams);
  //
  // try {
  // System.out
  // .println("++++ doWork für Command: " + CheckRepInZAMTlogCommand.class.getName());
  // boolean bDoWork = objReps.doWork();
  // System.out.println("++++ Return doWork für Command: " + bDoWork);
  //
  // System.out
  // .println("++++ doCheck für Command: " + CheckRepInZAMTlogCommand.class.getName());
  // boolean bDoCheck = objReps.doCheck();
  // System.out.println("++++ Return doCheck für Command: " + bDoCheck);
  // }
  // catch (Exception e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // }
  // }
  // }
  // }
  // }
  // }
  // }
  // }
}}

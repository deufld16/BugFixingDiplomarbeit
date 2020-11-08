package simulator.recorder.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;

/**
 * Klasse schreibt die Antworten der Kasse in die entsprechenden Ergebnis Files
 */
public class RecorderResponseWriter {

  private String strSavePath;
  private String strMarkerName;

  /**
   * 
   * @param strSavePath : Speicher Verzeichnis (String)
   * @param strMarkerName : MarkerName des Recorders (String)
   */
  public RecorderResponseWriter(String strSavePath, String strMarkerName) {
    this.strSavePath = strSavePath;
    this.strMarkerName = strMarkerName;
  }

  public void writeData(LinkedList<String> listResponseLines) {

    BufferedWriter bWriterPrinterResponse = null;
    BufferedWriter bWriterGserverResponse = null;
    BufferedWriter bWriterDrawerResponse = null;
    BufferedWriter bWriterDisplayResponse = null;
    System.out.println("---------------------->" + strSavePath  + File.separator+ "printer_" + strMarkerName + ".txt");
    try {

      bWriterPrinterResponse =
          new BufferedWriter(new FileWriter(strSavePath  + File.separator+ "printer_" + strMarkerName + ".txt"));
      bWriterPrinterResponse.write("");// Inhalt löschen

      bWriterGserverResponse =
          new BufferedWriter(new FileWriter(strSavePath + File.separator+ "gserver_" + strMarkerName + ".txt"));
      bWriterGserverResponse.write("");// Inhalt löschen

      bWriterDrawerResponse =
          new BufferedWriter(new FileWriter(strSavePath + File.separator+ "drawer_" + strMarkerName + ".txt"));
      bWriterDrawerResponse.write("");// Inhalt löschen

      bWriterDisplayResponse =
          new BufferedWriter(new FileWriter(strSavePath + File.separator+ "display_" + strMarkerName + ".txt"));
      bWriterDisplayResponse.write("");// Inhalt löschen

      for (String strLine : listResponseLines) {

        if (strLine.contains("<printer>")) {
          bWriterPrinterResponse.append(strLine);
          bWriterPrinterResponse.newLine();
        }
        else if (strLine.contains("<gserver>")) {
          bWriterGserverResponse.append(strLine);
          bWriterGserverResponse.newLine();
        }
        else if (strLine.contains("<drawer>")) {
          bWriterDrawerResponse.append(strLine);
          bWriterDrawerResponse.newLine();
        }
        else if (strLine.contains("<display>")) {
          bWriterDisplayResponse.append(strLine);
          bWriterDisplayResponse.newLine();
        }
      }

    }
    catch (IOException ex) {
      ex.printStackTrace();
    }
    finally {
      try {
        bWriterPrinterResponse.close();
        bWriterGserverResponse.close();
        bWriterDrawerResponse.close();
        bWriterDisplayResponse.close();
      }
      catch (IOException e) { /* ignored */}

    }

  }
}

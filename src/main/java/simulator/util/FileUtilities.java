package simulator.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtilities {

  /** The logger. */
  private static final Logger logger = LoggerFactory.getLogger(FileUtilities.class);

  /**
   * writes the strings from a given list to a file
   * 
   * @param lines list of strings with the file contents
   * @param filename name of the file
   */
  public static void writeFile(List<String> lines, String filename) {

    writeFile(lines, filename, false);

  }

  public static void writeFile(List<String> lines, String filename, boolean append) {

    // check if folder exists
    File file = new File(filename);
    String folder = file.getParent();
    File fileFolder = new File(folder);
    if (!fileFolder.exists()) {
      fileFolder.mkdirs();
    }

    BufferedWriter bufferedWriter = null;

    try {
      // bufferedWriter = new BufferedWriter(new FileWriter(filename, append));

      bufferedWriter =
          new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, append), "ISO-8859-1"));

      for (String line : lines) {
        bufferedWriter.write(line);
        bufferedWriter.newLine();
      }

    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }
    finally {
      if (bufferedWriter != null) {
        try {
          bufferedWriter.flush();
          bufferedWriter.close();
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

}

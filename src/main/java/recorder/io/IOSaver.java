/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.io;

import recorder.guiOperations.GUIOperations;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Florian
 */
public class IOSaver {

    /***
     * Method that saves all articles for the yellow buttons
     * 
     * @param allButtons
     * @return Erfolg
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static boolean saveArticleConfigFile(List<JButton> allButtons) throws FileNotFoundException, IOException {
        File file = GUIOperations.getArticleConfigFile();
        if (file == null) {
            JFileChooser fch = new JFileChooser();
            int retValue = fch.showDialog(null, "Speicherort für Artikelkonfigurationsdatei auswählen");
            if (retValue == JFileChooser.APPROVE_OPTION) {
                file = fch.getSelectedFile();
                GUIOperations.setArticleConfigFile(file);
            } else {
                JOptionPane.showMessageDialog(null, "Das Beschreiben des Buttons wurde abgebrochen");
                return false;
            }
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.toPath().toString())));
        int cnt = 1;
        for (JButton btn : allButtons) {
            if (btn.getActionCommand() != null) {
                if (!btn.getActionCommand().equalsIgnoreCase("-")) {
                    writer.write("button_" + cnt + "=" + btn.getActionCommand());
                    writer.newLine();
                }
            }
            cnt++;
        }
        writer.close();
        return true;
    }

    /**
     * Method that saves all authorization limits
     * 
     * @param limit
     * @throws IOException 
     */
    public static void saveLimits(List<Double> limit) throws IOException {
        FileWriter fr = new FileWriter(Paths.get(GUIOperations.getResPath().toString(), "limits.conf").toFile());
        BufferedWriter bw = new BufferedWriter(fr);
        int cnt = 0;
        for (Double d : limit) {
            bw.write(cnt + "=" + d.toString());
            cnt++;
            bw.newLine();
        }
        bw.close();

    }
    
    /**
     * Method that saves all existing simulation file types
     * 
     * @param descriptions
     * @param extensions
     * @throws IOException 
     */
    public static void saveExtensions(List<String> descriptions, List<String> extensions) throws IOException {
        FileWriter fr = new FileWriter(Paths.get(GUIOperations.getResPath().toString(), "simulationtyp.conf").toFile());
        BufferedWriter bw = new BufferedWriter(fr);
        for (int i = 0; i < extensions.size(); i++) {
            bw.write(extensions.get(i) + "=" + descriptions.get(i));
            bw.newLine();
        }
        bw.close();
    }
}

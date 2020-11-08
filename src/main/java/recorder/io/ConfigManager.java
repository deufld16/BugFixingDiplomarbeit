/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author Maxi
 */
public class ConfigManager {

    /**
     * Method to load the article configuration (yellow buttons)
     *
     * @param path Pfad zur Artikel-Konfiguration
     * @return eingelesene Artikel-Konfiguration
     */
    public static Map<String, String> loadFile(Path path) {
        Map<String, String> eanMap = new HashMap();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path.toString())));
            String zeile = "";
            while ((zeile = reader.readLine()) != null) {
                zeile = zeile.trim();
                if (!zeile.contains("#")) {
                    try {
                        eanMap.put(zeile.split("\\=")[0], zeile.split("\\=")[1]);
                    } catch (IndexOutOfBoundsException e) {
                        if (zeile.indexOf("=") == zeile.length() - 1) {
                            eanMap.put(zeile.split("\\=")[0], "-");
                        } else {
                            JOptionPane.showMessageDialog(null, "Bitte eine valide Konifgurationsdatei auswählen");
                            break;
                        }
                    }
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return eanMap;
    }
}

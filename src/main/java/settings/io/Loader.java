/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package settings.io;

import general.bl.GlobalParamter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Florian Deutschmann
 */
public class Loader {

    /**
     * Method which is used to update a parameter in the parameter.conf file. This method needs an identifier and the new path
     * to insert the new path at the right place in the config file.
     * @param path
     * @param identifier
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static void changeParameters(Path path, String identifier) throws FileNotFoundException, IOException {
        Path pathToSave_Read = Paths.get(GlobalParamter.getInstance().getSettingsResPath().toString(), "parameter.conf");

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pathToSave_Read.toString())));

        String newLine = "";
        List<String> linesToWrite = new LinkedList<>();
        while ((newLine = reader.readLine()) != null) {
            if (newLine.split("=")[0].equals(identifier)) {
                linesToWrite.add(identifier + "=" + path);
            } else {
                linesToWrite.add(newLine);
            }
        }
        reader.close();

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathToSave_Read.toString())));

        for (String line : linesToWrite) {
            writer.write(line);
            writer.newLine();
        }

        writer.close();
    }
    /**
     * Method which is used to set the parameter.conf file to its default state. This method is being used when no parameter.conf file has 
     * been deleted or if it is the first start of the program.
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static void resetToDefaultConfiguration() throws FileNotFoundException, IOException {
        Path pathToSave_Read = Paths.get(GlobalParamter.getInstance().getSettingsResPath().toString(), "parameter.conf");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pathToSave_Read.toString())));

        for (String line : GlobalParamter.getInstance().getDefaultParameterConfiguration()) {
            writer.write(line);
            writer.newLine();
        }

        writer.close();
    }
    /**
     * Method which is being used to return a Map which contains all the configured parameters. An identifier can be used to retrieve a specific
     * parameter from this map.
     * @param changed
     * @return
     * @throws IOException 
     */
    public static Map<String, String> getSpecificParameter(boolean changed) throws IOException {
        if (GlobalParamter.getInstance().getParameter() == null || changed) {
            Path pathToSave_Read = Paths.get(GlobalParamter.getInstance().getSettingsResPath().toString(), "parameter.conf");
            Map<String, String> parameterMap = new HashMap<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(pathToSave_Read.toString())));

            String newLine = "";
            List<String> linesToWrite = new LinkedList<>();

            while ((newLine = reader.readLine()) != null) {
                switch (newLine.split("=")[0]) {
                    case "recorderPath":
                        parameterMap.put("recorderPath", newLine.split("=")[1]);
                        break;
                    case "dynamicFunctions":
                        parameterMap.put("dynamicFunc", newLine.split("=")[1]);
                        break;
                    case "articleConfig":
                        parameterMap.put("artConfig", newLine.split("=")[1]);
                        break;
                    case "projectPath":
                        parameterMap.put("projectPath", newLine.split("=")[1]);
                        break;
                    case "globalPath":
                        parameterMap.put("globalPath", newLine.split("=")[1]);
                        break;
                }
            }
            reader.close();
            GlobalParamter.getInstance().setParameter(parameterMap);
        }

        return GlobalParamter.getInstance().getParameter();
    }
}

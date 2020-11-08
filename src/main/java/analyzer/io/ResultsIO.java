/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer.io;

import analyzer.beans.TestGroupErg;
import analyzer.enums.ResultFileType;
import general.io.Mapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * This class is to read and write data from/to the disk, 
 * so that the results could be analyed resp. changes could be saved
 * 
 * @author Maximilian Strohmaier
 */
public class ResultsIO 
{
    private static Path ergPath;
    private static Path refPath;
    private static final Path ANALYZER_RES_PATH = Paths.get(
            System.getProperty("user.dir"), "src", "main", "java", "analyzer", "res");
    
    /***
     * Method to read the data returned by the simulation process
     * and needed for the analysis
     * 
     * @return a list of resulting test groups
     * 
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    public static List<TestGroupErg> readSimulationErg() throws ParserConfigurationException, SAXException, IOException
    {
        return Mapper.mapResultFilesToBeans(ergPath, refPath);
    }
    
    /***
     * Method to save a new version of a specific reference file
     * 
     * @param refFilePath  path to locate the reference file
     * @param lines  lines to write in the new reference file
     * 
     * @throws java.io.IOException
     */
    public static void updateReferenceFile(Path refFilePath, List<String> lines) throws IOException
    {
        File refFile = refFilePath.toFile();
        if(!refFile.exists()) {
            File parentFile = refFile.getParentFile();
            if(!parentFile.exists()) {
                parentFile.mkdirs();
            }
            refFile.createNewFile();
        }
        Files.write(refFilePath, lines);
    }
    
    /**
     * Method to initialize the similarity rates by reading it from the
     * corresponding .properties file and storing it to each ResultFileType
     */
    public static void initSimilarityRates() {
        Properties props = ResultsIO.loadProperties("similarity_rates.properties");
        props.stringPropertyNames().forEach(description -> {
            ResultFileType.valueOf(description.toUpperCase()).setSimilarityRate(
                    Double.parseDouble(props.getProperty(description)));
        });
    }
    
    /***
     * Method to read a .properties file from the analyzer-ressource-path 
     * of the project
     * 
     * @param filename  name of the properties-file
     * @return  read Properties
     */
    public static Properties loadProperties(String filename) {
        Properties props = new Properties();
        try {
            FileInputStream fis = new FileInputStream(Paths.get(ANALYZER_RES_PATH.toString(), filename).toFile());
            props.load(fis);
        } catch (IOException ex) {
        }
        return props;
    }
    
    /***
     * Method to write a.properties file to the analyzer-ressource-path 
     * of the project
     * 
     * @param filename  name of the properties-file
     * @param props  Properties which should be saved
     */
    public static void saveProperties(String filename, Properties props) {
        FileOutputStream fos = null;
        try { 
            fos = new FileOutputStream(Paths.get(ANALYZER_RES_PATH.toString(), filename).toFile());
            props.store(fos, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static Path getErgPath() {
        return ergPath;
    }

    public static void setErgPath(Path ergPath) {
        ResultsIO.ergPath = ergPath;
    }

    public static Path getRefPath() {
        return refPath;
    }

    public static void setRefPath(Path refPath) {
        ResultsIO.refPath = refPath;
    }
}

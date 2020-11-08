/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.io;

import analyzer.beans.CashpointErg;
import analyzer.beans.ReferenceFile;
import analyzer.beans.Result;
import analyzer.beans.ResultType;
import analyzer.beans.TestCaseErg;
import analyzer.beans.TestGroupErg;
import analyzer.enums.CashpointGroup;
import analyzer.enums.ResultFileType;
import general.beans.io_objects.CommandRun;
import general.beans.io_objects.ProjectRun;
import general.beans.io_objects.TestCaseRun;
import general.beans.io_objects.TestGroupRun;
import general.bl.GlobalParamter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class that conducts xml-to-Object Mappings and needed IO operations
 *
 * @author Lukas Krobath
 * @author Maximilian Strohmaier
 */
public class Mapper {

    /**
     * This method converts a path to a valid Java Beans representation of the
     * Rewe Systems Testing File System.
     *
     * @param parent : Path to "parent directory" which is a directory full of
     * projects, as specified within Rewe.
     * @return
     * @throws Exception
     */
    public static List<ProjectRun> mapFilesToBeans(Path parent) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        Document doc = null;

        List<ProjectRun> allProjects = new ArrayList();
        // Select all files from parent directory and creating all projects
        try {
            for (File res : getDirectoriesOfDirectory(parent.toFile())) {
                ProjectRun pro = new ProjectRun(res.getName(), res.toPath());
                //Go through each project's run
                for (File tgroup : getRunFilesOfDirectory(new File(res, "run"))) {

                    try {
                        docBuilder = docFactory.newDocumentBuilder();
                        doc = docBuilder.parse(tgroup);
                        doc.getDocumentElement().normalize();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //Set description of project from run.xml
                    try {
                        NodeList description = doc.getElementsByTagName("description");
                        pro.setDescription(description.item(0).getTextContent());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //Handle each testgroup in project
                for (File tgroupDir : getDirectoriesOfDirectory(new File(res, "run"))) {
                    //parse run.xml for every testgroup
                    TestGroupRun tg = null;
                    for (File tgrouprun : getRunFilesOfDirectory(tgroupDir)) {

                        try {
                            docBuilder = docFactory.newDocumentBuilder();
                            doc = docBuilder.parse(tgrouprun);
                            doc.getDocumentElement().normalize();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //Create a new Testgroup object
                        try {
                            NodeList desc = doc.getElementsByTagName("description");
                            NodeList empid = doc.getElementsByTagName("empid");
                            NodeList emppass = doc.getElementsByTagName("emppass");
                            NodeList tllid = doc.getElementsByTagName("tllid");
                            tg = new TestGroupRun(Integer.parseInt(empid.item(0).getTextContent()),
                                    Integer.parseInt(tllid.item(0).getTextContent()),
                                    Integer.parseInt(emppass.item(0).getTextContent()),
                                    new ArrayList<TestCaseRun>(),
                                    desc.item(0).getTextContent(),
                                    tgroupDir.toPath());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    //goind through all testcases
                    for (File tcaseDir : getDirectoriesOfDirectory(tgroupDir)) {
                        TestCaseRun tc;
                        for (File tcaserun : getRunFilesOfDirectory(tcaseDir)) {
                            try {
                                docBuilder = docFactory.newDocumentBuilder();
                                doc = docBuilder.parse(tcaserun);
                                doc.getDocumentElement().normalize();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            //Create a new Testcase object
                            try {
                                NodeList des = doc.getElementsByTagName("description");
                                NodeList commands = doc.getElementsByTagName("command");
                                List<CommandRun> commandList = new ArrayList<CommandRun>();
                                //Loop through all command items
                                for (int i = 0; i < commands.getLength(); i++) {
                                    String cdesc = "";
                                    for (int j = 0; j < commands.item(i).getChildNodes().getLength(); j++) {
                                        if (commands.item(i).getChildNodes().item(j).getNodeName().equals("command_text")) {
                                            cdesc = commands.item(i).getChildNodes().item(j).getTextContent();
                                        }

                                    }
                                    //Create a new command
                                    CommandRun command = new CommandRun(((Element) commands.item(i)).getAttribute("class"),
                                            commands.item(i).getChildNodes(), cdesc, tcaseDir.toPath());
                                    command.setDisplayName(((Element) commands.item(i)).getAttribute("displayname"));
                                    commandList.add(command);
                                }
                                tc = new TestCaseRun(commandList, des.item(0).getTextContent(), tcaseDir.toPath());
                                tg.getTestCases().add(tc);
                            } catch (Exception e) {
                                //e.printStackTrace();
                            }
                        }

                    }
                    pro.getTestgroups().add(tg);
                }
                allProjects.add(pro);
            }
        } catch (NullPointerException n) {
            allProjects = new ArrayList<>();
        }
        return allProjects;
    }

    /**
     * *
     * Method to read all relevant data for the analysis and map it to the
     * according beans objects
     *
     * @param ergPath erg parent path that includes the testgroups and all
     * further explorer layers
     * @param refPath  ref parent path that includes the testgroups and all
     * further explorer layers
     * 
     * @return  list of all testgroups within the erg directory
     * 
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static List<TestGroupErg> mapResultFilesToBeans(Path ergPath, Path refPath) throws ParserConfigurationException, SAXException, IOException {
        List<TestGroupErg> testgroups = new LinkedList<>();
        List<String> invalidTestgroupsInfo = new LinkedList<>();
        int numOfResultFiles = 0;
        boolean noResultsForTg;

        List<File> tgFiles = null;
        try {
            tgFiles = getDirectoriesOfDirectory(ergPath.toFile());
        } catch (NullPointerException ex) {
            return testgroups;
        }
        for (File tgFile : tgFiles) {
            //iterates over all testgroups
            noResultsForTg = true;
            List<TestCaseErg> testcases = new LinkedList<>();
            //boolean noCashpoints = true;
            for (File tcFile : getDirectoriesOfDirectory(tgFile)) {
                //iterates over all testcases
                List<CashpointErg> cashpoints = new LinkedList<>();
                Properties props = null;
                try {
                    props = getCashpointMapping(tcFile.toString()
                            + File.separator + "erg_ref_mapping.properties");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null,
                            "Fehlendes Kassenmapping (erg_ref_mapping.properties)"
                                    + " in \n"+tcFile.toString());
                    return null;
                }

                for (File cashpFile : getDirectoriesOfDirectory(tcFile)) {
                    //iterates over all cashpoints
                    List<Result> results = new LinkedList<>();
                    Map<String, List<ResultType>> resultTypeMapping = new HashMap<>();

                    for (File resultFile : getFilesOfDirectory(cashpFile)) {
                        //iterates over all result files (result types)
                        String fileStr = "";
                        String marker;
                        ResultFileType rft;
                        try {
                            fileStr = resultFile.getName();
                            marker = fileStr.substring(
                                    fileStr.indexOf("_") + 1, fileStr.lastIndexOf("."));
                            String fileStr2 = fileStr.substring(0, fileStr.indexOf("_"));

                            rft = ResultFileType.valueOf(fileStr2.toUpperCase());
                        } catch (IllegalArgumentException | StringIndexOutOfBoundsException ex) {
                            JOptionPane.showMessageDialog(null, "Von den "
                                    + "definierten Ergebnistypen (display, drawer, ...) "
                                    + "abweichende Datei gefunden\n"
                                    + "  --> "+fileStr+"");
                            continue;
                        }
                        List<ResultType> resultTypes = resultTypeMapping.get(marker);
                        if (resultTypes == null) {
                            resultTypes = new LinkedList<>();
                        }
                        Path refFilePath = Paths.get(
                                refPath.toString(),
                                tgFile.getName(),
                                tcFile.getName(),
                                props.getProperty(cashpFile.getName()),
                                resultFile.getName());
                        File tmpFile = refFilePath.toFile();
                        ReferenceFile reference;
                        try {
                            reference = new ReferenceFile(
                                    Files.readAllLines(tmpFile.toPath(), StandardCharsets.ISO_8859_1),
                                    marker,
                                    refFilePath);
                        } catch (NoSuchFileException ex) {
                            reference = new ReferenceFile(
                                    new LinkedList<String>(),
                                    marker, 
                                    refFilePath);
                        }

                        try {
                            resultTypes.add(new ResultType(
                                    rft,
                                    new LinkedList<>(Files.readAllLines(resultFile.toPath(), StandardCharsets.ISO_8859_1)),
                                    reference));
                            resultTypeMapping.put(marker, resultTypes);
                        } catch (NoSuchFileException ex) {
                            JOptionPane.showMessageDialog(null,
                                    "Der angegebene erg-Pfad beinhaltet nicht "
                                    + "die erfordlichen Dateien!");
                            return null;
                        }
                        
                        noResultsForTg = false;
                        numOfResultFiles++;
                    }

                    for (String marker : resultTypeMapping.keySet()) {
                        //creates the logical layer of results
                        //    - one result contains several result types
                        //    - one cashpoint contains several results
                        Result result = new Result(
                                marker,
                                new LinkedList<>(resultTypeMapping.get(marker)));
                        if (!results.contains(result)) {
                            results.add(result);
                        }
                    }
                    results = results.stream().sorted().collect(Collectors.toList());

                    String cashpointGroupStr = null;
                    try {
                        cashpointGroupStr = props
                                .getProperty(cashpFile.getName())
                                .toUpperCase();
                    } catch (NullPointerException ex) {
                        JOptionPane.showMessageDialog(null, "Kassenmapping in "
                                + tcFile.getCanonicalPath() + " beinhaltet nicht "
                                + "alle erforderlichen Werte.");
                        return null;
                    }
                    CashpointErg cp = new CashpointErg(
                            results,
                            CashpointGroup.valueOf(cashpointGroupStr),
                            cashpFile.getName(),
                            cashpFile.toPath());
                    cashpoints.add(cp);
                    //noCashpoints = false;
                }
                TestCaseErg tc = new TestCaseErg(cashpoints, tcFile.getName(), tcFile.toPath());
                testcases.add(tc);
            }
//            if(!noCashpoints) {
//                TestGroupErg tg = new TestGroupErg(testcases, tgFile.getName(), tgFile.toPath());
//                testgroups.add(tg);
//            } else {
//                invalidTestgroupsInfo.add(tgFile.getName());
//            }
            if(!noResultsForTg) {
                TestGroupErg tg = new TestGroupErg(testcases, tgFile.getName(), tgFile.toPath());
                testgroups.add(tg);
            } else {
                invalidTestgroupsInfo.add(tgFile.getName());
            }
        }
        
        String invalidInfo = "";
        for (int i = 0; i < invalidTestgroupsInfo.size(); i++) {
            invalidInfo += invalidTestgroupsInfo.get(i);
            if(i == invalidTestgroupsInfo.size()-2) {
                invalidInfo += " und ";
            }
            if(i < invalidTestgroupsInfo.size()-2) {
                invalidInfo += ", ";
            }
        }
        if(!invalidInfo.equals("")) {
            String prefix = invalidTestgroupsInfo.size() > 1 
                                ? "Für die Testgruppen " 
                                : "Für die Testgruppe ";
            String suffix = " hat keine einzige Kasse etwas geantwortet.\n"
                                + (invalidTestgroupsInfo.size() > 1 
                                    ? "Sie werden " : "Sie wird ")
                                + "daher nicht angezeigt.";
            JOptionPane.showMessageDialog(null, prefix+invalidInfo+suffix);
            if(numOfResultFiles == 0) {
                JOptionPane.showMessageDialog(null, "Unter dem angegebenen Pfad "
                        + "wurden keine validen Ergebnisse gefunden!");
                return null;
            }
        }
        
        GlobalParamter.getInstance().setNumOfResultFiles(numOfResultFiles);
        return testgroups;
    }

    public static void updateNodeList(CommandRun command) {

    }

    /**
     * Method to get all sub directories of a directory
     *
     * @param path
     * @return
     * @throws NullPointerException
     */
    public static List<File> getDirectoriesOfDirectory(File path) throws NullPointerException {
        List<File> files = new ArrayList<>();
        List<File> tmp = Arrays.asList(path.listFiles());
        for (File file : tmp) {
            if (file.isDirectory()) {
                files.add(file);
            }
        }
        return files;
    }

    /**
     * Method to get all run.xml's of a directory
     *
     * @param path
     * @return
     */
    private static List<File> getRunFilesOfDirectory(File path) {
        List<File> files = new ArrayList<>();
        List<File> tmp = Arrays.asList(path.listFiles());
        for (File file : tmp) {
            if (file.isFile() && file.getName().equals("run.xml")) {
                files.add(file);
            }
        }
        return files;
    }

    /**
     * Method to get all files of a directory
     *
     * @param path
     * @return
     */
    public static List<File> getFilesOfDirectory(File path) {
        List<File> files = new ArrayList<>();
        List<File> tmp = Arrays.asList(path.listFiles());
        for (File file : tmp) {
            if (file.isFile()) {
                files.add(file);
            }
        }
        return files;
    }

    /**
     * Method to get the current cashpoint mapping
     * @param propsFile
     * @return
     * @throws IOException 
     */
    public static Properties getCashpointMapping(String propsFile) throws IOException {
        Properties props = new Properties();
        FileInputStream fis = null;
        fis = new FileInputStream(propsFile);
        props.load(fis);
        fis.close();
        return props;
    }

    /**
     * Method to map all commands of commands.xml to CommandRun objects
     *
     * @param path
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public static List<CommandRun> mapCommandsToBeans(Path path) throws SAXException, IOException, ParserConfigurationException {
        List<CommandRun> allCommands = new ArrayList<>();

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(path.toFile());

        NodeList nodes = doc.getElementsByTagName("command");
        for (int i = 0; i < nodes.getLength(); i++) {
            String description = "";
            for (int j = 0; j < nodes.item(i).getChildNodes().getLength(); j++) {
                if (nodes.item(i).getChildNodes().item(j).getNodeName().equals("command_text")) {
                    description = nodes.item(i).getChildNodes().item(j).getTextContent();
                }
            }
            CommandRun command = new CommandRun(((Element) nodes.item(i)).getAttribute("class"),
                    nodes.item(i).getChildNodes(), description, path);
            command.setDisplayName(((Element) nodes.item(i)).getAttribute("displayname"));
            allCommands.add(command);
        }
        return allCommands;
    }

}

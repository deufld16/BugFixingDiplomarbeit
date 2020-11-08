/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.bl;

import analyzer.beans.WhitelistEntry;
import analyzer.bl.AnalyzerManager;
import dashboard.beans.Nutzer;
import explorer.gui.ExplorerAddCommandDlg;
import dashboard.beans.Durchlauf;
import general.beans.io_objects.ProjectRun;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JTree;
import recorder.guiOperations.GUIOperations;
import simulator.beans.Kasse;
import simulator.beans.Backoffice;
import simulator.beans.Testsystem;

/**
 * class which contains general Parameter/Variables that are needed within the entire programm like paths, etc...
 * @author Florian Deutschmann
 */
public class GlobalParamter {

    private static GlobalParamter instance = null;

    public static GlobalParamter getInstance() {
        if (instance == null) {
            instance = new GlobalParamter();
        }
        return instance;
    }

    private Path generalResPath = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "general", "res");
    private Path settingsResPath = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "settings", "res");
    private Path explorerResPath = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "explorer", "res");
    private Path dashboardResPath = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "dashboard", "res");
    private Path generalDevelopPath = Paths.get("C:", "Develop", "dstore_Testframework");
    private ExplorerAddCommandDlg dlg = null;
    private Path generalCommandsPath = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "explorer", "res", "commands.xml");
    private final String SIMILARITY_RATES_FILE = "similarity_rates.properties";
    
    private List<String> defaultParamterConfiguration = new LinkedList<>();
    private List<Integer> expandedRows = new LinkedList<>(Arrays.asList(0));
    private List<ProjectRun> workingProjects = new ArrayList<>();
    private Map<String, String> parameter = null;
    private List<Kasse> kassen = new ArrayList<>();
    private List<Backoffice> backoffices = new ArrayList<>();
    private boolean shift_key_down = false;
    private boolean ctrl_key_down = false;
    private Map<String, Integer> ctrl_command_v2_mapping = new HashMap();
    private List<Testsystem> testsysteme = new ArrayList<>();
    private List<WhitelistEntry> whitelistEntries = new LinkedList<>();
    private JTree trExplorer;
    private String ergRefWeighting = "ref";
    private boolean diffAlignmentRestriction = true;
    //private List<Nutzer> allUser = new LinkedList<>();
    //private Nutzer selected_user = null;
    //private boolean statistic_db_reachable = false;
    //private Durchlauf currentRun = new Durchlauf();
    private boolean optimizeDiffer = false;
    private int numOfResultFiles = 0;

    public List<String> getDefaultParamterConfiguration() {
        return defaultParamterConfiguration;
    }

    public ExplorerAddCommandDlg getDlg() {
        return dlg;
    }

    public void setDlg(ExplorerAddCommandDlg dlg) {
        this.dlg = dlg;
    }

    public boolean isShift_key_down() {
        return shift_key_down;
    }

    public void setShift_key_down(boolean shift_key_down) {
        this.shift_key_down = shift_key_down;
    }
//
//    public Nutzer getSelected_user() {
//        return selected_user;
//    }
//
//    public void setSelected_user(Nutzer selected_user) {
//        this.selected_user = selected_user;
//    }

//    public boolean isStatistic_db_reachable() {
//        return statistic_db_reachable;
//    }
//
//    public void setStatistic_db_reachable(boolean statistic_db_reachable) {
//        this.statistic_db_reachable = statistic_db_reachable;
//    }
    
    public Path getDashboardResPath() {
        return dashboardResPath;
    }

    public void setDashboardResPath(Path dashboardResPath) {
        this.dashboardResPath = dashboardResPath;
    }

//    public List<Nutzer> getAllUser() {
//        return allUser;
//    }
//
//    public void setAllUser(List<Nutzer> allUser) {
//        this.allUser = allUser;
//    }

    public boolean isCtrl_key_down() {
        return ctrl_key_down;
    }

    public void setCtrl_key_down(boolean ctrl_key_down) {
        this.ctrl_key_down = ctrl_key_down;
    }

    public Map<String, Integer> getCtrl_command_v2_mapping() {
        return ctrl_command_v2_mapping;
    }

    public void setCtrl_command_v2_mapping(Map<String, Integer> ctrl_command_v2_mapping) {
        this.ctrl_command_v2_mapping = ctrl_command_v2_mapping;
    }

    public List<Testsystem> getTestsysteme() {
        return testsysteme;
    }

    public void setTestsysteme(List<Testsystem> testsysteme) {
        this.testsysteme = testsysteme;
    }
    
    public void setDefaultParamterConfiguration(List<String> defaultParamterConfiguration) {
        this.defaultParamterConfiguration = defaultParamterConfiguration;
    }

    public Map<String, String> getParameter() {
        return parameter;
    }

    public void setParameter(Map<String, String> parameter) {
        this.parameter = parameter;
    }

    public JTree getTrExplorer() {
        return trExplorer;
    }

    public void setTrExplorer(JTree trExplorer) {
        this.trExplorer = trExplorer;
    }

    public List<Kasse> getKassen() {
        return kassen;
    }

    public void setKassen(List<Kasse> kassen) {
        this.kassen = kassen;
    }

    public List<Backoffice> getBackoffices() {
        return backoffices;
    }

    public void setBackoffices(List<Backoffice> backoffices) {
        this.backoffices = backoffices;
    }
    
    public List<Integer> getExpandedRows() {
        return expandedRows;
    }

    public void setExpandedRows(List<Integer> expandedRows) {
        this.expandedRows = expandedRows;
    }

    public List<ProjectRun> getWorkingProjects() {
        return workingProjects;
    }

    public void setWorkingProjects(List<ProjectRun> workingProjects) {
        this.workingProjects = workingProjects;
    }

    public Path getGeneralResPath() {
        return generalResPath;
    }

    public void setGeneralResPath(Path generalResPath) {
        this.generalResPath = generalResPath;
    }

    public Path getSettingsResPath() {
        return settingsResPath;
    }

    public List<String> getDefaultParameterConfiguration() {
        return defaultParamterConfiguration;
    }

    public Path getGeneralCommandsPath() {
        return generalCommandsPath;
    }

    public void setGeneralCommandsPath(Path generalCommandsPath) {
        this.generalCommandsPath = generalCommandsPath;
    }
    
    public void setDefaultParamterConfiguration() {
        defaultParamterConfiguration.add("recorderPath=" + System.getProperty("user.home") + File.separator + "Documents");
        defaultParamterConfiguration.add("dynamicFunctions=" + GUIOperations.getResPath().toString() + File.separator + "functions.conf");
        defaultParamterConfiguration.add("articleConfig=" + GUIOperations.getResPath().toString() + File.separator + "articles.conf");
        defaultParamterConfiguration.add("projectPath=" + "C:" + File.separator + "Develop" + File.separator + "dstore_Testframework" + File.separator + "dtf-project-suite"
                                            + File.separator + "trunk" + File.separator + "__HTL_Vorbereitung" + File.separator + "__NEUE_STRUKTUR__"
                                            + File.separator + "projekte");
        defaultParamterConfiguration.add("globalPath=" + "C:" + File.separator + "Develop" + File.separator + "dstore_Testframework" + File.separator + "dtf-project-suite"
                                            + File.separator + "trunk" + File.separator + "__HTL_Vorbereitung" + File.separator + "__NEUE_STRUKTUR__"
                                            + File.separator + "global");
    }

    public void setSettingsResPath(Path settingsResPath) {
        this.settingsResPath = settingsResPath;
    }

    public Path getExplorerResPath() {
        return explorerResPath;
    }

    public void setExplorerResPath(Path explorerResPath) {
        this.explorerResPath = explorerResPath;
    }

    public Path getGeneralDevelopPath() {
        return generalDevelopPath;
    }

    public void setGeneralDevelopPath(Path generalDevelopPath) {
        this.generalDevelopPath = generalDevelopPath;
    }

    public List<WhitelistEntry> getWhitelistEntries() {
        return whitelistEntries;
    }

    public void setWhitelistEntries(List<WhitelistEntry> whitelistEntries) {
        this.whitelistEntries = whitelistEntries;
        //AnalyzerManager.getInstance().executeDiff();
    }

    public String getSIMILARITY_RATES_FILE() {
        return SIMILARITY_RATES_FILE;
    }

    public String getErgRefWeighting() {
        return ergRefWeighting;
    }

    public void setErgRefWeighting(String ergRefWeighting) {
        this.ergRefWeighting = ergRefWeighting;
    }

    public boolean isDiffAlignmentRestriction() {
        return diffAlignmentRestriction;
    }

    public void setDiffAlignmentRestriction(boolean diffAlignmentRestriction) {
        this.diffAlignmentRestriction = diffAlignmentRestriction;
    }

//    public Durchlauf getCurrentRun() {
//        return currentRun;
//    }
//
//    public void setCurrentRun(Durchlauf currentRun) {
//        this.currentRun = currentRun;
//    }

    public boolean isOptimizeDiffer() {
        return optimizeDiffer;
    }

    public void setOptimizeDiffer(boolean optimizeDiffer) {
        this.optimizeDiffer = optimizeDiffer;
    }

    public int getNumOfResultFiles() {
        return numOfResultFiles;
    }

    public void setNumOfResultFiles(int numOfResultFiles) {
        this.numOfResultFiles = numOfResultFiles;
    }
}

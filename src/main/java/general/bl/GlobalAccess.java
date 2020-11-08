/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.bl;

import analyzer.gui.AnalyzerPanel;
import dashboard.gui.DashboardPanel;
import explorer.beans.Database;
import explorer.gui.ExplorerPanel;
import general.beans.io_objects.ProjectRun;
import general.enums.Tools;
import general.gui.Test_IDE_MainFrame;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.JMenu;
import javax.swing.JPanel;
import recorder.gui.MainFrame;
import remote.gui.RemotePanel;
import settings.gui.PaSettingsNew;
import simulator.gui.SimulatorPanel;

/**
 * Singlton which contains all Swing components that are needed within the entire software
 * @author Florian Deutschmann
 */
public class GlobalAccess {

    private static GlobalAccess instance = null;

    private Map<Tools, List<JMenu>> dynamicHeadlineMap = new HashMap<>();
    private AnalyzerPanel paAnalyzer = new AnalyzerPanel();
    private SimulatorPanel paSimulator = new SimulatorPanel();
    private DashboardPanel paDashboard = new DashboardPanel();
    private RemotePanel paRemote = new RemotePanel();
    private ExplorerPanel paExplorer = new ExplorerPanel();
    private JPanel paRecorder;
    private MainFrame mFrameRecorder;
    private PaSettingsNew paSettings = new PaSettingsNew(mFrameRecorder);
    private Path generalResPath = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "general", "res");
    private boolean hidden = false;
    private List<ProjectRun> workingProjects = new ArrayList<>();
    private List<Database> allDatabases = new LinkedList<>();
    private boolean databaseReachable = false;

    //Jframe Components
    private Test_IDE_MainFrame test_ide_main_frame;

    private GlobalAccess(){
        //paExplorer = new ExplorerPanel();
        //paSettings = new PaSettingsNew();
    }

    public List<Database> getAllDatabases() {
        return allDatabases;
    }

    public void setAllDatabases(List<Database> allDatabases) {
        this.allDatabases = allDatabases;
    }
     
    public static GlobalAccess getInstance() {
        if (instance == null) {
            instance = new GlobalAccess();
        }
        return instance;
    }

    public boolean isDatabaseReachable() {
        return databaseReachable;
    }

    public void setDatabaseReachable(boolean databaseReachable) {
        this.databaseReachable = databaseReachable;
    }
    
    

    public Test_IDE_MainFrame getTest_ide_main_frame() {
        return test_ide_main_frame;
    }

    public void setTest_ide_main_frame(Test_IDE_MainFrame test_ide_main_frame) {
        this.test_ide_main_frame = test_ide_main_frame;
    }

    public List<ProjectRun> getWorkingProjects() {
        return workingProjects;
    }

    public void setWorkingProjects(List<ProjectRun> workingProjects) {
        System.out.println("workingprojects set");
        this.workingProjects = workingProjects;
    }

    public PaSettingsNew getPaSettings() {
        return paSettings;
    }

    public void setPaSettings(PaSettingsNew paSettings) {
        this.paSettings = paSettings;
    }
    
    public MainFrame getmFrameRecorder() {
        return mFrameRecorder;
    }

    public void setmFrameRecorder(MainFrame mFrameRecorder) {
        this.mFrameRecorder = mFrameRecorder;
    }
    
    public Map<Tools, List<JMenu>> getDynamicHeadlineMap() {
        return dynamicHeadlineMap;
    }

    public void setDynamicHeadlineMap(Map<Tools, List<JMenu>> dynamicHeadlineMap) {
        this.dynamicHeadlineMap = dynamicHeadlineMap;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean isHidden) {
        this.hidden = isHidden;
    }

    public AnalyzerPanel getPaAnalyzer() {
        return paAnalyzer;
    }

    public void setPaAnalyzer(AnalyzerPanel paAnalyzer) {
        this.paAnalyzer = paAnalyzer;
    }

    public SimulatorPanel getPaSimulator() {
        return paSimulator;
    }

    public void setPaSimulator(SimulatorPanel paSimulator) {
        this.paSimulator = paSimulator;
    }

    public JPanel getPaRecorder() {
        return paRecorder;
    }

    public void setPaRecorder(JPanel paRecorder) {
        this.paRecorder = paRecorder;
    }

    public Path getGeneralResPath() {
        return generalResPath;
    }

    public DashboardPanel getPaDashboard() {
        return paDashboard;
    }

    public void setPaDashboard(DashboardPanel paDashboard) {
        this.paDashboard = paDashboard;
    }

    
    
    public void setGeneralResPath(Path generalResPath) {
        this.generalResPath = generalResPath;
    }

    public ExplorerPanel getPaExplorer() {
        return paExplorer;
    }

    public void setPaExplorer(ExplorerPanel paExplorer) {
        this.paExplorer = paExplorer;
    }

    public RemotePanel getPaRemote() {
        return paRemote;
    }

    public void setPaRemote(RemotePanel paRemote) {
        this.paRemote = paRemote;
    }
    
}

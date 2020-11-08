/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.bl;

import analyzer.io.ResultsIO;
import general.enums.Tools;
import java.util.Properties;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

/**
 * A singlton that contains all the methods which are being needed within the
 * entire software
 *
 * @author Florian Deutschmann
 */
public class GlobalMethods {

    private static GlobalMethods instance = null;
    private Properties diffProperties = null;

    public static GlobalMethods getInstance() {
        if (instance == null) {
            instance = new GlobalMethods();
        }
        return instance;
    }

    /**
     * Method which is being used to update the UI of the MainFrame
     */
    public void updateMainFrame() {
        GlobalAccess.getInstance().getTest_ide_main_frame().invalidate();
        GlobalAccess.getInstance().getTest_ide_main_frame().validate();
        GlobalAccess.getInstance().getTest_ide_main_frame().repaint();
    }

    /**
     * Method which is changing the currently displayed tool in regards to the
     * tool (Enum) which is handed over to the method
     *
     * @param tool
     */
    public void changeTool(Tools tool) {
        GlobalAccess.getInstance().getTest_ide_main_frame().getPaDynamic().removeAll();
        JMenuBar mb = GlobalAccess.getInstance().getTest_ide_main_frame().getJMenuBar();
        mb.removeAll();
        switch (tool) {
            case ANALYZER:
                for (JMenu menu : GlobalAccess.getInstance().getDynamicHeadlineMap().get(Tools.ANALYZER)) {
                    mb.add(menu);
                }
                GlobalAccess.getInstance().getTest_ide_main_frame().getPaDynamic().add(GlobalAccess.getInstance().getPaAnalyzer());
                break;
            case DASHBOARD:
                for (JMenu menu : GlobalAccess.getInstance().getDynamicHeadlineMap().get(Tools.DASHBOARD)) {
                    mb.add(menu);
                }
                GlobalAccess.getInstance().getTest_ide_main_frame().getPaDynamic().add(GlobalAccess.getInstance().getPaDashboard());
                break;
            case EXPLORER:
                for (JMenu menu : GlobalAccess.getInstance().getDynamicHeadlineMap().get(Tools.EXPLORER)) {
                    mb.add(menu);
                }
                GlobalAccess.getInstance().getTest_ide_main_frame().getPaDynamic().add(GlobalAccess.getInstance().getPaExplorer());
                break;
            case RECORDER:
                for (JMenu menu : GlobalAccess.getInstance().getDynamicHeadlineMap().get(Tools.RECORDER)) {
                    mb.add(menu);
                }
                GlobalAccess.getInstance().getTest_ide_main_frame().getPaDynamic().add(GlobalAccess.getInstance().getPaRecorder());
                break;
            case SIMULATOR:
                for (JMenu menu : GlobalAccess.getInstance().getDynamicHeadlineMap().get(Tools.SIMULATOR)) {
                    mb.add(menu);
                }
                GlobalAccess.getInstance().getTest_ide_main_frame().getPaDynamic().add(GlobalAccess.getInstance().getPaSimulator());
                break;
            case REMOTE:
                for (JMenu menu : GlobalAccess.getInstance().getDynamicHeadlineMap().get(Tools.REMOTE)) {
                    mb.add(menu);
                }
                GlobalAccess.getInstance().getTest_ide_main_frame().getPaDynamic().add(GlobalAccess.getInstance().getPaRemote());
                break;
            case SETTINGS:
                for (JMenu menu : GlobalAccess.getInstance().getDynamicHeadlineMap().get(Tools.SETTINGS)) {
                    mb.add(menu);
                }
                GlobalAccess.getInstance().getTest_ide_main_frame().getPaDynamic().add(GlobalAccess.getInstance().getPaSettings());
                break;
        }
        GlobalAccess.getInstance().getTest_ide_main_frame().getPaDynamic().revalidate();
        GlobalAccess.getInstance().getTest_ide_main_frame().getPaDynamic().repaint();
    }

    /**
     * Method that is used to restart the Recorder subtool
     */
    public void onRestartRecorder() {
        GlobalAccess.getInstance().getmFrameRecorder().onRestart();
        updateMainFrame();
    }
    
    /**
     * Method to save all settings for the differ
     */
    public void saveCustomDiffSettings() {
        Properties props = new Properties();
        props.setProperty("weighting", GlobalParamter.getInstance().getErgRefWeighting());
        props.setProperty("restricted", GlobalParamter.getInstance().isDiffAlignmentRestriction()+"");
        ResultsIO.saveProperties("diff_settings.properties", props);
    }
    
    /**
     * Method to load all settings for the differ
     */
    public void loadCustomDiffSettings() {
        diffProperties = ResultsIO.loadProperties("diff_settings.properties");
        
        String weightingProp = diffProperties.getProperty("weighting");
        if (weightingProp != null) {
            GlobalParamter.getInstance().setErgRefWeighting(weightingProp);
        }
        
        String restrictionProp = diffProperties.getProperty("restricted");
        if(restrictionProp != null) {
            GlobalParamter.getInstance().setDiffAlignmentRestriction(Boolean.valueOf(restrictionProp));
        }
    }

    public Properties getDiffProperties() {
        return diffProperties;
    }
}

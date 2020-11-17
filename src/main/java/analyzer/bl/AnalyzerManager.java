/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer.bl;

import analyzer.beans.CashpointErg;
import analyzer.beans.Result;
import analyzer.beans.ResultType;
import analyzer.beans.TestCaseErg;
import analyzer.beans.TestGroupErg;
import analyzer.enums.ResultFileType;
import analyzer.gui.AnalyzerPanel;
import analyzer.io.ResultsIO;
import dashboard.database.DB_Access;
import dashboard.beans.Durchlauf;
import dashboard.beans.DurchlaufNew;
import dashboard.bl.DatabaseGlobalAccess;
import dashboard.database.DB_Access_Manager;
import dashboard.gui.LoadingDLG;
import general.bl.GlobalAccess;
import general.bl.GlobalParamter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * This class is used to access the analysis functionality
 *
 * @author Maximilian Strohmaier
 */
public class AnalyzerManager {

    private static AnalyzerManager instance = null;
    private List<TestGroupErg> testgroupsErg;
    private AnalyzerPanel panel;
    private Properties stored_similarity_rates = null;

    private final String DIFF_IDENTIFIER = "§§d§";
    private final String DIFF_IDENTIFIER_END = "§§e§";
    private final String WHITELIST_SECTION_IDENTIFIER_START = "§§y§";
    private final String WHITELIST_SECTION_IDENTIFIER_END = "§§z§";

    private AnalyzerManager() {
    }

    public static AnalyzerManager getInstance() {
        if (instance == null) {
            instance = new AnalyzerManager();
        }
        return instance;
    }

    /**
     * Method to execute the entire analysis process (read data,
     * initiate diff-process, pass on data to the depiction of the results)
     *
     * @return success
     */
    public boolean runAnalysis() {
        try {
            testgroupsErg = ResultsIO.readSimulationErg();
            if (testgroupsErg != null) {
//                executeDiff();
                panel.adjustComponents();
                LoadingDLG ldlg = new LoadingDLG(
                        GlobalAccess.getInstance().getTest_ide_main_frame(), 
                        true, 
                        GlobalParamter.getInstance().getNumOfResultFiles(), 
                        "Diff-Vorgang wird ausgeführt",
                        "%d von %d Dateien verglichen");
                Thread thread = new Thread(new DiffExecutor(ldlg));
                thread.start();
                ldlg.setVisible(true);
                return true;
            }
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Method to run the diff process using Differ() class for all result files
     * within testgroupsErg and mark the success of each and each layer 
     * above the result file accordingly. 
     * Furthermore, it takes necessary actions to update the UI with the recent
     * developments.
     */
//    public void executeDiff() {
//        Differ differ = new Differ();
//        
//        if(testgroupsErg != null) {
//            for (TestGroupErg testGroupErg : testgroupsErg) {
//                boolean testgroupSuccessful = true;
//
//                for (TestCaseErg testcase : testGroupErg.getTestcases()) {
//                    boolean testcaseSuccessful = true;
//
//                    for (CashpointErg cashpoint : testcase.getCashpoints()) {
//                        boolean cashpointSuccessful = true;
//
//                        for (Result result : cashpoint.getResults()) {
//                            boolean resultSuccessful = true;
//
//                            for (ResultType type : result.getTypes()) {
//                                double simRate = type.getResultFileType().getSimilarityRate();
//                                Map<String, List<String>> differences
//                                        = differ.work(
//                                                Whitelist.applyWhitelist(
//                                                        trimLines(
//                                                                type.getReference().getLines(), 
//                                                                type.getResultFileType()),
//                                                        type.getResultFileType()),
//                                                Whitelist.applyWhitelist(
//                                                        trimLines(
//                                                                type.getLines(), 
//                                                                type.getResultFileType()), 
//                                                        type.getResultFileType()),
//                                                simRate);
//                                List<String> refLinesOnlyValid = 
//                                        Whitelist.removeIgnoreSections(
//                                                differences.get("ref"));
//                                List<String> ergLinesOnlyValid = 
//                                        Whitelist.removeIgnoreSections(
//                                                differences.get("erg"));
//                                if (refLinesOnlyValid.equals(ergLinesOnlyValid)) {
//                                    type.setSuccessful(true);
//                                } else {
//                                    type.setSuccessful(false);
//                                    resultSuccessful = false;
//                                }
//                                type.setDifferences(differences);
//                            }
//
//                            result.setSuccessful(resultSuccessful);
//                            if (!resultSuccessful) {
//                                cashpointSuccessful = false;
//                            }
//                        }
//
//                        cashpoint.setSuccessful(cashpointSuccessful);
//                        if (!cashpointSuccessful) {
//                            testcaseSuccessful = false;
//                        }
//                    }
//
//                    testcase.setSuccessful(testcaseSuccessful);
//                    if (!testcaseSuccessful) {
//                        testgroupSuccessful = false;
//                    }
//                }
//
//                testGroupErg.setSuccessful(testgroupSuccessful);
//            }
//        }
//
//        if(panel != null) {
//            ResultSelectionManager rsm = ResultSelectionManager.getInstance();
//            rsm.setAllTestgroups(testgroupsErg);
//            rsm.updateAll();
//        }
//    }
    
    /**
     * Method to remove the unnecessary parts of the content of an erg- or ref-file
     * 
     * @param lines  all lines of the file
     * @param resultFileType  Type of the resultFile (printer, drawer, ...)
     * @return  all lines reduced by the unnecessary parts
     */
    public List<String> trimLines(List<String> lines, ResultFileType resultFileType) {
        List<String> trimmedLines = new LinkedList<>();
        
        for (String line : lines) {
            String sectionStart = "<" + resultFileType.getDescription() + ">";
            String sectionEnd = "</" + resultFileType.getDescription() + ">";
            int startIndex = line.indexOf(sectionStart);
            int endIndex = line.indexOf(sectionEnd) + sectionEnd.length();
            try {
                String trimmedLine = line.substring(startIndex, endIndex);
                trimmedLines.add(trimmedLine);
            } catch (StringIndexOutOfBoundsException ex) {
                trimmedLines.add(line);
            }
        }
        
        return trimmedLines;
    }
    
    /***
     * This method is used to check if a change of the acceptance state 
     * of one of the children of a specific layer in the project explorer,
     * also changes the acceptance state of this specific layer
     * (i.e., when an acceptance of a specific result type results in the
     * situation that all non-successful result types are in accepted state,
     * the corresponding result-layer should be accepted as well (and turn green))
     * 
     * @param parent  layer in the explorer that needs to be checked
     * @param state  true if state changes to accepted, false if state changes to declined
     * @return  new state of the parent layer 
     */    
    public boolean updateAcceptanceState(Object parent, boolean state) {
        if (parent instanceof TestGroupErg) {
            for (TestCaseErg testcase : ((TestGroupErg) parent).getTestcases()) {
                if(!testcase.isSuccessful() && testcase.isAccepted() != state) {
                    ((TestGroupErg) parent).setAccepted(!state);
                    return !state;
                }
            }
            ((TestGroupErg) parent).setAccepted(state);
        } else if (parent instanceof TestCaseErg) {
            for (CashpointErg cashpoint : ((TestCaseErg) parent).getCashpoints()) {
                if(!cashpoint.isSuccessful() && cashpoint.isAccepted() != state) {
                    ((TestCaseErg) parent).setAccepted(!state);
                    return !state;
                }
            }
            ((TestCaseErg) parent).setAccepted(state);
        } else if (parent instanceof CashpointErg) {
            for (Result result : ((CashpointErg) parent).getResults()) {
                if(!result.isSuccessful() && result.isAccepted() != state) {
                    ((CashpointErg) parent).setAccepted(!state);
                    return !state;
                }
            }
            ((CashpointErg) parent).setAccepted(state);
        } else if (parent instanceof Result) {
            for (ResultType type : ((Result) parent).getTypes()) {
                if(!type.isSuccessful() && type.isAccepted() != state) {
                    ((Result) parent).setAccepted(!state);
                    return !state;
                }
            }
            ((Result) parent).setAccepted(state);
        }
        return state;
    }
    
    /**
     * This method is used to store the new versions of the reference files 
     * (all non-successful erg-files, which have been accepted by the user)
     * Includes a user confirmation before IO operation
     * 
     * @return  true if user decides to continue, false if user decides to cancel
     */
    public boolean saveNewReferences() {
        List<ResultType> newReferences = new LinkedList<>();
        
        for (TestGroupErg testGroupErg : testgroupsErg) {
            for (TestCaseErg testCaseErg : testGroupErg.getTestcases()) {
                for (CashpointErg cashpoint : testCaseErg.getCashpoints()) {
                    for (Result result : cashpoint.getResults()) {
                        for (ResultType type : result.getTypes()) {
                            if(!type.isSuccessful() && type.isAccepted()) {
                                newReferences.add(type);
                            }
                        }
                    }
                }
            }
        }
        int selection = JOptionPane.showConfirmDialog(null, (""
            + newReferences.size() 
            + ((newReferences.size() == 1) ? " Referenzdatei wird" : " Referenzdateien werden") 
            + " erneuert. Speichern?"));
        switch(selection) {
            case JOptionPane.YES_OPTION:
                for (ResultType newReference : newReferences) {
                    try {
                        ResultsIO.updateReferenceFile(
                                newReference.getReference().getPath(),
                                newReference.getLines());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
//                Durchlauf currentRun = GlobalParamter.getInstance().getCurrentRun();
//                if(currentRun != null) {
//                    currentRun.setUebernahmeAnz(newReferences.size());
//                    try {
//                        if(GlobalParamter.getInstance().isStatistic_db_reachable()) {
//                            DB_Access.getInstance().updateDurchlauf(currentRun);
//                        }
//                    } catch (SQLException ex) {
//                        JOptionPane.showMessageDialog(null, "Fehler beim Aktualisieren der Statistikwerte");
//                    }
//                }
                DurchlaufNew durchlauf = DatabaseGlobalAccess.getInstance().getDurchlauf();
                if(durchlauf != null){
                    durchlauf.setUebernahmeAnz(newReferences.size());
                    DB_Access_Manager.getInstance().updateData();
                }
            case JOptionPane.NO_OPTION:
                return true;
            default:
                return false;
        }
    }

    public AnalyzerPanel getPanel() {
        return panel;
    }

    public void setPanel(AnalyzerPanel panel) {
        this.panel = panel;
    }

    public List<TestGroupErg> getTestgroups() {
        return testgroupsErg;
    }

    public void setTestgroups(List<TestGroupErg> testgroups) {
        this.testgroupsErg = testgroups;
    }

    public String getDIFF_IDENTIFIER() {
        return DIFF_IDENTIFIER;
    }

    public String getWHITELIST_SECTION_IDENTIFIER_START() {
        return WHITELIST_SECTION_IDENTIFIER_START;
    }

    public String getWHITELIST_SECTION_IDENTIFIER_END() {
        return WHITELIST_SECTION_IDENTIFIER_END;
    }

    public Properties getStored_similarity_rates() {
        return stored_similarity_rates;
    }

    public void setStored_similarity_rates(Properties stored_similarity_rates) {
        this.stored_similarity_rates = stored_similarity_rates;
    }

    public String getDIFF_IDENTIFIER_END() {
        return DIFF_IDENTIFIER_END;
    }
    
    private class DiffExecutor implements Runnable {
        private LoadingDLG ldlg;

        public DiffExecutor(LoadingDLG ldlg) {
            this.ldlg = ldlg;
        }

        @Override
        public void run() {
            Differ differ = new Differ();
        
            if(testgroupsErg != null) {
                for (TestGroupErg testGroupErg : testgroupsErg) {
                    boolean testgroupSuccessful = true;

                    for (TestCaseErg testcase : testGroupErg.getTestcases()) {
                        boolean testcaseSuccessful = true;

                        for (CashpointErg cashpoint : testcase.getCashpoints()) {
                            boolean cashpointSuccessful = true;

                            for (Result result : cashpoint.getResults()) {
                                boolean resultSuccessful = true;

                                for (ResultType type : result.getTypes()) {
                                    double simRate = type.getResultFileType().getSimilarityRate();
                                    Map<String, List<String>> differences
                                            = differ.work(
                                                    Whitelist.applyWhitelist(
                                                            trimLines(
                                                                    type.getReference().getLines(), 
                                                                    type.getResultFileType()),
                                                            type.getResultFileType()),
                                                    Whitelist.applyWhitelist(
                                                            trimLines(
                                                                    type.getLines(), 
                                                                    type.getResultFileType()), 
                                                            type.getResultFileType()),
                                                    simRate);
                                    List<String> refLinesOnlyValid = 
                                            Whitelist.removeIgnoreSections(
                                                    differences.get("ref"));
                                    List<String> ergLinesOnlyValid = 
                                            Whitelist.removeIgnoreSections(
                                                    differences.get("erg"));
                                    if (refLinesOnlyValid.equals(ergLinesOnlyValid)) {
                                        type.setSuccessful(true);
                                    } else {
                                        type.setSuccessful(false);
                                        resultSuccessful = false;
                                    }
                                    type.setDifferences(differences);
                                    
                                    ldlg.increaseCurrValue();
                                }

                                result.setSuccessful(resultSuccessful);
                                if (!resultSuccessful) {
                                    cashpointSuccessful = false;
                                }
                            }

                            cashpoint.setSuccessful(cashpointSuccessful);
                            if (!cashpointSuccessful) {
                                testcaseSuccessful = false;
                            }
                        }

                        testcase.setSuccessful(testcaseSuccessful);
                        if (!testcaseSuccessful) {
                            testgroupSuccessful = false;
                        }
                    }

                    testGroupErg.setSuccessful(testgroupSuccessful);
                }
            }

            if(panel != null) {
                ResultSelectionManager rsm = ResultSelectionManager.getInstance();
                rsm.setAllTestgroups(testgroupsErg);
                rsm.updateAll();
            }
            
        }
        
    }
}

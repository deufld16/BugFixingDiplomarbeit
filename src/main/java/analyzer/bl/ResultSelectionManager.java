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
import analyzer.gui.AnalyzerPanel;
import analyzer.gui.components.ResultTypeButton;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;

/**
 * This class is used to manage display-data and selection-actions
 * for the comboboxes and the result type buttons
 * 
 * @author Maximilian Strohmaier
 */
public class ResultSelectionManager 
{
    private static ResultSelectionManager instance = null;
    private AnalyzerPanel panel;
    private DefaultComboBoxModel<Result> dcbmResult;
    private DefaultComboBoxModel<CashpointErg> dcbmCashpoint;
    private DefaultComboBoxModel<TestCaseErg> dcbmTC;
    private DefaultComboBoxModel<TestGroupErg> dcbmTG;
    private JPanel paTabs;
    private ButtonGroup bgTabs = new ButtonGroup();;
    private boolean tabSelected;
    
    private List<TestGroupErg> allTestgroups = new LinkedList<>();

    private ResultSelectionManager() {
    }
    
    public static ResultSelectionManager getInstance()
    {
        if(instance == null)
        {
            instance = new ResultSelectionManager();
        }
        return instance;
    }
    
    /***
     * Metod to create a new model for the testgroups-combobox
     * 
     * @return  DefaultComboBoxModel
     */
    public DefaultComboBoxModel createCbModelTestgroup()
    {
        dcbmTG = new DefaultComboBoxModel<>();
        return dcbmTG;
    }
    
    /***
     * Metod to create a new model for the testcases-combobox
     * 
     * @return  DefaultComboBoxModel
     */
    public DefaultComboBoxModel createCbModelTestcase()
    {
        dcbmTC = new DefaultComboBoxModel<>();
        return dcbmTC;
    }
    
    /***
     * Metod to create a new model for the cashpoints-combobox
     * 
     * @return  DefaultComboBoxModel
     */
    public DefaultComboBoxModel createCbModelCashpoint()
    {
        dcbmCashpoint = new DefaultComboBoxModel<>();
        return dcbmCashpoint;
    }
    
    /***
     * Metod to create a new model for the results-combobox
     * 
     * @return  DefaultComboBoxModel
     */
    public DefaultComboBoxModel createCbModelResult()
    {
        dcbmResult = new DefaultComboBoxModel<>();        
        return dcbmResult;
    }
    
    /***
     * Method to reset all selected filters
     */
    public void clearAllSelectionFilters()
    {
        dcbmTG.removeAllElements();
        dcbmTC.removeAllElements();
        dcbmCashpoint.removeAllElements();
        dcbmResult.removeAllElements();
        paTabs.removeAll();
        bgTabs = new ButtonGroup();
        panel.updateUI();
    }
    
    /***
     * Method to call when new testgroups were added to allTestgroups
     */
    public void updateAll() {
        if(dcbmTG != null) {
            panel.setEventAllowedOnCb(false);
            dcbmTG.removeAllElements();
            if(allTestgroups != null) {
                allTestgroups.forEach(e -> dcbmTG.addElement(e));
            }
            testgroupChanged();
        }
    }
    
    /***
     * Method that handles a change of the selected testgroup
     */
    public void testgroupChanged()
    {
        if(dcbmTC != null && dcbmTG != null)
        {
            panel.setEventAllowedOnCb(false);
            dcbmTC.removeAllElements();
            TestGroupErg tg = (TestGroupErg) dcbmTG.getSelectedItem();            
            if(tg != null) {
                tg.getTestcases().forEach(e -> dcbmTC.addElement(e));
            }
            testcaseChanged();
        }
    }
    
    /***
     * Method that handles a change of the selected testcase
     */
    public void testcaseChanged()
    {
        if(dcbmCashpoint != null && dcbmTC != null)
        {
            panel.setEventAllowedOnCb(false);
            dcbmCashpoint.removeAllElements();
            TestCaseErg tcErg = (TestCaseErg) dcbmTC.getSelectedItem();
//            System.out.println("Alle Testfälle:");
//            for (int i = 0; i < dcbmTC.getSize(); i++) {
//                System.out.println("  "+dcbmTC.getElementAt(i));
//            }
//            System.out.println("Testfall: " + tcErg.getDescription());
//            System.out.println("Testfall_1: " + panel.getSelectedTestcase().getDescription());
            if(tcErg != null) {
                System.out.println("Kassenpunkte: " + tcErg.getCashpoints().stream().map(CashpointErg::getDescription).collect(Collectors.toList()));
                List<CashpointErg> cps = tcErg.getCashpoints();
                //tcErg.getCashpoints().forEach(e -> dcbmCashpoint.addElement(e));   
                for (CashpointErg cashpoint : tcErg.getCashpoints()) {
                    dcbmCashpoint.addElement(cashpoint);
                }
            }
            
            cashpointChanged();
        }
    }
    
    /***
     * Method that handles a change of the selected cashpoint
     */
    public void cashpointChanged()
    {
        if(dcbmResult != null && dcbmCashpoint != null)
        {
            panel.setEventAllowedOnCb(false);
            dcbmResult.removeAllElements();
            CashpointErg cpErg = (CashpointErg) dcbmCashpoint.getSelectedItem();
            if(cpErg != null) {
                //cpErg.getResults().forEach(e -> dcbmResult.addElement(e));
                for (Result result : cpErg.getResults()) {
                    dcbmResult.addElement(result);
                }
            }
            
            resultChanged();
        }
    }
    
    /***
     * Method that handles a change of the selected result.
     * Determines the selected result-type and displays the according diff-results
     */
    public void resultChanged()
    {
        panel.setEventAllowedOnCb(false);
        panel.clearDiffPanes();
        Result selectedResult = (Result) dcbmResult.getSelectedItem();
        paTabs.removeAll();
        bgTabs = new ButtonGroup();
        List<ResultTypeButton> typeTabs = new LinkedList<>();
        if(selectedResult != null) {
            tabSelected = true;
            for (ResultType rType : selectedResult.getTypes()) 
            {
                ResultTypeButton rtb = new ResultTypeButton(rType, tabSelected);
                rtb.setPanel(panel);
                bgTabs.add(rtb);
                paTabs.add(rtb);
                tabSelected = false;
                typeTabs.add(rtb);
            }
            panel.setTypeTabs(typeTabs);
            panel.displayDiff();
        } else {
            panel.setTypeTabs(typeTabs);
        }
        panel.updateUI();
        panel.setEventAllowedOnCb(true);
    }
    
    /***
     * Method to select the next difference and display it
     * 
     * @param next  true, if first occurence of difference (even if it is 
     * already selected) should count as the next difference
     * 
     */
    public void selectNextDifference(boolean next) {
        //check on ResultType layer        
        List<ResultTypeButton> typeTabs = panel.getTypeTabs();
        for (int i = 0; i < typeTabs.size(); i++) {
            if(!next && typeTabs.get(i).isSelected()) {
                next = true;
                continue;
            }
            if(next && !typeTabs.get(i).getDisplayedResultType().isSuccessful()) {
                typeTabs.get(i).setSelected(true);                
                panel.setTypeTabs(typeTabs);
                panel.displayDiff();
                panel.updateUI();
                return;
            }
        }
        
        //check on Result layer
        if (getSelectedCashpoint() != null) {
            List<Result> results = getSelectedCashpoint().getResults();
            next = false;
            for (int i = 0; i < results.size(); i++) {
                Result result = results.get(i);
                if (!next && result.equals(getSelectedResult())) {
                    next = true;
                    continue;
                }
                if(next && !result.isSuccessful()) {
                    dcbmResult.setSelectedItem(result);
                    selectNextDifference(true);
                    return;
                }
            }
        }
        
        //check on Cashpoint layer
        if (getSelectedTC() != null) {
            List<CashpointErg> cashpoints = getSelectedTC().getCashpoints();
            next = false;
            for (int i = 0; i < cashpoints.size(); i++) {
                CashpointErg cp = cashpoints.get(i);
                if(!next && cp.equals(getSelectedCashpoint())) {
                    next = true;
                    continue;
                }
                if(next  && !cp.isSuccessful()) {
                    dcbmCashpoint.setSelectedItem(cp);
                    selectNextDifference(true);
                    return;
                }
            }
        }
        
        //check on Testcase layer
        if(getSelectedTG() != null) {
            List<TestCaseErg> testcases = getSelectedTG().getTestcases();
            next = false;
            for (int i = 0; i < testcases.size(); i++) {
                TestCaseErg tc = testcases.get(i);
                if(!next && tc.equals(getSelectedTC())) {
                    next = true;
                    continue;
                }
                if(next && !tc.isSuccessful()) {
                    dcbmTC.setSelectedItem(tc);
                    selectNextDifference(true);
                    return;
                }
            }
        }
        
        //check on Testgroup layer
        if (allTestgroups != null) {
            next = false;
            for (int i = 0; i < allTestgroups.size(); i++) {
                TestGroupErg tg = allTestgroups.get(i);
                if(!next && tg.equals(getSelectedTG())) {
                    next = true;
                    continue;
                }
                if(next && !tg.isSuccessful()) {
                    dcbmTG.setSelectedItem(tg);
                    selectNextDifference(true);
                    return;
                }
            }
        }
    }
    
    public TestGroupErg getTestgroupAt(int index) {
        return dcbmTG.getElementAt(index);
    }

    public TestGroupErg getSelectedTG() {
        return (TestGroupErg) dcbmTG.getSelectedItem();
    }
    
    public TestCaseErg getSelectedTC() {
        return (TestCaseErg) dcbmTC.getSelectedItem();
    }
    
    public CashpointErg getSelectedCashpoint() {
        return (CashpointErg) dcbmCashpoint.getSelectedItem();
    }
    
    public Result getSelectedResult() {
        return (Result) dcbmResult.getSelectedItem();
    }

    public JPanel getPaTabs() {
        return paTabs;
    }

    public void setPaTabs(JPanel paTabs) {
        this.paTabs = paTabs;
    }

    public ButtonGroup getBgTabs() {
        return bgTabs;
    }

    public void setBgTabs(ButtonGroup bgTabs) {
        this.bgTabs = bgTabs;
    }

    public boolean isTabSelected() {
        return tabSelected;
    }

    public void setTabSelected(boolean tabSelected) {
        this.tabSelected = tabSelected;
    }

    public AnalyzerPanel getPanel() {
        return panel;
    }

    public void setPanel(AnalyzerPanel panel) {
        this.panel = panel;
    }

    public List<TestGroupErg> getAllTestgroups() {
        return allTestgroups;
    }

    public void setAllTestgroups(List<TestGroupErg> allTestgroups) {
        this.allTestgroups = allTestgroups;
    }
}

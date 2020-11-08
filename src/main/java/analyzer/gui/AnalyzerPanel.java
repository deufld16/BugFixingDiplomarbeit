/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer.gui;

import analyzer.beans.CashpointErg;
import analyzer.beans.Result;
import analyzer.beans.ResultType;
import analyzer.beans.TestCaseErg;
import analyzer.beans.TestGroupErg;
import analyzer.bl.AnalyzerManager;
import analyzer.bl.ResultSelectionManager;
import analyzer.gui.components.DiffPane;
import analyzer.gui.components.ResultTypeButton;
import analyzer.gui.renderers.ComboboxRenderer;
import analyzer.io.ResultsIO;
import general.bl.ContainerKeyListener;
import general.bl.GlobalAccess;
import general.bl.GlobalMethods;
import general.bl.GlobalParamter;
import general.enums.Tools;
import java.awt.Color;
import java.awt.Component;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Panel to display the result analysis
 * 
 * @author Maximilian Strohmaier
 */
public class AnalyzerPanel extends javax.swing.JPanel {
    
    private boolean activated = false;
    private List<JMenu> ownMenus;
    private AnalyzerManager am;
    private ResultSelectionManager rsm;
    private List<ResultTypeButton> typeTabs = new LinkedList<>();
    private final ChangeListener CL_SP_1 = this::onScrollSp1;
    private final ChangeListener CL_SP_2 = this::onScrollSp2;
    private boolean eventAllowedOnCb = true;
    private final ContainerKeyListener CKL = new ContainerKeyListener();
    
    private DiffPane refPane;
    private DiffPane ergPane;

    
    /**
     * Creates new form AnalyzerGUI
     */
    public AnalyzerPanel() {
        initComponents();
        GlobalMethods gm = GlobalMethods.getInstance();
        if (gm.getDiffProperties() == null) {
            gm.loadCustomDiffSettings();
        }
        
        paFinalActions.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.GRAY));
        setComponentsState(this, false);
        
        refPane = new DiffPane();
        refPane.addKeyListener(CKL);
        refPane.setFocusable(true);
        sp1.setViewportView(refPane);
        
        ergPane = new DiffPane();
        ergPane.addKeyListener(CKL);
        ergPane.setFocusable(true);
        sp2.setViewportView(ergPane);
        
        for (Component comp : getComponents()) {
            setMenuBarHideListener(comp);
        }
        updateUI();
    }
    
    /***
     * This method is used to add the listener so that the vertical menu bar
     * can be hidden with strg+h
     */
    private void setMenuBarHideListener(Component component) {
        component.addKeyListener(CKL);
        component.setFocusable(true);
        
        if(component instanceof JPanel)
        {
            for (Component subComp : ((JPanel) component).getComponents()) {
                setMenuBarHideListener(subComp);
            }
        }
        else if(component instanceof JScrollPane)
        {
            for (Component subComp : ((JScrollPane) component).getComponents()) {
                setMenuBarHideListener(subComp);
            }
        }
    }
    
    /***
     * Method to open an AnalyzeInitDialog to get necessary input data 
     * and start a new analysis
     */
    public void startNewDiff()
    {
        if(!activated) {
            AnalyzerInitDlg initDlg = new AnalyzerInitDlg(null, true);
            initDlg.setVisible(true);
            if(initDlg.isOk()) {
                activate(initDlg.getRefPath(), initDlg.getErgPath());
            }
        }
    }
    
    /***
     * Method to activate the analysis process and enable the UI
     * 
     * @param refPath  Parent path for the ref files (e.g., ending with 20.050/)
     * @param ergPath  Parent path for the erg files (e.g., ending with 20200604_1339/)
     */
    public void activate(Path refPath, Path ergPath)
    {
        int paneResult = JOptionPane.showConfirmDialog(null, "Soll eine automatisierte "
                + "Optimierung des Differs vorgenommen werden?\n\n"
                + "Hinweis: Der Diff-Prozess kann in diesem Fall etwas mehr "
                + "Zeit in Anspruch nehmen.", "Diff-Optimierung", JOptionPane.YES_NO_OPTION);
        GlobalParamter.getInstance().setOptimizeDiffer(paneResult == JOptionPane.YES_OPTION);
        
        adjustComponents();
        ResultsIO.setRefPath(refPath);
        ResultsIO.setErgPath(ergPath);
        
        if(am.runAnalysis()) {
            setComponentsState(this, true);
            updateAllActionLevels();
            activated = true;
            ownMenus = GlobalAccess.getInstance().getDynamicHeadlineMap().get(Tools.ANALYZER);
            ownMenus.get(0).getMenuComponent(0).setEnabled(false);
            ownMenus.get(0).getMenuComponent(1).setEnabled(true);
            ownMenus.get(0).getMenuComponent(2).setEnabled(true);
        }
    }
    
    /***
     * Method to deactivate the analysis process and disable the UI
     */
    public void deactivate()
    {
        btCancel.setFocusPainted(false);
        btFinish.setFocusPainted(false);
        refPane.setText("");
        ergPane.setText("");
        rsm.clearAllSelectionFilters();
        updateAllActionLevels();
        
        sp1.getViewport().removeChangeListener(CL_SP_1);
        sp2.getViewport().removeChangeListener(CL_SP_2);
        ((JMenuItem) ownMenus.get(0).getMenuComponent(1)).setText("Scrollverbindung von ref und erg aufheben");
        updateUI();
        
        setComponentsState(this, false);
        activated = false;
        ownMenus.get(0).getMenuComponent(0).setEnabled(true);
        ownMenus.get(0).getMenuComponent(1).setEnabled(false);
        ownMenus.get(0).getMenuComponent(2).setEnabled(false);
        
        typeTabs = new LinkedList<>();
    }
    
    /***
     * Method to restart the analysis process with the same parameters (erg + 
     * ref path)
     */
     public void restart() {
         deactivate();
         activate(ResultsIO.getRefPath(), ResultsIO.getErgPath());
     }
    
    /***
     * Method to adjust the GUI components in order to get an adequate UI
     */
    private void adjustComponents()
    {        
        sp1.getViewport().addChangeListener(CL_SP_1);
        sp2.getViewport().addChangeListener(CL_SP_2);
        updateUI();
        
        am = AnalyzerManager.getInstance();
        am.setPanel(this);
        
        rsm = ResultSelectionManager.getInstance();
        rsm.setPanel(this);        
        rsm.setPaTabs(paTabs);
        
        cbSelectTG.setModel(rsm.createCbModelTestgroup());
        ListCellRenderer lcrTg = cbSelectTG.getRenderer();
        cbSelectTG.setRenderer(new ComboboxRenderer(lcrTg));
        
        cbSelectTC.setModel(rsm.createCbModelTestcase());
        ListCellRenderer lcrTc = cbSelectTC.getRenderer();
        cbSelectTC.setRenderer(new ComboboxRenderer(lcrTc));
        
        cbSelectCashpoint.setModel(rsm.createCbModelCashpoint());
        ListCellRenderer lcrCp = cbSelectCashpoint.getRenderer();
        cbSelectCashpoint.setRenderer(new ComboboxRenderer(lcrCp));
        
        cbSelectResult.setModel(rsm.createCbModelResult());
        ListCellRenderer lcrRe = cbSelectResult.getRenderer();
        cbSelectResult.setRenderer(new ComboboxRenderer(lcrRe));
    }
    
    /***
     * Method that is called when a user scrolls on the ref-diff-pane
     * 
     * @param e  ChangeEvent
     */
    private void onScrollSp1(ChangeEvent e) {
        JViewport viewport = (JViewport) e.getSource();
        sp2.getViewport().setViewPosition(viewport.getViewPosition());
    }
    
    /***
     * Method that is called when a user scrolls on the erg-diff-pane
     * 
     * @param e  ChangeEvent
     */
    private void onScrollSp2(ChangeEvent e) {
        JViewport viewport = (JViewport) e.getSource();
        sp1.getViewport().setViewPosition(viewport.getViewPosition());
    }
    
    /***
     * Method to connect/disconnect the scrolling behavior of the 2 diff-panes 
     * according to the menu selection
     */
    public void editScrollBehavior() {
        JMenuItem mi = (JMenuItem) ownMenus.get(0).getMenuComponent(1);
        String miText = mi.getText();
        if(miText.endsWith("aufheben")) {
            sp1.getViewport().removeChangeListener(CL_SP_1);
            sp2.getViewport().removeChangeListener(CL_SP_2);
            miText = "Scrollverbindung von ref und erg erzeugen";
        } else {
            sp1.getViewport().addChangeListener(CL_SP_1);
            sp2.getViewport().addChangeListener(CL_SP_2);
            miText = "Scrollverbindung von ref und erg aufheben";
        }
        mi.setText(miText);
        updateUI();
    }
    
    /***
     * Method to enable/disable all components
     * 
     * @param parentComponent  origin-component
     * @param enable  if true, enable all components; if false, disable all components
     */
    private void setComponentsState(Component parentComponent, boolean enable) 
    {
        Component[] comps = null;
        if(parentComponent instanceof JPanel)
        {
            comps = ((JPanel) parentComponent).getComponents();
        }
        else if(parentComponent instanceof JScrollPane)
        {
            comps = ((JScrollPane) parentComponent).getComponents();
        }
        try {
            for (Component comp : comps) {            
                if(comp instanceof JPanel)
                {
                    setComponentsState((JPanel) comp, enable);
                }
                else if(comp instanceof JScrollPane)
                {
                    setComponentsState((JScrollPane) comp, enable);
                }
                comp.setEnabled(enable);
            }
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
        }
    }
    
    /***
     * Method to update the displaytion of the tg-radio-button
     */
    public void updateActionLevelTestgroup()
    {
        try {
            rbActionLevelTG.setText(rsm.getSelectedTG().getDescription());
            rbActionLevelTG.setEnabled(true);
            rbActionLevelTG.setSelected(true);
        } catch (NullPointerException nex)
        {
            rbActionLevelTG.setText("");
            rbActionLevelTG.setEnabled(false);
        }
    }
    
    /***
     * Method to update the displaytion of the tc-radio-button
     */
    public void updateActionLevelTestcase()
    {
        try {
            rbActionLevelTC.setText(rsm.getSelectedTC().getDescription());
            rbActionLevelTC.setEnabled(true);
            rbActionLevelTC.setSelected(true);
        } catch (NullPointerException nex)
        {
            rbActionLevelTC.setText("");
            rbActionLevelTC.setEnabled(false);
        }
    }
    
    /**
     * Method to update the displaytion of the cashpoint-radio-button
     */
    public void updateActionLevelCashpoint()
    {
        try {
            rbActionLevelCashp.setText(rsm.getSelectedCashpoint().getDescription());
            rbActionLevelCashp.setEnabled(true);
            rbActionLevelCashp.setSelected(true);
            cbSelectCashpoint.setToolTipText(rsm.getSelectedCashpoint().getGroup().toString());
        } catch (NullPointerException nex)
        {
            rbActionLevelCashp.setText("");
            rbActionLevelCashp.setEnabled(false);
            cbSelectCashpoint.setToolTipText("");
        }
    }
   
    /***
     * Method to update the displaytion of the result-radio-button
     */
    public void updateActionLevelResult()
    {
        try {
            rbActionLevelResult.setText(rsm.getSelectedResult().getMarker());
            rbActionLevelResult.setEnabled(true);
            rbActionLevelResult.setSelected(true);
        } catch (NullPointerException nex)
        {
            rbActionLevelResult.setText("");
            rbActionLevelResult.setEnabled(false);
        }
    }
    
    /**
     * Method to update the displaytion of the result-type-radio-button
     */
    public void updateActionLevelResultType()
    {
        try {
            rbActionLevelResultType.setText(rsm.getBgTabs().getSelection().getActionCommand());
            rbActionLevelResultType.setEnabled(true);
            rbActionLevelResultType.setSelected(true);
        } catch (NullPointerException nex)
        {
            rbActionLevelResultType.setText("");
            rbActionLevelResultType.setEnabled(false);
        }
    }
    
    /**
     * Method to update the displaytion of all radiobuttons
     */
    private void updateAllActionLevels()
    {
        updateActionLevelTestgroup();
        updateActionLevelTestcase();
        updateActionLevelCashpoint();
        updateActionLevelResult();
        updateActionLevelResultType();
    }
    
    /**
     * Method to display the diffing result in both diff panes
     */
    public void displayDiff()
    {
        for (ResultTypeButton resultTypeButton : typeTabs) {
            ResultType rt = resultTypeButton.getDisplayedResultType();
            if(resultTypeButton.isSelected()) {
                Map<String, List<String>> differences = 
                        resultTypeButton.getDisplayedResultType().getDifferences();
                if(differences != null) {
                    refPane.setTextByLines(differences.get("ref"));
                    ergPane.setTextByLines(differences.get("erg"));
                }
            }
        }
    }

    public boolean isEventAllowedOnCb() {
        return eventAllowedOnCb;
    }

    public void setEventAllowedOnCb(boolean eventAllowedOnCb) {
        this.eventAllowedOnCb = eventAllowedOnCb;
    }
    
    /**
     * Method to handle a change of the acceptance state
     * 
     * @param accept  true if state changes to accepted, false if state changes to declined 
     */
    private void acceptDifferences(boolean accept) {
        switch(bgRbActionLevel.getSelection().getActionCommand()) {
            case "tg":
                rsm.getSelectedTG().setAccepted(accept);
                rsm.getSelectedTG().acceptAllChildren(accept);
                break;
            case "tc":
                rsm.getSelectedTC().setAccepted(accept);
                rsm.getSelectedTC().acceptAllChildren(accept);
                am.updateAcceptanceState(rsm.getSelectedTG(), activated);
                break;
            case "cashp":
                rsm.getSelectedCashpoint().setAccepted(accept);
                rsm.getSelectedCashpoint().acceptAllChildren(accept);
                am.updateAcceptanceState(rsm.getSelectedTC(), activated);
                am.updateAcceptanceState(rsm.getSelectedTG(), activated);
                break;
            case "result":
                rsm.getSelectedResult().setAccepted(accept);
                rsm.getSelectedResult().acceptAllChildren(accept);
                am.updateAcceptanceState(rsm.getSelectedCashpoint(), activated);
                am.updateAcceptanceState(rsm.getSelectedTC(), activated);
                am.updateAcceptanceState(rsm.getSelectedTG(), activated);
                break;
            case "resultType":
                for (ResultTypeButton resultTypeButton : typeTabs) {
                    ResultType rt = resultTypeButton.getDisplayedResultType();
                    if(resultTypeButton.isSelected() && !rt.isSuccessful()) {
                        rt.setAccepted(accept);
                        am.updateAcceptanceState(rsm.getSelectedResult(), activated);
                        am.updateAcceptanceState(rsm.getSelectedCashpoint(), activated);
                        am.updateAcceptanceState(rsm.getSelectedTC(), activated);
                        am.updateAcceptanceState(rsm.getSelectedTG(), activated);
                        break;
                    }
                }
                break;
        }
        validate();
        repaint();
        for (ResultTypeButton typeTab : typeTabs) {
            typeTab.repaint();
        }
    }

    /**
     * Method to reset the displaytion of the diff panes
     */
    public void clearDiffPanes() {
        refPane.setText("");
        ergPane.setText("");
    }

    public List<ResultTypeButton> getTypeTabs() {
        return typeTabs;
    }
    
    public void setTypeTabs(List<ResultTypeButton> typeTabs) {
        this.typeTabs = typeTabs;
    }
   
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bgRbActionLevel = new javax.swing.ButtonGroup();
        paActions = new javax.swing.JPanel();
        paActionsWest = new javax.swing.JPanel();
        btDiffDecline = new javax.swing.JButton();
        btDiffAccept = new javax.swing.JButton();
        paActionsEast = new javax.swing.JPanel();
        btNext = new javax.swing.JButton();
        paResultTGTC = new javax.swing.JPanel();
        cbSelectTG = new javax.swing.JComboBox<>();
        cbSelectTC = new javax.swing.JComboBox<>();
        paResultKassaFile = new javax.swing.JPanel();
        cbSelectCashpoint = new javax.swing.JComboBox<>();
        cbSelectResult = new javax.swing.JComboBox<>();
        paAction2West = new javax.swing.JPanel();
        lbActionLevelInfo = new javax.swing.JLabel();
        rbActionLevelTG = new javax.swing.JRadioButton();
        rbActionLevelTC = new javax.swing.JRadioButton();
        paAction2East = new javax.swing.JPanel();
        rbActionLevelCashp = new javax.swing.JRadioButton();
        rbActionLevelResult = new javax.swing.JRadioButton();
        rbActionLevelResultType = new javax.swing.JRadioButton();
        paDiff = new javax.swing.JPanel();
        paVRef = new javax.swing.JPanel();
        sp1 = new javax.swing.JScrollPane();
        paVErg = new javax.swing.JPanel();
        sp2 = new javax.swing.JScrollPane();
        paItemsBottom = new javax.swing.JPanel();
        paTabs = new javax.swing.JPanel();
        paFinalActions = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btFinish = new javax.swing.JButton();
        btCancel = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 8, 8));
        setLayout(new java.awt.BorderLayout(0, 8));

        paActions.setBackground(new java.awt.Color(255, 255, 255));
        paActions.setLayout(new java.awt.GridLayout(3, 2, 8, 8));

        paActionsWest.setBackground(new java.awt.Color(255, 255, 255));
        paActionsWest.setLayout(new java.awt.GridLayout(1, 2, 8, 0));

        btDiffDecline.setBackground(new java.awt.Color(51, 51, 51));
        btDiffDecline.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btDiffDecline.setForeground(new java.awt.Color(255, 255, 255));
        btDiffDecline.setText("Unterschiede ablehnen");
        btDiffDecline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onDecline(evt);
            }
        });
        paActionsWest.add(btDiffDecline);

        btDiffAccept.setBackground(new java.awt.Color(51, 51, 51));
        btDiffAccept.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btDiffAccept.setForeground(new java.awt.Color(255, 255, 255));
        btDiffAccept.setText("Unterschiede akzeptieren");
        btDiffAccept.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onAccept(evt);
            }
        });
        paActionsWest.add(btDiffAccept);

        paActions.add(paActionsWest);

        paActionsEast.setBackground(new java.awt.Color(255, 255, 255));
        paActionsEast.setLayout(new java.awt.GridLayout(1, 2, 8, 0));

        btNext.setBackground(new java.awt.Color(51, 51, 51));
        btNext.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btNext.setForeground(new java.awt.Color(255, 255, 255));
        btNext.setText("Nächster Unterschied");
        btNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onNextDifference(evt);
            }
        });
        paActionsEast.add(btNext);

        paActions.add(paActionsEast);

        paResultTGTC.setBackground(new java.awt.Color(255, 255, 255));
        paResultTGTC.setLayout(new java.awt.GridLayout(1, 2, 8, 0));

        cbSelectTG.setBackground(new java.awt.Color(120, 120, 120));
        cbSelectTG.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cbSelectTG.setForeground(new java.awt.Color(204, 10, 10));
        cbSelectTG.setActionCommand("tg");
        cbSelectTG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onSelectFilterTG(evt);
            }
        });
        paResultTGTC.add(cbSelectTG);

        cbSelectTC.setBackground(new java.awt.Color(120, 120, 120));
        cbSelectTC.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cbSelectTC.setActionCommand("tc");
        cbSelectTC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onSelectFilterTC(evt);
            }
        });
        paResultTGTC.add(cbSelectTC);

        paActions.add(paResultTGTC);

        paResultKassaFile.setBackground(new java.awt.Color(255, 255, 255));
        paResultKassaFile.setLayout(new java.awt.GridLayout(1, 2, 8, 0));

        cbSelectCashpoint.setBackground(new java.awt.Color(120, 120, 120));
        cbSelectCashpoint.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cbSelectCashpoint.setActionCommand("cashp");
        cbSelectCashpoint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onSelectFilterCashpoint(evt);
            }
        });
        paResultKassaFile.add(cbSelectCashpoint);

        cbSelectResult.setBackground(new java.awt.Color(120, 120, 120));
        cbSelectResult.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        cbSelectResult.setActionCommand("result");
        cbSelectResult.setPreferredSize(new java.awt.Dimension(93, 50));
        cbSelectResult.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onSelectFilterResult(evt);
            }
        });
        paResultKassaFile.add(cbSelectResult);

        paActions.add(paResultKassaFile);

        paAction2West.setBackground(new java.awt.Color(255, 255, 255));
        paAction2West.setLayout(new java.awt.GridLayout(1, 3));

        lbActionLevelInfo.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        lbActionLevelInfo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbActionLevelInfo.setText("<html><p>Unterschiede</p><p>übernehmen für</p>");
        lbActionLevelInfo.setToolTipText("");
        paAction2West.add(lbActionLevelInfo);

        bgRbActionLevel.add(rbActionLevelTG);
        rbActionLevelTG.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        rbActionLevelTG.setActionCommand("tg");
        paAction2West.add(rbActionLevelTG);

        bgRbActionLevel.add(rbActionLevelTC);
        rbActionLevelTC.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        rbActionLevelTC.setActionCommand("tc");
        paAction2West.add(rbActionLevelTC);

        paActions.add(paAction2West);

        paAction2East.setBackground(new java.awt.Color(255, 255, 255));
        paAction2East.setLayout(new java.awt.GridLayout(1, 3));

        bgRbActionLevel.add(rbActionLevelCashp);
        rbActionLevelCashp.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        rbActionLevelCashp.setActionCommand("cashp");
        paAction2East.add(rbActionLevelCashp);

        bgRbActionLevel.add(rbActionLevelResult);
        rbActionLevelResult.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        rbActionLevelResult.setActionCommand("result");
        paAction2East.add(rbActionLevelResult);

        bgRbActionLevel.add(rbActionLevelResultType);
        rbActionLevelResultType.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        rbActionLevelResultType.setSelected(true);
        rbActionLevelResultType.setActionCommand("resultType");
        paAction2East.add(rbActionLevelResultType);

        paActions.add(paAction2East);

        add(paActions, java.awt.BorderLayout.NORTH);

        paDiff.setBackground(new java.awt.Color(255, 255, 255));
        paDiff.setLayout(new java.awt.GridLayout(1, 2, 8, 8));

        paVRef.setBackground(new java.awt.Color(255, 255, 255));
        paVRef.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)), "ref", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14), new java.awt.Color(51, 51, 51))); // NOI18N
        paVRef.setLayout(new java.awt.BorderLayout());

        sp1.setBackground(new java.awt.Color(51, 51, 51));
        paVRef.add(sp1, java.awt.BorderLayout.CENTER);

        paDiff.add(paVRef);

        paVErg.setBackground(new java.awt.Color(255, 255, 255));
        paVErg.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 51, 51)), "erg", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 0, 14), new java.awt.Color(51, 51, 51))); // NOI18N
        paVErg.setLayout(new java.awt.BorderLayout());

        sp2.setBackground(new java.awt.Color(51, 51, 51));
        paVErg.add(sp2, java.awt.BorderLayout.CENTER);

        paDiff.add(paVErg);

        add(paDiff, java.awt.BorderLayout.CENTER);

        paItemsBottom.setBackground(new java.awt.Color(255, 255, 255));
        paItemsBottom.setLayout(new java.awt.GridLayout(2, 1, 8, 8));

        paTabs.setBackground(new java.awt.Color(255, 255, 255));
        paTabs.setLayout(new java.awt.GridLayout(1, 4));
        paItemsBottom.add(paTabs);

        paFinalActions.setBackground(new java.awt.Color(255, 255, 255));
        paFinalActions.setLayout(new java.awt.GridLayout(1, 4, 8, 8));
        paFinalActions.add(jLabel1);

        btFinish.setBackground(new java.awt.Color(51, 51, 51));
        btFinish.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btFinish.setForeground(new java.awt.Color(255, 255, 255));
        btFinish.setText("Fertigstellen");
        btFinish.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onFinish(evt);
            }
        });
        paFinalActions.add(btFinish);

        btCancel.setBackground(new java.awt.Color(51, 51, 51));
        btCancel.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        btCancel.setForeground(new java.awt.Color(255, 255, 255));
        btCancel.setText("Abbrechen");
        btCancel.setPreferredSize(new java.awt.Dimension(131, 60));
        btCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                onCancel(evt);
            }
        });
        paFinalActions.add(btCancel);
        paFinalActions.add(jLabel2);

        paItemsBottom.add(paFinalActions);

        add(paItemsBottom, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Method called when clicking on cancel button
     * 
     * @param evt 
     */
    private void onCancel(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onCancel
        // TODO add your handling code here:
        deactivate();
    }//GEN-LAST:event_onCancel

    /**
     * Method called when clicking on finish button
     * 
     * @param evt 
     */
    private void onFinish(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onFinish
        // TODO add your handling code here:
        if(am.saveNewReferences()) {
            deactivate();
            GlobalAccess.getInstance().getTest_ide_main_frame().changeTool("explorer");
        }
    }//GEN-LAST:event_onFinish

    /**
     * Method called when clicking on decline differences button
     * 
     * @param evt 
     */
    private void onDecline(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onDecline
        // TODO add your handling code here:
        acceptDifferences(false);
    }//GEN-LAST:event_onDecline

    /**
     * Method called when clicking on accept differences button
     * 
     * @param evt 
     */
    private void onAccept(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onAccept
        // TODO add your handling code here:
        acceptDifferences(true);
    }//GEN-LAST:event_onAccept
    
    /**
     * Method called when clicking on next difference button
     * 
     * @param evt 
     */
    private void onNextDifference(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onNextDifference
        // TODO add your handling code here:
        rsm.selectNextDifference(false);
    }//GEN-LAST:event_onNextDifference

    /**
     * Method called when selecting another testcase from the combobox
     * 
     * @param evt 
     */
    private void onSelectFilterTC(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onSelectFilterTC
        // TODO add your handling code here:
        if(eventAllowedOnCb)
        {
            rsm.testcaseChanged();
            updateActionLevelTestcase();
            updateActionLevelCashpoint();
            updateActionLevelResult();
            updateActionLevelResultType();
        }
    }//GEN-LAST:event_onSelectFilterTC

    /**
     * Method called when selecting another testgroup from the combobox
     * 
     * @param evt 
     */
    private void onSelectFilterTG(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onSelectFilterTG
        // TODO add your handling code here:
        if(eventAllowedOnCb)
        {
            rsm.testgroupChanged();
            updateAllActionLevels();
        }
    }//GEN-LAST:event_onSelectFilterTG

    /**
     * Method called when selecting another cashpoint from the combobox
     * 
     * @param evt 
     */
    private void onSelectFilterCashpoint(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onSelectFilterCashpoint
        // TODO add your handling code here:
        if(eventAllowedOnCb)
        {
            rsm.cashpointChanged();
            updateActionLevelCashpoint();
            updateActionLevelResult();
            updateActionLevelResultType();
        }
    }//GEN-LAST:event_onSelectFilterCashpoint

    /**
     * Method called when selecting another result from the combobox
     * 
     * @param evt 
     */
    private void onSelectFilterResult(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_onSelectFilterResult
        // TODO add your handling code here:
        if(eventAllowedOnCb)
        {
            rsm.resultChanged();
            updateActionLevelResult();
            updateActionLevelResultType();
        }
    }//GEN-LAST:event_onSelectFilterResult

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup bgRbActionLevel;
    private javax.swing.JButton btCancel;
    private javax.swing.JButton btDiffAccept;
    private javax.swing.JButton btDiffDecline;
    private javax.swing.JButton btFinish;
    private javax.swing.JButton btNext;
    private javax.swing.JComboBox<CashpointErg> cbSelectCashpoint;
    private javax.swing.JComboBox<Result> cbSelectResult;
    private javax.swing.JComboBox<TestCaseErg> cbSelectTC;
    private javax.swing.JComboBox<TestGroupErg> cbSelectTG;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel lbActionLevelInfo;
    private javax.swing.JPanel paAction2East;
    private javax.swing.JPanel paAction2West;
    private javax.swing.JPanel paActions;
    private javax.swing.JPanel paActionsEast;
    private javax.swing.JPanel paActionsWest;
    private javax.swing.JPanel paDiff;
    private javax.swing.JPanel paFinalActions;
    private javax.swing.JPanel paItemsBottom;
    private javax.swing.JPanel paResultKassaFile;
    private javax.swing.JPanel paResultTGTC;
    private javax.swing.JPanel paTabs;
    private javax.swing.JPanel paVErg;
    private javax.swing.JPanel paVRef;
    private javax.swing.JRadioButton rbActionLevelCashp;
    private javax.swing.JRadioButton rbActionLevelResult;
    private javax.swing.JRadioButton rbActionLevelResultType;
    private javax.swing.JRadioButton rbActionLevelTC;
    private javax.swing.JRadioButton rbActionLevelTG;
    private javax.swing.JScrollPane sp1;
    private javax.swing.JScrollPane sp2;
    // End of variables declaration//GEN-END:variables

}

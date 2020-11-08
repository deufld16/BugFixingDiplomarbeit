/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer.gui.components;

import analyzer.beans.ResultType;
import analyzer.gui.AnalyzerPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import javax.swing.JToggleButton;

/**
 * This class represents an UI component that is used to display and manage
 * the result-types
 * 
 * @author Maximilian Strohmaier
 */
public class ResultTypeButton extends JToggleButton
{
    private AnalyzerPanel panel;
    private ResultType displayedResultType = new ResultType();
    private final Color COLOR_NOK = new Color(204, 10, 10);
    private final Color COLOR_OK = new Color(15, 125, 24);
    
    public ResultTypeButton(ResultType resultType, boolean selected) {
        super(resultType.getResultFileType().getDescription());
        this.displayedResultType = resultType;
        customizeAppearance();
        setSelected(selected);
        setActionCommand(resultType.getResultFileType().getDescription());
        addActionListener((ActionEvent e) -> {
            selectType(e);
        });
    }
    
    /***
     * Method to customize the appearance of the component,
     * so that a consistence UI could be achieved
     */
    private void customizeAppearance()
    {
        setFont(new Font("Tahoma", Font.PLAIN, 18));
        setBackground(new Color(120, 120, 120));
        setPreferredSize(new Dimension(100, 60));
    }
    
    /***
     * Method to handle a click on the component
     * @param evt  represents the Event initiated by the user's selection
     */
    private void selectType(ActionEvent evt) 
    {
        panel.updateActionLevelResultType();
        panel.displayDiff();
    }
    
    public AnalyzerPanel getPanel() {
        return panel;
    }

    public void setPanel(AnalyzerPanel panel) {
        this.panel = panel;
    }

    public ResultType getDisplayedResultType() {
        return displayedResultType;
    }

    @Override
    public void repaint() {
        if(displayedResultType != null && !displayedResultType.isSuccessful()) {
            setForeground(Color.WHITE);
            setBackground(displayedResultType.isAccepted()
                    ? COLOR_OK
                    : COLOR_NOK);
        }
        super.repaint();
    }
}

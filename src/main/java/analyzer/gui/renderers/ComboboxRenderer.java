/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer.gui.renderers;

import analyzer.beans.CashpointErg;
import analyzer.beans.Result;
import analyzer.beans.TestCaseErg;
import analyzer.beans.TestGroupErg;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *  Customized Renderer for the comboboxes
 * 
 * @author Maximilian Strohmaier
 */
public class ComboboxRenderer implements ListCellRenderer {
    
    private final ListCellRenderer lcr;
    private final Color COLOR_NOK = new Color(204, 10, 10);
    private final Color COLOR_OK = new Color(15, 125, 24);
    private Color foregroundColor = Color.black;

    public ComboboxRenderer(ListCellRenderer lcr) {
        this.lcr = lcr;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if(value != null) {
            if(value.getClass().equals(TestGroupErg.class)) 
            {
                if(!((TestGroupErg) value).isSuccessful()) 
                {
                    if(!((TestGroupErg) value).isAccepted()) 
                    {
                        list.setForeground(COLOR_NOK);
                    } 
                    else 
                    {
                        list.setForeground(COLOR_OK);
                    }
                } 
                else 
                {    
                    list.setForeground(Color.BLACK);
                }
            }
            else if(value.getClass().equals(TestCaseErg.class)) 
            {
                if(!((TestCaseErg) value).isSuccessful()) 
                {
                    if(!((TestCaseErg) value).isAccepted()) 
                    {
                        list.setForeground(COLOR_NOK);
                    } 
                    else 
                    {
                        list.setForeground(COLOR_OK);
                    }
                } 
                else 
                {    
                    list.setForeground(Color.BLACK);
                }
            }
            else if(value.getClass().equals(CashpointErg.class)) 
            {
                if(!((CashpointErg) value).isSuccessful()) 
                {
                    if(!((CashpointErg) value).isAccepted()) 
                    {
                        list.setForeground(COLOR_NOK);
                    } 
                    else 
                    {
                        list.setForeground(COLOR_OK);
                    }
                } 
                else 
                {    
                    list.setForeground(Color.BLACK);
                }
            }
            else if(value.getClass().equals(Result.class)) 
            {
                if(!((Result) value).isSuccessful()) 
                {
                    if(!((Result) value).isAccepted()) 
                    {
                        list.setForeground(COLOR_NOK);
                    } 
                    else 
                    {
                        list.setForeground(COLOR_OK);
                    }
                } 
                else 
                {    
                    list.setForeground(Color.BLACK);
                }
            }
        }
        return lcr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    }    
}

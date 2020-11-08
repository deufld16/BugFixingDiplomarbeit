/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package settings.bl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Lukas Krobath
 */
public class TableCellRenderer extends DefaultTableCellRenderer{

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        c.setBackground(Color.WHITE);
        c.setFont(c.getFont().deriveFont(Font.PLAIN, 18));
        if(c instanceof JLabel){
            ((JLabel)c).setHorizontalAlignment(JLabel.CENTER);
        }
        if(isSelected){
            c.setBackground(new Color(57,105,138));
        }
        
        return c;
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.guiRenderer;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Florian
 */
public class ArticleTableRenderer extends DefaultTableCellRenderer {

    /**
     * Renderer for paArticleTable
     *
     * @param table
     * @param value
     * @param isSelected
     * @param hasFocus
     * @param row
     * @param column
     * @return
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel lb = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        int collindex = table.convertColumnIndexToModel(column);
        if (value instanceof Integer || value instanceof Double) {
            lb.setHorizontalAlignment(RIGHT);
        }
        if (collindex == 2) {
            lb.setText(String.format("%.2f €", (Double) value));
        } else if (collindex == 3) {
            lb.setText(String.format(String.format("%d", (Integer) value)) + " %");
        }
        return lb;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.guiRenderer;

import recorder.beans.Article;
import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import recorder.beans.Command;
import recorder.beans.Operation;

/**
 *
 * @author Florian
 */
public class DisplayListRenderer extends DefaultListCellRenderer {

    /**
     * 
     * Renderer for the cash point display list
     * 
     * @param list
     * @param value
     * @param index
     * @param isSelected
     * @param cellHasFocus
     * @return
     */
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel lb = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        lb.setBackground(Color.white);
        if (index % 2 != 0) {
            lb.setBackground(new Color(235, 235, 235));
        }
        if (value instanceof Article) {
            Article art = (Article) value;
            if ((art.isJugendSchutz() && !art.isJugendSchutzOk()) || (art.isEloading() && !art.isEloadingAmmountOk()) || art.isPriceZero()) {
                lb.setForeground(Color.red);
            }
        } else if (value instanceof Command) {
            Command command = (Command) value;
            if (command.isError()) {
                lb.setForeground(Color.red);
            }
        } else if (value instanceof Operation) {
            Operation operation = (Operation) value;
            if (operation.isIsError()) {
                lb.setForeground(Color.red);
            }
        }
        return lb;
    }

}

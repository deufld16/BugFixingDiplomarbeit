/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator.gui;

import general.beans.io_objects.CommandRun;
import general.beans.io_objects.ExplorerLayer;
import general.beans.io_objects.ProjectRun;
import general.beans.io_objects.TestCaseRun;
import general.beans.io_objects.TestGroupRun;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.nio.file.Paths;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

/**
 * Renders a overview list label
 *
 * @author Lukas Krobath
 */
public class GegenstandRenderer extends JLabel implements ListCellRenderer<ExplorerLayer> {

    @Override
    public Component getListCellRendererComponent(JList<? extends ExplorerLayer> list, ExplorerLayer value, int index, boolean isSelected, boolean cellHasFocus) {
        try {
            ImageIcon image;
            Font f = getFont();
            if (value instanceof ProjectRun) {
                image = general.io.Loader.loadLeafIcon("project.png", 20, 20);
                setFont(f.deriveFont(f.getStyle() | Font.BOLD));
            } else if (value instanceof TestGroupRun) {
                image = general.io.Loader.loadLeafIcon("testgroup.png", 20, 20);
                setFont(f.deriveFont(f.getStyle() | Font.BOLD));
            } else if (value instanceof TestCaseRun) {
                image = general.io.Loader.loadLeafIcon("testcase.png", 20, 20);
            } else {
                image = general.io.Loader.loadLeafIcon("command.png", 20, 20);
            }
            Image im = image.getImage();
//            im = im.getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            setIcon(new ImageIcon(im));
            setText(value.toString());
            setBorder(new EmptyBorder(3, 3, 3, 3));

            if (isSelected) {
                setOpaque(true);
                setBackground(Color.gray);
            } else {
                setOpaque(true);
                setBackground(Color.white);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return this;
    }
}

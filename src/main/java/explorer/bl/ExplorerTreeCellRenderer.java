/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package explorer.bl;

import general.beans.io_objects.CommandRun;
import general.beans.io_objects.ExplorerLayer;
import general.beans.io_objects.ProjectRun;
import general.beans.io_objects.TestCaseRun;
import general.beans.io_objects.TestGroupRun;
import java.awt.Component;
import java.io.File;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Class which is used as the renderer for the JTree. This class is resposible for seting the right icons depending on the class type of the
 * objects which are mapped/associated with the Rows/TreeNodes of the JTree
 * 
 * @author Anna Lechner & Florian Deutschmann
 */
public class ExplorerTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {   
        if (value instanceof DefaultMutableTreeNode) {
            value = ((DefaultMutableTreeNode) value).getUserObject();         
            if (value instanceof File) {
                value = ((File) value).getName();
            }
            try {
                if (!(value instanceof File) && !(value instanceof String)) {

                    if (((ExplorerLayer) value) instanceof TestGroupRun) {
                        setLeafIcon(general.io.Loader.loadLeafIcon("testgroup.png", 30, 30));
                        setOpenIcon(general.io.Loader.loadLeafIcon("testgroup.png", 30, 30));
                        setClosedIcon(general.io.Loader.loadLeafIcon("testgroup.png", 30, 30));
                    } else if (((ExplorerLayer) value) instanceof TestCaseRun) {
                        setLeafIcon(general.io.Loader.loadLeafIcon("testcase.png", 30, 30));
                        setOpenIcon(general.io.Loader.loadLeafIcon("testcase.png", 30, 30));
                        setClosedIcon(general.io.Loader.loadLeafIcon("testcase.png", 30, 30));
                    } else if (((ExplorerLayer) value) instanceof CommandRun) {
                        setLeafIcon(general.io.Loader.loadLeafIcon("command.png", 30, 30));
                        setOpenIcon(general.io.Loader.loadLeafIcon("command.png", 30, 30));
                        setClosedIcon(general.io.Loader.loadLeafIcon("command.png", 30, 30));
                    } else if (((ExplorerLayer) value) instanceof ProjectRun) {
                        setLeafIcon(general.io.Loader.loadLeafIcon("project.png", 30, 30));
                        setOpenIcon(general.io.Loader.loadLeafIcon("project.png", 30, 30));
                        setClosedIcon(general.io.Loader.loadLeafIcon("project.png", 30, 30));
                    }
                } else {
                    if (row == 0) {
                        setLeafIcon(general.io.Loader.loadLeafIcon("root.png", 30, 30));
                        setOpenIcon(general.io.Loader.loadLeafIcon("root.png", 30, 30));
                        setClosedIcon(general.io.Loader.loadLeafIcon("root.png", 30, 30));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    }

}

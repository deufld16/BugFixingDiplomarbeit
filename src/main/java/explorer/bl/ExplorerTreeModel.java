/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package explorer.bl;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 * Class which is an adapted version of the DefaultTreeModel with extra methods which are being needed for the Jtree to function as we need it to function
 * @author Florian Deutschmann
 */
public class ExplorerTreeModel extends DefaultTreeModel {

    public ExplorerTreeModel(TreeNode tn) {
        super(tn);
    }
    /**
     * Method, which newly sets the root and tells all listeners about this change in the structure
     * @param root the topmost treeNode
     */
    public void updateStructure(TreeNode root) {
        
        setRoot(root);
        fireTreeStructureChanged();
    }

    /**
     * Method to update the tree structure
     */
    public void fireTreeStructureChanged() {
        Object[] o = {root};
        TreeModelEvent e = new TreeModelEvent(this, o);
        for (TreeModelListener l : getTreeModelListeners()) {
            l.treeNodesChanged(e);
        }

    }
}

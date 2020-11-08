/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package explorer.bl;

import general.bl.GlobalAccess;
import general.bl.GlobalParamter;
import java.awt.MouseInfo;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Class which makes sure that the JPopUpMenu which is being used when rightclicking the JTree is always being called for the right row of the JTree
 * @author Florian Deutschmann
 */
public class ExplorerPopupMenuListener implements PopupMenuListener {

    /**
     * This method makes sure that if a certain row is selected but the rightclick (call of JPopUpMenu) happens on another row, that this specific row 
     * is selected and that the JPopUpMenu is called for this row
     * @param e 
     */
    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        if (GlobalParamter.getInstance().getTrExplorer().getSelectionRows().length <= 1) {
            int xCord = MouseInfo.getPointerInfo().getLocation().x - GlobalParamter.getInstance().getTrExplorer().getLocationOnScreen().x;
            int yCord = MouseInfo.getPointerInfo().getLocation().y - GlobalParamter.getInstance().getTrExplorer().getLocationOnScreen().y;
            int closestRow = GlobalParamter.getInstance().getTrExplorer().
                    getClosestRowForLocation(xCord, yCord);
            GlobalParamter.getInstance().getTrExplorer().setSelectionRow(closestRow);
            GlobalAccess.getInstance().getPaExplorer().generateTablePopupMenu((DefaultMutableTreeNode) GlobalParamter.
                    getInstance().getTrExplorer().getSelectionPath().getLastPathComponent());
        }
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
    }

}

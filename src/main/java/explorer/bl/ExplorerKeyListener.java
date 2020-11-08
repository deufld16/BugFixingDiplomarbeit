/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package explorer.bl;

import general.bl.GlobalAccess;
import general.bl.GlobalParamter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author Anna Lechner & Florian Deutschmann
 */
public class ExplorerKeyListener implements KeyListener {

    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * Method which adds the transferhandler again after the shift or control key
     * are being released (multi transfer is and should not be possible)
     *
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (!GlobalParamter.getInstance().isCtrl_key_down() || !GlobalParamter.getInstance().isShift_key_down()) {
            if (e.getKeyCode() == 16) {
                GlobalParamter.getInstance().setShift_key_down(true);
                GlobalParamter.getInstance().getTrExplorer().setTransferHandler(null);
            } else if (e.getKeyCode() == 17) {
                GlobalParamter.getInstance().setCtrl_key_down(true);
                GlobalParamter.getInstance().getTrExplorer().setTransferHandler(null);
            }
        }
    }

    /**
     * Method which checks if the key combination CTRL + S or CTRL + Z are being
     * used and creates/loads a savepoint -A savepoint is being used to save the
     * current state of the JTree This method also removes the TranfserHandler
     * on the JTtree if the CTRL or SHIFT key are being pressed because mutli
     * tranfer is and should not be possible
     *
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_D) {
            GlobalAccess.getInstance().getPaExplorer().closeProject();
        } else if (e.getKeyCode() == KeyEvent.VK_DELETE) {
            GlobalAccess.getInstance().getPaExplorer().removeMultiSelection();
        } else if ((e.getKeyCode() == KeyEvent.VK_R) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            GlobalAccess.getInstance().getPaExplorer().displayRun();
        } else if ((e.getKeyCode() == KeyEvent.VK_U) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            GlobalAccess.getInstance().getPaExplorer().displayRecorder();
        }
        if (e.getKeyCode() == 16) {
            GlobalParamter.getInstance().setShift_key_down(false);
            if (GlobalParamter.getInstance().getTrExplorer().getTransferHandler() == null) {
                GlobalParamter.getInstance().getTrExplorer().setTransferHandler(GlobalAccess.getInstance().getPaExplorer().getEtth());
            }
        }
        if (e.getKeyCode() == 17) {
            GlobalParamter.getInstance().setCtrl_key_down(false);
            if (GlobalParamter.getInstance().getTrExplorer().getTransferHandler() == null) {
                GlobalParamter.getInstance().getTrExplorer().setTransferHandler(GlobalAccess.getInstance().getPaExplorer().getEtth());
            }
        }
    }
    
}

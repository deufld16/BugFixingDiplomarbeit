/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.bl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JOptionPane;

/**
 * This class is a keyListener which can be used within the entire programm
 * @author Florian Deutschmann
 */
public class ContainerKeyListener implements KeyListener {

    @Override
    public void keyTyped(KeyEvent e) {
    }
    /**
     * KeyPressed Event that contains all ShortCuts and Keycombination that are needed within the entire programm
     * @param e 
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_H) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
            if (GlobalAccess.getInstance().isHidden()) {
                GlobalAccess.getInstance().setHidden(false);
                if (GlobalAccess.getInstance().getTest_ide_main_frame().getContentPane().getSize().width < 1250) {
                    GlobalAccess.getInstance().getTest_ide_main_frame().getPaSideBar().setSize(50,
                            GlobalAccess.getInstance().getTest_ide_main_frame().getContentPane().getSize().height);
                } else {
                    GlobalAccess.getInstance().getTest_ide_main_frame().getPaSideBar().setSize(75,
                            GlobalAccess.getInstance().getTest_ide_main_frame().getContentPane().getSize().height);
                }
                GlobalAccess.getInstance().getTest_ide_main_frame().add(BorderLayout.WEST, GlobalAccess.getInstance().getTest_ide_main_frame().getPaSideBar());
                resizeComponents();
                GlobalMethods.getInstance().updateMainFrame();
            } else {
                System.out.println("Component Removed - boolean set to false");
                Component compToRemove = null;
                for (Component component : GlobalAccess.getInstance().getTest_ide_main_frame().getContentPane().getComponents()) {
                    if (component.getName() != null) {
                        if (component.getName().equalsIgnoreCase("sideBarPanel")) {
                            compToRemove = component;
                        }
                    }
                }
                if (compToRemove != null) {
                    GlobalAccess.getInstance().getTest_ide_main_frame().remove(compToRemove);
                } else {
                    JOptionPane.showMessageDialog(GlobalAccess.getInstance().getTest_ide_main_frame(), "Error - Sidebar could not be removed");
                }
                GlobalAccess.getInstance().setHidden(true);
                resizeComponents();
                GlobalMethods.getInstance().updateMainFrame();
            }
        }
    }

    /**
     * Method to resize the components
     */
    private void resizeComponents() {
        if (GlobalAccess.getInstance().isHidden()) {
            if (GlobalAccess.getInstance().getTest_ide_main_frame().getPaDynamic().getComponents().length != 0) {
                GlobalAccess.getInstance().getTest_ide_main_frame().getPaDynamic().
                        setSize(GlobalAccess.getInstance().getTest_ide_main_frame().getContentPane().getSize().width,
                                GlobalAccess.getInstance().getTest_ide_main_frame().getContentPane().getSize().height);
                GlobalAccess.getInstance().getTest_ide_main_frame().getPaDynamic().getComponents()[0].setSize(
                        GlobalAccess.getInstance().getTest_ide_main_frame().getContentPane().getSize().width,
                        GlobalAccess.getInstance().getTest_ide_main_frame().getContentPane().getSize().height);
            }
        } else {
            if (GlobalAccess.getInstance().getTest_ide_main_frame().getContentPane().getSize().width < 1250) {
                GlobalAccess.getInstance().getTest_ide_main_frame().getPaDynamic().
                        setSize(GlobalAccess.getInstance().getTest_ide_main_frame().getContentPane().getSize().width - 60,
                                GlobalAccess.getInstance().getTest_ide_main_frame().getContentPane().getSize().height);
                GlobalAccess.getInstance().getTest_ide_main_frame().getPaDynamic().getComponents()[0].
                        setSize(GlobalAccess.getInstance().getTest_ide_main_frame().getContentPane().getSize().width - 60,
                                GlobalAccess.getInstance().getTest_ide_main_frame().getContentPane().getSize().height);
            } else {
                GlobalAccess.getInstance().getTest_ide_main_frame().getPaDynamic().
                        setSize(GlobalAccess.getInstance().getTest_ide_main_frame().getContentPane().getSize().width - 85,
                                GlobalAccess.getInstance().getTest_ide_main_frame().getContentPane().getSize().height);
                GlobalAccess.getInstance().getTest_ide_main_frame().getPaDynamic().
                        getComponents()[0].setSize(GlobalAccess.getInstance().getTest_ide_main_frame().getContentPane().getSize().width - 85,
                                GlobalAccess.getInstance().getTest_ide_main_frame().getContentPane().getSize().height);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.beans;

import java.awt.Component;

/**
 *
 * @author Florian Deutschmann
 */
public class GuiComponent {
    private int minWidth;
    private int minHeight;
    private int maxWidth;
    private int maxHeight;
    private int widthPercent;
    private int heightPercent;
    private Component component;

    public GuiComponent(int minWidth, int minHeight, int maxWidth, int maxHeight, int widthPercent, int heightPercent, Component component) {
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.widthPercent = widthPercent;
        this.heightPercent = heightPercent;
        this.component = component;
    }


    
    public int getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(int minWidth) {
        this.minWidth = minWidth;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }

    public int getWidthPercent() {
        return widthPercent;
    }

    public void setWidthPercent(int widthPercent) {
        this.widthPercent = widthPercent;
    }

    public int getHeightPercent() {
        return heightPercent;
    }

    public void setHeightPercent(int heightPercent) {
        this.heightPercent = heightPercent;
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }
    
    
    
}

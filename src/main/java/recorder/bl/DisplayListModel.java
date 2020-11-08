/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.bl;

import recorder.beans.Article;
import recorder.guiOperations.GUIOperations;
import java.util.LinkedList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.JOptionPane;
import recorder.beans.Karte;

/**
 *
 * @author Florian
 */
public class DisplayListModel extends AbstractListModel<Object> {

    private List<Object> displayedObjects = new LinkedList<>();
    private List<Object> removedObjects = new LinkedList<>();

    private int amount = 1;

    /**
     * Method to add one or more articles to the display list
     *
     * @param a hinzuzufügender Artikel
     */
    public void addArtikel(Article a) {

        if (!GUIOperations.isPayProcessStarted()) {
            a.setAmount(amount);
            displayedObjects.add(a);
            this.fireContentsChanged(this, 0, displayedObjects.size() - 1);
            amount = 1;
        }else{
            JOptionPane.showMessageDialog(null, "Der Bezahlungsprozess wurde bereits gestartet - "
                    + "Artikel kann nicht hinzugefügt werden");
        }
    }

    /**
     * Method to add functions to the display list
     *
     * @param o hinzuzufügendes Objekt
     */
    public void addObject(Object o) {
        displayedObjects.add(o);
        this.fireContentsChanged(this, 0, displayedObjects.size() - 1);
    }

    /**
     * Method to remove one or more articles from the display list
     *
     * @param art zu entfernender Artikel
     */
    public void remArtikel(Article art) {
        removedObjects.add(art);
        this.fireContentsChanged(this, 0, displayedObjects.size() - 1);
    }
    
    /**
     * Method to remove a card from the display list
     * @param card 
     */
    public void remCard(Karte card){
        removedObjects.add(card);
        this.fireContentsChanged(this, 0, displayedObjects.size() - 1);
    }

    /**
     * Method to clear the display list
     *
     */
    public void clear() {
        displayedObjects.clear();
        this.fireContentsChanged(this, 0, displayedObjects.size() - 1);
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public List<Object> getRemovedObjects() {
        return removedObjects;
    }

    public void setRemovedObjects(List<Object> removedObjects) {
        this.removedObjects = removedObjects;
    }

    @Override
    public int getSize() {
        return displayedObjects.size();
    }

    @Override
    public Object getElementAt(int index) {
        return displayedObjects.get(index);
    }

    public List<Object> getDisplayedArtikel() {
        return displayedObjects;
    }

    public void setDisplayedArtikel(List<Object> displayedArtikel) {
        this.displayedObjects = displayedArtikel;
    }
}

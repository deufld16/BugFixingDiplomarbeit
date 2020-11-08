/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dashboard.enums;

/**
 *
 * @author A180953
 */
public enum ChangeType {
    DELETED(1, "Added"),
    CHANGED(2, "Deleted"),
    MOVED(3, "Moved"),
    CHILD_STATECHANGED(4, "ChildStateChanged"),
    ADDED(5, "Changed");
    
    private int type;
    private String identifier;
    
    private ChangeType(int type, String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    
    
}

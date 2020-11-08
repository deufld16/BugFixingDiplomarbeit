/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.beans;

import java.util.HashMap;
import java.util.Objects;

/**
 *
 * @author KROBATHLukas
 *
 * Data class for creating the XML file
 */
public class Command {

    private String commandName;
    private HashMap<String, Object> attributes;
    private String displayText;
    private boolean isTextNode = false;
    private String textNodeContent;
    private boolean error = false;

    public Command(String commandName, HashMap<String, Object> attributes) {
        this.commandName = commandName;
        this.attributes = attributes;
        this.displayText = "Command Object";
    }

    public Command(String commandName, HashMap<String, Object> attributes, String displayText) {
        this.commandName = commandName;
        this.attributes = attributes;
        this.displayText = displayText;
    }

    public Command(String commandname) {
        this.commandName = commandname;
        this.attributes = new HashMap<>();
        this.displayText = "Command Object";
    }

    public Command(String commandname, boolean isTextNode, String textNodeContent) {
        this.commandName = commandname;
        this.isTextNode = isTextNode;
        this.attributes = new HashMap<>();
        this.textNodeContent = textNodeContent;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public HashMap<String, Object> getAttributes() {
        return attributes;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setAttributes(HashMap<String, Object> attributes) {
        this.attributes = attributes;
    }

    public boolean isIsTextNode() {
        return isTextNode;
    }

    public void setIsTextNode(boolean isTextNode) {
        this.isTextNode = isTextNode;
    }

    public String getTextNodeContent() {
        return textNodeContent;
    }

    public void setTextNodeContent(String textNodeContent) {
        this.textNodeContent = textNodeContent;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Command other = (Command) obj;
        if (!this.commandName.equals(other.commandName)) {
            return false;
        }
        if (!Objects.equals(this.attributes, other.attributes)) {
            return false;
        }
        return true;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    @Override
    public String toString() {
        return displayText;
    }

}

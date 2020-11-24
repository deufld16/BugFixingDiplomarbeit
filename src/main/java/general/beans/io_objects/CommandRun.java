/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.beans.io_objects;

import dashboard.beans.Durchlaufgegenstand;
import java.nio.file.Path;
import java.util.Objects;
import org.w3c.dom.NodeList;

/**
 * Class that represents a Command in the file structure before the simulation
 * process
 *
 * @author Maxiilian Strohmaier
 */
public class CommandRun extends ExplorerLayer {

    private String className;
    private NodeList nodeList;
    private String displayName;

    public CommandRun(String className, NodeList nodeList, String description, Path path) {
        super(description, path);
        this.className = className;
        this.nodeList = nodeList;
        System.out.println("Ich bin neu 4");
    }

    public CommandRun(String className, NodeList nodeList, String description, Path path, Durchlaufgegenstand durchlaufgegenstand) {
        super(description, path, durchlaufgegenstand);
        this.className = className;
        this.nodeList = nodeList;
        System.out.println("Ich bin neu");
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public NodeList getNodeList() {
        return nodeList;
    }

    public void setNodeList(NodeList nodeList) {
        this.nodeList = nodeList;
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
        final CommandRun other = (CommandRun) obj;
        if (!Objects.equals(this.className, other.className)) {
            return false;
        }
        if (!Objects.equals(this.nodeList, other.nodeList)) {
            return false;
        }
        if (this.getPath() != other.getPath()) {
            return false;
        }

        if (!this.getDescription().equalsIgnoreCase(other.getDescription())) {
            return false;
        }
        return true;
    }

}

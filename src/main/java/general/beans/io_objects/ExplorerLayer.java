/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.beans.io_objects;

import dashboard.beans.DurchlaufgegenstandNew;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Base Class to represent a layer in the file structure
 *
 * @author Maximilian Strohmaier
 */
public class ExplorerLayer {

    protected String description;
    protected Path path;
    protected boolean isExpanded = false;
    protected DurchlaufgegenstandNew durchlauf_gegenstand;

    public ExplorerLayer(String description, Path path, DurchlaufgegenstandNew durchlauf_gegenstand) {
        this.description = description;
        this.path = path;
        this.durchlauf_gegenstand = durchlauf_gegenstand;
    }

    public ExplorerLayer(String description, Path path) {
        this.description = description;
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        System.out.println(durchlauf_gegenstand);
        if (durchlauf_gegenstand != null) {
            durchlauf_gegenstand.setBezeichnung(description);
        }
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return description;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    public DurchlaufgegenstandNew getDurchlauf_gegenstand() {
        return durchlauf_gegenstand;
    }

    public void setDurchlauf_gegenstand(DurchlaufgegenstandNew durchlauf_gegenstand) {
        this.durchlauf_gegenstand = durchlauf_gegenstand;
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
        final ExplorerLayer other = (ExplorerLayer) obj;
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.path, other.path)) {
            return false;
        }
        return true;
    }

    public boolean isIsExpanded() {
        return isExpanded;
    }

    public void setIsExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.beans.io_objects;

import dashboard.beans.Durchlaufgegenstand;
import dashboard.beans.Projekt;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Lukas Krobath
 */
public class ProjectRun extends ExplorerLayer{
    private List<TestGroupRun> testgroups;
    
    public ProjectRun(String description, Path path, Durchlaufgegenstand durchlaufgegenstand) {
        super(description, path, durchlaufgegenstand);
        this.testgroups = new ArrayList<>();
    }
        public ProjectRun(String description, Path path) {
        super(description, path);
        this.testgroups = new ArrayList<>();
    }

    public List<TestGroupRun> getTestgroups() {
        return testgroups;
    }

    public void setTestgroups(List<TestGroupRun> testgroups) {
        this.testgroups = testgroups;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final ProjectRun other = (ProjectRun) obj;
        if (!Objects.equals(this.testgroups, other.testgroups)) {
            return false;
        }
        return true;
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer.beans;

import analyzer.enums.CashpointGroup;
import general.beans.io_objects.ExplorerLayer;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Class that represents a cashpoint in the file structure after the simulation process
 * 
 * @author Maximilian Strohmaier
 */
public class CashpointErg extends ExplorerLayer{
    
    private List<Result> results;
    private CashpointGroup group;
    private boolean successful;
    private boolean accepted;

    public CashpointErg(List<Result> results, CashpointGroup group, String description, Path path) {
        super(description, path);
        this.results = results;
        this.group = group;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public CashpointGroup getGroup() {
        return group;
    }

    public void setGroup(CashpointGroup group) {
        this.group = group;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
    
    public void acceptAllChildren(boolean accepted) {
        for (Result result : results) {
            result.setAccepted(accepted);
            result.acceptAllChildren(accepted);
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.results);
        hash = 37 * hash + Objects.hashCode(this.group);
        hash = 37 * hash + (this.successful ? 1 : 0);
        hash = 37 * hash + (this.accepted ? 1 : 0);
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
        final CashpointErg other = (CashpointErg) obj;
        if (this.successful != other.successful) {
            return false;
        }
        if (this.accepted != other.accepted) {
            return false;
        }
        if (!Objects.equals(this.results, other.results)) {
            return false;
        }
        if (this.group != other.group) {
            return false;
        }
        return true;
    }
}

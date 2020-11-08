/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer.beans;

import java.util.List;
import java.util.Objects;

/**
 * This class is used to store informaion returned by the simulaion process
 * and whether it was successful or not.
 * It is a virtual layer in the project structure that allows the bundling
 * process and further the graphical representation of several executed 
 * commands (e.g., recorders) within the same testcase
 * 
 * @author Maximilian Strohmaier
 */
public class Result implements Comparable<Result> {
    
    private String marker;
    private List<ResultType> types;
    private boolean successful;
    private boolean accepted;

    public Result(String marker, List<ResultType> types) {
        this.marker = marker;
        this.types = types;
    }

    public List<ResultType> getTypes() {
        return types;
    }

    public void setTypes(List<ResultType> types) {
        this.types = types;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
    
    public void acceptAllChildren(boolean Accepted) {
        for (ResultType type : types) {
            type.setAccepted(accepted);
        }
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    @Override
    public String toString() {
        return marker;
    }    

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + Objects.hashCode(this.marker);
        hash = 13 * hash + Objects.hashCode(this.types);
        hash = 13 * hash + (this.successful ? 1 : 0);
        hash = 13 * hash + (this.accepted ? 1 : 0);
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
        final Result other = (Result) obj;
        if (this.successful != other.successful) {
            return false;
        }
        if (this.accepted != other.accepted) {
            return false;
        }
        if (!Objects.equals(this.marker, other.marker)) {
            return false;
        }
        if (!Objects.equals(this.types, other.types)) {
            return false;
        }
        return true;
    }    

    @Override
    public int compareTo(Result o) {
        return marker.compareTo(o.getMarker());
    }

}

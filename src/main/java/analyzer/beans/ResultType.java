/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer.beans;

import analyzer.enums.ResultFileType;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class is to store a type for a result that could be returned from 
 * a simulation process and whether it was successful or not
 * 
 * @author Maximilian Strohmaier
 */
public class ResultType {
     private ResultFileType resultFileType;
     private List<String> lines;
     private Map<String, List<String>> differences;
     private ReferenceFile reference;
     private boolean successful;
     private boolean accepted;

    public ResultType() {
    }

    public ResultType(ResultFileType resultFileType, boolean successful) {
        this.resultFileType = resultFileType;
        this.successful = successful;
    }   

    public ResultType(ResultFileType resultFileType, List<String> lines, ReferenceFile reference) {
        this.resultFileType = resultFileType;
        this.lines = lines;
        this.reference = reference;
    }

    public ResultFileType getResultFileType() {
        return resultFileType;
    }

    public void setResultFileType(ResultFileType resultFileType) {
        this.resultFileType = resultFileType;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public ReferenceFile getReference() {
        return reference;
    }

    public void setReference(ReferenceFile reference) {
        this.reference = reference;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public Map<String, List<String>> getDifferences() {
        return differences;
    }

    public void setDifferences(Map<String, List<String>> differences) {
        this.differences = differences;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.resultFileType);
        hash = 53 * hash + Objects.hashCode(this.lines);
        hash = 53 * hash + Objects.hashCode(this.differences);
        hash = 53 * hash + Objects.hashCode(this.reference);
        hash = 53 * hash + (this.successful ? 1 : 0);
        hash = 53 * hash + (this.accepted ? 1 : 0);
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
        final ResultType other = (ResultType) obj;
        if (this.successful != other.successful) {
            return false;
        }
        if (this.accepted != other.accepted) {
            return false;
        }
        if (this.resultFileType != other.resultFileType) {
            return false;
        }
        if (!Objects.equals(this.lines, other.lines)) {
            return false;
        }
        if (!Objects.equals(this.differences, other.differences)) {
            return false;
        }
        if (!Objects.equals(this.reference, other.reference)) {
            return false;
        }
        return true;
    }
}

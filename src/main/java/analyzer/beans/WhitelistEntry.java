/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer.beans;

import analyzer.enums.ResultFileType;
import java.util.List;
import java.util.Objects;

/**
 * Class that represents an entry in the whitelist
 * 
 * @author Maximilian Strohmaier
 */
public class WhitelistEntry {
    private String description;
    private String regex;
    private List<ResultFileType> applicationTypes;
    
    public WhitelistEntry(String description, String regex, List<ResultFileType> applicationTypes) {
        this.description = description;
        this.regex = regex;
        this.applicationTypes = applicationTypes;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public List<ResultFileType> getApplicationTypes() {
        return applicationTypes;
    }

    public void setApplicationTypes(List<ResultFileType> applicationTypes) {
        this.applicationTypes = applicationTypes;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.description);
        hash = 53 * hash + Objects.hashCode(this.regex);
        hash = 53 * hash + Objects.hashCode(this.applicationTypes);
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
        final WhitelistEntry other = (WhitelistEntry) obj;
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.regex, other.regex)) {
            return false;
        }
        if (!Objects.equals(this.applicationTypes, other.applicationTypes)) {
            return false;
        }
        return true;
    }

    
}
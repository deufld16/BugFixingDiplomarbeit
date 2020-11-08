/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer.beans;

import general.beans.io_objects.ExplorerLayer;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * Class to store the reference file during the analysis
 * 
 * @author Maximilian Strohmaier
 */
public class ReferenceFile extends ExplorerLayer {
    
    private List<String> lines;

    public ReferenceFile(List<String> lines, String description, Path path) {
        super(description, path);
        this.lines = lines;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.lines);
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
        final ReferenceFile other = (ReferenceFile) obj;
        if (!Objects.equals(this.lines, other.lines)) {
            return false;
        }
        return true;
    }
    
    
    
}

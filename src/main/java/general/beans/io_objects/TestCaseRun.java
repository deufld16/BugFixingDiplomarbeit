/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.beans.io_objects;

import dashboard.beans.DurchlaufgegenstandNew;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Class that represents a Test Case in the file structure before the simulation
 * process
 *
 * @author Maximilian Strohmaier
 */
public class TestCaseRun extends ExplorerLayer {

    private List<CommandRun> commands = new LinkedList<>();

    public TestCaseRun(String description, Path path) {
        super(description, path);
    }

    public TestCaseRun(String description, Path path, DurchlaufgegenstandNew durchlaufgegenstand) {
        super(description, path, durchlaufgegenstand);
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
        for (CommandRun cr : commands) {
            cr.setPath(path);
        }
    }

    public TestCaseRun(List<CommandRun> commands, String description, Path path) {
        super(description, path);
        this.commands = commands;
    }

    public List<CommandRun> getCommands() {
        return commands;
    }

    public void setCommands(List<CommandRun> commands) {
        this.commands = commands;
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
        final TestCaseRun other = (TestCaseRun) obj;
        if (!Objects.equals(this.commands, other.commands)) {
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

    @Override
    public String toString() {
        if (path.getFileName().toString().toUpperCase().contains("TC")) {
            return path.getFileName().toString();
        } else {
            return description;
        }
    }

}

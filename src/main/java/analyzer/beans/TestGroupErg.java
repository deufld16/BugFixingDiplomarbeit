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
 * Class that represents a test group in the file structure after the simulation process
 * 
 * @author Maximilian Strohmaier
 */
public class TestGroupErg extends ExplorerLayer {

    private List<TestCaseErg> testcases;
    private boolean successful;
    private boolean accepted;

    public TestGroupErg(List<TestCaseErg> testcases, boolean successful, String description, Path path) {
        super(description, path);
        this.testcases = testcases;
        this.successful = successful;
    }

    public TestGroupErg(List<TestCaseErg> testcases, String description, Path path) {
        super(description, path);
        this.testcases = testcases;
    }

    public List<TestCaseErg> getTestcases() {
        return testcases;
    }

    public void setTestcases(List<TestCaseErg> testcases) {
        this.testcases = testcases;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    @Override
    public String toString() {
        return description;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
    
    public void acceptAllChildren(boolean accepted) {
        for (TestCaseErg testcase : testcases) {
            testcase.setAccepted(accepted);
            testcase.acceptAllChildren(accepted);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.testcases);
        hash = 79 * hash + (this.successful ? 1 : 0);
        hash = 79 * hash + (this.accepted ? 1 : 0);
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
        final TestGroupErg other = (TestGroupErg) obj;
        if (this.successful != other.successful) {
            return false;
        }
        if (this.accepted != other.accepted) {
            return false;
        }
        if (!Objects.equals(this.testcases, other.testcases)) {
            return false;
        }
        return true;
    }
    
}

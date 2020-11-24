/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package general.beans.io_objects;

import dashboard.beans.Durchlaufgegenstand;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Class that represents a Test Group in the file structure before the
 * simulation process
 *
 * @author Maximilian Strohmaier
 */
public class TestGroupRun extends ExplorerLayer {

    private int empId; //Bedienernummer
    private int tllId; //Ladennummer
    private int password; //Bedienerpasswort
    private List<TestCaseRun> testCases = new LinkedList<>();

    public TestGroupRun(String description, Path path) {
        super(description, path);
    }

    public TestGroupRun(String description, Path path, Durchlaufgegenstand durchlaufgegenstand) {
        super(description, path, durchlaufgegenstand);
    }

    public TestGroupRun(int empId, int tllId, int password, List<TestCaseRun> testCases, String description, Path path) {
        super(description, path);
        this.empId = empId;
        this.tllId = tllId;
        this.password = password;
        this.testCases = testCases;
    }
    
        public TestGroupRun(int empId, int tllId, int password, List<TestCaseRun> testCases, String description, Path path, Durchlaufgegenstand durchlaufgegenstand) {
        super(description, path, durchlaufgegenstand);
        this.empId = empId;
        this.tllId = tllId;
        this.password = password;
        this.testCases = testCases;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public int getTllId() {
        return tllId;
    }

    public void setTllId(int tllId) {
        this.tllId = tllId;
    }

    public int getPassword() {
        return password;
    }

    public void setPassword(int password) {
        this.password = password;
    }

    public List<TestCaseRun> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCaseRun> testCases) {
        this.testCases = testCases;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
        for (TestCaseRun tc : testCases) {
            tc.setPath(Paths.get(path.toString(), tc.getPath().getFileName().toString()));
        }
    }

    public boolean isIsExpanded() {
        return isExpanded;
    }

    public void setIsExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
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
        final TestGroupRun other = (TestGroupRun) obj;
        if (this.empId != other.empId) {
            return false;
        }
        if (this.tllId != other.tllId) {
            return false;
        }
        if (this.password != other.password) {
            return false;
        }
        if (!Objects.equals(this.testCases, other.testCases)) {
            return false;
        }

        if (this.path != other.path) {
            return false;
        }

        if (!this.description.equalsIgnoreCase(other.description)) {
            return false;
        }
        return true;
    }
}

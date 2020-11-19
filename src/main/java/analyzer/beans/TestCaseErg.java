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
 * Class that represents a test case in the file structure after the simulation
 * process
 *
 * @author Maximilian Strohmaier
 */
public class TestCaseErg extends ExplorerLayer {

    private List<CashpointErg> cashpoints;
    private boolean successful;
    private boolean accepted;

    public TestCaseErg(List<CashpointErg> cashpoints, boolean successful, String description, Path path) {
        super(description, path);
        this.cashpoints = cashpoints;
        this.successful = successful;
    }

    public TestCaseErg(List<CashpointErg> cashpoints, String description, Path path) {
        super(description, path);
        this.cashpoints = cashpoints;
    }

    public List<CashpointErg> getCashpoints() {
        return cashpoints;
    }

    public void setCashpoints(List<CashpointErg> cashpoints) {
        this.cashpoints = cashpoints;
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

    public void acceptAllChildren(boolean accepted) {
        for (CashpointErg cp : cashpoints) {
            cp.setAccepted(accepted);
            cp.acceptAllChildren(accepted);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.cashpoints);
        hash = 79 * hash + (this.successful ? 1 : 0);
        hash = 79 * hash + (this.accepted ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestCaseErg other = (TestCaseErg) obj;
        if (this.successful != other.successful) {
            return false;
        }
        if (this.accepted != other.accepted) {
            return false;
        }
        if (!Objects.equals(this.cashpoints, other.cashpoints)) {
            return false;
        }
        return true;
    }
}

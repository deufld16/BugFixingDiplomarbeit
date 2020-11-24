/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dashboard.beans;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author flori
 */
@Entity
@Table(name = "testcase")
@NamedQueries({
    @NamedQuery(name = "TestCase.selectAll", query = "SELECT tc FROM TestCase tc"),})
public class TestCase extends Durchlaufgegenstand {

    @OneToMany(mappedBy = "testCase", cascade = CascadeType.ALL)
    private List<Command> commands = new LinkedList<>();

    @ManyToOne
    @JoinColumn(name = "testGruppe", nullable = false)
    private Testgruppe testGruppe;

    public TestCase() {
    }

    public TestCase(String bezeichnung) {
        super(bezeichnung);
    }

    public TestCase(String bezeichnung, LocalDate erstelldatum, int deleted) {
        super(bezeichnung, erstelldatum, deleted);
    }

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    public Testgruppe getTestGruppe() {
        return testGruppe;
    }

    public void setTestGruppe(Testgruppe testGruppe) {
        this.testGruppe = testGruppe;
        this.testGruppe.getTestCases().add(this);
    }

    
}

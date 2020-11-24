/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dashboard.beans;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.eclipse.persistence.annotations.CascadeOnDelete;

/**
 *
 * @author flori
 */
@Entity
@Table(name = "command")
@NamedQueries({
    @NamedQuery(name = "Command.selectAll", query = "SELECT c FROM Command c"),
})
public class Command extends Durchlaufgegenstand {

    @ManyToOne
    @JoinColumn(name = "testCase", nullable = false)
    private TestCase testCase;

    public Command() {
    }
    
    public Command(String bezeichnung){
        super(bezeichnung);
    }

    public Command(String bezeichnung, LocalDate erstelldatum, int deleted) {
        super(bezeichnung, erstelldatum, deleted);
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public void setTestCase(TestCase testCase) {
        this.testCase = testCase;
        this.testCase.getCommands().add(this);
    }
    
    
}

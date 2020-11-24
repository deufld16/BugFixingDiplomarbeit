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
 * @author Florian Deutschmann
 */
@Entity
@Table(name = "testgruppe")
@NamedQueries({
    @NamedQuery(name = "Testgruppe.selectAll", query = "SELECT tg FROM Testgruppe tg"),
    @NamedQuery(name = "Testgruppe.doesNotContain", query = "SELECT tg FROM Testgruppe tg JOIN Projekt p WHERE tg IN(:testgruppen) AND p = :proj"),
})
public class Testgruppe extends Durchlaufgegenstand {

    @OneToMany(mappedBy = "testGruppe", cascade = CascadeType.ALL)
    private List<TestCase> testCases = new LinkedList<>();

    @ManyToOne
    @JoinColumn(name = "projekt", nullable = false)
    private Projekt projekt;

    public Testgruppe() {
    }
    
    public Testgruppe(String bezeichnung){
        super(bezeichnung);
    }

    public Testgruppe(String bezeichnung, LocalDate erstelldatum, int deleted) {
        super(bezeichnung, erstelldatum, deleted);
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    public Projekt getProjekt() {
        return projekt;
    }

    public void setProjekt(Projekt projekt) {
        this.projekt = projekt;
        this.projekt.getTestgruppen().add(this);
    }
    
    
}

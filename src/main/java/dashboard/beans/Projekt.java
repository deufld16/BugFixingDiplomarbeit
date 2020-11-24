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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author flori
 */
@Entity
@Table(name = "projekt")
@NamedQueries({
    @NamedQuery(name = "Projekt.selectAll", query = "SELECT p FROM Projekt p"),
})
public class Projekt extends Durchlaufgegenstand{

    @OneToMany(mappedBy = "projekt", cascade = CascadeType.ALL)
    private List<Testgruppe> testgruppen = new LinkedList<>();
    
    public Projekt() {
    }
    
    public Projekt(String bezeichnung){
        super(bezeichnung);
    }
    
    public Projekt(String bezeichnung, LocalDate erstelldatum, int deleted) {
        super(bezeichnung, erstelldatum, deleted);
    }

    public List<Testgruppe> getTestgruppen() {
        return testgruppen;
    }

    public void setTestgruppen(List<Testgruppe> testgruppen) {
        this.testgruppen = testgruppen;
    }
}

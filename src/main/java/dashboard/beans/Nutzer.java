/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dashboard.beans;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Florian Deutschmann
 */
@Entity
@Table(name = "nutzer")
@NamedQueries({
    @NamedQuery(name = "NutzerNew.selectAll", query = "SELECT n FROM Nutzer n")
})
public class Nutzer implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int nutzerid;
    
    @Column(length = 255, nullable = false)
    private String username;

    @OneToMany(mappedBy = "nutzer", cascade = CascadeType.ALL)
    private List<Durchlauf> allDurchlauf = new LinkedList<>();
    
    @OneToMany(mappedBy = "nutzer", cascade = CascadeType.ALL)
    private List<Change> allAenderungen = new LinkedList<>();
    
    public Nutzer(){
    }
    
    public Nutzer(String username) {
        this.username = username;
    }
    
    public int getNutzerid() {
        return nutzerid;
    }

    public void setNutzerid(int nutzerid) {
        this.nutzerid = nutzerid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return username;
    }

    public List<Durchlauf> getAllDurchlauf() {
        return allDurchlauf;
    }

    public void setAllDurchlauf(List<Durchlauf> allDurchlauf) {
        this.allDurchlauf = allDurchlauf;
    }

    public List<Change> getAllAenderungen() {
        return allAenderungen;
    }

    public void setAllAenderungen(List<Change> allAenderungen) {
        this.allAenderungen = allAenderungen;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.username);
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
        final Nutzer other = (Nutzer) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        return true;
    }

    
}

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

/**
 *
 * @author flori
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "NutzerNew.selectAll", query = "SELECT n FROM NutzerNew n")
})
public class NutzerNew implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int nutzerid;
    
    @Column(length = 255, nullable = false)
    private String username;

    @OneToMany(mappedBy = "nutzer", cascade = CascadeType.ALL)
    private List<DurchlaufNew> allDurchlauf = new LinkedList<>();
    
    @OneToMany(mappedBy = "nutzer", cascade = CascadeType.ALL)
    private List<ChangeNew> allAenderungen = new LinkedList<>();
    
    public NutzerNew(){
    }
    
    public NutzerNew(String username) {
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

    public List<DurchlaufNew> getAllDurchlauf() {
        return allDurchlauf;
    }

    public void setAllDurchlauf(List<DurchlaufNew> allDurchlauf) {
        this.allDurchlauf = allDurchlauf;
    }

    public List<ChangeNew> getAllAenderungen() {
        return allAenderungen;
    }

    public void setAllAenderungen(List<ChangeNew> allAenderungen) {
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
        final NutzerNew other = (NutzerNew) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        return true;
    }

    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dashboard.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Florian Deutschmann
 */
@Entity
@Table(name = "durchlaufgegenstand")
@Inheritance(strategy = InheritanceType.JOINED)
public class Durchlaufgegenstand implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int gegenstandid;

    @Column(nullable = false, length = 255)
    private String bezeichnung;
    @Column(nullable = false)
    private LocalDate erstelldatum;
    @Column(nullable = false)
    private int deleted;

    @ManyToMany(mappedBy = "gegenstand", cascade = CascadeType.ALL)
    private List<Durchlauf> allDurchlauf = new LinkedList<>();

    @OneToMany(mappedBy = "gegenstand", cascade = CascadeType.ALL)
    private List<Change> allAenderungen = new LinkedList<>();

    public Durchlaufgegenstand() {

    }
    
    public Durchlaufgegenstand(String bezeichnung){
        this.bezeichnung = bezeichnung;
    }

    public Durchlaufgegenstand(String bezeichnung, LocalDate erstelldatum, int deleted) {
        this.bezeichnung = bezeichnung;
        this.erstelldatum = erstelldatum;
        this.deleted = deleted;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public int getGegenstandid() {
        return gegenstandid;
    }

    public void setGegenstandid(int gegenstandid) {
        this.gegenstandid = gegenstandid;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public LocalDate getErstelldatum() {
        return erstelldatum;
    }

    public void setErstelldatum(LocalDate erstelldatum) {
        this.erstelldatum = erstelldatum;
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
        hash = 37 * hash + Objects.hashCode(this.bezeichnung);
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
        final Durchlaufgegenstand other = (Durchlaufgegenstand) obj;
        if (!Objects.equals(this.bezeichnung, other.bezeichnung)) {
            return false;
        }
        return true;
    }

    
    
    @Override
    public String toString() {
        return gegenstandid + " - " + bezeichnung;
    }

}

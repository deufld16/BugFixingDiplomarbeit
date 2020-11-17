/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dashboard.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author flori
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "DurchlaufNew.selectAll", query = "SELECT d FROM DurchlaufNew d"),
    @NamedQuery(name = "DurchlaufNew.selectAllInterval", query = "SELECT d FROM DurchlaufNew d WHERE d.durchlaufDatum BETWEEN :vonDate AND :bisDate"),
    @NamedQuery(name = "DurchlaufNew.selectAllIntervalUser", query = "SELECT d FROM DurchlaufNew d WHERE d.durchlaufDatum BETWEEN :vonDate AND :bisDate AND d.nutzer.nutzerid = :userId"),
})
public class DurchlaufNew implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int durchlaufId;
    @Column(nullable = false)
    private LocalDate durchlaufDatum;
    @Column(nullable = false)
    private int anzahl;
    @Column(nullable = false)
    private int erfolgreich;
    @Column(nullable = false)
    private int fehlgeschlagen;
    @Column(nullable = false)
    private int uebernahmeAnz;
    
    @ManyToMany
    @JoinColumn(name = "gegenstand", nullable = false)
    private List<DurchlaufgegenstandNew> gegenstand = new LinkedList<>();
    
    @ManyToOne
    @JoinColumn(name = "nutzer", nullable = false)
    private NutzerNew nutzer;

    public DurchlaufNew() {
    }

    public DurchlaufNew(LocalDate durchlaufDatum, int anzahl, int erfolgreich, int fehlgeschlagen, int uebernahmeAnz) {
        this.durchlaufDatum = durchlaufDatum;
        this.anzahl = anzahl;
        this.erfolgreich = erfolgreich;
        this.fehlgeschlagen = fehlgeschlagen;
        this.uebernahmeAnz = uebernahmeAnz;
    }
  
    public int getDurchlaufId() {
        return durchlaufId;
    }

    public void setDurchlaufId(int durchlaufId) {
        this.durchlaufId = durchlaufId;
    }

    public LocalDate getDurchlaufDatum() {
        return durchlaufDatum;
    }

    public void setDurchlaufDatum(LocalDate durchlaufDatum) {
        this.durchlaufDatum = durchlaufDatum;
    }

    public int getAnzahl() {
        return anzahl;
    }

    public void setAnzahl(int anzahl) {
        this.anzahl = anzahl;
    }

    public int getErfolgreich() {
        return erfolgreich;
    }

    public void setErfolgreich(int erfolgreich) {
        this.erfolgreich = erfolgreich;
    }

    public int getFehlgeschlagen() {
        return fehlgeschlagen;
    }

    public void setFehlgeschlagen(int fehlgeschlagen) {
        this.fehlgeschlagen = fehlgeschlagen;
    }

    public int getUebernahmeAnz() {
        return uebernahmeAnz;
    }

    public void setUebernahmeAnz(int uebernahmeAnz) {
        this.uebernahmeAnz = uebernahmeAnz;
    }

    public List<DurchlaufgegenstandNew> getGegenstand() {
        return gegenstand;
    }

    public void setGegenstand(List<DurchlaufgegenstandNew> gegenstand) {
        this.gegenstand = gegenstand;
        for (DurchlaufgegenstandNew durchlaufgegenstandNew : gegenstand) {
            System.out.println(durchlaufgegenstandNew.getGegenstandid() + durchlaufgegenstandNew.getBezeichnung());
           durchlaufgegenstandNew.getAllDurchlauf().add(this);
        }
      //  this.gegenstand.getAllDurchlauf().add(this);
    }

    public NutzerNew getNutzer() {
        return nutzer;
    }

    public void setNutzer(NutzerNew nutzer) {
        this.nutzer = nutzer;
        this.nutzer.getAllDurchlauf().add(this);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + this.durchlaufId;
        hash = 37 * hash + Objects.hashCode(this.durchlaufDatum);
        hash = 37 * hash + this.anzahl;
        hash = 37 * hash + this.erfolgreich;
        hash = 37 * hash + this.fehlgeschlagen;
        hash = 37 * hash + this.uebernahmeAnz;
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
        final DurchlaufNew other = (DurchlaufNew) obj;
        if (this.durchlaufId != other.durchlaufId) {
            return false;
        }
        if (this.anzahl != other.anzahl) {
            return false;
        }
        if (this.erfolgreich != other.erfolgreich) {
            return false;
        }
        if (this.fehlgeschlagen != other.fehlgeschlagen) {
            return false;
        }
        if (this.uebernahmeAnz != other.uebernahmeAnz) {
            return false;
        }
        if (!Objects.equals(this.durchlaufDatum, other.durchlaufDatum)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DurchlaufNew{" + "durchlaufId=" + durchlaufId + ", durchlaufDatum=" + durchlaufDatum + ", gegenstand=" + gegenstand + ", nutzer=" + nutzer + '}';
    }

    
    
    
}

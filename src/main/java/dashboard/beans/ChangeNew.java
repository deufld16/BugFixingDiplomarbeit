/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dashboard.beans;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author flori
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "ChangeNew.selectAll", query = "SELECT c FROM ChangeNew c"),
    @NamedQuery(name = "ChangeNew.selectAllInterval", query = "SELECT c FROM ChangeNew c WHERE c.changeDate BETWEEN :vonDate AND :bisDate"),
    @NamedQuery(name = "ChangeNew.selectAllIntervalUser", query = "SELECT c FROM ChangeNew c WHERE c.changeDate BETWEEN :vonDate AND :bisDate AND c.nutzer.nutzerid = :userId"),})
public class ChangeNew implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int changeID;
    @Column(nullable = false)
    private LocalDateTime changeDate;

    @ManyToOne
    @JoinColumn(name = "gegenstand", nullable = false)
    private DurchlaufgegenstandNew gegenstand;

    @ManyToOne
    @JoinColumn(name = "nutzer", nullable = false)
    private NutzerNew nutzer;

    @ManyToOne
    @JoinColumn(name = "changeType", nullable = false)
    private ChangeType changeType;

    public ChangeNew() {

    }

    public ChangeNew(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public LocalDateTime getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(LocalDateTime changeDate) {
        this.changeDate = changeDate;
    }

    public int getChangeID() {
        return changeID;
    }

    public void setChangeID(int changeID) {
        this.changeID = changeID;
    }

    public DurchlaufgegenstandNew getGegenstand() {
        return gegenstand;
    }

    public void setGegenstand(DurchlaufgegenstandNew gegenstand) {
        this.gegenstand = gegenstand;
        this.gegenstand.getAllAenderungen().add(this);
    }

    public NutzerNew getNutzer() {
        return nutzer;
    }

    public void setNutzer(NutzerNew nutzer) {
        this.nutzer = nutzer;
        this.nutzer.getAllAenderungen().add(this);
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
        this.changeType.getChanges().add(this);
    }

    @Override
    public String toString() {
        return nutzer.getNutzerid() + " - " + gegenstand.getGegenstandid() + " - " + changeDate;
    }

}

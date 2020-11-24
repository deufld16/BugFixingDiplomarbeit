/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dashboard.beans;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "changetype")
@NamedQueries({
    @NamedQuery(name = "ChangeType.selectAll", query = "SELECT ct FROM ChangeType ct"),
})
public class ChangeType implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int changeTypeId;
    @Column(length = 255, nullable = false)
    private String bezeichnung;
    
    @OneToMany(mappedBy = "changeType", cascade = CascadeType.ALL)
    private List<Change> changes = new LinkedList<>();

    public ChangeType() {
    }

    public ChangeType(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public int getChangeTypeId() {
        return changeTypeId;
    }

    public void setChangeTypeId(int changeTypeId) {
        this.changeTypeId = changeTypeId;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    public List<Change> getChanges() {
        return changes;
    }

    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }


    @Override
    public String toString() {
        return changeTypeId + " - " + bezeichnung;
    }
    
    
}

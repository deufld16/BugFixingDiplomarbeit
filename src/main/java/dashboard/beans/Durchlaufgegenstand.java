/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dashboard.beans;

import java.time.LocalDate;

/**
 *
 * @author A180953
 */
public class Durchlaufgegenstand {
    private int gegenstandid;
    private String bezeichnung;
    private LocalDate erstelldatum;
    private int deleted;

    public Durchlaufgegenstand(int gegenstandid, String bezeichnung, LocalDate erstelldatum, int deleted) {
        this.gegenstandid = gegenstandid;
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

    @Override
    public String toString() {
        return gegenstandid + " - " + bezeichnung;
    }
    
    
}

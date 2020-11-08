/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dashboard.beans;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Class to store relevant statistics-infomation during an execution process.
 * It represents the entity Durchlauf at the database
 * 
 * @author Maximilian Strohmaier
 */
public class Durchlauf {
    private int durchlaufId;
    private LocalDateTime durchlaufDatum;
    private int anzahl;
    private int erfolgreich;
    private int fehlgeschlagen;
    private int uebernahmeAnz;
    private int nutzerId;

    public Durchlauf() {
        durchlaufId = 1;
    }

    public Durchlauf(int durchlaufId, LocalDateTime durchlaufDatum, int anzahl, int erfolgreich, int fehlgeschlagen, int uebernahmeAnz, int nutzerId) {
        this.durchlaufId = durchlaufId;
        this.durchlaufDatum = durchlaufDatum;
        this.anzahl = anzahl;
        this.erfolgreich = erfolgreich;
        this.fehlgeschlagen = fehlgeschlagen;
        this.uebernahmeAnz = uebernahmeAnz;
        this.nutzerId = nutzerId;;
    }

    
    
    public int getDurchlaufId() {
        return durchlaufId;
    }

    public void setDurchlaufId(int durchlaufId) {
        this.durchlaufId = durchlaufId;
    }

    public LocalDateTime getDurchlaufDatum() {
        return durchlaufDatum;
    }

    public void setDurchlaufDatum(LocalDateTime durchlaufDatum) {
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

    public int getNutzerId() {
        return nutzerId;
    }

    public void setNutzerId(int nutzerId) {
        this.nutzerId = nutzerId;
    }

    @Override
    public String toString() {
        return durchlaufId + "";
    }
    
    
}

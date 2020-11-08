/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator.beans;

import java.util.List;
import java.util.Objects;

/**
 * Represents an Testsystem consisting of one backoffice and several cashpoints
 *
 * @author Lukas Krobath
 */
public class Testsystem {

    private List<Kasse> kassen;
    private Backoffice backoffice;
    private String name;
    private boolean active = false;

    public Testsystem(List<Kasse> kassen, Backoffice backoffice, boolean active, String name) {
        this.kassen = kassen;
        this.backoffice = backoffice;
        this.active = active;
        this.name = name;
    }

    public Testsystem(List<Kasse> kassen, Backoffice backoffice, boolean active) {
        this.kassen = kassen;
        this.backoffice = backoffice;
        this.active = active;
        String res = "Backoffice: " + backoffice.getStrIpAdr() + " -";
        for (Kasse kasse : kassen) {
            res += " " + kasse.getStrIpAdr() + ": " + kasse.getType() + ",";
        }
        res = res.substring(0, res.length() - 1);
        this.name = res;
    }

    public List<Kasse> getKassen() {
        return kassen;
    }

    public void setKassen(List<Kasse> kassen) {
        this.kassen = kassen;
    }
    
    public String getConfig(){
       String res = "Backoffice: " + backoffice.getStrIpAdr() + " -";
        for (Kasse kasse : kassen) {
            res += " " + kasse.getStrIpAdr() + ": " + kasse.getType() + ",";
        }
        res = res.substring(0, res.length() - 1);
        return res;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Backoffice getBackoffice() {
        return backoffice;
    }

    public void setBackoffice(Backoffice backoffice) {
        this.backoffice = backoffice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 5;
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
        final Testsystem other = (Testsystem) obj;
        if (!Objects.equals(this.kassen, other.kassen)) {
            return false;
        }
        if (!Objects.equals(this.backoffice, other.backoffice)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return name; //To change body of generated methods, choose Tools | Templates.
    }

}

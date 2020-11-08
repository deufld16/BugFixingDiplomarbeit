/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dashboard.beans;

import java.io.Serializable;

/**
 *
 * @author A180953
 */
public class Nutzer implements Serializable{
    private int nutzerid;
    private String username;

    public Nutzer(int nutzerid, String username) {
        this.nutzerid = nutzerid;
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

    @Override
    public int hashCode() {
        int hash = 7;
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
        if (this.nutzerid != other.nutzerid) {
            return false;
        }
        return true;
    }
    
}

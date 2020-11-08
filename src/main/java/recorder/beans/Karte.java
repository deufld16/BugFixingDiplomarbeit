/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.beans;

import java.util.Objects;

/**
 *
 * @author Florian Deutschmann
 * 
 * Data class to depict a card
 */
public class Karte {
    
    private String id;
    private int aufladung;
    private boolean isStorno;
    private String type;

    public Karte(String id, int aufladung, boolean isStorno, String type) {
        this.id = id;
        this.aufladung = aufladung;
        this.isStorno = isStorno;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAufladung() {
        return aufladung;
    }

    public void setAufladung(int aufladung) {
        this.aufladung = aufladung;
    }

    public boolean isIsStorno() {
        return isStorno;
    }

    public void setIsStorno(boolean isStorno) {
        this.isStorno = isStorno;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return "Karte eingescannt - id: " + id;
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
        final Karte other = (Karte) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }
    
}

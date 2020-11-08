/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.beans;

import java.util.Objects;

/**
 *
 * @author Florian 
 * 
 * Data class for the articles that are displayed in the article table /
 * in the display list
 */
public class Article {

    private String xmlArticleName;
    private boolean leergut;
    private boolean jugendSchutz;
    private String ean;
    private String articleName;
    private boolean pfand;
    private double preis;
    private boolean rabatt;
    private int ust;
    private boolean weight;
    private String category;
    private int amount;
    private boolean eloading;
    private boolean serialNrRequired = false;
    private String serialNr = null;
    private String eloadingState = "";
    private boolean jugendSchutzOk;
    private boolean eloadingAmmountOk = true;
    private String pfandArtikel;
    private boolean inXml = true;
    private Object weigthArticles;
    private boolean isAbfrage = false;
    private boolean priceZero = false;
    private int gewichts_ean_menge = -1;
    private boolean storno_error = false;
    /**
     * boolean, ob Artikel über Keyboard eingegeben wurde oder nicht
     */
    private boolean keyboard;

    public Article(String xmlArticleName, boolean leergut, boolean jugendSchutz, String ean, String articleName, boolean pfand, String pfandArtikel, double preis, boolean rabatt, int ust, boolean weight, String category, boolean eloading) {
        this.xmlArticleName = xmlArticleName;
        this.leergut = leergut;
        this.jugendSchutz = jugendSchutz;
        this.ean = ean;
        this.articleName = articleName;
        this.pfand = pfand;
        this.preis = preis;
        this.rabatt = rabatt;
        this.ust = ust;
        this.weight = weight;
        this.category = category;
        this.eloading = eloading;
        this.pfandArtikel = pfandArtikel;
    }
    
    public Article(String xmlArticleName, boolean leergut, boolean jugendSchutz, String ean, String articleName, boolean pfand, String pfandArtikel,double preis, boolean rabatt, int ust, boolean weight, String category, boolean eloading, boolean serialNrRequired) {
        this.xmlArticleName = xmlArticleName;
        this.leergut = leergut;
        this.jugendSchutz = jugendSchutz;
        this.ean = ean;
        this.articleName = articleName;
        this.pfand = pfand;
        this.preis = preis;
        this.rabatt = rabatt;
        this.ust = ust;
        this.weight = weight;
        this.category = category;
        this.eloading = eloading;
        this.serialNrRequired = serialNrRequired;
        this.pfandArtikel = pfandArtikel;
    }

    public Article(String xmlArticleName, boolean leergut, boolean jugendSchutz, String ean, String articleName, boolean pfand, String pfandArtikel, double preis, boolean rabatt, int ust, boolean weight, String category, boolean eloading, boolean serialNrRequired, int amount) {
        this.xmlArticleName = xmlArticleName;
        this.leergut = leergut;
        this.jugendSchutz = jugendSchutz;
        this.ean = ean;
        this.articleName = articleName;
        this.pfand = pfand;
        this.preis = preis;
        this.rabatt = rabatt;
        this.ust = ust;
        this.weight = weight;
        this.category = category;
        this.eloading = eloading;
        this.serialNrRequired = serialNrRequired;
        this.pfandArtikel = pfandArtikel;
        this.amount = amount;
    }

    public boolean isInXml() {
        return inXml;
    }

    public void setInXml(boolean inXml) {
        this.inXml = inXml;
    }

    public int getGewichts_ean_menge() {
        return gewichts_ean_menge;
    }

    public void setGewichts_ean_menge(int gewichts_ean_menge) {
        this.gewichts_ean_menge = gewichts_ean_menge;
    }

    public boolean isPriceZero() {
        return priceZero;
    }

    public void setPriceZero(boolean priceZero) {
        this.priceZero = priceZero;
    }

    public boolean isStorno_error() {
        return storno_error;
    }

    public void setStorno_error(boolean storno_error) {
        this.storno_error = storno_error;
    }

    public boolean isKeyboard() {
        return keyboard;
    }

    public void setKeyboard(boolean keyboard) {
        this.keyboard = keyboard;
    }

    public boolean isIsAbfrage() {
        return isAbfrage;
    }

    public void setIsAbfrage(boolean isAbfrage) {
        this.isAbfrage = isAbfrage;
    }
    
    public String getXmlArticleName() {
        return xmlArticleName;
    }

    public Object getWeigthArticles() {
        return weigthArticles;
    }

    public void setWeigthArticles(Object weigthArticles) {
        this.weigthArticles = weigthArticles;
    }
    
    public void setXmlArticleName(String xmlArticleName) {
        this.xmlArticleName = xmlArticleName;
    }

    public boolean isEloadingAmmountOk() {
        return eloadingAmmountOk;
    }

    public void setEloadingAmmountOk(boolean eloadingAmmountOk) {
        this.eloadingAmmountOk = eloadingAmmountOk;
    }

    public boolean isLeergut() {
        return leergut;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setLeergut(boolean leergut) {
        this.leergut = leergut;
    }

    public boolean isJugendSchutz() {
        return jugendSchutz;
    }

    public void setJugendSchutz(boolean jugendSchutz) {
        this.jugendSchutz = jugendSchutz;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public boolean isPfand() {
        return pfand;
    }

    public void setPfand(boolean pfand) {
        this.pfand = pfand;
    }

    public double getPreis() {
        return preis;
    }

    public void setPreis(double preis) {
        this.preis = preis;
    }

    public boolean isRabatt() {
        return rabatt;
    }

    public void setRabatt(boolean rabatt) {
        this.rabatt = rabatt;
    }

    public int getUst() {
        return ust;
    }

    public void setUst(int ust) {
        this.ust = ust;
    }

    public boolean isWeight() {
        return weight;
    }

    public void setWeight(boolean weight) {
        this.weight = weight;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isEloading() {
        return eloading;
    }

    public void setEloading(boolean eloading) {
        this.eloading = eloading;
    }

    public boolean isSerialNrRequired() {
        return serialNrRequired;
    }

    public void setSerialNrRequired(boolean serialNrRequired) {
        this.serialNrRequired = serialNrRequired;
    }

    public String getSerialNr() {
        return serialNr;
    }

    public void setSerialNr(String serialNr) {
        this.serialNr = serialNr;
    }

    public String getEloadingState() {
        return eloadingState;
    }

    public void setEloadingState(String eloadingState) {
        this.eloadingState = eloadingState;
    }

    public boolean isJugendSchutzOk() {
        return jugendSchutzOk;
    }

    public void setJugendSchutzOk(boolean jugendSchutzOk) {
        this.jugendSchutzOk = jugendSchutzOk;
    }

    @Override
    public String toString() {
        if (amount != 0) {
            return String.format("%3dx %-23s - %3dx %.2f €", amount, articleName, amount, preis);
        }
        return String.format("%-20s - %.2f €", articleName, preis);
    }

    public String getPfandArtikel() {
        return pfandArtikel;
    }

    public void setPfandArtikel(String pfandArtikel) {
        this.pfandArtikel = pfandArtikel;
    }

    @Override
    public int hashCode() {
        int hash = 3;
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
        final Article other = (Article) obj;
        if (this.leergut != other.leergut) {
            return false;
        }
        if (this.jugendSchutz != other.jugendSchutz) {
            return false;
        }
        if (this.pfand != other.pfand) {
            return false;
        }
        if (Double.doubleToLongBits(this.preis) != Double.doubleToLongBits(other.preis)) {
            return false;
        }
        if (this.rabatt != other.rabatt) {
            return false;
        }
        if (this.ust != other.ust) {
            return false;
        }
        if (this.weight != other.weight) {
            return false;
        }
        if (this.amount != other.amount) {
            return false;
        }
        if (this.eloading != other.eloading) {
            return false;
        }
        if (this.serialNrRequired != other.serialNrRequired) {
            return false;
        }
        if (this.jugendSchutzOk != other.jugendSchutzOk) {
            return false;
        }
        if (this.keyboard != other.keyboard) {
            return false;
        }
        if (!Objects.equals(this.xmlArticleName, other.xmlArticleName)) {
            return false;
        }
        if (!Objects.equals(this.ean, other.ean)) {
            return false;
        }
        if (!Objects.equals(this.articleName, other.articleName)) {
            return false;
        }
        if (!Objects.equals(this.category, other.category)) {
            return false;
        }
        if (!Objects.equals(this.serialNr, other.serialNr)) {
            return false;
        }
        if (!Objects.equals(this.eloadingState, other.eloadingState)) {
            return false;
        }
        if (!Objects.equals(this.pfandArtikel, other.pfandArtikel)) {
            return false;
        }
        return true;
    }

}
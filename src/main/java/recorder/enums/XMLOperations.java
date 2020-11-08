/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.enums;

/**
 *
 * @author Florian
 * 
 * Enum to depict the XML "Operation" objects
 * Is used for the display of the display list
 */

public enum XMLOperations {
    
    ZEILENSTORNO("Zeilenstorno"),
    SOFORTSTORNO("Sofortstorno"),
    ZUBEZAHLEN("Summe der Artikel: "),
    TEILZAHLUNG("Teilzahlung von: "),
    SUMME("Summe beglichen - Rückgeld:"),
    GEWINNSPIEL_OK("Gewinnspiel OK"),
    GEWINNSPIEL_NOK("Gewinnspiel NOK"),
    WARENRUECKNAHME("Warenrücknahme"),
    GESCHENKKARTE("Teilzahlung (mit Karte) von: "),
    GESCHENKKARTE_TOTAL("Restbetrag mit Karte beglichen"),
    NACHTRAEGLICHE_AENDERUNG("");
    
    private String displayText;

    private XMLOperations(String displayText) {
        this.displayText = displayText;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    @Override
    public String toString() {
        return super.toString(); //To change body of generated methods, choose Tools | Templates.
    }
}

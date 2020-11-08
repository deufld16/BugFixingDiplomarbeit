/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recorder.beans;

/**
 *
 * @author Florian Deutschmann
 * 
 * Data class to depict all other means of payment (Geschenkkarten etc.)
 */
public class AndereZahlungsmittel {
    
    private String display_name;
    private int pay_id;
    private int pay_sub;

    public AndereZahlungsmittel(String display_name, int pay_id, int pay_sub) {
        this.display_name = display_name;
        this.pay_id = pay_id;
        this.pay_sub = pay_sub;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public int getPay_id() {
        return pay_id;
    }

    public void setPay_id(int pay_id) {
        this.pay_id = pay_id;
    }

    public int getPay_sub() {
        return pay_sub;
    }

    public void setPay_sub(int pay_sub) {
        this.pay_sub = pay_sub;
    }

    @Override
    public String toString() {
        return display_name;
    }
    
}

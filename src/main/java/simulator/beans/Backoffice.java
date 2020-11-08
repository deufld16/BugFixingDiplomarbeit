/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator.beans;

/**
 * Represents a backoffice
 *
 * @author Lukas Krobath
 */
public class Backoffice {

    private String strIpAdr;
    private short sLastSecOfIPAdr;

    public Backoffice(String strIpAdr) {
        this.strIpAdr = strIpAdr;
        sLastSecOfIPAdr = getShortIP(strIpAdr);
    }

    public String getStrIpAdr() {
        return strIpAdr;
    }

    public void setStrIpAdr(String strIpAdr) {
        this.strIpAdr = strIpAdr;
    }

    public short getsLastSecOfIPAdr() {
        return sLastSecOfIPAdr;
    }

    public void setsLastSecOfIPAdr(short sLastSecOfIPAdr) {
        this.sLastSecOfIPAdr = sLastSecOfIPAdr;
    }

    private short getShortIP(String strTempIpAdr) {
        String[] strParts = strTempIpAdr.split("\\.");
        short sTempIPAdr = Short.parseShort(strParts[3]);
        return sTempIPAdr;
    }
}

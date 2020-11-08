package simulator.beans;

import java.util.Objects;

/**
 * Represents an cashpoint
 *
 * @author Lukas Krobath
 */
public class Kasse {

    /**
     * Enum to represent the cashpoint type
     */
    public static enum TYP {
        RB4("rb4"),
        RB5("rb5"),
        RB6("rb6");

        private String type;

        TYP(String type) {
            this.type = type;
        }

        public String type() {
            return type;
        }

        @Override
        public String toString() {
            return type.toUpperCase();
        }

    }

    private int iRegId;
    private int iRegGrp;
    private String strIpAdr;
    private short sLastSecOfIPAdr;
    private TYP type;

    public Kasse(int iRegId, int iRegGrp, String strIpAdr, TYP type) {
        this.iRegId = iRegId;
        this.iRegGrp = iRegGrp;
        this.strIpAdr = strIpAdr;
        this.type = type;
        sLastSecOfIPAdr = getShortIP(this.strIpAdr);
    }

    private short getShortIP(String strTempIpAdr) {
        String[] strParts = strTempIpAdr.split("\\.");
        short sTempIPAdr = Short.parseShort(strParts[3]);
        return sTempIPAdr;
    }

    public TYP getType() {
        return type;
    }

    public void setType(TYP type) {
        this.type = type;
    }

    public int getiRegId() {
        return iRegId;
    }

    public void setiRegId(int iRegId) {
        this.iRegId = iRegId;
    }

    public int getiRegGrp() {
        return iRegGrp;
    }

    public void setiRegGrp(int iRegGrp) {
        this.iRegGrp = iRegGrp;
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
        final Kasse other = (Kasse) obj;
        if (this.iRegId != other.iRegId) {
            return false;
        }
        if (this.iRegGrp != other.iRegGrp) {
            return false;
        }
        if (this.sLastSecOfIPAdr != other.sLastSecOfIPAdr) {
            return false;
        }
        if (!Objects.equals(this.strIpAdr, other.strIpAdr)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Kasse [iRegId=" + iRegId + ", iRegGrp=" + iRegGrp + ", strIpAdr=" + strIpAdr
                + ", sLastSecOfIPAdr=" + sLastSecOfIPAdr + "]" + type;
    }

}

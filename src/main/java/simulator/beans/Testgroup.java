package simulator.beans;

import java.util.Objects;

/**
 * Representation of a testgroup (test with a special focus)
 *
 * @author Lukas Krobath
 */
public class Testgroup {

    private String strEmpId;
    private int iTllId;
    private String strPasswd;
    private Testcase objActTC;

    /**
     *
     * @param strEmpId : EmpID des Benutzers (String)
     * @param strPasswd : Passwort des Benutzers (String)
     * @param iTllId : Verwendete Lade (int)
     */
    public Testgroup(String strEmpId, String strPasswd, int iTllId, Testcase objActTC) {
        this.strEmpId = strEmpId;
        this.strPasswd = strPasswd;
        this.iTllId = iTllId;
        this.objActTC = objActTC;
    }

    public String getStrEmpId() {
        return strEmpId;
    }

    public void setStrEmpId(String strEmpId) {
        this.strEmpId = strEmpId;
    }

    public int getiTllId() {
        return iTllId;
    }

    public void setiTllId(int iTllId) {
        this.iTllId = iTllId;
    }

    public String getStrPasswd() {
        return strPasswd;
    }

    public void setStrPasswd(String strPasswd) {
        this.strPasswd = strPasswd;
    }

    public Testcase getObjActTC() {
        return objActTC;
    }

    public void setObjActTC(Testcase objActTC) {
        this.objActTC = objActTC;
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
        final Testgroup other = (Testgroup) obj;
        if (this.iTllId != other.iTllId) {
            return false;
        }
        if (!Objects.equals(this.strEmpId, other.strEmpId)) {
            return false;
        }
        if (!Objects.equals(this.strPasswd, other.strPasswd)) {
            return false;
        }
        if (!Objects.equals(this.objActTC, other.objActTC)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Testgroup [strEmpId=" + strEmpId + ", iTllId=" + iTllId + ", strPasswd=" + strPasswd
                + ", objActTC=" + objActTC + "]";
    }

}

package simulator.beans;

/**
 * Represents an runtime environment which is an combination of the test beans
 * structure and the cashpoint
 *
 * @author Lukas Krobath
 */
public class RuntimeEnv {

    private int iRegCount;
    private String strServerAddr;
    private Testgroup objActTG;
    private Kasse objActKasse;

    public RuntimeEnv(String strServerAddr, int iRegCount, Testgroup tg, Kasse kassa) {
        this.iRegCount = iRegCount;
        this.strServerAddr = strServerAddr;
        this.objActTG = tg;
        this.objActKasse = kassa;
    }

    public int getiRegCount() {
        return iRegCount;
    }

    public void setiRegCount(int iRegCount) {
        this.iRegCount = iRegCount;
    }

    public String getStrServerAddr() {
        return strServerAddr;
    }

    public void setStrServerAddr(String strServerAddr) {
        this.strServerAddr = strServerAddr;
    }

    public Testgroup getObjActTG() {
        return objActTG;
    }

    public void setObjActTG(Testgroup objActTG) {
        this.objActTG = objActTG;
    }

    public Kasse getObjActKasse() {
        return objActKasse;
    }

    public void setObjActKasse(Kasse objActKasse) {
        this.objActKasse = objActKasse;
    }

    @Override
    public String toString() {
        return "RuntimeEnv [iRegCount=" + iRegCount + ", strServerAddr=" + strServerAddr + ", objActTG="
                + objActTG + ", objActKasse=" + objActKasse + "]";
    }

}

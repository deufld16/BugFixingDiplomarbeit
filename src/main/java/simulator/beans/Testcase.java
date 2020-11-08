package simulator.beans;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Representation of a testcase (subsection of a testgroup)
 *
 * @author Lukas Krobath
 */
public class Testcase {

    private String strTestCasePath;
    private String strRefPath;
    private String strErgPath;
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");
    private LocalDateTime timestamp;
    private int kassaNr;

    public Testcase(Path strTestCasePath, LocalDateTime timestamp, int kassaNr) {
        this.timestamp = timestamp;
        this.kassaNr = kassaNr;
        this.strTestCasePath = strTestCasePath.toString();
        strTestCasePath.getParent().getParent().getParent().resolve("erg").resolve(dtf.format(timestamp)).resolve(strTestCasePath.getParent().toFile().getName()).resolve(strTestCasePath.toFile().getName()).resolve("kasse" + kassaNr).toFile().mkdirs();
        this.strErgPath = strTestCasePath.getParent().getParent().getParent().resolve("erg").resolve(dtf.format(timestamp)).resolve(strTestCasePath.getParent().toFile().getName()).resolve(strTestCasePath.toFile().getName()).resolve("kasse" + kassaNr).toString();
    }

    public String getStrTestCasePath() {
        return strTestCasePath;
    }

    public void setStrTestCasePath(String strTestCasePath) {
        this.strTestCasePath = strTestCasePath;
    }

    public String getStrRefPath() {
        return strRefPath;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getKassaNr() {
        return kassaNr;
    }

    public void setStrActRefPath(String strRefPath) {
        this.strRefPath = strRefPath;
    }

    public String getStrErgPath() {
        return strErgPath;
    }

    public void setStrActErgPath(String strErgPath) {
        this.strErgPath = strErgPath;
    }

    @Override
    public String toString() {
        return "Testcase [strTestCasePath=" + strTestCasePath + ", strRefPath=" + strRefPath + ", strErgPath="
                + strErgPath + "]";
    }

}

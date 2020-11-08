/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package explorer.enums;

/**
 *
 * @author Florian Deutschmann
 */
public enum DBNames_Display {
    DB_NAME_NPOS, DB_NAME_HDB, DB_NAME_ETILAG;

    @Override
    public String toString() {
        switch (this) {
            case DB_NAME_NPOS:
                return "npos";
            case DB_NAME_HDB:
                return "historyd";
            case DB_NAME_ETILAG:
                return "etilag";
            default:
                break;
        }
        return "";
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyzer.enums;

/**
 * Enum to store the different groups of cashpoints (rb4, rb5, ...)
 * 
 * @author Maximilian Strohmaier
 */
public enum CashpointGroup {
    RB4,
    RB5,
    RB6;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}

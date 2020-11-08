/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator.interfaces;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to mark a backoffice access command
 * @author Lukas Krobath
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface BackofficeAccess {
    boolean access();
}

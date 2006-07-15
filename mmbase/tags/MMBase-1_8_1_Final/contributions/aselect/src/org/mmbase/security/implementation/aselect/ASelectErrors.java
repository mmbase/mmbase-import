/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
*/
package org.mmbase.security.implementation.aselect;


/**
 * ASelect Errors.
 *
 *
 * @author Alfa & Ariss b.v.
 * @version 1.1
 */
public class ASelectErrors  {
    final static String ASELECT_NO_ERROR                    = "0000";
    final static String ASELECT_UNKNOWN_APPLICATION         = "0031";
    final static String ASELECT_SERVER_ID_MISMATCH          = "0033";
    final static String ASELECT_UNKNOWN_USER                = "0102";
    final static String ASELECT_COULD_NOT_AUTHENTICATE_USER = "0103";
    final static String ASELECT_COULD_NOT_AUTHENTICATE_USER_1 = "0003";

    /**
     * sigh..
     */
    public static String getMessage(String error) {
        if (error.equals(ASELECT_NO_ERROR)) {
            return "No error";
        } else if (error.equals(ASELECT_UNKNOWN_APPLICATION)) {
            return "Unknown application";
        } else if (error.equals(ASELECT_UNKNOWN_USER)) {
            return "Unknown user";
        } else if (error.equals(ASELECT_COULD_NOT_AUTHENTICATE_USER)) {
            return "Could not authenticate user";
        } else if (error.equals(ASELECT_COULD_NOT_AUTHENTICATE_USER_1)) {
            return "Could not authenticate user";
        } else if (error.equals(ASELECT_SERVER_ID_MISMATCH)) {
            return "ASelect Server ID mismatch.";
        } else {
            return "Unknown error code " + error;
        }
    }
}

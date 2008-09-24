/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.sms;

/**
 * This basic SMS implementation simply explicitely stores the information in member variables.
 * @author Michiel Meeuwissen
 * @version $Id: BasicSMS.java,v 1.5 2007-12-07 13:06:43 michiel Exp $
 */
public class BasicSMS implements SMS {

    private static final long serialVersionUID = 1L;

    private final String mobile;
    private final int operator;
    private final String message;
    public BasicSMS(String mob, int o, String mes) {
        this.mobile = mob;
        this.operator = o;
        this.message  = mes;
    }
    public BasicSMS(String mob, String mes) {
        this(mob, -1, mes);
    }
    public String toString() {
        return mobile + ":" + message;
    }

    public String getMobile() {
        return mobile;
    }
    public int getOperator() {
        return operator;
    }
    public String getMessage() {
        return message;
    }
}

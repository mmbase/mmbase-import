/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams.transcoders;

import org.mmbase.util.logging.*;
import org.mmbase.bridge.*;


/**
 *
 * @author Michiel Meeuwissen
 */

public class MimeType {

    public static final String STAR = "*";
    public static final MimeType ANY = new MimeType(STAR, STAR);

    private final String type;
    private final String subType;


    public MimeType(String s) {
        String[] m = s.split("/", 2);
        type = m[0];
        if (m.length > 1) {
            subType = m[1];
        } else {
            subType = STAR;
        }
    }
    public MimeType(String t, String s) {
        type = t;
        subType = s;
    }

    public String getType() {
        return type;
    }
    public String getSubType() {
        return subType;
    }

    public String toString() {
        return type + "/" + subType;
    }
}

/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.util.*;

/**
 * Entry for Arguments. A (function) argument is specified by a name and type.
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @see Arguments
 */

public class Argument { 

    // package for Arguments
    String key;
    Class type;

    protected Argument() {}

    public Argument(String k, Class c) {
        key = k;
        type = c;
    }
    

    public boolean equals(Object o) {
        if (o instanceof Argument) {
            Argument a = (Argument) o;
            return a.key.equals(key) && a.type.equals(type);
        }
        return false;
    }

    public String toString() {
        return type + " " + key;
    }


    /** 
     * An Argument.Wrapper wraps one Argument around an Argument[]
     * (then you can put it in a Argument[]).  Arguments will
     * recognize this.
     */
    public static class Wrapper extends Argument {
        Argument[] arguments;

        public Wrapper(Argument[] arg) {
            key = "[ARRAYWRAPPER]";
            arguments = arg;
        }
        
        public String toString() {
            return "WRAPPED" + Arrays.asList(arguments).toString();
        }
    }

}

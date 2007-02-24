/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

/**
 * Describes what encoding is configured.
 */

public class Config {
    public Class<?> clazz;
    public int   config;
    public String info;
    public Config(Class<?> c, int i ) {
        clazz = c;
        config = i;
        info = "";
    }
    public Config(Class<?> c, int i, String in ) {
        clazz = c;
        config = i;
        info = in;
    }
    public String toString() {
        return "" + config + ":" + info;
    }
}

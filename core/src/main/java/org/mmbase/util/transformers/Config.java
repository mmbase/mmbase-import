/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.transformers;

/**
 * Describes what encoding is configured.
 * @version $Id$
 */

public class Config {
    public final Class<?> clazz;
    public final int   config;
    public final String info;
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
    @Override
    public String toString() {
        return "" + config + ":" + info;
    }
}

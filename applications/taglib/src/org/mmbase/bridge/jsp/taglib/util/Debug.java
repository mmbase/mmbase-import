/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.util;


/**
 * Representation of the values of 'debug' attributes, as used in tags like mm:include and
 * mm:component.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9
 * @version $Id: Debug.java,v 1.2 2007-07-27 08:14:32 michiel Exp $
 */
public enum Debug {

    NONE,

    HTML {
        public String start(String name, String url) {
            return "\n<!-- " + name + " page = '" + url + "' -->\n";
        }
        public String end(String name, String url) {
            return "\n<!-- END " + name + " page = '" + url + "' -->\n";
        }
    },


    CSS {
        public String start(String name, String url) {
            return "\n/* " + name +  " page  = '" + url + "' */\n";
        }
        public String end(String name, String url) {
            return "\n/* END " + name + " page = '" + url + "' */\n";
        }
    },

    XML {
        public String start(String name, String url) {
            return "<!-- " + name + " page = '" + url + "' -->";
        }
        public String end(String name, String url) {
            return "<!-- END " + name + " page = '" + url + "' -->";
        }
    },

    PLAIN {
        public String start(String name, String url) {
            return "[start:" + name + ":'" + url + "']";
        }
        public String end(String name, String url) {
            return "[end:" + name + ":'" + url + "']";
        }
    };

    public static Debug valueOfOrEmpty(String s) {
        if (s == null || s.length() == 0) return NONE;
        return valueOf(s.toUpperCase());
    }


    public String start(String name, String url) {
        return "";
    }

    public String end(String name, String url) {
        return "";
    }

}
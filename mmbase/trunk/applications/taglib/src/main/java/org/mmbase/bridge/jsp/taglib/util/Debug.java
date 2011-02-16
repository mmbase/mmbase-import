/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.util;

import org.mmbase.util.transformers.Xml;


/**
 * Representation of the values of 'debug' attributes, as used in tags like mm:include and
 * mm:component.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9
 * @version $Id$
 */
public enum Debug {

    NONE,
    LOG,

    HTML {
        @Override
        public String start(String name, Object url) {
            return "\n<!-- " + name + " page = '" + Xml.XMLEscape("" + url) + "' -->\n";
        }
        @Override
        public String end(String name, Object url) {
            return "\n<!-- END " + name + " page = '" + Xml.XMLEscape("" + url) + "' -->\n";
        }
    },


    CSS {
        @Override
        public String start(String name, Object url) {
            return "\n/* " + name +  " page  = '" + url + "' */\n";
        }
        @Override
        public String end(String name, Object url) {
            return "\n/* END " + name + " page = '" + url + "' */\n";
        }
    },

    XML {
        @Override
        public String start(String name, Object url) {
            return "<!-- " + name + " page = '" + Xml.XMLEscape("" + url) + "' -->";
        }
        @Override
        public String end(String name, Object url) {
            return "<!-- END " + name + " page = '" + Xml.XMLEscape("" + url) + "' -->";
        }
    },

    PLAIN {
        @Override
        public String start(String name, Object url) {
            return "[start:" + name + ":'" + url + "']";
        }
        @Override
        public String end(String name, Object url) {
            return "[end:" + name + ":'" + url + "']";
        }
    };

    public static Debug valueOfOrEmpty(String s) {
        if (s == null || s.length() == 0) return NONE;
        return valueOf(s.toUpperCase());
    }


    public String start(String name, Object url) {
        return "";
    }

    public String end(String name, Object url) {
        return "";
    }

}

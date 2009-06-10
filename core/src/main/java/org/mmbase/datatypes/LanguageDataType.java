/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Returns the current language a a default value. For the rest it is like {@link StringDataType}.
 * @author Michiel Meeuwissen
 * @version $Id: NodeManagerNamesDataType.java 35348 2009-05-21 15:40:40Z michiel $
 * @since MMBase-1.9.1
 */
public class LanguageDataType extends StringDataType {

    private static final Logger log = Logging.getLoggerInstance(LanguageDataType.class);

    private static final long serialVersionUID = 1L;

    public LanguageDataType(String name) {
        super(name);
    }

    protected static Locale getLocale(Cloud cloud, Field field) {
        if (cloud != null) {
            return cloud.getLocale();
        } else if (field != null) {
            try {
                return field.getNodeManager().getCloud().getLocale();
            } catch (UnsupportedOperationException uoe) {
                // Core field can do this
                return org.mmbase.util.LocalizedString.getDefault();
            }
        } else {
            return org.mmbase.util.LocalizedString.getDefault();
        }
    }


    @Override
    public String getDefaultValue(Locale locale, Cloud cloud, Field field) {
        if (locale != null) {
            return locale.getLanguage();
        } else {
            return getLocale(cloud, field).getLanguage();
        }
    }

    public static class Getter implements org.mmbase.datatypes.processors.Processor {
        private static final long serialVersionUID = 9004314043476276185L;
        public Object process(Node node, Field field, Object value) {
            if (log.isDebugEnabled()) {
                log.debug("node " + node + " " + field + " "  + value);
            }
            if (value == null || "".equals(value)) {
                return getLocale(node == null ? null : node.getCloud(), field).getLanguage();
            } else {
                return value;
            }
        }
    }

}

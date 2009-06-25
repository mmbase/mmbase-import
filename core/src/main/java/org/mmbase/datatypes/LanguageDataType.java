/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.LocalizedString;
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

    protected boolean languageInSelf = false;

    public LanguageDataType(String name) {
        super(name);
    }


    /**
     * If you set this to true, then the names of the languages are not presented in the current
     * locale, but in the language itself (if that is known by the JVM).
     * @since MMBase-1.9.2
     */
    public void setInSelf(boolean b) {
        languageInSelf = b;
    }

    protected static Locale getLocale(Cloud cloud, Field field) {
        Locale loc;
        if (cloud != null) {
            loc = cloud.getLocale();
        } else if (field != null) {
            try {
                loc = field.getNodeManager().getCloud().getLocale();
            } catch (UnsupportedOperationException uoe) {
                // Core field can do this
                loc = org.mmbase.util.LocalizedString.getDefault();
            }
        } else {
            loc = org.mmbase.util.LocalizedString.getDefault();
        }
        return loc;
    }


    @Override public Iterator<Map.Entry<String, String>> getEnumerationValues(final Locale locale, final Cloud cloud, final Node node, final Field field) {

        final Iterator<Map.Entry<String, String>> superIterator = super.getEnumerationValues(locale, cloud, node, field);
        if (languageInSelf) {
            return new Iterator<Map.Entry<String, String>>() {
                public boolean hasNext() {
                    return superIterator.hasNext();
                }
                public Map.Entry<String, String> next() {
                    Map.Entry<String, String> superEntry = superIterator.next();
                    Locale valueLocale = LocalizedString.getLocale(superEntry.getKey());
                    return new org.mmbase.util.Entry<String, String>(superEntry.getKey(), valueLocale.getDisplayLanguage(valueLocale));
                }
                public void remove() {
                    superIterator.remove();
                }

            };
        } else {
            return superIterator;
        }
    }



    @Override
    public String getDefaultValue(Locale locale, Cloud cloud, Field field) {
        Locale loc;
        if (locale != null) {
            loc = locale;
        } else {
            loc = getLocale(cloud, field);
        }
        if (validate(loc.toString(), null, field).size() == 0) {
            return loc.toString();

        } else {
            // simply return the first valid one then.
            String ret =  getEnumerationValues(loc, cloud, null, field).next().getKey();
            return ret;
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

/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;

/**
 * A String which is localized. There are two mechanisms to find and provide translations: They can
 * explicitely be set with {@link #set} (e.g. during parsing an XML), or a resource bundle can be
 * associated with {@link #setBundle}, which will be used to find translations based on the key of
 * this object.
 *
 * @author Michiel Meeuwissen
 * @version $Id: LocalizedString.java,v 1.1 2005-03-16 15:57:58 michiel Exp $
 * @since MMBase-1.8
 */
public class LocalizedString  {

    private final String key; 
    private Map    values = null;
    private String bundle = null;


    /**
     * @param k The key of this String
     */
    public LocalizedString(String k) {
        key = k;
    }

    /**
     * Gets the value for a certain locale. If no match is found, it falls back to the key.
     */
    public String get(Locale locale) {
        if (locale == null) {
            locale = new Locale(org.mmbase.module.core.MMBase.getMMBase().getLanguage(), "");
        }
        if (values != null) {
            String result = (String) values.get(locale);
            
            if (result != null) return result;
            
            String variant  = locale.getVariant();
            String country  = locale.getCountry();
            String language = locale.getLanguage();
            
            if (! "".equals(variant)) {
                result = (String) values.get(new Locale(language, country));
                if (result != null) return result;
            }
            
            if (! "".equals(country)) {
                result = (String) values.get(new Locale(language));
                if (result != null) return result;
            }
        }

        if (bundle != null) {
            try {
                return ResourceBundle.getBundle(bundle, locale).getString(key);
            } catch (MissingResourceException mre) {
                // fall back to key
            }
        }

        return key;
    }

    /**
     * Sets the value for a certain locale. If the value for a more general locale is still unset,
     * it will also set that (so, it sets also nl when setting nl_BE when nl still is unset).
     */
    public void set(String value, Locale locale) {
        if (values == null) {
            values = new HashMap();
        }

        if (locale == null) {
            locale = new Locale(org.mmbase.module.core.MMBase.getMMBase().getLanguage(), "");
        }

        values.put(locale, value);
        
        String variant  = locale.getVariant();
        String country  = locale.getCountry();
        String language = locale.getLanguage();
        if (! "".equals(variant)) {
            Locale loc = new Locale(language, country);
            if (values.get(loc) == null) {
                values.put(loc, value);
            }
        }
        if (! "".equals(country)) {
            Locale loc = new Locale(language);
            if (values.get(loc) == null) {
                values.put(loc, value);
            }
        }
    }
    /**
     * A resource-bundle with given name can be associated to this LocalizedString. If no
     * tranlations where explicitely added, it can be used to look up the translation in the bundle,
     * using the key.
     */

    public void setBundle(String b) {
        bundle = b;
    }

    public String toString() {
        return "localized(" + key + ")";
    }
}

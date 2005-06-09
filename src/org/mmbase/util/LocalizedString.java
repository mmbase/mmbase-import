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
 * @version $Id: LocalizedString.java,v 1.6 2005-06-09 21:35:37 michiel Exp $
 * @since MMBase-1.8
 */
public class LocalizedString  implements java.io.Serializable {

    private static Locale defaultLocale = null; // means 'system default' and 'unset'.

    /**
     * Sets a default locale for this JVM or web-app. When not using it, the locale is the system
     * default. Several web-apps do run in one JVM however and it is very imaginable that you want a
     * different default for the Locale.
     *
     * So, this function can be called only once. Calling it the second time will not do
     * anything. It returns the already set default locale then, which should probably prompt you to log an error
     * a throw an exception or so. Otherwise it returns <code>null</code> indicating that the
     * default locale is now what you just set.
     */
    public static Locale setDefault(Locale locale) {
        if (defaultLocale != null) return defaultLocale;
        defaultLocale = locale;
        return null;
    }
    /**
     * Returns the default locale if set, or otherwise the system default ({@link java.util.Locale#getDefault}).
     */
    public static Locale getDefault() {
        return defaultLocale != null ? defaultLocale : Locale.getDefault();
    }

    //    private final String key;
    private String key;

    private Map    values = null;
    private String bundle = null;

    // just for the contract of Serializable
    protected LocalizedString() {
        
    }

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
            locale = defaultLocale == null ? Locale.getDefault() : defaultLocale;
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

            // Some LocalizedString isntances may have a default value stored with the key 'null'
            // instead of the locale from MMBase. This is the case for values stored while the
            // MMBase module was not yet active
            // This code 'fixes' that reference.
            // It's not nice, but as a proper fix likely requires a total rewrite of Module.java and
            // MMBase.java, this will have to do for the moment.
            if (locale.equals(defaultLocale)) {
                result = (String) values.get(null);
                if (result != null) {
                    values.put(locale, result);
                    return result;
                }
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
     * it will also set that (so, it sets also nl when setting nl_BE if nl still is unset).
     */
    public void set(String value, Locale locale) {
        if (values == null) {
            values = new HashMap();
        }

        if (locale == null) {
            locale = defaultLocale;
        }

        values.put(locale, value);

        if (locale != null) {
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
    }

    /**
     * Locale -> description
     */
    public Map asMap() {
        if (values == null) return new HashMap();
        return Collections.unmodifiableMap(values);
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

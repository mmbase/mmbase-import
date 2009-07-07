/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.xml.DocumentReader;
import org.w3c.dom.*;

/**
 *<p>
 * A String which is localized. There are two mechanisms to find and provide translations: They can
 * explicitely be set with {@link #set} (e.g. during parsing an XML), or a resource-bundle can be
 * associated with {@link #setBundle}, which will be used to find translations based on the key of
 * this object.
 *</p>
 *<p>
 * The 'set' mechanism can also be driven by {@link #fillFromXml}, which provides a sensible way to fill the LocalizedString with
 * setting from a sub element of XMLs.
 *</p>
 *<p>
 * The idea is that objects of this type can be used in stead of normal String objects, for error
 * messages, descriptions and other texts which need localization (e.g. because they are exposed to
 * end-users).
 *</p>
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */
public class LocalizedString implements java.io.Serializable, PublicCloneable<LocalizedString> {

    private static final Logger log = Logging.getLoggerInstance(LocalizedString.class);

    private static final long serialVersionUID = 1L;

    private static Locale defaultLocale = null; // means 'system default' and 'unset'.

    /**
     * Sets a default locale for this JVM or web-app. When not using it, the locale is the system
     * default. Several web-apps do run in one JVM however and it is very imaginable that you want a
     * different default for the Locale.
     * @return The previously set default locale. Should normally be <code>null</code> since it is
     * odd to call this more than once.
     */
    public static Locale setDefault(Locale locale) {
        Locale prev = defaultLocale;
        defaultLocale = locale;
        return prev;
    }
    /**
     * Returns the default locale if set, or otherwise the system default ({@link java.util.Locale#getDefault}).
     */
    public static Locale getDefault() {
        return defaultLocale != null ? defaultLocale : Locale.getDefault();
    }

    /**
     * Converts a collection of localized strings to a collection of normal strings.
     * @param col    Collection of LocalizedString objects
     * @param locale Locale to be used for the call to {@link #get(Locale)} which obviously is needed
     */
    public static Collection<String> toStrings(Collection<LocalizedString> col, Locale locale) {
        Collection<String> res = new ArrayList<String>();
        for (LocalizedString s : col) {
            res.add(s.get(locale));
        }
        return res;
    }

    private String key;
    private Map<Locale, String> values = null;
    private String bundle = null;

    /**
     * @param k The key of this String, if k == <code>null</code> then the first set will define it.
     */
    public LocalizedString(String k) {
        key = k;
    }

    /**
     * Gets the key to use as a default and/or for obtaining a value from the bundle
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the key to use as a default and/or for obtaining a value from the bundle
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Gets the value for a certain locale. If no match is found, it falls back to the key.
     */
    public String get(Locale locale) {
        if (locale == null) {
            locale = getDefault();
        }
        if (values != null) {
            String result = values.get(locale);

            if (result != null) return result;

            String variant  = locale.getVariant();
            String country  = locale.getCountry();
            String language = locale.getLanguage();

            if (! "".equals(variant)) {
                result = values.get(new Locale(language, country));
                if (result != null) return result;
            }

            if (! "".equals(country)) {
                result = values.get(new Locale(language));
                if (result != null) return result;
            }

            // Some LocalizedString instances may have a default value stored with the key 'null'
            // instead of the locale from MMBase. This is the case for values stored while the
            // MMBase module was not yet active.
            // This code 'fixes' that reference.
            // It's not nice, but as a proper fix likely requires a total rewrite of Module.java and
            // MMBase.java, this will have to do for the moment.
            Locale def = getDefault();
            if (locale.equals(def)) {
                result = values.get(null);
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
                // fall back to key.
                if (log.isDebugEnabled()) {
                    log.debug("Cannot get resource from bundle: " + bundle + ", key: " + key);
                }
            }
        }

        return key;
    }

    /**
     * Sets the value for a certain locale. If the value for a more general locale is still unset,
     * it will also set that (so, it sets also nl when setting nl_BE if nl still is unset).
     */
    public void set(final String value, Locale locale) {
        if (key == null) key = value;

        if (values == null) {
            values = new HashMap<Locale, String>();
        }

        if (locale == null) {
            locale = getDefault();
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
     * Returns a Map representation of the localisation setting represented by this
     * LocalizedString. It is an unmodifiable mapping: Locale -> localized value.
     */
    public Map<Locale, String> asMap() {
        if (values == null) return Collections.emptyMap();
        return Collections.unmodifiableMap(values);
    }

    protected Map<Locale, String> getValues() {
        return values;
    }

    protected  String getBundle() {
        return bundle;
    }

    /**
     * A resource-bundle with given name can be associated to this LocalizedString. If no
     * translations were explicitely added, it can be used to look up the translation in the bundle,
     * using the key.
     */

    public void setBundle(String b) {
        bundle = b;
    }

    /**
     * {@inheritDoc}
     *
     * For LocalizedString this returns the String for the default Locale (see {@link #getDefault}).
     */
    @Override
    public String toString() {
        return get((Locale) null);
    }

    public String getDebugString() {
        return "k: " + getKey() + " values: " + getValues() + " b:" + getBundle() + " dl: " + defaultLocale;
    }

    /**
     * This utility takes care of reading the xml:lang attribute from an element
     * @param element a DOM element
     * @return A {@link java.util.Locale} object, or <code>null</code> if the element did not have,
     * or had an empty, xml:lang attribute
     */
    public static Locale getLocale(Element element) {
        return getLocale(element.getAttribute("xml:lang"));
    }


    /**
     * @since MMBase-1.8.1
     */
    public static Locale getLocale(String xmlLang) {
        Locale loc = null;
        if (xmlLang != null && (! xmlLang.equals(""))) {

            String[] split = xmlLang.split("[-_]", 3);
            if (split.length == 1) {
                loc = new Locale(split[0]);
            } else if (split.length == 2) {
                loc = new Locale(split[0], split[1]);
            } else {
                loc = new Locale(split[0], split[1], split[2]);
            }
        }
        return loc;
    }

    /**
     * Degrades a Locale object to a more general Locale. Principally this means that first the
     * 'variant' will be dropped and then the country. As an extra the 'variant' is also degraded
     * progressively. This is done by taking away parts (from the end) which are separated by
     * underscore characters. Also, after degrading the country, also locales are tried with no
     * country, but with a variant only.
     * So e.g. nl_BE_a_b is degraded to nl_BE_a, then nl_BE, then nl__a_b, then nl__a, then nl.
     *
     * @param locale The locale to be degraded
     * @param originalLocale The original locale (used to find back the original variant after
     * dropping the country)
     * @return A degraded Locale or <code>null</code> if the locale could not be degraded any further.
     *
     * @since MMBase-1.8.5
     */
    public static Locale degrade(Locale locale, Locale originalLocale) {
        String language = locale.getLanguage();
        String country  = locale.getCountry();
        String variant  = locale.getVariant();
        if (variant != null && ! "".equals(variant)) {
            String[] var = variant.split("_");
            if (var.length > 1) {
                StringBuilder v = new StringBuilder(var[0]);
                for (int i = 1; i < var.length - 1; i++) {
                    v.append('_');
                    v.append(var[i]);
                }
                return new Locale(language, country, v.toString());
            } else {
                return new Locale(language, country);
            }
        }
        if (! "".equals(country)) {
            String originalVariant = originalLocale.getVariant();
            if (originalVariant  != null && ! "".equals(originalVariant)) {
                return new Locale(language, "", originalVariant);
            } else {
                return new Locale(language);
            }
        }
        // cannot be degraded any more.
        return null;
    }

    /**
     * @since MMBase-1.9.2
     */
    public static List<Locale> degrade(Locale locale) {
        List<Locale> result = new ArrayList<Locale>();
        while (locale != null) {
            result.add(locale);
            locale = degrade(locale, result.get(0));
        }
        return result;

    }


    /**
     * This utility determines the value of an xml:lang attribute. So, given a {@link java.util.Locale}
     * it produces a String.
     * @param locale A java locale
     * @return A string that can be used as the value for an XML xml:lang attribute.
     * @since MMBase-1.8.1
     */
    public static String getXmlLang(Locale locale) {
        if (locale == null) return null;
        StringBuilder lang = new StringBuilder(locale.getLanguage());
        String country = locale.getCountry();
        if (country.length() > 0) {
            lang.append("-").append(country);
            String variant = locale.getVariant();
            if (variant != null && variant.length() > 0) {
                lang.append("-").append(variant);
            }
        }
        return lang.toString();
    }

    /**
     * This utility takes care of setting the xml:lang attribute on an element.
     * @param element Element on which the xml:lang attribute is going to be set
     * @param locale  Java's Locale object
     * @since MMBase-1.8.1
     */
    public static void setXmlLang(Element element, Locale locale) {
        String xmlLang = getXmlLang(locale);
        if (xmlLang != null) {
            element.setAttribute("xml:lang", xmlLang);
        }
    }

    /**
     * Given a certain tagname, and a DOM parent element, it configures this LocalizedString, using
     * subtags with this tagname with 'xml:lang' attributes. This boils down to repeative calls to {@link #set(String, Locale)}.
     */

    public void fillFromXml(final String tagName, final Element element) {
        if (element == null) return;
        NodeList childNodes = element.getChildNodes();
        for (int k = 0; k < childNodes.getLength(); k++) {
            if (childNodes.item(k) instanceof Element) {
                Element childElement = (Element) childNodes.item(k);
                if (tagName.equals(childElement.getLocalName())) {
                    Locale locale = getLocale(childElement);
                    String description = DocumentReader.getNodeTextValue(childElement);
                    set(description, locale);
                }
            }
        }
    }

    /**
     * Writes this LocalizedString object back to an XML, i.e. it searches for and creates
     * sub-elements (identified by xml:lang attributes) of a certain given parent element, and sets
     * the node-text-value of those elements corresponding to the locale.
     * @param tagName Tag-name of the to be used sub-elements
     * @param ns      Namespace of the to be created sub-elements, or <code>null</code>
     * @param element The parent element which must contain the localized elements.
     * @param path    A comma separated list of names of tags which must skipped, before appending
     * childs. See {@link org.mmbase.util.xml.DocumentReader#appendChild(Element, Element, String)}.
     *
     * @since MMBase-1.8.1
     */
    public void toXml(final String tagName, final String ns, final Element element, final String path) {
        if (values != null) { // if no explicit values, nothing can be done

            // what if there are corresponding elements already:
            org.w3c.dom.NodeList nl  = element.getElementsByTagName(tagName);
            for (Map.Entry<Locale, String> entry : values.entrySet()) {
                Locale loc   = entry.getKey();
                String value = entry.getValue();
                String xmlLang = getXmlLang(loc);
                // look if such an element is already available
                Element child = null;
                for (int j = 0; j < nl.getLength(); j++) {
                    Element cand = (Element) nl.item(j);
                    String l = cand.getAttribute("xml:lang");
                    if (l.equals(xmlLang) || (l.equals("") && xmlLang == null)) {
                        child = cand;
                        break;
                    }
                }
                if (child == null) {
                    if (ns != null) {
                        child = element.getOwnerDocument().createElementNS(ns, tagName);
                    } else {
                        child = element.getOwnerDocument().createElement(tagName);
                    }
                    if (loc != null || value.length() > 0) {
                        DocumentReader.appendChild(element, child, path);
                    }
                    setXmlLang(child, loc);
                }
                DocumentReader.setNodeTextValue(child, value);
            }
        }
    }

    @Override
    public LocalizedString clone() {
        try {
            LocalizedString clone = (LocalizedString)super.clone();
            if (values != null) {
                clone.values = (Map)((HashMap)values).clone();
            }
            return clone;
        } catch (CloneNotSupportedException cnse) {
            // should not happen
            throw new RuntimeException("Cannot clone this LocalizedString", cnse);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LocalizedString) {
            LocalizedString os = (LocalizedString) o;
            return
                key.equals(os.getKey()) &&
                (values == null ? os.getValues() == null : values.equals(os.getValues())) &&
                (bundle == null ? os.getBundle() == null : bundle.equals(os.getBundle()))
                ;
        } else {
            return false;
        }
    }
    @Override
    public int hashCode() {
        int result = 0;
        result = HashCodeUtil.hashCode(result, key);
        result = HashCodeUtil.hashCode(result, values);
        result = HashCodeUtil.hashCode(result, bundle);
        return result;
    }


    /**
     * @since MMBase-1.9.2
     */
    public ReadonlyLocalizedString getReadonlyLocalizedString() {
        return new ReadonlyLocalizedString(this);
    }

}

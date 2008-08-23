/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;
import org.mmbase.util.logging.*;

/**
 * Extends and wraps LocalizedString.
 *
 * @author Michiel Meeuwissen
 * @version $Id: WrappedLocalizedString.java,v 1.2 2008-08-23 18:56:31 michiel Exp $
 * @since MMBase-1.9
 */
public class WrappedLocalizedString extends LocalizedString {

    private static final Logger log = Logging.getLoggerInstance(WrappedLocalizedString.class);


    protected LocalizedString wrapped;
    private Locale defaultLocale = null;

    // just for the contract of Serializable
    protected WrappedLocalizedString() {

    }

    /**
     * @param s The wrapped LocalizedString.
     */
    public WrappedLocalizedString(LocalizedString s) {
        if (s == null) s = new LocalizedString("NULL");
        wrapped = s;
    }

    public Locale setLocale(Locale loc) {
        Locale prev = defaultLocale;
        defaultLocale = loc;
        return prev;
    }

    //javadoc inherited
    public String getKey() {
        return wrapped.getKey();
    }

    //javadoc inherited
    public void setKey(String key) {
        wrapped.setKey(key);
    }

    // javadoc inherited
    public String get(Locale locale) {
        if (locale == null) locale = defaultLocale;
        return wrapped.get(locale);
    }

    //javadoc inherited
    public void set(String value, Locale locale) {
        wrapped.set(value, locale);
    }


    // javadoc inherited
    public void setBundle(String b) {
        wrapped.setBundle(b);
    }


}

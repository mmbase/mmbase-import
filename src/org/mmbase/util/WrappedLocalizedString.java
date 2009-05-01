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
 * @version $Id$
 * @since MMBase-1.9
 */
public class WrappedLocalizedString extends LocalizedString {

    private static final Logger log = Logging.getLoggerInstance(WrappedLocalizedString.class);

    protected final LocalizedString wrapped;
    private Locale defaultLocale = null;


    /**
     * @param s The wrapped LocalizedString.
     */
    public WrappedLocalizedString(LocalizedString s) {
        super("WRAPPED");
        if (s == null) s = new LocalizedString("NULL");
        wrapped = s;
    }


    public Locale setLocale(Locale loc) {
        Locale prev = defaultLocale;
        defaultLocale = loc;
        return prev;
    }


    @Override
    public String getKey() {
        return wrapped.getKey();
    }

    @Override
    public void setKey(String key) {
        wrapped.setKey(key);
    }

    @Override
    public String get(Locale locale) {
        if (locale == null) locale = defaultLocale;
        return wrapped.get(locale);
    }

    @Override
    public void set(String value, Locale locale) {
        wrapped.set(value, locale);
    }

    @Override
    public Map<Locale, String> asMap() {
        return wrapped.asMap();
    }

    @Override
    public void setBundle(String b) {
        wrapped.setBundle(b);
    }

    @Override
    protected String getBundle() {
        return wrapped.getBundle();
    }
    @Override
    protected Map<Locale, String>getValues() {
        return wrapped.getValues();
    }
    @Override
    public boolean equals(Object o) {
        return wrapped.equals(o);
    }



}

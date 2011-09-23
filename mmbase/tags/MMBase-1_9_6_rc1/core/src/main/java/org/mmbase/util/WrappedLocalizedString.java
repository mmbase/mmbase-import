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
public abstract class WrappedLocalizedString extends LocalizedString {
    private static final long serialVersionUID = 0L;
    private static final Logger log = Logging.getLoggerInstance(WrappedLocalizedString.class);

    protected final LocalizedString wrapped;


    /**
     * @param s The wrapped LocalizedString.
     */
    protected  WrappedLocalizedString(LocalizedString s) {
        super("WRAPPED");
        if (s == null) {
            s = new LocalizedString("NULL");
        }
        wrapped = s;
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
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object o) {
        return wrapped.equals(o);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.wrapped != null ? this.wrapped.hashCode() : 0);
        return hash;
    }

    @Override
    public ReadonlyLocalizedString getReadonlyLocalizedString() {
        return wrapped.getReadonlyLocalizedString();
    }



}

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

 *
 *
 * @author Michiel Meeuwissen
 * @version $Id: ReplacingLocalizedString.java 34936 2009-05-04 14:41:51Z michiel $
 * @since MMBase-1.9.2
 */
public class LocaleLocalizedString extends ReadonlyLocalizedString {
    private static final long serialVersionUID = 0L;

    private static final Logger log = Logging.getLoggerInstance(LocaleLocalizedString.class);


    private Locale defaultLocale = null;

    public Locale setLocale(Locale loc) {
        Locale prev = defaultLocale;
        defaultLocale = loc;
        return prev;
    }
    /**
     * @param s The wrapped LocalizedString.
     */
    public LocaleLocalizedString(LocalizedString s) {
        super(s);
    }


    @Override
    public String get(Locale locale) {
        if (locale == null) locale = defaultLocale;
        return super.get(locale);
    }



}

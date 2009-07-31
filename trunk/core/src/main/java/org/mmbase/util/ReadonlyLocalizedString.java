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
 * Extends and wraps LocalizedString, to make it readonly
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.1
 */
public class ReadonlyLocalizedString extends WrappedLocalizedString {
    private static final long serialVersionUID = 0L;

    private static final Logger log = Logging.getLoggerInstance(WrappedLocalizedString.class);


    /**
     * @param s The wrapped LocalizedString.
     */
    ReadonlyLocalizedString(LocalizedString s) {
        super(s);
    }


    @Override
    public void setKey(String key) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void set(String value, Locale locale) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void setBundle(String b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LocalizedString clone() {
        // clone is writeable again.
        return wrapped.clone();
    }

    @Override
    public ReadonlyLocalizedString getReadonlyLocalizedString() {
        return this;
    }


}

/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.datatypes.handlers;

import org.mmbase.util.functions.Parameter;
import org.mmbase.bridge.*;

/**
 * This generalizes  one rendition of a form.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.1
 */

public interface Request {
    /**
     * Encounter something that would make the current form invalid.
     */
    void invalidate();

    boolean isValid();

    /**
     * Obtains the Cloud that can be used if no Node available yet.
     */
    Cloud getCloud();

    java.util.Locale getLocale();

    /**
     *
     */
    String getName(Field field);

    /**
     * Gets the user specified value for a field
     */
    Object getValue(Field field);
    Object getValue(Field field, String part);

    /**
     * Handler implementations can put properties on the request to do some adminstration.
     */
    <C> C setProperty(Parameter<C> name, C value);
    <C> C getProperty(Parameter<C> name);
}

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
 * @version $Id: Request.java,v 1.2 2008-07-15 19:41:01 michiel Exp $
 * @since MMBase-1.9.0
 */

public interface Request {
    /**
     * Encounter something that would make the current form invalid.
     */
    void invalidate();
    /**
     * Obtains the Cloud that can be used if no Node available yet.
     */
    Cloud getCloud();
    /**
     *
     */
    //String getName(Field field);
    /**
     * Gets the user specified value for a field
     */
    Object getValue(Node node, Field field);

    /**
     * Handler implementation can put properties on the request to do some adminstration.
     */
    <C> C setProperty(Parameter<C> name, C value);
    <C> C getProperty(Parameter<C> name);
}

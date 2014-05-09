/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;


import org.mmbase.bridge.*;

/**
 * Interface for doing field processing.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.7
 */

public interface Processor extends java.io.Serializable {

    /**
     * @param node  The node object for which the field must be transformed.
     * @param field The field which is set.
     * @param value The (new) field value.
     */
    Object process(Node node, Field field, Object value);
}

/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.Locale;

/**
 * This interface represents a node's field type information object.
 *
 * @author Pierre van Rooden
 * @author Jaco de Groot
 * @version $Id: Field.java,v 1.19 2005-05-10 22:49:00 michiel Exp $
 */
public interface Field extends org.mmbase.core.FieldType {
    /**
     * Returns the node manager this field belongs to.
     *
     * @return  the node manager this field belongs to
     */
    public NodeManager getNodeManager();


    /**
     * Returns the description for this field.
     *
     * @return  the description for this field
     */
    public String getDescription();



}

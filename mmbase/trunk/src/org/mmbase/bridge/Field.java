/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;
import org.mmbase.module.core.*;

/**
 * This interface represents a node's field type information object.
 *
 * @author Pierre van Rooden
 * @author Jaco de Groot
 * @version $Id: Field.java,v 1.6 2002-02-22 14:28:24 michiel Exp $
 */
public interface Field {

    public final static int STATE_VIRTUAL    = 0;
    public final static int STATE_PERSISTENT = 2;
    public final static int STATE_SYSTEM     = 3;
    public final static int STATE_UNKNOWN    = -1;
    
    
    public final static int TYPE_STRING  = 1;
    public final static int TYPE_INTEGER = 2;
    public final static int TYPE_BYTE    = 4;
    public final static int TYPE_FLOAT   = 5;
    public final static int TYPE_DOUBLE  = 6;
    public final static int TYPE_LONG    = 7;
    public final static int TYPE_XML     = 8;
    public final static int TYPE_UNKNOWN = -1;
    
    /**
     * Returns the node manager this field belongs to.
     *
     * @return  the node manager this field belongs to
     */
    public NodeManager getNodeManager();

    /**
     * Returns the name of this field.
     *
     * @return  the name of this field
     */
    public String getName();

 	
    /**
     * Returns the GUI name for the data type this field contains.
     *
     * @return  the GUI name for the data type this field contains
     */
    public String getGUIType();

    /**
     * Returns the GUI name for this field.
     *
     * @return  the GUI name for this field
     */
    public String getGUIName();
    
    /**
     * Returns the identifier for the data type this field contains.
     *
     * @return  an <code>int</code> wich identifies the type of data this field
     *          contains
     */
    public int getType();
    
    /**
     * Returns this field's state identifier.
     *
     * @return  an <code>int</code> wich identifies the state of this field
     */
    public int getState();

    /**
     * Returns the maximum length of data this field can contain.
     * For example if a field contains characters the size indicates the
     * maximum number of characters it can contain.
     * If the field is a numeric field (such as an integer), the result is -1.
     *
     * @return  the maximum length of data this field can contain
     */

    public int getMaxLength();
	
}

/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @javadoc
 * @author Pierre van Rooden
 * @since  MMBase-1.8
 * @version $Id: MMBaseType.java,v 1.1 2005-06-28 14:01:40 pierre Exp $
 * @see org.mmbase.util.functions.Parameter
 */

public interface MMBaseType {

    /** MMBase base type identifier for the String data type */
    public final static int TYPE_STRING  = 1;
    /** MMBase base type identifier for the Integer data type */
    public final static int TYPE_INTEGER = 2;
    /** MMBase base type identifier for the binary (byte[]) data type */
    public final static int TYPE_BINARY    = 4;
//    public final static int TYPE_BYTE    = 4;
    /** MMBase base type identifier for the Float data type */
    public final static int TYPE_FLOAT   = 5;
    /** MMBase base type identifier for the Double data type */
    public final static int TYPE_DOUBLE  = 6;
    /** MMBase base type identifier for the Long data type */
    public final static int TYPE_LONG    = 7;
    /** MMBase base type identifier for the DOM Document data type */
    public final static int TYPE_XML     = 8;
    /** MMBase base type identifier for the Node data type */
    public final static int TYPE_NODE    = 9;
    /**
     * MMBase base type identifier for the Date data type
     * @since MMBase-1.8
     */
    public final static int TYPE_DATETIME  = 10;
    /**
     * MMBase base type identifier for the Boolean data type
     * @since MMBase-1.8
     */
    public final static int TYPE_BOOLEAN   = 11;
    /**
     * MMBase base type identifier for the List data type
     * @since MMBase-1.8
     */
    public final static int TYPE_LIST      = 12;
    /** MMBase base type identifier for data types whose type is unknown */
    public final static int TYPE_UNKNOWN = -1;

    /**
     * Returns the identifier for the MMBase base type for this data type.
     * @return  an <code>int</code> which identifies the base type
     */
    public int getType();

}

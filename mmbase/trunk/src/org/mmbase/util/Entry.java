/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

/**
 * Represents a pair of values ('key' and a 'value'). It is a straight-forward implementation of
 * {@link java.util.Map.Entry}, and can be used as a utility for Map implementations. 
 *
 * @since MMBase-1.8
 * @version $Id: Entry.java,v 1.3 2005-12-20 18:26:59 michiel Exp $
 * @author Michiel Meeuwissen
 */
public class Entry implements java.util.Map.Entry, PublicCloneable, java.io.Serializable {

    private Object key; // cannot be final because of cloneable/serializable, but logically, it could.
    private Object value;

    protected Entry() {
        // serializable
    }

    /**
     * @param k The key of this Map.Entry
     * @param v The value of this Map.Entry
     */
    public Entry(Object k, Object v) {
        key = k ;
        value = v;
    }
    public Entry(java.util.Map.Entry e) {
        key = e.getKey();
        value = e.getValue();
    }

    // see Map.Entry
    public Object getKey() {
        return key;
    }

    // see Map.Entry
    public Object getValue() {
        return value;
    }

    // see Map.Entry
    public Object setValue(Object v) {
        Object r = value;
        value = v;
        return r;
    }

    public Object clone() {
        return new Entry(key, value);
    }
    /**
     * A sensible toString, for debugging purposes ('&lt;key&gt;=&lt;value&gt;').
     */
    public String toString() {
        return key + "=" + value;
    }
}

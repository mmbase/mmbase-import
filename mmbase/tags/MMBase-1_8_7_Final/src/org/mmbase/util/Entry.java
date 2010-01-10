/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;
import java.util.Map;

/**
 * Represents a pair of values ('key' and a 'value'). It is a straight-forward implementation of
 * {@link java.util.Map.Entry}, and can be used as a utility for Map implementations. 
 *
 * @since MMBase-1.8
 * @version $Id$
 * @author Michiel Meeuwissen
 */
public final class Entry implements Map.Entry, PublicCloneable, java.io.Serializable {

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
    public Entry(Map.Entry e) {
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
        return new Entry(key, value); // can do this, because this class is final
    }

    public int hashCode() {
        return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
    }
    public boolean equals(Object o) {
        if (o instanceof Map.Entry) {
            Map.Entry entry = (Map.Entry) o;
            return
                (key == null ? entry.getKey() == null : key.equals(entry.getKey())) &&
                (value == null ? entry.getValue() == null : value.equals(entry.getValue()));
        } else {
            return false;
        }
    }
    /**
     * A sensible toString, for debugging purposes ('&lt;key&gt;=&lt;value&gt;').
     */
    public String toString() {
        return key + "=" + value;
    }
}

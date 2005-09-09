/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

/**
 * Represents a pair of values. Normally a 'key' and a 'value'.
 *
 * @since MMBase-1.8
 */
public class Entry implements java.util.Map.Entry, Cloneable, java.io.Serializable {

    private  Object key;
    private  Object value;

    protected Entry() {
        // serializable
    }

    public Entry (Object k, Object v) {
        key = k ; value = v;
    }
    public Object  getKey() { return key; }
    public Object  getValue() { return value; }

    public Object  setValue(Object v) { Object r = value; value = v;  return r;}
    
    public String toString() { return key + "=" + value; }
   
    
}

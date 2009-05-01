/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.util.*;

/**
 * Utility class for splitting delimited values.
 *
 * @author Pierre van Rooden
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class StringSplitter {

    /**
     * Simple util method to split delimited values to a list. Useful for attributes.
     * Similar to <code>String.split()</code>, but returns a List instead of an array, and trims the values.
     * @param string the string to split
     * @param delimiter
     * @return a (modifiable) List containing the elements
     */
    static public List<String> split(String string, String delimiter) {
        List<String> result = new ArrayList<String>();
        if (string == null) return result;
        for (String v : string.split(delimiter)) {
            result.add(v.trim());
        }
        return result;
    }


    /**
     * Simple util method to split comma separated values.
     * @see #split(String, String)
     * @param string the string to split
     * @return a List containing the elements
     */
    static public List<String> split(String string) {
        return split(string, ",");
    }

    /**
     * Splits up a String, (using comma delimiter), but takes into account brackets. So
     * a(b,c,d),e,f(g) will be split up in a(b,c,d) and e and f(g).
     * @since MMBase-1.8
     */
    static public List<String> splitFunctions(CharSequence attribute) {
        int commaPos =  0;
        int nested   =  0;
        List<String>  result = new ArrayList<String>();
        int i;
        int length   =  attribute.length();
        for(i = 0; i < length; i++) {
            char c = attribute.charAt(i);
            if ((c == ',') || (c == ';')){
                if(nested == 0) {
                    result.add(attribute.subSequence(commaPos, i).toString().trim());
                    commaPos = i + 1;
                }
            } else if (c == '(') {
                nested++;
            } else if (c == ')') {
                nested--;
            }
        }
        if (i > 0) {
            result.add(attribute.toString().substring(commaPos).trim());
        }
        return result;
    }

    /**
     * @since MMBase-1.9
     */
    static public Map<String, String> map(String string) {
        return map(string, ",");
    }
    /**
     * Splits a String into a map.
     * @param delimiter Delimiter to split entries. If this is a newline, then the string will be
     * read like properties
     * @since MMBase-1.9.1
     */
    static public Map<String, String> map(String string, String delimiter) {
        if (delimiter.equals("\n")) {
            final Properties props = new Properties();
            try {
                props.load(new java.io.ByteArrayInputStream(string.getBytes("ISO-8859-1")));
            } catch (java.io.UnsupportedEncodingException uee) {
                // ISO-8859-1 _IS_ supported
                new RuntimeException(uee);
            } catch (java.io.IOException ioe) {
                new RuntimeException(ioe);
            }
            // java sucks a bit, because for some reason (I can't imagine which one) Properties is
            // not a Map<String, String>. There we can't simply return the props,
            // but must perform the following horribleness.
            //
            // In java 1.6 there is 'solution', which probably will hardly help. (http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6253413)
            return  new AbstractMap<String, String>() {
                public Set<Map.Entry<String, String>> entrySet() {
                    return new AbstractSet<Map.Entry<String, String>>() {
                        public int size() {
                            return props.size();
                        }
                        public Iterator<Map.Entry<String, String>> iterator() {
                            return new Iterator<Map.Entry<String, String>>() {
                                private final Iterator<Map.Entry<Object, Object>> i = props.entrySet().iterator();
                                public boolean hasNext() {
                                    return i.hasNext();
                                }
                                public Map.Entry<String, String> next()  {
                                    Map.Entry<Object, Object> entry = i.next();
                                    return new org.mmbase.util.Entry<String, String>((String) entry.getKey(), (String) entry.getValue());
                                }
                                public void remove() {
                                    i.remove();
                                }
                            };
                        }
                    };
                }
            };

        } else {
            Map<String, String> map = new HashMap<String, String>();
            List<String> keyValues = split(string, delimiter);
            for (String kv : keyValues) {
                if ("".equals(kv)) continue;
                int is = kv.indexOf('=');
                map.put(kv.substring(0, is), kv.substring(is + 1));
            }
            return map;
        }
    }

}

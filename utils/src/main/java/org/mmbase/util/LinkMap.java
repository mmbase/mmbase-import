/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.util;

import java.util.*;

/**
 * Combines to Maps to one new map. One map is 'leading' and determins wich keys are mapped. The second map can override values, if it contains the same mapping.
 * There are several ways to describe what must happen with <em>changes</em> on the map.
 * You can e.g. maintain original values of a map <code>values</code> like so:
 <pre>
    Map originals = new HashMap();
    Map wrapper = new LinkMap(values, originals, LinkMap.Changes.CONSERVE);

    wrapper.put(key, value);

    Object newValue = values.get(key);
    Object originalValue = originals.get(key);
    assert originalValue == wrapper.get(key);
 </pre>
 * Changes on a map can be made temporay by wrapping it like this:
 <pre>
    Map wrapper = new LinkMap(values, new HashMap(), LinkMap.Changes.SECOND);
    wrapper.put(key, value);
    Object newValue = wrapper.get(key);
    Object oldValue = values.get(key);
    assert value == newValue;

 </pre>
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public class LinkMap<K, V> extends AbstractMap<K,V> {

    /**
     * Enum for the parameter of the constructor  {@link LinkMap#LinkMap(Map, Map, Changes)}
     */
    public enum Changes {
        /**
         * Changes are reflected in the 'first' map only. So they remain invisible if second maps contains the same key.
         */
        FIRST,
        /**
         * Changes are reflected in the 'second' map, map1 remains unmodified.
         */
         SECOND,
        /**
         * Changes are reflected in the both maps
         */
         BOTH,
        /**
         * Changes are reflected in the first map, but before that, the <em>old</em> value is copied to the second map (unless a mapping is already in that map).
         * This effectively creates a unmodifiable map, but the wrapped map is modified anyways.
         */
        CONSERVE,
        /**
         * No changes are allowed. The map behaves as an unmodifiable map (throwing {@link UnsupportedOperationException})
         */
         NONE;
    }
    private final Changes changes;
    private final Map<K, V> map1;
    private final Map<K, V> map2;
    /**
     * Creates a (modifiable) Linked Map. What precisely happens on modification is ruled by the <code>c</code> parameter.
     * @see Changes#FIRST
     * @see Changes#SECOND
     * @see Changes#BOTH
     * @see Changes#CONSERVE
     * @see Changes#NONE
     */
    public LinkMap(Map<K,V> m1, Map<K,V> m2, Changes c) {
        map1 = m1; map2 = m2;
        changes = c;
    }
    /**
     * Creates an unmodifiable Linked Map
     */
    public LinkMap(Map<K,V> m1, Map<K,V> m2) {
        this(m1, m2, Changes.NONE);
    }
    public Set<Map.Entry<K,V>> entrySet() {
        return new AbstractSet<Map.Entry<K,V>>() {
            public Iterator<Map.Entry<K,V>> iterator() {
                final Iterator<Map.Entry<K,V>> i = map1.entrySet().iterator();
                return new Iterator<Map.Entry<K,V>>() {
                    public boolean hasNext() {
                        return i.hasNext();
                        }
                    public Map.Entry<K,V> next() {
                        final Map.Entry<K, V> entry1 = i.next();
                        final K key = entry1.getKey();
                        return new Map.Entry<K, V>() {
                            public K getKey() {
                                return key;
                            }
                            public V getValue() {
                                if (map2.containsKey(key)) {
                                    return map2.get(key);
                                } else {
                                    return entry1.getValue();
                                }
                            }
                            public V setValue(V v) {
                                return LinkMap.this.put(key, v);
                            }
                        };
                    }
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
            public int size() {
                return map1.size();
            }
        };
    }
    @Override
    public int size() {
        return map1.size();
    }
    @Override
    public V get(Object key) {
        if (map2.containsKey(key)) {
            return map2.get(key);
        } else {
            return map1.get(key);
        }
    }
    @Override
    public V put(K key, V v) {
        V r = get(key);
        switch(changes) {
        case FIRST:      map1.put(key, v); break;
        case SECOND:     map2.put(key, v); break;
        case BOTH:       map1.put(key, v); map2.put(key, v); break;
        case CONSERVE:   if (! map2.containsKey(key)) { map2.put(key, r); } map1.put(key, v); break;
        case NONE:       throw new UnsupportedOperationException();
        }
        return r;
    }
    @Override
    public boolean containsKey(Object key) {
        return map1.containsKey(key);
    }

    public static void main(String[] args) {
        System.out.println("Please run with -ea");
        Map<String,String> values = new HashMap<String,String>();
        values.put("a", "A");
        values.put("b", "B");

        System.out.println("values: " + values);
        {
            final String key = "b"; final String value = "C";
            Map<String,String> originals = new HashMap<String,String>();
            Map<String,String> wrapper = new LinkMap<String,String>(values, originals, LinkMap.Changes.CONSERVE);

            wrapper.put(key, value);

            Object newValue = values.get(key);
            Object originalValue = originals.get(key);
            assert originalValue == wrapper.get(key);
            assert newValue == value;
            assert originalValue.equals("B");
            System.out.println("wrapper: " + wrapper);
            System.out.println("originals: " + originals);
            System.out.println("values: " + values);
        }

        {
            final String key = "b"; final String value = "D";
            Map<String,String> wrapper = new LinkMap<String,String>(values, new HashMap<String,String>(),
                                                                    LinkMap.Changes.SECOND);
            wrapper.put(key, value);
            Object newValue = wrapper.get(key);
            Object oldValue = values.get(key);
            assert value == newValue;
            assert newValue.equals("D");
            assert oldValue.equals("C");
            System.out.println("wrapper: " + wrapper);
            System.out.println("values: " + values);
        }

    }
}

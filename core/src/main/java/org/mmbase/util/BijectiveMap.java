package org.mmbase.util;

import java.util.*;

/**
 * A map representing a 1-1 bijective relation between 2 sets of values.
 *
 * @author Michiel Meeuwissen
 * @since MMBase-1.9.1
 * @version $Id$
 */


public class BijectiveMap<K, V> extends AbstractMap<K, V> {

    private final Map<K, V> backing;
    private final Map<V, K> inverse;

    public BijectiveMap() {
        backing = new HashMap<K, V>();
        inverse = new HashMap<V, K>();
    }
    private BijectiveMap(Map<K, V> backing, Map<V, K> inverse) {
        this.backing = backing;
        this.inverse = inverse;
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new AbstractSet<Map.Entry<K, V>>() {
            @Override
            public int size() {
                return backing.size();
            }
            @Override
            public Iterator<Map.Entry<K, V>> iterator() {
                return new Iterator<Map.Entry<K, V>>() {
                    private final Iterator<Map.Entry<K, V>> i = backing.entrySet().iterator();
                    private Map.Entry<K, V> entry;
                    public boolean hasNext() {
                        return i.hasNext();
                    }
                    public Map.Entry<K, V> next() {
                        entry = i.next();
                        return entry;
                    }
                    public void remove() {
                        i.remove();
                        inverse.remove(entry.getValue());

                    }
                };
            }
        };
    }

    @Override
    public V put(K key, V value) {
        if (backing.containsKey(key)) {
            V prevValue = backing.get(key);
            if (! prevValue.equals(value)) {
                if (inverse.containsKey(value)) throw new IllegalArgumentException();
                inverse.remove(prevValue);
                inverse.put(value, key);
                backing.put(key, value);
            }
            return prevValue;
        } else {
            if (inverse.containsKey(value)) {
                throw new IllegalArgumentException("Cannot put " + key + "->" + value + " Because the value already exists " + inverse);
            }
            backing.put(key, value);
            inverse.put(value, key);
            return null;
        }
    }

    public K inverseGet(V value) {
        return inverse.get(value);
    }
    public Map<V, K> getInverse() {
        return new BijectiveMap<V, K>(inverse, backing);
    }
}

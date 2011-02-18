/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.util;

import java.util.*;

import org.mmbase.bridge.*;

/**
 * A {@link java.util.Map} representing a Node. This class can be used if you need a bridge {@link org.mmbase.bridge.Node} object to look like a
 * {@link java.util.Map} (where the keys are the fields). Don't confuse this with {@link MapNode}.
 *
 * This object is also still a Node object.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.8
 */

public class NodeMap extends NodeWrapper implements Map<String, Object> {

    /**
     * @param node The Node which is wrapped, and is presented as a Map.
     */
    public NodeMap(Node node) {
        super(node);
    }

    // javadoc inherited
    @Override
    public void clear() {
        // the fields of a node are fixed by it's nodemanager.
        throw new UnsupportedOperationException("You cannot remove fields from a Node.");
    }

    // javadoc inherited
    @Override
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            return getNodeManager().hasField((String) key);
        } else {
            return false;
        }
    }

    // javadoc inherited
    // code copied from AbstractMap
    @Override
    public boolean containsValue(Object value) {
        Iterator<Entry<String, Object>>  i = entrySet().iterator();
        if (value==null) {
            while (i.hasNext()) {
                Entry<String, Object> e = i.next();
                if (e.getValue()==null) {
                    return true;
                }
            }
        } else {
            while (i.hasNext()) {
                Entry<String,Object>  e = i.next();
                if (value.equals(e.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    // javadoc inherited
    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException("You cannot remove fields from a Node.");
    }

    /**
     * Defaults to {@link #getValue}, but could e.g. be overridden with {@link #getValueWithoutProcess}
     * @since MMBase-1.9.2
     */
    protected Object getValueForMap(String field) {
        return getValue(field);
    }
    /**
     * Defaults to {@link #setValue}, but could e.g. be overridden with {@link #setValueWithoutProcess}
     * @since MMBase-1.9.2
     */
    protected void setValueForMap(String field, Object value) {
        setValue(field, value);
    }

    // javadoc inherited
    @Override
    public Set<Entry<String, Object>> entrySet() {
        return new AbstractSet<Entry<String, Object>>() {
            FieldList fields = getNodeManager().getFields();
            @Override
            public Iterator<Entry<String, Object>> iterator() {
                return new Iterator<Entry<String, Object>>() {
                    FieldIterator i = fields.fieldIterator();
                    @Override
                    public boolean hasNext() {
                        return i.hasNext();
                    }
                    @Override
                    public Entry<String, Object> next() {
                        return new Map.Entry<String, Object>() {
                            Field field = i.nextField();
                            @Override
                            public String getKey() {
                                return field.getName();
                            }
                            @Override
                            public Object getValue() {
                                return NodeMap.this.getValueForMap(field.getName());
                            }
                            @Override
                            public Object setValue(Object value) {
                                Object r = getValue();
                                NodeMap.this.setValueForMap(field.getName(), value);
                                return r;
                            }
                            @Override
                            public String toString() {
                                return getKey() + "=" + NodeMap.this.getValueWithoutProcess(field.getName());
                            }
                        };
                    }
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("You cannot remove fields from a Node.");
                    }
                };
            }
            @Override
            public int size() {
                return fields.size();
            }
        };
    }

    // javadoc inherited
    // todo: could be modifiable?
    @Override
    public Collection<Object> values() {
        return new AbstractCollection<Object>() {
            FieldList fields = getNodeManager().getFields();
            @Override
            public Iterator<Object> iterator() {
                return new Iterator<Object>() {
                    FieldIterator i = fields.fieldIterator();
                    @Override
                    public boolean hasNext() {
                        return i.hasNext();
                    }
                    @Override
                    public Object next() {
                        Field field = i.nextField();
                        return NodeMap.this.getValueForMap(field.getName());
                    }
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("You cannot remove fields from a Node.");
                    }
                };
            }
            @Override
            public int size() {
                return fields.size();
            }
        };
    }

    // javadoc inherited
    @Override
    public Set<String> keySet() {
        return new AbstractSet<String>() {
                FieldList fields = getNodeManager().getFields();
                @Override
                public Iterator<String> iterator() {
                    return new Iterator<String>() {
                        FieldIterator i = fields.fieldIterator();
                        @Override
                        public boolean hasNext() {
                            return i.hasNext();
                        }
                        @Override
                        public String next() {
                            Field field = i.nextField();
                            return field.getName();
                        }
                        @Override
                        public void remove() {
                            throw new UnsupportedOperationException("You cannot remove fields from a Node.");
                        }
                    };
            }
            @Override
            public int size() {
                return fields.size();
            }
            };
    }

    // javadoc inherited
    @Override
    public void putAll(Map<? extends String, ?> map) {
        for (java.util.Map.Entry<? extends String, ?> e : map.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public Object put(String key, Object value) {
        Object r = getValueForMap(key);
        setValueForMap(key, value);
        return r;
    }

    @Override
    public Object get(Object key) {
        return getValueForMap((String) key);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int size() {
        return getNodeManager().getFields().size();
    }

    @Override
    public String toString() {
        return entrySet().toString();
    }

}


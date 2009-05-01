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
 * @version $Id: NodeMap.java,v 1.7 2009-04-30 14:39:01 michiel Exp $
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
    public void clear() {
        // the fields of a node are fixed by it's nodemanager.
        throw new UnsupportedOperationException("You cannot remove fields from a Node.");
    }

    // javadoc inherited
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            return getNodeManager().hasField((String) key);
        }
        else {
            return false;
        }
    }

    // javadoc inherited
    // code copied from AbstractMap
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
    public Object remove(Object key) {
        throw new UnsupportedOperationException("You cannot remove fields from a Node.");
    }

    // javadoc inherited
    public Set<Entry<String, Object>> entrySet() {
        return new AbstractSet<Entry<String, Object>>() {
                FieldList fields = getNodeManager().getFields();
                @Override
                public Iterator<Entry<String, Object>> iterator() {
                    return new Iterator<Entry<String, Object>>() {
                            FieldIterator i = fields.fieldIterator();
                            public boolean hasNext() { return i.hasNext();}
                            public Entry<String, Object>  next() {
                                return new Map.Entry<String, Object>() {
                                        Field field = i.nextField();
                                        public String getKey() {
                                            return field.getName();
                                        }
                                        public Object getValue() {
                                            return NodeMap.this.getValue(field.getName());
                                        }
                                        public Object setValue(Object value) {
                                            Object r = getValue();
                                            NodeMap.this.setValue(field.getName(), value);
                                            return r;
                                        }
                                        public String toString() {
                                            return getKey() + "=" + NodeMap.this.getValueWithoutProcess(field.getName());
                                        }
                                    };
                            }
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
    public Collection<Object> values() {
        return new AbstractCollection<Object>() {
                FieldList fields = getNodeManager().getFields();
                @Override
                public Iterator<Object> iterator() {
                    return new Iterator<Object>() {
                            FieldIterator i = fields.fieldIterator();
                            public boolean hasNext() { return i.hasNext();}
                            public Object  next() {
                                Field field = i.nextField();
                                return NodeMap.this.getValue(field.getName());
                            }
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
    public Set<String> keySet() {
        return new AbstractSet<String>() {
                FieldList fields = getNodeManager().getFields();
                @Override
                public Iterator<String> iterator() {
                    return new Iterator<String>() {
                            FieldIterator i = fields.fieldIterator();
                            public boolean hasNext() { return i.hasNext();}
                            public String  next() {
                                Field field = i.nextField();
                                return field.getName();
                            }
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
    public void putAll(Map<? extends String, ? extends Object> map) {
        for (java.util.Map.Entry<? extends String, ? extends Object> e : map.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    public Object put(String key, Object value) {
        Object r = getValue(key);
        setValue(key, value);
        return r;
    }

    public Object get(Object key) {
        return getValue((String) key);
    }

    public boolean isEmpty() {
        return false;
    }

    public int size() {
        return getNodeManager().getFields().size();
    }

    @Override
    public String toString() {
        return entrySet().toString();
    }

}


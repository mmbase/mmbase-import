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
 * A Map representing a Node. This class can be used if you need a bridge Node object to look like a
 * Map (where the keys are the fields).
 *
 * This object is also still a Node object.
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.8
 */

public class NodeMap extends NodeWrapper implements Map {

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
        return getNodeManager().hasField((String) key);
    }

    // javadoc inherited
    // code copied from AbstractMap
    public boolean containsValue(Object value) {
        Iterator  i = entrySet().iterator();
        if (value==null) {
            while (i.hasNext()) {
                Entry e = (Entry) i.next();
                if (e.getValue()==null) {
                    return true;
                }
            }
        } else {
            while (i.hasNext()) {
                Entry  e = (Entry) i.next();
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
    public Set entrySet() {
        return new AbstractSet() {
                FieldList fields = getNodeManager().getFields();
                public Iterator iterator() {
                    return new Iterator() {
                            FieldIterator i = fields.fieldIterator();
                            public boolean hasNext() { return i.hasNext();}
                            public Object  next() {
                                return new Map.Entry() {
                                        Field field = i.nextField();
                                        public Object getKey() {
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
                                    };
                            }
                            public void remove() {
                                throw new UnsupportedOperationException("You cannot remove fields from a Node.");
                            }
                        };
                }
                public int size() {
                    return fields.size();
                }
            };
    }

    // javadoc inherited
    // todo: could be modifiable?
    public Collection values() {
        return new AbstractCollection() {
                FieldList fields = getNodeManager().getFields();
                public Iterator iterator() {
                    return new Iterator() {
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
                public int size() {
                    return fields.size();
                }
            };
    }

    // javadoc inherited
    public Set keySet() {
        return new AbstractSet() {
                FieldList fields = getNodeManager().getFields();
                public Iterator iterator() {
                    return new Iterator() {
                            FieldIterator i = fields.fieldIterator();
                            public boolean hasNext() { return i.hasNext();}
                            public Object  next() {
                                Field field = i.nextField();
                                return field.getName();
                            }
                            public void remove() {
                                throw new UnsupportedOperationException("You cannot remove fields from a Node.");
                            }
                        };
                }
                public int size() {
                    return fields.size();
                }
            };
    }

    // javadoc inherited
    public void putAll(Map map) {
        Iterator i = map.entrySet().iterator();
        while (i.hasNext()) {
            Entry e = (Entry) i.next();
            setValue((String) e.getKey(), e.getValue());
        }
    }

    // javadoc inherited
    public Object put(Object key, Object value) {
        Object r = getValue((String) key);
        setValue((String) key, value);
        return r;
    }

    // javadoc inherited
    public Object get(Object key) {
        return getValue((String) key);
    }

    // javadoc inherited
    public boolean isEmpty() {
        return false;
    }

    // javadoc inherited
    public int size() {
        return getNodeManager().getFields().size();
    }
}


package org.mmbase.util;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.NodeWrapper;
import org.mmbase.bridge.util.NodeMap;
import org.mmbase.bridge.util.MapNode;
import org.mmbase.datatypes.DataType;
import org.mmbase.datatypes.DataTypes;
import org.mmbase.util.transformers.CharTransformer;
import java.util.*;
import org.mmbase.util.logging.*;


/**
 * Plugged into {@link org.mmbase.util.Casting} to supply some extra casting based on the availability of an MMBase Bridge.
 * @since MMBase-2.0
 */
public class BridgeCaster implements Caster {

    private static final Logger log = Logging.getLoggerInstance(Casting.class);

    private static Cloud anonymousCloud;

    @Override
    public <C> C toType(Class<C> type, Object cloud, Object value) throws NotRecognized {
        if (type.equals(Node.class)) {
            try {
                if (cloud == null) {
                    if (anonymousCloud == null || ! anonymousCloud.getUser().isValid()) {
                        anonymousCloud = ContextProvider.getDefaultCloudContext().getCloud("mmbase");
                    }
                    cloud = anonymousCloud;
                }
                return (C) toNode(value, (Cloud) cloud);
            } catch (Exception e) {
                // suppose that that was because mmbase not running
                return (C) (value instanceof Node ? value : null);
            }
        } else if (type.equals(org.mmbase.datatypes.DataType.class)) {
            return (C) toDataType(value);
        } else if (type.equals(org.mmbase.security.Operation.class)) {
            return (C) org.mmbase.security.Operation.getOperation(Casting.toString(value));
        } else {
            throw NotRecognized.INSTANCE;
        }
    }

    @Override
    public Object wrap(final Object o, final CharTransformer escaper) throws NotRecognized {
        if (o instanceof Node) {
            return new NodeMap((Node)o) {

                @Override
                public Object getValue(String fieldName) {
                    NodeManager nm = getNodeManager();
                    if (nm.hasField(fieldName)) {
                        switch (nm.getField(fieldName).getType()) {
                        case org.mmbase.bridge.Field.TYPE_NODE:
                            // I don't understand why, but the 'number' field is of type NODE,
                            // which makes no sense whatsoever.
                            if (!"number".equals(fieldName)) {
                                return Casting.wrap(getNodeValue(fieldName), escaper);
                            } else {
                                return super.getStringValue(fieldName);
                            }
                        case org.mmbase.bridge.Field.TYPE_DATETIME:
                            return Casting.wrap(getDateValue(fieldName), escaper);
                        case org.mmbase.bridge.Field.TYPE_XML:
                            return Casting.wrap(getXMLValue(fieldName), escaper);
                        case org.mmbase.bridge.Field.TYPE_UNKNOWN:
                            log.debug("NodeManager " + nm + " has field " + fieldName + " but it is of unknown type.");
                            return Casting.wrap(super.getValueWithoutProcess(fieldName), escaper);
                        default:
                            return Casting.escape(escaper, Casting.toString(super.getValue(fieldName)));
                        }
                    } else {
                        return Casting.escape(escaper, Casting.toString(super.getValue(fieldName)));
                    }
                }
                @Override
                public String toString() {
                    int number = node.getNumber();
                    if (number != -1) {
                        return Casting.escape(escaper, "" + number);
                    } else {
                        return Casting.escape(escaper, node.getStringValue("_number"));
                    }
                }
            };
        } else if (o instanceof org.mmbase.bridge.NodeList) {
            return new NodeListWrapper((org.mmbase.bridge.NodeList) o, escaper);
        } else {
            throw NotRecognized.INSTANCE;
        }
    }

    @Override
    public Object unWrap(final Object o) throws NotRecognized {
        if (o instanceof NodeWrapper) {
            return ((NodeWrapper)o).getNode();
        } else if (o instanceof NodeListWrapper) {
            return ((NodeListWrapper)o).getCollection();
        } else {
            throw NotRecognized.INSTANCE;
        }
    }

    @Override
    public Map toMap(Object o) throws NotRecognized {
        if (o instanceof Node) {
            return new NodeMap((Node)o);
        } else if (o instanceof org.mmbase.util.functions.Parameters) {
            return ((org.mmbase.util.functions.Parameters) o).toMap();
        } else {
            throw NotRecognized.INSTANCE;
        }
    }
    @Override
    public int toInt(Object i) throws NotRecognized {
        if (i instanceof Node) {
            return ((Node)i).getNumber();
        } else {
            throw NotRecognized.INSTANCE;
        }
    }


    @Override
    public boolean toBoolean(Object i) throws NotRecognized {
        if (i instanceof Node) {
            return true;
        } else {
            throw NotRecognized.INSTANCE;
        }
    }

    @Override
    public boolean isStringRepresentable(Class<?> type) {
        return Node.class.isAssignableFrom(type);
    }



    @Override
    public long toLong(Object i) throws NotRecognized {
        if (i instanceof Node) {
            return ((Node)i).getNumber();
        } else {
            throw NotRecognized.INSTANCE;
        }
    }
    @Override
    public float toFloat(Object i) throws NotRecognized {
        if (i instanceof Node) {
            return ((Node)i).getNumber();
        } else {
            throw NotRecognized.INSTANCE;
        }
    }
    @Override
    public double toDouble(Object i) throws NotRecognized {
        if (i instanceof Node) {
            return ((Node)i).getNumber();
        } else {
            throw NotRecognized.INSTANCE;
        }
    }

    @Override
    public String toString(Object s) throws NotRecognized {
        if (s instanceof org.mmbase.bridge.Query) {
            return ((org.mmbase.bridge.Query) s).toSql();
        } else {
            throw NotRecognized.INSTANCE;
        }
    }

    /**
     * Convert an object to an Node.
     * If the value is Numeric, the method
     * tries to obtain the mmbase object with that number.
     * A <code>Map</code> returns a virtual <code>Node</code> representing the map, (a
     * {@link MapNode}).
     * All remaining situations return the node with the alias <code>i.toString()</code>, which can
     * be <code>null</code> if no node which such an alias.
     * @param i the object to convert
     * @param cloud the Cloud to use for loading a node
     * @return the value as a <code>Node</code>
     * @since MMBase-1.7
     */
    public static Node toNode(Object i, Cloud cloud) {
        Node res = null;
        if (i instanceof Node) {
            res = (Node)i;
        } else if (i instanceof Number) {
            int nodenumber = ((Number)i).intValue();
            if (nodenumber != -1 && cloud.hasNode(nodenumber)) {
                res = cloud.getNode(nodenumber);
            }
        } else if (i instanceof Map<?, ?>) {
            res = new MapNode((Map)i, cloud);
        } else if (i != null && !i.equals("")) {
            res = cloud.getNode(Casting.toString(i));
        }
        return res;
    }


    /**
     * @since MMBase-1.9.1
     */
    static public DataType<?> toDataType(Object o) {
        if (o instanceof DataType<?>) {
            return (DataType<?>) o;
        } else {
            return DataTypes.getDataType(Casting.toString(o));
        }

    }

    /**
     * @since MMBase-1.9
     */
    public static class NodeListWrapper extends org.mmbase.bridge.util.CollectionNodeList implements Casting.Unwrappable {
        private final CharTransformer escaper;
        NodeListWrapper(org.mmbase.bridge.NodeList list, CharTransformer e) {
            super(list);
            for (Map.Entry<Object, Object> entry : list.getProperties().entrySet()) {
                setProperty(entry.getKey(), entry.getValue());
            }
            escaper = e;
        }
        @Override
        public Node get(int index) {
            return (Node) Casting.wrap(super.get(index), escaper);
        }
        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder();
            Iterator<Node> i = iterator();
            boolean hasNext = i.hasNext();
            while (hasNext) {
                Casting.toStringBuilder(buf, i.next());
                hasNext = i.hasNext();
                if (hasNext) {
                    buf.append(',');
                }
            }
            return buf.toString();
        }

    }

    @Override
    public String toString() {
        return getClass().getName() + " for " + ContextProvider.getDefaultCloudContext();
    }



}
/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import org.mmbase.bridge.Field;
import org.mmbase.module.corebuilders.RelDef;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.Casting;

/**
 * @javadoc
 *
 * @author Rico Jansen
 * @version $Id$
 */
public class TemporaryNodeManager implements TemporaryNodeManagerInterface {

    private static final Logger log = Logging.getLoggerInstance(TemporaryNodeManager.class);

    /**
     * Return value for setObjectField
     */
    public static final String UNKNOWN = "unknown";
    /**
     * @since MMBase-1.8
     */
    public static final String INVALID_VALUE = "invalid value";

    private final MMBase mmbase;

    /**
     * @javadoc
     */
    public TemporaryNodeManager(MMBase mmbase) {
        this.mmbase = mmbase;
    }

    /**
     * @javadoc
     */
    public String createTmpNode(String type, String owner, String key) {
        if (log.isDebugEnabled()) {
            log.debug("createTmpNode : type=" + type + " owner=" + owner + " key=" + key);
        }
        // WTF!?
        //        if (owner.length() > 12) owner = owner.substring(0, 12);
        MMObjectBuilder builder = mmbase.getBuilder(type);
        MMObjectNode node;
        if (builder != null) {
            node = builder.getNewTmpNode(owner, getTmpKey(owner, key));
            if (log.isDebugEnabled()) {
                log.debug("New tmpnode " + node);
            }
        } else {
            log.error("Can't find builder " + type);
        }
        return key;
    }

    /**
     * @javadoc
     */
    public String createTmpRelationNode(String role,String owner,String key, String source,String destination) throws Exception {
        // decode type to a builder using reldef
        RelDef reldef = mmbase.getRelDef();
        int rnumber = reldef.getNumberByName(role, true);
        if(rnumber == -1) {
            throw new Exception("role " + role + " is not a proper relation");
        }
        MMObjectBuilder builder = reldef.getBuilder(reldef.getNode(rnumber));
        String bulname          = builder.getTableName();

        // Create node
        createTmpNode(bulname, owner, key);
        setObjectField(owner, key, "_snumber", getTmpKey(owner, source));
        setObjectField(owner, key, "_dnumber", getTmpKey(owner, destination));
        setObjectField(owner, key, "rnumber", "" + rnumber);
        return key;
    }

    /**
     * @javadoc
     */
    public String createTmpAlias(String name,String owner,String key, String destination) {
        MMObjectBuilder builder = mmbase.getOAlias();
        String bulname = builder.getTableName();

        // Create alias node
        createTmpNode(bulname, owner, key);
        setObjectField(owner, key, "_destination", getTmpKey(owner, destination));
        setObjectField(owner, key, "name", name);
        return key;
    }

    /**
     * @javadoc
     */
    public String deleteTmpNode(String owner,String key) {
        MMObjectBuilder b = mmbase.getBuilder("object");
        b.removeTmpNode(getTmpKey(owner, key));
        if (log.isDebugEnabled()) {
            log.debug("delete node " + getTmpKey(owner, key));
        }
        return key;
    }

    /**
     * @javadoc
     */
    public MMObjectNode getNode(String owner, String key) {
        MMObjectBuilder bul = mmbase.getBuilder("object");
        MMObjectNode node = bul.getTmpNode(getTmpKey(owner, key));
        // fallback to normal nodes
        if (node == null) {
            log.debug("getNode tmp not node found " + key);
            node = bul.getNode(key);
            if(node == null) throw new RuntimeException("Node not found !! (key = '" + key + "')");
        }
        return node;
    }

    /**
     * @javadoc
     */
    public String getObject(String owner, String key, String dbkey) {
        MMObjectBuilder bul = mmbase.getBuilder("object");
        MMObjectNode node = bul.getTmpNode(getTmpKey(owner, key));
        if (node == null) {
            log.debug("getObject not tmp node found " + key);
            node = bul.getHardNode(dbkey);
            if (node == null) {
                log.warn("Node not found in database " + dbkey);
            } else {
                bul.putTmpNode(getTmpKey(owner, key), node);
            }
        }
        if (node != null) {
            return key;
        } else {
            return null;
        }
    }

    /**
     * @javadoc
     * @return An empty string if succesfull, the string {@link #UNKNOWN} if the field was not found in the node.
     *         The string {@link #INVALID_VALUE} if the value was not valid for the field's type.
     */
    public String setObjectField(String owner, String key, String field, Object value) {
        MMObjectNode node = getNode(owner, key);
        if (node != null) {
            int type = node.getDBType(field);
            if (type >= 0) {
                if (value instanceof String) {
                    String stringValue = (String)value;
                    switch(type) {
                    case Field.TYPE_XML:
                    case Field.TYPE_STRING:
                        node.setValue(field, stringValue);
                        break;
                    case Field.TYPE_NODE:
                    case Field.TYPE_INTEGER:
                        try {
                            int i = -1;
                            if (!stringValue.equals("")) i = Integer.parseInt(stringValue);
                            node.setValue(field, i);
                        } catch (NumberFormatException x) {
                            log.debug("Value for field " + field + " is not a number '" + stringValue + "'");
                            return INVALID_VALUE;
                        }
                        break;
                    case Field.TYPE_BINARY:
                        log.error("We don't support casts from String to Binary");
                        return INVALID_VALUE; // so, a String value is invalid for binaries.
                    case Field.TYPE_FLOAT:
                        try {
                            float f = -1;
                            if (!stringValue.equals("")) f = Float.parseFloat(stringValue);
                            node.setValue(field,f);
                        } catch (NumberFormatException x) {
                            log.debug("Value for field " + field + " is not a number " + stringValue);
                            return INVALID_VALUE;
                        }
                        break;
                    case Field.TYPE_DOUBLE:
                        try {
                            double d = -1;
                            if (!stringValue.equals("")) d = Double.parseDouble(stringValue);
                            node.setValue(field,d);
                        } catch (NumberFormatException x) {
                            log.debug("Value for field " + field + " is not a number " + stringValue);
                            return INVALID_VALUE;
                        }
                        break;
                    case Field.TYPE_LONG:
                        try {
                            long l = -1;
                            if (!stringValue.equals("")) l = Long.parseLong(stringValue);
                            node.setValue(field,l);
                        } catch (NumberFormatException x) {
                            log.debug("Value for field " + field + " is not a number " + stringValue);
                            return INVALID_VALUE;
                        }
                        break;
                    case Field.TYPE_DATETIME:
                        try {
                            node.setValue(field, Casting.toDate(value));
                        } catch (Exception e) {
                            return INVALID_VALUE;
                        }
                        break;
                    case Field.TYPE_BOOLEAN:
                        if (org.mmbase.datatypes.StringDataType.BOOLEAN_PATTERN.matcher(stringValue).matches()) {
                            node.setValue(field, Casting.toBoolean(value));
                        } else {
                            return INVALID_VALUE;
                        }
                        break;
                    default:
                        log.error("Unknown type for field " + field);
                        break;
                    }
                } else {
                    node.setValue(field, value);
                }
            } else {
                node.setValue(field, value);
                log.warn("Invalid type for field " + field);
                return UNKNOWN;
            }
        } else {
            log.error("setObjectField(): Can't find node : "+key);
        }
        return "";
    }

    /**
     * @javadoc
     * @deprecated use {@link #getObjectField}
     */
    public String getObjectFieldAsString(String owner,String key,String field) {
        String rtn;
        MMObjectNode node = getNode(owner,key);
        if (node == null) {
            log.error("getObjectFieldAsString(): node " + key + " not found!");
            rtn = "";
        } else {
            rtn = node.getStringValue(field);
        }
        return rtn;
    }

    /**
     * @javadoc
     */
    public Object getObjectField(String owner,String key,String field) {
        Object rtn;
        MMObjectNode node = getNode(owner,key);
        if (node == null) {
            log.error("getObjectFieldAsString(): node " + key + " not found!");
            rtn = "";
        } else {
            rtn = node.getStringValue(field);
        }
        return rtn;
    }

    /**
     * @javadoc
     */
    private String getTmpKey(String owner,String key) {
        return owner + "_" + key;
    }
}

/* -*- tab-width: 4; -*-

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;
import java.lang.Exception;

import org.mmbase.util.*;
import org.mmbase.module.corebuilders.FieldDefs;
import org.mmbase.module.corebuilders.RelDef;
import org.mmbase.module.corebuilders.InsRel;
import org.mmbase.module.corebuilders.OAlias;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Rico Jansen
 * @version $Id: TemporaryNodeManager.java,v 1.25 2001-09-10 08:24:38 pierre Exp $
 */
public class TemporaryNodeManager implements TemporaryNodeManagerInterface {

    private static Logger log = Logging.getLoggerInstance(TemporaryNodeManager.class.getName());

    private MMBase mmbase;

    public TemporaryNodeManager(MMBase mmbase) {
        this.mmbase=mmbase;
    }

    public String createTmpNode(String type,String owner,String key) {
        log.debug("createTmpNode : type=" + type + " owner=" + owner + " key=" + key);
        if (owner.length()>12) owner=owner.substring(0,12);
        MMObjectBuilder builder=mmbase.getMMObject(type);
        MMObjectNode node;
        if (builder!=null) {
            node=builder.getNewTmpNode(owner,getTmpKey(owner,key));
            log.debug("New tmpnode " + node);
        } else {
            log.error("Can't find builder " + type);
        }
        return key;
    }

    public String createTmpRelationNode(String type,String owner,String key, String source,String destination) throws Exception {
        String bulname="";
        MMObjectNode node=null;
        MMObjectBuilder builder=null;
        RelDef reldef;
        int rnumber;

        // decode type to a builder using reldef
        reldef=mmbase.getRelDef();
        rnumber=reldef.getGuessedByName(type);
        if(rnumber==-1) {
            throw new Exception("type "+type+" is not a proper relation");
        }
        builder = reldef.getBuilder(reldef.getNode(rnumber));
        bulname=builder.getTableName();

        // Create node
        createTmpNode(bulname,owner,key);
        builder.checkAddTmpField("_snumber");
        builder.checkAddTmpField("_dnumber");
        setObjectField(owner,key,"_snumber",getTmpKey(owner,source));
        setObjectField(owner,key,"_dnumber",getTmpKey(owner,destination));
        setObjectField(owner,key,"rnumber",""+rnumber);
        return key;
    }

    public String createTmpAlias(String name,String owner,String key, String destination) {
        MMObjectBuilder builder=mmbase.getOAlias();
        String bulname=builder.getTableName();

        // Create alias node
        createTmpNode(bulname,owner,key);
        builder.checkAddTmpField("_destination");
        setObjectField(owner,key,"_destination",getTmpKey(owner,destination));
        setObjectField(owner,key,"name",name);
        return key;
    }

    public String deleteTmpNode(String owner,String key) {
        MMObjectBuilder b=mmbase.getMMObject("typedef");
        b.removeTmpNode(getTmpKey(owner,key));
        log.debug("delete node " + getTmpKey(owner,key));
        return key;
    }

    public MMObjectNode getNode(String owner,String key) {
        MMObjectBuilder bul=mmbase.getMMObject("typedef");
        MMObjectNode node;
        node=bul.getTmpNode(getTmpKey(owner,key));
        // fallback to normal nodes
        if (node==null) {
            log.debug("getNode tmp not node found " + key);
            node=bul.getNode(key);
            if(node==null) throw new java.lang.RuntimeException("Node not found !! (key = '" + key + "')");
        }
        node.parent.checkAddTmpField("_number");
        if (node.parent instanceof InsRel) {
            node.parent.checkAddTmpField("_snumber");
            node.parent.checkAddTmpField("_dnumber");
        }
                if (node.parent instanceof OAlias) {
                    node.parent.checkAddTmpField("_destination");
                }
        return node;
    }

    public String getObject(String owner,String key,String dbkey) {
        MMObjectBuilder bul=mmbase.getMMObject("typedef");
        MMObjectNode node;
        node=bul.getTmpNode(getTmpKey(owner,key));
        if (node==null) {
            log.debug("getObject not tmp node found " + key);
            node=bul.getHardNode(dbkey);
            if (node==null) {
                log.warn("Node not found in database " + dbkey);
            } else {
                bul.putTmpNode(getTmpKey(owner,key),node);
            }
        }
        if (node != null) {
            node.parent.checkAddTmpField("_number");
            if (node.parent instanceof InsRel) {
                node.parent.checkAddTmpField("_snumber");
                node.parent.checkAddTmpField("_dnumber");
            }
            if (node.parent instanceof OAlias) {
                node.parent.checkAddTmpField("_destination");
            }
            return key;
        } else {
            return null;
        }
    }

    public String setObjectField(String owner,String key,String field,Object value) {
        MMObjectNode node;
        int i;float f;double d;long l;
        String stringValue;

        // Memo next can be done by new MMObjectNode.setValue
        node=getNode(owner,key);
        if (node!=null) {
            int type=node.getDBType(field);
            if (type>=0) {
                if (value instanceof String) {
                    stringValue=(String)value;
                    switch(type) {
                        case FieldDefs.TYPE_STRING:
                            node.setValue(field, stringValue);
                            break;
                        case FieldDefs.TYPE_INTEGER:
                            try {
                                i=Integer.parseInt(stringValue);
                                node.setValue(field,i);
                            } catch (NumberFormatException x) {
                                log.error("Value for field " + field + " is not a number " + stringValue);
                            }
                            break;
                        case FieldDefs.TYPE_BYTE:
                            log.error("We don't support casts from String to Byte");
                            break;
                        case FieldDefs.TYPE_FLOAT:
                            try {
                                f=Float.parseFloat(stringValue);
                                node.setValue(field,f);
                            } catch (NumberFormatException x) {
                                log.error("Value for field " + field + " is not a number " + stringValue);
                            }
                            break;
                        case FieldDefs.TYPE_DOUBLE:
                            try {
                                d=Double.parseDouble(stringValue);
                                node.setValue(field,d);
                            } catch (NumberFormatException x) {
                                log.error("Value for field " + field + " is not a number " + stringValue);
                            }
                            break;
                        case FieldDefs.TYPE_LONG:
                            try {
                                l=Long.parseLong(stringValue);
                                node.setValue(field,l);
                            } catch (NumberFormatException x) {
                                log.error("Value for field "+field+" is not a number "+stringValue);
                            }
                            break;
                        default:
                            log.error("Unknown type for field "+field);
                            break;
                    }
                } else {
                    node.setValue(field,value);
                }
            } else {
                node.setValue(field,value);
//                debug("Invalid type for field "+field);
                return "unknown";
            }
        } else {
            log.error("setObjectField(): Can't find node : "+key);
        }
        return "";
    }


    public String getObjectFieldAsString(String owner,String key,String field) {
        String rtn;
        MMObjectNode node;
        node=getNode(owner,key);
        if (node==null) {
            log.error("getObjectFieldAsString(): node " + key + " not found!");
            rtn="";
        } else {
            rtn=node.getValueAsString(field);
        }
        return rtn;
    }

    public Object getObjectField(String owner,String key,String field) {
        Object rtn;
        MMObjectNode node;
        node=getNode(owner,key);
        if (node==null) {
            log.error("getObjectFieldAsString(): node " + key + " not found!");
            rtn="";
        } else {
            rtn=node.getValueAsString(field);
        }
        return rtn;
    }

    private String getTmpKey(String owner,String key) {
        return owner+"_"+key;
    }
}

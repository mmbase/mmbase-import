/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.community;

import org.mmbase.bridge.*;

import org.mmbase.module.core.*;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Describes a Node for holding temporary Message information.
 *
 * @author Rob Vermeulen
 * @author Pierre van Rooden
 */
public class MessageNode implements Node {

    private Map values = new HashMap();
    private Cloud cloud = null;
    private int number=-1;
    private NodeManager messageManager=null;
    private Node message=null;

    MessageNode(Cloud cloud) {
        this(cloud,null);
    }

    MessageNode(Cloud cloud, String alias) {
        this.messageManager=cloud.getNodeManager("message");
        if (alias!=null) {
            message=cloud.getNode(alias);
            if (message.getNodeManager()!=messageManager) {
                throw new BridgeException("Message with number/alias "+alias+" not found or of invalid type");
            }
            this.number=message.getNumber();
        }
    };

    private Node getMsg() {
        if (message==null) {
            throw new BridgeException("Operation not available within a post");
        }
        return message;
    }

    public Cloud getCloud() {
        return cloud;
    }

    public NodeManager getNodeManager() {
        return messageManager;
    };

    public int getNumber() {
        return number;
    };

    public void setValue(String fieldname, Object value) {
        values.put(fieldname,value);
    };

    public void setIntValue(String fieldname, int value) {
        setValue(fieldname,new Integer(value));
    }

    public void setFloatValue(String fieldname, float value) {
        setValue(fieldname,new Float(value));
    }

    public void setDoubleValue(String fieldname, double value) {
        setValue(fieldname,new Double(value));
    }

    public void setByteValue(String fieldname, byte[] value) {
        setValue(fieldname,value);
    }

    public void setLongValue(String fieldname, long value) {
        setValue(fieldname,new Long(value));
    }

    public void setStringValue(String fieldname, String value) {
        setValue(fieldname,value);
    }

    public Object getValue(String fieldname) {
        Object val=values.get(fieldname);
        if (val==null && message!=null) {
            val=message.getValue(fieldname);
        }
        return val;
    }

    public boolean getBooleanValue(String fieldname) {
        return getMsg().getBooleanValue(fieldname);
    }

    public Node getNodeValue(String fieldname) {
        return getMsg().getNodeValue(fieldname);
    }

    public int getIntValue(String fieldname) {
        return getMsg().getIntValue(fieldname);
    }

    public float getFloatValue(String fieldname) {
        return getMsg().getFloatValue(fieldname);
    }

    public long getLongValue(String fieldname) {
        return getMsg().getLongValue(fieldname);
    }

    public double getDoubleValue(String fieldname) {
        return getMsg().getDoubleValue(fieldname);
    }

    public byte[] getByteValue(String fieldname) {
        return getMsg().getByteValue(fieldname);
    }

    public String getStringValue(String fieldname) {
        Object objectvalue=getValue(fieldname);
        if (objectvalue==null) {
            return "";
        } else {
            return objectvalue.toString();
        }
    }

    public org.w3c.dom.Element  getXMLValue(String fieldname, org.w3c.dom.Document tree) {
        invalidOperation();  // not implemented
        return null;
    }

    private void invalidOperation() {
        throw new BridgeException("Operation not available: temporary message node");
    }

    public void commit() { invalidOperation(); }

    public void cancel() { invalidOperation(); }

    public void delete() { invalidOperation(); }

    public void delete(boolean deleteRelations) { invalidOperation(); }

    public String toString() {
        return "";
    }

    public boolean hasRelations() {
        return getMsg().hasRelations();
    }

    public void deleteRelations() { invalidOperation(); }

    public void deleteRelations(String relationManager) { invalidOperation(); }

    public RelationList getRelations() {
        return getMsg().getRelations();
    }

    public RelationList getRelations(String role) {
        return getMsg().getRelations(role);
    }

    public RelationList getRelations(String role, String nodeManager) {
        return getMsg().getRelations(role,nodeManager);
    }

    public int countRelations() {
        return getMsg().countRelations();
    }

    public int countRelations(String relationManager) {
        return getMsg().countRelations(relationManager);
    }

    public NodeList getRelatedNodes() {
        return getMsg().getRelatedNodes();
    }

    public NodeList getRelatedNodes(String nodeManager) {
        return getMsg().getRelatedNodes(nodeManager);
    }

    public int countRelatedNodes(String type) {
        return getMsg().countRelatedNodes(type);
    }

    public StringList getAliases() {
        return getMsg().getAliases();
    }

    public void createAlias(String alias) { invalidOperation(); }

    public void deleteAlias(String alias) { invalidOperation(); }

    public Relation createRelation(Node destinationNode,
                                   RelationManager relationManager) {
        invalidOperation();
        return null;
    }

    public void setContext(String context) { invalidOperation(); }

    public String getContext() {
        return getMsg().getContext();
    }

    public StringList getPossibleContexts() {
        return getMsg().getPossibleContexts();
    }

    public boolean mayWrite() {
        return true;
    }

    public boolean mayDelete() {
        return false;
    }

    public org.w3c.dom.Element toXML(org.w3c.dom.Document tree) {
        invalidOperation();  // not implemented
        return null;
    }
    public org.w3c.dom.Element toXML(org.w3c.dom.Document tree, FieldList f) {
        invalidOperation();  // not implemented
        return null;

    }
    public org.w3c.dom.Element toXML(org.w3c.dom.Document tree, Field f) {
        invalidOperation();  // not implemented
        return null;
    }
    public boolean mayLink() {
        return false;
    }

    public boolean mayChangeContext() {
        return true;
    }

}

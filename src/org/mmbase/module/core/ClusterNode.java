/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;

import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;


/**
 * ClusterNode combines fields of different nodes in a single "virtual" node.<br/>
 * This corresponds to the way that an SQL "join" select statement combines 
 * fields of different tables in result rows.
 * <p>
 * The individual fields are retrieved from a set of related nodes using a 
 * multilevel query, i.e. a query joining tables using the relations between 
 * the tables.
 * <p>
 * This class overrides a number of methods, allowing direct access to data in
 * the nodes which form the 'virtual' node.
 * <br />
 * In future releases, data will NOT be stored directkly in this node anymore.
 * Instead, it will be stored in the underlying MMObjectNodes.
 * For reasons of optiomalization, however, we cannot do this right now.
 * MMObjectNode will need a status field that allows us to recognize whether
 * it is fully loaded, partially loaded, or being edited.
 * This can then be checked in 'retrievevalue'.
 * In addition, to prevent caching conflicts, nodes will need to maintain
 * their references. This allows for a secure caching mechanism.
 * <br />
 * Among other things, this allows one to change values in a multilevel node,
 * or to access functionality that would otherwise be restricted to 'real'
 * nodes.
 *
 * @author Pierre van Rooden
 * @version $Id: ClusterNode.java,v 1.13 2003-12-01 14:42:41 robmaris Exp $
 * @see ClusterBuilder
 */
public class ClusterNode extends VirtualNode {

    private static final Logger log = Logging.getLoggerInstance(ClusterNode.class);

    /**
     * Holds the name - value pairs of related nodes in this virtual node.
     */
    protected Hashtable nodes = null; // why is it synchronized?

    /**
     * Main contructor.
     * @param parent the node's parent, generally an instance of the ClusterBuilder builder.
     */
    public ClusterNode(MMObjectBuilder parent) {
        super(parent);
        nodes = new Hashtable();
    }

    /**
     * Main contructor.
     * @param parent the node's parent, generally an instance of the ClusterBuilder builder.
     * @param rnofnodes Nr of referenced nodes.
     */
    public ClusterNode(MMObjectBuilder parent, int nrofnodes) {
        super(parent);
        nodes = new Hashtable(nrofnodes);
    }

    /**
     * Tests whether the data in a node is valid (throws an exception if this is not the case).
     * The call is performed on all loaded 'real' nodes. If a 'real' node has not previously been
     * forcably loaded, it is assumed to be correct.
     * @throws org.mmbase.module.core.InvalidDataException
     *      If the data was unrecoverably invalid
     *      (the references did not point to existing objects)
     */
    public void testValidData() throws InvalidDataException { // why is it public?
        for (Enumeration r = nodes.elements(); r.hasMoreElements(); ) {
          ((MMObjectNode)r.nextElement()).testValidData();
        }
    };

    /**
      * commit : commits the node to the database or other storage system.
      * This can only be done on a existing (inserted) node. it will use the
      * changed Vector as its base of what to commit/changed
      * @return <code>true</code> if the commit was succesfull, <code>false</code> is it failed
      */
    public boolean commit() {
        boolean res = true;
        for (Enumeration r = nodes.elements(); r.hasMoreElements(); ) {
            MMObjectNode n = (MMObjectNode)r.nextElement();
            if(n.isChanged()) {
                res = res && n.commit();
            }
        }
        return res;
    }

    /**
     * Obtain the 'real' nodes, associated with a specified objectbuilder.
     * @param buildername the name of the builder of the requested node, as known
     *        within the virtual node
     * @return the node, or <code>null</code> if it does not exist or is unknown
     */
    public MMObjectNode getRealNode(String builderName) {
        if (builderName == null) return null;
        MMObjectNode node = (MMObjectNode) nodes.get(builderName);
        if (node != null) return node;
        Integer number = (Integer) retrieveValue(builderName + ".number");
        if (number != null) {
            node = parent.getNode(number.intValue());
            if (node != null) nodes.put(builderName, node);
            return node;
        }
        return null;
    }

    /**
     * Stores a value in the values hashtable.
     * If the value is not stored in the virtualnode,
     * the 'real' node is used instead.
     * @param fieldname the name of the field to change
     * @param fieldValue the value to assign
     */
    protected void storeValue(String fieldName, Object fieldValue) {
        MMObjectNode node = (MMObjectNode)nodes.get(getBuilderName(fieldName));
        if (node != null) {
            node.values.put(ClusterBuilder.getFieldNameFromField(fieldName), fieldValue);
        } else {
            values.put(fieldName, fieldValue);
        }
    }

    /**
     * Sets a key/value pair in the main values of this node.
     * Note that if this node is a node in cache, the changes are immediately visible to
     * everyone, even if the changes are not committed.
     * The fieldname is added to the (public) 'changed' vector to track changes.
     * @param fieldname the name of the field to change
     * @param fieldValue the value to assign
     * @return always <code>true</code>
     */
    public boolean setValue(String fieldName, Object fieldValue) {
        // Circument interference by the database during initial loading of the node
        // This is not pretty, but the alternative is rewriting all support classes...
        if (initializing) {
            if (fieldValue == null) fieldValue = MMObjectNode.VALUE_NULL;
            if (! (parent instanceof ClusterBuilder)) {
                values.put(ClusterBuilder.getFieldNameFromField(fieldName), fieldValue);
            } else {
                values.put(fieldName, fieldValue);
            }
            return true;
        }
        String builderName = getBuilderName(fieldName);

        MMObjectNode n     = getRealNode(builderName);
        if (n != null) {
            String realFieldName = ClusterBuilder.getFieldNameFromField(fieldName);
            n.setValue(realFieldName, fieldValue);
            values.remove(fieldName);
            return true;
        }
        log.warn("Could not set field '" + fieldName + "')");
        return false; // or throw exception?
    }

    /**
     * Determines the builder name of a specified fieldname, i.e.
     * "news" in "news.title",
     * @param fieldname the name of the field
     * @return the buidler name of the field
     */
    protected String getBuilderName(String fieldName) {
        int pos = fieldName.indexOf(".");
        if (pos == -1) {
            return null;
        } else {
            String builderName = fieldName.substring(0, pos);
            int pos2 = builderName.lastIndexOf("(");
            builderName = builderName.substring(pos2 + 1);
            // XXX: we should check on commas and semicolons too... ?
            return builderName;
        }
    }

    // MM: special arrangment for if parent is not ClusterBuilder.
    // could give NPE so this is a fix...  (1.7)
    public MMObjectBuilder getBuilder() {
        if (parent instanceof ClusterBuilder) {
            return super.getBuilder();
        } else {
            return parent;
        }

    }

    /**
     * Get a value of a certain field.
     * @param fieldname the name of the field who's data to return
     * @return the field's value as an <code>Object</code>
     */
    public Object getValue(String fieldName) {
        String builder = getBuilderName(fieldName);
        if (builder == null) {
            // there is no 'builder' specified,
            // so the fieldname itself is a builder name
            // -> so return the MMObjectNode for that buidler
            if (parent instanceof ClusterBuilder) {
                return getRealNode(fieldName);
            }

        }
        Object o = super.getValue(fieldName);
        if (o == null) {
            // the normal approach does not yield results.
            // get the value from the original builder
            String builderName = getBuilderName(fieldName);
            MMObjectNode n = getRealNode(builderName);
            if (n!=null) {
                o = n.getValue(((ClusterBuilder)parent).getFieldNameFromField(fieldName));
            } else {
                // fall back to builder if this node doesn't contain a number to fetch te original
                MMObjectBuilder bul = parent.mmb.getMMObject(builderName);
                if (bul != null) {
                    o = bul.getValue(this, fieldName);
                }
            }
        }
        return o;
    }


    /**
     * Get a value of a certain field.
     * The value is returned as a String. Non-string values are automatically converted to String.
     * @param fieldname the name of the field who's data to return
     * @return the field's value as a <code>String</code>
     */
    public String getStringValue(String fieldName) {

        // try to get the value from the values table
        String tmp = "";
        Object o = getValue(fieldName);
        if (o != null) {
            tmp = "" + o;
        }
        // check if the object is shorted
        if (tmp.indexOf("$SHORTED")==0) {
            log.debug("getStringValue(): node=" + this + " -- fieldName " + fieldName);
            // obtain the database type so we can check if what
            // kind of object it is. this have be changed for
            // multiple database support.
            int type = getDBType(fieldName);

            log.debug("getStringValue(): fieldName " + fieldName + " has type " + type);
            // check if for known mapped types
            if (type == FieldDefs.TYPE_STRING) {

                // determine actual node number for this field
                // takes into account when in a multilevel node
                int number = getIntValue(getBuilderName(fieldName) + ".number");
                tmp = parent.getShortedText(fieldName, number);

                // did we get a result then store it in the values for next use
                if (tmp != null) {
                    // store the unmapped value (replacing the $SHORTED text)
                    storeValue(fieldName, tmp);
                }
            }
        }
        // return the found value
        return tmp;
    }


    /**
     * Get a binary value of a certain field.
     * @param fieldName the name of the field who's data to return
     * @return the field's value as an <code>byte []</code> (binary/blob field)
     */
    public byte[] getByteValue(String fieldName) {
        // try to get the value from the values table
        Object obj = getValue(fieldName);

        // we signal with a empty byte[] that its not obtained yet.
        if (obj instanceof byte[]) {
            // was allready unmapped so return the value
            return (byte[])obj;
        } else {
            // determine actual node number for this field
            // takes into account when in a multilevel node
            int number = getIntValue(getBuilderName(fieldName) + ".number");
            // call our builder with the convert request this will probably
            // map it to the database we are running.
            byte[] b = parent.getShortedByte(fieldName,number);

            // we could in the future also leave it unmapped in the values
            // or make this programmable per builder ?
            storeValue(fieldName, b);
            // return the unmapped value
            return b;
        }
    }

    /**
     * Tests whether one of the values of this node was changed since the last commit/insert.
     * @return <code>true</code> if changes have been made, <code>false</code> otherwise
     */
    public boolean isChanged() {
        for (Enumeration r = nodes.elements(); r.hasMoreElements(); ) {
            if (((MMObjectNode)r.nextElement()).isChanged()) return true;
        }
        return false;
    }

    /**
     * Return the relations of this node.
     * This is not allowed on a cluster node
     * @throws <code>RuntimeException</code>
     */
    public Enumeration getRelations() {
        throw new RuntimeException("Cannot follow relations on a cluster node. ");
    }

}

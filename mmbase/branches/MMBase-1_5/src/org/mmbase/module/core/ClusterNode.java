/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;
import java.sql.*;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;


/**
 * ClusterNode is a representation of a 'cluster' of objectnodes.
 * It represents a set of related nodes, retrieved using a multilevel query.
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
 * @version 26 Apr 2001
 */
public class ClusterNode extends VirtualNode {
    /**
     * Logger routine
     */
    private static Logger log = Logging.getLoggerInstance(ClusterNode.class.getName());

    /**
     * Holds the name - value pairs of related nodes in this virtual node.
     */
    protected Hashtable nodes=null;

    /**
     * Determines whether the node is being loaded.
     */
    public boolean initializing = true;

    /**
     * Main contructor.
     * @param parent the node's parent, generally an instance of the ClusterBuilder builder.
     */
    public ClusterNode(MMObjectBuilder parent) {
        super(parent);
        nodes=new Hashtable();
    }
    /**
     * Main contructor.
     * @param parent the node's parent, generally an instance of the ClusterBuilder builder.
     * @param rnofnodes Nr of referenced nodes.
     */
    public ClusterNode(MMObjectBuilder parent, int nrofnodes) {
        super(parent);
        nodes=new Hashtable(nrofnodes);
    }

    /**
     * Tests whether the data in a node is valid (throws an exception if this is not the case).
     * The call is performed on all loaded 'real' nodes. If a 'real' node has not previously been
     * forcably loaded, it is assumed to be correct.
     * @throws org.mmbase.module.core.InvalidDataException
     *      If the data was unrecoverably invalid
     *      (the references did not point to existing objects)
     */
    public void testValidData() throws InvalidDataException {
      for (Enumeration r=nodes.elements(); r.hasMoreElements(); ) {
        ((MMObjectNode)r.nextElement()).testValidData();
      }
    };

     /**
      * commit : commits the node to the database or other storage system
      * this can only be done on a existing (inserted) node. it will use the
      * changed Vector as its base of what to commit/changed
      * @return <code>true</code> if the commit was succesfull, <code>false</code> is it failed
      */
    public boolean commit() {
      boolean res=true;
      for (Enumeration r=nodes.elements(); r.hasMoreElements(); ) {
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
    public MMObjectNode getRealNode(String buildername) {
      MMObjectNode node = (MMObjectNode)nodes.get(buildername);
      if (node!=null) return node;
      Integer number = (Integer)retrieveValue(buildername+".number");
      if (number!=null) {
          node=parent.getNode(number.intValue());
          if (node!=null) nodes.put(buildername,node);
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
    protected void storeValue(String fieldname,Object fieldvalue) {
        MMObjectNode node = (MMObjectNode)nodes.get(getBuilderName(fieldname));
        if (node!=null) {
            node.values.put(((ClusterBuilder)parent).getFieldNameFromField(fieldname),
                            fieldvalue);
        } else {
            values.put(fieldname,fieldvalue);
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
    public boolean setValue(String fieldname,Object fieldvalue) {
        // Circument interference by the database during initial loading of the node
        // This is not pretty, but the alternative is rewriting all support classes...
        if (initializing) {
            values.put(fieldname,fieldvalue);
            return true;
        }
        String buildername=getBuilderName(fieldname);
        MMObjectNode n=getRealNode(buildername);
        if (n!=null) {
            String realfieldname=((ClusterBuilder)parent).getFieldNameFromField(fieldname);
            n.setValue(realfieldname,fieldvalue);
            values.remove(fieldname);
            return true;
        }
        return false; // or throw exception?
    }

    /**
     * Determines the builder name of a specified fieldname, i.e.
     * "news" in "news.title",
     * @param fieldname the name of the field
     * @return the buidler name of the field
     */
    protected String getBuilderName(String fieldname) {
        int pos=fieldname.indexOf(".");
        if (pos==-1) {
            return null;
        } else {
            String bulname=fieldname.substring(0,pos);
            int pos2=bulname.lastIndexOf("(");
            bulname=bulname.substring(pos2+1);
            // XXX: we should check on commas and semicolons too... ?
            return bulname;
        }
    }

    /**
     * Get a value of a certain field.
     * @param fieldname the name of the field who's data to return
     * @return the field's value as an <code>Object</code>
     */
    public Object getValue(String fieldname) {
        String builder=getBuilderName(fieldname);
        if (builder==null) {
            // there is no 'builder' specified,
            // so the fieldname itself is a builder name
            // -> so return the MMObjectNode for that buidler
            return getRealNode(fieldname);
        }
        Object o = super.getValue(fieldname);
        if (o == null) {
            // the normal approach does not yield results.
            // get the value from the original builder
            String buildername=getBuilderName(fieldname);
            MMObjectNode n=getRealNode(buildername);
            if (n!=null) {
                o = n.getValue(((ClusterBuilder)parent).getFieldNameFromField(fieldname));
            } else { 
                // fall back to builder if this node doesn't contain a number to fetch te original
                MMObjectBuilder bul = parent.mmb.getMMObject(buildername);
                if (bul != null) {                    
                    o = bul.getValue(this,fieldname);
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
    public String getStringValue(String fieldname) {

        // try to get the value from the values table
        String tmp = "";
        Object o=getValue(fieldname);
        if (o!=null) {
            tmp=""+o;
        }
        // check if the object is shorted
        if (tmp.indexOf("$SHORTED")==0) {
            log.debug("getStringValue(): node="+this+" -- fieldname "+fieldname);
            // obtain the database type so we can check if what
            // kind of object it is. this have be changed for
            // multiple database support.
            int type=getDBType(fieldname);

            log.debug("getStringValue(): fieldname "+fieldname+" has type "+type);
            // check if for known mapped types
            if (type==FieldDefs.TYPE_STRING) {

                // determine actual node number for this field
                // takes into account when in a multilevel node
                int number=getIntValue(getBuilderName(fieldname)+".number");
                tmp=parent.getShortedText(fieldname,number);

                // did we get a result then store it in the values for next use
                if (tmp!=null) {
                    // store the unmapped value (replacing the $SHORTED text)
                    storeValue(fieldname,tmp);
                }
            }
        }
        // return the found value
        return tmp;
    }

    /**
     * Get a binary value of a certain field.
     * @param fieldname the name of the field who's data to return
     * @return the field's value as an <code>byte []</code> (binary/blob field)
     */
    public byte[] getByteValue(String fieldname) {
        // try to get the value from the values table
        Object obj=getValue(fieldname);

        // we signal with a empty byte[] that its not obtained yet.
        if (obj instanceof byte[]) {
            // was allready unmapped so return the value
            return (byte[])obj;
        } else {
            // determine actual node number for this field
            // takes into account when in a multilevel node
            int number=getIntValue(getBuilderName(fieldname)+".number");
            // call our builder with the convert request this will probably
            // map it to the database we are running.
            byte[] b=parent.getShortedByte(fieldname,number);

            // we could in the future also leave it unmapped in the values
            // or make this programmable per builder ?
            storeValue(fieldname,b);
            // return the unmapped value
            return b;
        }
    }

    /**
     * Tests whether one of the values of this node was changed since the last commit/insert.
     * @return <code>true</code> if changes have been made, <code>false</code> otherwise
     */
    public boolean isChanged() {
      boolean res=false;
      for (Enumeration r=nodes.elements(); r.hasMoreElements(); ) {
        res=res || ((MMObjectNode)r.nextElement()).isChanged();
      }
      return res;
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

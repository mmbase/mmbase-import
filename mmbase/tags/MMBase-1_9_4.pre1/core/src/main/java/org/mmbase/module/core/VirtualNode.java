/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

import java.util.*;

/**
 * VirtualNode is a representation of a virtual objectnode.
 * Virtual Object nodes are nodes that are not stored in a databasetable.
 * Note that a temporary node is not virtual.
 * This class captures a number of methods that would normally require datbase
 * access, such as obtaining relations or determining age of a node.
 *
 * @author Pierre van Rooden
 * @version $Id$
 */
public class VirtualNode extends MMObjectNode {

    /**
     * Main constructor.
     * @param parent the node's parent
     */
    public VirtualNode(MMObjectBuilder parent) {
        super(parent, false);
    }

    /**
     * Alternate constructor, to create a node with the values given.
     */
    public VirtualNode(Map<String, Object> values) {
        super(new VirtualBuilder(MMBase.getMMBase()), values);
    }

    @Override
    public boolean isVirtual() {
        return true;
    }

    /**
     * Overrides to no throw exception on non-existing fields
     */
    @Override
    protected boolean checkFieldExistance(String fieldName) {
        return true;
    }

     /**
      * commit : commits the node to the database or other storage system.
      * Generally, commiting a virtual node has no effect, so the basic
      * implementation returns false.
      * @return <code>false</code>
      */
    @Override
    public boolean commit() {
      return false;
    }

    /**
     *  Insert is not implemented on a virtual node.
     *  @return nothing, throws an exception
     *  @throws UnsupportedOperationException
     */
    @Override
    public int insert(String userName) {
        throw new UnsupportedOperationException("Method insert is not implemented on a virtual node.");
    }

    /**
     * {@inheritDoc}
     * A virtual node never has relations.
     * @return <code>false</code>
     */
    @Override
    public boolean hasRelations() {
        return false;
    }

    /**
     * {@inheritDoc}
     * A virtual node never has relations.
     * @return empty <code>Enumeration</code>
     */
    @Override
    public Enumeration<MMObjectNode> getRelations() {
        return new java.util.Vector<MMObjectNode>(0).elements();
    }

    /**
     * {@inheritDoc}
     * A virtual node never has relations.
     * @return 0, because Virtual nodes have no relations.
     */
    @Override
    public int getRelationCount() {
        return 0;
    }

    /**
     * {@inheritDoc}
     * A virtual node never has relations.
     * @param wantedtype the 'type' of related nodes (NOT the relations!).
     * @return 0, because Virtual nodes have no relations.
     */
    @Override
    public int getRelationCount(String wantedtype) {
        return 0;
    }

    /**
     * Returns the node's age
     * A virtual node is always new (0)
     * @return the age in days (0)
     */
    @Override
    public int getAge() {
        return 0;
    }

    @Override
    public int getOType() {
        return -1;
    }

}

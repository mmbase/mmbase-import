/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module.core;

/**
 * Event/changes interface for MMObjectNodes this is a callback  
 * interface thats need to be implemented when a object wants to add 
 * itself as a change listener on Builder to recieve signals if nodes change.
 *
 * @author Daniel Ockeloen
 * @version $Id$
 * @deprecated Use {@link org.mmbase.core.event.NodeEventListener}
 */
public interface MMBaseObserver {
    /**
     * Called when a remote node is changed.
     *
     * @param machine Name of the machine that changed the node.
     * @param number  Number of the changed node as a <code>String</code>
     * @param builder Type of the changed node
     * @param ctype   command type, 'c'=changed, 'd'=deleted', 'r'=relations changed, 'n'=new
     * @return always <code>true</code>
     * @todo javadoc is copied from MMObjectBuilder, but MMObjectBuilder is not an MMBaseObserver
     */
    public boolean nodeRemoteChanged(String machine, String number, String builder, String ctype);

    /**
     * Called when a local node is changed.
     *
     * @param machine Name of the machine that changed the node.
     * @param number  Number of the changed node as a <code>String</code>
     * @param builder Type of the changed node
     * @param ctype   command type, 'c'=changed, 'd'=deleted', 'r'=relations changed, 'n'=new
     * @return always <code>true</code>
     * @todo javadoc is copied from MMObjectBuilder, but MMObjectBuilder is not an MMBaseObserver
     */

    public boolean nodeLocalChanged(String machine, String number, String builder, String ctype);
}

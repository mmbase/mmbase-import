/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.fields;


import org.mmbase.bridge.*;

/**
 * If the processor defined for the field is of this type, then the 'commit' method will be called
 * on commit of the Node.
 *
 * @author Michiel Meeuwissen
 * @version $Id: CommitProcessor.java,v 1.1 2004-09-17 09:29:25 michiel Exp $
 * @since MMBase-1.8
 */

public interface CommitProcessor {
    
    /**
     * Will be called on commit of the node.
     */
    void   commit(Node node, Field field);
}

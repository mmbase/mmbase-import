/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.fields;


import org.mmbase.bridge.*;

/**
 * If the processor defined for the field is of this type, then the {@link #commit} method will be called
 * on commit of the Node.
 * You should also implement the {@link #process} method of a CommitProcessor as the action to peform
 * when the value is processed for setting data on the field.
 *
 * @author Michiel Meeuwissen
 * @version $Id: CommitProcessor.java,v 1.2 2005-07-26 14:36:31 pierre Exp $
 * @since MMBase-1.8
 */

public interface CommitProcessor extends Processor {

    /**
     * Will be called on commit of the node.
     */
    void commit(Node node, Field field);
}

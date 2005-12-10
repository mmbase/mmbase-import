/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;

/**
 * This processor can be used as a 'set' processor on a (datetime) field. The field will then be set
 * to the current user id when the node is committed.
 *
 * @author Michiel Meeuwissen
 * @version $Id: LastModifier.java,v 1.3 2005-12-10 14:33:36 michiel Exp $
 * @since MMBase-1.8
 * @see   LastModifier
 */


public class LastModifier implements CommitProcessor {

    private static final long serialVersionUID = 1L;

    public void commit(Node node, Field field) {
        node.setValueWithoutProcess(field.getName(),node.getCloud().getUser().getIdentifier());
    }

    public String toString() {
        return "lastmodifier";
    }
}

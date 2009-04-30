/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;

/**
 * If you use this processor for a field, then commit will fail if the node is not 'changed'. Used
 * to assert it is changed  in test-cases.
 *
 * @author Michiel Meeuwissen
 * @version $Id: AssertChangedCommitProcessor.java,v 1.2 2009-04-30 14:17:54 michiel Exp $
 * @since MMBase-1.9.1
 */

public class AssertChangedCommitProcessor implements CommitProcessor {

    public final void commit(Node node, Field field) {
        if (! (node.isChanged() || node.isNew())) {
            throw new RuntimeException("Failed " + field.getName() + ". Node is unexpectedly not changed. ");
        }
    }
}



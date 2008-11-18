/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;

/**
 * If you use this processor for a field, then setting it to an empty value will be ignored (the
 * previous value will remain intact).
 *
 * @author Michiel Meeuwissen
 * @version $Id: AssertChangedCommitProcessor.java,v 1.1 2008-11-18 23:30:07 michiel Exp $
 * @since MMBase-1.9.1
 */

public class AssertChangedCommitProcessor implements CommitProcessor {

    public final void commit(Node node, Field field) {
        if (! (node.isChanged() || node.isNew())) {
            throw new RuntimeException("Failed " + field.getName() + ". Node is unexpectedly not changed. ");
        }
    }
}



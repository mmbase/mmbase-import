/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;

/**
 * The CommitProcessor that does nothing.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8
 */

public final class EmptyCommitProcessor  implements CommitProcessor {

    private static final long serialVersionUID = 1L;

    private static EmptyCommitProcessor instance = new EmptyCommitProcessor();

    public static EmptyCommitProcessor getInstance() {
        return instance;
    }

    public final void commit(Node node, Field field) {
        return;
    }

    public String toString() {
        return "EMPTY";
    }
}

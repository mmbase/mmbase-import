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
 * @version $Id: EmptyCommitProcessor.java,v 1.1 2005-11-04 23:11:52 michiel Exp $
 * @since MMBase-1.8
 */

public class EmptyCommitProcessor  implements CommitProcessor {

    private static final int serialVersionUID = 1;

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

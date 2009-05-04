/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.*;

/**
 * Chains a bunch of other processors into one new processor.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.7
 */

public class ChainedCommitProcessor implements  CommitProcessor {
    private static final Logger log = Logging.getLoggerInstance(ChainedCommitProcessor.class);

    private static final long serialVersionUID = 1L;

    private List processors = new ArrayList();

    public ChainedCommitProcessor add(CommitProcessor proc) {
        processors.add(proc);
        return this;
    }

    public void commit(Node node, Field field) {
        Iterator i = processors.iterator();
        while (i.hasNext()) {
            Object proc = i.next();
            if (proc instanceof CommitProcessor) {
                CommitProcessor commitProc = (CommitProcessor) proc;
                commitProc.commit(node, field);
            }
        }
        return;
    }

    public String toString() {
        return "chained commit" + processors;
    }



}

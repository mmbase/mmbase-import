/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import java.util.*;

/**
 * Chains a bunch of other processors into one new processor.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.7
 */

public class ChainedCommitProcessor implements  CommitProcessor, org.mmbase.util.PublicCloneable {

    private static final long serialVersionUID = 1L;

    private ArrayList<CommitProcessor> processors = new ArrayList<CommitProcessor>();

    public ChainedCommitProcessor add(CommitProcessor proc) {
        if (proc instanceof ChainedCommitProcessor) {
            processors.addAll(((ChainedCommitProcessor) proc).getProcessors());
        } else {
            processors.add(proc);
        }
        return this;
    }

    public void commit(Node node, Field field) {
        for (CommitProcessor proc : processors) {
            proc.commit(node, field);
        }
        return;
    }

    /**
     * @since 1.9.2
     */
    public List<CommitProcessor> getProcessors() {
        return Collections.unmodifiableList(processors);
    }

    @Override
    public String toString() {
        return "chained" + processors;
    }

    @Override
    public Object clone() {
        try {
            ChainedCommitProcessor clone = (ChainedCommitProcessor)super.clone();
            clone.processors = (ArrayList<CommitProcessor>) processors.clone();
            return clone;
        } catch (CloneNotSupportedException cnse) {
            //
            return null;
        }
    }

}

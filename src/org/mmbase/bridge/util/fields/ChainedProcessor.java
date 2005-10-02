/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.util.fields;

import org.mmbase.bridge.*;
import java.util.*;

/**
 * Chains a bunch of other processors into one new processor.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ChainedProcessor.java,v 1.5 2005-10-02 16:25:19 michiel Exp $
 * @since MMBase-1.7
 */

public class ChainedProcessor implements Processor, CommitProcessor {

    private List processors = new ArrayList();

    public ChainedProcessor add(Object proc) {
        processors.add(proc);
        return this;
    }

    public Object process(Node node, Field field, Object value) {

        Iterator i = processors.iterator();
        while (i.hasNext()) {
            Processor proc = (Processor) i.next();
            value = proc.process(node, field, value);
        }
        return value;
    }

    public void commit(Node node, Field field) {
        Iterator i = processors.iterator();
        while (i.hasNext()) {
            Object proc = i.next();
            if (i instanceof CommitProcessor) {
                CommitProcessor commitProc = (CommitProcessor) proc;
                commitProc.commit(node, field);
            }
        }
        return;
    }



}

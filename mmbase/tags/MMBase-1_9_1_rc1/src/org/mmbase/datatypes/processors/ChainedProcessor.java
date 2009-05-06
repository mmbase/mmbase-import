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

public class ChainedProcessor implements Processor {

    private static final long serialVersionUID = 1L;

    private List<Processor> processors = new ArrayList<Processor>();

    public ChainedProcessor add(Processor proc) {
        processors.add(proc);
        return this;
    }

    public Object process(Node node, Field field, Object value) {
        for (Processor proc: processors) {
            value = proc.process(node, field, value);
        }
        return value;
    }

    public String toString() {
        return "chained" + processors;
    }


}

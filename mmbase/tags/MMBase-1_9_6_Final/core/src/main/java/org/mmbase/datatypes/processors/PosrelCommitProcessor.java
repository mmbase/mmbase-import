/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;

/**
 * Used as 'commitprocessor' on the 'pos' field of a posrel object, this guesses a nice default if
 * you left the value empty on commit.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8.6
 */

public class PosrelCommitProcessor implements CommitProcessor {

    private static final Logger log = Logging.getLoggerInstance(PosrelCommitProcessor.class);

    private static final long serialVersionUID = 1L;


    public void commit(Node node, Field field) {
        if (log.isDebugEnabled()) {
            log.debug("Committing" + node);
        }
        if (node.getValue(field.getName()) == null || "".equals(node.getStringValue(field.getName()))) {
            Node source      = node.getNodeValue("snumber");
            if (source != null) {
                NodeQuery q = Queries.createNodeQuery(source);
                String role = node.getNodeValue("rnumber").getStringValue("sname");
                Step relationStep = q.addRelationStep(node.getCloud().getNodeManager("object"),
                                                      role,
                                                      "destination");
                q.setNodeStep(relationStep);
                Integer max = (Integer) Queries.max(q, q.getStepField(q.getNodeManager().getField(field.getName())));
                log.debug("max now " + max);
                if (max == null) max = 0;
                node.setValueWithoutProcess(field.getName(), max + 1);
            }
        }
    }

}



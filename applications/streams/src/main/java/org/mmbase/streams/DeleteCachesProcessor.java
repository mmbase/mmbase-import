/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.datatypes.processors.*;
import org.mmbase.util.logging.*;


/**
 * This commit-processor is used on node of the type 'streamsources', and is used to delete
 * associated streamsourcescaches
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class DeleteCachesProcessor implements CommitProcessor {
    private static final long serialVersionUID = 0L;

    private static final Logger LOG = Logging.getLoggerInstance(DeleteCachesProcessor.class);



    public void commit(final Node node, final Field field) {
        if (node.getNumber() > 0) {
            final NodeManager caches = node.getCloud().getNodeManager("streamsourcescaches");
            NodeQuery q = caches.createQuery();
            Queries.addConstraint(q, Queries.createConstraint(q, "id", FieldCompareConstraint.EQUAL, node));
            for (Node cache : caches.getList(q)) {
                if (cache.mayDelete()) {
                    cache.delete(true);
                } else {
                    LOG.warn("May not delete " + cache);
                }
            }
        }
    }

}

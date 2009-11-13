/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.streams;

import org.mmbase.util.functions.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.security.ActionRepository;
import org.mmbase.datatypes.processors.*;
import org.mmbase.util.logging.*;

/**
 * Retriggers creation of the caches.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class CreateCachesFunction  extends NodeFunction<Boolean> {

    private static final Logger LOG = Logging.getLoggerInstance(CreateCachesFunction.class);
    public CreateCachesFunction() {
        super("createcaches");
    }


    CreateCachesProcessor getCacheCreator(final Field url) {
        CommitProcessor commitProcessor = url.getDataType().getCommitProcessor();
        if (commitProcessor instanceof ChainedCommitProcessor) {
            ChainedCommitProcessor chain = (ChainedCommitProcessor) commitProcessor;
            LOG.info("Lookin in " + chain.getProcessors());
            for (CommitProcessor cp : chain.getProcessors()) {
                if (cp instanceof CreateCachesProcessor) {
                    return (CreateCachesProcessor) cp;
                }
            }
            return null;
        } else {
            if (commitProcessor instanceof CreateCachesProcessor) {
                return (CreateCachesProcessor) commitProcessor;
            } else {
                return null;
            }
        }
    }

    @Override
    protected Boolean getFunctionValue(final Node node, final Parameters parameters) {
        if (node.getNumber() > 0 && node.getCloud().may(ActionRepository.getInstance().get("streams", "retrigger_jobs"), null)) {
            LOG.info("Recreating caches for " + node.getNumber());
            final Field url = node.getNodeManager().getField("url");

            {
                NodeList list = SearchUtil.findNodeList(node.getCloud(), node.getNodeManager().getProperty("org.mmbase.streams.cachestype"), "id", node.getNumber());
                for (Node cache : list) {
                    cache.delete(true);
                    LOG.service("Deleted " + cache.getNumber());
                }
            }

            {
                final CreateCachesProcessor cc = getCacheCreator(url);
                if (cc != null) {
                    LOG.info("Calling " + cc);
                    cc.createCaches(node.getCloud().getNonTransactionalCloud(), node.getNumber());
                    return true;
                } else {
                    LOG.error("No CreateCachesProcessor in " + url);
                    return false;
                }
            }
        } else {
            return false;
        }
    }

}

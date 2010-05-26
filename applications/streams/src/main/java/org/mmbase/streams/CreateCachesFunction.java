/*

This file is part of the MMBase Streams application, 
which is part of MMBase - an open source content management system.
    Copyright (C) 2009 André van Toly, Michiel Meeuwissen

MMBase Streams is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

MMBase Streams is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with MMBase. If not, see <http://www.gnu.org/licenses/>.

*/

package org.mmbase.streams;

import org.mmbase.streams.createcaches.Processor;

import org.mmbase.util.functions.*;
import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.security.ActionRepository;
import org.mmbase.datatypes.processors.*;
import org.mmbase.util.logging.*;

/**
 * Retriggers creation of (all) caches of a source node.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class CreateCachesFunction  extends NodeFunction<Boolean> {

    private static final Logger LOG = Logging.getLoggerInstance(CreateCachesFunction.class);
    public CreateCachesFunction() {
        super("createcaches");
    }

    /**
     * CommitProcessor is on url field of source node.
     * @param url   field url of source node
     * @return Processor to (re)create caches nodes
     */
    protected static Processor getCacheCreator(final Field url) {
        CommitProcessor commitProcessor = url.getDataType().getCommitProcessor();
        if (commitProcessor instanceof ChainedCommitProcessor) {
            ChainedCommitProcessor chain = (ChainedCommitProcessor) commitProcessor;
            LOG.service("Lookin in " + chain.getProcessors());
            for (CommitProcessor cp : chain.getProcessors()) {
                if (cp instanceof Processor) {
                    return (Processor) cp;
                }
            }
            return null;
        } else {
            if (commitProcessor instanceof Processor) {
                return (Processor) commitProcessor;
            } else {
                return null;
            }
        }
    }

    @Override
    protected Boolean getFunctionValue(final Node node, final Parameters parameters) {
        if (node.getNumber() > 0 
                && node.getCloud().may(ActionRepository.getInstance().get("streams", "retrigger_jobs"), null)) {
            LOG.info("Recreating caches for #" + node.getNumber());
            final Field url = node.getNodeManager().getField("url");

            {
                Node mediafragment = node.getNodeValue("mediafragment");
                String cachestype = node.getNodeManager().getProperty("org.mmbase.streams.cachestype");
                NodeList list = SearchUtil.findRelatedNodeList(mediafragment, cachestype, "related"); 
                
                // when the streamsourcescaches are initially of the wrong type they don't get deleted, this helps a bit
                if (list.size() < 1) {
                    if (cachestype.startsWith("video")) {
                        list = SearchUtil.findRelatedNodeList(mediafragment, "audiostreamsourcescaches", "related");
                    } else if (cachestype.startsWith("audio")) {
                        list = SearchUtil.findRelatedNodeList(mediafragment, "videostreamsourcescaches", "related");
                    }
                }
                
                for (Node cache : list) {
                    cache.delete(true);
                    LOG.service("deleted streamsourcescaches #" + cache.getNumber());
                }
            }

            {
                final Processor cc = getCacheCreator(url);
                if (cc != null) {
                    LOG.service("Calling " + cc);
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

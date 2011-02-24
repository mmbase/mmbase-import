/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.media;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.datatypes.processors.CommitProcessor;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * This commit-processor is used on nodes of the type 'mediafragments' and
 * deletes associated 'mediasources' when a 'mediafragments' node is deleted.
 * To a 'mediasources' belonging nodes of type 'streamsources' in Streams will be 
 * deleted by org.mmbase.streams.DeleteCachesProcessor.
 *
 * @author André van Toly
 * @version $Id$
 */

public class DeleteSourcesProcessor implements CommitProcessor {
    private static final long serialVersionUID = 0L;

    public static String NOT = DeleteSourcesProcessor.class.getName() + ".DONOT";

    private static final Logger LOG = Logging.getLoggerInstance(DeleteSourcesProcessor.class);
    
    
    @Override
    public void commit(final Node node, final Field field) {
        if (node.getCloud().getProperty(NOT) != null) {
            LOG.service("Not doing because of property");
            return;
        }
        if (node.getNumber() > 0) {
            String nodemanager = node.getNodeManager().getProperty("org.mmbase.media.containedtype");
            
            if (nodemanager != null && !"".equals(nodemanager)) {
                
                NodeList sources = SearchUtil.findRelatedNodeList(node, nodemanager, "related");
                LOG.info("Deleting " + sources.size() + " sources.");
                for (Node src : sources) {
                    if (src.mayDelete()) {
                        src.delete(true);
                    } else {
                        LOG.warn("May not delete " + src);
                    }
                }
            }
        }
    }

}

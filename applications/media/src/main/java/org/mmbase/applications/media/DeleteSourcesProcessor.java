/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.media;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.SearchUtil;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.util.Queries;
import org.mmbase.datatypes.processors.*;
import org.mmbase.util.logging.*;


/**
 * This commit-processor is used on node of the type 'mediafragments', and is used to delete
 * associated 'streamsources' and 'images' when a 'mediafragments' is deleted.
 * Nodes of type 'streamssources' will be deleted by DeleteCachesProcessor.
 *
 * @author André van Toly
 * @version $Id$
 */

public class DeleteSourcesProcessor implements CommitProcessor {
    private static final long serialVersionUID = 0L;

    public static String NOT = DeleteSourcesProcessor.class.getName() + ".DONOT";

    private static final Logger LOG = Logging.getLoggerInstance(DeleteSourcesProcessor.class);
    
    
    public void commit(final Node node, final Field field) {
        if (node.getCloud().getProperty(NOT) != null) {
            LOG.service("Not doing because of property");
            return;
        }
        if (node.getNumber() > 0) {
            NodeList sources = SearchUtil.findRelatedNodeList(node, node.getNodeManager().getProperty("org.mmbase.media.cointaintype"), "related");
            LOG.info("Deleting " + sources.size() + " sources");
            for (Node src : sources) {
                if (src.mayDelete()) {
                    src.delete(true);
                } else {
                    LOG.warn("May not delete " + src);
                }
            }
            
            NodeList images = SearchUtil.findRelatedNodeList(node, "images", "related");
            LOG.info("Deleting " + images.size() + " sources");
            for (Node img : images) {
                if (img.mayDelete()) {
                    img.delete(true);
                } else {
                    LOG.warn("May not delete " + img);
                }
            }
            
        }
    }

}

 /*

 This software is OSI Certified Open Source Software.
 OSI Certified is a certification mark of the Open Source Initiative.

 The license (Mozilla version 1.0) can be read at the MMBase site.
 See http://www.MMBase.org/license
 */

package org.mmbase.applications.media;

import org.mmbase.bridge.*;
import org.mmbase.datatypes.processors.CommitProcessor;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: Codec.java 35335 2009-05-21 08:14:41Z michiel $
 */
public class FragmentTypeFixer implements CommitProcessor {
    private static Logger log = Logging.getLoggerInstance(FragmentTypeFixer.class);

    private static final long serialVersionUID = 1L;

    public void commit(Node node, Field field) {
        if (! node.isNew()) {
            NodeList fragments = node.getRelatedNodes(node.getCloud().getNodeManager("mediafragments"), "related", "source");
            NodeManager targetType = node.getCloud().getNodeManager(node.getNodeManager().getProperty("org.mmbase.media.containertype"));
            for (Node fragment : fragments) {
                fragment.setNodeManager(targetType);
            }
        }

    }


}


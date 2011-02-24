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
 * Makes sure a mediasources node has its equivalent mediafragments,
 * a videosources needs a videofragments etc.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class FragmentTypeFixer implements CommitProcessor {
    private static final Logger log = Logging.getLoggerInstance(FragmentTypeFixer.class);
    public final static String NOT = FragmentTypeFixer.class + ".NOT";
    private static final long serialVersionUID = 1L;

    @Override
    public void commit(Node node, Field field) {
        if (node.getCloud().getProperty(NOT) != null) {
            log.info("Skipping because of " + NOT);
            return;
        }
        assert node.getValue("number") != null;
        Node fragment = node.getNodeValue("mediafragment");
        assert node.getValue("number") != null;
        if (fragment == null) {
            log.info("No fragment yet " + node);
            //System.out.println("No fragment yet " + node);
            return;
        }
        if (fragment.getNumber() > 0) {
            assert fragment.getNumber() > 0;
            assert fragment.getValue("otype") != null;
            String targetType = node.getNodeManager().getProperty("org.mmbase.media.containertype");
            if (targetType == null) {
                throw new IllegalStateException("No such node manager " + node.getNodeManager().getProperty("org.mmbase.media.containertype") + " (container type of " + node.getNodeManager().getName() + ", node " + node.getNumber() + ")");
            }
            if (! fragment.getNodeManager().getName().equals(targetType)) {
                fragment.setNodeManager(fragment.getCloud().getNodeManager(targetType));
                assert fragment.getIntValue("otype") > 0;
                fragment.commit();
            } else {
                log.debug("Fragment of " + node.getNumber() + " has correct fragment already");
            }
        } else {
            log.warn("Fragment negative number: " + fragment);
        }
    }


}


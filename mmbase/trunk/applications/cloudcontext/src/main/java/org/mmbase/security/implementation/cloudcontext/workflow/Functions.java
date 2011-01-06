/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.security.implementation.cloudcontext.workflow;

import org.mmbase.util.functions.*;
import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since  MMBase-1.9.6
 */
public  class Functions  {

    private static final Logger LOG = Logging.getLoggerInstance(Functions.class);


    public static void markForDelete(@Required @Name("node")
                                     Node node) throws SecurityException {
        if (!node.mayDelete()) throw new org.mmbase.security.SecurityException("You may not delete " + node.getNumber());

        // use admin cloud to change the context itself, because the user may perhaps not change contexts
        Cloud adminCloud = node.getCloud().getCloudContext().getCloud("mmbase", "class", null);
        Node adminNode = adminCloud.getNode(node.getNumber());
        adminNode.setContext(WorkFlowContextProvider.DELETED + adminNode.getContext());
        adminNode.commit();
    }



}

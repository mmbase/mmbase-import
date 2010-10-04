/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.datatypes.processors;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.util.logging.*;


/**
 * Fills field with a owner value per default.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9.4
 */

public class DefaultOwner  implements Processor {
    private static final Logger LOG = Logging.getLoggerInstance(DefaultOwner.class);

    public Object process(Node node, Field field, Object value) {
        LOG.debug("Getting default value for " + field);
        final Cloud cloud;
        if (node == null) {
            cloud = CloudThreadLocal.currentCloud();
        } else {
            cloud = node.getCloud();
        }
        if (cloud == null) {
            LOG.debug("No cloud  using " + value);
            return value;
        } else {
            return cloud.getUser().getOwnerField();
        }
    }

}



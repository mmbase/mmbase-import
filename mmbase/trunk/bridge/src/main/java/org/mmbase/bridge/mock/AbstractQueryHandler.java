/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.mock;

import java.util.*;
import org.mmbase.bridge.*;

/**

 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.2
 * @todo    EXPERIMENTAL
 */

abstract class AbstractQueryHandler implements QueryHandler  {

    protected final MockCloud cloud;
    AbstractQueryHandler(MockCloud cloud) {
        this.cloud = cloud;
    }

    protected Set<String> getNodeManagerAndDescendants(NodeManager nm) {
        Set<String> wantedNodeManagers = new HashSet<String>();
        wantedNodeManagers.add(nm.getName());
        for (NodeManager descendant : nm.getDescendants()) {
            wantedNodeManagers.add(descendant.getName());
        }
        return wantedNodeManagers;
    }


}

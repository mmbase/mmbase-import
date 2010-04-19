/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.mock;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.util.*;

/**

 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.2
 * @todo    EXPERIMENTAL
 */

class NodeQueryHandler extends AbstractQueryHandler  {

    NodeQueryHandler(MockCloud cloud) {
        super(cloud);
    }


    @Override
    public List<Map<String, Object>> getRecords(Query query) {
        assert query instanceof NodeQuery;
        NodeQuery nq = (NodeQuery) query;
        if (query.getSteps().size() == 1) {
            if (query.getConstraint() != null) {
                throw new UnsupportedOperationException();
            } else {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                Set<String> wantedNodeManagers = getNodeManagerAndDescendants(nq.getNodeManager());
                for (MockCloudContext.NodeDescription nd : cloud.cloudContext.nodes.values()) {
                    //System.out.println("Checking " + nd);
                    if (wantedNodeManagers.contains(nd.type)) {
                        list.add(nd.values);
                    }
                }
                return list;
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }


}

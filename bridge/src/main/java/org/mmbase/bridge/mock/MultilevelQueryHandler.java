/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.mock;

import java.util.*;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;

/**

 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.2
 * @todo    EXPERIMENTAL
 */

class MultilevelQueryHandler extends AbstractQueryHandler  {

    MultilevelQueryHandler(MockCloud cloud) {
        super(cloud);
    }

    @Override
    public List<Map<String, Object>> getRecords(Query query) {
        if (query.getSteps().size() == 1) {
            if (query.getConstraint() != null) {
                throw new UnsupportedOperationException("No support for queries with constraints yet");
            } else {
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

                NodeManager nm = query.getCloud().getNodeManager(query.getSteps().get(0).getTableName());
                Set<String> wantedNodeManagers = getNodeManagerAndDescendants(nm);
                for (MockCloudContext.NodeDescription nd : cloud.cloudContext.nodes.values()) {
                    if (wantedNodeManagers.contains(nd.type)) {
                        Map<String, Object> result = new HashMap<String, Object>();
                        for (StepField sf : query.getFields()) {
                            String key = sf.getAlias();
                            if (key == null) {

                                String prefix = sf.getStep().getAlias();
                                if (prefix == null) {
                                    prefix = sf.getStep().getTableName();
                                }
                                key = prefix + "." + sf.getFieldName();
                            }
                            result.put(key, nd.values.get(sf.getFieldName()));
                        }
                        list.add(result);
                    }
                }
                return list;
            }
        } else {
            throw new UnsupportedOperationException("No support for queries with more than one step yet");
        }
    }


}

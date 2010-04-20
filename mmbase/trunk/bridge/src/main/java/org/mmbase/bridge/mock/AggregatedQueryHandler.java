/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.mock;

import java.util.*;
import org.mmbase.bridge.Query;
import org.mmbase.storage.search.*;

/**
 * Query-handler that deals with aggregated queries.  At the moment this is only partly implemented
 * (Only count queries with 1 step, and no constraint).
 *
 * @author  Michiel Meeuwissen
 * @version $Id$
 * @since   MMBase-1.9.2
 * @todo    EXPERIMENTAL
 */

class AggregatedQueryHandler extends  AbstractQueryHandler  {

    AggregatedQueryHandler(MockCloud cloud) {
        super(cloud);
    }


    @Override
    public List<Map<String, Object>> getRecords(Query query) {
        if (query.getSteps().size() == 1 && query.getFields().size() == 1) {
            StepField field = query.getFields().get(0);
            if (field instanceof AggregatedField) {
                AggregatedField af = (AggregatedField) field;
                if (af.getAggregationType() == AggregatedField.AGGREGATION_TYPE_COUNT ||
                    af.getAggregationType() == AggregatedField.AGGREGATION_TYPE_COUNT_DISTINCT // we count for one field so it is automaticly distinct
                    ) {
                    if (query.getConstraint() != null) {
                        throw new UnsupportedOperationException("Query '" + query.toSql() + "' has a constraint, that is not yet supported");
                    } else {
                        String tableName = query.getSteps().get(0).getTableName();
                        Set<String> wantedNodeManagers = getNodeManagerAndDescendants(cloud.getNodeManager(tableName));
                        int count = 0;
                        for (MockCloudContext.NodeDescription nd : cloud.cloudContext.nodes.values()) {
                            //System.out.println("Checking " + nd);
                            if (wantedNodeManagers.contains(nd.type)) {
                                count++;
                            }
                        }
                        Map<String, Object> result = new HashMap<String, Object>();
                        String key = field.getAlias();
                        if (key == null) key = field.getFieldName();
                        result.put(key, count);
                        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                        list.add(result);
                        //System.out.println("Count " + result);
                        return list;
                    }
                } else {
                    throw new UnsupportedOperationException("Aggregation " + af.getAggregationType() + " is not supported");
                }
            } else {
                throw new UnsupportedOperationException("Field should be AggregatedField, but is " + field.getClass()); // RuntimeException ?
            }
        } else {
            throw new UnsupportedOperationException("Query '" + query.toSql() + "' has more than one field, which is not yet supported");
        }
    }


}

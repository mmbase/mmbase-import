
package org.mmbase.mynews;


import java.util.*;
import org.mmbase.storage.search.*;
import org.mmbase.storage.search.implementation.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.InsRel;

import org.mmbase.util.logging.*;


public class MyNewsBuilder extends org.mmbase.util.functions.ExampleBuilder {
    private static final Logger log = Logging.getLoggerInstance(MyNewsBuilder.class);

    // adds the virtual field 'magazine'.
    // Simply the magazine this news is belonging too.
    
    public Object getValue(MMObjectNode node, String field) {
        if ("magazine".equals(field)) {
            BasicSearchQuery query = new BasicSearchQuery();
            BasicStep step = query.addStep(this);
            MMObjectBuilder mags = getMMBase().getBuilder("mags");
            BasicRelationStep relation = query.addRelationStep((InsRel) getMMBase().getBuilder("posrel"), mags);
            query.addField(relation.getNext(), mags.getField("number"));
            try {
                List<MMObjectNode> nodes = getMMBase().getClusterBuilder().getClusterNodes(query);
                if (nodes.size() > 0) {
                    return mags.getNode(nodes.get(0).getStringValue("mags.number")).getValues();
                } else {
                    return null;
                }
            } catch (SearchQueryException sqe) {
                log.warn(sqe);
                return null;
            }
        } else {
            return super.getValue(node, field);
        }

    }
}

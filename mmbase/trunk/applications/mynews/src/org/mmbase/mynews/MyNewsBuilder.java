
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
            log.info("Gettign magazine virtual field");
            BasicSearchQuery query = new BasicSearchQuery();
            BasicStep step = query.addStep(this);
            MMObjectBuilder mags = getMMBase().getBuilder("mags");
            BasicRelationStep relation = query.addRelationStep((InsRel) getMMBase().getBuilder("posrel"), mags);
            query.addField(relation.getNext(), mags.getField("number"));
            try {
                List<MMObjectNode> nodes = getMMBase().getClusterBuilder().getClusterNodes(query);
                if (nodes.size() > 0) {
                    String magNumber = nodes.get(0).getStringValue("mags.number");
                    log.info("Returning mag " + magNumber + mags.getNode(magNumber).getValues());
                    return mags.getNode(magNumber).getValues();
                } else {
                    log.service("No magazine node found for " + node + " query: " + org.mmbase.util.Casting.toString(query));
                    return null;
                }
            } catch (SearchQueryException sqe) {
                log.warn(sqe);
                return null;
            }
        } else if ("utitle".equals(field)) {
            return ("" + getValue(node, "title")).toUpperCase();
        } else {
            return super.getValue(node, field);
        }

    }
}

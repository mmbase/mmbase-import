/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

import java.util.Iterator;
import java.util.List;

import org.mmbase.bridge.Field;
import org.mmbase.cache.AggregatedResultCache;
import org.mmbase.module.core.*;
import org.mmbase.storage.StorageException;

/**
 * A <code>ResultBuilder</code> is a builder for
 * {@link ResultNode ResultNodes}, that represent the results of executing
 * an arbitrary search query.
 * <p>
 * This builder contains info on the fields of the resultnodes.
 *
 * @author  Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public class ResultBuilder extends VirtualBuilder {

    private final SearchQuery query;

    /**
     * Creator.
     * Creates new <code>ResultBuilder</code> instance, used to represent
     * the results of executing a search query.
     *
     * @param mmbase MMBase instance.
     * @param query The search query that defines the search.
     */
    public ResultBuilder(MMBase mmbase, SearchQuery query) {
        super(mmbase);
        this.query = query;

        // Create fieldsByAlias map.
        for (StepField field : query.getFields()) {
            String fieldAlias = field.getAlias();
            if (fieldAlias == null) {
                fieldAlias = field.getFieldName();
            }
            fields.put(fieldAlias, org.mmbase.core.util.Fields.createField(fieldAlias, field.getType(), -1, Field.STATE_VIRTUAL, null));
        }
    }

    /**
     * @see org.mmbase.module.core.VirtualBuilder#getNewNode(java.lang.String)
     */
    @Override
    public MMObjectNode getNewNode(String owner) {
        return new ResultNode(this);
    }

    public List<MMObjectNode> getResult() throws StorageException, SearchQueryException {
        AggregatedResultCache cache = AggregatedResultCache.getCache();

        List<MMObjectNode> resultList = cache.get(query);
        if (resultList == null) {
            resultList = this.mmb.getSearchQueryHandler().getNodes(query, this);
            cache.put(query, resultList);
        }
        return resultList;
    }

}

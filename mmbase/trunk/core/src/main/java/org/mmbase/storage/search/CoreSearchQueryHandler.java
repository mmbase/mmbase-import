/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search;

import java.util.List;
import org.mmbase.module.core.MMObjectBuilder;

/**
 * Defines methods for an object that handles search query requests.
 *
 * @author Rob van Maris
 * @version $Id: SearchQueryHandler.java -1   $
 * @since MMBase-1.7
 */
public interface CoreSearchQueryHandler extends SearchQueryHandler {

    /**
     * Processes a search query, returns the result as a list of nodes.
     * Depending on the specified builder, the results will be:
     * <ul>
     * <li>Resultnodes, with fields named according to the field aliases
     * specified by the query.
     * <li>Clusternodes, with fields named
     * <code>&lt;step alias&gt;.&lt;field name&gt;</code>, where
     * the step alias is required to be of the form
     * <code>&lt;step tablename&gt;&lt;x&gt;</code>, and
     * <code>&lt;x&gt;</code> is either empty or a single digit. Examples: <br />
     * <code>images.number</code>, <code>images0.number</code>,
     * <code>images1.number</code>
     * <li>Real nodes, where all fields are required to be included in
     * the query.
     * </ul>
     * @param query The search query.
     * @param builder The builder for the result nodes. Specify a
     *        {@link ResultBuilder ResultBuilder}
     *        to get resultnodes.
     *        {@link org.mmbase.module.core.ClusterBuilder ClusterBuilder}
     *        to get clusternodes.
     * @return The resulting nodes.
     * @see ResultNode
     * @see org.mmbase.module.core.ClusterNode
     */
    public List<org.mmbase.module.core.MMObjectNode> getNodes(SearchQuery query, MMObjectBuilder builder) throws SearchQueryException;


}

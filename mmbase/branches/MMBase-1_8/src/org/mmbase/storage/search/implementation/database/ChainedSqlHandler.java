/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation.database;

import org.mmbase.storage.search.*;

/**
 * Baseclass for <em>chained sql handlers</em>, these are
 * {@link org.mmbase.storage.search.implementation.database.SqlHandler SqlHandler}
 * implementations that wrap <code>SqlHandler</code> objects to create a chain
 * of handlers, following the <em>Chain Of Responsibility</em> design pattern.
 * <p>
 * This class is provided as a baseclass to for chained handlers.
 * It implements all <code>SqlHandler</code> methods by delegating to
 * its <em>successor</em>, i.e. the next handler in the chain.
 *
 * @author  Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 * @see org.mmbase.storage.search.implementation.database.SqlHandler
 */
public class ChainedSqlHandler implements SqlHandler {

    /** Successor in chain or responsibility. */
    private SqlHandler successor = null;

    /**
     * Creates a new instance of ChainedSqlHandler.
     *
     * @param successor Successor in chain of responsibility.
     */
    public ChainedSqlHandler(SqlHandler successor) {
        this.successor = successor;
    }

    // javadoc is inherited
    public String toSql(SearchQuery query, SqlHandler firstInChain)
    throws SearchQueryException {
        return successor.toSql(query, firstInChain);
    }

    // javadoc is inherited
    public void appendQueryBodyToSql(
    StringBuffer sb, SearchQuery query, SqlHandler firstInChain)
    throws SearchQueryException {
        successor.appendQueryBodyToSql(sb, query, firstInChain);
    }

    // javadoc is inherited
    public void appendConstraintToSql(StringBuffer sb, Constraint constraint,
    SearchQuery query, boolean inverse, boolean inComposite)
    throws SearchQueryException {
        successor.appendConstraintToSql(sb, constraint, query,
        inverse, inComposite);
    }

    // javadoc is inherited
    public int getSupportLevel(int feature, SearchQuery query)
    throws SearchQueryException {
        return successor.getSupportLevel(feature, query);
    }

    // javadoc is inherited
    public int getSupportLevel(Constraint constraint, SearchQuery query)
    throws SearchQueryException {
        return successor.getSupportLevel(constraint, query);
    }

    // javadoc is inherited
    public String getAllowedValue(String value) {
        return successor.getAllowedValue(value);
    }

    /**
     * Accessor to successor in chain of responsibility.
     *
     * @return The successor.
     */
    protected SqlHandler getSuccessor() {
        return successor;
    }

}

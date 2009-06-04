/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.storage.search.implementation.database;

import org.mmbase.storage.search.*;
/**
 * Interface for handler classes that are used to create SQL string
 * representations of {@link SearchQuery SearchQuery} objects.
 * <p>
 * A number of <code>SqlHandler</code> objects can create a chain of handlers,
 * following the <em>Chain Of Responsibility</em> design pattern.
 * <p>
 * In short:
 * <ul>
 * <li>A chain is formed of <code>SqlHandler</code> objects, where each
 * element in the chain, except the last one, is called a <em>chained</em>
 * handler.
 * Each chained handler has a <em>successor</em>, which is the next element
 * in the  chain.
 * <li>The first element receives all requests first (a <em>request</em> =
 * call of one of the methods in the interface).
 * When a chained element receives a request, it can either handle it or pass
 * it on to its successor.
 * <li>The last element in the chain, handles all remaining requests.
 * </ul>
 * <p>
 * Each handler in the chain adds functionality to its successor(s),
 * in a way similar to subclassing. The chained design
 * enables a chain of handlers to be configured and created
 * at runtime.
 * <p>
 * In addition to the methods defined in the interface, the concrete
 * <code>SqlHandler</code> class for the last element in the chain
 * is required to have a constructor with this signature:
 * <blockquote><code>
    public &lt;constructor&gt;(Map disallowedValues) { .. }
 * </code></blockquote>
 * where <code>disallowedValues</code> is a map that maps disallowed
 * table/fieldnames to allowed alternatives.
 * <p>
 * The concrete <code>SqlHandler</code> class for the other, chained,
 * elements in the chain are required to have a constructor
 * with this signature:
 * <blockquote><code>
    public &lt;constructor&gt;(SqlHandler successor) { .. }
 * </code></blockquote>
 * where <code>successor</code> is the successor in the chain
 * of responsibility.
 *
 * @author  Rob van Maris
 * @version $Id$
 * @since MMBase-1.7
 */
public interface SqlHandler {
    /**
     * Represents a SearchQuery object as a string in SQL format,
     * using the database configuration.
     *
     * @param query The searchquery.
     * @param firstInChain The first element in the chain of handlers.
     *        At some point <code>appendQueryBodyToSql() will have
     *        to be called on this handler, to generate the body of the query.
     * @return SQL string representation of the query.
     */
    String toSql(SearchQuery query, SqlHandler firstInChain) throws SearchQueryException;

    /**
     * Represents body of a SearchQuery object as a string in SQL format,
     * using the database configuration. Appends this to a stringbuffer.
     * <br />
     * The body of the SQL query string is defined as the substring containing
     * fields, tables, constraints and orders.
     *
     * @param sb The stringbuffer to append to.
     * @param query The searchquery.
     * @param firstInChain The first element in the chain of handlers.
     *        At some point <code>appendConstraintToSql()</code> will have
     *        to be called on this handler, to generate the constraints in
     *        the query.
     */
    public void appendQueryBodyToSql(StringBuilder sb, SearchQuery query, SqlHandler firstInChain) throws SearchQueryException;

    /**
     * Represents Constraint object, that is not a CompositeConstraint,
     * as a constraint in SQL format, appending the result to a stringbuffer.
     * When it is part of a composite expression, it will be surrounded by
     * parenthesis when needed.
     *
     * @param sb The stringbuffer to append to.
     * @param constraint The (non-composite) constraint.
     * @param query The searchquery containing the constraint.
     * @param inverse True when the inverse constraint must be represented,
     *        false otherwise.
     * @param inComposite True when the constraint is part of
     *        a composite expression.
     */
    void appendConstraintToSql(StringBuilder sb, Constraint constraint,
                               SearchQuery query, boolean inverse, boolean inComposite) throws SearchQueryException;

    /**
     * Gets the level at which a feature is supported for a query
     * by this handler. This is one of either:
     * <ul>
     * <li>{@link SearchQueryHandler#SUPPORT_NONE SUPPORT_NONE}
     * <li>{@link SearchQueryHandler#SUPPORT_WEAK SUPPORT_WEAK}
     * <li>{@link SearchQueryHandler#SUPPORT_NORMAL SUPPORT_NORMAL}
     * <li>{@link SearchQueryHandler#SUPPORT_OPTIMAL SUPPORT_OPTIMAL}
     * </ul>
     * Given the choice, the query handler with the highest level of support is prefered.
     */
    public int getSupportLevel(int feature, SearchQuery query) throws SearchQueryException;

    /**
     * Gets the level at which a constraint is supported for a query
     * by this handler. This is one of either:
     * <ul>
     * <li>{@link SearchQueryHandler#SUPPORT_NONE SUPPORT_NONE}
     * <li>{@link SearchQueryHandler#SUPPORT_WEAK SUPPORT_WEAK}
     * <li>{@link SearchQueryHandler#SUPPORT_NORMAL SUPPORT_NORMAL}
     * <li>{@link SearchQueryHandler#SUPPORT_OPTIMAL SUPPORT_OPTIMAL}
     * </ul>
     * Given the choice, the query handler with the highest level of support is prefered.
     */
    public int getSupportLevel(Constraint constraint, SearchQuery query) throws SearchQueryException;

    /**
     * Maps string to value that is allowed as table or field name.
     *
     * @deprecated use {@link org.mmbase.storage.StorageManagerFactory#getStorageIdentifier}
     * @param value The string value.
     * @return The mapped value.
     */
    public String getAllowedValue(String value);
}

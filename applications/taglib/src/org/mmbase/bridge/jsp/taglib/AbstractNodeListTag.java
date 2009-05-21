/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;


import java.util.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.jstl.core.*;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;
import org.mmbase.bridge.jsp.taglib.util.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.*;

/**
 * AbstractNodeListTag, provides basic functionality for listing objects
 * stored in MMBase
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @author Pierre van Rooden
 * @version $Id$
 */

abstract public class AbstractNodeListTag extends AbstractNodeProviderTag implements BodyTag, ListProvider {

    private static final Logger log = Logging.getLoggerInstance(AbstractNodeListTag.class);

    private static final int QUERY_WARN_SIZE = 1000;

    /**
     * Holds the list of fields to sort the list on.
     * The sort itself is implementation specific.
     */
    protected Attribute orderby = Attribute.NULL;

    /**
     * Holds the direction to sort the list on (per field in {@link #orderby}).
     * The sort itself is implementation specific.
     */
    protected Attribute directions = Attribute.NULL;

    /**
     * Holds the clause used to filter the list.
     * This is either a SQL-clause, with MMBase fields in brackets,
     * a altavista-like search, preceded with the keyword ALTA, or
     * a MMBase database node search, preceded with the keyord MMNODE.
     * The filter itself is implementation specific (not all lists may implement this!).
     */
    protected Attribute constraints = Attribute.NULL;

    final protected NodeListHelper listHelper = new NodeListHelper(this, nodeHelper);

    private Query generatingQuery;

    protected BridgeList<Node> getReturnList() {
        return listHelper.getReturnList();
    }

    public Object getCurrent() {
        return listHelper.getCurrent();
    }

    public int getIndex() {
        return listHelper.getIndex();
    }
    public int getIndexOffset() {
        return listHelper.getIndexOffset();
    }

    public void remove() {
        listHelper.remove();
    }

    public Query getGeneratingQuery() {
        return generatingQuery;
    }

    /**
     * Sets the fields to sort on.
     * @param orderby A comma separated list of fields on which the returned
     * nodes should be sorted
     */
    public void setOrderby(String orderby) throws JspTagException {
        this.orderby = getAttribute(orderby);
    }

    /**
     * Sets the direction to sort on
     * @param directions the selection query for the object we are looking for
     * direction
     */
    public void setDirections(String directions) throws JspTagException {
        this.directions = getAttribute(directions);
    }

    /**
     * Set the list maximum
     * @param max the max number of values returned
     */
    public void setMax(String max) throws JspTagException {
        listHelper.setMax(max);
    }

    /**
     * Set the list offset
     * @param o Offset for the returned list.
     */
    public void setOffset(String o) throws JspTagException {
        listHelper.setOffset(o);
    }

    public void setComparator(String c) throws JspTagException {
        listHelper.setComparator(c);
    }
    public void setAdd(String c) throws JspTagException {
        listHelper.setAdd(c);
    }
    public void setRetain(String c) throws JspTagException {
        listHelper.setRetain(c);
    }
    public void setRemove(String c) throws JspTagException {
        listHelper.setRemove(c);
    }

    public void setVarStatus(String s) throws JspTagException {
        listHelper.setVarStatus(s);
    }


    /**
     * Sets the selection query
     * @param where the selection query
     */
    public void setConstraints(String where) throws JspTagException {
        constraints = getAttribute(where);
    }

    protected static class NodesAndTrim {
        boolean  needsTrim;
        BridgeList<Node> nodeList;
    }

    protected final NodesAndTrim getNodesAndTrim(Query query) throws JspTagException {
        return getNodesAndTrim(query, 0);
    }

    /**
     *
     * @param more  How many more than max must be queried (if something will be subtracted later)
     * @since MMBase-1.7
     * @return true If successful
     */
    protected NodesAndTrim getNodesAndTrim(Query query, int more) throws JspTagException {
        generatingQuery = query;
        if (log.isDebugEnabled()) {
            log.debug("Using " + query.toSql());
        }
        NodesAndTrim result = new NodesAndTrim();
        if (listHelper.getComparator().length() == 0) {
            if (listHelper.getMax() != Attribute.NULL) {
                int m = listHelper.getMax().getInt(this, -1);
                if (m != -1) {
                    query.setMaxNumber(m + more);
                }
            }
            if (listHelper.getOffset() != Attribute.NULL) {
                query.setOffset(listHelper.getOffset().getInt(this, 0));
            }

            if (query instanceof NodeQuery) {
                NodeQuery nq = (NodeQuery) query;
                result.nodeList = nq.getNodeManager().getList(nq);
            } else {
                result.nodeList = query.getCloud().getList(query);
            }
            result.needsTrim = more > 0;
        } else {
            // using comparator, doing max and offset programmaticly, otherwise the comparator is loosing most of its use
            if (query instanceof NodeQuery) {
                NodeQuery nq = (NodeQuery) query;
                result.nodeList = nq.getNodeManager().getList(nq);
            } else {
                result.nodeList = query.getCloud().getList(query);
            }

            // give a warning if what you are doing right now is not very smart
            if(result.nodeList.size() > QUERY_WARN_SIZE) {
                log.warn("Trying to use compare on a query with result size " + result.nodeList.size() + " > " + QUERY_WARN_SIZE + " in page " +
                         ((HttpServletRequest)pageContext.getRequest()).getRequestURI() + "." +
                         " Note that the attribute 'max' will in combination with the 'comparator' attribute not set a limit on the query" +
                         " (but the result will be limited afterwards). You might want to limit the query in another way (use a container?)");
            }
            result.needsTrim = true;
        }
        return result;
    }

    // ContextProvider implementation
    public ContextContainer getContextContainer() throws JspTagException {
        return listHelper.getContextContainer();
    }

    protected static final int NOT_HANDLED = -100;

    protected final int doStartTagHelper() throws JspTagException {
        initTag();
        log.debug("doStartTaghelper");

        listHelper.doStartTagHelper();

        if (getReferid() != null) {
            Object o =  org.mmbase.util.Casting.unWrap(getObject(getReferid()));
            if (o instanceof NodeList) {
                // ok
            } else if (o instanceof BridgeList) {
                BridgeList<?> bl = (BridgeList<?>) o;
                NodeList nl = new CollectionNodeList(bl, getCloudVar());
                for (Map.Entry<Object, Object> entry : bl.getProperties().entrySet()) {
                    nl.setProperty(entry.getKey(), entry.getValue());
                }
                o = nl;
            } else if (o instanceof Collection) {
                o  = new CollectionNodeList((Collection) o, getCloudVar());
            } else {
                throw new JspTagException("Context variable " + getReferid() + " is not a NodeList (or some other Collection of Nodes), but " + (o == null ? "NULL" : "a " + o.getClass()));
            }
            if (orderby != Attribute.NULL) {
                throw new JspTagException("'orderby' attribute does not make sense with 'referid' attribute");
            }

            if (directions != Attribute.NULL) {
                throw new JspTagException("'directions' attribute does not make sense with 'referid' attribute");
            }
            if (constraints != Attribute.NULL) {
                throw new JspTagException("'contraints' attribute does not make sense with 'referid' attribute");
            }
            if (getReferid().equals(getId())) { // in such a case, don't whine
                getContextProvider().getContextContainer().unRegister(getId());
            }
            return setReturnValues((BridgeList<Node>) o, true);
        }
        return NOT_HANDLED;
    }

    /**
     * Creates the node iterator and sets appropriate variables (such as listsize)
     * from a passed node list.
     * The list is assumed to be already sorted and trimmed.
     * @param nodes the nodelist to create the iterator from
     * @return EVAL_BODY_BUFFERED if the resulting list is not empty, SKIP_BODY if the
     *  list is empty. THis value should be passed as the result of {
     *  @link #doStartTag}.
     */
    protected int setReturnValues(BridgeList<Node> nodes) throws JspTagException {
        return setReturnValues(nodes, false);
    }

    /**
     * Creates the node iterator and sets appropriate variables (such as listsize).
     * Takes a node list, and trims it using the offset and
     * max attributes if so indicated.
     * The list is assumed to be already sorted.
     * @param nodes the nodelist to create the iterator from
     * @param trim if true, trim the list using offset and max
     *        (if false, it is assumed the calling routine already did so)
     * @return EVAL_BODY_BUFFERED if the resulting list is not empty, SKIP_BODY if the
     *  list is empty. This value should be passed as the result of {@link #doStartTag}.
     */
    protected int setReturnValues(BridgeList<Node> nodes, boolean trim) throws JspTagException {
        Query query = (Query) nodes.getProperty(NodeList.QUERY_PROPERTY);

        if (query != null) {
            // get changeOn value for mm:changed tag
            List<SortOrder> ls = query.getSortOrders();
            if (ls.size() > 0) {
                StepField sf= ls.get(0).getField();
                if (query instanceof NodeQuery) {
                    nodes.setProperty("orderby", sf.getFieldName());
                } else {
                    nodes.setProperty("orderby", sf.getStep().getAlias() + '.' + sf.getFieldName());
                }
            }
        } else {
            if (orderby != Attribute.NULL) nodes.setProperty("orderby", orderby.getString(this));
        }

        return listHelper.setReturnValues(nodes, trim);
    }


    public int doAfterBody() throws JspTagException {
        super.doAfterBody();
        return listHelper.doAfterBody();
    }

    public int doEndTag() throws JspTagException {
        listHelper.doEndTag();
        return  super.doEndTag();
    }

    public void doFinally() {
        generatingQuery = null;
        listHelper.doFinally();
        super.doFinally();
    }

    /**
     * If you order a list, then the 'changed' property will be
     * true if the field on which you order changed value.
     **/
    public boolean isChanged() {
        return listHelper.isChanged();
    }

    public int size() {
        return listHelper.size();
    }

    public LoopTagStatus getLoopStatus() {
        return listHelper.getLoopStatus();
    }
}


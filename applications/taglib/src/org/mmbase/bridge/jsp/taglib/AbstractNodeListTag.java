/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.*;
import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.debug.TimerTag;
import org.mmbase.bridge.jsp.taglib.util.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.StringSplitter;
import org.mmbase.util.logging.*;

/**
 * AbstractNodeListTag, provides basic functionality for listing objects
 * stored in MMBase
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @author Pierre van Rooden
 * @version $Id: AbstractNodeListTag.java,v 1.58 2003-12-17 20:53:59 michiel Exp $
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

    /**
     * The maximum number of elements in a list.
     * Setting the list size to conform to this maximum is implementation specific.
     */
    protected Attribute  max = Attribute.NULL;

    /**
     * The offset of the elements that are returned in a list.
     * Setting the list to conform to this ofsset is implementation specific.
     */
    protected Attribute offset = Attribute.NULL;


    protected Attribute comparator = Attribute.NULL;


    /**
     * Lists do implement ContextProvider
     */
    private   ContextCollector  collector;

    /**
     * Determines whether a field in {@link #orderby} changed
     * during iteration.
     */
    protected boolean changed = true;

    /**
     * Data member to hold an iteration of the values to return.
     * This variable is set in {@link #setReturnValues(NodeList)}, which
     * should be called from {@link #doStartTag}, and will be used to
     * fill the return variables for every iteration.
     */
    protected NodeIterator nodeIterator;
    protected NodeList     returnList;

    /**
     * The current item
     */
    protected int currentItemIndex= -1;

    /**
     * A handle necessary when using the Time Tag;
     */
    protected int timerHandle = -1;

    private String previousValue = null; 

    public int getIndex() {
        return currentItemIndex;
    }
    public int getIndexOffset() {
        return 1;
    }
    public Object getCurrent() {
        return getNodeVar();
    }

    public void remove() {
        nodeIterator.remove();
    }

    /**
     * Sets the fields to sort on.
     * @param sorted A comma separated list of fields on witch the returned
     * nodes should be sorted
     */
    public void setOrderby(String orderby) throws JspTagException {
        this.orderby = getAttribute(orderby);
    }

    /**
     * Sets the direction to sort on
     * @param direction the selection query for the object we are looking for
     * direction
     */
    public void setDirections(String directions) throws JspTagException {
        this.directions = getAttribute(directions);
    }

    /**
     * Set the list maximum
     * @param max the max number of values returned
     */
    public void setMax(String m) throws JspTagException {
        max = getAttribute(m);
    }

    /**
     * Sets the list maximum with an integer argument. Tomcat needs
     * this if you feed it with an rtexprvalue of type int.
     *

     commented out, use "" + for tomcat!
    public void setMax(int m) {
        max = m;
    }
     */

    /**
     * Set the list offset
     * @param max the max number of values returned
     */
    public void setOffset(String o) throws JspTagException {
        offset = getAttribute(o);
    }
    /*
    public void setOffset(int o) { // also need with integer argument for Tomcat.
        offset = o;
    }

    */


    public void setComparator(String c) throws JspTagException {
        comparator = getAttribute(c);
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
        NodeList nodeList;
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
        NodesAndTrim result = new NodesAndTrim();
        if (comparator.getString(this).equals("")) {
            if (max != Attribute.NULL) {
                int m = max.getInt(this, -1);
                if (m != -1) {
                    query.setMaxNumber(m + more);
                }
            }
            if (offset != Attribute.NULL) {
                query.setOffset(offset.getInt(this, 0));
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
        if (collector == null) return getContextProvider().getContextContainer(); // to make sure old-style implemntation work (which do not initialize container)
        return collector.getContextContainer();
    }

    protected static int NOT_HANDLED = -100;
    protected int doStartTagHelper() throws JspTagException {
        log.debug("doStartTaghelper");

        // make a (temporary) container
        collector = new ContextCollector(getContextProvider().getContextContainer());

        // serve parent timer tag:
        TagSupport t = findParentTag(TimerTag.class, null, false);
        if (t != null) {
            timerHandle = ((TimerTag)t).startTimer(getId(), getClass().getName());
        }

        if (getReferid() != null) {
            Object o =  getObject(getReferid());
            if (! (o instanceof NodeList)) {
                throw new JspTagException("Context variable " + getReferid() + " is not a NodeList");
            }
            if (orderby != Attribute.NULL) {
                throw new JspTagException("'orderby' attribute does not make sense with 'referid' attribute");
            }
            if (offset != Attribute.NULL) {
                throw new JspTagException("'offset' attribute does not make sense with 'referid' attribute");
            }
            if (max != Attribute.NULL) {
                throw new JspTagException("'max' attribute does not make sense with 'referid' attribute");
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
            return setReturnValues((NodeList) o);
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
    protected int setReturnValues(NodeList nodes) throws JspTagException {
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
     *  list is empty. THis value should be passed as the result of {@link #doStartTag}.
     */
    protected int setReturnValues(NodeList nodes, boolean trim) throws JspTagException {
        ListSorter.sort(nodes, (String) comparator.getValue(this), pageContext);

        if (trim && (max != Attribute.NULL || offset != Attribute.NULL)) {
            int currentSize = nodes.size();

            int maxi = max.getInt(this, currentSize);
            int maxx = (maxi > currentSize ? currentSize : maxi);

            int offseti = offset.getInt(this, 0);

            int to = maxx + offseti;
            if (to >= currentSize) {
                to = currentSize;
            }
            if (offseti >= currentSize) {
                offseti = currentSize;
            }
            if (offseti < 0) {
                offseti = 0;
            }
            nodes = nodes.subNodeList(offseti, to);

        }
        returnList   = nodes;

        // returnList is know, now we can serve parent formatter tag
        FormatterTag f = (FormatterTag) findParentTag(FormatterTag.class, null, false);
        if (f != null && f.wantXML()) {
            f.getGenerator().add(nodes);
        }

        nodeIterator = returnList.nodeIterator();
        currentItemIndex= -1;
        previousValue = null;
        changed = true;

        Query query = (Query) nodes.getProperty(NodeList.QUERY_PROPERTY);

        if (query != null) {
            // get changeOn value for mm:changed tag
            List ls = query.getSortOrders();
            if (ls.size() > 0) {
                StepField sf= ((SortOrder)ls.get(0)).getField();
                if (query instanceof NodeQuery) {
                    returnList.setProperty("orderby", sf.getFieldName());
                } else {
                    returnList.setProperty("orderby", sf.getStep().getAlias() + '.' + sf.getFieldName());
                }
            }
        } else {
            if (orderby!=Attribute.NULL) returnList.setProperty("orderby", orderby.getString(this));
        }

        if (nodeIterator.hasNext()) {
            //doInitBody(); // because EVAL_BODY_INCLUDE is returned now (by setReturnValues), doInitBody is not called by taglib impl.
            //return EVAL_BODY_INCLUDE;
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }
    }


    public int doAfterBody() throws JspTagException {
        log.debug("doafterbody");
        super.doAfterBody();
        if (getId() != null) {
            getContextProvider().getContextContainer().unRegister(getId());
        }

        if (collector != null) { // might occur for some legacy extensions
            log.debug("copying to collector");
            collector.doAfterBody();
        }
        if (nodeIterator.hasNext()){
            doInitBody();
            return EVAL_BODY_AGAIN;
        } else {
            log.debug("writing body");
            if (bodyContent != null) {
                try {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                } catch (IOException ioe){
                    throw new TaglibException(ioe);
                }
            }
            return SKIP_BODY;
        }

    }

    public int doEndTag() throws JspTagException {

        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), returnList);
        }
        TagSupport t = findParentTag(TimerTag.class, null, false);
        if (t != null) {
            ((TimerTag)t).haltTimer(timerHandle);
        }
        return  super.doEndTag();
    }

    public void doInitBody() throws JspTagException {
        if (nodeIterator.hasNext()){
            currentItemIndex ++;
            Node next = nodeIterator.nextNode();
            // use order as stored in the nodelist (the property of the tag may not be set
            // if you use referid to get the result of a prevuious listtag)
            String listOrder=(String) returnList.getProperty("orderby");
            if (listOrder != null && ! "".equals(listOrder)) {
                // then you can also ask if 'changed' the node
                // look only at first field of sorted for the /moment.
                String f = (String) StringSplitter.split(listOrder).get(0);
                String value = "" + next.getValue(f); // cannot cast  to String, since it can also be e.g. Integer.
                if (previousValue != null) {
                    if (value.equals(previousValue)) {
                        changed = false;
                    } else {
                        changed = true;
                    }
                }
                previousValue = value;
            }
            setNodeVar(next);
            fillVars();
        }
    }

    /**
     * If you order a list, then the 'changed' property will be
     * true if the field on which you order changed value.
     **/
    public boolean isChanged() {
        return changed;
    }

    public int size(){
        return returnList.size();
    }

}


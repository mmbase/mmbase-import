/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.*;
import org.mmbase.bridge.jsp.taglib.debug.TimerTag;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import javax.servlet.jsp.jstl.core.*;

import java.io.IOException;
import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.Casting;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.7
 */

public class NodeListHelper implements ListProvider {

    private static final Logger log = Logging.getLoggerInstance(NodeListHelper.class);

    private final ContextReferrerTag thisTag;
    private final NodeProviderHelper nodeHelper;

    public NodeListHelper(ContextReferrerTag thisTag, NodeProviderHelper nodeHelper) {
        this.thisTag = thisTag;
        this.nodeHelper = nodeHelper;
    }

    public String getId() {
        try {
            return (String) thisTag.id.getValue(thisTag);
        } catch (JspTagException j) {
            throw new RuntimeException(j);
        }
    }

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

    protected Attribute add = Attribute.NULL;
    protected Attribute retain = Attribute.NULL;
    protected Attribute remove= Attribute.NULL;
    protected Attribute varStatus = Attribute.NULL;
    protected String varStatusName = null;


    /**
     * Lists do implement ContextProvider
     */
    private   ContextCollector  collector;


    /**
     * Determines whether a field in {@link AbstractNodeListTag#orderby} changed
     * during iteration.
     */
    protected boolean changed = true;

    /**
     * Data member to hold an iteration of the values to return.
     * This variable is set in {@link #setReturnValues(BridgeList, boolean)}, which
     * should be called from {@link AbstractNodeListTag#doStartTag}, and will be used to
     * fill the return variables for every iteration.
     */
    protected ListIterator<Node> nodeIterator;
    protected BridgeList<Node> returnList;


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

    public void remove() {
        nodeIterator.remove();
    }

    /**
     * Set the list maximum
     * @param m the max number of values returned
     * @throws JspTagException when parsing of attributes fails
     */
    public void setMax(String m) throws JspTagException {
        max = thisTag.getAttribute(m);
    }

    public Attribute getMax() {
        return max;
    }

    /**
     * Set the list offset
     * @param o The offset for the List.
     * @throws JspTagException when parsing of attributes fails
     */
    public void setOffset(String o) throws JspTagException {
        offset = thisTag.getAttribute(o);
    }

    public Attribute getOffset() {
        return offset;
    }

    public void setComparator(String c) throws JspTagException {
        comparator = thisTag.getAttribute(c);
    }

    /**
     * @since MMBase-1.8
     */
    public void setAdd(String a) throws JspTagException {
        add = thisTag.getAttribute(a);
    }

    /**
     * @since MMBase-1.8
     */
    public void setRetain(String a) throws JspTagException {
        retain = thisTag.getAttribute(a);
    }

    /**
     * @since MMBase-1.8
     */
    public void setRemove(String a) throws JspTagException {
        remove = thisTag.getAttribute(a);
    }
    /**
     * @since MMBase-1.8.6
     */
    public void setVarStatus(String s) throws JspTagException {
        varStatus = thisTag.getAttribute(s);
    }


    public String getComparator() throws JspTagException {
        return comparator.getString(thisTag);
    }

    public BridgeList<Node> getReturnList() {
        return returnList;
    }

    public ContextContainer getContextContainer() throws JspTagException {
        if (collector == null) return thisTag.getContextProvider().getContextContainer(); // to make sure old-style implemntation work (which do not initialize container)
        return collector;
    }
    public PageContext getPageContext() throws JspTagException {
        return thisTag.getPageContext();
    }

    /**
     * @since MMBase-1.9
     */
    protected Cloud getCloud(BridgeList<Node> nodes, Cloud cloud) throws JspTagException {
        if (cloud != null) return cloud;
        if (cloud == null) {
            Query q = (Query) nodes.getProperty(NodeList.QUERY_PROPERTY);
            if (q != null) cloud = q.getCloud();
        }
        if (cloud == null && nodes.size() > 0) {
            Node n = nodes.get(0);
            if (n != null) {
                cloud = n.getCloud();
            } else {
                log.warn("Found NULL in " + nodes);
            }
        }
        if (cloud == null) {
            CloudProvider cp = thisTag.findParentTag(CloudProvider.class, null, false);
            if (cp != null) cloud = cp.getCloudVar();
        }
        return cloud;
    }
    public int setReturnValues(BridgeList<Node> nodes, boolean trim) throws JspTagException {
        Cloud cloud = null;

        if (add != Attribute.NULL) {
            Object addObject = thisTag.getObjectConditional(add.getString(thisTag));
            if (addObject != null) {
                if (addObject instanceof Collection) {
                    nodes.addAll((Collection<Node>) addObject);
                } else {
                    cloud = getCloud(nodes, cloud);
                    nodes.add(Casting.toNode(addObject, cloud));
                }
            }
        }
        if (retain != Attribute.NULL) {
            Object retainObject = thisTag.getObjectConditional(retain.getString(thisTag));
            if (retainObject != null && ! "".equals(retainObject)) {
                if (retainObject instanceof Collection) {
                    nodes.retainAll((Collection<Node>) retainObject);
                } else {
                    cloud = getCloud(nodes, cloud);
                    nodes.retainAll(Collections.singletonList((Casting.toNode(retainObject, cloud))));
                }
            }
        }
        if (remove != Attribute.NULL) {
            Object removeObject = thisTag.getObjectConditional(remove.getString(thisTag));
            if (removeObject != null && ! "".equals(removeObject)) {
                if (removeObject instanceof Collection) {
                    nodes.removeAll((Collection<Node>) removeObject);
                } else {
                    cloud = getCloud(nodes, cloud);
                    nodes.remove((Casting.toNode(removeObject, cloud)));
                }
            }
        }
        ListSorter.sort(nodes, (String) comparator.getValue(thisTag), thisTag);

        if (trim && (max != Attribute.NULL || offset != Attribute.NULL)) {
            int currentSize = nodes.size();

            int maxi = max.getInt(thisTag, currentSize);
            int maxx = (maxi > currentSize ? currentSize : maxi);

            int offseti = offset.getInt(thisTag, 0);

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
            nodes = nodes.subList(offseti, to);

        }
        returnList   = nodes;

        // returnList is know, now we can serve parent formatter tag
        FormatterTag f = thisTag.findParentTag(FormatterTag.class, null, false);
        if (f != null && f.wantXML()) {
            f.getGenerator().add(nodes);
            cloud = getCloud(nodes, cloud);
            f.setCloud(cloud);
        }

        nodeIterator = returnList.listIterator();
        currentItemIndex= -1;
        previousValue = null;
        changed = true;

        if (nodeIterator.hasNext()) {
            setNext(); // because EVAL_BODY_INCLUDE is returned now (by setReturnValues), doInitBody is not called by taglib impl.
            return ContextReferrerTag.EVAL_BODY;
        } else {
            return Tag.SKIP_BODY;
        }
    }

    public void doStartTagHelper() throws JspTagException {
        // make a (temporary) container
        collector = new ContextCollector(thisTag.getContextProvider());
        varStatusName = (String) varStatus.getValue(thisTag);
        // serve parent timer tag:
        TimerTag t = thisTag.findParentTag(TimerTag.class, null, false);
        if (t != null) {
            timerHandle = t.startTimer(getId(), thisTag.getClass().getName());
        }
        /*
        if (thisTag.getReferid() != null) {
            if (offset != Attribute.NULL) {
                throw new JspTagException("'offset' attribute does not make sense with 'referid' attribute");
            }
            if (max != Attribute.NULL) {
                throw new JspTagException("'max' attribute does not make sense with 'referid' attribute");
            }
        }
        */

    }

    public int doAfterBody() throws JspTagException {
        log.debug("doafterbody");
        if (getId() != null) {
            thisTag.getContextProvider().getContextContainer().unRegister(getId());
        }
        if (varStatusName != null) {
            thisTag.getContextProvider().getContextContainer().unRegister(varStatusName);
        }

        if (collector != null) { // might occur for some legacy extensions
            log.debug("copying to collector");
            collector.doAfterBody();
        }
        if (nodeIterator.hasNext()){
            setNext();
            return IterationTag.EVAL_BODY_AGAIN;
        } else {
            log.debug("writing body");
            if (ContextReferrerTag.EVAL_BODY == BodyTag.EVAL_BODY_BUFFERED) {
                BodyContent bodyContent = thisTag.getBodyContent();
                if (bodyContent != null) {
                    try {
                        bodyContent.writeOut(bodyContent.getEnclosingWriter());
                    } catch (IOException ioe){
                        throw new TaglibException(ioe);
                    }
                }
            }
            return Tag.SKIP_BODY;
        }
    }

    public int doEndTag() throws JspTagException {
        if (getId() != null) {
            thisTag.getContextProvider().getContextContainer().register(getId(), returnList, false); // use false because check was done in doStartTag (and doAfterBody not always called).
        }
        nodeHelper.doEndTag();
        TimerTag t = thisTag.findParentTag(TimerTag.class, null, false);
        if (t != null) {
            t.haltTimer(timerHandle);
        }
        doFinally();
        previousValue = null;
        return EVAL_PAGE;
    }


    /**
     * The first ordered field is used to determin the 'changed' status of a Node in a NodeList.
     * @since MMBase-1.8
     */
    protected String getFirstOrderedField(BridgeList<Node> returnList, NodeManager nextNodeManager) {
        // the orderby attribute is arranged in AbstractNodeListTag#setReturnValues
        // Perhaps its code could more logically be present here.
        /*
        Query query = (Query) returnList.getProperty(NodeList.QUERY_PROPERTY);
        if (query != null && false) {
            List sortOrders = query.getSortOrders();
            if (sortOrders.size() > 0) {
                SortOrder order = (SortOrder) sortOrders.get(0);
                StepField stepField = order.getField();
                String alias = stepField.getAlias();
                if (alias == null) {
                    Step step = stepField.getStep();
                    String stepAlias = step.getAlias();
                    if (stepAlias == null) stepAlias = step.getTableName();
                    alias = stepAlias + "." + stepField.getFieldName();
                }
                return alias;
            } else {
                return null;
            }

        } else {
        */
        // use order as stored in the nodelist (the property of the tag may not be set
        // if you use referid to get the result of a previous listtag)
        String listOrder = (String) returnList.getProperty("orderby");
        if (listOrder != null && ! "".equals(listOrder)) {
                // then you can also ask if 'changed' the node
                // look only at first field of sorted for the moment.
            String[] fa = listOrder.trim().split("\\s*,\\s*");
            int i = 0;
            while(i < fa.length && ! nextNodeManager.hasField(fa[i])) {
                i++;
            }
            return i < fa.length ? fa[i] : null;
        } else {
            return null;
        }
        // }
    }

    public void setNext() throws JspTagException {
        try {
            currentItemIndex ++;
            Node next = nodeIterator.next();
            while (next == null) {
                log.warn("Found null in node list " + returnList + " skipping");
                currentItemIndex ++;
                if (nodeIterator.hasNext()) {
                    next = nodeIterator.next();
                } else {
                    return;
                }
            }
            NodeManager nextNodeManager = next.getNodeManager();
            if (nextNodeManager == null) throw new RuntimeException("Found node " + next + " has no NodeManager");
            String orderField = getFirstOrderedField(returnList, nextNodeManager);
            if (orderField != null) {
                String value =  "" + next.getValue(orderField);
                if (previousValue != null) {
                    if (value.equals(previousValue)) {
                        changed = false;
                    } else {
                        changed = true;
                    }
                }
                previousValue = value;

            }
            nodeHelper.setNodeVar(next);
            nodeHelper.fillVars();
            if (varStatusName != null) {
                org.mmbase.bridge.jsp.taglib.util.ContextContainer cc = thisTag.getContextProvider().getContextContainer();
                cc.register(varStatusName, getLoopStatus());
            }
        } catch (BridgeException be) { // e.g. NodeManager does not exist
            log.warn(be.getMessage(), be);
        }
    }

    /**
     * If you order a list, then the 'changed' property will be
     * true if the field on which you order changed value.
     **/
    public boolean isChanged() {
        return changed;
    }

    public int size() {
        return returnList.size();
    }

    public Object getCurrent() {
        return nodeHelper.getNodeVar();
    }

    public LoopTagStatus getLoopStatus() {
        return new ListProviderLoopTagStatus(this);
    }

    public void release() {
        doFinally();
    }

    public void doFinally() {
        if (collector != null) {
            try {
                collector.release(thisTag.getPageContext(), thisTag.getContextProvider().getContextContainer());
                collector = null;
            } catch (Exception e) {
            }
        }
        if (nodeHelper != null) {
            nodeHelper.doFinally();
        }
        nodeIterator = null;
        returnList = null;
        previousValue = null;
    }
    // unused
    public int doStartTag() throws JspTagException {
        return -1;
    }
    // unused
    public Tag getParent() {
        return null;
    }
    // unused
    public void setParent(Tag tag) {

    }
    // unused
    public void setPageContext(PageContext pc) {
    }



}

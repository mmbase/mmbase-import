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

import org.mmbase.bridge.*;
import java.io.IOException;

import org.mmbase.util.StringSplitter;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: NodeListHelper.java,v 1.4 2004-03-24 00:59:01 michiel Exp $ 
 * @since MMBase-1.7
 */

public class NodeListHelper implements ListProvider {

    private static final Logger log = Logging.getLoggerInstance(NodeListHelper.class);
        
    private ContextReferrerTag thisTag;
    private NodeProviderHelper nodeHelper;

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
     * This variable is set in {@link #setReturnValues(NodeList, boolean)}, which
     * should be called from {@link AbstractNodeListTag#doStartTag}, and will be used to
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

    public void remove() {
        nodeIterator.remove();
    }
    /**
     * Set the list maximum
     * @param m the max number of values returned
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

    public String getComparator() throws JspTagException {
        return comparator.getString(thisTag);
    }

    public NodeList getReturnList() {
        return returnList;
    }

    public ContextContainer getContextContainer() throws JspTagException {
        if (collector == null) return thisTag.getContextProvider().getContextContainer(); // to make sure old-style implemntation work (which do not initialize container)
        return collector.getContextContainer();
    }

    public int setReturnValues(NodeList nodes, boolean trim) throws JspTagException {
        
        ListSorter.sort(nodes, (String) comparator.getValue(thisTag), thisTag.getPageContext());

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
            nodes = nodes.subNodeList(offseti, to);

        }
        returnList   = nodes;

        // returnList is know, now we can serve parent formatter tag
        FormatterTag f = (FormatterTag) thisTag.findParentTag(FormatterTag.class, null, false);
        if (f != null && f.wantXML()) {
            f.getGenerator().add(nodes);
        }

        nodeIterator = returnList.nodeIterator();
        currentItemIndex= -1;
        previousValue = null;
        changed = true;



        if (nodeIterator.hasNext()) {
            setNext(); // because EVAL_BODY_INCLUDE is returned now (by setReturnValues), doInitBody is not called by taglib impl.
            return ContextReferrerTag.EVAL_BODY;
        } else {
            return BodyTagSupport.SKIP_BODY;
        }


    }


    public void doStartTagHelper() throws JspTagException {

        // make a (temporary) container
        collector = new ContextCollector((ListProvider) thisTag);

        // serve parent timer tag:
        TagSupport t = thisTag.findParentTag(TimerTag.class, null, false);
        if (t != null) {
            timerHandle = ((TimerTag)t).startTimer(getId(), getClass().getName());
        }
        if (thisTag.getReferid() != null) {
            if (offset != Attribute.NULL) {
                throw new JspTagException("'offset' attribute does not make sense with 'referid' attribute");
            }
            if (max != Attribute.NULL) {
                throw new JspTagException("'max' attribute does not make sense with 'referid' attribute");
            }
        }

    }

    public int doAfterBody() throws JspTagException {
        log.debug("doafterbody");    
        if (getId() != null) {
            thisTag.getContextProvider().getContextContainer().unRegister(getId());
        }

        if (collector != null) { // might occur for some legacy extensions
            log.debug("copying to collector");
            collector.doAfterBody();
        }
        if (nodeIterator.hasNext()){
            setNext();
            return BodyTagSupport.EVAL_BODY_AGAIN;
        } else {
            log.debug("writing body");
            if (ContextReferrerTag.EVAL_BODY == BodyTagSupport.EVAL_BODY_BUFFERED) {
                BodyContent bodyContent = thisTag.getBodyContent();
                if (bodyContent != null) {
                    try {
                        bodyContent.writeOut(bodyContent.getEnclosingWriter());
                    } catch (IOException ioe){
                        throw new TaglibException(ioe);
                    }
                }
            }
            return BodyTagSupport.SKIP_BODY;
        }

    }

    public void doEndTag() throws JspTagException {

        if (getId() != null) {
            thisTag.getContextProvider().getContextContainer().register(getId(), returnList, false); // use false because check was done in doStartTag (and doAfterBody not always called).
        }
        TagSupport t = thisTag.findParentTag(TimerTag.class, null, false);
        if (t != null) {
            ((TimerTag)t).haltTimer(timerHandle);
        }
        
    }

    public void setNext() throws JspTagException {
        currentItemIndex ++;
        try {
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
            nodeHelper.setNodeVar(next);
            nodeHelper.fillVars();
        } catch (BridgeException be) { // e.g. NodeManager does not exist
            log.warn(be.getMessage());
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



}

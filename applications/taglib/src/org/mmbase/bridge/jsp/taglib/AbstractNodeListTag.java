/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTag;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.NodeIterator;
import org.mmbase.bridge.NodeList;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * AbstractNodeListTag, provides basic functionality for listing objects
 * stored in MMBase
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @author Pierre van Rooden
 */
abstract public class AbstractNodeListTag extends AbstractNodeProviderTag implements BodyTag, ListItemInfo {
    private static Logger log = Logging.getLoggerInstance(AbstractNodeListTag.class.getName());

    /**
     * Holds the list of fields to sort the list on.
     * The sort itself is implementation specific.
     */
    protected String sortedString=null;

    /**
     * Holds the direction to sort the list on (per field in {@link #sortedString}).
     * The sort itself is implementation specific.
     */
    protected String directionString=null;

    /**
     * Holds the clause used to filter the list.
     * This is either a SQL-clause, with MMBase fields in brackets,
     * a altavista-like search, preceded with the keyword ALTA, or
     * a MMBase database node search, preceded with the keyord MMNODE.
     * The filter itself is implementation specific (not all lists may implement this!).
     */
    protected String whereString=null;

    /**
     * The maximum number of elements in a list.
     * Setting the list size to conform to this maximum is implementation specific.
     */
    protected int max = -1;

    /**
     * The offset of the elements that are returned in a list.
     * Setting the list to conform to this ofsset is implementation specific.
     */
    protected int offset   = 0;

    /**
     * Determines whether a field in {@link #sortedString} changed
     * during iteration.
     */
    protected boolean changed = true;

    /**
     * Data member to hold an iterationof the values to return.
     * This variable is set in {@link #setReturnValues(NodeList)}, which
     * should be called from {@link #doStartTag}, and will be used to
     * fill the return variables for every iteration.
     */
    protected NodeIterator returnValues;

    /**
     * The actual size of the list.
     */
    protected int listSize = 0;

    /**
     * The current item
     */
    protected int currentItemIndex= -1;

    private String previousValue = null; // static voor doInitBody

    public int getIndex() {
        return currentItemIndex;
    }

    /**
     * Sets the fields to sort on.
     * @param sorted A comma separated list of fields on witch the returned
     * nodes should be sorted
     */
    public void setSorted(String sorted){
        this.sortedString = sorted;
    }

    /**
     * Sets the direction to sort on
     * @param direction the selection query for the object we are looking for
     * direction
     */
    public void setDirection(String direction){
        this.directionString = direction;
    }

    /**
     * Set the list maximum
     * @param max the max number of values returned
     */
    public void setMax(String m) throws JspTagException {
        try {
            max = Integer.parseInt(getAttributeValue(m));
        } catch (NumberFormatException e) {
            throw new JspTagException("Max should be an integer value "+
                        "(value found was "+m+")");
        }
    }

    /**
     * Sets the list maximum with an integer argument. Tomcat needs
     * this if you feed it with an rtexprvalue of type int.
     *
     */
    public void setMax(int m) {
        max = m;
    }

    /**
     * Set the list offset
     * @param max the max number of values returned
     */
    public void setOffset(String o) throws JspTagException {
        try {
            offset = Integer.parseInt(getAttributeValue(o));
        } catch (NumberFormatException e) {
            throw new JspTagException("Offset should be an integer value "+
                        "(value found was "+o+")");
        }
    }

    public void setOffset(int o) { // also need with integer argument for Tomcat.
        offset = o;
    }

    /**
     * Sets the selection query
     * @param where the selection query
     */
    public void setWhere(String where) {
        this.whereString = where;
    }

    abstract public int doStartTag() throws JspTagException;

    /**
     * Creates the node iterator and sets appropriate variables (i.e. listsize)
     * froma  passed node list.
     * The list is assumed to be already sorted and trimmed.
     * @param nodes the nodelist to create the iterator from
     * @return EVAL_BODY_TAG if the resulting list is not empty, SKIP_BODY if the
     *  list is empty. THis value should be passed as the result of {
     *  @link #doStartTag}.
     */
    protected int setReturnValues(NodeList nodes) {
        return setReturnValues(nodes,false);
    }

    /**
     * Creates the node iterator and sets appropriate variables (i.e. listsize).
     * Takes a node list, and trims it using the offset and
     * max attributes if so indicated.
     * The list is assumed to be already sorted.
     * @param nodes the nodelist to create the iterator from
     * @param trim if true, trim the list using offset and max
     *        (if false, it is assumed the calling routine already did so)
     * @return EVAL_BODY_TAG if the resulting list is not empty, SKIP_BODY if the
     *  list is empty. THis value should be passed as the result of {
     *  @link #doStartTag}.
     */
    protected int setReturnValues(NodeList nodes, boolean trim) {
        if (trim && (max>-1 || offset > 0)) {
            int currentSize = nodes.size();
            int maxx = (max>currentSize ? currentSize : max);
            int to = maxx + offset;
            if (to >= currentSize) {
                to = currentSize;
            }
            if (offset >= currentSize) {
                offset = currentSize;
            }
            if (offset < 0) {
                offset = 0;
            }
            nodes = nodes.subNodeList(offset, to);
            returnValues = nodes.nodeIterator();
        } else {
            returnValues = nodes.nodeIterator();
        }
        listSize = nodes.size();
        currentItemIndex= -1;
        previousValue = null;
        changed = true;
        if (returnValues.hasNext())
            return EVAL_BODY_TAG;
        return SKIP_BODY;
    }


    public int doAfterBody() throws JspTagException {
        String id = getId();
        if (id != null && ! "".equals(id)) {
            getContextTag().unRegister(id);
        }
        if (returnValues.hasNext()){
            doInitBody();
            return EVAL_BODY_TAG;
        } else {
            try {
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            } catch (IOException ioe){
                throw new JspTagException(ioe.toString());
            }
            return SKIP_BODY;
        }

    }

    public void doInitBody() throws JspTagException {
        if (returnValues.hasNext()){
            currentItemIndex ++;
            Node next = returnValues.nextNode();
            if (sortedString != null) { // then you can also ask if 'changed' the node
                // look only at first field of sorted for the /moment.
                String f = (String)stringSplitter(sortedString).get(0);
                String value = "" + next.getValue(f); // cannot cast  to String, since it can also be e.g. Integer.
                if (previousValue !=null) {
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

    public boolean isFirst(){
        return (currentItemIndex == 0);
    }

    /**
     * If you order a list, then the 'changed' property will be
     * true if the field on which you order changed value.
     **/
    public boolean isChanged() {
        return changed;
    }

    public int size(){
        return listSize;
    }

    public boolean isLast(){
        return (! returnValues.hasNext());
    }

}


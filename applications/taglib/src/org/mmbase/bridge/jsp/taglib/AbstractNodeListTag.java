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

import org.mmbase.bridge.jsp.taglib.util.StringSplitter;

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
abstract public class AbstractNodeListTag extends AbstractNodeProviderTag implements BodyTag, ListProvider {
    private static Logger log = Logging.getLoggerInstance(AbstractNodeListTag.class.getName());

    /**
     * Holds the list of fields to sort the list on.
     * The sort itself is implementation specific.
     */
    protected String orderby = null;

    /**
     * Holds the direction to sort the list on (per field in {@link orderby}).
     * The sort itself is implementation specific.
     */
    protected String directions = null;

    /**
     * Holds the clause used to filter the list.
     * This is either a SQL-clause, with MMBase fields in brackets,
     * a altavista-like search, preceded with the keyword ALTA, or
     * a MMBase database node search, preceded with the keyord MMNODE.
     * The filter itself is implementation specific (not all lists may implement this!).
     */
    protected String constraints = null;

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
    protected NodeIterator returnValues;
    protected NodeList     returnList;

    /**
     * The current item
     */
    protected int currentItemIndex= -1;

    private String previousValue = null; // static voor doInitBody

    public int getIndex() {
        return currentItemIndex;
    }
    public Object getCurrent() {
        return getNodeVar();
    }

    /**
     * Sets the fields to sort on.
     * @param sorted A comma separated list of fields on witch the returned
     * nodes should be sorted
     */
    public void setOrderby(String orderby) throws JspTagException {
        this.orderby = getAttributeValue(orderby);
    }

    /**
     * Sets the direction to sort on
     * @param direction the selection query for the object we are looking for
     * direction
     */
    public void setDirections(String directions) throws JspTagException {
        this.directions = getAttributeValue(directions);
    }

    /**
     * Set the list maximum
     * @param max the max number of values returned
     */
    public void setMax(String m) throws JspTagException {
        max = getAttributeInteger(m).intValue();
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
        offset = getAttributeInteger(o).intValue();
    }
    /*
    public void setOffset(int o) { // also need with integer argument for Tomcat.
        offset = o;
    }

    */
    /**
     * Sets the selection query
     * @param where the selection query
     */
    public void setConstraints(String where) throws JspTagException {
        this.constraints = getAttributeValue(where);
    }


    protected static int NOT_HANDLED = -100;
    protected int doStartTagHelper() throws JspTagException {
        if (getReferid() != null) {
            Object o =  getObject(getReferid());
            if (! (o instanceof NodeList)) {
                throw new JspTagException("Context variable " + getReferid() + " is not a NodeList");
            }
            if (orderby != null) {
                throw new JspTagException("'orderby' attribute does not make sense with 'referid' attribute");
            }
            if (offset != 0) {
                throw new JspTagException("'offset' attribute does not make sense with 'referid' attribute");
            }
            if (max != -1) {
                throw new JspTagException("'max' attribute does not make sense with 'referid' attribute");
            }
            if (directions != null) {
                throw new JspTagException("'directions' attribute does not make sense with 'referid' attribute");
            }
            if (constraints != null) {
                throw new JspTagException("'contraints' attribute does not make sense with 'referid' attribute");
            }
            return setReturnValues((NodeList) o);
        }
        return NOT_HANDLED;
    }
    /**
     * Creates the node iterator and sets appropriate variables (i.e. listsize)
     * froma  passed node list.
     * The list is assumed to be already sorted and trimmed.
     * @param nodes the nodelist to create the iterator from
     * @return EVAL_BODY_TAG if the resulting list is not empty, SKIP_BODY if the
     *  list is empty. THis value should be passed as the result of {
     *  @link #doStartTag}.
     */
    protected int setReturnValues(NodeList nodes) throws JspTagException {
        return setReturnValues(nodes, false);
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
    protected int setReturnValues(NodeList nodes, boolean trim) throws JspTagException {
        if (trim && (max > -1 || offset > 0)) {
            int currentSize = nodes.size();
            int maxx = (max > currentSize ? currentSize : max);
            if (max == -1 ) maxx = currentSize;
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
       
        } 
        returnList   = nodes;
        returnValues = returnList.nodeIterator();        
        currentItemIndex= -1;
        previousValue = null;
        changed = true;
        if (returnValues.hasNext())
            return EVAL_BODY_TAG;
        return SKIP_BODY;
    }


    public int doAfterBody() throws JspTagException {
        super.doAfterBody();
        if (getId() != null) {
            getContextTag().unRegister(getId());
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
    public int doEndTag() throws JspTagException {
        if (getId() != null) {
            getContextTag().register(getId(), returnList);
        }        
        return  super.doEndTag();
    }

    public void doInitBody() throws JspTagException {
        if (returnValues.hasNext()){
            currentItemIndex ++;
            Node next = returnValues.nextNode();
            if (orderby != null && ! "".equals(orderby)) { 
                // then you can also ask if 'changed' the node
                // look only at first field of sorted for the /moment.
                String f = (String)StringSplitter.split(orderby).get(0);
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
            if (getId() != null) {
                getContextTag().register(getId(), next);
            }        
            fillVars();
        }
    }
    /*
    public boolean isFirst(){
        return (currentItemIndex == 0);
    }
    */

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
    /*
    public boolean isLast(){
        return (! returnValues.hasNext());
    }
    */

}


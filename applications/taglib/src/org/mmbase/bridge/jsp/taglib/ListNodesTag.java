/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.containers.ListNodesContainerTag;
import javax.servlet.jsp.JspTagException;

import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.StringSplitter;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ListNodesTag, provides functionality for listing single nodes in MMBase
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @author Pierre van Rooden
 * @version $Id: ListNodesTag.java,v 1.11 2003-08-08 16:03:48 michiel Exp $ 
 */

public class ListNodesTag extends AbstractNodeListTag {
    private static Logger log = Logging.getLoggerInstance(ListNodesTag.class);

    protected Attribute type      = Attribute.NULL;
    protected Attribute container = Attribute.NULL; 


    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    /**
     * @param type a nodeManager
     */
    public void setType(String t) throws JspTagException {
        type = getAttribute(t);
    }


    protected NodeManager nodeManager;

    /**
     * @since MMBase-1.7
     */
    protected NodeManager getNodeManager() {
        return nodeManager;
    }

    /**
     * @since MMBase-1.7
     */
    protected NodeList getNodes() throws JspTagException {
        ListNodesContainerTag c = (ListNodesContainerTag) findParentTag(ListNodesContainerTag.class, (String) container.getValue(this), false);

        if (c == null) {
            if (type == Attribute.NULL) {
                throw new JspTagException("Attribute 'type' must be provided in listnodes tag (unless referid is given)");
            }            
            nodeManager = getCloud().getNodeManager(type.getString(this));
            NodeList nodes = nodeManager.getList(constraints.getString(this), (String) orderby.getValue(this), directions.getString(this));
            return nodes;
        } else {
            NodeQuery query = (NodeQuery) c.getQuery();
            nodeManager = query.getNodeManager();
            // following code will also be necessary in list-tag, so need perhaps be available in AbstractNodeListTag

            if (constraints != Attribute.NULL) {
                Constraint newConstraint = query.createConstraint(constraints.getString(this));
                Constraint constraint = query.getConstraint();
                if (constraint != null) {
                    log.debug("compositing constraint");
                    newConstraint = query.createConstraint(constraint, CompositeConstraint.LOGICAL_AND, newConstraint);
                }
                query.setConstraint(newConstraint);
            }
            
            List order = StringSplitter.split(orderby.getString(this));
            List dirs  = StringSplitter.split(directions.getString(this));
            for (int i = 0; i < order.size(); i++) {
                int or;
                if (dirs.size() > i &&  "down".equalsIgnoreCase((String) dirs.get(i)))  {                    
                    or =  SortOrder.ORDER_DESCENDING;
                } else {
                    or =  SortOrder.ORDER_ASCENDING;
                }
                StepField orderField = query.createStepField((String) order.get(i));
                query.addSortOrder(orderField, or);
            }
            
            NodeList nodes = query.getNodeManager().getList(query);
            return nodes;
        }        
    }

    /**
     * Performs the search
     */
    public int doStartTag() throws JspTagException{
        int superresult =  doStartTagHelper(); // the super-tag handles the use of referid...
        if (superresult != NOT_HANDLED) {
            return superresult;
        }
        NodeList list = getNodes();
        return setReturnValues(list, true);

    }

}


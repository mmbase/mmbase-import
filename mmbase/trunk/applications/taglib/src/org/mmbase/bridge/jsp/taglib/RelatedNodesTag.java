/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * RelatedNodesTag, provides functionality for listing single related nodes in MMBase
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @author Pierre van Rooden
 * @author Jaco de Groot
 * @version $Id: RelatedNodesTag.java,v 1.24 2003-08-27 21:32:41 michiel Exp $ 
 */

public class RelatedNodesTag extends AbstractNodeListTag {
    private static final Logger log = Logging.getLoggerInstance(RelatedNodesTag.class);
    protected Attribute type      = Attribute.NULL;
    protected Attribute role      = Attribute.NULL;
    protected Attribute searchDir = Attribute.NULL;

    /**
     * @param type a nodeManager
     */
    public void setType(String type) throws JspTagException {
        this.type = getAttribute(type);
    }
    /**
     * @param role a role
     */
    public void setRole(String role) throws JspTagException {
        this.role = getAttribute(role);
    }

    /**
     * The search parameter, determines how directionality affects the search.
     * Possible values are <code>both</code>, <code>destination</code>,
     * <code>source</code>, and <code>all</code>
     * @param search the swerach value
     */
    public void setSearchdir(String search) throws JspTagException {
        searchDir = getAttribute(search);
    }


    /**
     * Performs the search
     */
    public int doStartTag() throws JspTagException {
        int superresult =  doStartTagHelper(); // the super-tag handles the use of referid...
        if (superresult != NOT_HANDLED) {
            return superresult;
        }
        // obtain a reference to the node through a parent tag
        Node parentNode = getNode();
        if (parentNode == null) {
            throw new JspTagException("Could not find parent node!!");
        }
        
        Cloud cloud = getCloud();
        NodeQuery query = cloud.createNodeQuery();
        Step step1 = query.addStep(parentNode.getNodeManager());
        query.addNode(step1, parentNode);
        
        NodeManager otherManager;
        if (type == Attribute.NULL) {
            otherManager = cloud.getNodeManager("object");
        } else {
            otherManager = cloud.getNodeManager(type.getString(this));
        }

        RelationStep step2 = query.addRelationStep(otherManager, (String) role.getValue(this), (String) searchDir.getValue(this));
        Step step3 = step2.getNext();

        query.setNodeStep(step3);  // define it as NodeQuery

        List orderbys    = orderby.getList(this);
        List directionss = directions.getList(this);
        for (int i = 0; i < orderbys.size(); i ++) {
            String fieldName = (String) orderbys.get(i);
            int dot = fieldName.indexOf('.');
            
            StepField sf;
            if (dot == -1) {
                sf = query.getStepField(otherManager.getField(fieldName));
            } else {
                String alias = fieldName.substring(0, dot);
                String field2Name = fieldName.substring(dot + 1);
                if (! alias.equals(step2.getAlias())) throw new  JspTagException("'" + alias + "' not equal '" + step2.getAlias());
                sf = query.createStepField(step2, field2Name);
            }

            int order = SortOrder.ORDER_ASCENDING;
            if (directionss.size() > i) {
                String dir = ((String) directionss.get(i)).toUpperCase();
                if (dir.equals("DOWN")) {
                    order = SortOrder.ORDER_DESCENDING;
                }
            }
            query.addSortOrder(sf, order);
        }

        if (constraints != Attribute.NULL) {
            String s = constraints.getString(this);
            if (! s.equals("")) {
                LegacyConstraint con = query.createConstraint(constraints.getString(this));
                query.setConstraint(con);
            }
        }       
                                           
        NodeList nodes = cloud.getList(query);

        return setReturnValues(nodes, true);
    }
}


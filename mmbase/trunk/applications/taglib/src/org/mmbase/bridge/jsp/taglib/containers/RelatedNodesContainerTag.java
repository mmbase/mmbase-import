/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.containers;

import java.util.List;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.util.Queries;
import org.mmbase.storage.search.*;
//import org.mmbase.util.logging.*;

/**
 * Container cognate of RelatedNodesTag
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7
 * @version $Id: RelatedNodesContainerTag.java,v 1.5 2004-03-24 09:49:20 michiel Exp $
 */
public class RelatedNodesContainerTag extends ListNodesContainerTag {

    //private static final Logger log = Logging.getLoggerInstance(RelatedNodesContainerTag.class);

    protected Attribute role      = Attribute.NULL;
    /**
     * @param role a role
     */
    public void setRole(String role) throws JspTagException {
        this.role = getAttribute(role);
    }


    public int doStartTag() throws JspTagException {        
        Node node = getNode();
        Cloud cloud = getCloud();
        query = cloud.createNodeQuery();

        Step step = query.addStep(node.getNodeManager());
        query.addNode(step, node);

        if (nodeManager != Attribute.NULL || role != Attribute.NULL) {

            String nodeManagerName;
            if (nodeManager == Attribute.NULL) {
                nodeManagerName = "object";
            } else {
                nodeManagerName = nodeManager.getString(this); 
            }
            RelationStep relationStep = query.addRelationStep(cloud.getNodeManager(nodeManagerName),
                                                              (String) role.getValue(this), (String) searchDirs.getValue(this));
            query.setNodeStep(relationStep.getNext());
            if (path != Attribute.NULL) throw new JspTagException("Should specify either 'type' or 'path' attributes on relatednodescontainer");
            if (element != Attribute.NULL) throw new JspTagException("'element' can only be used in combination with 'path' attribute");
        } else {
            if (path == Attribute.NULL) throw new JspTagException("Should specify either 'type' or 'path' attributes on relatednodescontainer");

            List newSteps = Queries.addPath(query, (String) path.getValue(this), (String) searchDirs.getValue(this));
            
            if (element != Attribute.NULL) {
                String alias = element.getString(this);
                Step nodeStep = Queries.searchStep(newSteps, alias);
                if (nodeStep == null) { 
                    throw new JspTagException("Could not set element to '" + alias + "' (no such (new) step)");
                }
                query.setNodeStep(nodeStep);
            } else {
                // default to third step (first two are the node and the relation)
                query.setNodeStep((Step) query.getSteps().get(2));
            }
        }
        return EVAL_BODY;
    }



}

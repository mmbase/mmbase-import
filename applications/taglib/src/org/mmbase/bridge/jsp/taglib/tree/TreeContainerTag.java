/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.tree;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.bridge.jsp.taglib.containers.*;
import org.mmbase.bridge.jsp.taglib.util.*;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.util.*;

/**
 * Container cognate for TreeTag
 *
 * @author Michiel Meeuwissen
 * @since  MMBase-1.7.1
 * @version $Id: TreeContainerTag.java,v 1.4 2005-06-20 16:03:38 michiel Exp $
 */
public class TreeContainerTag extends RelatedNodesContainerTag implements NodeQueryContainer, ContainerReferrer { // extending from relatednodescontainer only for the attributes

    protected Attribute maxDepth    = Attribute.NULL;
    protected Attribute container   = Attribute.NULL;

    public void setMaxdepth(String md) throws JspTagException {
        maxDepth = getAttribute(md);
    }
    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }

    protected GrowingTreeList tree;

    public TreeList getTree() {
        return tree;
    }


    public Query getQuery() {
        return tree.getTemplate();
    }
    public NodeQuery getNodeQuery() {
        return (NodeQuery) getQuery();
    }
    
    /**
     * Retrieves the starting query from environment. 
     * Static because also used by TreeTag itself.
     */
    static NodeQuery  getStartQuery(ContextReferrerTag thisTag, Attribute containerAttribute, Attribute nodeAttribute) throws JspTagException {
        NodeQuery query = null;
        String container = (String) containerAttribute.getValue(thisTag);
        String node      = nodeAttribute.getString(thisTag);
        if (container == null && node.equals("")) {
            NodeQueryContainer c = (NodeQueryContainer) thisTag.findParentTag(NodeQueryContainer.class, null, false);
            if (c != null) {
                query = c.getNodeQuery();
            }
        } else if (! "".equals(container)) {
            NodeQueryContainer c = (NodeQueryContainer) thisTag.findParentTag(NodeQueryContainer.class, container, false);
            if (c != null) {
                query = c.getNodeQuery();
            }
        }
        if (query == null) { // try to work as node-referrer
            NodeProvider np =  (NodeProvider) thisTag.findParentTag(NodeProvider.class, (String) nodeAttribute.getValue(thisTag), false);
            if (np == null) {
                throw new TaglibException("No NodeQueryContainer nor a NodeProvider found in tree-tag");
            } else {
                query = Queries.createNodeQuery(np.getNodeVar());
            }
        }
        return query;

    }


    public int doStartTag() throws JspTagException {
        // first of all, we need a 'start' query, take it from a surrounding 'nodequery container' 

        query = getStartQuery(this, container, parentNodeId);

        
        if (nodeManager != Attribute.NULL) {
            tree = new GrowingTreeList(query, maxDepth.getInt(this, 5),  
                                       query.getCloud().getNodeManager(nodeManager.getString(this)),
                                       role.getString(this),
                                       searchDirs.getString(this));
                                       
            if (path != Attribute.NULL) throw new JspTagException("Should specify either 'type' or 'path' attributes on listnodescontainer");
        } else {
            if (path == Attribute.NULL) throw new JspTagException("Should specify either 'type' or 'path' attributes on listnodescontainer");
            tree = new GrowingTreeList(query, maxDepth.getInt(this, 5));
            Queries.addPath(tree.getTemplate(), (String) path.getValue(this), (String) searchDirs.getValue(this));
            query.setNodeStep((Step) tree.getTemplate().getSteps().get(2));
        }
        return EVAL_BODY;
    }

    public int doAfterBody() throws JspTagException {
        if (EVAL_BODY == EVAL_BODY_BUFFERED) {
            try {
                if (bodyContent != null) {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                }
            } catch (java.io.IOException ioe){
                throw new JspTagException(ioe.toString());
            } 
        }
        return SKIP_BODY;        
    }
    public int doEndTag() throws JspTagException {
        tree = null;
        return super.doEndTag();
    }

}

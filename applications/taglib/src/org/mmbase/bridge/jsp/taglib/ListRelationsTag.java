/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.containers.ListRelationsContainerTag;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;

/**
 * ListRelationsTag, a tag around bridge.Node.getRelations.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ListRelationsTag.java,v 1.24 2008-12-30 16:19:54 michiel Exp $
 */

public class ListRelationsTag extends AbstractNodeListTag {

    private Attribute type = Attribute.NULL;
    private Attribute role = Attribute.NULL;
    private Attribute searchDir = Attribute.NULL;
    protected Attribute container = Attribute.NULL;

    private NodeManager nm;
    private Node     relatedFromNode;
    private static final String RELATED_FROM = "relatedFromNode";


    Node getRelatedfromNode() {
        BridgeList<Node> returnList = getReturnList();
        return returnList == null ? null : (Node) returnList.getProperty(RELATED_FROM);
    }


    public void setContainer(String c) throws JspTagException {
        container = getAttribute(c);
    }


    /**
     * @param t The name of a node manager
     */
    public void setType(String t) throws JspTagException {
        type  = getAttribute(t);
    }
    /**
     * @param r a role
     */
    public void setRole(String r) throws JspTagException {
        role  = getAttribute(r);
    }

    public void setSearchdir(String s) throws JspTagException {
        searchDir = getAttribute(s);
    }




    public Node getRelatedNode() throws JspTagException {
        Relation rel = getNodeVar().toRelation();
        if (rel.getIntValue("snumber") == relatedFromNode.getNumber()) {
            return rel.getDestination();
        } else {
            return rel.getSource();
        }
    }

    public int doStartTag() throws JspTagException{
        int superresult =  doStartTagHelper(); // the super-tag handles the use of referid...
        if (superresult != NOT_HANDLED) {
            relatedFromNode = (Node)      listHelper.getReturnList().getProperty(RELATED_FROM);
            if (relatedFromNode == null) {
                throw new IllegalStateException("No 'relatedFromProperty' found in list " + listHelper.getReturnList().getClass() + " " + listHelper.getReturnList());
            }
            return superresult;
        }

        ListRelationsContainerTag c = findParentTag(ListRelationsContainerTag.class, (String) container.getValue(this), false);

        NodeQuery query;
        if (c == null || type != Attribute.NULL || role != Attribute.NULL || searchDir != Attribute.NULL) { // containerless version
            // obtain a reference to the node through a parent tag
            relatedFromNode = getNode();
            if (relatedFromNode == null) {
                throw new JspTagException("Could not find parent node!!");
            }

            nm = null;
            if (type != Attribute.NULL) {
                nm = relatedFromNode.getCloud().getNodeManager(type.getString(this));
            }

            query = Queries.createRelationNodesQuery(relatedFromNode, nm, (String) role.getValue(this), (String) searchDir.getValue(this));
        } else { // working with container
            query = (NodeQuery) c.getQuery();
            relatedFromNode = c.getRelatedFromNode();
        }


        if (orderby != Attribute.NULL) {
            Queries.addSortOrders(query,        (String) orderby.getValue(this), (String) directions.getValue(this));
        }

        Queries.sortUniquely(query);

        NodesAndTrim result = getNodesAndTrim(query);
        assert relatedFromNode != null;
        result.nodeList.setProperty(RELATED_FROM, relatedFromNode); // used to be used by mm:relatednode but not any more.
        assert result.nodeList.getProperty(RELATED_FROM) != null;


        return setReturnValues(result.nodeList, result.needsTrim);
    }

    public int doEndTag() throws JspTagException {
        return super.doEndTag();
    }

    public void doFinally() {
        nm = null;
        relatedFromNode = null;
        super.doFinally();
    }
}


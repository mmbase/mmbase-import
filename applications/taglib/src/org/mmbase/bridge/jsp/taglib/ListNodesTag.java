/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.NodeManager;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * ListNodesTag, provides functionality for listing single nodes in MMBase
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 * @author Pierre van Rooden
 */
public class ListNodesTag extends AbstractNodeListTag {
    private static Logger log = Logging.getLoggerInstance(ListNodesTag.class.getName());

    protected String typeString=null;

    /**
     * @param type a nodeManager
     */
    public void setType(String type) throws JspTagException {
        typeString = getAttributeValue(type);
    }

    /**
     * Performs the search
     */
    public int doStartTag() throws JspTagException{
        int superresult =  doStartTagHelper(); // the super-tag handles the use of referid...
        if (superresult != NOT_HANDLED) {
            return superresult;
        }
        if (typeString == null) {
            throw new JspTagException("Attribute 'type' must be provided in listnodes tag (unless referid is given)");
        }

        NodeManager manager=getCloud().getNodeManager(typeString);
        NodeList nodes = manager.getList(constraints, orderby, directions);
        return setReturnValues(nodes,true);
    }

}


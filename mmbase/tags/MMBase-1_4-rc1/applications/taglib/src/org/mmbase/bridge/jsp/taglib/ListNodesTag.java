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
        NodeManager manager=getCloudProviderVar().getNodeManager(typeString);
        NodeList nodes = manager.getList(whereString,sortedString,directionString);
        return setReturnValues(nodes,true);
    }

}


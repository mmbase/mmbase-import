/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.util.*;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.NodeList;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.util.logging.*;

/**
 * ResultListTag, provides functionality for obtaining a list using a nodemanager command
 *
 * @author Pierre van Rooden
 * @version $Id: ResultListTag.java,v 1.3 2003-12-03 06:57:39 keesj Exp $
 * @todo compare with functioncontainer/functioncontainerreferrers
 */

public class ResultListTag extends AbstractNodeListTag {
    private static final Logger log = Logging.getLoggerInstance(ResultListTag.class);

    protected Attribute command = Attribute.NULL;
    protected Attribute nodemanager = Attribute.NULL;
    protected Attribute module = Attribute.NULL;

    /**
     * @param fields a comma separated list of fields of the nodes.
     **/
    public void setCommand(String command) throws JspTagException {
        this.command = getAttribute(command);
    }

    public void setModule(String module) throws JspTagException {
        this.module = getAttribute(module);
    }

    public void setNodemanager(String nodemanager) throws JspTagException {
        this.nodemanager = getAttribute(nodemanager);
    }

    /**
     * Performs the list command
     */
    public int doStartTag() throws JspTagException {
        javax.servlet.http.HttpServletRequest request = (javax.servlet.http.HttpServletRequest) pageContext.getRequest();
        javax.servlet.http.HttpServletResponse response = (javax.servlet.http.HttpServletResponse) pageContext.getResponse();

        Map attributes = new HashMap();

        NodeList nodes;

        if (nodemanager != Attribute.NULL) {
            if (module != Attribute.NULL) {
                throw new JspTagException("You can only use one of 'nodemanager' or 'module' in a resultlist tag");
            }
            nodes = getCloud().getNodeManager(nodemanager.getString(this)).getList(command.getString(this), attributes, request, response);
        } else {
            if (module == Attribute.NULL) {
                throw new JspTagException("Either attribute 'nodemanager' or 'module' must be provided in the resultlist tag");
            }
            nodes = getCloud().getCloudContext().getModule(module.getString(this)).getList(command.getString(this), attributes, request, response);
        }

        return setReturnValues(nodes, true);
    }

}

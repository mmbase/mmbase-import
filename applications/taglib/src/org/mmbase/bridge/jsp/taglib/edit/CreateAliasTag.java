/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.edit;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.jsp.taglib.NodeReferrerTag;

/**
* To call the method createAlias from Node.
* 
* @author Michiel Meeuwissen
*/
public class CreateAliasTag extends NodeReferrerTag {    

    private String parameter = null;

    public void setParameter(String p) {
        parameter = p;
    }
    /**
    * Add the alias.
    **/
    public int doAfterBody() throws JspTagException {        
        // search the node:
        Node node = findNodeProvider().getNodeVar();
        
        // alias name is in the body if no parameter is given
        String alias;
        if (parameter == null) {
            alias = bodyContent.getString();
        } else {
            alias =  pageContext.getRequest().getParameter(parameter);
        }

        if (alias != null) {
            if (! "".equals(alias)) {
                node.createAlias(alias);
            }
        }
        return SKIP_BODY;
    }
}

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

    private String alias = null;

    public void setName(String n) throws JspTagException {
        alias = getAttributeValue(n);
    }

    protected void doJob(Node n, String alias) {
        n.createAlias(alias);
    }

    /**
    * Add the alias.
    **/
    public int doAfterBody() throws JspTagException {        
        // search the node:
        Node node = getNode();
        
        // alias name is in the body if no attribute name is given
        if (alias == null) {
            alias = bodyContent.getString();
        } 

        if (alias != null) {
            if (! "".equals(alias)) {
                doJob(node, alias);
            }
        }
        return SKIP_BODY;
    }
}

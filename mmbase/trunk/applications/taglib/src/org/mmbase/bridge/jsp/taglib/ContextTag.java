/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;
import java.io.IOException;

import java.util.HashMap;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
*
* Groups tags. The tags can referrence each other, by use of the group
* hash of this class.
*
* @author Michiel Meeuwissen
*
**/
public class ContextTag extends CloudReferrerTag implements CloudProvider {

    private static Logger log = Logging.getLoggerInstance(ContextTag.class.getName());

    private HashMap nodes = new HashMap();
      
    public void  registerNode(String id, Node n) {
        nodes.put(id, n);
    }

    public Cloud getCloudVar() throws JspTagException {
        return getCloudProviderVar();
    }
    
    public Node getNode(String id) throws JspTagException {
        Node n = (Node) nodes.get(id);
        if (n == null) {
            throw new JspTagException("No node with id " + id + " was registered");
        }
        return n; 
    }
    public int doAfterBody() throws JspTagException {
        try {
            bodyContent.writeOut(bodyContent.getEnclosingWriter());            
            return SKIP_BODY;
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }
    }

}


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
import javax.servlet.http.HttpServletRequest;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

import org.mmbase.util.HttpPost;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
*
* Groups tags. The tags can referrence each other, by use of the group
* hash of this class.
*
* @author Michiel Meeuwissen
*
*/
public class ContextTag extends CloudReferrerTag implements CloudProvider {

    private static final int TYPE_HASHMAP = 0;
    private static final int TYPE_SESSION = 1;
    private static final int TYPE_PARAMETERS = 2;
    private static final int TYPE_POSTPARAMETERS = 3;

    private static Logger log = Logging.getLoggerInstance(ContextTag.class.getName());

    private HashMap nodes = new HashMap(); 
    // this hashmap can always be useful, also if the context is not TYPE_HASHMAP

    private int type = TYPE_HASHMAP;

    private HttpPost poster = null;
    private HttpServletRequest httpRequest = null;

    // avoid casting
    private HttpServletRequest getHttpRequest() {
        if (httpRequest == null) {
            httpRequest = (HttpServletRequest)pageContext.getRequest();
        }
        return httpRequest;
    }

    private HttpPost getPoster() {
        if (poster == null) {
            poster = new HttpPost(getHttpRequest());
        }
        return poster;
    }

    public void setType(String s) throws JspTagException {
        if ("hashmap".equals(s)) {
            type = TYPE_HASHMAP;
        } else if ("session".equals(s)) {
            type = TYPE_SESSION;
        } else if ("parameters".equals(s)) {
            type = TYPE_PARAMETERS;
        } else if ("postparameters".equals(s)) {
            type = TYPE_POSTPARAMETERS;
        } else {
            throw new JspTagException("Unknown context-type " + s);
        }        
    }

    /**
     *
     * 
     * @param key the key (id) of the node to register
     * @param node the node to put in the hashmap
     */
      
    public void  registerNode(String key, Node n) {
        switch (type) {
        case TYPE_SESSION:
            getHttpRequest().getSession().setAttribute(key, n);
            break;
        case TYPE_PARAMETERS:
        case TYPE_POSTPARAMETERS:
        case TYPE_HASHMAP:
        default:
            nodes.put(key, n);
            break;            
        }
    }

    public Cloud getCloudVar() throws JspTagException {
        return getCloudProviderVar();
    }
    
    public Object getObject(String key) throws JspTagException {
        Object result;
        switch (type) {
        case TYPE_SESSION:
            result = getHttpRequest().getSession().getAttribute(key);
            break;
        case TYPE_POSTPARAMETERS:
            result = getPoster().getPostParameter(key);
            if (result != null) break;
        case TYPE_PARAMETERS:
            result = pageContext.getRequest().getParameter(key);
            break;
        default:
            result = "";
        }
        return result;
    }

    public byte[] getBytes(String key) throws JspTagException {
        switch (type) {
        case TYPE_POSTPARAMETERS:
            try {
                return getPoster().getPostParameterBytes(key);
            } catch (org.mmbase.util.PostValueToLargeException e) {
                throw new JspTagException("Post value to large (" + e.toString() + ")");
            }
        default:
            throw new JspTagException("Can only get bytes from postparameters Context");
        }
    }

    public Node getNode(String key) throws JspTagException {
        Node n;
        switch (type) {
        case TYPE_SESSION:
            n = (Node) getHttpRequest().getSession().getAttribute(key);
            break;
        case TYPE_PARAMETERS:
            String paramValue = pageContext.getRequest().getParameter(key);
            if (paramValue != null ) { // found in paramlist
                n = getCloudProviderVar().getNode(pageContext.getRequest().getParameter(key));
                break;
            } // else also try with hashmap:
        case TYPE_HASHMAP:
        default:
            n = (Node) nodes.get(id);
        }
        if (n == null) {
            throw new JspTagException("No node with id " + key + " was registered");
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


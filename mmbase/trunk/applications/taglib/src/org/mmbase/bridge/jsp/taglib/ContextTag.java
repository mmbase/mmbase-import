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
* hash of this class. You can have several types, but they have a
* certain order. The most complex Context is a Context of type
* 'session'. If you try to get something from a session context, it
* will first search in the session, if it cannot find it there, it
* will try to find a postparameter with that key, if that is also not
* there, it will try a parameter, and finally it will try to see if
* the object was registered in this page.
*
* Inside a session context you will by the way not register anything
* in the hashmap of the context, because the session is used for that
* already.
*
*
* @author Michiel Meeuwissen
*/
public class ContextTag extends CloudReferrerTag implements CloudProvider {

    private static final int TYPE_PARENT         = -10; // uses parent Contex, if there is one.
    private static final int TYPE_HASHMAP        = 0;
    private static final int TYPE_PARAMETERS     = 10;
    private static final int TYPE_POSTPARAMETERS = 20;
    private static final int TYPE_SESSION        = 30;

    private static Logger log = Logging.getLoggerInstance(ContextTag.class.getName());

    private HashMap hashMap = new HashMap(); 
    // this hashmap can always be useful, also if the context is not TYPE_HASHMAP

    private int type = TYPE_PARENT;

    private CloudProvider parent = null;
    private boolean searchedParent = false;

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

    private CloudProvider getParentCloudProvider() {
        if (! searchedParent) {
            try {
                parent = findCloudProvider();
            } catch (JspTagException e) {
                // ok. No parent CloudProvider. No problem.
                // use the hashmap.
                parent = null;
            }
            searchedParent = true;
        }
        return parent;
    }

    public void setType(String s) throws JspTagException {
        if ("parent".equalsIgnoreCase(s)) {
            type = TYPE_PARENT;
        } else if ("hashmap".equalsIgnoreCase(s)) {
            type = TYPE_HASHMAP;
        } else if ("session".equalsIgnoreCase(s)) {
            type = TYPE_SESSION;
        } else if ("parameters".equalsIgnoreCase(s)) {
            type = TYPE_PARAMETERS;
        } else if ("postparameters".equalsIgnoreCase(s)) {
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
        register(key, n);
    }

    /** 
     * Register an Object with a key in the context. If the Context is
     * a session context, then it will be put in the session, otherwise in the hashmap.
     */

    public void register(String key, Object n) {
        if (log.isDebugEnabled()) {
            log.trace("registering " + n + " under " + key);
        }
        switch (type) {
        case TYPE_SESSION:
            getHttpRequest().getSession().setAttribute(key, n);
            break;
        case TYPE_PARAMETERS:
        case TYPE_POSTPARAMETERS:
        case TYPE_PARENT:
            if (getParentCloudProvider() != null) {
                parent.register(key, n);
                break;
            }
        case TYPE_HASHMAP:
            hashMap.put(key, n);
            break;        
        }
    }

    public void unRegister(String key) {
        switch(type) {
        case TYPE_SESSION:            
            getHttpRequest().getSession().removeAttribute(key);
            break;
        case TYPE_PARENT:
            if (getParentCloudProvider() != null) {
                parent.unRegister(key);
                break;
            }
        case TYPE_HASHMAP:
            hashMap.remove(key);
            break;
        }
    }

    public Cloud getCloudVar() throws JspTagException {
        return getCloudProviderVar();
    }
    
    public Object getObject(String key) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.trace("getting object " + key);
        }
        Object result;
        switch (type) {
        case TYPE_SESSION:
            result = getHttpRequest().getSession().getAttribute(key);
            if (result != null) break;
        case TYPE_POSTPARAMETERS:
            result = getPoster().getPostParameter(key);
            if (result != null) break;
        case TYPE_PARAMETERS:
            result = pageContext.getRequest().getParameter(key);
            if (result != null) break;
        case TYPE_PARENT:
            if (getParentCloudProvider() != null) {
                result = parent.getObject(key);
                break;
            }
        case TYPE_HASHMAP:
            result = hashMap.get(key);
            break;
        default:
            result = null;
        }
        return result;
    }

    public byte[] getBytes(String key) throws JspTagException {
        switch (type) {
        case TYPE_PARENT:
            if (getParentCloudProvider() != null) {
                return parent.getBytes(key);
            }
        case TYPE_SESSION:
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
        String paramValue = null;
        switch (type) {
        case TYPE_SESSION: // node can be in session
            n = (Node) getHttpRequest().getSession().getAttribute(key);
            break;          
        case TYPE_POSTPARAMETERS: // in parameters can only be a nodenumber...
            paramValue = getPoster().getPostParameter(key);
        case TYPE_PARAMETERS:
            if (paramValue == null) { // not found as postparameter
                paramValue = pageContext.getRequest().getParameter(key);
            }
            if (paramValue != null ) { // found in paramlist
                n = getCloudProviderVar().getNode(pageContext.getRequest().getParameter(key));
                break;
            } // else also try with hashmap:
        case TYPE_PARENT:
            if (getParentCloudProvider() != null) {
                return parent.getNode(key);
            }
        case TYPE_HASHMAP:
        default:
            n = (Node) hashMap.get(key);
        }
        if (n == null) {
            throw new JspTagException("No node with key " + key + " was registered");
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


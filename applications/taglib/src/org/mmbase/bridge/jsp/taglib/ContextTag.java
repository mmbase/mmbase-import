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
import java.util.Vector;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
public class ContextTag extends ContextReferrerTag {

    public static final int TYPE_PARENT         = -10; // uses parent Contex, if there is one.
    public static final int TYPE_HASHMAP        = 0;
    public static final int TYPE_PARAMETERS     = 10;
    public static final int TYPE_POSTPARAMETERS = 20;
    public static final int TYPE_SESSION        = 30;

    private static Logger log = Logging.getLoggerInstance(ContextTag.class.getName());

    private HashMap hashMap = new HashMap(); 
    // this hashmap can always be useful, also if the context is not TYPE_HASHMAP

    private int type = TYPE_PARENT;

    private ContextTag parent = null;
    private boolean searchedParent = false;

    private HttpPost poster = null;
    private HttpServletRequest httpRequest = null;
    private HttpSession        httpSession = null;

    // avoid casting
    private HttpServletRequest getHttpRequest() {
        if (httpRequest == null) {
            httpRequest = (HttpServletRequest)pageContext.getRequest();
        }
        return httpRequest;
    }

    
    private HttpSession getSession() {
        if (httpSession == null) {
            httpSession = getHttpRequest().getSession();
        }
        return httpSession;        
    }
    

    private HttpPost getPoster() {
        if (poster == null) {
            poster = new HttpPost(getHttpRequest());
        }
        return poster;
    }

    private ContextTag getParentContext() {
        if (! searchedParent) {
            try {
                parent = getContextTag();
            } catch (JspTagException e) {
                // ok. No parent Context. No problem.
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
    
    public int getTypeInt() { 
        // for some reason simply 'getType'
        // doesn't work (Bean property gets read only)
        return type;
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
            getSession().setAttribute(key, n);
            break;
        case TYPE_PARAMETERS:
        case TYPE_POSTPARAMETERS:
        case TYPE_PARENT:
            if (getParentContext() != null) {
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
            getSession().removeAttribute(key);
            break;
        case TYPE_PARAMETERS:
            pageContext.getRequest().removeAttribute(key);
            break;
        case TYPE_PARENT:
            if (getParentContext() != null) {
                parent.unRegister(key);
                break;
            }
        case TYPE_HASHMAP:
            hashMap.remove(key);
            break;
        }
    }

    /*
    public Cloud getCloudVar() throws JspTagException {
        return getCloudProviderVar();
    }
    */
    
    public Object getObject(String key) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.trace("getting object " + key);
        }
        Object result;
        switch (type) {
        case TYPE_SESSION:
            result = getSession().getAttribute(key);
            if (result != null) break;
        case TYPE_POSTPARAMETERS:
            result = getPoster().getPostParameter(key);
            if (result != null) break;
        case TYPE_PARAMETERS:
            result = pageContext.getRequest().getParameter(key);
            if (result != null) break;
        case TYPE_PARENT:
            if (getParentContext() != null) {
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
    public String getString(String key) throws JspTagException {
        return (String) getObject(key);

    }

    public String getObjectAsString(String key) throws JspTagException {
        Object o = getObject(key);
        if (o == null) return null;
        if (o instanceof Node) {
            Node n = (Node) o;
            return "" + n.getNumber();
        } 
        return o.toString();        
    }

    public byte[] getBytes(String key) throws JspTagException {
        switch (type) {
        case TYPE_PARENT:
            if (getParentContext() != null) {
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
        return (Node) getObject(key);
    }

    /**
     * Returns all keys which are known to this context.
     */

    public Vector getKeys(int t) {
        int _t = t;
        if (type < _t) _t = type; // can e.g. not treat hashmap as parameter context.
        Vector result = new Vector();
        switch (_t) {
        case TYPE_SESSION: {
            Enumeration e = getSession().getAttributeNames();
            while (e.hasMoreElements()) {
                result.add(e.nextElement());
            }
        }
        case TYPE_POSTPARAMETERS: {
            Enumeration e = getPoster().getPostParameters().keys();
            while (e.hasMoreElements()) {
                result.add(e.nextElement());
            }
        }
        case TYPE_PARAMETERS: {
            Enumeration e = pageContext.getRequest().getParameterNames();
            while (e.hasMoreElements()) {
                result.add(e.nextElement());
            }
        }
        case TYPE_PARENT:
            if (getParentContext() != null) {
                result.addAll(parent.getKeys(t));
            }
        case TYPE_HASHMAP:
            result.addAll(hashMap.keySet());
            break;
        default:
        }
        return result;        
    }


    public Vector getKeys() {
        return getKeys(type);
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


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

    public static final int TYPE_NOTSET         = -10;
    public static final int TYPE_PAGE           = 0;
    public static final int TYPE_PARENT         = 5; // uses parent Contex, if there is one.
    public static final int TYPE_PARAMETERS     = 10;
    public static final int TYPE_POSTPARAMETERS = 20;
    public static final int TYPE_SESSION        = 30;

    private static Logger log = Logging.getLoggerInstance(ContextTag.class.getName());

    private ContextTag parent = null;
    private boolean    searchedParent = false;

    private HttpPost           poster = null;
    private HttpServletRequest httpRequest = null;
    private HttpSession        httpSession = null;

    public void release() {
        log.debug("releasing");
        super.release();
    }

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

    public void setId(String i) {
        log.debug("setting id to " + i);
        id = i;
    }

    /**
     * Returns the id of the context. If there was no id-attibute in
     * this tag, then the id is implicitily 'context'.
     */

    public String getId() {
        if (id == null) return "context"; // a context has a default id.
        return id;
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


    /**
     *
     *
     * @param key the key (id) of the node to register
     * @param node the node to put in the hashmap
     */

    public void  registerNode(String key, Node n) throws JspTagException {
        register(key, n);
    }


    //

    public boolean findAndRegister(int from, String referid, String newid) throws JspTagException {
        if (newid == null) {
            throw new JspTagException("Cannot register with id is null");
        }
        if (referid == null) {
            throw new JspTagException("Cannot refer with id is null");
        }
        Object result = null;
        switch (from) {
        case TYPE_SESSION:
            result = getSession().getAttribute(referid);
            break;
        case TYPE_POSTPARAMETERS:
            result = getPoster().getPostParameter(referid);
            break;
        case TYPE_PARAMETERS:
            result = pageContext.getRequest().getParameter(referid);
            break;
        case TYPE_PARENT:
            if (getParentContext() != null) {
                if (parent.isRegistered(referid)) {
                    result = parent.getObject(referid);
                }
            }
            break;
        case TYPE_PAGE:
            //result = pageContext.getAttribute(referid);
            break;
        default:
            result = null;
        }
        register(newid, result);
        if (result == null) return false;
        log.debug("found " + newid + " (" + result + ")");
        return true;
    }

    /**
     * Searches a key in request, postparameters, session, parent context and registers it in this one.
     */

    public boolean findAndRegister(String externid, String newid) throws JspTagException {
        log.debug("finding externid " + externid + " with context " + getId());
        // if (findAndRegister(TYPE_PAGE, referid, id)) return true;
        log.debug("in parent");
        if (findAndRegister(TYPE_PARENT, externid, newid)) return true;
        unRegister(newid); // unregister the 'null' value
        log.debug("in parameters");
        if (findAndRegister(TYPE_PARAMETERS, externid, newid)) return true;
        unRegister(newid);
        log.debug("in postparameters");
        if (findAndRegister(TYPE_POSTPARAMETERS, externid, newid)) return true;
        unRegister(newid);
        log.debug("in session");
        if (findAndRegister(TYPE_SESSION, externid, newid)) return true;
        // don't unregister now, it stays registered as a null value,t hat is registerd, but not found.
        return false;
    }


    /**
     * Register an Object with a key in the context. If the Context is
     * a session context, then it will be put in the session, otherwise in the hashmap.
     */

    public void register(String newid, Object n) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.trace("registering " + n + " under " + newid + " with context " + getId());
        }
        //pageContext.setAttribute(id, n);
        if (isRegistered(newid)) {
            throw new JspTagException("Object with id " + newid + " was already registered in Context '" + getId() + "'");
        }
        getHashMap().put(newid, n);
    }

    public void unRegister(String key) {
        //pageContext.removeAttribute(key);
        log.debug("removing object " + key + " from Context " + getId());
        getHashMap().remove(key);
    }

    /*
    public Cloud getCloudVar() throws JspTagException {
        return getCloudProviderVar();
    }
    */

    public boolean isPresent(String key) throws JspTagException {
        // return (pageContext.getAttribute(id) != null);
        return (getObject(key) != null);
    }

    final private HashMap getHashMap() {
        String key = "CONTEXT:" + getId();
        log.debug("using HashMap " + key);
        if (pageContext.getAttribute(key) == null) {
            pageContext.setAttribute(key, new HashMap());
        }
        return (HashMap) pageContext.getAttribute(key);
    }

    private boolean isRegistered(String key) {
        return (getHashMap().containsKey(key));
    }

    public Object getObject(String key) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.trace("getting object " + key + " from Context " + getId());
        }
        //Object result = pageContext.getAttribute(id);
        //if (result == null) {

        if (! isRegistered(key)) {
            throw new JspTagException("Object with id " + key + " was not registered in Context '" + getId() + "'");
        }
        Object result = getHashMap().get(key);
        log.debug("found object " + key + " in context " + getId() + " value: " + result);
        return result;
    }

    public String getStringFindAndRegister(String id) throws JspTagException {
        boolean found = findAndRegister(id, id);
        if (! found) {
            throw new JspTagException("No object with id " + id + " could be found in Context '" + getId() + "'");
        }
        return getObjectAsString(id);
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
        try {
            return getPoster().getPostParameterBytes(key);
        } catch (org.mmbase.util.PostValueToLargeException e) {
            throw new JspTagException("Post value to large (" + e.toString() + ")");
        }
    }

    public Node getNode(String key) throws JspTagException {
        return (Node) getObject(key);
    }

    public Vector getKeys() {
        Vector result = new Vector();
        Enumeration e = pageContext.getAttributeNamesInScope(pageContext.PAGE_SCOPE);
        while (e.hasMoreElements()) {
            result.add(e.nextElement());
        }
        return result;
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


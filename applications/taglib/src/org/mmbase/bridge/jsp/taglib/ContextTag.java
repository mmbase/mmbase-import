/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;
import java.io.IOException;
import java.io.*;

import java.util.HashMap;
import java.util.Vector;
import java.util.Iterator;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

//import org.mmbase.util.HttpPost;
//import com.oreilly.servlet.MultipartRequest;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
*
* Groups tags. The tags can referrence each other, by use of the group
* hash of this class. You can have several types, but they have a
* certain order. The most complex Context is a Context of type
* 'session'. If you try to get something from a session context, it
* will first search in the session, if it cannot find it there, it
* will try to find a multipart post with that key, if that is also not
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
    public static final int TYPE_MULTIPART      = 20;
    public static final int TYPE_SESSION        = 30;

    private static Logger log = Logging.getLoggerInstance(ContextTag.class.getName());

    private HashMap myHashMap = null; 

    private ContextTag parent = null;
    private boolean    searchedParent = false;

    //private HttpPost           poster = null;
    private MMultipartRequest    multipartRequest = null;
    private boolean             multipartChecked;
    private HttpServletRequest  httpRequest      = null;
    private HttpSession         httpSession      = null;

    public void release() {  
        // release is not called in Orion 1.5.2!!
        log.debug("releasing");
        //myHashMap = null;
        //poster = null;
        //cleanVars();
        super.release();
    }


    void fillVars() {
        ContextTag page = (ContextTag) pageContext.getAttribute("__context");
        if (page == null) { // first time on page
			// we want to be sure that these are refilled:
			httpSession = null;
			httpRequest = null;
			multipartChecked = false;
			// and also the multipart request must be filled if appropriate:
            if (isMultipart()) {
                getMultipartRequest();
            } else {
                multipartRequest = null;
                multipartChecked = true;
                
            }
			// 
            getSession();        
        } else {
            multipartRequest = page.multipartRequest;
            multipartChecked = true;
            httpRequest      = page.httpRequest;
            httpSession      = page.httpSession;
        }
    }

    public void setPageContext(PageContext pc) {
        super.setPageContext(pc);
        log.debug("setting page context");
        fillVars();
    }

    public int doStartTag() throws JspTagException {
        log.debug("Start tag of ContextTag");
        // release() is not called in Orion 1.5.2 (a bug!)  therefore we
        // must set some thing to null here.  I'm not sure by the way
        // if one can assume to have a new instance for every
        // page. This is however the case in e.g. orion 1.4.5 and
        // other servers.  In that case setting private members to
        // null here or in release is not necessary at all.

        parent = null;
        searchedParent = false;
        pageContext.removeAttribute(getId()); // make sure it does not exist.
        pageContext.setAttribute(getId(), new HashMap());
        myHashMap = (HashMap) pageContext.getAttribute(getId());
        if (myHashMap == null) {
            throw new JspTagException("hashmap is null");
        }
        log.debug("out");
        return EVAL_BODY_TAG;
    }

    // avoid casting
    private HttpServletRequest getHttpRequest() {
        if (httpRequest == null) {
            httpRequest = (HttpServletRequest)pageContext.getRequest();
            if (log.isDebugEnabled()) {
                Enumeration e = httpRequest.getParameterNames();
                String params = "";
                while (e.hasMoreElements()) {
                    params += e.nextElement() + ",";
                }
                log.debug("http parameters: " + params);
            }
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
        super.setId(i);
    }

    /**
     * Returns the id of the context. If there was no id-attibute in
     * this tag, then the id is implicitily 'context'.
     */

    public String getId() {
        if (id == null) return "context"; // a context has a default id.
        return id;
    }

    /*
    private HttpPost getPoster() {
        if (poster == null) {
            log.debug("Creating new  poster");
            poster = new HttpPost(getHttpRequest());
        }
        return poster;
    }
    */

    boolean isMultipart() {
        String ct = getHttpRequest().getContentType();
        if (ct == null) return false;
        return (ct.indexOf("multipart/form-data") != -1);
    }

    private MMultipartRequest getMultipartRequest() {
        if (multipartChecked) {
            return multipartRequest;
        } else {
            log.debug("Creating new MultipartRequest");
            multipartRequest = new MMultipartRequest(getHttpRequest());         
            multipartChecked = true;

            if (log.isDebugEnabled()) {
                if (multipartRequest != null) {
                    Enumeration e = multipartRequest.getParameterNames();
                    String params = "";
                    while (e.hasMoreElements()) {
                        params += e.nextElement() + ",";
                    }
                    log.debug("multipart parameters: " + params);
                } else {
                    log.debug("not a multipart request");
                }
            }
            return multipartRequest;          
        }

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
        // if it cannot be found, then 'null' will be put in the hashmap ('not present')

        switch (from) {
        case TYPE_SESSION:
            result = getSession().getAttribute(referid);
            break;
        case TYPE_MULTIPART: 
            if (isMultipart()) {
                log.debug("searching " + referid + " in multipart post");
                result = getMultipartRequest().getParameterValues(referid);
            } else {
                throw new JspTagException("Trying to read from multipart post, while request was not a multipart post");
            }
            break;
        case TYPE_PARAMETERS: {
            log.debug("searching parameter " + referid);
            Object[] resultvec = getHttpRequest().getParameterValues(referid);
            if (resultvec != null) {
                if (resultvec.length > 1) {
                    Vector rresult = new Vector(resultvec.length);
                    for (int i=0; i < resultvec.length; i++) {
                        rresult.add(resultvec[i]);         
                    }
                    result  = rresult;
                } else {
                    result = (String) resultvec[0];
                }
            }
        }
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
        log.debug("searching to register object " + externid + " in context " + getId());
        // if (findAndRegister(TYPE_PAGE, referid, id)) return true;
        log.debug("searching in parent");
        if (findAndRegister(TYPE_PARENT, externid, newid)) return true;
        unRegister(newid); // unregister the 'null' value
        log.debug("searching in parameters");
        if (findAndRegister(TYPE_PARAMETERS, externid, newid)) return true;
        unRegister(newid);
        if (isMultipart()) {
            log.debug("searching in multipart post");
            if (findAndRegister(TYPE_MULTIPART, externid, newid)) return true;
            unRegister(newid);
        }
        log.debug("searching in session");
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
            String mes = "Object with id " + newid + " was already registered in Context '" + getId() + "'";
            log.error(mes);
            throw new JspTagException(mes);
        }
        getHashMap().put(newid, n);
    }

    public void unRegister(String key) throws JspTagException {
        //pageContext.removeAttribute(key);
        log.debug("removing object " + key + " from Context " + getId());
        getHashMap().remove(key);
    }

    /*
    public Cloud getCloudVar() throws JspTagException {
        return getCloudProviderVar();
    }
    */

    /**
     * 'present' means ='not null'. 'null' means 'registered, but not present'.
	 * not registered is not present, of course.
     */

    public boolean isPresent(String key) throws JspTagException {
        // return (pageContext.getAttribute(id) != null);
		if (! isRegistered(key)) {
			log.warn("Checking presence of unregistered context variable " + key + " in context " + getId());
			return false;
		}
        return (getObject(key) != null);
    }


    final private HashMap getHashMap() throws JspTagException {
        //return myHashMap;
/*        String key = "CONTEXT:" + getId();
        log.debug("using HashMap " + key);
        if (pageContext.getAttribute(key) == null) {
            pageContext.setAttribute(key, new HashMap());
        }
*/
        HashMap hm = (HashMap) pageContext.getAttribute(getId());
        if (hm == null) {
            throw new JspTagException("hashmap is null");
        }
        return hm;
    }

    private boolean isRegistered(String key) throws JspTagException {
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
        return getMultipartRequest().getBytes(key);

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
        log.debug("after body of context " + getId());
        
        try {
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
            return SKIP_BODY;
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }
    }

}

// following class can be reimplemented when using other MultiPart post parser.


// oreilly implmenetation:
/* Doesn't seem to work (yet)
class MMultipartRequest {

    private static Logger log = Logging.getLoggerInstance(ContextTag.class.getName());
    private MultipartRequest o;

    MMultipartRequest(HttpServletRequest req) {
        try {
            o = new MultipartRequest(req, System.getProperty("java.io.tmpdir"));
        } catch (IOException e) {
            log.warn("" + e);    
        }
    };

    public byte[] getBytes(String param) throws JspTagException {
        try {
            File f = o.getFile(param);
            FileInputStream fs = new FileInputStream(f);

			// read the file to a byte[]. 
			// little cumbersome, but well...
			// perhaps it would be littler so if we use MultipartParser
			// but this is simpler, because oreilly..MultipartRequest is like a request.

            byte[] buf = new byte[1000];
            Vector bufs = new Vector();
            int size = 0;
			int grow;
			while ((grow = fs.read(buf)) > 0) {
                size += grow;
                bufs.add(buf);
                buf = new byte[1000];
            }    
			log.debug("size of image " + size);
            byte[] bytes = new byte[size];
            // copy the damn thing... 
			Iterator i = bufs.iterator();
			int curpos = 0;
			while (i.hasNext()) {
				byte[] tmp = (byte []) i.next();
				System.arraycopy(tmp, 0, bytes, curpos, tmp.length);
				curpos += tmp.length;
			}
			log.debug("size of image " + curpos);
            return bytes;                 
        }
        catch (FileNotFoundException e) {
            throw new JspTagException(e.toString());
        }
        catch (IOException e) {
            throw new JspTagException(e.toString());
        }

            //} catch (org.mmbase.util.PostValueToLargeException e) {
            //throw new JspTagException("Post value to large (" + e.toString() + ")");
            //}
    };
    public Object getParameterValues(String param) {
        Object result = null;
        Object[] resultvec = o.getParameterValues(param);
        if (resultvec != null) {
            if (resultvec.length > 1) {
                Vector rresult = new Vector(resultvec.length);
                for (int i=0; i < resultvec.length; i++) {
                    rresult.add(resultvec[i]);         
                }
                result  = rresult;
            } else {
                result = (String) resultvec[0];
            }
        }
        return result;
    };
    public Enumeration getParameterNames() {
        return o.getParameterNames();
    }
}
*/
// org.mmbase.util.HttpPost implementation


class MultipartRequest {

    private static Logger log = Logging.getLoggerInstance(ContextTag.class.getName());
    private org.mmbase.util.HttpPost o;

    MultipartRequest(HttpServletRequest req) {
		log.debug("Creating HttpPost instance");
        o = new org.mmbase.util.HttpPost(req);
    };

    public byte[] getBytes(String param) throws JspTagException {
        try {
            return o.getPostParameterBytes(param);
        } catch (org.mmbase.util.PostValueToLargeException e) {
            throw new JspTagException("Post value to large (" + e.toString() + ")");
        }
    };
    public Object getParameterValues(String param) {
        Object result = null;
        if (o.checkPostMultiParameter(param)) {
            log.info("This is a multiparameter!");
            result = o.getPostMultiParameter(param);
        } else {                
            result = (String) o.getPostParameter(param);
            log.debug("found " + result);
        }
        return result;
    };

    public Enumeration getParameterNames() {
        return o.getPostParameters().keys();
    }
}


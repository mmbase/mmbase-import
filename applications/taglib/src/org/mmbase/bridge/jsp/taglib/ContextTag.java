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
 * This is a HashMap, but the keys can contain 'dots', in which case
 * there is searched for HashMaps in the HashMap.
 */

class ContextContainer extends HashMap {

    private static Logger log = Logging.getLoggerInstance(ContextContainer.class.getName());
    private String id;
    private ContextContainer parent;

    /**
     * Since a ContextContainer can contain other ContextContainer, it
     * has to know which ContextContainer contains this. And it also
     * has an id.
     */

    ContextContainer(String _id, ContextContainer _parent) {
        super();
        id = _id;        
        parent = _parent;
    }

    /**
     * Keys must be Strings, so put(Object, ..) is forbidden in this HashMap!
     *
     */
       
    public Object put(Object key, Object value) {
        throw new RuntimeException("Error, key should be string in ContextContainers!");
    }
    /**
     * Not all Strings can be allowed as keys. Keys are like variable names. 
     *
     */

    public Object put(String key, Object value) throws JspTagException {
        if (key.indexOf('.') != -1) {
            throw new JspTagException("Key may not contain dots");
        }
        return super.put(key, value);
    }
    public boolean containsKey(Object key) {
        throw new RuntimeException("Error, key should be string in ContextContainers!");
    }

    /**
     * This function takes a key, which can contain dots. It returns
     * a structure with a new ContextContainer and the rest of the
     * key. It also returns if it had to 'go down' to find this,
     * because if it has done this, the next time you may not go up
     * again (check the parent).
     *
     */

    private Pair getPair(String key, boolean checkParent) throws JspTagException {
        // checking if key contains a 'dot'.
        int dotpos = key.indexOf('.');
        if (dotpos > 0) {  
            String contextKey = key.substring(0, dotpos);
            // find the Context:
            boolean wentDown = true;
            Object c = simpleGet(contextKey, checkParent);
            if(c == null && checkParent && parent != null) {
                c =  parent.get(key, true); 
                wentDown = false;
            } 
            
            if(c == null) {
                throw new JspTagException("Context '" + contextKey+ "' could not be found.");
            }
            if (! (c instanceof ContextContainer)) {
                throw new JspTagException(contextKey + " is not a Context, but a " + c.getClass().getName());
            }
            String newKey = key.substring(dotpos + 1);
            // and search with that one:
            return new Pair((ContextContainer)c, newKey, wentDown);
        } else {
            return null;
        }

    }
    /**
     * Like containsKey but doesn't check for dots.
     */
    private boolean simpleContainsKey(String key, boolean checkParent) {
        boolean result = super.containsKey(key);
        if (result == false && checkParent && parent != null) {
            result = parent.simpleContainsKey(key, true);
        }
        return result;
    }

    /**
     * Like the containsKey of HashMap.
     * 
     * @param key          The key to search
     * @param checkParent  If this is false, it will only look in the current Container (and below).
     */

    boolean containsKey(String key, boolean checkParent) throws JspTagException {
        Pair p = getPair(key, checkParent);
        if (p == null) {
            return simpleContainsKey(key, checkParent);
        } else {
            return p.context.containsKey(p.restKey, ! p.wentDown);
        }                
    }
    public boolean containsKey(String key) throws JspTagException {
        return containsKey(key, true);
    }


    /**
     * Like get, but does not try to search dots, because you know already that there aren't.
     */
    private Object simpleGet(String key, boolean checkParent) { // already sure that there is no dot. 
        Object result =  super.get(key);
        if (result == null && checkParent && parent != null) {
            return parent.simpleGet(key, true);
        } 
        return result;
    }

    /**
     * Like get, but you can explicity indicate if to search 'parent' Contextes as well
     */

    public Object get(String key, boolean checkParent) throws JspTagException {
        Pair p = getPair(key, checkParent);
        if (p == null) {
            return simpleGet(key, checkParent);
        } else {     
            return p.context.get(p.restKey, ! p.wentDown);
        }
    }

    public Object get(String key) throws JspTagException {
        return get(key, true);
    }

    /**
     * Container class, to store results of 'getPair' function.
     *
     */

    private class Pair {
        ContextContainer context;
        String           restKey;
        boolean          wentDown;
        Pair(ContextContainer c, String k, boolean w) {
            context = c; restKey = k; wentDown = w;
        }
    }

}


/**
 * <p>
 * A ContextTag is like parentheses, and as such can act as 
 * a 'namespace' (if it has an id) or as 'scope' (if it doesn't). 
 * </p> 
 * <p>
 * The context can be seen as a container for variables and their values.
 * </p><p>
 * (ContextReferrer) Tags living under a context tag can 'register'
 * themselves in the context (by use of the 'id' attribute') and in
 * that way become a variable. Other tags can refer to such variables.
 * </p><p>
 * A ContextTag is a ContextReferrer itself too, and therefore it is
 * possible to 'nest' contextes. And perhaps we will also make it
 * possible to treat contextes as variables and e.g. pass them to
 * another page as a whole.
 * </p><p>
 * It is also possible to put something into the Context by hand. For
 * that you can use the `ImportTag'.
 * </p><p>
 * Writing out the value of a variable can be done with the `Write' Tag.
 * </p>
 *
 * @author Michiel Meeuwissen 
 * @see    ImportTag
 * @see    WriteTag
 */

public class ContextTag extends ContextReferrerTag {

    public static final int TYPE_NOTSET         = -10;
    public static final int TYPE_PAGE           = 0;
    public static final int TYPE_PARENT         = 5; // uses parent Contex, if there is one.
    public static final int TYPE_PARAMETERS     = 10;
    public static final int TYPE_MULTIPART      = 20;
    public static final int TYPE_SESSION        = 30;

    private static Logger log = Logging.getLoggerInstance(ContextTag.class.getName());

    private ContextContainer container = null; 

    private ContextTag parent = null;
    private boolean    searchedParent = false;

    //private HttpPost           poster = null;
    private MMultipartRequest    multipartRequest = null;
    private boolean              multipartChecked;
    private HttpServletRequest   httpRequest      = null;
    private HttpSession          httpSession      = null;

    public void release() {  
        // release is not called in Orion 1.5.2!!
        log.debug("releasing");
        //myHashMap = null;
        //poster = null;
        //cleanVars();
        super.release();
    }

    /** 
     * Fills the private member variables for general use. They are
     * gotten from the pageContexTag if this does exist already.
     * 
     */

    void fillVars() {
        ContextTag page = (ContextTag) pageContext.getAttribute("__context");
        if (page == null) { // first time on page..
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

    void createContainer(ContextContainer c) {
        container = new ContextContainer(getId(), c);
    }

    ContextContainer getContainer() {
        return container;        
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
        createContainer(getContextTag().getContainer());
        if (getId() != null) {
            if (log.isDebugEnabled()) {
                log.debug("registering container " + getId() + " with context " + getContextTag().getId());
            }
            getContextTag().register(getId(), container);
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

    private ContextTag getParentContext() throws JspTagException {
        if (! searchedParent) {
            try {
                parent = getContextTag();
            } catch (JspTagException e) {
                throw new JspTagException("Could not find parent context!");
            }
            searchedParent = true;
        }
        return parent;
    }


    /**
     * Precisely like 'register', only it wants a Node.
     *
     * @param key the key (id) of the node to register
     * @param node the node to put in the hashmap
     */

    public void  registerNode(String key, Node n) throws JspTagException {
        register(key, n);
    }


    //

    public Object findAndRegister(int from, String referid, String newid) throws JspTagException {
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
                    result = parent.container.get(referid);
                    if (result == this.container) { // don't find this tag itself...
                        result = null;
                    }
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
        if (log.isDebugEnabled()) {
            log.debug("found " + newid + " (" + result + ")");
        }
        return result;
    }

    /**
     * Searches a key in request, postparameters, session, parent
     * context and registers it in this one.
     *
     * Returns null if it could not be found.  
     */

    public Object findAndRegister(String externid, String newid) throws JspTagException {
        log.debug("searching to register object " + externid + " in context " + getId());
        // if (findAndRegister(TYPE_PAGE, referid, id)) return true;
        log.debug("searching in parent");
        Object result; 
        result = findAndRegister(TYPE_PARENT, externid, newid);
        if (result != null) return result;
        unRegister(newid); // unregister the 'null' value
        log.debug("searching in parameters");
        result = findAndRegister(TYPE_PARAMETERS, externid, newid);
        if (result != null) return result;
        unRegister(newid);
        if (isMultipart()) {
            log.debug("searching in multipart post");
            result = findAndRegister(TYPE_MULTIPART, externid, newid);
            if (result != null) return result;
            unRegister(newid);
        }
        log.debug("searching in session");
        result = findAndRegister(TYPE_SESSION, externid, newid);
        // don't unregister now, it stays registered as a null value,t hat is registerd, but not found.
        return result;
    }


    /**
     * Register an Object with a key in the context. If the Context is
     * a session context, then it will be put in the session, otherwise in the hashmap.
     */

    public void register(String newid, Object n) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.trace("registering " + n + " a (" + (n!=null ? n.getClass().getName() :"")+ ") under " + newid + " with context " + getId());
        }
        //pageContext.setAttribute(id, n);
        if (isRegistered(newid)) {
            String mes = "Object with id " + newid + " was already registered in Context '" + getId() + "'";
            log.error(mes);
            throw new JspTagException(mes);
        }
        container.put(newid, n);
    }

    public void unRegister(String key) throws JspTagException {
        //pageContext.removeAttribute(key);
        log.debug("removing object " + key + " from Context " + getId());
        container.remove(key);
    }


    /**
     * 'present' means 'not null'. 'null' means 'registered, but not present'.
     *  Not registered is not present, of course.
     */

    public boolean isPresent(String key) throws JspTagException {
        //if (! isRegisteredSomewhere(key)) {
        //    log.warn("Checking presence of unregistered context variable " + key + " in context " + getId());
        //    return false;
        //}
        return (container.get(key) != null);
    }
    

    private boolean isRegistered(String key) throws JspTagException {
        return (container.containsKey(key, false)); // don't check parent.
    }
    private boolean isRegisteredSomewhere(String key) throws JspTagException {
        return (container.containsKey(key, true)); // do check parent.
    }
    
    public Object findAndRegister(String id) throws JspTagException {
        return findAndRegister(id, id);
    }
    public String findAndRegisterString(String id) throws JspTagException {
        return (String) findAndRegister(id, id);
    }

    public Object getContainerObject(String key) throws JspTagException {
        if (! isRegisteredSomewhere(key)) {
            throw new JspTagException("Object '" + key + "' is not registered");            
        }
        return container.get(key);
    }

    /**
     * hmm.. This kind of stuf must move to ImportTag, I think.
     */
    
    public byte[] getBytes(String key) throws JspTagException {
        return getMultipartRequest().getBytes(key);

    }

    /*
    public Vector getKeys() {
        Vector result = new Vector();
        Enumeration e = pageContext.getAttributeNamesInScope(pageContext.PAGE_SCOPE);
        while (e.hasMoreElements()) {
            result.add(e.nextElement());
        }
        return result;
    }
    */

    public int doAfterBody() throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("after body of context " + getId());
        }
        
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


class MMultipartRequest {

    private static Logger log = Logging.getLoggerInstance(ContextTag.class.getName());
    private org.mmbase.util.HttpPost o;

    MMultipartRequest(HttpServletRequest req) {
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


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
import java.util.Set;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Node;

//import org.mmbase.util.HttpPost;
//import com.oreilly.servlet.MultipartRequest;

import org.mmbase.bridge.jsp.taglib.util.ContextContainer;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;



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

    public static final int LOCATION_NOTSET         = -10;
    public static final int LOCATION_PAGE           = 0;
    public static final int LOCATION_PARENT         = 5; // uses parent Contex, if there is one.
    public static final int LOCATION_PARAMETERS     = 10;
    public static final int LOCATION_MULTIPART      = 20;
    public static final int LOCATION_SESSION        = 30;
    public static final int LOCATION_COOKIE         = 40;

    private static Logger log = Logging.getLoggerInstance(ContextTag.class.getName());


    public static int stringToLocation(String s) throws JspTagException {
        int location;
        if ("parent".equalsIgnoreCase(s)) {
            location = ContextTag.LOCATION_PARENT;
        } else if ("page".equalsIgnoreCase(s)) {
            location = ContextTag.LOCATION_PAGE;
        } else if ("session".equalsIgnoreCase(s)) {
            location = ContextTag.LOCATION_SESSION;
        } else if ("parameters".equalsIgnoreCase(s)) {
            location = ContextTag.LOCATION_PARAMETERS;
        } else if ("parameter".equalsIgnoreCase(s)) {
            location = ContextTag.LOCATION_PARAMETERS;
        } else if ("postparameters".equalsIgnoreCase(s)) { // backward compatible
            location = ContextTag.LOCATION_MULTIPART;
        } else if ("multipart".equalsIgnoreCase(s)) {
            location = ContextTag.LOCATION_MULTIPART;
        } else if ("cookie".equalsIgnoreCase(s)) {
            location = ContextTag.LOCATION_COOKIE;
        } else {
            throw new JspTagException("Unknown context-type " + s);
        }
        return location;
    }
    public static String locationToString(int i) {
        switch(i) {
        case LOCATION_PARENT:      return "parent";
        case LOCATION_PARAMETERS:  return "parameters";
        case LOCATION_SESSION:     return "session";
        case LOCATION_PAGE:        return "page";
        default:                   return "<>";
        }
    }


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


    public Object findAndRegister(int from, String referid, String newid) throws JspTagException {
        return findAndRegister(from, referid, newid, true);
    }

    protected Object findAndRegister(int from, String referid, String newid, boolean check) throws JspTagException {
        if (newid == null) {
            throw new JspTagException("Cannot register with id is null");
        }
        if (referid == null) {
            throw new JspTagException("Cannot refer with id is null");
        }
        Object result = null;
        // if it cannot be found, then 'null' will be put in the hashmap ('not present')

        switch (from) {
        case LOCATION_COOKIE:
            javax.servlet.http.Cookie[] cookies = getHttpRequest().getCookies();
            for (int i=0; i< cookies.length; i++) {
                if (cookies[i].getName().equals(referid)) {
                    // simply return the first value found.
                    // this is probably a little to simple...
                    // since a cookie can e.g. also have another path.
                    result = cookies[i].getValue();
                    // touch cookie
                    cookies[i].setMaxAge(WriteTag.MAX_COOKIE_AGE);
                    ((HttpServletResponse) (pageContext.getResponse())).addCookie(cookies[i]);
                    break;
                }
            }
            break;
        case LOCATION_SESSION:
            if (getSession() == null) {
                throw new JspTagException("Cannot use session if session is disabled");
            }
            result = getSession().getAttribute(referid);
            break;
        case LOCATION_MULTIPART: 
            if (isMultipart()) {
                if (log.isDebugEnabled()) {
                    log.debug("searching " + referid + " in multipart post");
                }
                result = getMultipartRequest().getParameterValues(referid);
            } else {
                throw new JspTagException("Trying to read from multipart post, while request was not a multipart post");
            }
            break;
        case LOCATION_PARAMETERS: {
            if (log.isDebugEnabled()) {
                log.debug("searching parameter " + referid);
            }
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
        case LOCATION_PARENT:
            if (getParentContext() != null) {
                if (parent.isRegistered(referid)) {
                    result = parent.container.get(referid);
                    if (result == this.container) { // don't find this tag itself...
                        result = null;
                    }
                }
            }
            break;
        case LOCATION_PAGE:
            //result = pageContext.getAttribute(referid);
            break;
        default:
            result = null;
        }
        register(newid, result, check);
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
        if (log.isDebugEnabled()) {
            log.debug("searching to register object " + externid + " in context " + getId());
        }
        if (isRegistered(newid)) {
            String mes = "Object with id " + newid + " was already registered in Context '" + getId() + "'";
            log.error(mes);
            throw new JspTagException(mes);
        }
        // if (findAndRegister(LOCATION_PAGE, referid, id)) return true;
        log.debug("searching in parent");
        Object result; 
        result = findAndRegister(LOCATION_PARENT, externid, newid, false); // don't check, we have checked already.
        if (result != null) return result;
        log.debug("searching in parameters");
        result = findAndRegister(LOCATION_PARAMETERS, externid, newid, false);
        if (result != null) return result;
        if (isMultipart()) {
            log.debug("searching in multipart post");
            result = findAndRegister(LOCATION_MULTIPART, externid, newid, false);
            if (result != null) return result;
        }
        log.debug("searching in session");
        result = findAndRegister(LOCATION_SESSION, externid, newid, false);
        return result;
    }



    private static boolean isContextVarNameChar(char c) {
        return Character.isLetter(c) || Character.isDigit(c) || c == '_';
    }
    public static boolean isContextIdentifierChar(char c) {
        return isContextVarNameChar(c) || c == '.' || c =='/'; // / for forward compatibility? 
    }

    /**
     * Register an Object with a key in the context. If the Context is
     * a session context, then it will be put in the session, otherwise in the hashmap.
     */

    protected void register(String newid, Object n, boolean check) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.trace("registering " + n + " a (" + (n!=null ? n.getClass().getName() :"")+ ") under " + newid + " with context " + getId());
        }        
        // Check if the id is a valid identifier
        // A valid id must begin with a letter or underscore, followed
        // by letters, underscores and digits.
        boolean valid = true;
        char chars[] = newid.toCharArray();
        if (chars.length < 1) {
            valid = false;
        } else {
            if (Character.isLetter(chars[0]) || chars[0] == '_') {
                for (int i = 1; i < chars.length; ++i) {
                    if (! isContextVarNameChar(chars[0])) {
                        valid = false;
                        break;
                    }
                }
            } else {
                valid = false;
            }
        }
        
        if (! valid) throw new JspTagException ("'" + newid + "' is not a valid Context identifier");

        //pageContext.setAttribute(id, n);
        if (check && isRegistered(newid)) { 
            String mes = "Object with id " + newid + " was already registered in Context '" + getId() + "'";
            log.error(mes);
            throw new JspTagException(mes);
        }


        container.put(newid, n);
    }

    public void register(String newid, Object n) throws JspTagException {
        register(newid, n, true);
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
            throw new JspTagException("Object '" + key + "' is not registered. Registered are " + container.keySet());
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


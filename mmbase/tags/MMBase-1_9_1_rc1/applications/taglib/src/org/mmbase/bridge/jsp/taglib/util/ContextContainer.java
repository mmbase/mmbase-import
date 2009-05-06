/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.util;

import org.mmbase.bridge.jsp.taglib.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.http.*;

import java.util.*;
import java.lang.reflect.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This is a HashMap, but the keys can contain 'dots', in which case
 * there is searched for HashMaps in the HashMap.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ContextContainer.java,v 1.67 2009-04-24 19:58:44 michiel Exp $
 **/

public abstract class ContextContainer extends AbstractMap<String, Object> implements Map<String, Object> {
    private static final Logger log = Logging.getLoggerInstance(ContextContainer.class);

    public static final int LOCATION_NOTSET         = -10;
    public static final int LOCATION_PAGE           = 0;
    public static final int LOCATION_PARENT         = 5; // uses parent Contex, if there is one.
    public static final int LOCATION_PARAMETERS     = 10;
    public static final int LOCATION_MULTIPART      = 20;
    public static final int LOCATION_MULTIPART_OPT  = 21;
    public static final int LOCATION_SESSION        = 30;
    public static final int LOCATION_COOKIE         = 40;
    public static final int LOCATION_ATTRIBUTES     = 50;
    public static final int LOCATION_REQUEST        = 50;
    public static final int LOCATION_APPLICATION    = 55;
    public static final int LOCATION_THIS           = 60; // current value, if there is one


    public static int stringToLocation(String s) throws JspTagException {
        int location;
        s = s.toLowerCase();
        if ("parent".equals(s)) {
            location = LOCATION_PARENT;
        } else if ("page".equals(s)) {
            location = LOCATION_PAGE;
        } else if ("session".equals(s)) {
            location = LOCATION_SESSION;
        } else if ("parameters".equals(s)) {
            location = LOCATION_PARAMETERS;
        } else if ("parameter".equals(s)) {
            location = LOCATION_PARAMETERS;
        } else if ("postparameters".equals(s)) { // backward compatible
            location = LOCATION_MULTIPART;
        } else if ("multipart".equals(s)) {
            location = LOCATION_MULTIPART;
        } else if ("multipart?".equals(s)) {
            location = LOCATION_MULTIPART_OPT;
        } else if ("cookie".equals(s)) {
            location = LOCATION_COOKIE;
        } else if ("attributes".equals(s)) {
            location = LOCATION_REQUEST;
        } else if ("request".equals(s)) {
            location = LOCATION_REQUEST;
        } else if ("application".equals(s)) {
            location = LOCATION_APPLICATION;
        } else if ("this".equals(s)) {
            location = LOCATION_THIS;
        } else {
            throw new JspTagException("Unknown location '" + s + "'");
        }
        return location;
    }

    public static String locationToString(int i) {
        switch(i) {
        case LOCATION_PARENT:      return "parent";
        case LOCATION_PARAMETERS:  return "parameters";
        case LOCATION_SESSION:     return "session";
        case LOCATION_PAGE:        return "page";
        case LOCATION_MULTIPART:   return "multipart";
        case LOCATION_MULTIPART_OPT:   return "multipart?";
        case LOCATION_COOKIE:      return "cookie";
        case LOCATION_REQUEST:     return "request";
        case LOCATION_APPLICATION: return "application";
        case LOCATION_THIS:        return "this";
        default:                   return "<>";
        }
    }



    private static boolean isContextVarNameChar(char c) {
        return Character.isLetter(c) || Character.isDigit(c) || c == '_';
    }
    static boolean isContextIdentifierChar(char c) {
        return isContextVarNameChar(c) || c == '.';
        // || c =='/'; // / for forward compatibility?
    }


    /**
     * Returns the Map which will is used for actually storing stuff.
     *
     * @since MMBase-1.8
     */
    public abstract Backing getBacking();


    public void release(PageContext pc, ContextContainer p) {
        getBacking().pullPageContext(pc);
        // restore also the parent.
        parent = p;
    }


    @Override
    public Set<Entry<String, Object>> entrySet() {
        return getBacking().entrySet();
    }


    private   final String id;
    protected ContextContainer parent;


    /**
     * Since a ContextContainer can contain other ContextContainer, it
     * has to know which ContextContainer contains this. And it also
     * has an id.
     */

    public ContextContainer(String i, ContextContainer p) {
        id = i;
        parent = p;
    }

    public String getId() {
        return id;
    }

    /**
     * @since MMBase-1.8
     */
    public void setParent(PageContext pc, ContextContainer p) {
        if (log.isDebugEnabled()) {
            log.debug("Setting parent of " + getClass() + " "  + this + " to " + pc + " backing " + getBacking().getClass());
        }
        getBacking().pushPageContext(pc);
        parent = p;
    }

    /**
     * @since MMBase-1.7
     */
    public ContextContainer getParent() {
        return parent;
    }

    /**
     * @since MMBase-1.8.3
     */
    public PageContext getPageContext() {
        return getBacking().getPageContext();
    }

    /**
     * Not all Strings can be allowed as keys. Keys are like variable names.
     */
    @Override
    public Object put(String key, Object value) {
        if (key.indexOf('.') != -1) {
            throw new RuntimeException("Key may not contain dots (" + key + ")");
        }
        return getBacking().put(key, value);
    }

    /**
     * This function takes a key, which can contain dots. It returns
     * a structure with a new ContextContainer and the rest of the
     * key. It also returns if it had to 'go down' to find this,
     * because if it has done this, the next time you may not go up
     * again (check the parent).
     *
     */

    protected Pair getPair(String key, boolean checkParent) throws JspTagException {
        // checking if key contains a 'dot'.
        int dotPos = -1;
        {
            int pos = 0;
            int keyLength = key.length();
            while (pos < keyLength) {
                char c = key.charAt(pos);
                if (c == '.') {
                    dotPos = pos;
                    break;
                }
                if (! isContextVarNameChar(c)) return null;
                pos++;
            }
        }
        if (dotPos > 0) {
            String contextKey = key.substring(0, dotPos);
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
            String newKey = key.substring(dotPos + 1);
            // and search with that one:
            if (c instanceof ContextContainer) {
                return new ContextContainerPair((ContextContainer)c, newKey, wentDown);
            } else if (c instanceof Map) {
                return new MapPair((Map<String, Object>)c, newKey, wentDown);
            } else {
                return new BeanPair(c, newKey, wentDown);
            }
        } else {
            return null;
        }

    }
    /**
     * Like containsKey but doesn't check for dots.
     */
    private boolean simpleContainsKey(String key, boolean checkParent) {
        if (getBacking().containsKey(key)) {
            return true;
        } else if (checkParent && parent != null) {
            if (log.isDebugEnabled()) {
                log.debug("Checking " + parent + " for " + key);
            }
            return parent.simpleContainsKey(key, true);
        } else {
            return false;
        }
    }

    /**
     * Like the containsKey of HashMap.
     *
     * @param key          The key to search
     * @param checkParent  If this is false, it will only look in the current Container (and below).
     */

    public boolean containsKey(String key, boolean checkParent) throws JspTagException {
        Pair p = getPair(key, checkParent);
        if (p == null) {
            return simpleContainsKey(key, checkParent);
        } else {
            return p.containsKey(p.restKey, ! p.wentDown);
        }
    }

    @Override
    public boolean containsKey(Object key) {
        try {
            return containsKey((String) key, true);
        } catch (Exception e) {
            log.error(e);
            return false;
        }
    }

    /**
     * Like get, but does not try to search dots, because you know already that there aren't.
     */
    protected Object simpleGet(String key, boolean checkParent) { // already sure that there is no dot.
        Object result =  getBacking().getOriginal(key);
        if (result == null && checkParent && parent != null) {
            return parent.simpleGet(key, true);
        }
        return result;
    }

    /**
     * Like get, but you can explicity indicate if to search 'parent' Contextes as well.
     */

    public Object get(String key, boolean checkParent) throws JspTagException {
        Pair p = getPair(key, checkParent);
        if (p == null) {
            return simpleGet(key, checkParent);
        } else {
            return p.get(p.restKey, ! p.wentDown);
        }
    }


    @Override
    public Object get(Object key) {
        try {
            return get((String) key, true);
        } catch (JspTagException e) {
            log.warn("Exception when trying to get value '" + key + "': " + e, e);
        }
        return null;
    }

    @Override
    public Set<String> keySet() {
        HashSet<String> result = new HashSet<String>(getBacking().keySet());
        if (parent != null) {
            result.addAll(parent.keySet());
        }
        return result;
    }

    /**
     * @since MMBase-1.7
     */
    Set<String> keySet(boolean checkParent) {
        if (checkParent) {
            return keySet();
        } else {
            return getBacking().keySet();
        }
    }

    // utilities, since MMBase-1.7 moved from ContextTag to here.

    /**
     * @since MMBase-1.7 (here)
     */
    public Object getObject(String key) throws JspTagException {
        if (! containsKey(key, true)) { // do check parent.
            throw new JspTagException("Object '" + key + "' is not registered. Registered are " + keySet());
        }
        if (log.isDebugEnabled()) {
            log.debug("Getting '" + key + "' from container " + this);
        }
        return get(key);
    }

    /**
     * @since MMBase-1.7 (here)
     */
    public void register(String newId, Object n, boolean check) throws JspTagException {
        register(newId, n, check, true);
    }

    /**
     * @since MMBase-1.8
     */
    public void register(String newId, WriterHelper helper, boolean check) throws JspTagException {
        register(newId, helper.getValue(), check, true);
    }

    /**
     * @since MMBase-1.7
     */
    protected void register(String newId, Object n, boolean check, boolean checkParent) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.trace("registering " + n + " a (" + (n != null ? n.getClass().getName() :"")+ ") under " + newId + " with context " + id);
        }
        // first check if current context is specified
        Pair pair = getPair(newId, checkParent);

        if (pair != null) {
            pair.register(pair.restKey, n, check, ! pair.wentDown);

        } else {

            // Check if the id is a valid identifier
            // A valid id must begin with a letter or underscore, followed
            // by letters, underscores and digits.
            boolean valid = true;
            char chars[] = newId.toCharArray();
            if (chars.length < 1) {
                log.debug("Id must be longer then 0");
                valid = false;
            } else {
                if (Character.isLetter(chars[0]) || chars[0] == '_') {
                    if (log.isDebugEnabled()) log.debug("First character is valid, checking the rest of " + newId);
                    for (int i = 1; i < chars.length; ++i) {
                        if (! isContextVarNameChar(chars[i])) {
                            valid = false;
                            break;
                        }
                    }
                } else {
                    if (log.isDebugEnabled()) log.debug("First character is not valid: " + chars[0]);
                    valid = false;
                }
            }
            if (! valid) {
                JspTagException exception = new TaglibException ("'" + newId + "' is not a valid Context identifier", new Throwable());
                throw exception;
            }

            log.debug("Valid");
            //pageContext.setAttribute(id, n);
            if ((! newId.equals("_")) && check && isRegistered(newId)) {
                JspTagException e = new JspTagException("Object with id " + newId + " was already registered in " + this);
                if (log.isDebugEnabled()) {
                    log.debug(Logging.stackTrace(e));
                }
                throw e;
            }
            if (log.isDebugEnabled()) {
                log.debug("putting '" + newId + "'/'" + n + "' in " + this);
            }
            put(newId, n);
        }
    }

    /**
     * @since MMBase-1.7 (here)
     */
    public void register(String newId, Object n) throws JspTagException {
        register(newId, n, true);
    }

    /**
     * @since MMBase-1.7
     */
    void registerAll(Map<String, Object> map) throws JspTagException {
        if (map == null) return;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            register(entry.getKey(), entry.getValue());
        }

    }
    /**
     * @since MMBase-1.7
     */
    void unRegisterAll(Set<String> set) throws JspTagException {
        if (set == null) return;
        Iterator<String> i = set.iterator();
        while (i.hasNext()) {
            String key = i.next();
            unRegister(key);
        }

    }
    /**
     * @since MMBase-1.7 (here)
     */
    public void registerNode(String newId,  org.mmbase.bridge.Node n) throws JspTagException {
        register(newId, n);
    }
    /**
     * @since MMBase-1.7 (here)
     */
    public boolean isRegistered(String key) throws JspTagException {
        return getBacking().containsOwnKey(key);
    }

    /**
     * @since MMBase-1.7 (here)
     */
    public void unRegister(String key) throws JspTagException {
        unRegister(key, true);
    }

    /**
     * @since MMBase-1.7
     */
    protected void unRegister(String key, boolean checkParent) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("removing object '" + key + "' from Context '" + id + "'");
        }
        Pair pair = getPair(key, checkParent);
        if (pair != null) {
            pair.unRegister(pair.restKey, ! pair.wentDown);
        } else {
            remove(key);
        }
    }
    /**
     * @since MMBase-1.7 (here)
    */
    public void reregister(String id, Object n) throws JspTagException {
        unRegister(id);
        register(id, n);
    }

    /**
     * Java Servlet Specification Version 2.3 SRV.4.9
     * says that a servlet engine should read a request
     * as ISO-8859-1 if request.getCharacterEncoding()
     * returns null. We override this behaviour because
     * the browser propably sends the request in the
     * same encoding as the html was send to te browser
     * And it is likely that the html was send to the
     * browser in the same encoding as the MMBase
     * encoding property.
     * @since MMBase-1.8
     */
    protected static Object fixEncoding(Object value, String encoding) throws TaglibException {
        if(value instanceof String) {
            try {
                value = new String(((String)value).getBytes("ISO-8859-1"), encoding);
            } catch (java.io.UnsupportedEncodingException e) {
                throw new TaglibException("Unsupported Encoding ", e);
            }
        } else if (value instanceof List) {
            ListIterator<Object> i = ((List<Object>)value).listIterator();
            while(i.hasNext()) {
                i.set(fixEncoding(i.next(), encoding));
            }
        }
        return value;

    }
    /**
     * @since MMBase-1.8
     */
    public static Object fixEncoding(Object value, PageContext pageContext) throws TaglibException {
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        String enc = req.getCharacterEncoding();
        if (enc == null) {
            enc = getDefaultCharacterEncoding(pageContext);
        } else {
            // I think this happens seldom, if ever.
            log.debug("form encoding specified: " + enc);
        }

        if (enc.equalsIgnoreCase("ISO-8859-1")) {
            enc = "CP1252";
        }
        log.debug("Fixing char encoding of " + value + " to " + enc);
        return fixEncoding(value, enc);
    }

    /**
     * @since MMBase-1.7
     */

    public Object find(PageContext pageContext, int from, String referId) throws JspTagException {
        Object result = null;
        switch (from) {
        case LOCATION_COOKIE:
            javax.servlet.http.Cookie[] cookies = ((HttpServletRequest) pageContext.getRequest()).getCookies();
            if (cookies == null) {
                log.debug("Cannot use cookies");
            } else {
                log.debug("Found cookies");
                for (Cookie element : cookies) {
                    if (log.isDebugEnabled()) {
                        log.debug(element.getName() + "/" + element.getValue());
                    }
                    if (element.getName().equals(referId)) {
                        // simply return the first value found.
                        // this is probably a little to simple...
                        // since a cookie can e.g. also have another path.
                        result = element.getValue();
                        break;
                    }
                }
            }
            break;
        case LOCATION_SESSION:
            if (((HttpServletRequest) pageContext.getRequest()).getSession(false) == null) {
                log.debug("Cannot use session if session is disabled");
            }  else {
                result = ((HttpServletRequest) pageContext.getRequest()).getSession(false).getAttribute(referId);
            }
            break;
        case LOCATION_MULTIPART_OPT:
        case LOCATION_MULTIPART:
            if (MultiPart.isMultipart(pageContext)) {
                if (log.isDebugEnabled()) {
                    log.debug("searching " + referId + " in multipart post");
                }
                MultiPart.MMultipartRequest mp = MultiPart.getMultipartRequest(pageContext);
                if (mp.isFile(referId)) {
                    result = mp.getInputStream(referId);
                } else {
                    try {
                        result = fixEncoding(mp.getParameterValues(referId), pageContext);
                    }  catch (java.io.UnsupportedEncodingException uee) {
                        throw new JspTagException(uee);
                    }
                }
                //result = MultiPart.getMultipartRequest(pageContext).getParameterValues(referId);
            } else {
                if (from == LOCATION_MULTIPART) {
                    throw new JspTagException("Trying to read from multipart post, while request was not a multipart post");
                }
                }
            break;
        case LOCATION_PARAMETERS: {
            if (log.isDebugEnabled()) {
                log.debug("searching parameter " + referId);
            }
            HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
            String[] resultvec = req.getParameterValues(referId);
            if (resultvec != null) {
                if (log.isDebugEnabled()) log.debug("Found: " + resultvec);
                if (resultvec.length > 1) {
                    result = fixEncoding(java.util.Arrays.asList(resultvec), pageContext);
                } else {
                    result = fixEncoding(resultvec[0], pageContext);
                }
            }
        }
        break;
        case LOCATION_PARENT:
            if (parent != null) {
                if (parent.containsKey(referId, true)) {
                    result = parent.get(referId);
                    if (result == this) { // don't find this tag itself...
                        result = null;
                    }
                }
            }
            break;
        case LOCATION_PAGE:
            result = pageContext.getAttribute(referId);
            break;
        case LOCATION_REQUEST:
            result = pageContext.getAttribute(referId, PageContext.REQUEST_SCOPE);
            break;
        case LOCATION_APPLICATION:
            result = pageContext.getAttribute(referId, PageContext.APPLICATION_SCOPE);
            break;
        case LOCATION_THIS:
            result = simpleGet(referId, false);
            break;
        default:
            result = null;
        }
        return result;

    }

    /**
     * @since MMBase-1.7
     */

    public Object find(PageContext pageContext, String externid) throws JspTagException {
        log.debug("searching in parent");
        Object result;
        result = find(pageContext, LOCATION_PARENT, externid);
        if (result != null) return result;
        log.debug("searching in attributes");
        result = find(pageContext, LOCATION_ATTRIBUTES, externid);
        if (result != null) return result;
        log.debug("searching in parameters");
        result = find(pageContext, LOCATION_PARAMETERS, externid);
        if (result != null) return result;
        if (MultiPart.isMultipart(pageContext)) {
            log.debug("searching in multipart post");
            result = find(pageContext, LOCATION_MULTIPART, externid);
            if (result != null) return result;
        }
        if (((HttpServletRequest) pageContext.getRequest()).getSession(false) != null) {
            log.debug("searching in session");
            result = find(pageContext, LOCATION_SESSION, externid);
        }
        return result;
    }

    /**
     * Searches a key in request, postparameters, session, parent
     * context and registers it in this one.
     *
     * Returns null if it could not be found.
     */
    public Object findAndRegister(PageContext pageContext, int from, String referId, String newId) throws JspTagException {
        return findAndRegister(pageContext, from, referId, newId, true);
    }

    public Object findAndRegister(PageContext pageContext, int from, String referId, String newId, boolean check) throws JspTagException {
        if (newId == null) {
            throw new JspTagException("Cannot register with id is null");
        }
        if (referId == null) {
            throw new JspTagException("Cannot refer with id is null");
        }
        Object result = find(pageContext, from, referId);
        // if it cannot be found, then 'null' will be put in the hashmap ('not present')

        register(newId, result, check);
        if (log.isDebugEnabled()) {
            log.debug("found " + newId + " (" + result + ")");
        }
        return result;
    }

    public Object findAndRegister(PageContext pageContext, String externid, String newId) throws JspTagException {
        return findAndRegister(pageContext, externid, newId, true);
    }
    public Object findAndRegister(PageContext pageContext, String externid, String newId, boolean check) throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("searching to register object " + externid + " in context " + getId() + " check: " + check);
        }
        if (check && isRegistered(newId)) {
            throw new JspTagException("Object with id " + newId + " was already registered in " + toString());
        }
        // if (findAndRegister(LOCATION_PAGE, referId, id)) return true;
        Object result = find(pageContext, externid);
        register(newId, result, check);
        return result;
    }

    public Object findAndRegister(PageContext pageContext, String id) throws JspTagException {
        return findAndRegister(pageContext, id, id);
    }
    public String findAndRegisterString(PageContext pageContext, String id) throws JspTagException {
        return findAndRegisterString(pageContext, id, true);
    }

    public String findAndRegisterString(PageContext pageContext, String id, boolean check) throws JspTagException {
        return (String) findAndRegister(pageContext, id, id, check);
    }

    public boolean isPresent(String key) throws JspTagException {
        //if (! isRegisteredSomewhere(key)) {
        //    log.warn("Checking presence of unregistered context variable " + key + " in context " + getId());
        //    return false;
        //}
        Object value = get(key);

        if (value == null) {
            return false;
        } else {
            if (value instanceof Collection) {
                if (((Collection) value).size() == 0) return false;
            }
            return true;
        }
    }

    /**
     * @since MMBase-1.8
     */
    public void setJspVar(PageContext pageContext, String jspvar, int type,  Object value) {
        getBacking().setJspVar(pageContext, jspvar, type, value);
    }

    static String getDefaultCharacterEncoding(PageContext pageContext) {
       String charEnc = pageContext.getResponse().getCharacterEncoding();
       if(charEnc != null) {
           return charEnc;
       }
       log.error("page encoding not specified, using iso-8859-1");
       return "iso-8859-1";
    }


    public String toString() {
        if (id == null) {
            return "the context without id " + getBacking().toString();
        } else {
            return "context '" + id  + "'";
        }
    }
}

/**
 * Container class, to store results of 'getPair' function, which is a 'parent' container plus
 * a key.
 *
 * Since MMBase-1.8 three different implementations are available, to work like EL as much as possible.
 */

abstract class Pair {
    public final String           restKey;
    public final boolean          wentDown;
    Pair(String k, boolean w) {
        restKey = k; wentDown = w;
    }
    abstract  boolean containsKey(String key, boolean checkParent) throws JspTagException;
    abstract  Object get(String key, boolean checkParent) throws JspTagException;
    abstract  void register(String newId, Object n, boolean check, boolean checkParent) throws JspTagException;
    abstract void unRegister(String key, boolean checkParent) throws JspTagException;
}

/**
 * @since MMBase-1.8
 */
class ContextContainerPair extends Pair {
    private ContextContainer context;
    ContextContainerPair(ContextContainer c, String k, boolean w) {
        super(k, w);
        context = c;
    }
    final boolean containsKey(String key, boolean checkParent) throws JspTagException {
        return context.containsKey(key, checkParent);
    }
    final Object get(String key, boolean checkParent) throws JspTagException {
        return context.get(key, checkParent);
    }
    final void register(String newId, Object n, boolean check, boolean checkParent) throws JspTagException {
        context.register(newId, n, check, checkParent);
    }
    final void unRegister(String key, boolean checkParent) throws JspTagException {
        context.unRegister(key, checkParent);
    }


}
/**
 * @since MMBase-1.8
 */
class MapPair extends Pair {
    private Map<String, Object> map;
    MapPair(Map<String, Object> c, String k, boolean w) {
        super(k, w);
        map = c;
    }
    final boolean containsKey(String key, boolean checkParent) throws JspTagException {
        if (checkParent) throw new JspTagException("Cannot check parent of Map");
        return map.containsKey(key);
    }
    final Object get(String key, boolean checkParent) throws JspTagException {
        if (checkParent) throw new JspTagException("Cannot check parent of Map");
        return map.get(key);
    }
    final void register(String newId, Object n, boolean check, boolean checkParent) throws JspTagException {
        if (checkParent) throw new JspTagException("Cannot check parent of Map");
        map.put(newId, n);
    }
    final void unRegister(String key, boolean checkParent) throws JspTagException {
        if (checkParent) throw new JspTagException("Cannot check parent of Map");
        map.remove(key);
    }
}

/**
 * @since MMBase-1.8
 */
class BeanPair extends Pair {
    private final Object bean;
    private final Class<? extends Object> clazz;
    BeanPair(Object c, String k, boolean w) {
        super(k, w);
        bean = c;
        clazz = bean.getClass();
    }

    private Method getMethod(String key) {
        String methodKey =  Character.toUpperCase(key.charAt(0)) + key.substring(1);
        Method method;
        try {
            method = clazz.getMethod("get" + methodKey);
        } catch (Exception e) {
            try {
                method = clazz.getMethod("is" + methodKey);
            } catch (Exception f) {
                return null;
            }
        }
        //while (! method.isAccessible()) {
        //}
        return method;
    }
    final boolean containsKey(String key, boolean checkParent) throws JspTagException {
        if (checkParent) throw new JspTagException("Cannot check parent of Bean");
        return getMethod(key) != null;
    }
    final Object get(String key, boolean checkParent) throws JspTagException {
        if (checkParent) throw new JspTagException("Cannot check parent of Bean");
        try {
            Method method = getMethod(key);
            if (method == null) return null;
            return method.invoke(bean);
        } catch (Exception iae) {
            throw new TaglibException(iae.getClass() + " Method " + getMethod(key) + " for " + bean + ": " + iae.getMessage(), iae);
        }
    }
    final void register(String newId, Object n, boolean check, boolean checkParent) throws JspTagException {
        throw new UnsupportedOperationException("Cannot register in a bean");
    }
    final void unRegister(String key, boolean checkParent) throws JspTagException {
        throw new UnsupportedOperationException("Cannot unregister in a bean");
    }

}

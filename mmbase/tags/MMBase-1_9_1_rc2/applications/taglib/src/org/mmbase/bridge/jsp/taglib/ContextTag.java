/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import java.io.IOException;

import javax.servlet.jsp.*;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestWrapper;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.util.*;
import org.mmbase.util.logging.*;




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
 * @version $Id$
 * @see ImportTag
 * @see WriteTag
 */

public class ContextTag extends ContextReferrerTag implements ContextProvider {
    private static final Logger log = Logging.getLoggerInstance(ContextTag.class);

    public static final String CONTEXTTAG_KEY        = "org.mmbase.taglib.context";
    public static final String CONTAINER_KEY_PREFIX  = "org.mmbase.taglib.contextcontainer.";
    public static final String DEFAULTENCODING_KEY   = "org.mmbase.taglib.defaultencoding";
    public static final String ISELIGNORED_PARAM     = "mmbase.taglib.isELIgnored";

    private static int  latestNumber = 0;

    private int number;

    private CloudContext cloudContext;
    private ContextContainer prevParent;

    private Attribute referid = Attribute.NULL;
    private Attribute scope   = Attribute.NULL;

    public void setReferid(String r) throws JspTagException {
        referid = getAttribute(r);
    }

    public void setScope(String s) throws JspTagException {
        scope = getAttribute(s);
    }

    private int getScope() throws JspTagException {
        String ss = scope.getString(this).toLowerCase();
        if ("".equals(ss)) {
            return PageContext.PAGE_SCOPE;
        } else if ("request".equals(ss)) {
            return PageContext.REQUEST_SCOPE;
        } else if ("session".equals(ss)) {
            return PageContext.SESSION_SCOPE;
        } else if ("page".equals(ss)) {
            return PageContext.PAGE_SCOPE;
        } else if ("application".equals(ss)) {
            return PageContext.APPLICATION_SCOPE;
        } else {
            throw new JspTagException("Unknown scope '" + ss + "'");
        }
    }

    /**
     * This context can also serve as a 'cloudcontext'.
     * That means that the cloud context commmunicates its cloudcontext to the context.
     */
    public void setCloudContext(CloudContext cc) {
        cloudContext = cc;
        String def = (String) pageContext.getAttribute(DEFAULTENCODING_KEY);
        if (def == null && cloudContext != null) {
            pageContext.setAttribute(DEFAULTENCODING_KEY, cloudContext.getDefaultCharacterEncoding());
        }
    }


    public void setPageContext(PageContext pc) {
        super.setPageContext(pc); // This will call fillVars for the 'page' Context.
        log.debug("setting page context");
    }

    /**
     * @param c Parent context-container, if <code>null</code> then a container writing to page context will be instantiated.
     */
    ContextContainer createContainer(ContextContainer c) { //throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("Creating context container for " + this + " " + pageContext) ;
        }
        number = latestNumber++;
        ContextContainer container;
        if (c == null && (!"true".equals(pageContext.getServletContext().getInitParameter(ISELIGNORED_PARAM)))) {
            if (log.isDebugEnabled()) {
                log.debug("page context for " + pageContext);
            }
            container = new PageContextContainer(pageContext);
        } else {
            container = new StandaloneContextContainer(pageContext, getId(), c);
        }
        pageContext.setAttribute(CONTAINER_KEY_PREFIX + number, container, PageContext.PAGE_SCOPE);
        return container;
    }

    public ContextContainer getContextContainer() {
        return (ContextContainer)pageContext.getAttribute(CONTAINER_KEY_PREFIX + number);
    }


    /**
     * @since MMBase-1.9
     */
    protected static ServletRequest unwrap(ServletRequest req) {
        while (req instanceof ServletRequestWrapper) {
            req = ((ServletRequestWrapper) req).getRequest();
        }
        return req;
    }


    public int doStartTag() throws JspTagException {
        log.debug("Start tag of ContextTag");
        ContextContainer container;
        int s = getScope();
        prevParent = null;
        if (referid != Attribute.NULL || (s != PageContext.PAGE_SCOPE && getId() != null)) {
            Object o;
            if (s == PageContext.PAGE_SCOPE) {
                o = getObject(referid.getString(this));
            } else {
                String id = referid.getString(this);
                if (id.length() == 0) {
                    id = getId();
                    if (id == null) throw new JspTagException("Must use id or referid attributes when using 'scope' attibute of context tag");
                }
                o = pageContext.getAttribute(id, s);
            }
            if (o == null || "".equals(o)) { // that means, lets ignore it.
                container = createContainer(getContextProvider().getContextContainer());
            } else {
                if (! (o instanceof ContextContainer)) {
                    throw new JspTagException("Found context var '" + o + "' is not of type Context but of '" + o.getClass().getName());
                }
                container = (ContextContainer)  o;
                if (log.isDebugEnabled()) {
                    log.debug("Resetting parent of " + container + " to " + getContextProvider().getContextContainer());
                }
                prevParent = container.getParent();
                if (prevParent instanceof PageContextContainer) {
                    // if for some reason, the parent in the container is from a different
                    // request. Do not accept that.
                    PageContextContainer prevPc = (PageContextContainer) prevParent;
                    if (((PageContextBacking) prevPc.getBacking()).getPageContext() != pageContext) {
                        ServletRequest prevReq = unwrap(((PageContextBacking) prevPc.getBacking()).getPageContext().getRequest());
                        if (prevReq != null && prevReq != unwrap(pageContext.getRequest())) {
                            log.warn("found a pagecontext container for a different request (" + prevReq + " !=  '" + pageContext.getRequest() + "'). Repairing");
                        } else {
                            log.debug("found a pagecontext container for a different pageContext. Repairing");
                        }
                        prevParent = new PageContextContainer(pageContext);
                    }
                }

                container.setParent(pageContext, getContextProvider().getContextContainer());
                pageContext.setAttribute(CONTAINER_KEY_PREFIX + number, container, PageContext.PAGE_SCOPE);
            }
        } else {
            container = createContainer(getContextProvider().getContextContainer());
        }


        if (s != PageContext.PAGE_SCOPE) {
            String id = getId();
            if (id == null) {
                id = referid.getString(this);
            }
            ContextContainer storedContainer =  new StandaloneContextContainer(id, container.getBacking().getOriginalMap(), container.getBacking().isELIgnored());
            pageContext.setAttribute(id, storedContainer, s);
        }
        setCloudContext(getContextTag().cloudContext);
        if (getId() != null) {
            if (log.isDebugEnabled()) {
                log.debug("registering container " + container + " " + getId() + " with context " + getContextProvider().getContextContainer().getId());
            }
            getContextProvider().getContextContainer().register(getId(), container, referid == Attribute.NULL);
        }
        log.debug("out");
        // return EVAL_BODY_INCLUDE; does not work in orion 1.6, tomcat < 4.1.19
        return EVAL_BODY;
    }

    /**
     * Precisely like 'register', only it wants a Node.
     *
     * @param key the key (id) of the node to register
     * @param node the node to put in the hashmap
     * @deprecated Use getContextProvider().getContextContainer().registerNode
     */
    public void  registerNode(String key, Node node) throws JspTagException {
        getContextContainer().registerNode(key, node);
    }


    /**
     * Searches a key in request, postparameters, session, parent
     * context and registers it in this one.
     *
     *  Returns null if it could not be found.
     * @deprecated Use getContextProvider().getContextContainer().findAndRegister
     */
    public Object findAndRegister(int from, String referid, String newid) throws JspTagException {
        return getContextContainer().findAndRegister(pageContext, from, referid, newid, true);
    }

    /**
     * @deprecated Use getContextProvider().getContextContainer().findAndRegister
     */
    protected Object findAndRegister(int from, String referid, String newid, boolean check) throws JspTagException {
        return getContextContainer().findAndRegister(pageContext, from, referid, newid, check);
    }

    /**
     * @deprecated Use getContextProvider().getContextContainer().findAndRegister
     */
    public Object findAndRegister(String externid, String newid) throws JspTagException {
        return getContextContainer().findAndRegister(pageContext, externid, newid);
    }


    /**
     * @deprecated Use getContextProvider().getContextContainer().register
     */
    public void register(String newid, Object n, boolean check) throws JspTagException {
        getContextContainer().register(newid, n, check);
    }

    /**
     * Register an Object with a key in the context. If the Context is
     * a session context, then it will be put in the session, otherwise in the hashmap.
     * @deprecated Use getContextProvider().getContextContainer().register
     */
    public void register(String newid, Object n) throws JspTagException {
        getContextContainer().register(newid, n);
    }


    /**
     * @deprecated Use getContextProvider().getContextContainer().unRegister
     */
    public void unRegister(String key) throws JspTagException {
        getContextContainer().unRegister(key);
    }

    /**
     * Registers an variable again. This can be used to change the type of a variable, e.g.
     *
     * @since MMBase-1.6
     * @deprecated Use getContextProvider().getContextContainer().reregister
     */
    public void reregister(String id, Object n) throws JspTagException {
        getContextContainer().reregister(id, n);
    }


    /**
     * 'present' means 'not null'. 'null' means 'registered, but not present'.
     *  Not registered is not present, of course.
     * @deprecated Use getContextProvider().getContextContainer().isPresent
     */

    public boolean isPresent(String key) throws JspTagException {
        return getContextContainer().isPresent(key);
    }

    /**
     * @deprecated Use getContextProvider().getContextContainer().isRegistered
     */
    public boolean isRegistered(String key) throws JspTagException {
        return getContextContainer().isRegistered(key);
    }
    /**
     * @deprecated Use getContextProvider().getContextContainer().findAndRegister
     */
    public Object findAndRegister(String id) throws JspTagException {
        return getContextContainer().findAndRegister(pageContext, id);
    }
    /**
     * @deprecated Use getContextProvider().getContextContainer().findAndRegisterString
     */
    public String findAndRegisterString(String id) throws JspTagException {
        return getContextContainer().findAndRegisterString(pageContext, id);
    }

    /**
     * @deprecated Use getContextProvider().getContextContainer().getObject
     */

    public Object getContainerObject(String key) throws JspTagException {
        return getContextContainer().getObject(key);

    }



    public  org.mmbase.util.SerializableInputStream getInputStream(String key) throws JspTagException {
        return MultiPart.getMultipartRequest(pageContext).getInputStream(key);

    }

    public int doAfterBody() throws JspTagException {
        if (log.isDebugEnabled()) {
            log.debug("after body of context " + getId());
        }
        // just to serve lousy app-server which do not support EVAL_BODY_INCLUDE
        if (EVAL_BODY == EVAL_BODY_BUFFERED) {
            try {
                if (bodyContent != null) {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                }
            } catch (IOException ioe){
                throw new TaglibException(ioe);
            }
        }
        return SKIP_BODY;
    }

    public int doEndTag() throws JspTagException {
        getContextContainer().release(pageContext, prevParent != null ? prevParent : getContextProvider().getContextContainer()); // remove the vars from 'page-context' again if necessary.
        cloudContext = null;
        prevParent = null;
        return super.doEndTag();
    }

    public void doFinally() {
        cloudContext = null;
        prevParent = null;
        super.doFinally();
    }

    public String toString() {
        return getClass().getName() + " with id " + getId() + " with container " + getContextContainer();
    }
}



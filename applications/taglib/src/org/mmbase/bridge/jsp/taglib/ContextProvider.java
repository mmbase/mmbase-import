/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import javax.servlet.jsp.JspTagException;


/**
 * Abstract representation of a 'context' tag. The one context is of
 * course the context tag itself. But also List tags work as a kind of
 * contextes.
 * 
 * @since MMBase-1.7
 * @version $Id: ContextProvider.java,v 1.1 2003-05-19 14:53:01 michiel Exp $
 **/

public interface ContextProvider  extends TagIdentifier {

    /**
     * Precisely like 'register', only it wants a Node.
     *
     * @param key the key (id) of the node to register
     * @param node the node to put in the hashmap
     */
    public void  registerNode(String key, org.mmbase.bridge.Node n) throws JspTagException;

    public Object findAndRegister(int from, String referid, String newid) throws JspTagException;

    /**
     * Searches a key in request, postparameters, session, parent
     * context and registers it in this one.
     *
     *  Returns null if it could not be found.
     */
    public Object findAndRegister(String externid, String newid) throws JspTagException;

    public Object findAndRegister(String id) throws JspTagException;

    public String findAndRegisterString(String id) throws JspTagException;

    public Object getContainerObject(String key) throws JspTagException;

    public void register(String newid, Object n, boolean check) throws JspTagException;

    /**
     * Register an Object with a key in the context. If the Context is
     * a session context, then it will be put in the session, otherwise in the hashmap.
     */
    public void register(String newid, Object n) throws JspTagException;

    public void unRegister(String key) throws JspTagException;

    /**
     * Registers an variable again. This can be used to change the type of a variable, e.g.
     *
     * @since MMBase-1.6
     */
    public void reregister(String id, Object n) throws JspTagException;

    /**
     * 'present' means 'not null'. 'null' means 'registered, but not present'.
     *  Not registered is not present, of course.
     */
    public boolean isPresent(String key) throws JspTagException;


    /**
     * Gets an object from the Context.
     *
     */
    public Object getObject(String key) throws JspTagException;

}

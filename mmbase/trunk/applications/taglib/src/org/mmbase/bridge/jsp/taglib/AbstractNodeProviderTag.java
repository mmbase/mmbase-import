/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;


import org.mmbase.bridge.jsp.taglib.util.Attribute;

import javax.servlet.jsp.JspTagException;

import javax.servlet.jsp.tagext.*;

import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 * A base class for tags which provide a node. The
 * general attributes for a NodeProvider are
 * <ul>
 * <li> id: The identifier. Used as a key for the Context. If this
 * attribute is missing, the Node variable will not be imported in the Context. </li>
 * <li> jspvar: An identifier for a jsp variable available in the
 * body. If this attribute is missing, no jsp-variable will be
 * created.</li>
 * </ul>
 *
 * @author Michiel Meeuwissen
 * @author Kees Jongenburger
 * @version $Id: AbstractNodeProviderTag.java,v 1.27 2003-12-21 13:27:50 michiel Exp $ 
 */

abstract public class AbstractNodeProviderTag extends NodeReferrerTag implements NodeProvider {

    // a node provider is a nodereferrer as well...
    // this is especially useful for some extended classes (like 'relatednodes').
    
    private static final Logger log = Logging.getLoggerInstance(AbstractNodeProviderTag.class);
    
    private   Node   node;        
    private   String jspvar = null;
    private   boolean  modified = false;
    

    // general attributes for NodeProviders
    // id from TagSupport

    public void setJspvar(String jv) {
        jspvar = jv;
        if ("".equals(jspvar)) jspvar = null;
    }

        
    /**
    * For use by children, they can find the current 'node' belonging
    * to this tag.
    */
    
    public Node getNodeVar() {
        return node;
    }
    
    /**
     * Children can also directly access the node member, but the
     * prefered method is to treat this variable as much as possible
     * as private, and use this.
     */

    protected void setNodeVar(Node node) {        
        this.node = node;
    }
    
    abstract public void doInitBody() throws JspTagException;
    
    /**
     * Fill the jsp and context vars
     *
     */

    protected void fillVars() throws JspTagException {    
        if (jspvar != null && node != null) {
            pageContext.setAttribute(jspvar, node);
        }
        if (id != Attribute.NULL) {
            getContextProvider().getContextContainer().registerNode(getId(), node);
        }
    }
               
    private String getSimpleReturnValueName(String fieldName){        
        return getSimpleReturnValueName(jspvar, fieldName);
    }
    /**
     * Generates the variable-name for a field.
     *
     * @param prefix A prefix to use. Can be null.
     * @param fieldName The name of the field.
     */
    static String getSimpleReturnValueName(String prefix, String fieldName){
        String field = fieldName.replace('.','_');
        if (prefix != null && ! "".equals(prefix)) {
            field = prefix + "_" + field;
        }
        return field;
    }

    public void setModified() {
        //log.info(Logging.stackTrace());
        modified = true;
    }

    /**
     * @since MMBase-1.7
     */
    protected boolean getModified() {
        return modified;
    }
    /**
    * Does everything needed on the afterbody tag of every
    * NodeProvider.  Normally this function would be overrided with
    * one that has to call super.doAfterBody().  But not all servlet
    * engines to call this function if there is no body. So, in that
    * case it should be called from doEndTag, if the tag can do
    * something without a body.
    **/
    public int doAfterBody() throws JspTagException {
        if (modified) {
            log.service("node was changed ! calling commit");
            node.commit();
        }
        return SKIP_BODY;
    }
    
    public int doEndTag() throws JspTagException {
        node = null;
        modified = false;
        return EVAL_PAGE;
    }
}

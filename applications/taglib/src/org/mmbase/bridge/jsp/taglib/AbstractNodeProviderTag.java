/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;


import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

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
 * @version $Id: AbstractNodeProviderTag.java,v 1.38 2006-07-17 15:38:47 johannes Exp $ 
 */

abstract public class AbstractNodeProviderTag extends NodeReferrerTag implements NodeProvider {
    // a node provider is a nodereferrer as well...
    // this is especially useful for some extended classes (like 'relatednodes').

    final protected  NodeProviderHelper nodeHelper = new NodeProviderHelper(this); // no m.i. and there are more nodeprovider which cannot extend this, they can use the same trick.


    protected Attribute fieldEscaper;

    /**
     * @since MMBase-1.8
     */
    public void setFieldescape(String e) throws JspTagException {
        fieldEscaper = getAttribute(e);
    }


    public void setJspvar(String jv) {
        nodeHelper.setJspvar(jv);
    }

    
    public Node getNodeVar() {
        return nodeHelper.getNodeVar();
    }
    

    protected void setNodeVar(Node node) {        
        nodeHelper.setNodeVar(node);
    }

    /**
     * @since MMBase-1.8
     */
    public void setCommitonclose(String c) throws JspTagException {
        nodeHelper.setCommitonclose(c);
    }
    
    /**
     * Fill the jsp and context vars
     *
     */

    protected void fillVars() throws JspTagException {    
        nodeHelper.fillVars();
    }
               
    public  Query getGeneratingQuery() throws JspTagException {
        return nodeHelper.getGeneratingQuery();
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
        return nodeHelper.doAfterBody();
    }
    
    public int doEndTag() throws JspTagException {
        super.doEndTag();
        return nodeHelper.doEndTag();
    }

    public void doFinally() {
        super.doFinally();
        nodeHelper.doFinally();
    }
}

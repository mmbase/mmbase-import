/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;


import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.Queries;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id: NodeProviderHelper.java,v 1.9 2005-06-22 17:24:01 michiel Exp $ 
 * @since MMBase-1.7
 */

public class NodeProviderHelper implements NodeProvider {

    private static final Logger log = Logging.getLoggerInstance(NodeProviderHelper.class);
        
    private   Node   node;        
    private   Query  query = null;
    private   String jspvar = null;
    private   boolean  modified = false;
    private   ContextReferrerTag thisTag;


    public NodeProviderHelper(ContextReferrerTag thisTag) {
        this.thisTag     = thisTag;
    }

    

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

    public void setNodeVar(Node node) {        
        this.node = node;
    }
    

    public void setGeneratingQuery(Query q) {
        query = q;
    }

    public Query getGeneratingQuery() throws JspTagException {
        if (query == null) {
            query = Queries.createNodeQuery(getNodeVar());
        } 
        return query;
    }

    public String getId() {
        try {
            return (String) thisTag.id.getValue(thisTag);
        } catch (JspTagException j) {
            throw new RuntimeException(j);
        }
    }


    boolean checked = false; // need to check jspvar/pagecontext-var conflict only first time.
    /**
     * Fill the jsp and context vars
     *
     */

    public void fillVars() throws JspTagException {    
        org.mmbase.bridge.jsp.taglib.util.ContextContainer cc = thisTag.getContextProvider().getContextContainer();
        if (thisTag.id != Attribute.NULL) {
            cc.registerNode(getId(), node);
        }
        if (jspvar != null && node != null) {
            cc.setJspVar(thisTag.getPageContext(), jspvar, WriterHelper.TYPE_NODE, node);
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

    public boolean getModified() {
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
            log.service("Committing node " + node.getNumber() + " for user " + node.getCloud().getUser().getIdentifier());
            node.commit();
        }
        return BodyTagSupport.SKIP_BODY;
    }
    
    public int doEndTag() throws JspTagException {
        // to enable gc:
        node     = null;
        modified = false;
        checked  = false;
        return BodyTagSupport.EVAL_PAGE;
    }
}

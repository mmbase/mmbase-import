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
import java.util.Stack;

import org.mmbase.bridge.*;
import org.mmbase.bridge.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.7
 */

public class NodeProviderHelper implements NodeProvider {

    private static final Logger log = Logging.getLoggerInstance(NodeProviderHelper.class);

    public static final String STACK_ATTRIBUTE = "org.mmbase.bridge.jsp.taglib._nodeStack";
    public static final String _NODE = "_node";

    private   NodeChanger   node;
    private   Query  query = null;
    private   String jspvar = null;
    private   ContextReferrerTag thisTag;
    private Attribute commit = Attribute.NULL;

    /**
     * 'underscore' stack, containing the values for '_node'.
     * @since MMBase_1.8
     */
    private   Stack<NodeChanger> _Stack;
    // whether this tag pushed something on the stack already.
    private   int pushed = 0;

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
     * @since MMBase-1.8
     */
    public void setCommitonclose(String c) throws JspTagException {
        commit = thisTag.getAttribute(c);
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
        if (node == null) {
            this.node = null;
        } else if (node instanceof NodeChanger) {
            this.node = (NodeChanger) node;
        } else {
            this.node = new NodeChanger(node);
        }
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
     */

    public void fillVars() throws JspTagException {
        org.mmbase.bridge.jsp.taglib.util.ContextContainer cc = thisTag.getContextProvider().getContextContainer();
        if (thisTag.id != Attribute.NULL) {
            cc.registerNode(getId(), node);
        }
        if (jspvar != null && node != null) {
            cc.setJspVar(thisTag.getPageContext(), jspvar, WriterHelper.TYPE_NODE, node);
        }
        PageContext pageContext = thisTag.getPageContext();

        _Stack = (Stack<NodeChanger>) pageContext.getAttribute(STACK_ATTRIBUTE, PageContext.REQUEST_SCOPE);
        if (_Stack == null) {
            _Stack = new Stack<NodeChanger>();
            pushed = 0;
            pageContext.setAttribute(STACK_ATTRIBUTE, _Stack, PageContext.REQUEST_SCOPE);
        }
        _Stack.push(node);
        pushed++;
        pageContext.setAttribute(_NODE, org.mmbase.util.Casting.wrap(node, (org.mmbase.util.transformers.CharTransformer) pageContext.findAttribute(ContentTag.ESCAPER_KEY)), PageContext.REQUEST_SCOPE);
    }

    /**
     * Generates the variable-name for a field.
     *
     * @param prefix A prefix to use. Can be null.
     * @param fieldName The name of the field.
     */
    static String getSimpleReturnValueName(String prefix, String fieldName){
        String field = fieldName.replace('.', '_');
        if (prefix != null && ! "".equals(prefix)) {
            field = prefix + "_" + field;
        }
        return field;
    }

    /**
     * @since MMBase-1.8.2
     */
    private void pop_Stack() {
        if (_Stack != null) {
            Object pop = _Stack.pop();
            pushed--;
            PageContext pageContext = thisTag.getPageContext();
            if (_Stack.empty()) {
                pageContext.removeAttribute(_NODE, PageContext.REQUEST_SCOPE);
                if (pushed != 0) {
                    // would otherwise come in infinite loop in doEndTag. But it should not have
                    // happend in the first place
                    log.warn("The _Stack was empty while there should still be things on it ", new Exception());
                    pushed = 0;
                }
                _Stack = null;
            } else {
                pageContext.setAttribute(_NODE, org.mmbase.util.Casting.wrap(_Stack.peek(), (org.mmbase.util.transformers.CharTransformer) pageContext.findAttribute(ContentTag.ESCAPER_KEY)), PageContext.REQUEST_SCOPE);
            }
        }
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
        if (node != null) {
            if (node.isNew() || node.isChangedByThis()) {
                // node can need committing
                if (commit.getBoolean(thisTag, true)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Committing node " + node.getNumber() + " for user " + node.getCloud().getUser().getIdentifier() + " changed fields: " + node.getChanged() + " " + node.isNew() + " " + node.isChanged() + " because ", new Exception());
                    }
                    node.commit();
                } else if (log.isDebugEnabled()) {
                    log.debug("Not committing " + node.getNumber() + " for user " + node.getCloud().getUser().getIdentifier() + " changed fields: " + node.getChanged() + " " + node.isNew() + " " + node.isChanged());
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Node " + node.getNumber() + " was not changed " + thisTag.getClass() + " ");
                }
            }
        }
        pop_Stack();
        return BodyTagSupport.SKIP_BODY;
    }

    public int doEndTag() throws JspTagException {
        // to enable gc:
        if (_Stack != null) {
            while (pushed > 0) {
                pop_Stack();
            }
        }
        pushed = 0;
        node = null;
        _Stack = null;
        query = null;
        checked = false;
        return BodyTagSupport.EVAL_PAGE;
    }

    public void doFinally () {
        node = null;
        _Stack = null;
        pushed = 0;
        query = null;
    }
}

/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.functions;

import java.util.Collection;

import javax.servlet.jsp.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.containers.FunctionContainerReferrer;
import org.mmbase.bridge.jsp.taglib.util.ContextContainer;
import org.mmbase.util.logging.*;

/**
 * A function tag for functions returning a NodeList. The result is iterated.
 *
 * This is one of the most straightforward ListProvider/NodeProvider implementations, you could use it as a template.
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.7
 * @version $Id: NodeListFunctionTag.java,v 1.18 2008-08-14 13:58:49 michiel Exp $
 */
public class NodeListFunctionTag extends AbstractFunctionTag implements ListProvider, FunctionContainerReferrer, NodeProvider {

    private static final Logger log = Logging.getLoggerInstance(NodeListFunctionTag.class);

    //cannot extend AbstractNodeList because we extend AbstractFunctionTag already.
    //even proxies are no option because I do not instantiate nor call these tag objects.
    //therefore, all this stuff with helpers.
    private NodeProviderHelper nodeHelper = new NodeProviderHelper(this);
    private NodeListHelper     listHelper = new NodeListHelper(this, nodeHelper);


    // implementation of NodeProvider
    public Node getNodeVar() throws JspTagException {
        return nodeHelper.getNodeVar();
    }

    public Query getGeneratingQuery() throws JspTagException {
        return nodeHelper.getGeneratingQuery();
    }
    /**
     * @since MMBase-1.8
     */
    public void setCommitonclose(String c) throws JspTagException {
        nodeHelper.setCommitonclose(c);
    }

    public void setJspvar(String jv) {
        nodeHelper.setJspvar(jv);
    }

    // implementation of ListProvider
    public int size() {
        return listHelper.size();
    }

    public int getIndex() {
        return listHelper.getIndex();
    }

    public int getIndexOffset() {
        return listHelper.getIndexOffset();
    }

    public boolean isChanged() {
        return listHelper.isChanged();
    }

    public Object getCurrent() {
        return listHelper.getCurrent();
    }

    public void remove() {
        listHelper.remove();
    }

    // extra stuff (should perhaps be part of ListProvider interface)
    public void setMax(String m) throws JspTagException {
        listHelper.setMax(m);
    }

    public void setOffset(String o) throws JspTagException {
        listHelper.setOffset(o);
    }

    public void setComparator(String c) throws JspTagException {
        listHelper.setComparator(c);
    }
    public void setAdd(String c) throws JspTagException {
        listHelper.setAdd(c);
    }
    public void setRetain(String c) throws JspTagException {
        listHelper.setRetain(c);
    }
    public void setRemove(String c) throws JspTagException {
        listHelper.setRemove(c);
    }
    public void setVarStatus(String s) throws JspTagException {
        listHelper.setVarStatus(s);
    }

    public ContextContainer getContextContainer() throws JspTagException {
        return listHelper.getContextContainer();
    }

    public int doStartTag() throws JspTagException {
        initTag();
        NodeList list;
        Object value = getFunctionValue(false);

        /* should be something like:

        NodeList list = Casting.toNodeList(getCloudVar(), value);

        */
        if (value instanceof NodeList) {
            list = (NodeList) value;
        } else {
            list = getCloudVar().createNodeList();
            if (value != null) {
                list.addAll((Collection) value);
            }
        }
        listHelper.doStartTagHelper();
        return listHelper.setReturnValues(list, true);
    }

    public int doAfterBody() throws JspException {
        log.debug("doafterbody");
        nodeHelper.doAfterBody();
        return listHelper.doAfterBody();
    }

    public int doEndTag() throws JspTagException {
        super.doEndTag();
        listHelper.doEndTag();
        return nodeHelper.doEndTag();
    }

    public void doFinally() {
        listHelper.doFinally();
        nodeHelper.doFinally();
    }

    public javax.servlet.jsp.jstl.core.LoopTagStatus getLoopStatus() {
        return new ListProviderLoopTagStatus(this);
    }
}


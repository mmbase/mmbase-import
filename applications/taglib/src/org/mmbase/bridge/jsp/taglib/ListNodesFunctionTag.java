/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.util.*;
import org.mmbase.bridge.jsp.taglib.containers.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.util.functions.Parameters;

import java.util.*;
import java.io.IOException;
import javax.servlet.http.*;

/**
 * A function tag for functions returning a NodeList. The result is iterated.
 *
 * This is one of the most straightforward ListProvider/NodeProvider implementations, you could use it as a template. 
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.7
 * @version $Id: ListNodesFunctionTag.java,v 1.1 2004-01-14 22:08:45 michiel Exp $
 */
public class ListNodesFunctionTag extends AbstractFunctionTag implements ListProvider, FunctionContainerReferrer, NodeProvider {
    //cannot extend AbstractNodeList because we extend AbstractFunctionTag alreeady. Sigh, stupid java.
    //even proxies are no option because I do not instantiate nor call these tag objects.
    //therefore, all this stuff with helpers.

    private static final Logger log = Logging.getLoggerInstance(ListNodesFunctionTag.class);


    private NodeProviderHelper nodeHelper = new NodeProviderHelper(this);
    private NodeListHelper     listHelper = new NodeListHelper(this, nodeHelper);


    // implementation of NodeProvider 

    public Node getNodeVar() throws JspTagException {
        return nodeHelper.getNodeVar();
    }
    public void setModified() {
        nodeHelper.setModified();
    }
    public void setJspvar(String jv) {
        nodeHelper.setJspvar(jv);
    }


    // implementation of ListProvider
    public int size(){
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



    public ContextContainer getContextContainer() throws JspTagException {
        return listHelper.getContextContainer();
    }

    public int doStartTag() throws JspTagException {

        NodeList list;
        { 
            Object value = getFunctionValue();
            if (value instanceof NodeList) {
                list = (NodeList) value;
            } else {
                list = getCloud().getCloudContext().createNodeList();
                list.addAll((Collection) value); 
                // the Collection must contain "Nodes" only, no MMObjectNodes otherwise Exception from BasicNodeList, because it cannot convert without Cloud
            }
        }
        return listHelper.setReturnValues(list, true);
    }


    public int doAfterBody() throws JspException {
        log.debug("doafterbody");
        nodeHelper.doAfterBody();
        return listHelper.doAfterBody();

    }

    public int doEndTag() throws JspTagException {
        listHelper.doEndTag();
        return nodeHelper.doEndTag();
    }

    public void doInitBody() throws JspTagException {
        listHelper.doInitBody();
    }

}

/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.IOException;
import java.util.*;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;

import org.mmbase.bridge.Node;

import org.mmbase.bridge.jsp.taglib.util.ContextContainer;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class makes a tag which can list the aliases of a Node.
 *
 * @author Michiel Meeuwissen
 **/

public class AliasListTag extends NodeReferrerTag implements ListProvider, Writer {
    private static Logger log = Logging.getLoggerInstance(AliasListTag.class);

    private List     returnList;
    private Iterator returnValues;
    //private String currentAlias;
    private int currentItemIndex= -1;

    public int size(){
        return returnList.size();
    }
    public int getIndex() {
        return currentItemIndex;
    }
    public boolean isChanged() {
        return true;
    }
    public Object getCurrent() {
        return getWriterValue();
    }

    /**
     * Lists do implement ContextProvider
     */
    protected ContextContainer container;
    private   Map              collector;



    // ContextProvider implementation
    public ContextContainer getContainer() {
        return container;
    }


    /**
     *
     *
     */
    public int doStartTag() throws JspTagException{

        container = new ContextContainer(null, getContextProvider().getContainer());
        collector = new HashMap();


        helper.overrideWrite(false); // default behavior is not to write to page
        helper.setTag(this);
        currentItemIndex= -1;  // reset index
        if (getReferid() != null) {
            Object o =  getObject(getReferid());
            if (! (o instanceof List)) {
                throw new JspTagException("Context variable " + getReferid() + " is not a List");
            }
            returnList = (List) o;
        } else {
            Node node = getNode();
            returnList = node.getAliases();
        }
        returnValues = returnList.iterator();
        // if we get a result from the query
        // evaluate the body , else skip the body
        if (returnValues.hasNext())
            return EVAL_BODY_BUFFERED;
        return SKIP_BODY;
    }

    public int doAfterBody() throws JspException {
        if (getId() != null) {
            getContextProvider().getContainer().unRegister(getId());
        }
        helper.doAfterBody();

        collector.putAll(container);
        container.clear();

        if (returnValues.hasNext()){
            doInitBody();
            return EVAL_BODY_AGAIN;
        } else {
            getContextProvider().getContainer().registerAll(collector);
            if (bodyContent != null) {
                try {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                } catch (IOException ioe){
                    throw new JspTagException(ioe.toString());
                }
            }
            return SKIP_BODY;
        }
    }
    public int doEndTag() throws JspTagException {
        if (getId() != null) {
            getContextProvider().getContainer().register(getId(), returnList);
        }
        return  EVAL_PAGE;
    }


    public void doInitBody() throws JspTagException {
        if (returnValues.hasNext()){
            currentItemIndex ++;
            helper.setValue(returnValues.next().toString());
            if (getId() != null) {
                getContextProvider().getContainer().register(getId(), helper.getValue());
            }

        }
    }
}


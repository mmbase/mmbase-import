/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;

import java.io.IOException;
import java.util.*;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;

import org.mmbase.bridge.*;

import org.mmbase.bridge.jsp.taglib.util.ContextContainer;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class makes a tag which can list strings.
 *
 * @author Michiel Meeuwissen
 * @version $Id: StringListTag.java,v 1.2 2003-08-05 21:54:59 michiel Exp $ 
 * @since MMBase-1.7
 */

public class StringListTag extends NodeReferrerTag implements ListProvider, Writer {

    private static Logger log = Logging.getLoggerInstance(StringListTag.class);

    protected  List    returnList;
    protected Iterator returnValues;
    protected int      currentItemIndex= -1;

    protected Attribute  max = Attribute.NULL;

    public int size(){
        return returnList.size();
    }
    public int getIndex() {
        return currentItemIndex;
    }

    public int getOffset() {
        return 1;
    }

    public boolean isChanged() {
        return true;
    }
    public Object getCurrent() {
        return getWriterValue();
    }


    public void setMax(String m) throws JspTagException {       
        max = getAttribute(m);
    }

    protected int getMaxNumber() throws JspTagException {
        return max.getInt(this, -1);
    }

    /**
     * Lists do implement ContextProvider
     */
    protected ContextContainer contextContainer;
    private   Map              collector;



    // ContextProvider implementation
    public ContextContainer getContainer() {
        return contextContainer;
    }


    /**
     * Creates the actual list of strings.
     */
    protected List getList() throws JspTagException {
        throw new JspTagException("Should use 'referid' attribute on liststrings tag");
    }

    /**
     * For use with referid
     */
    protected void  truncateList() throws JspTagException {
        if (max != Attribute.NULL) {
            int m = getMaxNumber();
            if (m > 0 && m < returnList.size()) {
                returnList = returnList.subList(0, m);
            }
        }        
    }

    /**
     *
     *
     */
    public int doStartTag() throws JspTagException{

        contextContainer = new ContextContainer(null, getContextProvider().getContainer());
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
            truncateList();
        } else {
            returnList = getList();
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

        collector.putAll(contextContainer);
        contextContainer.clear();

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
            helper.setValue("" + returnValues.next());
            if (getId() != null) {
                getContextProvider().getContainer().register(getId(), helper.getValue());
            }

        }
    }
}


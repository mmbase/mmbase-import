/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.*;
import org.mmbase.bridge.jsp.taglib.containers.*;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import java.util.*;
import java.io.IOException;

/**
 * The Function tag can be used as a child of a 'NodeProvider' tag (but not on clusternodes?). It
 * can call functions on the node.
 *
 * @author  Michiel Meeuwissen
 * @since   MMBase-1.7
 * @version $Id: ListFunctionTag.java,v 1.4 2003-11-19 16:57:42 michiel Exp $
 */
public class ListFunctionTag extends AbstractFunctionTag implements ListProvider, FunctionContainerReferrer {

    private static final Logger log = Logging.getLoggerInstance(ListFunctionTag.class);

    protected  List    returnList;
    protected Iterator iterator;
    protected int      currentItemIndex= -1;


    private   ContextCollector collector;
    protected Attribute  comparator = Attribute.NULL;

    public int size(){
        return returnList.size();
    }
    public int getIndex() {
        return currentItemIndex;
    }

    public int getIndexOffset() {
        return 1;
    }

    public boolean isChanged() {
        return true;
    }
    public Object getCurrent() {
        return getWriterValue();
    }

    public void remove() {
        iterator.remove();
    }


    public ContextContainer getContextContainer() {
        return collector.getContextContainer();
    }

    public int doStartTag() throws JspTagException {        
        Object value = getFunctionValue();

        if (! (value instanceof List)) {
            throw new JspTagException("Function result '" + value + "' is not of type List");
        }
        collector = new ContextCollector(getContextProvider().getContextContainer());

        helper.overrideWrite(false); // default behavior is not to write to page
        
        currentItemIndex= -1;  // reset index
        
        returnList = (List) value;
        ListSorter.sort(returnList, (String) comparator.getValue(this), pageContext);
        iterator = returnList.iterator();
        if (iterator.hasNext()) {
            return EVAL_BODY_BUFFERED;
        }
        return SKIP_BODY;
    }


    public int doAfterBody() throws JspException {
        if (getId() != null) {
            getContextProvider().getContextContainer().unRegister(getId());
        }

        helper.doAfterBody();
        collector.doAfterBody();

        if (iterator.hasNext()){
            doInitBody();
            return EVAL_BODY_AGAIN;
        } else {
            if (bodyContent != null) {
                try {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                } catch (IOException ioe){
                    throw new TaglibException(ioe);
                }
            }
            return SKIP_BODY;
        }
    }
    public int doEndTag() throws JspTagException {
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), returnList);
        }
        return  EVAL_PAGE;
    }


    public void doInitBody() throws JspTagException {
        if (iterator.hasNext()){
            currentItemIndex ++;
            helper.setValue("" + iterator.next());
            if (getId() != null) {
                getContextProvider().getContextContainer().register(getId(), helper.getValue());
            }

        }
    }




}

/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.StringIterator;
import org.mmbase.bridge.StringList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.implementation.BasicNodeList;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class makes a tag which can list the aliases of a Node.
 *
 * @author Michiel Meeuwissen
 **/

public class AliasListTag extends NodeReferrerTag implements ListProvider, Writer {
    private static Logger log = Logging.getLoggerInstance(AliasListTag.class.getName());

    protected WriterHelper helper = new WriterHelper(); 

    public void setVartype(String t) throws JspTagException { 
        helper.setVartype(t);
    }
    public void setJspvar(String j) {
        helper.setJspvar(j);
    }
    public void setWrite(String w) throws JspTagException {
        helper.setWrite(getAttributeBoolean(w));
    }
    public Object getValue() {
        return helper.getValue();
    }

    private StringList     returnList;
    private StringIterator returnValues;
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
        return getValue();
    }

    /**
     *
     *
     */
    public int doStartTag() throws JspTagException{       
        helper.overrideWrite(false); // default behavior is not to write to page
        currentItemIndex= -1;  // reset index        
        Node node = getNode();
        returnList = node.getAliases();
        returnValues = returnList.stringIterator();        
        // if we get a result from the query
        // evaluate the body , else skip the body
        if (returnValues.hasNext())
            return EVAL_BODY_TAG;
        return SKIP_BODY;
    }

    public int doAfterBody() throws JspTagException {
        if (getId() != null) {
            getContextTag().unRegister(getId());            
        }
        if (returnValues.hasNext()){
            doInitBody();
            return EVAL_BODY_TAG;
        } else {
            try {
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            } catch (IOException ioe){
                throw new JspTagException(ioe.toString());
            }
            return SKIP_BODY;
        }
    }
    public int doEndTag() throws JspTagException {
        if (getId() != null) {
            getContextTag().register(getId(), returnList);
        }        
        return  EVAL_PAGE;
    }


    public void doInitBody() throws JspTagException {
        if (returnValues.hasNext()){
            currentItemIndex ++;
            helper.setValue(returnValues.nextString());
            helper.setJspvar(pageContext);  
            if (getId() != null) {
                getContextTag().register(getId(), helper.getValue());
            }

        }
    }
}


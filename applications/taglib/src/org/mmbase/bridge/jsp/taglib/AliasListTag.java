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
public class AliasListTag extends NodeReferrerTag implements ListItemInfo {
    private static Logger log = Logging.getLoggerInstance(AliasListTag.class.getName());

    private StringIterator returnValues;
    private String currentAlias;
    private int currentItemIndex= -1;
    private int listSize = 0;

    public int size(){
        return listSize;
    }
    public int getIndex() {
        return currentItemIndex;
    }
    public boolean isChanged() {
        return true;
    }

    /**
    *
    **/
    public int doStartTag() throws JspTagException{       
        currentItemIndex= -1;  // reset index        
        Node node = findNodeProvider().getNodeVar();

        returnValues = node.getAliases().stringIterator();        
        // if we get a result from the query
        // evaluate the body , else skip the body
        if (returnValues.hasNext())
            return EVAL_BODY_TAG;
        return SKIP_BODY;
    }

    public int doAfterBody() throws JspTagException {
        String id = getId();
        if (id != null && ! "".equals(id)) {
            getContextTag().unRegister(id);
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


    public void doInitBody() throws JspTagException {
        if (returnValues.hasNext()){
            currentItemIndex ++;
            currentAlias = returnValues.nextString();
            String id = getId();
            if (id != null && ! "".equals(id)) {
                getContextTag().register(id, currentAlias);
            }
        }
    }
}


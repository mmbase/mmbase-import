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

import org.mmbase.bridge.Field;
import org.mmbase.bridge.FieldIterator;
import org.mmbase.bridge.FieldList;
import org.mmbase.bridge.NodeManager;
import org.mmbase.bridge.implementation.BasicNodeList;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* This class makes a tag which can list the fields of a NodeManager.
*
* @author Michiel Meeuwissen
**/
public class FieldListTag extends NodeReferrerTag implements ListItemInfo {

        
    private static Logger log = Logging.getLoggerInstance(FieldListTag.class.getName());
    
    private FieldIterator returnValues;
    private Field currentField;
    private int currentItemIndex= -1;
    private int listSize = 0;

    private String nodeManagerString = null;
    private NodeProvider nodeProvider = null;

    private int type = NodeManager.ORDER_LIST;

    public int size(){
        return listSize;
    }
    public int getIndex() {
        return currentItemIndex;
    }
    public boolean isChanged() {
        return true;
    }
        
    public Field getField() {
        return currentField;
    }

    public void setNodemanager(String n) {
        nodeManagerString = n;
    }

    public void setType(String t) throws JspTagException {
        if("create".equals(t)) {
            type = NodeManager.ORDER_CREATE;
        } else if ("edit".equals(t)) {
            type = NodeManager.ORDER_EDIT;
        } else if ("list".equals(t)) {
            type = NodeManager.ORDER_LIST;
        } else if ("search".equals(t)) {
            type = NodeManager.ORDER_SEARCH;
        } else {
            throw new JspTagException("Unknown field order type " + type);
        }
    }

    public NodeProvider getNodeProvider() {
        return nodeProvider;
    }

    /**
    *
    **/
    public int doStartTag() throws JspTagException{

        NodeManager nodeManager;
        
        if (nodeManagerString == null) { // living as NodeReferrer
            nodeProvider= findNodeProvider();
            nodeManager = nodeProvider.getNodeVar().getNodeManager();
        } else {
            nodeManager = getCloudProviderVar().getNodeManager(nodeManagerString);
        }

        FieldList fieldList = nodeManager.getFields(type);            
        listSize = fieldList.size();
        returnValues = fieldList.fieldIterator();

        //this is where we do the search       
        currentItemIndex= -1;  // reset index

        // if we get a result from the query
        // evaluate the body , else skip the body
        if (returnValues.hasNext())            
            return EVAL_BODY_TAG;
        return SKIP_BODY;
    }
        
    public int doAfterBody() throws JspTagException {
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
            currentField = returnValues.nextField();
        }
    }
}


/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;

import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.BodyContent;

import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* NodeFiedTag provides direct access to fields of a node.
*the tag does this by getting the default cloud and getting the Node by number/alias 
* @author Kees Jongenburger
*/
public class NodeFieldTag extends CloudReferrerTag {
    
    private static Logger log = Logging.getLoggerInstance(NodeFieldTag.class.getName());
    
    private String number=null;
    private String field=null;

        /*  michiel: this stuff was in the original NodeTag, zo it now
            belongs here. 

        if(field!=null) {
        String value = "";
        value = node.getStringValue(field);
        if(value == null) {
        value = "mm:node number="+number+" hasn't got field "+field;
        }
        
        }
        
        
        if(action!=null) {
        if(action.toLowerCase().equals("countrelations")) {
        if(type==null) {
        value = ""+node.countRelations();
        } else {
        value = ""+node.countRelations(type);
        }
        }
        if(action.toLowerCase().equals("countrelatednodes")) {
        if(type==null) {
        value = ""+node.getRelatedNodes().size();
        } else {
        //keesj:should there be a specific call in the MMCI
        value = ""+node.countRelatedNodes(type);
        }
        }
        }
        
        //pageContext.getOut().print(retval);
        try {
        bodyOut.clearBody();
        bodyOut.print(value);
        bodyOut.writeOut(bodyOut.getEnclosingWriter());
        } catch (java.io.IOException e) {
        }
        */
    
    
    public void setNumber(String number){
        this.number=number;    
    }
    
    public void setField(String field) {
        this.field = field;
    }
    
    public int doStartTag() throws JspTagException{            
        return EVAL_BODY_TAG;
    }
    
    
    public int doAfterBody() throws JspTagException {
        try {
            Node node = getDefaultCloud().getNode(number);
            //bodyOut.clearBody();
            //BodyContent bodyOut = getBodyContent();
            bodyContent.print(node.getStringValue(field));
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
        } catch (IOException ioe){
            throw new JspTagException(ioe.toString());
        }
        return SKIP_BODY;
    }
}

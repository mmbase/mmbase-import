/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.mmbase.module.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.bridge.*;

/**
 * My first taglib code, so don't be angry :-)
 * @author Rob Vermeulen
 */
public class MMNode extends MMTaglib implements BodyTag {
    
    private static boolean debug = true;
    private String number=null;
    private String action=null;
    private String field=null;
    private String type=null;
    
    public void setNumber(String number){
		this.number=number;	
    }
    
    public void setAction(String action){
		this.action=action;	
    }

    public void setField(String field){
		this.field=field;	
    }

    public void setType(String type){
		this.type=type;	
    }

    public int doStartTag() throws JspException{
		//System.out.println("doStartTag");
		return EVAL_BODY_TAG;
    }
    
	/**
	 * this method writes the content of the body back to the jsp page
	 **/
    public int doAfterBody() throws JspException {
		String value = "";
	
		try {
	    	//content is the content of the body
	    	String content = bodyOut.getString();

	    	// node is the node with which you want to fiddle
			Node node = null;
			try {
				node = getDefaultCloud().getNode(Integer.parseInt(number));
			} catch (NumberFormatException e){
				node = getDefaultCloud().getNodeByAlias(number);
			}
			if(node==null) {
				value = "mm:node number="+number+", Cannot find Node with number "+number;
	    		bodyOut.clearBody();
	    		bodyOut.print(value);
	    		bodyOut.writeOut(bodyOut.getEnclosingWriter());
        		return SKIP_BODY;
			}
			

			if(field!=null) {
				value = node.getStringValue(field);
				if(value==null) {
					value = "mm:node number="+number+" hasn't got field "+field;
				}
				
			}

			if(action!=null) {
				if(action.toLowerCase().equals("countrelations")) {
					if(type==null) {
						value = ""+node.countAllRelations();
					} else {
						value = ""+node.countRelations(type);
					}
				}
				if(action.toLowerCase().equals("countrelatednodes")) {
					if(type==null) {
						value = ""+node.getAllRelatedNodes().size();
					} else {
						//keesj:should there be a specific call in the MMCI
						value = ""+node.countRelatedNodes(type);
					}
				}
			}
	    
	    	//pageContext.getOut().print(retval);
	    	bodyOut.clearBody();
	    	bodyOut.print(value);
	    	bodyOut.writeOut(bodyOut.getEnclosingWriter());
		} catch (Exception e) {
			System.out.println("EXCEPTION");
		}
        return SKIP_BODY;
    }
}

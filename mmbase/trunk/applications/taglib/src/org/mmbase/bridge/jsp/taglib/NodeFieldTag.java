/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import javax.servlet.jsp.tagext.BodyTag;
import javax.servlet.jsp.tagext.Tag;

import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* NodeFiedTag provides direct access to fields of a node.
*the tag does this by getting the default cloud and getting the Node by number/alias 
* @author Kees Jongenburger
*/
public class NodeFieldTag extends BaseTag implements BodyTag{
	
	private static Logger log = Logging.getLoggerInstance(NodeFieldTag.class.getName());
	
	private String number=null;
	private String field=null;
	
	public void setNumber(String number){
		this.number=number;    
	}
	
	public void setField(String field) {
		this.field = field;
	}
	
	public int doStartTag() throws JspException{            
		return EVAL_BODY_TAG;
	}
	
	
	public int doAfterBody() throws JspException {
		try {
			Node node = getDefaultCloud().getNode(number);
			//bodyOut.clearBody();
			bodyOut.print(node.getStringValue(field));
			bodyOut.writeOut(bodyOut.getEnclosingWriter());
		} catch (IOException ioe){
			throw new JspException(ioe.toString());
		}
		return SKIP_BODY;
	}
}

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
* MMNode provides the fields of a node 
* @author Rob Vermeulen
*/
public class NodeTag extends AbstractNodeProviderTag implements BodyTag {
	
	private static Logger log = Logging.getLoggerInstance(NodeTag.class.getName());
	
	private String number=null;
	private String type=null;
	
	public void setNumber(String number){
		this.number=number;    
	}
	
	public void setParameter(String param) {
		this.number = pageContext.getRequest().getParameter(param);
	}
	
	
	public int doStartTag() throws JspException{            
		Node node;
		
		if (number != null) { 
			// explicity indicated which node (by number or alias)
			node = getDefaultCloud().getNode(number);
		} else { 
			// michiel: class must be of 'BodyTagSupport' type for this. I don't know yet how to do that.
			// get the node from a parent element.
			//NodeLikeTag nodeLike = 
			//    (NodeLikeTag) findAncestorWithClass((Tag)this,
			//                                        Class.forName("org.mmbase.bridge.jsp.taglib.NodeLikeTag"));
			
			// keesj like this?
			node = findNodeProvider(this,null).getNodeVar();
		}
		
		//keesj
		//FIXME does not make sence
		if(node == null) {
			throw new JspException("Cannot find Node with number " + number);
		}         
		setNodeVar(node);        
		log.debug("found node " + node.getValue("gui()"));
		
		//System.out.println("doStartTag");
		return EVAL_BODY_TAG; // should perhaps give a SKIP_BODY if 'field' is given.
	}
	
	public void doInitBody() throws JspException {       
		fillVars();	
	} 
	
	
	
	/**
	* this method writes the content of the body back to the jsp page
	**/
	public int doAfterBody() throws JspException {
		
		/*                                   f
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
		try {
			bodyOut.writeOut(bodyOut.getEnclosingWriter());
		} catch (IOException ioe){
			throw new JspException(ioe.toString());
		}
		return SKIP_BODY;
	}
}

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

import org.mmbase.bridge.*;

/**
 * experimental code(does no use MMCI)
 * @author Kees Jongenburger
 */
public class MMMod extends MMTaglib implements BodyTag {
    
    public static boolean debug = true;
    private String name=null;
    private String value=null;

    public void setName(String name){
	this.name = name;
    }

    public void setValue(String value){
	this.value=value;
    }
    
    
    public int doStartTag() throws JspException{
	return EVAL_BODY_TAG;
    }
    
     /**
      * this method writes the content of the body back to the jsp page
      * it wil only be used if the value wasn't found in the database
      **/
    public int doAfterBody() throws JspException {
	try {
	    //content is the content of the body
	    String content = bodyOut.getString();

	    if (value!=null) content=value;

	    //pi is the module we whant to use
	    Module mod= getDefaultCloudContext().getModule(name);
	
	    String retval = mod.getInfo(content,pageContext.getRequest(),pageContext.getResponse());
	    
	    //pageContext.getOut().print(retval);
	    bodyOut.clearBody();
	    bodyOut.print(retval);
	    bodyOut.writeOut(bodyOut.getEnclosingWriter());
	} catch (IOException ioe){
	    throw new JspTagException("IOException " + ioe.getMessage());
	}
        return SKIP_BODY;
    }
}








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

/**
 * experimental code(does no use MMCI)
 * @author Kees Jongenburger
 */
public class MMMod extends MMTaglib implements BodyTag {
    
    public static boolean debug = true;
    private String name=null;
    private String value=null;

    public ProcessorInterface getProcessor(String name) throws MMTaglibException{
	ProcessorInterface processor= null;
	Object o =Module.getModule(name); 
	if (o != null && o instanceof ProcessorInterface){
	    processor=(ProcessorInterface)o;
	} else {
	    if (o != null){
		throw new MMTaglibException("Module " + name + " is not a Prosessor , it does not implements the ProcessorInterface");
	    } else {
		throw new MMTaglibException("unalbe to get module  " + name + " maybe it does not exists");
	    }
	}
	return processor;
    }
    
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
	    ProcessorInterface pi = getProcessor(name);
	    String retval = pi.replace(getScanPage(),content);
	    
	    //pageContext.getOut().print(retval);
	    bodyOut.clearBody();
	    bodyOut.print(retval);
	    bodyOut.writeOut(bodyOut.getEnclosingWriter());
	} catch (MMTaglibException taglibException){
	    throw new JspTagException(taglibException.toString());
	} catch (IOException ioe){
	    throw new JspTagException("IOException " + ioe.getMessage());
	}
	
        return SKIP_BODY;
    }
    
    private scanpage getScanPage() throws MMTaglibException {
	if (! (pageContext.getRequest() instanceof HttpServletRequest)){
	    throw new MMTaglibException("the mod commmand only works on HTTP(the request is not an instance of HttpServletRequest)");
	}
	scanpage sp = new scanpage();
	sp.setReq((HttpServletRequest)pageContext.getRequest());
	sp.setRes((HttpServletResponse)pageContext.getResponse());
	sp.req_line=((HttpServletRequest)pageContext.getRequest()).getRequestURI();
	sp.querystring=((HttpServletRequest)pageContext.getRequest()).getQueryString();
	return sp;
    }
}








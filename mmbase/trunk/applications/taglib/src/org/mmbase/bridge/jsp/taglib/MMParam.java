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

/**
 * @author Daniel Ockeloen
 */
public class MMParam extends MMTaglib implements Tag {
    
    public static boolean debug = true;
    private String name=null;
    private String number=null;

    public void setName(String name){
	this.name = name;
    }

    public void setNumber(String number){
	this.number = number;
    }

    public int doEndTag() throws javax.servlet.jsp.JspTagException {
	HttpServletRequest req=(HttpServletRequest)pageContext.getRequest();
	String result=req.getParameter(name);

	try {
            pageContext.getOut().write(result);
         } catch(java.io.IOException e) {
            throw new JspTagException("IO Error: "+ e.getMessage()); 
         }
         return EVAL_PAGE;
    }
    
    
    
}








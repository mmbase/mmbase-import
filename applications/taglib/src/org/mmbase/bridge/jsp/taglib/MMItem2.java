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
import org.mmbase.bridge.*;
import org.mmbase.util.*;

/**
 * @author Daniel Ockeloen
 */
public class MMItem2 extends TagSupport implements Tag {
    
    String name="item2";

    public int doEndTag() throws javax.servlet.jsp.JspTagException {
	String result=(String)pageContext.getAttribute(name);
	try {
            pageContext.getOut().write(result);
         } catch(java.io.IOException e) {
            throw new JspTagException("IO Error: "+ e.getMessage()); 
         }
         return EVAL_PAGE;
    }
    
    
    
}

/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.mmbase.bridge.*;

/**
 * Calls 'doInfo' from NodeManager.
 * @author Michiel Meeuwissen
 */
public class InfoTag extends MMTaglib implements BodyTag {
    
    public static boolean debug = true;
    private String nodeManager = null;
    private String module      = null;

    public void setNodeManager(String nm){
	nodeManager = nm;
    }
    public void setModule(String nm){
	module = nm;
    }

    public int doStartTag() throws JspException{
	return EVAL_BODY_TAG;
    }
    
     /**
      *
      **/
    public int doAfterBody() throws JspException {
	try {            
	    //command is in the body of the tag
	    String command = bodyOut.getString();
            String result;
            
            if (nodeManager != null) {
                if (module != null) {
                    throw new JspTagException("Cannot give both module and nodemanager");
                }
                result = getDefaultCloud().getNodeManager(nodeManager).getInfo(command,
                                                                               pageContext.getRequest(),
                                                                               pageContext.getResponse());
            } else if (module != null) {
                result = getDefaultCloudContext().getModule(module).getInfo(command,
                                                                               pageContext.getRequest(),
                                                                               pageContext.getResponse());
            } else {
                throw new JspTagException("Must give module or nodemanager");
            }
            	    
	    //pageContext.getOut().print(retval);
	    bodyOut.clearBody();
	    bodyOut.print(result);
	    bodyOut.writeOut(bodyOut.getEnclosingWriter());
	} catch (java.io.IOException ioe){
	    throw new JspTagException("IOException " + ioe.getMessage());
	}
        return SKIP_BODY;
    }
}








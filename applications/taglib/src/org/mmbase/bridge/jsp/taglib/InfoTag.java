/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

//import javax.servlet.*;
//import javax.servlet.http.*;
import javax.servlet.jsp.JspTagException;
//import javax.servlet.jsp.tagext.*;

import org.mmbase.bridge.*;

/**
* Calls 'doInfo' from NodeManager or from Module.
*
* @author Michiel Meeuwissen
*/
public class InfoTag extends  CloudReferrerTag {
    
    public static boolean debug = true;
    private String nodeManager = null;
    private String module      = null;
    
    public void setNodeManager(String nm){
        nodeManager = nm;
    }
    public void setModule(String nm){
        module = nm;
    }
    
    public int doStartTag() throws JspTagException{
        return EVAL_BODY_TAG;
    }
    
    /**
    * implementation of TagExtraInfo return values declared here
    * should be filled at one point, currently fillVars is responsible for
    * that ant gets called before every
    *
    public VariableInfo[] getVariableInfo(TagData data){
        VariableInfo[] variableInfo = new VariableInfo[1];;
        
        String id = "";
        if (data.getAttribute("id") != null){
            id = "" + data.getAttribute("id");
        }
        
        variableInfo[0] = new VariableInfo(id,
            "java.lang.String",
            true,
            VariableInfo.AT_END);
        return variableInfo;
    }
    // tag does not define TEI, now, I think it is need though...

    */
    
    /**
    *
    **/
    public int doAfterBody() throws JspTagException {
        
        //command is in the body of the tag
        String command = bodyContent.getString();
        String result;
        
        if (nodeManager != null) {
            if (module != null) {
                throw new JspTagException("Cannot give both module and nodemanager");
            }
            result = getDefaultCloud().getNodeManager(nodeManager).getInfo(command,
                                                                           pageContext.getRequest(),
                pageContext.getResponse());
        } else if (module != null) {
            result = findCloudTag().getDefaultCloudContext().getModule(module).getInfo(command,
                pageContext.getRequest(),
                pageContext.getResponse());
        } else {
            throw new JspTagException("Must give module or nodemanager");
        }
        
        //pageContext.getOut().print(retval);
        pageContext.setAttribute(getId(), result);
        return SKIP_BODY;        
    }

}

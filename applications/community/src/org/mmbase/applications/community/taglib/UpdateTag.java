/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.community;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTag;

import java.util.Hashtable;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Module;
import org.mmbase.bridge.Node;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.bridge.jsp.taglib.*;

/**
* Posts a message
*
* @author Pierre van Rooden
*/
public class UpdateTag extends AbstractNodeProviderTag implements BodyTag {

    private static Logger log = Logging.getLoggerInstance(UpdateTag.class.getName());

    private Module community=null;
    private String message;

    public void setMessage(String m) throws JspTagException {
        message=getAttributeValue(m);
    }

    public int doStartTag() throws JspTagException{
        // firstly, search the node:
        if (message == null) {
            throw new JspTagException("Field 'message' not specified");
        }
        community=getCloudContext().getModule("communityprc");
        // create a temporary message node that holds the new data
        Node node = new MessageNode(getCloud(),message);
        setNodeVar(node);
        return EVAL_BODY_TAG;
    }

    public void doInitBody() throws JspTagException {
    }

    /**
     * store the given value
     **/
    public int doAfterBody() throws JspTagException {
        Node node=getNodeVar();
        String body=node.getStringValue("body").trim();
        if (body.length()==0) {
            throw new JspTagException("Field 'body' not specified");
        }
        Hashtable params=new Hashtable();
        try {
            Cloud cloud=getCloud();
            params.put("CLOUD",cloud);
        } catch (JspTagException e) {}
        params.put("MESSAGE-BODY",body);
        String user=node.getStringValue("user");
        if (user.length()!=0) params.put("MESSAGE-CHATTER",user);
        String username=node.getStringValue("username");
        if (username.length()!=0) params.put("MESSAGE-CHATTERNAME",username);
        String subject=node.getStringValue("subject").trim();
        if (subject.length()!=0) params.put("MESSAGE-SUBJECT",subject);
        community.process("MESSAGE-UPDATE",message,params,
                          pageContext.getRequest(),pageContext.getResponse());
        Object err=params.get("MESSAGE-ERROR");
        if (err!=null) {
            throw new JspTagException("Post failed : "+err);
        }
        try {
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
        } catch (java.io.IOException ioe){
            throw new JspTagException(ioe.toString());
        }
        return SKIP_BODY;
    }
}

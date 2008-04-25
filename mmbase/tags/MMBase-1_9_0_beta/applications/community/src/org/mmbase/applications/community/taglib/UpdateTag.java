/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.community.taglib;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTag;

import java.util.Hashtable;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.Module;
import org.mmbase.bridge.Node;

import org.mmbase.bridge.jsp.taglib.*;

/**
 * Posts a message
 *
 * @author Pierre van Rooden
 * @version $Id: UpdateTag.java,v 1.12 2007-06-21 15:50:23 nklasens Exp $
 */
 
public class UpdateTag extends AbstractNodeProviderTag implements BodyTag {

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
        Node node = getCloudVar().getNodeManager("message").createNode();
        setNodeVar(node);
        return EVAL_BODY_BUFFERED;
    }

    public void doInitBody() throws JspTagException {
    }

    /**
     * store the given value
     **/
    public int doAfterBody() throws JspTagException {
        Node node=getNodeVar();
        String subject=node.getStringValue("subject").trim();
        String body=node.getStringValue("body").trim();
        String user=node.getStringValue("user");
        String username=node.getStringValue("username");
        node.cancel();

        if (body.length()==0) {
            throw new JspTagException("Field 'body' not specified");
        }
        Hashtable<String, Object> params=new Hashtable<String, Object>();
        try {
            Cloud cloud=getCloudVar();
            params.put("CLOUD",cloud);
        } catch (JspTagException e) {}
        params.put("MESSAGE-BODY",body);
        if (user.length()!=0) params.put("MESSAGE-CHATTER",user);
        if (username.length()!=0) params.put("MESSAGE-CHATTERNAME",username);
        if (subject.length()!=0) params.put("MESSAGE-SUBJECT",subject);
        community.process("MESSAGE-UPDATE",message,params,
                          pageContext.getRequest(),pageContext.getResponse());
        Object err=params.get("MESSAGE-ERROR");
        if (err!=null) {
            throw new JspTagException("Post failed : "+err);
        }
        if (bodyContent != null) {
            try {
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            } catch (java.io.IOException ioe){
                throw new JspTagException(ioe.toString());
            }
        }
        return SKIP_BODY;
    }
}

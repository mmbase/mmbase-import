/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.community;

import java.io.IOException;
import java.util.*;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTag;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* Who tag, provides functionality for listing users of a channel.
*
* @author Pierre van Rooden
**/
public class WhoTag extends AbstractNodeListTag {
    //this class is growing to big..
    private static Logger log = Logging.getLoggerInstance(WhoTag.class.getName());

    private String channel=null;
    private String type=null;

    protected Module community = null;

    public int getIndex() {
        return currentItemIndex;
    }

    public void setChannel(String channel) throws JspTagException {
        this.channel= getAttributeValue(channel);
    }

    public void setType(String type) throws JspTagException {
        this.type=getAttributeValue(type);
    }

    public int doStartTag() throws JspTagException {
        //this is where we do the seach
        // XXX: have to add some error checking too
        community=getCloudContext().getModule("communityprc");
        if (community==null) throw new JspTagException("Community module is not active");
        if (channel==null) throw new JspTagException("Attribute channel has not been specified");

        Hashtable params=new Hashtable();
        params.put("CHANNEL",channel);
        try {
            Cloud cloud=getCloudProviderVar();
            params.put("CLOUD",cloud);
        } catch (JspTagException e) {}

        if (sortedString!=null) params.put("SORTFIELDS",sortedString);
        if (directionString!=null) params.put("SORTDIRS",directionString);
        if (offset>0) params.put("FROMCOUNT",""+offset);
        if (max>-1) params.put("MAX",""+max);
        NodeList nodes = community.getList("WHO",params,pageContext.getRequest(),pageContext.getResponse());
        return setReturnValues(nodes,false);
    }
}


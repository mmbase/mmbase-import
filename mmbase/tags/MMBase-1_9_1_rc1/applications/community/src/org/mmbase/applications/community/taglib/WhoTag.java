/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.community.taglib;

import java.util.*;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;

/**
 * Who tag, provides functionality for listing users of a channel.
 *
 * @author Pierre van Rooden
 * @version $Id: WhoTag.java,v 1.13 2007-06-21 15:50:23 nklasens Exp $
 */
 
public class WhoTag extends AbstractNodeListTag {

    private String channel= null;

    protected Module community = null;

    public void setChannel(String channel) throws JspTagException {
        this.channel= getAttributeValue(channel);
    }

    public int doStartTag() throws JspTagException {
        //this is where we do the seach
        // XXX: have to add some error checking too
        community=getCloudContext().getModule("communityprc");
        if (community==null) throw new JspTagException("Community module is not active");
        if (channel==null) { // must be the surrounding node
            Node n = getNode();
            channel = n.getStringValue("number");
        }

        Hashtable<String, Object> params=new Hashtable<String, Object>();
        params.put("CHANNEL",channel);
        try {
            Cloud cloud=getCloudVar();
            params.put("CLOUD",cloud);
        } catch (JspTagException e) {}

        if (orderby != null)   params.put("SORTFIELDS",orderby);
        if (directions !=null) params.put("SORTDIRS",  directions);
        Attribute max    = listHelper.getMax();
        Attribute offset = listHelper.getOffset();
        if (offset != Attribute.NULL)        params.put("FROMCOUNT", "" + offset.getInt(this, 0));
        if (max    != Attribute.NULL)        params.put("MAX", max.getString(this));
        NodeList nodes = community.getList("WHO", params, pageContext.getRequest(), pageContext.getResponse());
        return setReturnValues(nodes,false);
    }
}
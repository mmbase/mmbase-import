/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.community;

import java.util.*;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * TreeTag, provides functionality for listing messages in community.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 **/
public class TreeTag extends AbstractNodeListTag {

   
    //this class is growing to big..
    private static Logger log = Logging.getLoggerInstance(TreeTag.class.getName());

    private Attribute thread   = Attribute.NULL;
    private Attribute fieldlist= Attribute.NULL;
    private Attribute  maxdepth= Attribute.NULL;
    private Attribute  startafternode = Attribute.NULL;
    private Attribute  startaftersequence = Attribute.NULL;
    private Attribute  type = Attribute.NULL;
    private Attribute  opentag = Attribute.NULL;
    private Attribute  closetag = Attribute.NULL;

    protected Module community = null;

    public void setThread(String thread) throws JspTagException {
        this.thread = getAttribute(thread);
    }

    public void setType(String type) throws JspTagException {  // not used
        this.type = getAttribute(type);
    }
    protected String getType() throws JspTagException {
        if (type == Attribute.NULL) return "message";
        return type.getString(this);
    }

    public void setFields(String fields) throws JspTagException {
        fieldlist = getAttribute(fields);
    }
    protected List getFields() throws JspTagException {
        List res = new Vector();
        StringTokenizer st = new StringTokenizer(fieldlist.getString(this), ",");
        while(st.hasMoreTokens()){
            res.add(st.nextToken().trim());
        }
        return res;
    }

    private void setDefaultFields() throws JspTagException {
        setFields("number,listhead,depth,listtail,subject,timestamp,replycount,info");
    }

    public void setMaxdepth(String maxdepth) throws JspTagException {
        this.maxdepth = getAttribute(maxdepth);
    }

    public void setStartafternode(String startafternode) throws JspTagException {
        this.startafternode = getAttribute(startafternode);
    }

    public void setStartaftersequence(String startaftersequence) throws JspTagException {
        this.startaftersequence=getAttribute(startaftersequence);
    }

    public void setOpentag(String tag) throws JspTagException {
        this.opentag=getAttribute(tag);
    }

    public void setClosetag(String tag) throws JspTagException {
        this.closetag=getAttribute(tag);
    }

    /**
     *
     */
    public int doStartTag() throws JspTagException {
        //this is where we do the seach
        community=getCloudContext().getModule("communityprc");
        if (thread==null) throw new JspTagException("Attribute thread has not been specified");
        Hashtable params=new Hashtable();
        params.put("NODE",thread);
        if (fieldlist==null) {
            setDefaultFields();
        }
        params.put("FIELDS",fieldlist);
        try {
            Cloud cloud=getCloud();
            params.put("CLOUD",cloud);
        } catch (JspTagException e) {}


        if (orderby != Attribute.NULL)     params.put("SORTFIELDS", orderby.getString(this));
        if (directions != Attribute.NULL)  params.put("SORTDIRS", directions.getString(this));
        if (maxdepth   != Attribute.NULL)  params.put("MAXDEPTH", "" + maxdepth.getString(this));
        if (offset != Attribute.NULL)     params.put("FROMCOUNT", "" + offset.getInt(this, 0));
        if (max     != Attribute.NULL)     params.put("MAXCOUNT", max.getString(this));
        if (startafternode != Attribute.NULL) {
            params.put("STARTAFTERNODE", startafternode.getString(this));
        } else if (startaftersequence != Attribute.NULL) {
            params.put("STARTAFTERSEQUENCE",startaftersequence.getString(this));
        }
        if (opentag!= Attribute.NULL) params.put("OPENTAG",opentag.getString(this));
        if (closetag!= Attribute.NULL) params.put("CLOSETAG",closetag.getString(this));
        NodeList nodes = community.getList("TREE",params,pageContext.getRequest(),pageContext.getResponse());
        
        return setReturnValues(nodes,false);
    }

}


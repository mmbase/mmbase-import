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
 * TreeTag, provides functionality for listing messages in community.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 **/
public class TreeTag extends AbstractNodeListTag {
    //this class is growing to big..
    private static Logger log = Logging.getLoggerInstance(TreeTag.class.getName());

    private String thread=null;
    private Vector fieldlist=null;
    private int maxdepth=-1;
    private int startafternode=-1;
    private int startaftersequence=-1;
    private String type="message";
    private String opentag=null;
    private String closetag=null;

    protected Module community = null;

    public void setThread(String thread) throws JspTagException {
        this.thread=getAttributeValue(thread);
    }

    public void setType(String type) {
        this.type=type;
    }

    public void setFields(String fields) {
        //super.setFields(fields);
        fieldlist= new Vector();
        StringTokenizer st = new StringTokenizer(fields, ",");
        while(st.hasMoreTokens()){
            fieldlist.addElement(st.nextToken().trim());
        }
    }

    private void setDefaultFields(){
        setFields("number,listhead,depth,listtail,subject,timestamp,replycount,info");
    }

    public void setMaxdepth(String maxdepth) throws JspTagException {
        String m = getAttributeValue(maxdepth);
        try {
            this.maxdepth=Integer.parseInt(m);
        } catch (NumberFormatException e) {
            throw new JspTagException ("Attribute maxdepth should be a number");
        }
    }

    public void setStartafternode(String startafternode) throws JspTagException {
        String s = getAttributeValue(startafternode);
        try {
            this.startafternode=Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new JspTagException ("Attribute startafternode should be a number");
        }
    }

    public void setStartaftersequence(String startaftersequence) throws JspTagException {
        String s = getAttributeValue(startaftersequence);
        try {
            this.startaftersequence=Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new JspTagException ("Attribute startaftersequence should be a number");
        }
    }

    public void setOpentag(String tag){
        this.opentag=tag;
    }

    public void setClosetag(String tag){
        this.closetag=tag;
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

        if (orderby != null)       params.put("SORTFIELDS", orderby);
        if (directions != null)    params.put("SORTDIRS", directions);
        if (maxdepth >- 1)         params.put("MAXDEPTH", "" + maxdepth);
        if (offset > 0)            params.put("FROMCOUNT", "" + offset);
        if (max > -1)              params.put("MAXCOUNT", "" + max);
        if (startafternode>-1) {
            params.put("STARTAFTERNODE",""+startafternode);
        } else if (startaftersequence>-1) {
            params.put("STARTAFTERSEQUENCE",""+startaftersequence);
        }
        if (opentag!=null) params.put("OPENTAG",opentag);
        if (closetag!=null) params.put("CLOSETAG",closetag);
        NodeList nodes = community.getList("TREE",params,pageContext.getRequest(),pageContext.getResponse());
        
        return setReturnValues(nodes,false);
    }

}


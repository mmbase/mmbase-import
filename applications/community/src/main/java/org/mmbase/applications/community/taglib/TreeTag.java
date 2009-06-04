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
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * TreeTag, provides functionality for listing messages in community.
 *
 * @author Pierre van Rooden
 * @author Michiel Meeuwissen
 * @version $Id$
 */
 
public class TreeTag extends AbstractNodeListTag {
   
    private static final Logger log = Logging.getLoggerInstance(TreeTag.class);

    private Attribute thread   = Attribute.NULL;
    private Attribute fieldList= Attribute.NULL;
    private Attribute  maxdepth= Attribute.NULL;
    private Attribute  startafternode = Attribute.NULL;
    private Attribute  startaftersequence = Attribute.NULL;
    private Attribute  type = Attribute.NULL;
    private Attribute  openTag = Attribute.NULL;
    private Attribute  closeTag = Attribute.NULL;

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
        fieldList = getAttribute(fields);
    }
    protected List<String> getFields(String fields) {
        List<String> res = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(fields, ",");
        while(st.hasMoreTokens()){
            res.add(st.nextToken().trim());
        }
        return res;
    }

    private List<String> getDefaultFields() {
        return getFields("number,listhead,depth,listtail,subject,timestamp,replycount,info");
    }

    public void setMaxdepth(String maxdepth) throws JspTagException {
        this.maxdepth = getAttribute(maxdepth);
    }

    public void setStartafternode(String startafternode) throws JspTagException {
        this.startafternode = getAttribute(startafternode);
    }

    public void setStartaftersequence(String startaftersequence) throws JspTagException {
        this.startaftersequence = getAttribute(startaftersequence);
    }

    public void setOpenTag(String tag) throws JspTagException {
        this.openTag = getAttribute(tag);
    }

    public void setCloseTag(String tag) throws JspTagException {
        this.closeTag = getAttribute(tag);
    }

    /**
     *
     */
    public int doStartTag() throws JspTagException {
      int superresult =  doStartTagHelper(); // the super-tag handles the use of referid...
        if (superresult != NOT_HANDLED) {
            return superresult;
        }

        if (thread == Attribute.NULL) throw new JspTagException("Attribute thread has not been specified");

        //this is where we do the seach
        community = getCloudContext().getModule("communityprc");
        Hashtable<String, Object> params = new Hashtable<String, Object>();
        params.put("NODE", thread.getString(this));

        List<String> fields;
        if (fieldList == Attribute.NULL) {
            fields = getDefaultFields();
        } else {
            fields = getFields(fieldList.getString(this));
        }
        params.put("FIELDS", new Vector<String>(fields));
        // if you don't supply Vector but the ArrayList, then it goes terrible wrong (without clear message).

        try {
            Cloud cloud = getCloudVar();
            params.put("CLOUD", cloud);
        } catch (JspTagException e) {
            log.debug(e.toString());
        }

        Attribute max    = listHelper.getMax();
        Attribute offset = listHelper.getOffset();
        if (orderby    != Attribute.NULL)  params.put("SORTFIELDS", orderby.getString(this));
        if (directions != Attribute.NULL)  params.put("SORTDIRS", directions.getString(this));
        if (maxdepth   != Attribute.NULL)  params.put("MAXDEPTH", maxdepth.getString(this));
        if (offset     != Attribute.NULL)  params.put("FROMCOUNT", "" + offset.getInt(this, 0));
        if (max        != Attribute.NULL)  params.put("MAXCOUNT", max.getString(this));
        if (startafternode != Attribute.NULL) {
            params.put("STARTAFTERNODE", startafternode.getString(this));
        } else if (startaftersequence != Attribute.NULL) {
            params.put("STARTAFTERSEQUENCE",startaftersequence.getString(this));
        }
        if (openTag  != Attribute.NULL) params.put("OPENTAG", openTag.getString(this));
        if (closeTag != Attribute.NULL) params.put("CLOSETAG", closeTag.getString(this));

        NodeList nodes = community.getList("TREE", params, 
                                           pageContext.getRequest(), 
                                           pageContext.getResponse());
        if (log.isDebugEnabled()) {
            log.debug("Found " + nodes + " with " + params);
        }
        return setReturnValues(nodes, false);
    }

}


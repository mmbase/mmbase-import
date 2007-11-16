/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;

import org.mmbase.bridge.jsp.taglib.util.Attribute;

import org.mmbase.bridge.Cloud;
import org.mmbase.framework.Framework;
import org.mmbase.util.functions.Parameters;

/**
 * Lives under a cloudprovider. Can give information about the node,
 * like what its name is.
 *
 * @author  Michiel Meeuwissen
 * @version $Id: CloudInfoTag.java,v 1.13 2007-11-16 16:23:37 michiel Exp $
 * @since   MMBase-1.8
 */

public class CloudInfoTag extends CloudReferrerTag implements Writer {

    private static final int TYPE_NAME                  = 0;
    private static final int TYPE_USER                  = 1;
    private static final int TYPE_RANK                  = 2;
    private static final int TYPE_RANK_INT              = 3;
    private static final int TYPE_AUTHENTICATE          = 4;
    private static final int TYPE_USERNODE              = 5;
    private static final int TYPE_MMBASEVERSION         = 100;
    private static final int TYPE_TAGLIBVERSION         = 101;


    private Attribute type = Attribute.NULL;

    public void setType(String tu) throws JspTagException {
        type = getAttribute(tu);
    }

    private int getType() throws JspTagException {
        String t = type.getString(this).toLowerCase();
        if ("name".equals(t)) {
            return TYPE_NAME;
        } else if ("user".equals(t)) {
            return TYPE_USER;
        } else if ("usernode".equals(t)) {
            return TYPE_USERNODE;
        } else if ("rank".equals(t)) {
            return TYPE_RANK;
        } else if ("rankint".equals(t)) {
            return TYPE_RANK_INT;
        } else if ("authenticate".equals(t)) {
            return TYPE_AUTHENTICATE;
        } else if ("mmbaseversion".equals(t)) {
            return TYPE_MMBASEVERSION;
        } else if ("taglibversion".equals(t)) {
            return TYPE_TAGLIBVERSION;
        } else {
            throw new JspTagException("Unknown value for attribute type (" + t + ")");
        }
    }

    public int doStartTag() throws JspTagException{

        Cloud cloud = getCloudVar();

        Object show;

        // set node if necessary:
        switch(getType()) {
        case TYPE_NAME:
            show = cloud.getName();
            break;
        case TYPE_USER:
            show = cloud.getUser().getIdentifier();
            break;
        case TYPE_USERNODE:
            Framework fw = Framework.getInstance();
            Parameters frameworkParams = fw.createParameters();
            fillStandardParameters(frameworkParams);
            frameworkParams.setAutoCasting(true);
            show = fw.getUserNode(frameworkParams);
            break;
        case TYPE_RANK:
            show = cloud.getUser().getRank().toString();
            break;
        case TYPE_RANK_INT:
            show = "" + cloud.getUser().getRank().getInt();
            break;
        case TYPE_AUTHENTICATE:
            show = "" + cloud.getUser().getAuthenticationType();
            break;
        case TYPE_MMBASEVERSION:
            show = org.mmbase.Version.get();
            break;
        case TYPE_TAGLIBVERSION:
            show = getTaglibVersion();
            break;
        default:
            show = "";
        }

        helper.setValue(show);
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), helper.getValue());
        }
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }

    /**
     * Write the value of the nodeinfo.
     */
    public int doEndTag() throws JspTagException {
        helper.doEndTag();
        return super.doEndTag();

    }
}

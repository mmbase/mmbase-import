/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;

import java.util.Vector;
import java.util.StringTokenizer;
import org.mmbase.bridge.Node;

/**
 * Produces a url to 'img.db'. Using this tag makes your pages more
 * portable to other system, and hopefully less sensitive for future
 * changes in how the image servlet works.
 *
 * @author Michiel Meeuwissen
 **/

public class ImageTag extends FieldTag {

    private String template = null;

    /**
     * The transformation template
     */

    public void setTemplate(String t) throws JspTagException {
        template = getAttributeValue(t);
    }

    public int doStartTag() throws JspTagException {
        node = null;
        Node node = getNodeVar();
        if (node.getNodeManager().getField("handle") == null) {
            throw new JspTagException("Found parent node does not have 'handle' field, therefore cannot be a image. Perhaps you have the wrong node, perhaps you'd have to use the 'node' attribute?");
        }

        // some servlet implementation's 'init' cannot determin this theirselves, help them a little:
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        String context = req.getContextPath();

        /* perhaps 'getSessionName' should be added to CloudProvider
         * EXPERIMENTAL 
         */
        String sessionName = "cloud_mmbase";
        CloudTag ct = null;
        ct = (CloudTag) findParentTag("org.mmbase.bridge.jsp.taglib.CloudTag", null, false);
        if (ct != null) {
            sessionName = ct.getSessionName();
        }        


        String url;
        if (template == null) {
            // the image itself
            url = node.getStringValue("servletpath(" + sessionName + ",number," + context + ")");
        } else {
            // the cached image
            url = node.getStringValue("servletpath(" + sessionName + ",cache(" + template + ")," + context + ")");
        }
        helper.setValue(url);
        helper.setPageContext(pageContext);
        helper.setJspvar();
        if (getId() != null) {
            getContextTag().register(getId(), helper.getValue());
        }
        return EVAL_BODY_BUFFERED;
    }
}


/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import java.io.File;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;

import org.mmbase.util.Arguments;
import org.mmbase.util.UriParser;
import org.mmbase.module.builders.AbstractServletBuilder;
import org.mmbase.module.builders.Images;

import org.mmbase.security.Rank;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Produces an url to the image servlet mapping. Using this tag makes
 * your pages more portable to other system, and hopefully less
 * sensitive for future changes in how the image servlet works.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ImageTag.java,v 1.36 2003-06-06 10:03:07 pierre Exp $ 
 */

public class ImageTag extends FieldTag {

    private static Logger log = Logging.getLoggerInstance(ImageTag.class.getName());
    private Attribute template = Attribute.NULL;

    /**
     * The transformation template
     */

    public void setTemplate(String t) throws JspTagException {
        template = getAttribute(t);
    }

    public int doStartTag() throws JspTagException {
        node = null;
        getNodeVar();
        if (!node.getNodeManager().hasField("handle")) {
            throw new JspTagException("Found parent node '" + node.getNumber() + "' of type " + node.getNodeManager().getName() + " does not have 'handle' field, therefore cannot be a image. Perhaps you have the wrong node, perhaps you'd have to use the 'node' attribute?");
        }

        // some servlet implementation's 'init' cannot determin this theirselves, help them a little:
        HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
        String context = req.getContextPath();

        /* perhaps 'getSessionName' should be added to CloudProvider
         */
        String sessionName = "";

        if(! getCloud().getUser().getRank().equals(Rank.ANONYMOUS.toString())) {
            sessionName = "cloud_mmbase";
            CloudTag ct = null;
            ct = (CloudTag) findParentTag(CloudTag.class, null, false);
            if (ct != null) {
                sessionName = ct.getSessionName();
            }
        }

        String number;
        String t = template.getString(this);
        if ("".equals(t)) {
            // the node/image itself
            number = node.getStringValue("number");
        } else {
            // the cached image
            number = node.getFunctionValue("cache", new Arguments(Images.CACHE_ARGUMENTS).set("template", t)).toString();
        }


        String servletPath;
        {
            Arguments args = new Arguments(AbstractServletBuilder.SERVLETPATH_ARGUMENTS)
                .set("session",  sessionName)
                .set("context",  UriParser.makeRelative(new File(req.getServletPath()).getParent(), "/"))
                .set("argument", number)
                ;
            servletPath = node.getFunctionValue("servletpath", args).toString();
        }
        helper.setTag(this);
        helper.setValue(((HttpServletResponse) pageContext.getResponse()).encodeURL(servletPath));
        if (getId() != null) {
            getContextProvider().getContainer().register(getId(), helper.getValue());
        }
        return EVAL_BODY_BUFFERED;
    }
}


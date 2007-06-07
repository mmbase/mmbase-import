/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */
package org.mmbase.bridge.jsp.taglib.pageflow;
import java.util.ArrayList;
import java.util.Map;

import org.mmbase.bridge.jsp.taglib.TaglibException;
import org.mmbase.bridge.jsp.taglib.pageflow.UrlTag.UrlParameters;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.util.Notfound;
import javax.servlet.jsp.JspTagException;

import org.mmbase.util.Casting;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * Like IncludeTag, but an entire tree of files is being probed to find the one
 * that has the most specified value.
 *
 * This is a taglib-implementation of the 'LEAFPART' command
 * A full description of this command can be found in the mmbase-taglib.xml file.
 *
 * @author Johannes Verelst
 * @version $Id: LeafIncludeTag.java,v 1.22 2007-06-07 13:23:24 michiel Exp $
 */

public class LeafIncludeTag extends IncludeTag {

    private static final Logger log = Logging.getLoggerInstance(LeafIncludeTag.class);
   
    protected Attribute objectList = Attribute.NULL;
    private TreeHelper th = new TreeHelper();

    public int doStartTag() throws JspTagException {
        log.debug("starttag " + getId());
        extraParameters = new ArrayList<Map.Entry<String, Object>>();
        parameters = new UrlParameters(this);
        helper.useEscaper(false);
        th.setCloud(getCloudVar());
        th.setBackwardsCompatible(! "false".equals(pageContext.getServletContext().getInitParameter("mmbase.taglib.smartpath_backwards_compatible")));
        
        try {
            if (referid != Attribute.NULL) {
                if (page != Attribute.NULL || component != Attribute.NULL) throw new TaglibException("Cannot specify both 'referid' and 'page' attributes");
                
                Object o = getObject(getReferid());
                if (o instanceof Url) {
                    Url u = (Url) getObject(getReferid());
                    extraParameters.addAll(u.params);
                    url = new Url(this, u, parameters, true);
                } else {
                    url = new Url(this, th.findLeafFile(Casting.toString(o), objectList.getValue(this).toString(), pageContext.getSession()), getComponent(), parameters, true);
                }
            } else {
                String leafPage = th.findLeafFile(getPage(), objectList.getValue(this).toString(), pageContext.getSession());
                if (log.isDebugEnabled()) {
                    log.debug("Found " + leafPage + " from " + getPage());
                }
                if ("".equals(leafPage)) {
                    throw new TaglibException("Could not find leaf-page '" + getPage() + "'");
                }
                url = new Url(this, leafPage , getComponent(), parameters, true);
            }
        } catch (java.io.IOException ioe) {
            throw new TaglibException(ioe);
        }

        if (getId() != null) {
            parameters.getWrapped(); // dereference this
            getContextProvider().getContextContainer().register(getId(), url); 
        }

        url.setLegacy();
        return EVAL_BODY_BUFFERED;
    }

    public void doFinally() {
        th.doFinally();
        super.doFinally();
    }

    public void setObjectlist(String p) throws JspTagException {
        objectList = getAttribute(p);
    }

    // override to cancel
    protected boolean doMakeRelative() {
    	log.debug("doMakeRelative() overridden!");
        return false;
    }
}

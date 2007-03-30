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
 * @version $Id: LeafIncludeTag.java,v 1.18 2007-03-30 14:40:55 johannes Exp $
 */

public class LeafIncludeTag extends IncludeTag {

    private static final Logger log = Logging.getLoggerInstance(LeafIncludeTag.class);
    private static  final Logger pageLog = Logging.getLoggerInstance(Logging.PAGE_CATEGORY);

    protected Attribute objectList = Attribute.NULL;
    private TreeHelper th = new TreeHelper();

    public int doStartTag() throws JspTagException {
        pageLog.info("leafinclude starttag:" + getPage());
        pageLog.debug("starttag " + getId());
        extraParameters = new ArrayList<Map.Entry<String, Object>>();
        parameters = new UrlParameters(this);
        helper.useEscaper(false);
        th.setCloud(getCloudVar());
        
        if (referid != Attribute.NULL) {
            if (page != Attribute.NULL || component != Attribute.NULL) throw new TaglibException("Cannot specify both 'referid' and 'page' attributes");

            Object o = getObject(getReferid());
            if (o instanceof Url) {
                Url u = (Url) getObject(getReferid());
                extraParameters.addAll(u.params);
                pageLog.service("Creating url from referid");
                url = new Url(this, u, parameters, true);
            } else {
                pageLog.service("Creating url from referid: " + Casting.toString(o));
                url = new Url(this, th.findLeafFile(Casting.toString(o), objectList.getValue(this).toString(), pageContext.getSession()), getComponent(), parameters, true);
            }
        } else {
            String leafPage = th.findLeafFile(getPage(), objectList.getValue(this).toString(), pageContext.getSession());
            pageLog.service("Creating url: [" + getPage() + "] = [" + leafPage + "]");
            url = new Url(this, leafPage , getComponent(), parameters, true);
        }

        if (getId() != null) {
            parameters.getWrapped(); // dereference this
            getContextProvider().getContextContainer().register(getId(), url); 
        }

        url.setLegacy();
        pageLog.info("leafinclude end of starttag:" + url.toString());
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

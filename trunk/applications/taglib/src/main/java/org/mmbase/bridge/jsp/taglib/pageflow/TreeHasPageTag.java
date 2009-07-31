/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.pageflow;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.*;
import java.io.*;
import javax.servlet.jsp.JspTagException;
import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.8.6
 */

public class TreeHasPageTag extends CloudReferrerTag implements Condition {

    private static  final Logger log = Logging.getLoggerInstance(TreeHasPageTag.class);

    protected Attribute  page                 = Attribute.NULL;
    public void setPage(String p) throws JspTagException {
        page = getAttribute(p);
    }
    protected Attribute inverse = Attribute.NULL;

    public void setInverse(String b) throws JspTagException {
        inverse = getAttribute(b);
    }
    protected boolean getInverse() throws JspTagException {
        return inverse.getBoolean(this, false);
    }
    protected Attribute objectList = Attribute.NULL;

    public void setObjectlist(String o) throws JspTagException {
        objectList = getAttribute(o);
    }

    protected final TreeHelper th = new TreeHelper();

    public int doStartTag() throws JspTagException {
        if (page == Attribute.NULL) {
            throw new JspTagException("Attribute 'page' was not specified");
        }
        if (objectList == Attribute.NULL) {
            throw new JspTagException("Attribute 'objectlist' was not specified");
        }
        th.setCloud(getCloudVar());
        th.setBackwardsCompatible(! "false".equals(pageContext.getServletContext().getInitParameter("mmbase.taglib.smartpath_backwards_compatible")));
        try {
            String treePage = th.findTreeFile(page.getString(this), objectList.getString(this), pageContext.getSession());
            log.debug("Testing " + treePage);
            boolean has =  treePage != null && ! "".equals(treePage) && ResourceLoader.getWebRoot().getResource(treePage).openConnection().getDoInput();
            if (! has) {
                String resource = HasPageTag.getResource(pageContext, page.getString(this));
                has = ResourceLoader.getWebRoot().getResource(resource).openConnection().getDoInput();
            }
            if (has != getInverse()){
                return EVAL_BODY;
            } else {
                return SKIP_BODY;
            }
        } catch (IOException ioe) {
            log.error(ioe);
            return SKIP_BODY;
        }
    }

    public int doAfterBody() throws JspTagException {
        if (EVAL_BODY == EVAL_BODY_BUFFERED) { // not needed if EVAL_BODY_INCLUDE
            if (bodyContent != null) {
                try{
                    if(bodyContent != null) {
                        bodyContent.writeOut(bodyContent.getEnclosingWriter());
                    }
                } catch(java.io.IOException e){
                    throw new TaglibException(e);
                }
            }
        }
        return SKIP_BODY;
    }
}

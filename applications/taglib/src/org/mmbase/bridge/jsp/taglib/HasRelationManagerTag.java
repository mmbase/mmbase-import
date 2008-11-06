/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.Condition;
import org.mmbase.bridge.*;

import javax.servlet.jsp.JspTagException;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;



/**
 * Straight-forward wrapper arround {@link org.mmbase.bridge.Cloud#hasRelationManager}.
 *
 * @author Michiel Meeuwissen
 * @version $Id: HasRelationManagerTag.java,v 1.3 2008-11-06 17:39:25 michiel Exp $
 * @since MMBase-1.8
 */

public class HasRelationManagerTag extends CloudReferrerTag implements Condition {
    private static final Logger log = Logging.getLoggerInstance(HasRelationManagerTag.class);
    protected Attribute inverse            = Attribute.NULL;
    protected Attribute sourceManager      = Attribute.NULL;
    protected Attribute destinationManager = Attribute.NULL;
    protected Attribute role               = Attribute.NULL;

    public void setInverse(String b) throws JspTagException {
        inverse = getAttribute(b);
    }
    public void setSourcemanager(String n) throws JspTagException {
        sourceManager = getAttribute(n);
    }
    public void setDestinationmanager(String n) throws JspTagException {
        destinationManager = getAttribute(n);
    }
    public void setRole(String n) throws JspTagException {
        role = getAttribute(n);
    }


    protected boolean getInverse() throws JspTagException {
        return inverse.getBoolean(this, false);
    }

    protected NodeManager getNodeManager(Cloud cloud, Attribute a) throws JspTagException {
        Object id = a.getValue(this);
        if ("".equals(id)) id = null;
        if (id == null) {
            return cloud.getNodeManager("object");
        } else {

            if (id instanceof String) {
                String sid = (String) id;
                if (! cloud.hasNodeManager(sid) && org.mmbase.datatypes.StringDataType.INTEGER_PATTERN.matcher(sid).matches()) {
                    id = cloud.getNode(sid);
                } else {
                    return cloud.getNodeManager(sid);
                }
            }
            if (id instanceof NodeManager) {
                return (NodeManager) id;
            } else if (id instanceof Node) {
                if (log.isDebugEnabled()) {
                    log.debug("Taking nodemanager of node " + id);
                }
                return ((Node) id).getNodeManager();
            } else {
                return cloud.getNodeManager(org.mmbase.util.Casting.toString(id));
            }
        }
    }


    public int doStartTag() throws JspTagException {
        Cloud cloud = getCloudVar();
        if (cloud.hasRelationManager(getNodeManager(cloud, sourceManager),
                                     getNodeManager(cloud, destinationManager), role.getString(this)) != getInverse()) {
            return EVAL_BODY;
        } else {
            return SKIP_BODY;
        }
    }
    public int doAfterBody() throws JspTagException {
        if (EVAL_BODY == EVAL_BODY_BUFFERED) { // not needed if EVAL_BODY_INCLUDE
            try{
                if(bodyContent != null) {
                    bodyContent.writeOut(bodyContent.getEnclosingWriter());
                }
            } catch(java.io.IOException e){
                throw new JspTagException("IO Error: " + e.getMessage());
            }
        }
        return SKIP_BODY;
    }
}

/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import javax.servlet.jsp.*;

import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.util.logging.*;

/**
 * The CountRelationsTag can be used as a child of a 'NodeProvider' tag to
 * show the number of relations the node has.
 *
 * @author Jaco de Groot
 * @author Michiel Meeuwissen
 * @version $Id$
 */

public class CountRelationsTag extends NodeReferrerTag implements Writer {

    private static final Logger log   = Logging.getLoggerInstance(CountRelationsTag.class);
    private Attribute type      = Attribute.NULL;
    private Attribute searchDir = Attribute.NULL;
    private Attribute role      = Attribute.NULL;

    /**
     * Set the type of related nodes wich should be counted. If not set all
     * related nodes are counted.
     */
    public void setType(String type) throws JspTagException {
        this.type = getAttribute(type);
    }

    public void setSearchdir(String s) throws JspTagException {
        searchDir = getAttribute(s);
    }

    public void setRole(String r) throws JspTagException {
        role = getAttribute(r);
    }

    public int doStartTag() throws JspException {
        super.doStartTag();
        if (getReferid() != null) {
            helper.setValue(getContextProvider().getContextContainer().getObject(getReferid()));
        } else {
            log.debug("Search the node.");
            Node node = getNode();
            Cloud cloud = node.getCloud();
            NodeManager other =
                type == Attribute.NULL ?
                cloud.getNodeManager("object") :
                cloud.getNodeManager(type.getString(this));
            String direction = (String) searchDir.getValue(this);
            if (direction == null) direction = "BOTH";
            String r = (String) role.getValue(this);
            if ("".equals(r)) r = null;
            helper.setValue(node.countRelatedNodes(other, r, direction));
        }
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), helper.getValue());
        }
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }

    public int doEndTag() throws JspTagException {
        helper.doEndTag();
        return super.doEndTag();

    }
}

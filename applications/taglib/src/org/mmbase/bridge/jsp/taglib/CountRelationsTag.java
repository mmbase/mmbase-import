/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import org.mmbase.bridge.jsp.taglib.util.Attribute;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The CountRelationsTag can be used as a child of a 'NodeProvider' tag to
 * show the number of relations the node has.
 *
 * @author Jaco de Groot
 * @author Michiel Meeuwissen
 */
public class CountRelationsTag extends NodeReferrerTag implements Writer {

    private static Logger log = Logging.getLoggerInstance(CountRelationsTag.class.getName());
    private Attribute type = Attribute.NULL;

    /**
     * Set the type of related nodes wich should be counted. If not set all
     * related nodes are counted.
     */
    public void setType(String type) throws JspTagException {
        this.type = getAttribute(type);
    }

    public int doStartTag() throws JspTagException {
        helper.setTag(this);
        if (getReferid() != null) {
            helper.setValue(getContextProvider().getContainer().getObject(getReferid()));
        } else {
            log.debug("Search the node.");
            Node node = getNode();
            if (type == Attribute.NULL) {
                helper.setValue(new Integer(node.countRelations()));
            } else {
                helper.setValue(new Integer(node.countRelatedNodes(type.getString(this))));
            }
        }
        if (getId() != null) {
            getContextProvider().getContainer().register(getId(), helper.getValue());
        }
        return EVAL_BODY_BUFFERED;
    }

    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }

    public int doEndTag() throws JspTagException {
        return helper.doEndTag();
    }
}

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
import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The CountRelationsTag can be used as a child of a 'NodeProvider' tag to
 * show the number of relations the node has.
 *
 * @author Jaco de Groot
 * @author Michiel Meeuwissen
 * @version $Id: CountRelationsTag.java,v 1.15 2003-08-06 19:43:06 michiel Exp $ 
 */

public class CountRelationsTag extends NodeReferrerTag implements Writer {

    private static Logger log   = Logging.getLoggerInstance(CountRelationsTag.class);
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

    public int doStartTag() throws JspTagException {
        helper.setTag(this);
        if (getReferid() != null) {
            helper.setValue(getContextProvider().getContainer().getObject(getReferid()));
        } else {
            log.debug("Search the node.");
            Node node = getNode();
            NodeManager other = type      == Attribute.NULL ? getCloud().getNodeManager("object") : getCloud().getNodeManager(type.getString(this));
            int search = RelationStep.DIRECTIONS_BOTH;
            if (searchDir != Attribute.NULL) {
                String string = searchDir.getString(this).toLowerCase();
                for (search = 0; search < RelationStep.DIRECTIONALITY_NAMES.length; search ++) {
                    if (string.equals(RelationStep.DIRECTIONALITY_NAMES[search])) break;
                }
            }
            helper.setValue(new Integer(node.countRelatedNodes(other, (String) role.getValue(this), search)));
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

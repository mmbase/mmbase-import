/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.Node;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The CountRelationsTag can be used as a child of a 'NodeProvider' tag to
 * show the number of relations the node has.
 * 
 * @author Jaco de Groot
 */
public class CountRelationsTag extends NodeReferrerTag {
    private static Logger log = Logging.getLoggerInstance(CountRelationsTag.class.getName());
    protected Node node;
    private String type;   

    /**
     * Set the type of related nodes wich should be counted. If not set all
     * related nodes are counted.
     */
    public void setType(String type) throws JspTagException {
        this.type = getAttributeValue(type);
    }

    public int doStartTag() throws JspTagException{
        return EVAL_BODY_TAG;
    }

    public int doAfterBody() throws JspTagException {
        log.debug("Search the node.");
        node = getNode();
        if (node == null) {
            throw new JspTagException ("Did not find node in the parent node provider");
        }
        try {         
            log.debug("Check if the type attribute is set.");
            if (type == null) {
                bodyContent.print(node.countRelations());
            } else {
                bodyContent.print(node.countRelatedNodes(type));
            }
            bodyContent.writeOut(bodyContent.getEnclosingWriter());
        } catch (java.io.IOException e) {
            throw new JspTagException (e.toString());            
        }
        return SKIP_BODY;
    }
}

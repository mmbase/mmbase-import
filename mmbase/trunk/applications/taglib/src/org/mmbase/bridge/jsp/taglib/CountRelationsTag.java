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
 * @author Michiel Meeuwissen
 */
public class CountRelationsTag extends NodeReferrerTag implements Writer {

    // Writer implementation:
    protected WriterHelper helper = new WriterHelper();
    public void setVartype(String t) throws JspTagException { 
        helper.setVartype(t);
    }
    public void setJspvar(String j) {
        helper.setJspvar(j);
    }
    public void setWrite(String w) throws JspTagException {
        helper.setWrite(getAttributeBoolean(w));
    }
    public Object getWriterValue() {
        return helper.getValue();
    }

    private static Logger log = Logging.getLoggerInstance(CountRelationsTag.class.getName());
    private String type;   

    /**
     * Set the type of related nodes wich should be counted. If not set all
     * related nodes are counted.
     */
    public void setType(String type) throws JspTagException {
        this.type = getAttributeValue(type);
    }

    public int doStartTag() throws JspTagException {
        if (getReferid() != null) {
            helper.setValue(getContext().getObject(getReferid()));
        } else {
            log.debug("Search the node.");
            Node node = getNode();
            if (type == null) {
                helper.setValue(new Integer(node.countRelations())); 
            } else {
            helper.setValue(new Integer(node.countRelatedNodes(type)));
            }        
        }
        helper.setJspvar(pageContext);  
        if (getId() != null) {
            getContextTag().register(getId(), helper.getValue());
        }
        return EVAL_BODY_TAG;
    }

    public int doAfterBody() throws JspTagException {
        helper.setBodyContent(bodyContent);
        return helper.doAfterBody();
    }
}

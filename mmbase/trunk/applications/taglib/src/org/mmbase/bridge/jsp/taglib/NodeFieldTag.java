/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.Node;

import javax.servlet.jsp.JspTagException;

/**
 * This is a field tag that does not need a Node tag around it,
 * because it does provide the attribute 'number' itself.
 *
 * @author Kees Jongenburger
 * @author Michiel Meeuwissen
 */

public class NodeFieldTag extends FieldTag {

    private String number = null;

    /**
     * @deprecated use setName
     */
    public void setField(String f) {
        // dammit, why is this attribute not named 'name' as in FieldTag?
        try {
            setName(f);
        } catch (JspTagException e) {
            throw new RuntimeException(e.toString());
        }
    }
    public void setNumber(String number) throws JspTagException {
        this.number = getAttributeValue(number);    
    }
    
    public Node getNodeVar() throws JspTagException {
        return getCloudProviderVar().getNode(number);
    }
}

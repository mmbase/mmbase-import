/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;
import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
/**
 * Interface designed to make it possible for child tags
 * to access a field defined in a tag.
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public interface FieldProvider extends TagIdentifier {
    // Since a field cannot exist alone, it must also implement NodeProvider.
    // To get the value of a field, you always need a Node too. A 'Field' is only
    // a description.
    /**
     * @return the field contained in the tag
     *
     */
    public Field getFieldVar() throws JspTagException;

    public Node getNodeVar() throws JspTagException;


    
}

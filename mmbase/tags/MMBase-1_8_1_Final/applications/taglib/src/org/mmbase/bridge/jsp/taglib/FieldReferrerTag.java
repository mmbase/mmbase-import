/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Field;

/**
 * A fieldreferrer tag is a tag which needs (or can use) a 'field' to
 * operate on. The most evident example is the FieldInfo tag. 
 *
 * Field itself is a FieldReferrer too (it can reuse another one)
 *
 * @author Michiel Meeuwissen
 * @see    FieldInfoTag
 * @version $Id: FieldReferrerTag.java,v 1.9 2005-07-20 14:57:55 michiel Exp $ 
 */

public abstract class FieldReferrerTag extends NodeReferrerTag {	

    private Attribute parentFieldId = Attribute.NULL;

    public void setField(String field) throws JspTagException {
        parentFieldId = getAttribute(field);
    }

    /**
    * This method tries to find an ancestor object of type NodeProvider
    * @return the FieldProvider if found else an exception.
    *
    */	
    public FieldProvider findFieldProvider() throws JspTagException {        
        FieldProvider fp =  (FieldProvider) findParentTag(FieldProvider.class, (String) parentFieldId.getValue(this));
        if (fp instanceof Writer) {
            ((Writer) fp).haveBody();
        }
        return fp;
    }


    protected Field getField() throws JspTagException {
        return findFieldProvider().getFieldVar();
    }

   
}

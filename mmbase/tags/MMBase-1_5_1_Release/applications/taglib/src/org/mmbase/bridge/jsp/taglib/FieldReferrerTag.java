/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.JspTagException;

import org.mmbase.bridge.Field;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;


/**
 *
 * @author Michiel Meeuwissen 
 *
 */

public abstract class FieldReferrerTag extends NodeReferrerTag {	

    private static Logger log = Logging.getLoggerInstance(FieldReferrerTag.class.getName()); 

    private String parentFieldId = null;
    //private NodeProvider nodeProvider = null;

    /**
     **/

    public void setField(String field){
        parentFieldId = field;
    }

    /**
    * This method tries to find an ancestor object of type NodeProvider
    * @return the FieldProvider if found else an exception.
    *
    */	
    public FieldProvider findFieldProvider() throws JspTagException {        
        return (FieldProvider) findParentTag("org.mmbase.bridge.jsp.taglib.FieldProvider", parentFieldId);
    }


    protected Field getField() throws JspTagException {
        return findFieldProvider().getFieldVar();
    }

}

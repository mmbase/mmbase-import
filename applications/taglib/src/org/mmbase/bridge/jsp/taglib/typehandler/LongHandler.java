/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Node;
import org.mmbase.bridge.jsp.taglib.FieldInfoTag;


/**
 * @javadoc
 *
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: LongHandler.java,v 1.3 2003-06-06 10:03:38 pierre Exp $
 */

public class LongHandler extends AbstractTypeHandler {

    private DateHandler dateHandler;

    /**
     * Constructor for LongHandler.
     * @param context
     */
    public LongHandler(FieldInfoTag context) {
        super(context);
        dateHandler = new DateHandler(context);
    }
    
    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    public String htmlInput(Node node, Field field, boolean search) throws JspTagException {        
        if (field.getGUIType().equals("eventtime")) {
            return dateHandler.htmlInput(node, field, search);
        } 
        return super.htmlInput(node, field, search);
    }

    /**
     * @see TypeHandler#useHtmlInput(Node, Field)
     */
    public String useHtmlInput(Node node, Field field) throws JspTagException {               
        if (field.getGUIType().equals("eventtime")) {
            return dateHandler.useHtmlInput(node, field);
        } 

        return super.useHtmlInput(node, field);
    }

    /**
     * @see TypeHandler#whereHtmlInput(Field)
     */
    public String whereHtmlInput(Field field) throws JspTagException {
        if (field.getGUIType().equals("eventtime")) {
            return dateHandler.whereHtmlInput(field);
        } 
        return super.whereHtmlInput(field);
    }       

}

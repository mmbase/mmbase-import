/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.*;
import org.mmbase.bridge.jsp.taglib.FieldInfoTag;
import org.mmbase.bridge.jsp.taglib.ParamHandler;
import org.mmbase.storage.search.Constraint;


/**
 * @javadoc
 *
 * @author Gerard van de Looi
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: LongHandler.java,v 1.8 2004-05-26 21:58:21 michiel Exp $
 */

public class LongHandler extends AbstractTypeHandler {

    private DateHandler dateHandler;

    /**
     * Constructor for LongHandler.
     * @param tag
     */
    public LongHandler(FieldInfoTag tag) {
        super(tag);
        dateHandler = new DateHandler(tag);
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
    public boolean useHtmlInput(Node node, Field field) throws JspTagException {               
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

    public Constraint whereHtmlInput(Field field, Query query) throws JspTagException {
        if (field.getGUIType().equals("eventtime")) {
            return dateHandler.whereHtmlInput(field, query);
        } 
        return super.whereHtmlInput(field, query);
    }       

    public void paramHtmlInput(ParamHandler handler, Field field) throws JspTagException  {
        String guiType = field.getGUIType();      
        if (guiType.equals("eventtime")) {
            dateHandler.paramHtmlInput(handler, field);
        } else {
            super.paramHtmlInput(handler, field);
        }
    }


}

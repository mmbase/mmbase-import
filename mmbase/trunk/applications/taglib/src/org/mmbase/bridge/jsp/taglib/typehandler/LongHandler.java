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
 * @version $Id: LongHandler.java,v 1.11 2006-02-09 13:53:00 michiel Exp $
 */

public class LongHandler extends AbstractTypeHandler {

    private DateHandler dateHandler;
    private DurationHandler durationHandler;

    /**
     * Constructor for LongHandler.
     * @param tag
     */
    public LongHandler(FieldInfoTag tag) {
        super(tag);
        dateHandler = new DateHandler(tag);
        durationHandler = new DurationHandler(tag);
    }

    /**
     * @see TypeHandler#htmlInput(Node, Field, boolean)
     */
    public String htmlInput(Node node, Field field, boolean search) throws JspTagException {
        String gui = field.getGUIType();
        if (gui.equals("eventtime")) {
            return dateHandler.htmlInput(node, field, search);
        } else if (gui.equals("relativetime")) {
            return durationHandler.htmlInput(node, field, search);
        } else {
            return super.htmlInput(node, field, search);
        }
    }

    /**
     * @see TypeHandler#useHtmlInput(Node, Field)
     */
    public boolean useHtmlInput(Node node, Field field) throws JspTagException {
        String gui = field.getGUIType();
        if (gui.equals("eventtime")) {
            return dateHandler.useHtmlInput(node, field);
        } else if (gui.equals("relativetime")) {
            return durationHandler.useHtmlInput(node, field);
        } else {
            return super.useHtmlInput(node, field);
        }
    }

    /**
     * @see TypeHandler#whereHtmlInput(Field)
     */
    public String whereHtmlInput(Field field) throws JspTagException {
        String gui = field.getGUIType();
        if (gui.equals("eventtime")) {
            return dateHandler.whereHtmlInput(field);
        } else if (gui.equals("relativetime")) {
            return durationHandler.whereHtmlInput(field);
        } else {
            return super.whereHtmlInput(field);
        }
    }

    public Constraint whereHtmlInput(Field field, Query query) throws JspTagException {
        String gui = field.getGUIType();
        if (gui.equals("eventtime")) {
            return dateHandler.whereHtmlInput(field, query);
        } else if (gui.equals("relativetime")) {
            return durationHandler.whereHtmlInput(field, query);
        } else {
            return super.whereHtmlInput(field, query);
        }
    }

    public void paramHtmlInput(ParamHandler handler, Field field) throws JspTagException  {
        String gui = field.getGUIType();
        if (gui.equals("eventtime")) {
            dateHandler.paramHtmlInput(handler, field);
        } else if (gui.equals("relativetime")) {
            durationHandler.paramHtmlInput(handler, field);
        } else {
            super.paramHtmlInput(handler, field);
        }
    }

    protected Object cast(Object value, Node node, Field field) {
        if (value == null || "".equals(value)) return "";
        return  super.cast(value, node, field);
    }



}

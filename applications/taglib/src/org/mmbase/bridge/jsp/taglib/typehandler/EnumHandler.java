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

import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This handler can be used to create option list by use of a resource.
 * 
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: EnumHandler.java,v 1.10 2003-08-05 09:07:00 michiel Exp $
 */

public class EnumHandler extends AbstractTypeHandler implements TypeHandler {

    private static Logger log = Logging.getLoggerInstance(EnumHandler.class);
    private ResourceBundle bundle;
    private boolean available;
    /**
     * @param context
     */
    public EnumHandler(FieldInfoTag context, String enumType) throws JspTagException {
        super(context);
        try {
            Class.forName(enumType);
        } catch (Exception ee) {
            try {
                String resource;            
                if (enumType.indexOf('.') == -1 ) {
                    resource = "org.mmbase.bridge.jsp.taglib.typehandler.resources." + enumType;
                } else {
                    resource = enumType;
                    
                }
                bundle    = ResourceBundle.getBundle(resource, context.getCloud().getLocale(), getClass().getClassLoader());
                available = true;
            } catch (java.util.MissingResourceException e) {
                log.warn(e.toString());
                available = false;
            }
        }
    }

    public boolean isAvailable() {
        return available;
    }


    public String htmlInput(Node node, Field field, boolean search) throws JspTagException {
        StringBuffer buffer = new StringBuffer();
        Map enumValues = new TreeMap(); 
        Enumeration e = bundle.getKeys();
        while (e.hasMoreElements()) {
            String propertyKey = (String) e.nextElement();
            Integer key = new Integer(propertyKey);
            enumValues.put(key, bundle.getString(propertyKey));
        }

        buffer.append("<select name=\"");
        buffer.append(prefix(field.getName()));
        buffer.append("\">\n");
        int value = 0;
        if (node != null) {
            value = node.getIntValue(field.getName());
        }

        for(Iterator i = enumValues.keySet().iterator(); i.hasNext(); ) { 
            Integer key = (Integer) i.next();
            buffer.append("<option value=\"");
            buffer.append(key);
            buffer.append("\"");
            if ((node != null) && (key.intValue() == value)) {
                buffer.append(" selected=\"selected\"");
            } else if (search) {
                try {
                    int searchi = Integer.parseInt( (String) context.getContextProvider().getContainer().find(context.getPageContext(), prefix(field.getName())));
                    if (searchi == key.intValue()) {
                        buffer.append(" selected=\"selected\"");
                    }
                } catch (NumberFormatException nfe) {
                    // never mind. perhaps was not yet present in post --> java.lang.NumberFormatException: null
                }
            }
            buffer.append(">");
            buffer.append(enumValues.get(key));
            buffer.append("</option>\n");
        }
        buffer.append("</select>");
        if (search) {
            String name = prefix(field.getName()) + "_search";
            String searchi =  (String) context.getContextProvider().getContainer().find(context.getPageContext(), name);
            buffer.append("<input type=\"checkbox\" name=\"");
            buffer.append(name);            
            buffer.append("\" ");
            if (searchi != null) {
                buffer.append(" checked=\"checked\"");
            }
            buffer.append(" />\n");
        }
        return buffer.toString();        
    }


    /**
     * @see TypeHandler#whereHtmlInput(Field)
     */
    public String whereHtmlInput(Field field) throws JspTagException {
        String fieldName = field.getName();
        String id = prefix(fieldName + "_search");
        if ( (String) context.getContextProvider().getContainer().find(context.getPageContext(), id) == null) {
            return "";
        } else {
            return super.whereHtmlInput(field);
        }
    }        


    public void whereHtmlInput(Field field, Query query) throws JspTagException {
        String fieldName = field.getName();
        String id = prefix(fieldName + "_search");
        if ( (String) context.getContextProvider().getContainer().find(context.getPageContext(), id) == null) {
        } else {
            super.whereHtmlInput(field, query);
        }
    }        



}

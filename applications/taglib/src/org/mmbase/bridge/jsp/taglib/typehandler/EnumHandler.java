/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.jsp.taglib.typehandler;

import javax.servlet.jsp.JspTagException;
import org.mmbase.bridge.*;
import org.mmbase.storage.search.Constraint;
import org.mmbase.bridge.jsp.taglib.FieldInfoTag;

import java.util.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This handler can be used to create option list by use of a resource.
 * 
 * @author Michiel Meeuwissen
 * @since  MMBase-1.6
 * @version $Id: EnumHandler.java,v 1.17 2004-02-17 18:07:03 michiel Exp $
 */

public class EnumHandler extends AbstractTypeHandler implements TypeHandler {

    private static final Logger log = Logging.getLoggerInstance(EnumHandler.class);
    private ResourceBundle bundle;
    private boolean available;
    /**
     * @param tag
     */
    public EnumHandler(FieldInfoTag tag,  String enumType) throws JspTagException {
        super(tag);
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
                bundle    = ResourceBundle.getBundle(resource, tag.getCloud().getLocale(), getClass().getClassLoader());
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
            enumValues.put(propertyKey, bundle.getString(propertyKey));
        }

        buffer.append("<select name=\"");
        buffer.append(prefix(field.getName()));
        buffer.append("\" ");
        addExtraAttributes(buffer);
        buffer.append(">\n");
        String value = "";
        if (node != null) {
            value = node.getStringValue(field.getName());
        }

        for(Iterator i = enumValues.keySet().iterator(); i.hasNext(); ) { 
            String key = (String) i.next();
            buffer.append("<option value=\"");
            buffer.append(key);
            buffer.append("\"");
            if ((node != null) && (key.equals(value))) {
                buffer.append(" selected=\"selected\"");
            } else if (search) {
                String searchs = (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), prefix(field.getName()));
                if (key.equals(searchs)) {
                    buffer.append(" selected=\"selected\"");
                }
            }
            buffer.append(">");
            buffer.append(enumValues.get(key));
            buffer.append("</option>\n");
        }
        buffer.append("</select>");
        if (search) {
            String name = prefix(field.getName()) + "_search";
            String searchi =  (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), name);
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
        if ( (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), id) == null) {
            return "";
        } else {
            return super.whereHtmlInput(field);
        }
    }        


    public Constraint whereHtmlInput(Field field, Query query) throws JspTagException {
        String fieldName = field.getName();
        String id = prefix(fieldName + "_search");
        if ( (String) tag.getContextProvider().getContextContainer().find(tag.getPageContext(), id) == null) {
            return null;
        } else {
            return super.whereHtmlInput(field, query);
        }
    }        



}

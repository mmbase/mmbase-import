/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.containers.*;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;

import java.util.Enumeration;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.Field;
import org.mmbase.bridge.Query;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.Arguments;
import org.mmbase.module.core.MMObjectBuilder;


import org.mmbase.bridge.jsp.taglib.typehandler.TypeHandler;
import org.mmbase.bridge.jsp.taglib.typehandler.DefaultTypeHandler;
import org.mmbase.util.XMLBasicReader;
import org.xml.sax.InputSource;
import org.w3c.dom.Element;


/**
 * The FieldInfoTag can be used as a child of a 'FieldProvider' to
 * provide info about the field or fieldtype.
 *
 * @author Michiel Meeuwissen
 * @author Jaco de Groot
 * @author Gerard van de Looi
 * @version $Id: FieldInfoTag.java,v 1.70 2003-11-19 16:57:41 michiel Exp $
 */

public class FieldInfoTag extends FieldReferrerTag implements Writer {
    private static Logger log;

    private static Class defaultHandler = DefaultTypeHandler.class;
    private static Class[] handlers;

    static {
        try {
            log = Logging.getLoggerInstance(FieldInfoTag.class);
            initializeTypeHandlers();
        } catch (Exception e) {
            log.error(e.toString());
        }
    }


    protected static final int TYPE_NAME     = 0;
    protected static final int TYPE_GUINAME  = 1;
    protected static final int TYPE_VALUE    = 2;
    protected static final int TYPE_GUIVALUE  = 3;
    protected static final int TYPE_TYPE      = 4;
    protected static final int TYPE_GUITYPE   = 5;
    protected static final int TYPE_DESCRIPTION = 6;

    protected static final int TYPE_UNSET     = 100;

    // input and useinput produces pieces of HTML
    // very handy if you're creating an editors, but well yes, not very elegant.
    protected static final int TYPE_INPUT    = 10;
    protected static final int TYPE_USEINPUT = 11;
    protected static final int TYPE_SEARCHINPUT = 12;
    protected static final int TYPE_USESEARCHINPUT = 13;
    protected static final int TYPE_REUSESEARCHINPUT = 14;


    private String sessionName = "cloud_mmbase";

    public String getSessionName() {
        return sessionName;
    }

    protected Attribute type = Attribute.NULL;
    public void setType(String t) throws JspTagException {
        type = getAttribute(t);
    }

    protected Attribute container  = Attribute.NULL; // not implemented


    // Must be protected because otherwise tomcat does not work
    // public would be defendable because typehandlers perhaps could need it.
    protected int getType() throws JspTagException {
        String t = type.getString(this).toLowerCase();
        if ("".equals(t)) {
            return TYPE_UNSET;
        } else if ("name".equals(t)) {
            return TYPE_NAME;
        } else if ("guiname".equals(t)) {
            return TYPE_GUINAME;
        } else if ("value".equals(t)) {
            return TYPE_VALUE;
        } else if ("guivalue".equals(t)) {
            return TYPE_GUIVALUE;
       } else if ("type".equals(t)) {
            return TYPE_TYPE;
       } else if ("guitype".equals(t)) {
            return TYPE_GUITYPE;
       } else if ("description".equals(t)) {
            return TYPE_DESCRIPTION;
        } else if ("input".equals(t)) {
            return TYPE_INPUT;
        } else if ("useinput".equals(t)) {
            return TYPE_USEINPUT;
        } else if ("searchinput".equals(t)) {
            return TYPE_SEARCHINPUT;
        } else if ("usesearchinput".equals(t)) {
            return TYPE_USESEARCHINPUT;
        } else if ("reusesearchinput".equals(t)) {
            return TYPE_REUSESEARCHINPUT;
        } else {
            throw new JspTagException("Unknown value for attribute type (" + t + ")");
        }
    }
    private Attribute options = Attribute.NULL;
    public void setOptions(String o) throws JspTagException {
        options = getAttribute(o);
    }

    public String getOptions() throws JspTagException {
        return (String) options.getValue(this);
    }

    /**
     * Answer the type handler for the given type.
     * The type handler is responsible for showing the html
     */
    protected TypeHandler getTypeHandler(int type) {
        Class handler;
        if ((type < 0) || (type >= handlers.length)) {
            log.warn("Could not find typehandler for type " + type + " using default");
            handler = getDefaultTypeHandler();
        } else { 
            handler = handlers[type];
            if (handler == null) {
                log.warn("Could not find typehandler for type " + type + " using default");
                handler = getDefaultTypeHandler();
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("using handler " + handler);
        }
        try {            
            return (TypeHandler)handler.getConstructor(new Class[]{FieldInfoTag.class}).newInstance(new Object[]{this});
        } catch (Exception e) {
            log.warn("Could not find typehandler for type " + type + " using default. Reason: " + e.toString() );
            return new DefaultTypeHandler(this);
        }
    }
    
    /**
     * Initialize the type handlers default supported by the system.
     */
    private static void initializeTypeHandlers() {
        log.service("Reading taglib field-handlers");
        handlers = new Class[org.mmbase.module.corebuilders.FieldDefs.TYPE_MAXVALUE + 1];

        Class thisClass = FieldInfoTag.class;
        InputSource fieldtypes = new InputSource(thisClass.getResourceAsStream("resources/fieldtypes.xml"));
        XMLBasicReader reader  = new XMLBasicReader(fieldtypes, thisClass);
        Element fieldtypesElement = reader.getElementByPath("fieldtypes");
        Enumeration e = reader.getChildElements(fieldtypesElement, "fieldtype");
        while (e.hasMoreElements()) {
            Element element = (Element) e.nextElement();
            String typeString = element.getAttribute("id");            
            int fieldType =  org.mmbase.module.corebuilders.FieldDefs.getDBTypeId(typeString);
            String claz = reader.getElementValue(reader.getElementByPath(element, "fieldtype.class"));
            try {
                log.debug("Adding field handler " + claz + " for type " + fieldType);
                handlers[fieldType] = Class.forName(claz);
            } catch (java.lang.ClassNotFoundException ex) {
                log.error("Class " + claz + " could not be found for type " + fieldType);
                handlers[fieldType] = defaultHandler;
            }
        }
    }
    
    /**
     * Set the type handler for the given type.
     */
    private static Class getDefaultTypeHandler() {
        return defaultHandler;
    }


    public int doStartTag() throws JspTagException{

        Node          node = null;
        FieldProvider fieldProvider = findFieldProvider();
        Field         field = ((FieldProvider) fieldProvider).getFieldVar();

        /* perhaps 'getSessionName' should be added to CloudProvider
         * EXPERIMENTAL
         */
        CloudTag ct = null;
        ct = (CloudTag) findParentTag(CloudTag.class, null, false);
        if (ct != null) {
            sessionName = ct.getSessionName();            
        }

        // found the field now. Now we can decide what must be shown:
        String show = null;

        int infoType = getType();

        if (log.isDebugEnabled()) {
            log.debug("infotype:" + type.getValue(this) + " -> " + infoType);
        }
        // set node if necessary:
        switch(infoType) {
        case TYPE_INPUT:
            if (node == null) { // try to find nodeProvider
                node = fieldProvider.getNodeVar();
            } // node can stay null.
            break;
            // these types do really need a NodeProvider somewhere:
            // so 'node' may not stay null.
        case TYPE_VALUE:
        case TYPE_GUIVALUE:
        case TYPE_USEINPUT:
            if (node == null) {
                node = fieldProvider.getNodeVar();
            }
            if (node == null) {
                throw new JspTagException("Could not find surrounding NodeProvider, which is needed for type=" + type);
            }
            break;
        default:
        }

        switch(infoType) {
        case TYPE_NAME:
            show = field.getName();
            break;
        case TYPE_GUINAME:
            show = field.getGUIName();
            break;
        case TYPE_VALUE:
            show = decode(node.getStringValue(field.getName()), node);
            break;
        case TYPE_GUIVALUE: {
            if (log.isDebugEnabled()) {
                log.debug("field " + field.getName() + " --> " + node.getStringValue(field.getName()));
            }

            Arguments args = new Arguments(MMObjectBuilder.GUI_ARGUMENTS);
            args.set("field",    field.getName());
            args.set("language", getCloud().getLocale().getLanguage());
            args.set("session",  sessionName);
            args.set("response", pageContext.getResponse());
            args.set("request",  pageContext.getRequest());
            show = decode(node.getFunctionValue("gui", args).toString(), node);
            if (show.trim().equals("")) {
                show = decode(node.getStringValue(field.getName()), node);
            }
            break;
        }
        case TYPE_INPUT:
            show = htmlInput(node, field, false);
            break;
        case TYPE_USEINPUT:
            show = useHtmlInput(node, field);
            fieldProvider.setModified();
            break;
        case TYPE_SEARCHINPUT:
            show = htmlInput(node, field, true);
            break;
        case TYPE_USESEARCHINPUT: {
            NodeListContainer c = (NodeListContainer) findParentTag(NodeListContainer.class, (String) container.getValue(this), false);
            if (c == null) { // produce a String to use in a constraint attribute of a list (legacy)
                log.debug("creating string constraint");
                show = whereHtmlInput(field);
            } else {
                Query query = c.getQuery();
                if (log.isDebugEnabled()) {
                    log.debug("Using " + query);
                } 
                whereHtmlInput(field, query);
                show = "";
            }

            break;
        }
        case TYPE_REUSESEARCHINPUT: {
            paramHtmlInput((ParamHandler) findParentTag(ParamHandler.class, null), field);
            show = "";
            break;
        }
        case TYPE_TYPE:
            show = "" + field.getType();
            break;
        case TYPE_GUITYPE:
            show = field.getGUIType();
            break;
        case TYPE_DESCRIPTION:
            show = field.getDescription();
            break;
        }
        
        helper.useEscaper(false); // fieldinfo typicaly produces xhtml
        helper.setValue(show);
        if (getId() != null) {
            getContextProvider().getContextContainer().register(getId(), helper.getValue());
        }
        return EVAL_BODY_BUFFERED;
    }



    /**
     * Creates a form entry.
     * @param node for this node.
     * @param field and this field.
     */

    private String htmlInput(Node node, Field field, boolean search) throws JspTagException {
        if (log.isDebugEnabled()) {
            String value = "<search>";
            if (! search) {
                if (node == null) {
                    value = "<create>";
                } else {
                    value = node.getStringValue(field.getName());
                }
            }
            log.debug("field " + field.getName() + " gui type: " + field.getGUIType() + "  value: " + value);
        }
        return getTypeHandler(field.getType()).htmlInput(node, field, search);
    }


    /**
     * Applies a form entry.
     */

    private String useHtmlInput(Node node, Field field) throws JspTagException {
        return getTypeHandler(field.getType()).useHtmlInput(node, field);
    }


    /**
     * If you use a form entry to search, then you can use this functions to create the where part.
     * @param field and this field.
     */
    private String whereHtmlInput(Field field) throws JspTagException {
        return getTypeHandler(field.getType()).whereHtmlInput(field);
    }

    private void  paramHtmlInput(ParamHandler handler, Field field) throws JspTagException {
         getTypeHandler(field.getType()).paramHtmlInput(handler, field);
    }


    private void  whereHtmlInput(Field field, Query query) throws JspTagException {
        getTypeHandler(field.getType()).whereHtmlInput(field, query);
    }


    /**
     * Write the value of the fieldinfo.
     */
    public int doEndTag() throws JspTagException {
        return helper.doEndTag();
    }

    public int doAfterBody() throws JspException {
        return helper.doAfterBody();
    }


    /**
     * decode and encode can be overriden.
     */

    public String decode (String value, org.mmbase.bridge.Node n) throws JspTagException {
        return value;
    }

    public String encode(String value, Field f) throws JspTagException {
        return value;
    }


}

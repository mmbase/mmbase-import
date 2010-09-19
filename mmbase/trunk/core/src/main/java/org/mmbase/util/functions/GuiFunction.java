/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import org.mmbase.bridge.*;
import org.mmbase.datatypes.DataType;
import java.util.*;
import java.text.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The gui function of MMObjectBuilder
 *
 * @author Michiel Meeuwissen
 * @version $Id$
 * @since MMBase-1.9
 */
public class GuiFunction extends org.mmbase.module.core.MMObjectNodeFunction<String> {
    private static final long serialVersionUID = 0L;
    private static final Logger log = Logging.getLoggerInstance(GuiFunction.class);
    public static final String GUI_INDICATOR = "no info";
    public static final Parameter<?>[] PARAMETERS = {
        Parameter.FIELD,
        Parameter.LANGUAGE,
        new Parameter<String>("session", String.class),
        Parameter.RESPONSE,
        Parameter.REQUEST,
        Parameter.LOCALE,
        new Parameter<String>("stringvalue", String.class)
        //new Parameter("length", Integer.class),
        //       field, language, session, response, request) Returns a (XHTML) gui representation of the node (if field is '') or of a certain field. It can take into consideration a http session variable name with loging information and a language");

    };

    public GuiFunction() {
        super("gui", PARAMETERS);
    }


    /**
     * Bridge version of {@link MMObjectBuilder#getGUIIndicator(String, Node)
     * @todo testing
     */
    protected String getGUIIndicator(String fieldName, Node node) {
        Field field = node.getNodeManager().getField(fieldName);

        if (field != null && field.getType() == Field.TYPE_NODE && ! fieldName.equals("number")) {
            try {
                Node otherNode = node.getNodeValue(fieldName);
                if (otherNode == null) {
                    return "";
                } else {
                    String rtn = otherNode.getFunctionValue("gui", null).toString();
                    return rtn;
                }
            } catch (RuntimeException rte) {
                log.warn("Cannot load node from field " + fieldName +" in node " + node.getNumber() + ":" +rte);
                return "invalid";
            }
        } else {
            return null;
        }
    }

    /**
     * Bridge version of {@link MMObjectBuilder#getNodeGUIIndicator(Node, Parameters)
     * @todo testing
     */
    protected String getNodeGUIIndicator(Node node, Parameters params) {
        // do the best we can because this method was not implemented
        // we get the first field in the object and try to make it
        // to a string we can return
        List<Field> list = node.getNodeManager().getFields(NodeManager.ORDER_LIST);
        if (list.size() > 0) {
            String fname = list.get(0).getName();
            String str = node.getStringValue( fname );
            if (str.length() > 128) {
                str =  str.substring(0, 128) + "...";
            }
            if (params == null) {
                // Needed for getGuiIndicator calls for NODE fields
                // Temporary fix, should perhaps be solved in getGuiIndicator(node,params)
                String result = getGUIIndicator(fname, node);
                if (result == null) {
                    result = str;
                }
                return result;
            } else {
                params.set("field", fname);
                params.set("stringvalue", str);
                return getGUIIndicator(node, params);
            }
        } else {
            return "no info";
        }
    }


    /**
     * Bridge version of {@link MMObjectBuilder#getGUIIndicator(Node, Parameters)
     * @todo testing
     */
    protected String getGUIIndicator(Node node, Parameters pars) {
        Locale locale   = pars.get(Parameter.LOCALE);
        String language = pars.get(Parameter.LANGUAGE);
        if (locale == null) {
            if (language != null) {
                locale = new Locale(language, "");
            }
        } else {
            if (language != null && (! locale.getLanguage().equals(language))) { // odd, but well,
                locale = new Locale(language, locale.getCountry());
            }
        }
        if (locale == null) locale = node.getCloud().getLocale();

        if (log.isDebugEnabled()) {
            log.debug("language " + locale.getLanguage() + " country " + locale.getCountry());
        }

        String rtn;
        String field = pars.getString(Parameter.FIELD);
        Field fdef = node.getNodeManager().getField(field);

        Object returnValue;
        if (fdef != null) {
            // test if the value can be derived from the enumerationlist of a datatype
            DataType dataType = fdef.getDataType();
            if (dataType instanceof org.mmbase.datatypes.BinaryDataType) {
                log.debug("Getting from " + node + " size for field " + field);
                returnValue = node.isNull(field) ? "" : "" + node.getSize(field) + " byte";
            } else {
                try {
                    returnValue = dataType.getEnumerationValue(locale, pars.get(Parameter.CLOUD), pars.get(Parameter.NODE), fdef, node.getStringValue(field));
                } catch (Exception e) {
                    returnValue = node.getStringValue(field);
                    log.warn("For " + fdef + " (" + returnValue + ") " + e.getMessage(), e);

                }
            }
        } else {
            returnValue = null;
        }
        if (returnValue != null) {
            rtn = returnValue.toString();
        } else {
            if (fdef != null && ("eventtime".equals(fdef.getGUIType()) ||
                                 fdef.getDataType() instanceof org.mmbase.datatypes.DateTimeDataType)) { // do something reasonable for this
                Date date;
                if (fdef.getType() == Field.TYPE_DATETIME) {
                    date = node.getDateValue(field);
                } else {
                    date = new Date(node.getLongValue(field) * 1000);
                }
                rtn = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM, locale).format(date);
                Calendar calendar = new GregorianCalendar(locale);
                calendar.setTime(date);
                if (calendar.get(Calendar.ERA) == GregorianCalendar.BC) {
                    java.text.DateFormat df = new java.text.SimpleDateFormat(" G", locale);
                    rtn += df.format(date);
                }
            } else {
                rtn = (String) pars.get("stringvalue");
                if (rtn == null) {
                    rtn = node.getStringValue(field);
                }
            }
        }
        rtn = org.mmbase.util.transformers.Xml.XMLEscape(rtn);
        return rtn;
    }
    /**
     * Bridge version of {@link MMObjectBuilder#getGUIIndicator(Node)
     * @todo testing
     */
    protected String getGUIIndicator(Node n) {
        return GUI_INDICATOR;
    }

    @Override
    protected String getFunctionValue(Node node, Parameters parameters) {
        if (log.isDebugEnabled()) {
            log.debug("GUI of builder with " + parameters);
        }
        String fieldName = parameters.get(Parameter.FIELD);
        if (fieldName != null && (! fieldName.equals("")) && parameters.get("stringvalue") == null) {
            if (node.getSize(fieldName) < 2000) {
                parameters.set("stringvalue", node.getStringValue(fieldName));
            }
        }
        org.mmbase.module.core.MMObjectNode n = (org.mmbase.module.core.MMObjectNode) parameters.get(Parameter.CORENODE);
        if (n != null) {
            return n.getBuilder().getGUIIndicator(n, parameters);
        } else {
            log.warn("No core node " + parameters);

            return GUI_INDICATOR;
        }
    }

}

/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.JspTagException;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.mmbase.bridge.Node;
import org.mmbase.bridge.Field;

import org.mmbase.util.Encode;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The FieldInfoTag can be used as a child of a 'FieldProvider' to
 * provide info about the field or fieldtype.
 *
 * @author Michiel Meeuwissen
 * @author Jaco de Groot
 */

public class FieldInfoTag extends FieldReferrerTag implements Writer {

    // Writer implementation:
    protected WriterHelper helper = new WriterHelper();
    public void setVartype(String t) throws JspTagException {
        helper.setVartype(t);
    }
    public void setJspvar(String j) {
        helper.setJspvar(j);
    }
    public void setWrite(String w) throws JspTagException {
        helper.setWrite(getAttributeBoolean(w));
    }
    public Object getWriterValue() {
        return helper.getValue();
    }
    public void haveBody() { helper.haveBody(); }


    private static Logger log = Logging.getLoggerInstance(FieldInfoTag.class.getName());

    private static final int TYPE_NAME     = 0;
    private static final int TYPE_GUINAME  = 1;
    private static final int TYPE_VALUE    = 2;
    private static final int TYPE_GUIVALUE  = 3;
    private static final int TYPE_TYPE      = 4;
    private static final int TYPE_GUITYPE   = 5;

    private static final int TYPE_UNSET     = 100;

    // input and useinput produces pieces of HTML
    // very handy if you're creating an editors, but well yes, not very elegant.
    private static final int TYPE_INPUT    = 10;
    private static final int TYPE_USEINPUT = 11;
    private static final int TYPE_SEARCHINPUT = 12;
    private static final int TYPE_USESEARCHINPUT = 13;

    private int type = TYPE_UNSET;

    private String sessionName = "cloud_mmbase";

    public void setType(String t) throws JspTagException {
        t = getAttributeValue(t).toLowerCase();
        if ("name".equals(t)) {
            type = TYPE_NAME;
        } else if ("guiname".equals(t)) {
            type = TYPE_GUINAME;
        } else if ("value".equals(t)) {
            type = TYPE_VALUE;
        } else if ("guivalue".equals(t)) {
            type = TYPE_GUIVALUE;
       } else if ("type".equals(t)) {
            type = TYPE_TYPE;
       } else if ("guitype".equals(t)) {
            type = TYPE_GUITYPE;
        } else if ("input".equals(t)) {
            type = TYPE_INPUT;
        } else if ("useinput".equals(t)) {
            type = TYPE_USEINPUT;
        } else if ("searchinput".equals(t)) {
            type = TYPE_SEARCHINPUT;
        } else if ("usesearchinput".equals(t)) {
            type = TYPE_USESEARCHINPUT;
        } else {
            throw new JspTagException("Unknown value for attribute type (" + t + ")");
        }
    }
    private String options;
    public void setOptions(String o) throws JspTagException {
        options = getAttributeValue(o);        
    }

    public int doStartTag() throws JspTagException{

        Field field;
        Node node = null;
        FieldProvider fieldProvider = findFieldProvider();

        field = ((FieldProvider) fieldProvider).getFieldVar();

        /* perhaps 'getSessionName' should be added to CloudProvider
         * EXPERIMENTAL 
         */
        CloudTag ct = null;
        ct = (CloudTag) findParentTag("org.mmbase.bridge.jsp.taglib.CloudTag", null, false);
        if (ct != null) {
            sessionName = ct.getSessionName();
        }

        // found the field now. Now we can decide what must be shown:
        String show = null;

        // set node if necessary:
        switch(type) {
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
                throw new JspTagException("Could not find surrounding NodeProvider, which is needed");
            }
            break;
        default:
        }

        switch(type) {
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
            
            List args = new Vector();
            args.add(field.getName());
            args.add(sessionName);
            args.add(node.getCloud().getLocale().getLanguage());
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
        case TYPE_USESEARCHINPUT:
            show = whereHtmlInput(field);
            break;
        case TYPE_TYPE:
            show = "" + field.getType();
            break;
        case TYPE_GUITYPE:
            show = field.getGUIType();
            break;
        }

        helper.setValue(show);
        helper.setJspvar(pageContext);
        if (getId() != null) {
            getContextTag().register(getId(), helper.getValue());
        }
        return EVAL_BODY_BUFFERED;
    }

    protected String decode (String value, Node n) throws JspTagException {
        return value;
    }

    protected String encode(String value, Field f) throws JspTagException {
        return value;
    }

    /**
     * Puts a prefix before a name. This is used in htmlInput and
     * useHtmlInput, they need it to get a reasonably unique value for
     * the name attribute of form elements.
     *
     */
    private String prefix(String s) throws JspTagException {
        String id = findFieldProvider().getId();
        if (id == null) id = "";
        if (id.equals("") ) {
            return s;
        } else {
            return id + "_" + s;
        }
    }

    /**
     * Creates a form entry.
     * @param node for this node.
     * @param field and this field.
     */

    private String htmlInput(Node node, Field field, boolean search) throws JspTagException {
        StringBuffer show;
        int type = field.getType();
        if (log.isDebugEnabled()) {
            String value = "<search>";
            if (! search) {
                if (node == null) {
                    value = "<create>";
                } else {
                    value = node.getStringValue(field.getName());
                }
            }
            log.debug("field " + field.getName() + " gui type: " + field.getGUIType() +
                      "value: " + value);
        }
        switch(type) {
        case Field.TYPE_BYTE: {
            List args = new Vector();
            args.add(sessionName);
            args.add("");
            show = new StringBuffer(
                (node != null ? node.getFunctionValue("gui", args).toString() : "") + 
                "<input type=\"file\" name=\"" + prefix(field.getName()) + "\" />");
            break;
        }
        case Field.TYPE_XML:
            if(! search) {
            // the wrap attribute is not valid in XHTML, but it is really needed for netscape < 6
            show = new StringBuffer("<textarea wrap=\"soft\" rows=\"10\" cols=\"80\" class=\"big\"  name=\"" + prefix(field.getName()) + "\">");
            if (node != null) {
                try {
                    // get the XML from this thing....
                    // javax.xml.parsers.DocumentBuilderFactory dfactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
                    // javax.xml.parsers.DocumentBuilder dBuilder = dfactory.newDocumentBuilder();
                    // org.w3c.dom.Element xml = node.getXMLValue(field.getName(), dBuilder.newDocument());
                    org.w3c.dom.Document xml = node.getXMLValue(field.getName());

                    if(xml!=null) {
                        // make a string from the XML
                        javax.xml.transform.TransformerFactory tfactory = javax.xml.transform.TransformerFactory.newInstance();
                        //tfactory.setURIResolver(new org.mmbase.util.xml.URIResolver(new java.io.File("")));
                        javax.xml.transform.Transformer serializer = tfactory.newTransformer();
                        serializer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
                        serializer.setOutputProperty(javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes");
                        java.io.StringWriter str = new java.io.StringWriter();
                        // there is a <field> tag placed around it,... we hate it :)
                        // change this in the bridge?
                        serializer.transform(new javax.xml.transform.dom.DOMSource(xml),  new javax.xml.transform.stream.StreamResult(str));

                        // fill the field with it....
                        show.append(Encode.encode("ESCAPE_XML", str.toString()));
                    }
                }
                catch(javax.xml.transform.TransformerConfigurationException tce) {
                    throw new JspTagException(tce.toString() + " " + Logging.stackTrace(tce));
                }
                catch(javax.xml.transform.TransformerException te) {
                    throw new JspTagException(te.toString() + " " + Logging.stackTrace(te));
                }
            }
            show.append("</textarea>");
            break;
            }
        case Field.TYPE_STRING:
            if(! search) {
                if(field.getMaxLength() > 2048)  {
                    // the wrap attribute is not valid in XHTML, but it is really needed for netscape < 6
                    show = new StringBuffer("<textarea wrap=\"soft\" rows=\"10\" cols=\"80\" class=\"big\"  name=\"" + prefix(field.getName()) + "\">");
                    if (node != null) {
                        show.append(Encode.encode("ESCAPE_XML", decode(node.getStringValue(field.getName()), node)));
                    }
                    show.append("</textarea>");
                    break;
                }
                if(field.getMaxLength() > 255 )  {
                    show = new StringBuffer("<textarea wrap=\"soft\" rows=\"5\" cols=\"80\" class=\"small\"  name=\"" + prefix(field.getName()) + "\">");
                    if (node != null) {
                        show.append(Encode.encode("ESCAPE_XML", decode(node.getStringValue(field.getName()), node)));
                    }
                    show.append("</textarea>");
                    break;
                }
                show = new StringBuffer("<input type =\"text\" class=\"small\" size=\"80\" name=\"" + prefix(field.getName()) + "\" value=\"");
                if (node != null) {
                    show.append(Encode.encode("ESCAPE_XML_ATTRIBUTE_DOUBLE", decode(node.getStringValue(field.getName()), node)));
                }
                show.append("\" />");
                break;
            }
        case Field.TYPE_NODE: {
            // if the gui was a builder(maybe query in future) then show a drop down for this thing, listing the nodes..
            if(getCloud().getNodeManagers().contains(field.getGUIType())) {
                // yippee! the gui was the same a an builder!
                show = new StringBuffer("<select name=\"" + prefix(field.getName()) + "\">\n");
                // list all our nodes of the specified builder here...
                int value = 0;
                if (node != null) value = node.getIntValue(field.getName());
                org.mmbase.bridge.NodeIterator nodes = getCloud().getNodeManager(field.getGUIType()).getList(null, null, null).nodeIterator();
                List args = new Vector();
                args.add(sessionName);
                args.add("");
                while(nodes.hasNext()) {
                    org.mmbase.bridge.Node tmp = nodes.nextNode();
                    // we have a match on the number!
                    show.append("  <option ");
                    if(tmp.getNumber() == value) {
                        // this is the selected one!
                        show.append("selected=\"selected\"");
                    }
                    show.append("value=\""+tmp.getNumber()+"\">");
                    show.append(Encode.encode("ESCAPE_XML", tmp.getFunctionValue("gui", args).toString()));
                    show.append("</option>\n");
                }
                show.append("</select>");
                if (search) {
                    show.append("<input type=\"checkbox\" name=\"").append(prefix(field.getName() + "_search")).append("\" />\n");
                }
                break;
            }            
        }
        case Field.TYPE_INTEGER:
            if (field.getGUIType().equals("types")) {
                show = new StringBuffer().append("<select name=\"").append(prefix(field.getName())).append("\">\n");
                int value = 0;
                if (node != null) {
                    value = node.getIntValue(field.getName());
                }
                // list all node managers.
                org.mmbase.bridge.NodeManagerIterator i = getCloud().getNodeManagers().nodeManagerIterator();
                if (log.isDebugEnabled()) {
                    log.debug("language: " + getCloud().getLocale().getLanguage());
                }
                   
                while (i.hasNext()) {
                    org.mmbase.bridge.NodeManager nm = i.nextNodeManager();
                    //int listvalue = nm.getNumber(); // TODO getNumber
                    String listvalue = nm.getName();
                    show.append("<option value=\"").append(listvalue).append("\""); 
                    if (node != null) {
                        /*
                        if (listvalue == value) {
                            show.append(" selected=\"selected\"");
                        }
                        */
                    }
                    show.append(">").append(nm.getGUIName()).append("</option>\n");
                }
                show.append("</select>");
                if (search) {
                    show.append("<input type=\"checkbox\" name=\"").append(prefix(field.getName() + "_search")).append("\" />\n");
                }
                break;
            }
            if (field.getGUIType().equals("reldefs")) {
                show = new StringBuffer("<select name=\"").append(prefix(field.getName())).append("\">\n");
                int value = 0;
                if (node != null) {
                    value = node.getIntValue(field.getName());
                }
                // list all roles
                org.mmbase.bridge.Cloud cloud = getCloud();
                org.mmbase.bridge.NodeManager typedef = cloud.getNodeManager("reldef");
                org.mmbase.bridge.NodeIterator i = typedef.getList(null, "sguiname,dguiname", null).nodeIterator();

                //java.util.Collections.sort(l);
                while (i.hasNext()) {
                    Node reldef = i.nextNode();
                    int listvalue = reldef.getNumber();
                    show.append("<option value=\"").append(reldef.getNumber()).append("\"");
                    if (node != null) {
                        if (listvalue == value) {
                            show.append(" selected=\"selected\"");
                        }
                    }
                    show.append(">").append(reldef.getStringValue("sguiname")).append("/").append(reldef.getStringValue("dguiname")).append("</option>\n");
                }
                show.append("</select>");
                if (search) {
                    show.append("<input type=\"checkbox\" name=\"").append(prefix(field.getName() + "_search")).append("\" />\n");
                }
                break;
            }
            if (field.getGUIType().equals("eventtime")) {
                Calendar cal = Calendar.getInstance();
                if (node !=null) {
                    if (node.getIntValue(field.getName()) != -1) {
                        cal.setTime(new Date(((long)node.getIntValue(field.getName()))*1000));
                    }
                }
                show = new StringBuffer("<input type=\"hidden\" name=\"").append(prefix(field.getName())).append("\" value=\"").append(cal.getTime().getTime()/1000).append("\" />");
                // give also present value, this makes it possible to see if user changed this field.

                
                if (options == null || options.indexOf("date") > -1) {
                    show.append("<select name=\"").append(prefix(field.getName() + "_day")).append("\">\n");
                    for (int i = 1; i <= 31; i++) {
                        if (cal.get(Calendar.DAY_OF_MONTH) == i) {
                            show.append("  <option selected=\"selected\">").append(i).append("</option>\n");
                        } else {
                            show.append("  <option>").append(i).append("</option>\n");
                        }
                    }
                    show.append("</select>-<select name=\"").append(prefix(field.getName() + "_month")).append("\">\n");
                    for (int i = 1; i <= 12; i++) {
                        if (cal.get(Calendar.MONTH) == (i - 1)) {
                            show.append("  <option selected=\"selected\">").append(i).append("</option>\n");
                        } else {
                            show.append("  <option>").append(i).append("</option>\n");
                        }
                    }
                    show.append("</select>-<input type =\"text\" size=\"5\" name=\"").append(prefix(field.getName() + "_year")).append("\" ").append("value=\"").append(cal.get(Calendar.YEAR)).append("\" />");
                } else {
                    show.append("<input type=\"hidden\" name=\"").append(prefix(field.getName() + "_day")).append("\" value=\"").append(cal.get(Calendar.DAY_OF_MONTH)).append("\" />");
                    show.append("<input type=\"hidden\" name=\"").append(prefix(field.getName() + "_month")).append("\" value=\"").append(cal.get(Calendar.MONTH)).append("\" />");
                    show.append("<input type=\"hidden\" name=\"").append(prefix(field.getName() + "_year")).append("\" value=\"").append(cal.get(Calendar.YEAR)).append("\" />");
                }
                if (options == null || options.indexOf("time") > -1) {
                    show.append("&nbsp;&nbsp;<select name=\"").append(prefix(field.getName() + "_hour")).append("\">\n");
                    for (int i = 0; i <= 23; i++) {
                        if (cal.get(Calendar.HOUR_OF_DAY) == i) {
                        show.append("  <option selected=\"selected\">");
                        } else {
                            show.append("  <option>");
                        }
                        if (i<10) show.append('0');
                        show.append(i).append("</option>\n");
                    }
                    show.append("</select> h :<select name=\"").append(prefix(field.getName() + "_minute")).append("\">\n");
                    for (int i = 0; i <= 59; i++) {
                        if (cal.get(Calendar.MINUTE) == i) {
                            show.append("  <option selected=\"selected\">");
                        } else {
                            show.append("  <option>");
                        }
                        if (i< 10) show.append('0');
                        show.append(i).append("</option>\n");
                    }
                    show.append("</select> m :<select name=\"").append(prefix(field.getName() + "_second")).append("\">\n");
                    for (int i = 0; i <= 59; i++) {
                        if (cal.get(Calendar.SECOND) == i) {
                            show.append("  <option selected=\"selected\">");
                        } else {
                            show.append("  <option>");
                    }
                        if (i< 10) show.append('0');
                        show.append(i).append("</option>\n");
                    }
                    show.append("</select> s");
                } else {
                    show.append("<input type=\"hidden\" name=\"").append(prefix(field.getName() + "_hour")).append("\" value=\"").append(cal.get(Calendar.HOUR_OF_DAY)).append("\" />");
                    show.append("<input type=\"hidden\" name=\"").append(prefix(field.getName() + "_minute")).append("\" value=\"").append(cal.get(Calendar.MINUTE)).append("\" />");
                    show.append("<input type=\"hidden\" name=\"").append(prefix(field.getName() + "_second")).append("\" value=\"").append(cal.get(Calendar.SECOND)).append("\" />");
                }
                break;
            }
        case Field.TYPE_FLOAT:
        case Field.TYPE_DOUBLE:
        case Field.TYPE_LONG:

            show =  new StringBuffer("<input type =\"text\" class=\"small\" size=\"80\" name=\"").append(prefix(field.getName())).append("\" ").append("value=\"");
            if (node != null) {
                show.append(node.getStringValue(field.getName()));
            } else if (search) {
                String searchParam = getContextTag().findAndRegisterString(prefix(field.getName()));
                show.append((searchParam == null ? "" : searchParam));
            }
            show.append("\" />");
            break;
        default: log.error("field: " + type );
            return "";
        }
        return show.toString();
    }

    /**
     * Applies a form entry.
     */

    private String useHtmlInput(Node node, Field field) throws JspTagException {
        String fieldName  = field.getName();
        int type = field.getType(); // not to be confused with the attribute 'type' of this tag.
        if (log.isDebugEnabled()) {
            log.debug("using html form input for field " + fieldName + " of " + node.getNumber());
        }

        switch(type) {
        case Field.TYPE_BYTE: {
            byte [] bytes  = getContextTag().getBytes(prefix(fieldName));
            if (bytes.length > 0) {
                log.debug("Setting bytes of");
                node.setByteValue(fieldName, bytes);
            } else { // not changed
                log.debug("Not setting bytes");
            }
            break;
        }
        case Field.TYPE_XML:
        case Field.TYPE_STRING: {
            // do the xml decoding thing...
            String fieldValue = getContextTag().findAndRegisterString(prefix(fieldName));
            fieldValue = encode(fieldValue, field);
            log.debug("got it");
            if (fieldValue == null) {
                log.debug("Field " + fieldName + " is null!");
            } else {
                log.debug("Field " + fieldName + " -> " + fieldValue);
                node.setValue(fieldName,  fieldValue);
                log.debug("set it");
            }

            break;
        }
        case Field.TYPE_INTEGER:
            if (field.getGUIType().equals("eventtime")) {
                Calendar cal = Calendar.getInstance();
                try {
                    Integer day    = new Integer(getContextTag().findAndRegisterString(prefix(fieldName + "_day")));
                    Integer month  = new Integer(getContextTag().findAndRegisterString(prefix(fieldName + "_month")));
                    Integer year   = new Integer(getContextTag().findAndRegisterString(prefix(fieldName + "_year")));
                    Integer hour   = new Integer(getContextTag().findAndRegisterString(prefix(fieldName + "_hour")));
                    Integer minute = new Integer(getContextTag().findAndRegisterString(prefix(fieldName + "_minute")));
                    Integer second = new Integer(getContextTag().findAndRegisterString(prefix(fieldName + "_second")));
                    int y = year.intValue();
                    if (y < 1902 || y > 2037) {
                        throw new JspTagException("Year must be between 1901 and 2038 (now " + y + ")");
                    }
                    cal.set(y, month.intValue() - 1, day.intValue(),
                            hour.intValue(), minute.intValue(), second.intValue());
                    node.setLongValue(fieldName, (long) (cal.getTime().getTime() / 1000));
                } catch (java.lang.NumberFormatException e) {
                    throw new JspTagException("Not a valid number (" + e.toString() + ") in field " + fieldName);
                }
                break;
            }
            log.debug("normal integer type, falling through");

        case Field.TYPE_NODE: // maybe add more options?
        case Field.TYPE_FLOAT:
        case Field.TYPE_DOUBLE:
        case Field.TYPE_LONG: {
            String fieldValue = getContextTag().findAndRegisterString(prefix(fieldName));
            if (fieldValue == null) {
                if (log.isDebugEnabled()) log.debug("Field " + fieldName + " is null!");
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Field " + fieldName + " -> " + fieldValue);
                }
                node.setValue(fieldName,  fieldValue);
            }

            break;
        }
        default: log.error("field: " + type );
        }
        return "";
    }


    /**
     * If you use a form entry to search, then you can use this functions to create the where part.
     * @param field and this field.
     */
    private String whereHtmlInput(Field field) throws JspTagException {
        StringBuffer show;
        int type = field.getType();
        String guitype = field.getGUIType();
        String fieldName = field.getName();
        switch(type) {
        case Field.TYPE_BYTE:
            throw new JspTagException("Don't know what to do with bytes()");
        case Field.TYPE_XML:
        case Field.TYPE_STRING: {
            String search = getContextTag().findAndRegisterString(prefix(fieldName));
            if (search == null) {
                log.error("parameter " + prefix(fieldName) + " could not be found");
                show =  null;
                break;
            }
            if ("".equals(search)) {
                show =  null;
                break;
            }
                show = new StringBuffer("( UPPER([").append(fieldName).append("]) LIKE '%").append(search.toUpperCase()).append("%')");
                break;
        }
        case Field.TYPE_NODE:
            if (getCloud().getNodeManagers().contains(field.getGUIType())) {
                String id = prefix(fieldName + "_search");
                if (getContextTag().findAndRegister(id, id) == null) {
                    show = null;
                    break;
                }
            }        
        case Field.TYPE_INTEGER:
            if (guitype.equals("eventtime")) {
                Calendar cal = Calendar.getInstance();
                try {
                    Integer day    = new Integer(getContextTag().findAndRegisterString(prefix(fieldName + "_day")));
                    Integer month  = new Integer(getContextTag().findAndRegisterString(prefix(fieldName + "_month")));
                    Integer year   = new Integer(getContextTag().findAndRegisterString(prefix(fieldName + "_year")));
                    Integer hour   = new Integer(getContextTag().findAndRegisterString(prefix(fieldName + "_hour")));
                    Integer minute = new Integer(getContextTag().findAndRegisterString(prefix(fieldName + "_minute")));
                    Integer second = new Integer(getContextTag().findAndRegisterString(prefix(fieldName + "_second")));
                    int y = year.intValue();
                    if (y < 1902 || y > 2037) {
                        throw new JspTagException("Year must be between 1901 and 2038 (now " + y + ")");
                    }
                    cal.set(y, month.intValue() - 1, day.intValue(),
                            hour.intValue(), minute.intValue(), second.intValue());
                } catch (java.lang.NumberFormatException e) {
                    throw new JspTagException("Not a valid number (" + e.toString() + ")");
                }
                // check if changed:
                if (! getContextTag().findAndRegisterString(prefix(fieldName)).equals("" + cal.getTime().getTime() /1000)) {
                    show = new StringBuffer("(" + fieldName + ">" + (cal.getTime().getTime() / 1000) + ")");
                } else {
                    show = null;
                }
                break;
            }
            if ("types".equals(guitype) || "reldefs".equals(guitype)) {
                String id = prefix(fieldName + "_search");
                if (getContextTag().findAndRegister(id, id) == null) {
                    show = null;
                    break;
                }
            }
            log.debug("normal integer type, falling through");
        case Field.TYPE_FLOAT:
        case Field.TYPE_DOUBLE:
        case Field.TYPE_LONG: {
                log.debug("treating simple types");
                log.debug("1");
                String search = getContextTag().findAndRegisterString(prefix(fieldName));
                if (search == null) {
                    log.error("parameter " + prefix(fieldName) + " could not be found");
                    show =  null;
                    break;
                }
                if ("".equals(search)) {
                    show =  null;
                    break;
                }
                show =  new StringBuffer("(" + fieldName + "=" + search + ")");
                break;
            }
        default: log.error("field: " + type + " not found");
            show = null;
        }
        if (show == null) return null;
        return show.toString();
    }

    /**
     * Write the value of the fieldinfo.
     */
    public int doEndTag() throws JspTagException {
        helper.setBodyContent(bodyContent);
        return helper.doEndTag();
    }
}

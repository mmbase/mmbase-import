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

import org.mmbase.bridge.Node;
import org.mmbase.bridge.Field;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
* The FieldInfoTag can be used as a child of a 'FieldListTag' or
* directly under a 'NodeProvider'tag.
* 
* @author Michiel Meeuwissen 
* @author Jaco de Groot
*/

public class FieldInfoTag extends NodeReferrerTag {
    
    private static Logger log = Logging.getLoggerInstance(FieldInfoTag.class.getName()); 

    private static final int TYPE_NAME     = 0;
    private static final int TYPE_GUINAME  = 1;
    private static final int TYPE_VALUE    = 2;
    private static final int TYPE_GUIVALUE    = 3;

    // input and useinput produces pieces of HTML
    // very handy if you're creating an editors, but well yes, not very elegant.
    private static final int TYPE_INPUT    = 10;
    private static final int TYPE_USEINPUT = 11;
    private static final int TYPE_WHEREINPUT = 12;
    
    private int type;   
    private String whichField = null;
       
    public void setType(String t) throws JspTagException {
        if ("name".equals(t)) {
            type = TYPE_NAME;
        } else if ("guiname".equals(t)) {
            type = TYPE_GUINAME;
        } else if ("value".equals(t)) {
            type = TYPE_VALUE;
        } else if ("guivalue".equals(t)) {
            type = TYPE_GUIVALUE;
        } else if ("input".equals(t)) {
            type = TYPE_INPUT;
        } else if ("useinput".equals(t)) {       
            type = TYPE_USEINPUT;
        } else if ("whereinput".equals(t)) {       
            type = TYPE_WHEREINPUT;
        } else {
            throw new JspTagException("Unknown value for attribute type (" + t + ")");
        }
    }

    public void setField(String f) {
        whichField = f;
    }
    
    public int doStartTag() throws JspTagException{
        return EVAL_BODY_TAG;
    }

    /**
     * Puts a prefix before a name. This is used in htmlInput and
     * useHtmlInput, they need it to get a reasonably unique value for
     * the name attribute of form elements.
     * 
     */
    private String prefix(String s) {
        String id = getId();
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

    private String htmlInput(Node node, Field field) {
        String show;
        int type = field.getType();
        switch(type) {
        case Field.TYPE_BYTE:
            show = "<input type=\"file\" name=\"" + prefix(field.getName()) + "\" />";
            break;
        case Field.TYPE_STRING:
            if(field.getMaxLength() > 2048)  {
                show = "<textarea wrap=\"on\" rows=\"10\" cols=\"80\" class=\"big\"  name=\"" + prefix(field.getName()) + "\">";
                if (node != null) {
                    show += node.getStringValue(field.getName());
                }                    
                show += "</textarea>";                
                break;                    
            }
            if(field.getMaxLength() > 255 )  {                
                show = "<textarea wrap=\"on\" rows=\"5\" cols=\"80\" class=\"small\"  name=\"" + prefix(field.getName()) + "\">"; 
                if (node != null) {
                    show += node.getStringValue(field.getName());
                }                    
                show += "</textarea>";
                break;
            }
        case Field.TYPE_INTEGER:  
            if (field.getGUIType().equals("eventtime")) {
                Calendar cal = Calendar.getInstance();
                if (node !=null) {
                    if (node.getIntValue(field.getName()) != -1) {
                        cal.setTime(new Date(((long)node.getIntValue(field.getName()))*1000));
                    }
                }
                show = "<input type=\"hidden\" name=\"" + prefix(field.getName()) + "\" value=\"" + cal.getTime().getTime()/1000 + "\" />";
                // give also present value, this makes it possible to see if user changed this field.
                show +=  "<select name=\"" + prefix(field.getName() + "_day") + "\">\n";
                for (int i = 1; i <= 31; i++) {
                    if (cal.get(Calendar.DAY_OF_MONTH) == i) {
                        show += "  <option selected=\"selected\">" + i + "</option>\n";
                    } else {
                        show += "  <option>" + i + "</option>\n";
                    }
                }
                show += "</select>-";
                show += "<select name=\"" + prefix(field.getName() + "_month") + "\">\n";
                for (int i = 1; i <= 12; i++) {
                    if (cal.get(Calendar.MONTH) == (i - 1)) {
                        show += "  <option selected=\"selected\">" + i + "</option>\n";
                    } else {
                        show += "  <option>" + i + "</option>\n";
                    }
                }
                show += "</select>-";
                show += "<input type =\"text\" size=\"5\" name=\"" + prefix(field.getName() + "_year") + "\" " + 
                    "value=\"" + cal.get(Calendar.YEAR) + "\" />";
                show += "&nbsp;&nbsp;<select name=\"" + prefix(field.getName() + "_hour") + "\">\n";
                for (int i = 0; i <= 23; i++) {
                    if (cal.get(Calendar.HOUR_OF_DAY) == i) {
                        show += "  <option selected=\"selected\">";
                    } else {
                        show += "  <option>";
                    }
                    if (i<10) show += "0";
                    show += i + "</option>\n";
                }
                show += "</select> h :";
                show += "<select name=\"" + prefix(field.getName() + "_minute") + "\">\n";
                for (int i = 0; i <= 59; i++) {                  
                    if (cal.get(Calendar.MINUTE) == i) {
                        show += "  <option selected=\"selected\">";
                    } else {
                        show += "  <option>";
                    }
                    if (i< 10) show += "0";
                    show += i + "</option>\n";
                }
                show += "</select> m :";
                show += "<select name=\"" + prefix(field.getName() + "_second") + "\">\n";
                for (int i = 0; i <= 59; i++) {
                    if (cal.get(Calendar.SECOND) == i) {
                        show += "  <option selected=\"selected\">";
                    } else {
                        show += "  <option>";
                    }
                    if (i< 10) show += "0";
                    show += i + "</option>\n";
                }
                show += "</select> s";
                break;
            }
        case Field.TYPE_FLOAT:
        case Field.TYPE_DOUBLE:
        case Field.TYPE_LONG:
            show =  "<input type =\"text\" class=\"small\" size=\"80\" name=\"" + prefix(field.getName()) + "\" " + 
                "value=\"";
            if (node != null) {
                show +=  node.getStringValue(field.getName());
            }
            show += "\" />";
            break;
        default: log.error("field: " + type );
            show = "";
        }
        return show;
    }

    /**
     * Applies a form entry.
     */

    private String useHtmlInput(Node node, Field field) throws JspTagException {
        String fieldName  = field.getName();
        int type = field.getType(); // not to be confused with the attribute 'type' of this tag.
        
        switch(type) {
        case Field.TYPE_BYTE:
            node.setByteValue(fieldName, findCloudProvider().getBytes(prefix(fieldName)));
            break;
        case Field.TYPE_INTEGER:             
            if (field.getGUIType().equals("eventtime")) {
                Calendar cal = Calendar.getInstance();
                try {
                    Integer day    = new Integer(getString(prefix(fieldName + "_day")));
                    Integer month  = new Integer(getString(prefix(fieldName + "_month")));
                    Integer year   = new Integer(getString(prefix(fieldName + "_year")));
                    Integer hour   = new Integer(getString(prefix(fieldName + "_hour")));
                    Integer minute = new Integer(getString(prefix(fieldName + "_minute")));
                    Integer second = new Integer(getString(prefix(fieldName + "_second")));
                    int y = year.intValue();
                    if (y < 1902 || y > 2037) {
                        throw new JspTagException("Year must be between 1901 and 2038 (now " + y + ")");
                    }
                    cal.set(y, month.intValue() - 1, day.intValue(), 
                            hour.intValue(), minute.intValue(), second.intValue());
                    node.setIntValue(fieldName, (int) (cal.getTime().getTime() / 1000));
                } catch (java.lang.NumberFormatException e) {
                    throw new JspTagException("Not a valid number (" + e.toString() + ")");
                }
                break;
            }
        case Field.TYPE_STRING:
        case Field.TYPE_FLOAT:
        case Field.TYPE_DOUBLE:
        case Field.TYPE_LONG:
            String fieldValue = getString(prefix(fieldName));
            log.debug("got it");
            if (fieldValue == null) {
                log.debug("Field " + fieldName + " is null!");
            } else {
                log.debug("Field " + fieldName + " -> " + fieldValue);
                node.setValue(fieldName,  fieldValue);
                log.debug("set it");
            }
            
            break;
        default: log.error("field: " + type );
        }  
        return "";
    }


    /**
     * If you use a form entry to search, then you can use this functions to create the where part.
     * @param field and this field.
     */

    private String whereHtmlInput(Field field) throws JspTagException {
        String show;
        int type = field.getType();
        String fieldName = field.getName();       
        switch(type) {
        case Field.TYPE_BYTE:
            throw new JspTagException("Don't know what to do with bytes()");
        case Field.TYPE_STRING:
            {
                String search = getString(prefix(fieldName));
                if (search == null) {
                    log.error("parameter " + prefix(fieldName) + " could not be found");
                    show =  null;
                    break;
                }
                if ("".equals(search)) {
                    show =  null;
                    break;
                }
                search = search.toUpperCase();
                show = "( UCASE(" + fieldName + ") LIKE '%" + search + "%')";
            }
            break;
        case Field.TYPE_INTEGER:  
            if (field.getGUIType().equals("eventtime")) {
                Calendar cal = Calendar.getInstance();
                try {
                    Integer day    = new Integer(getString(prefix(fieldName + "_day")));
                    Integer month  = new Integer(getString(prefix(fieldName + "_month")));
                    Integer year   = new Integer(getString(prefix(fieldName + "_year")));
                    Integer hour   = new Integer(getString(prefix(fieldName + "_hour")));
                    Integer minute = new Integer(getString(prefix(fieldName + "_minute")));
                    Integer second = new Integer(getString(prefix(fieldName + "_second")));
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
                if (! getString(prefix(fieldName)).equals("" + cal.getTime().getTime() /1000)) { 
                    show = "(" + fieldName + "=" + (cal.getTime().getTime() / 1000) + ")";
                } else {
                    show = null;
                }
                break;
            }
        case Field.TYPE_FLOAT:
        case Field.TYPE_DOUBLE:
        case Field.TYPE_LONG:
            {
                String search = getString(prefix(fieldName));
                if (search == null) {
                    log.error("parameter " + prefix(fieldName) + " could not be found");
                    show =  null;
                    break;
                }
                if ("".equals(search)) {
                    show =  null;
                    break;
                }
                show =  "(" + fieldName + "=" + getString(prefix(fieldName)) + ")";
            }
            break;
        default: log.error("field: " + type );
            show = null;
        }
        return show;
    }

    /**
    * Write the value of the fieldinfo.
    */
    public int doAfterBody() throws JspTagException {
        
        Field field;
        Node node = null;
        FieldListTag fieldTag = null;

        if (whichField == null) { // must be in FieldList then
            // firstly, search the field:
            Class fieldClass;
            try {
                fieldClass = Class.forName("org.mmbase.bridge.jsp.taglib.FieldListTag");
                
            } catch (java.lang.ClassNotFoundException e) {
                throw new JspTagException ("Could not find FieldListTag class");  
            }
            
            fieldTag = (FieldListTag) findAncestorWithClass((Tag)this, fieldClass); 
            if (fieldTag == null) {
                throw new JspTagException ("Could not find parent FieldListTag");  
            }
            
            if (getId() == null) { // inherit id..
                setId(fieldTag.getId());
            }


            field = fieldTag.getField();
        } else { // not in List, get it from a parent Node
            node = findNodeProvider().getNodeVar();
            field = node.getNodeManager().getField(whichField);
            if (field == null) {
                throw new JspTagException("Unknown field '" + whichField + "' for nodemanager " + node.getNodeManager().getName());
            }
        }
        
        // found the field now. Now we can decide what must be shown:
        String show = null;

        // set node if necessary:
        switch(type) {
        case TYPE_VALUE:
        case TYPE_GUIVALUE:
        case TYPE_INPUT:
            if (node == null) { // try to find nodeProvider
                try {
                    node = fieldTag.findNodeProvider().getNodeVar();                    
                } catch (JspTagException e) {
                    // if not can be found, then simply node can stay null.
                }
            }
            break;
        case TYPE_USEINPUT:
            if (node == null) node = findNodeProvider().getNodeVar();
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
            show = node.getStringValue(field.getName());
            break;
        case TYPE_GUIVALUE:
            show = node.getStringValue("gui("+field.getName()+")");
            if (show.trim().equals("")) {
                show = node.getStringValue(field.getName());
            }
            break;
        case TYPE_INPUT:
            show = htmlInput(node, field);
            break;
        case TYPE_USEINPUT:
            show = useHtmlInput(node, field); 
            break;
        case TYPE_WHEREINPUT:
            show = whereHtmlInput(field); 
            break;
        }

        try {
            if (show != null) {
                String body = bodyContent.getString();
                bodyContent.clearBody();
                bodyContent.print(show + body);
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            }        
        } catch (java.io.IOException e) {
            throw new JspTagException (e.toString());            
        }        
        return SKIP_BODY;
    }
}

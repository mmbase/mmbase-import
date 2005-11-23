/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.editor;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;
import java.io.IOException;
import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.ContextReferrerTag;
import org.mmbase.bridge.jsp.taglib.ParamHandler;
import org.mmbase.util.Entry;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.mmbase.util.XMLBasicReader;

/**
 * The EditTag collects the nodenrs, fields and queries of the FieldTags in its body.
 * FieldTags register these with the EditTag. You can pass these values to an 
 * editor by creating an url for example like YAMMEditor does which gives access to
 * the editor yammeditor.jsp.<br />
 * Extend Editor to create your own and edit the edittagtypes.xml resources file
 * to let the EditTag know about it.
 *
 * @author Andr&eacute; van Toly
 * @version $Id: EditTag.java,v 1.3 2005-11-23 13:07:53 andre Exp $
 * @see org.mmbase.bridge.jsp.taglib.editor.Editor
 * @see org.mmbase.bridge.jsp.taglib.editor.YAMMEditor
 */
public class EditTag extends ContextReferrerTag implements ParamHandler {

    private static final Logger log = Logging.getLoggerInstance(EditTag.class);
    private Attribute type = Attribute.NULL;
    
    private Query query;
    private int nodenr;
    private String fieldName;

    private List queryList = new ArrayList();       // query List
    private List nodenrList = new ArrayList();      // nodenr List
    private List fieldList = new ArrayList();       // fieldname List

    private static final Map edittagTypes = new HashMap();  // edittagtype -> class
    protected List parameters;
    
    private Editor yaeditor = null;     // should do all the work
    
    /**
     * The type of editor, see resources/edittagtypes.xml for different types or
     * add your own editor.
     *
     * @param t     String with editor type.
     */ 
    public void setType(String t) throws JspTagException {
        type = getAttribute(t);
    }

    /**
     * The type of editor, see resources/edittagtypes.xml for different types or
     * add your own editor.
     *
     * @return String stating the type of editor the tag refers to.
     */ 
    public String getType() throws JspTagException {
        if (type == Attribute.NULL) {
            return "edittag";           // the default
        } else {
            return type.getString(this);
        }
    }
    
    /**
     * Parameters of this tag. The tag's type is defined in its attribute, but you
     * can give it as much parameters as you like to controll your implementations
     * behaviour.
     *
     * @param key     String with a key
     * @param value   Object with a value
     */ 
    public void addParameter(String key, Object value) throws JspTagException {
        log.debug("adding parameter " + key + "/" + value);
        parameters.add(new Entry(key, value));
    }
    
    /*
     * Start the EditTag, put the implementations found in its resources in a Map, 
     * consult the attribute type which implementation to use and instantiate it.
     *
     *
    */    
    public int doStartTag() throws JspTagException {
        log.debug("doStartTag of EditTag");
        parameters = new ArrayList();
        
        InputSource ettypes = new InputSource(EditTag.class.getResourceAsStream("edittagtypes.xml"));
        XMLBasicReader reader  = new XMLBasicReader(ettypes, EditTag.class);
        
        Element edittypesElement = reader.getElementByPath("edittagtypes");
        Iterator i = reader.getChildElements(edittypesElement, "editor");
        while (i.hasNext()) {
            Element element = (Element) i.next();
            String type = element.getAttribute("type");
            String claz = reader.getElementValue(reader.getElementByPath(element, "editor.class"));
            if (!claz.equals("")) {
                edittagTypes.put(type, claz);
                log.debug("Found editor type: '" + type + "' with class: '" + claz + "'");
            } 
        }
        
        String classname = (String) edittagTypes.get(getType());
        log.debug("Using editor: " + classname);
        Class c = null;
        try {
            c = Class.forName(classname);
            yaeditor = (Editor) c.newInstance();
        } catch (ClassNotFoundException cnfe) {
            log.error("Class '" + classname + "' not found: " + cnfe);
        } catch (InstantiationException ie) {
            log.error("Unable to instantiate class '" + classname + "': " + ie);
        } catch (IllegalAccessException iae) {
            log.error("IllegalAccessException instantiating class " + classname + "': " + iae); 
        }
                
        return EVAL_BODY_INCLUDE;
    }
    
    /*
     * Pass all gathered information to the implementing editor, get the
     * the result back and write it to the page.
     *
    */
    public int doEndTag() throws JspTagException {
        String editorstr = "";
        
        yaeditor.setParameters(parameters);
        
        yaeditor.setQueryList(queryList);
        yaeditor.setNodenrList(nodenrList);
        yaeditor.setFieldList(fieldList);
        
        yaeditor.registerFields(queryList, nodenrList, fieldList);
        
        // resulting String
        editorstr = yaeditor.getEditorHTML();
        
        helper.setValue(editorstr);
        helper.useEscaper(false);
        
        // make all lists null
        parameters = null;
        queryList = null;
        nodenrList = null;
        fieldList = null;
        log.debug("end of doEndTag of EditTag");
        helper.doEndTag();
        return super.doEndTag();
    }
    
    /**
     * Here is were the FieldTag registers its fields and some associated 
     * and maybe usefull information with the EditTag.
     *
     * @param query     SearchQuery object that delivered the field
     * @param nodenr    int with the number of the node the field belongs to
     * @param fieldName String with the fieldname
     */ 
    public void registerField(Query query, int nodenr, String fieldName) {
        log.debug("query: " + query);
        log.debug("nodenr: " + nodenr);
        log.debug("fieldName: " + fieldName);
        queryList.add(query);
        nodenrList.add(String.valueOf(nodenr));
        fieldList.add(fieldName);
        
        log.debug("register nodenr '" + nodenr + "' and fieldName '" + fieldName + "'");
    }   
}

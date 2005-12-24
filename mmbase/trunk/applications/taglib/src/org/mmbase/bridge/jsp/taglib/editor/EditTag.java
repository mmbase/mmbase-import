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
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.storage.search.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.util.Entry;
import org.mmbase.util.ResourceLoader;
import org.mmbase.util.ResourceWatcher;
import org.mmbase.util.xml.DocumentReader;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.ContextReferrerTag;
import org.mmbase.bridge.jsp.taglib.ParamHandler;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.mmbase.util.XMLBasicReader;

/**
 * The EditTag collects the nodenrs, fields and queries of the FieldTags in its body.
 * FieldTags register these with the EditTag. You can pass these values to an 
 * editor by creating an url for example like YAMMEditor does which gives access to
 * the editor yammeditor.jsp.<br />
 * Extend Editor to create your own and edit the edittag.xml resources file
 * in the MMBase config/taglib directory to let the EditTag know about it.
 *
 * @author Andr&eacute; van Toly
 * @version $Id: EditTag.java,v 1.7 2005-12-24 13:18:47 andre Exp $
 * @see org.mmbase.bridge.jsp.taglib.editor.Editor
 * @see org.mmbase.bridge.jsp.taglib.editor.YAMMEditor
 * @since MMBase-1.8
 */
public class EditTag extends ContextReferrerTag implements ParamHandler {

    private static final Logger log = Logging.getLoggerInstance(EditTag.class);
    private static final Map edittagTypes = new HashMap();		// edittagtype -> class
    
    static {
    	try {
			org.mmbase.util.XMLEntityResolver.registerPublicID("-//MMBase//DTD edittagtypes 1.0//EN", "edittagtypes_1_0.dtd", EditTag.class);
            ResourceWatcher watcher = new ResourceWatcher(ResourceLoader.getConfigurationRoot().getChildResourceLoader("taglib")) {
				public void onChange(String resource) {
					edittagTypes.clear();
					
					// default: reading from taglib jar in case no other resources exist
					InputStream stream = EditTag.class.getResourceAsStream("resources/edittag.xml");
					if (stream != null) {	// fallback in case config/taglib may not exist
						log.info("Reading default edittag resource: " + EditTag.class.getName() + "/resources/edittag.xml");
						
						InputSource ettypes = new InputSource(stream);
						readXML(ettypes);
					}
					
					ResourceLoader taglibLoader = ResourceLoader.getConfigurationRoot().getChildResourceLoader("taglib");
					List resources = taglibLoader.getResourceList(resource);
					log.info("Found edittag resources: " + resources);
					
					ListIterator i = resources.listIterator();
					while (i.hasNext()) {
						try {
							URL u = (URL) i.next();
							log.info("Reading edittag resource: " + u);
							URLConnection con = u.openConnection();
							if (con.getDoInput()) {
								InputSource source = new InputSource(con.getInputStream());
								readXML(source);
							}
						} catch (Exception e) {
							log.error("Error connecting or resource not found: " + e);
						}
					}
				}
			};
            watcher.add("edittag.xml");
            watcher.start();
            watcher.onChange("edittag.xml");
			
		} catch (Exception e){
			log.error(e.toString());
		}
    }
    
    /**
     * 'reads' a resource XML and puts its values in a Map with types of edittags
     *
     */
    protected static void readXML(InputSource edittagSource) {    
        DocumentReader reader  = new DocumentReader(edittagSource, EditTag.class);
        Element root = reader.getElementByPath("edittagtypes");
        
		Iterator i = reader.getChildElements(root, "editor");
		while (i.hasNext()) {
			Element element = (Element) i.next();
			String type = element.getAttribute("type");
			String claz = reader.getElementValue(reader.getElementByPath(element, "editor.class"));
    	    log.debug("type: " + type + " and class: " + claz);
			if (!claz.equals("") && !edittagTypes.containsKey(type) ) {
				edittagTypes.put(type, claz);
				log.info("Found and added editor type: '" + type + "' with class: '" + claz + "'");
			} 
		}
    }
    
    private Attribute type = Attribute.NULL;
    
    private Query query;
    private int nodenr;
    private String fieldName;

    private List queryList = new ArrayList();       // query List
    private List nodenrList = new ArrayList();      // nodenr List
    private List fieldList = new ArrayList();       // fieldname List

    protected List parameters = new ArrayList();
    
    private Editor yaeditor = null;     // should do all the work
    
    /**
     * The type of editor, add your own editor in config/taglib/edittag.xml.
     *
     * @param t     String with editor type.
     */ 
    public void setType(String t) throws JspTagException {
        type = getAttribute(t);
    }

    /**
     * The type of editor, see config/taglib/edittag.xml for different types or
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
    
    /**
     * Start the EditTag, put the implementations found in its resources in a Map, 
     * consult the attribute type which implementation to use and instantiate it.
     *
     */    
    public int doStartTag() throws JspTagException {
        log.debug("doStartTag of EditTag");
        // clear lists (in case of tag caching, the previous values may be present.)
        queryList.clear();
        parameters.clear();
        nodenrList.clear();
        fieldList.clear();                
        String className = (String) edittagTypes.get(getType());
        log.debug("Using editor: " + className);
        Class c = null;
        try {
            c = Class.forName(className);
            yaeditor = (Editor) c.newInstance();
        } catch (ClassNotFoundException cnfe) {
            log.error("Class '" + className + "' not found: " + cnfe);
        } catch (InstantiationException ie) {
            log.error("Unable to instantiate class '" + className + "': " + ie);
        } catch (IllegalAccessException iae) {
            log.error("IllegalAccessException instantiating class " + className + "': " + iae); 
        }
        return EVAL_BODY;
    }
    
    /**
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
        //yaeditor.getEditorHTML(getPageContext());
        
        helper.setValue(editorstr);
        helper.useEscaper(false);
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
        if (log.isDebugEnabled()) {
            log.debug("query: " + query);
            log.debug("nodenr: " + nodenr);
            log.debug("fieldName: " + fieldName);
        }
        queryList.add(query);
        nodenrList.add(String.valueOf(nodenr));
        fieldList.add(fieldName);
        
        log.debug("register nodenr '" + nodenr + "' and fieldName '" + fieldName + "'");
    }   
}

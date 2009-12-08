/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.bridge.jsp.taglib.editor;

import javax.servlet.jsp.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import org.mmbase.bridge.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

import org.mmbase.util.*;
import org.mmbase.util.xml.*;
import org.mmbase.util.functions.Parameters;

import org.mmbase.bridge.jsp.taglib.util.Attribute;
import org.mmbase.bridge.jsp.taglib.*;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;

/**
 * The EditTag collects the nodenrs, fields and queries of the FieldTags in its body.
 * FieldTags register these with the EditTag. You can pass these values to an
 * editor by creating an url for example like YAMMEditor does which gives access to
 * the editor yammeditor.jsp.<br />
 * If you like you can extend Editor to create your own way to edit MMBase content.
 * Edit the edittag.xml resources file to add your class in the MMBase config/taglib
 * directory to let the EditTag know about it. The class BasicEditor gives an example how
 * to generate an url to the basic jsp editors and my_editors.
 *
 *
 * @author Andr&eacute; van Toly
 * @author Michiel Meeuwissen
 * @version $Id$
 * @see Editor
 * @see BasicEditor
 * @see YAMMEditor
 * @since MMBase-1.8
 */
public class EditTag extends CloudReferrerTag implements ParamHandler {

    private static final Logger log = Logging.getLoggerInstance(EditTag.class);
    private static final Map<String, EditorDefinition> edittagTypes = new HashMap<String, EditorDefinition>();      // edittagtype -> editordefinition

    public static final String KEY = "org.mmbase.taglib.edit";
    public static final int SCOPE = PageContext.REQUEST_SCOPE;

    static {
        try {
            org.mmbase.util.xml.EntityResolver.registerPublicID("-//MMBase//DTD edittagtypes 1.0//EN", "edittagtypes_1_0.dtd", EditTag.class);
            ResourceWatcher watcher = new ResourceWatcher(ResourceLoader.getConfigurationRoot().getChildResourceLoader("taglib")) {
                public void onChange(String resource) {
                    edittagTypes.clear();
                    // default: reading from taglib jar in case no other resources exist
                     InputStream stream = EditTag.class.getResourceAsStream("resources/edittag.xml");
                     if (stream != null) {   // fallback in case config/taglib may not exist
                         log.service("Reading default edittag resource: " + EditTag.class.getName() + "/resources/edittag.xml");
                         InputSource ettypes = new InputSource(stream);
                         readXML(ettypes);
                     }


                    ResourceLoader taglibLoader = ResourceLoader.getConfigurationRoot().getChildResourceLoader("taglib");
                    List<URL> resources = taglibLoader.getResourceList(resource);
                    log.service("Reading edittag resources: " + resources);
                    ListIterator<URL> i = resources.listIterator();
                    while (i.hasNext()) {
                        try {
                            URL u = i.next();
                            URLConnection con = u.openConnection();
                            if (con.getDoInput()) {
                                log.debug("Reading edittag resource: " + u);
                                InputSource source = new InputSource(con.getInputStream());
                                readXML(source);
                            } else {
                                log.debug("Unavailable Edittag resource: " + u);
                            }
                        } catch (Exception e) {
                            log.error("Error connecting or resource not found: " + e);
                        }
                    }
                    ArrayList<String> l = new ArrayList<String>(edittagTypes.keySet());
                    Collections.sort(l);
                    log.service("Found edit-tag types " + l);
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

        for (Element element: reader.getChildElements(root, "editor")) {
            String type = element.getAttribute("type");
            try {
                EditorDefinition newDef = new EditorDefinition(element);
                Object original = edittagTypes.put(type, newDef);
                if (original != null) {
                    log.service("Replaced edittag '" + type + "'  " + original + " --> " + newDef);
                } else {
                    log.debug("Created edittag '" + type + "': " + newDef);
                }
            } catch (Exception e) {
                log.error("In: " + XMLWriter.write(element, true) + ": " + e.getClass() + "  " + e.getMessage());
            }
        }

    }

    private Attribute type = Attribute.NULL;

    private Object prevEditor = null;
    private Editor editor = null;     // should do all the work

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
        if (log.isDebugEnabled()) log.debug("adding parameter " + key + "/" + value);
        editor.getParameters().set(key, value);
    }

    /**
     * Start the EditTag, put the implementations found in its resources in a Map,
     * consult the attribute type which implementation to use and instantiate it.
     *
     */
    public int doStartTag() throws JspTagException {
        if (log.isDebugEnabled()) log.debug("doStartTag of EditTag");

        prevEditor = pageContext.getAttribute(KEY, SCOPE);

        EditorDefinition def = edittagTypes.get(getType());
        if (def == null) {
            throw new JspTagException("'" + getType() + "' is not a known edit type. Known are " + edittagTypes.keySet());
        }
        if (log.isDebugEnabled()) {
            log.debug("Using editor: " + def);
        }
        editor = def.newInstance();
        pageContext.setAttribute(KEY, editor, SCOPE);

        return EVAL_BODY;
    }

    /**
     * Pass all gathered information to the implementing editor, get the
     * the result back and write it to the page.
     *
     */
    public int doEndTag() throws JspTagException {

        fillStandardParameters(editor.getParameters());
        editor.getParameters().checkRequiredParameters();
        try {
            editor.getEditorHTML(getPageContext());
        } catch (IOException ioe) {
            log.error("Error writing to PageContext: " + ioe.getMessage(), ioe);
        }
        pageContext.setAttribute(KEY, prevEditor, SCOPE);
        // for gc:
        editor = null;
        prevEditor = null;
        return super.doEndTag();
    }



    // if EVAL_BODY == EVAL_BODY_BUFFERED
    public int doAfterBody() throws JspTagException {
        if (EVAL_BODY == EVAL_BODY_BUFFERED && bodyContent != null) {
            try {
                bodyContent.writeOut(bodyContent.getEnclosingWriter());
            } catch (IOException ioe) {
                throw new TaglibException(ioe);
            }
        }
        return SKIP_BODY;
    }


    static class EditorDefinition {
        private final Class<?> clazz;
        private final Map<String, String>   params = new HashMap<String, String>(); /* String -> String */

        public EditorDefinition(Element element) throws ClassNotFoundException {
            Class<?> c = null;
            org.w3c.dom.NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                if (childNodes.item(i) instanceof Element) {
                    Element childElement = (Element) childNodes.item(i);
                    if (childElement.getLocalName().equals("class")) {
                        String className = DocumentReader.getNodeTextValue(childElement);
                        c = Class.forName(className);
                    }  else if (childElement.getLocalName().equals("param")) {
                        String name = childElement.getAttribute("name");
                        String value = DocumentReader.getNodeTextValue(childElement);
                        if (params.put(name, value) != null) {
                            log.error("Parameter '" + name + "' is defined more than once in " + XMLWriter.write(element, true));
                        }

                    }
                }
            }
            clazz = c;

        }

        public Editor newInstance() {
            Editor res = null;
            try {
                res =  (Editor) clazz.newInstance();
            } catch (InstantiationException ie) {
                log.error("Unable to instantiate class '" + clazz + "': " + ie);
            } catch (IllegalAccessException iae) {
                log.error("IllegalAccessException instantiating class " + clazz+ "': " + iae);
            }
            if (res != null && params.size() > 0) {
                Parameters editorParams = res.getParameters();
                editorParams.setAll(params);
            }
            return res;
        }
    }

}

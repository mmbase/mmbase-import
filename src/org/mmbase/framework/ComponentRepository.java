/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import org.mmbase.util.*;

import org.w3c.dom.*;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The class maintains all compoments which are registered in the current MMBase.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ComponentRepository.java,v 1.2 2006-08-30 20:46:05 michiel Exp $
 * @since MMBase-1.9
 */
public class ComponentRepository {

    public static final String XSD_COMPONENT = "component.xsd";
    public static final String NAMESPACE = "http://www.mmbase.org/xmlns/component";
    static {
        XMLEntityResolver.registerSystemID(NAMESPACE + ".xsd", XSD_COMPONENT, ComponentRepository.class);
    }

    private static final Logger log = Logging.getLoggerInstance(ComponentRepository.class);

    private  static final ComponentRepository repository = new ComponentRepository();
    public static ComponentRepository getInstance() {
        return repository;
    }

    private Map<String, Component> rep = new HashMap<String, Component>();

    private ComponentRepository() {
        readConfiguration();
    }

    public Collection<Component> getComponents() {
        return Collections.unmodifiableCollection(rep.values());
    }

    public Component getComponent(String name) {
        return rep.get(name);
    }

    public void shutdown() {
        rep.clear();
    }

    protected void readConfiguration() {
        ResourceLoader loader =  ResourceLoader.getConfigurationRoot().getChildResourceLoader("components");
        Collection<String> components = loader.getResourcePaths(ResourceLoader.XML_PATTERN, true /* recursive*/);
        log.info("In " + loader + " the following components XML's were found " + components);
        for (String file : components) {
            try {
                Document doc = loader.getDocument(file);
                String name = doc.getDocumentElement().getAttribute("name");
                String fileName = ResourceLoader.getName(file);
                if (! fileName.equals(name)) {
                    log.warn("Component " + name + " is defined in resource with name " + file);
                }
                rep.put(name, getComponent(name, doc));
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        log.info("Found the following components " + getComponents());

    }

    protected Component getComponent(String name, Document doc) throws org.xml.sax.SAXException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        Class claz = Class.forName(org.mmbase.util.xml.DocumentReader.getNodeTextValue(doc.getDocumentElement().getElementsByTagName("class").item(0)));
        Component comp = (Component) claz.getConstructor(String.class).newInstance(name);
        comp.configure(doc);
        return comp;
    }

}

/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import java.lang.reflect.*;
import org.w3c.dom.*;
import org.mmbase.util.*;


import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The class maintains all compoments which are registered in the current MMBase.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ComponentRepository.java,v 1.5 2006-10-13 23:00:03 johannes Exp $
 * @since MMBase-1.9
 */
public class ComponentRepository {

    public static final String XSD_COMPONENT = "component.xsd";
    public static final String NAMESPACE = "http://www.mmbase.org/xmlns/component";
    static {
        XMLEntityResolver.registerSystemID(NAMESPACE + ".xsd", XSD_COMPONENT, ComponentRepository.class);
    }

    private static final Logger log = Logging.getLoggerInstance(ComponentRepository.class);

    private static final ComponentRepository repository = new ComponentRepository();

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

    public static Object getInstance(Element classElement, Object... args) throws org.xml.sax.SAXException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        Class claz = Class.forName(classElement.getAttribute("name"));
        List<Class> argTypes = new ArrayList<Class>(args.length);
        for (Object arg : argTypes) {
            argTypes.add(arg.getClass());
        }
        Object o = claz.getConstructor(argTypes.toArray(new Class[] {})).newInstance(args);

        NodeList params = classElement.getElementsByTagName("param");
        for (int i = 0 ; i < params.getLength(); i++) {
            Element param = (Element) params.item(i);
            String name = param.getAttribute("name");
            String value = org.mmbase.util.xml.DocumentReader.getNodeTextValue(param);
            Method method = claz.getMethod("set" + name.substring(0, 1).toUpperCase() + name.substring(1), String.class);
            method.invoke(o, value);
        }
        return o;
    }


    protected Component getComponent(String name, Document doc) throws org.xml.sax.SAXException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        Element classElement = (Element) doc.getDocumentElement().getElementsByTagName("class").item(0);
        Component component;
        if (classElement == null) {
            component = new BasicComponent(name);
        } else {
            component = (Component) getInstance(classElement, name);
        }
        component.configure(doc.getDocumentElement()); 
        return component;
    }

}

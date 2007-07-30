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
 * @version $Id: ComponentRepository.java,v 1.23 2007-07-30 23:51:41 michiel Exp $
 * @since MMBase-1.9
 */
public class ComponentRepository {

    public static final String XSD_COMPONENT = "component.xsd";
    public static final String NAMESPACE = "http://www.mmbase.org/xmlns/component";

    public static final String XSD_FRAMEWORK = "framework.xsd";
    public static final String NAMESPACE_FRAMEWORK = "http://www.mmbase.org/xmlns/framework";
    static {
        XMLEntityResolver.registerSystemID(NAMESPACE + ".xsd", XSD_COMPONENT, ComponentRepository.class);
        XMLEntityResolver.registerSystemID(NAMESPACE_FRAMEWORK + ".xsd", XSD_COMPONENT, ComponentRepository.class);
    }

    private static final Logger log = Logging.getLoggerInstance(ComponentRepository.class);

    private static final ComponentRepository repository = new ComponentRepository();

    public static ComponentRepository getInstance() {
        return repository;
    }
    static {
        ResourceWatcher rw = new ResourceWatcher() {
                public void onChange(String r) {
                    getInstance().readConfiguration(r);
                }
            };
        rw.add("components");
        rw.onChange();
        rw.setDelay(2 * 1000); // 2 s
        rw.start();

    }

    private Map<String, Component> rep = new HashMap<String, Component>();
    private List<Component> failed = new ArrayList<Component>();

    private ComponentRepository() { }

    public Block.Type[] getBlockClassification(String id) {
        if (id == null) {
            return new Block.Type[] {Block.Type.ROOT};
        } else {
            return Block.Type.getClassification(id, false);
        }


    }

    public Collection<Component> getComponents() {
        return Collections.unmodifiableCollection(rep.values());
    }

    
    public Collection<Component> getFailedComponents() {
        return Collections.unmodifiableCollection(failed);
    }

    public Component getComponent(String name) {
        return rep.get(name);
    }

    protected boolean resolve() {
        int resolved = 0;
        int unsatisfied = 0;
        for (Component comp : getComponents()) {
            Collection<VirtualComponent> us = comp.getUnsatisfiedDependencies();
            for (VirtualComponent virtual :  us) {
                Component proposed = getComponent(virtual.getName());
                if (proposed != null) {
                    resolved++;
                    if (proposed.getVersion() >= virtual.getVersion()) {
                        comp.resolve(virtual, proposed);
                    } else {
                        unsatisfied++;
                        log.warn("" + comp + " depends on " + virtual + " but the version of " + proposed + " is only " + proposed.getVersion());
                    }
                } else {
                    log.warn("" + comp + " depends on " + virtual + " but no such component.");
                }
            }
        }

        return unsatisfied == 0;
    }


    public void shutdown() {
        clear();
    }
    protected void clear() {
        Block.Type.ROOT.subs.clear();
        Block.Type.ROOT.blocks.clear();
        Block.Type.NO.subs.clear();
        Block.Type.NO.blocks.clear();
        rep.clear();
    }

    /**
     * Reads all component xmls
     */
    protected void readConfiguration(String child) {
        clear();
        ResourceLoader loader =  ResourceLoader.getConfigurationRoot().getChildResourceLoader(child);
        Collection<String> components = loader.getResourcePaths(ResourceLoader.XML_PATTERN, true /* recursive*/);
        log.info("In " + loader + " the following components XML's were found " + components);
        for (String file : components) {
            try {
                Document doc = loader.getDocument(file, true, getClass());
                String name = doc.getDocumentElement().getAttribute("name");
                String fileName = ResourceLoader.getName(file);
                if (! fileName.equals(name)) {
                    log.warn("Component " + name + " is defined in resource with name " + file);
                } else {
                    log.service("Instantatiating component '" + name + "'");
                }
                if (rep.containsKey(name)) {
                    failed.add(getComponent(name, doc));
                    Component org = rep.get(name);
                    log.error("There is already a component with name '" + name + "' (" + org.getUri() + "), " + doc.getDocumentURI() + " defines another one, which is now ignored");
                } else {
                    rep.put(name, getComponent(name, doc));
                }
            } catch (Exception e) {
                log.error("For " + loader.getResource(file) + ": " + e.getMessage(), e);
            }
        }
        if (! resolve()) {
            log.error("Not all components satisfied their dependencies");
        }
        log.info("Found the following components " + getComponents());

    }

    /**
     * Instantaties any object using an Dom Element and constructor arguments. Sub-param tags are
     * used on set-methods on the newly created object. This is a pretty generic method, it should
     * perhaps be moved to org.mmbase.util.
     */
    public static Object getInstance(Element classElement, Object... args) 
        throws org.xml.sax.SAXException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        String className = classElement.getAttribute("name");
        if ("".equals(className)) className = classElement.getAttribute("class"); // for urlconverters config (not ok yet)
        Class claz = Class.forName(className);
        List<Class> argTypes = new ArrayList<Class>(args.length);
        for (Object arg : args) {
            argTypes.add(arg.getClass());
        }
        Class[] argTypesArray = argTypes.toArray(new Class[] {});
        Constructor constructor = null;
        for (Constructor c : claz.getConstructors()) {
            Class[] parameterTypes = c.getParameterTypes();
            if (parameterTypes.length != argTypesArray.length) continue;
            for (int i = 0; i < parameterTypes.length; i++) {
                if (! parameterTypes[i].isAssignableFrom(argTypesArray[i])) continue;
            }
            constructor = c;
            break;
        }
        if (constructor == null) throw new NoSuchMethodError();
        Object o = constructor.newInstance(args);

        NodeList params = classElement.getChildNodes();
        for (int i = 0 ; i < params.getLength(); i++) {
            try {
                Node node = params.item(i);
                if (node instanceof Element && node.getNodeName().equals("param")) {
                    Element param = (Element)node;
                    String name = param.getAttribute("name");
                    String value = org.mmbase.util.xml.DocumentReader.getNodeTextValue(param);
                    Method method = claz.getMethod("set" + name.substring(0, 1).toUpperCase() + name.substring(1), String.class);
                    method.invoke(o, value);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return o;
    }

    public static Object getInstanceWithSubElement(Element element, Object... args) throws org.xml.sax.SAXException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
        NodeList childs =  element.getChildNodes();
        Object instance = null;
        for (int i = 0; i < childs.getLength(); i++) {
            Node node = childs.item(i);
            if (node instanceof Element && node.getNodeName().equals("class")) {
                instance =  getInstance((Element) node, args);
            }
        }
        return instance;
    }


    /**
     * Given  an XML, creates and configures one component.
     */
    protected Component getComponent(String name, Document doc) throws org.xml.sax.SAXException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {

        Component component = (Component) getInstanceWithSubElement(doc.getDocumentElement(), name);
        if (component == null) {
            component = new BasicComponent(name);
        }
        component.configure(doc.getDocumentElement()); 
        return component;
    }

}

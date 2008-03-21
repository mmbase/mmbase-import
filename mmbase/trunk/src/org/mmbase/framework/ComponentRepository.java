/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.framework;

import java.util.*;
import org.w3c.dom.*;
import org.mmbase.util.*;
import org.mmbase.util.xml.Instantiator;



import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This (singleton) class maintains all compoments which are registered in the current MMBase.
 *
 * @author Michiel Meeuwissen
 * @version $Id: ComponentRepository.java,v 1.31 2008-03-21 09:26:33 fpunt Exp $
 * @since MMBase-1.9
 */
public class ComponentRepository {

    public static final String XSD_COMPONENT = "component.xsd";
    public static final String NAMESPACE_COMPONENT = "http://www.mmbase.org/xmlns/component";

    public static final String XSD_FRAMEWORK = "framework.xsd";
    public static final String NAMESPACE_FRAMEWORK = "http://www.mmbase.org/xmlns/framework";
    static {
        XMLEntityResolver.registerSystemID(NAMESPACE_COMPONENT + ".xsd", XSD_COMPONENT, ComponentRepository.class);
        XMLEntityResolver.registerSystemID(NAMESPACE_FRAMEWORK + ".xsd", XSD_FRAMEWORK, ComponentRepository.class);
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

    private final Map<String, Component> rep = new HashMap<String, Component>();
    private final List<Component> failed     = new ArrayList<Component>();

    private ComponentRepository() { }

    /**
     * @javadoc
     */
    public Block.Type[] getBlockClassification(String id) {
        if (id == null) {
            return new Block.Type[] {Block.Type.ROOT};
        } else {
            return Block.Type.getClassification(id, false);
        }


    }

    /**
     * The available components.
     */
    public Collection<Component> getComponents() {
        return Collections.unmodifiableCollection(rep.values());
    }

    /**
     * The components which could not be instantiated or configured, due to some
     * misconfiguration.
     */
    public Collection<Component> getFailedComponents() {
        return Collections.unmodifiableCollection(failed);
    }

    /**
     * Acquires the component with given name, or <code>null</code> if no such component.
     */
    public Component getComponent(String name) {
        return rep.get(name);
    }

    public Block getBlock(String componentName, String blockName) {
        Component component = getComponent(componentName);
        if (component == null) throw new IllegalArgumentException("No component with name '" + componentName + "'");
        return component.getBlock(blockName);
    }

    protected boolean resolve() {
        int unsatisfied = 0;
        for (Component comp : getComponents()) {
            for (VirtualComponent virtual :  comp.getUnsatisfiedDependencies()) {
                Component proposed = getComponent(virtual.getName());
                if (proposed != null) {
                    if (proposed.getVersion() >= virtual.getVersion()) {
                        comp.resolve(virtual, proposed);
                    } else {
                        unsatisfied++;
                        log.warn("" + comp + " depends on " + virtual + " but the version of " + proposed + " is only " + proposed.getVersion());
                    }
                } else {
                    unsatisfied++;
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
        failed.clear();
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
                String namespace = doc.getDocumentElement().getNamespaceURI();
                String name = doc.getDocumentElement().getAttribute("name");
                String fileName = ResourceLoader.getName(file);
                if (! fileName.equals(name)) {
                    log.warn("Component " + name + " is defined in resource with name " + file);
                } else {
                    log.service("Instantiating component '" + name + "' " + namespace);
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
     * Given  an XML, creates and configures one component.
     */
    protected Component getComponent(String name, Document doc)
        throws org.xml.sax.SAXException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {

        Component component = (Component) Instantiator.getInstanceWithSubElement(doc.getDocumentElement(), name);
        if (component == null) {
            component = new BasicComponent(name);
        }
        component.configure(doc.getDocumentElement());
        return component;
    }
}

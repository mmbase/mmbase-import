/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.applications.templateengine;

import java.io.*;
import java.lang.reflect.*;

import org.mmbase.applications.templateengine.jsp.*;
import simplexml.*;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.logging.*;
/**
 * registry of the reusable components
 * @author keesj
 * @version $Id: ComponentRegistry.java,v 1.1.1.1 2004-04-02 14:58:47 keesj Exp $
  */
public class ComponentRegistry {
    private static Logger log = Logging.getLoggerInstance(ComponentRegistry.class);
    private Components components;
    private Templates templates;
    private Containers containers;
    private LayoutManagers layoutManagers;

    private static ComponentRegistry componentRegistry = null;

    private ComponentRegistry() {
        components = new Components();
        templates = new Templates();
        containers = new Containers();
        layoutManagers = new LayoutManagers();
        updateRegistry();
    }

    public static ComponentRegistry getInstance() {
        synchronized (ComponentRegistry.class) {
            if (componentRegistry == null)
                componentRegistry = new ComponentRegistry();
        }
        return componentRegistry;
    }

    public Components getComponents() {
        Components c = new Components();
        c.addAll(components);
        return c;
    }

    public Component getComponent(String name) {
        try {
            Component g = components.getComponentByName(name);
            if (g != null)
                return (Component) g.clone();
        } catch (CloneNotSupportedException e) {
            log.error("component " + name + " (class) does not allow cloning:" + Logging.stackTrace(e));
        }
        log.warn("component {" + name + "} is not registerd");
        return null;
    }

    public Containers getContainers() {
        Containers c = new Containers();
        c.addAll(containers);
        return c;
    }

    public Container getContainer(String name) {
        try {
            Container c = containers.getContainerByName(name);
            if (c != null)
                return (Container) c.clone();
        } catch (CloneNotSupportedException e) {
            log.error("container " + name + " (class) does not allow cloning:" + Logging.stackTrace(e));
        }
        log.warn("container {" + name + "} is not registerd");
        return null;
    }

    /**
     * @return
     */
    public LayoutManagers getLayoutManagers() {
        LayoutManagers l = new LayoutManagers();
        l.addAll(layoutManagers);
        return l;
    }

    public LayoutManager getLayoutManager(String name) {
        return layoutManagers.getLayoutManagerByName(name);
    }

    public Templates getTemplates() {
        Templates t = new Templates();
        t.addAll(templates);
        return t;
    }
    /**
     * @return
     */
    public Template getTemplate(String name) {
        try {
            Template t = templates.getTemplateByName(name);
            if (t != null)
                return (Template) t.clone();
        } catch (CloneNotSupportedException e) {
            log.error("template " + name + " (class) does not allow cloning:" + Logging.stackTrace(e));
        }
        log.warn("template {" + name + "} is not registerd");
        return null;

    }

    public void updateRegistry() {
        //MMBaseContext.getConfigPath()
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(MMBaseContext.getConfigPath() + "/te", "registry.xml"))));
        } catch (FileNotFoundException e4) {
            // TODO Auto-generated catch block
            String message = "can not find " + MMBaseContext.getConfigPath() + "/te/registry.xml";
            log.warn(message);
            //throw new RuntimeException(message,e4); jva 1.4
            throw new RuntimeException(message + Logging.stackTrace(e4));
        }
        StringWriter sw = new StringWriter();
        String data = null;
        try {
            while ((data = br.readLine()) != null) {
                sw.write(data);
            }
        } catch (IOException e) {
            throw new RuntimeException("can not load xml");
        }
        String xmlData = sw.toString();
        XMLElement xmle = new XMLElement();
        xmle.parseString(xmlData);
        if (!xmle.getTagName().equals("registry"))
            log.error("error while reading the registry root node not regsitry but " + xmle.getTagName());

        for (int x = 0; x < xmle.countChildren(); x++) {
            XMLElement child = xmle.getChildAt(x);

            //layout manager
            if (child.getTagName().equals("layoutManager")) {
                //<layoutManager class="te.jsp.JSPLayoutManager" name="default" description="default list layout">
                //	<property name="jspPage">/te/layout/default.jsp</property>
                //</layoutManager>
                if (child.getProperty("class") != null && !child.getProperty("class").equals("org.mmbase.applications.templateengine.jsp.JSPLayoutManager")) {
                    log.error("layoutManager class not of type  te.jsp.JSPLayoutManager {" + child.getProperty("class") + "}");
                }
                String name = child.getProperty("name");
                String description = child.getProperty("description");
                for (int y = 0; y < child.countChildren(); y++) {
                    XMLElement property = child.getChildAt(y);
                    if (!property.getTagName().equals("property")) {
                        log.error("unknown subtab of " + child.getTagName() + " named " + property.getTagName());
                        continue;
                    }
                    String propertyName = property.getProperty("name");

                    if (propertyName.equals("jspPage")) {
                        String path = property.getContents();
                        LayoutManager lam = layoutManagers.getLayoutManagerByName(name);
                        log.debug("adding layout manager with name " + name);
                        synchronized (layoutManagers) {
                            if (lam != null) {
                                log.debug("updating layout manager");
                                layoutManagers.remove(lam);
                            }
                            layoutManagers.add(new JSPLayoutManager(path, name, description));
                        }
                    } else {
                        log.error("unknown property of layout manager");
                    }
                }
            } else if (child.getTagName().equals("container")) {
                //<container class="te.jsp.JSPContainer" path="/te/container/default.jsp" name="horizontal" description="a container with a horizontal layout" layout="horizontal"/>
                String className = child.getProperty("class");
                String name = child.getProperty("name");
                String path = child.getProperty("path");
                String description = child.getProperty("description");
                String layout = child.getProperty("layout");
                if (!className.equals("org.mmbase.applications.templateengine.jsp.JSPContainer")) {
                    log.error("currently impossible to load containers that are not org.mmbase.applications.templateengine.jsp.JSPContainer :" + child);
                    continue;
                }
                if (className == null || name == null || path == null || description == null || layout == null) {
                    log.error(
                        "container can not be loaded because some parameters are missing(className,name,path,description,layout)("
                            + className
                            + ","
                            + name
                            + ","
                            + path
                            + ","
                            + description
                            + ","
                            + layout
                            + ")");
                    continue;
                }
                Container c = containers.getContainerByName(name);
                if (c != null) {
                    log.debug("updating container");
                    containers.remove(c);
                }
                LayoutManager lom = layoutManagers.getLayoutManagerByName(layout);
                if (lom == null) {
                    log.error("while loading the layout manager for a container the layout manager(" + layout + ") was not registered :" + child);
                    continue;
                }
                log.debug("adding container with name " + name);
                c = new JSPContainer(path, lom);
                c.setName(name);
                c.setDescription(description);
                containers.add(c);
            } else if (child.getTagName().equals("template")) {
                String className = child.getProperty("class");
                String name = child.getProperty("name");
                String path = child.getProperty("path");
                String description = child.getProperty("description");
                String layout = child.getProperty("layout");
                try {
                    Class clazz = Class.forName(className);
                    Class[] interfaces = clazz.getInterfaces();
                    boolean isTemplate = false;
                    for (int g = 0; g < interfaces.length; g++) {
                        Class inter = interfaces[g];
                        inter.getName().equals("org.mmbase.applications.templateengine.Template");
                        isTemplate = true;
                    }
                    if (!isTemplate) {
                        log.error(className + " does not implement te.Template so it's not a template");
                        continue;
                    }

                    try {
                        Constructor constructor = clazz.getConstructor(new Class[] { String.class, LayoutManager.class });

                        if (className == null || name == null || path == null || description == null || layout == null) {
                            log.error(
                                "template can not be loaded because some parameters are missing(className,name,path,description,layout)("
                                    + className
                                    + ","
                                    + name
                                    + ","
                                    + path
                                    + ","
                                    + description
                                    + ","
                                    + layout
                                    + ")");
                            continue;
                        }

                        Template t = templates.getTemplateByName(name);
                        if (t != null) {
                            log.debug("updating template");
                            templates.remove(t);
                        }

                        LayoutManager lom = layoutManagers.getLayoutManagerByName(layout);
                        if (lom == null) {
                            log.error("while loading the layout manager for a template the layout manager(" + layout + ") was not registered :" + child);
                            continue;
                        }

                        log.debug("adding template with name " + name);
                        try {
                            t = (Template) constructor.newInstance(new Object[] { path, lom });
                            t.setName(name);
                            t.setDescription(description);
                            templates.add(t);
                        } catch (IllegalArgumentException e3) {
                            log.error(Logging.stackTrace(e3));
                        } catch (InstantiationException e3) {
                            log.error(Logging.stackTrace(e3));
                        } catch (IllegalAccessException e3) {
                            log.error(Logging.stackTrace(e3));
                        } catch (InvocationTargetException e3) {
                            log.error(Logging.stackTrace(e3));
                        }
                    } catch (SecurityException e2) {
                        log.error(Logging.stackTrace(e2));
                        continue;
                    } catch (NoSuchMethodException e2) {
                        log.error("class " + className + " does not contain a constructor with parameter String,LayoutManager");
                        e2.printStackTrace();
                        continue;
                    }

                } catch (ClassNotFoundException e1) {
                    log.error("can not find class {" + className + "} for configuration element " + child);
                    continue;
                }

            } else if (child.getTagName().equals("component")) {
                String className = child.getProperty("class");
                String name = child.getProperty("name");
                String path = child.getProperty("path");
                String description = child.getProperty("description");
                if (className == null || name == null || description == null) {
                    log.error(
                        "component can not be loaded because some parameters are missing(className,name,description)("
                            + className
                            + ","
                            + name
                            + ","
                            + path
                            + ","
                            + description
                            + ")");
                    continue;
                }
                Component c = components.getComponentByName(name);
                if (c != null) {
                    log.debug("replacing component " + name);
                    components.remove(c);
                }
                if (className.equals("org.mmbase.applications.templateengine.jsp.JSPComponent")) {
                    if (path == null) {
                        log.error("can not create a jsp component for " + child + " attribute path missing");
                    }
                    c = new JSPComponent(path);
                    c.setName(name);
                    c.setDescription(description);
                } else {
                    Class clazz = null;
                    try {
                        clazz = Class.forName(className);
                    } catch (ClassNotFoundException e1) {
                        log.error("class " + className + " not found, wil not initialize the component ()" + child + "");
                        continue;
                    }
                    try {
                        Object o = clazz.newInstance();
                        if (o instanceof Component) {
                            c = (Component) o;
                            c.setName(name);
                            c.setDescription(description);

                            try {
                                Object cloned = c.clone();
                                if (!cloned.getClass().getName().equals(clazz.getName())) {
                                    log.error(
                                        "The component "
                                            + name
                                            + " (class "
                                            + c.getClass().getName()
                                            + ") doe supports cloning the the clone method does not return the same object type. reusable component should have a properly implemented clone method. ik will not be added ("
                                            + child
                                            + ")");
                                    continue;
                                }
                            } catch (CloneNotSupportedException e3) {
                                log.error("The component " + name + " (class " + c.getClass().getName() + ") doe not support the encoding and wil not be added");
                                continue;
                            }
                        } else {
                            log.error(clazz.getName() + "  is does not implement the Component interface. skipping");
                        }
                    } catch (InstantiationException e2) {
                        log.error("class " + name + " can not be instantiated (no empty constructor . skipping?)()" + child + "");
                        continue;
                    } catch (IllegalAccessException e2) {
                        log.error("class " + name + " can not be instantiated (IllegalAccessException)()" + child + "");
                    }

                }
                log.debug("adding component with name " + name);
                components.add(c);
            } else {
                log.error("found unknown component in the registry:" + child);
            }
        }
    }
}

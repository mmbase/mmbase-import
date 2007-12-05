/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.xml;

import java.util.*;
import java.lang.reflect.*;
import org.w3c.dom.*;

import org.mmbase.util.logging.*;

/**
 * Utilities to use an XML to instantiate Java objects, using reflection.
 *
 * @since MMBase-1.9
 * @author Michiel Meeuwissen
 * @version $Id: Instantiator.java,v 1.1 2007-12-05 16:31:51 michiel Exp $
 */
public abstract class Instantiator {

    private static final Logger log = Logging.getLoggerInstance(Instantiator.class);

    /**
     * Instantiates any object using an Dom Element and constructor arguments. Sub-param tags are
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
        if (constructor == null) throw new NoSuchMethodError("No constructors found for " + args);
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



}

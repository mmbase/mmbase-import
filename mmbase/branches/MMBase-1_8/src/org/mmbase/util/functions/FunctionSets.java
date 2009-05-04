/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.mmbase.util.xml.DocumentReader;
import org.mmbase.util.*;
import org.mmbase.module.core.*;

import java.io.*;
import java.util.*;
import org.xml.sax.InputSource;
import org.w3c.dom.*;
import java.net.*;



/**
 * A utility class for maintaining and querying functionsets.
 * A set function belongs to a certain namespace of functions ('sets'), and therefore is identified by
 * two strings: The name of the 'set' and the name of the function.
 * <br />
 * Function sets can be defined in the functions/functionsets.xml configuration file.
 * <br />
 * This class implements a number of static methods for maintaining {@link FunctionSet} objects,
 * and filling these with {@link FunctionSet} objects that match the namespace.
 * It also implements a {@link #getFunction} method for obtaining a function from such a set.
 *
 * @author Dani&euml;l Ockeloen
 * @author Michiel Meeuwissen
 * @since  MMBase-1.8
 * @version $Id$
 */
public class FunctionSets {

    public static final String DTD_FUNCTIONSET_1_0  = "functionset_1_0.dtd";
    public static final String DTD_FUNCTIONSETS_1_0 = "functionsets_1_0.dtd";

    public static final String PUBLIC_ID_FUNCTIONSET_1_0  = "-//MMBase//DTD functionset config 1.0//EN";
    public static final String PUBLIC_ID_FUNCTIONSETS_1_0 = "-//MMBase//DTD functionsets config 1.0//EN";

    private static final Logger log = Logging.getLoggerInstance(FunctionSets.class);

    private static final Map functionSets = new HashMap();

    static {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_FUNCTIONSET_1_0,  DTD_FUNCTIONSET_1_0,  FunctionSets.class);
        XMLEntityResolver.registerPublicID(PUBLIC_ID_FUNCTIONSETS_1_0, DTD_FUNCTIONSETS_1_0, FunctionSets.class);
    }

    /**
     * Returns the {@link Function} with the given function name, and which exists in the set with the given set name.
     * If this is the first call, or if the set does not exist in the cache, the cache
     * is refreshed by reading the functionset.xml configuration file.
     * @param setName the name of the function set
     * @param functionName the name of the function
     * @return the {@link Function}, or <code>nulll</code> if either the fucntion or set is not defined
     */
    public static Function getFunction(String setName, String functionName) {
        FunctionSet set = getFunctionSet(setName);
        if (set != null) {
            Function fun = set.getFunction(functionName);
            if (fun != null) {
                return fun;
            } else {
                log.warn("No function with name : " + functionName + " in set : " + setName + ", functions available: " + set);
            }
        } else {
            log.warn("No functionset with name : " + setName);
        }
        return null;
    }

    static {
        ResourceLoader functionLoader = ResourceLoader.getConfigurationRoot().getChildResourceLoader("functions");
        // read the XML
        try {
            ResourceWatcher watcher = new ResourceWatcher(functionLoader) {
                    public void onChange(String resource) {
                        functionSets.clear();
                        clear();
                        add(resource);
                        readSets(this);
                    }
                };
            watcher.start();
            watcher.onChange("functionsets.xml");
        } catch (Throwable t) {
            log.error(t.getClass().getName() + ": " + Logging.stackTrace(t));
        }

    }

    /**
     * Returns the {@link FunctionSet} with the given set name.
     * If this is the first call, or if the set does not exist in the cache, the cache
     * is refreshed by reading the functionset.xml configuration file.
     * configuration file.
     * @param setName the name of the function set
     * @return the {@link FunctionSet}, or <code>null</code> if the set is not defined
     */
    public static FunctionSet getFunctionSet(String setName) {
        return (FunctionSet)functionSets.get(setName);
    }

    /**
     * Reads the current function set from the functionsets.xml configuration file.
     * The read sets are added to the functionset cache.
     * @todo It makes FunctionSet's now using a sub-XML. It would be possible to create a complete function-set by reflection.
     */

    private static void readSets(ResourceWatcher watcher) {

        List resources = watcher.getResourceLoader().getResourceList("functionsets.xml");
        log.service("Using " + resources);
        ListIterator i = resources.listIterator();
        while (i.hasNext()) i.next();
        while (i.hasPrevious()) {
            try {
                URL u = (URL) i.previous();
                log.service("Reading " + u);
                URLConnection con = u.openConnection();
                if (con.getDoInput()) {
                    InputSource source = new InputSource(con.getInputStream());
                    DocumentReader reader = new DocumentReader(source, FunctionSets.class);

                    for(Iterator ns = reader.getChildElements("functionsets", "functionset"); ns.hasNext(); ) {
                        Element n = (Element)ns.next();

                        String setName     = n.getAttribute("name");
                        if (functionSets.containsKey(setName)) {
                            log.warn("The function-set '" + setName + "' did exist already");
                        }
                        String setResource = n.getAttribute("resource");
                        if (setResource.equals("")) setResource = n.getAttribute("file"); // deprecated, it's not necessarily a file
                        watcher.add(setResource);
                        decodeFunctionSet(watcher.getResourceLoader(), setResource, setName);
                    }
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    /**
     * Reads a 'sub' xml (a functionset XML) referred to by functionsets.xml.
     * The read set is added to the functionset cache.
     * @param
     * @param
     */
    private static void decodeFunctionSet(ResourceLoader loader, String setResource, String setName) throws IOException {
        DocumentReader reader = new DocumentReader(loader.getInputSource(setResource), FunctionSets.class);

        String setDescription = reader.getElementValue("functionset.description");

        FunctionSet functionSet = new FunctionSet(setName, setDescription);
        functionSets.put(setName, functionSet);

        for (Iterator functionElements = reader.getChildElements("functionset", "function"); functionElements.hasNext();) {
            Element element = (Element)functionElements.next();
            String functionName = reader.getElementAttributeValue(element,"name");
            if (functionName != null) {

                Element a = reader.getElementByPath(element, "function.type");

                String type = reader.getElementValue(a); // 'class' or 'instance'

                a = reader.getElementByPath(element, "function.description");
                String description = reader.getElementValue(a);

                a = reader.getElementByPath(element, "function.class");
                String className = reader.getElementValue(a);

                a = reader.getElementByPath(element, "function.method");
                String methodName = reader.getElementValue(a);

                // read the return types and values
                a = reader.getElementByPath(element, "function.return");
               	ReturnType returnType = null;
		if (a != null) {
                    String returnTypeClassName = reader.getElementAttributeValue(a, "type");
                    if (returnTypeClassName != null) {
                        try {
                            Class returnTypeClass = getClassFromName(returnTypeClassName);
                            returnType = new ReturnType(returnTypeClass, "");
                        } catch (Exception e) {
                            log.warn("Cannot determine return type : " + returnTypeClassName + ", will auto-detect");
                        }
                    }
                }


                /* obtaining field definitions for a result Node... useful ??

                for (Enumeration n2 = reader.getChildElements(a, "field"); n2.hasMoreElements();) {
                    Element return_element = (Element)n2.nextElement();
                    String returnFieldName = reader.getElementAttributeValue(return_element, "name");
                    String returnFieldValueType = reader.getElementAttributeValue(return_element, "type");
                    String returnFieldDescription = reader.getElementAttributeValue(return_element, "description");
                    // not implemented (yet) :
                    // FunctionReturnValue r=new FunctionReturnValue(returnname,returnvaluetype);
                    // fun.addReturnValue(returnname,r);
                    // r.setDescription(description);
                }
                */

                // read the parameters
                List parameterList = new ArrayList();
                for (Iterator parameterElements = reader.getChildElements(element,"param"); parameterElements.hasNext();) {
                    Element parameterElement = (Element)parameterElements.next();
                    String parameterName = reader.getElementAttributeValue(parameterElement, "name");
                    String parameterType = reader.getElementAttributeValue(parameterElement, "type");
                    String required = reader.getElementAttributeValue(parameterElement, "required");
                    description = reader.getElementAttributeValue(parameterElement, "description");

                    Parameter parameter = null;

                    Class parameterClass = getClassFromName(parameterType);
                    parameter = new Parameter(parameterName, parameterClass);
                    parameter.dataType.setRequired("true".equals(required));
                    
//                  check for a default value
                    org.w3c.dom.Node n3 = parameterElement.getFirstChild();
                    if (n3 != null) {
                        parameter.setDefaultValue(parameter.autoCast(n3.getNodeValue()));
                    }

                    if (parameterClass.isPrimitive() && parameter.getDefaultValue() == null && ! parameter.isRequired()) {
                        // that would give enigmatic IllegalArgumentExceptions, so fix that.
                        parameter.setDefaultValue(Casting.toType(parameterClass, new Integer(-1)));
                        log.debug("Primitive parameter '" + parameterName + "' had default value null, which is impossible for primitive types. Setting to " + parameter.getDefaultValue());
                    }
                    
                    parameterList.add(parameter);

                }

                Parameter[] parameters = (Parameter[]) parameterList.toArray(new Parameter[0]);

                try {
                    SetFunction fun = new SetFunction(functionName, parameters, returnType, className, methodName);
                    fun.setType(type);
                    fun.setDescription(description);
                    functionSet.addFunction(fun);
                } catch (Exception e) {
                    log.error(e);
                    log.error(Logging.stackTrace(e));
                    log.error(Logging.stackTrace(e.getCause()));
                }
            }
        }
    }

    /**
     * Tries to determine the correct class from a given classname.
     * Classnames that are not fully expanded are expanded to the java.lang package.
     */
    private static Class getClassFromName(String className) {
        String fullClassName = className;
        boolean fullyQualified = className.indexOf('.') > -1;
        if (!fullyQualified) {
            if (className.equals("int")) { // needed?
                return int.class;
            } else if (className.equals("NodeList")) {
                return org.mmbase.bridge.NodeList.class;
            } else if (className.equals("Node")) {
                return org.mmbase.bridge.Node.class;
            }
            fullClassName = "java.lang." + Character.toUpperCase(fullClassName.charAt(0)) + fullClassName.substring(1);
        }
        try {
            return Class.forName(fullClassName);
        } catch (ClassNotFoundException cne) {
            log.warn("Cannot determine parameter type : '" + className + "' (expanded to: '" + fullClassName + "'), using Object as type instead.");
            return Object.class;
        }
    }

}

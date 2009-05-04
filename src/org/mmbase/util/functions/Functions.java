/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util.functions;

import java.lang.reflect.*;
import java.lang.reflect.Field;
import java.util.*;

import org.mmbase.util.logging.*;


/**
 * This class defines static methods for defining Function and Parameters objects.
 * These methods include ways to retrieve Function definitions for a class using reflection,
 * and methods to convert a List to a Parameters object, and a Parameter array to a
 * List.
 *
 * @since MMBase-1.8
 * @author Pierre van Rooden
 * @author Daniel Ockeloen
 * @author Michiel Meeuwissen
 * @version $Id$
 */
public class Functions {

    private static final Logger log = Logging.getLoggerInstance(Functions.class);

    /**
     * Converts a certain List to an Parameters if it is not already one.
     */
    public static Parameters buildParameters(Parameter[] def, List args) {
        Parameters a;
        if (args instanceof Parameters) {
            a = (Parameters) args;
        } else {
            a = new Parameters(def, args);
        }
        return a;
    }

    /**
     * Adds the definitions to a List. Resolves the {@link Parameter.Wrapper}'s (recursively).
     * @return List with only simple Parameter's.
     */
    public static List define(Parameter[] def, List list) {
        if (def == null) return list;
        for (int i = 0; i < def.length; i++) {
            if (def[i] instanceof Parameter.Wrapper) {
                define(((Parameter.Wrapper) def[i]).arguments, list);
            } else {
                list.add(def[i]);
            }
        }
        return list;
    }

    /**
     * @javadoc
     */
    public static Method getMethodFromClass(Class claz, String name) {
        Method method = null;
        Method[] methods = claz.getMethods();
        for (int j = 0; j < methods.length; j++) {
            if (methods[j].getName().equals(name)) {
                if (method != null) {
                    throw new IllegalArgumentException("There is more than one method with name '" + name + "' in " + claz);
                }
                method = methods[j];
            }
        }
        if (method == null) {
            throw new IllegalArgumentException("There is no method with name '" + name + "' in " + claz);
        }
        return method;
    }

    /**
     * Generates a map of Parameter[] objects for a given class through reflection.
     * The map keys are the names of te function the Parameter[] object belongs to.
     * <br />
     * The method parses the given class for constants (final static public members)
     * of type Parameter[]. The member name up to the first underscore in that name
     * is considered the name for a function supported by that class.
     * i.e. :
     * <pre>
     *    public final static Parameter[] AGE_PARAMETERS = {};
     * </pre>
     * defines a function 'age' which takes no parameters.
     * <pre>
     *    public final static Parameter[] GUI_PARAMETERS = {
     *        new Parameter("field", String.class),
     *        Parameter.LANGUAGE
     *    }
     * </pre>
     * defines a function 'gui' which two parameters: 'field' and 'language'.
     * Results form reflection are stored in an internal cache.
     * The method returns the Parameter[] value (if any) of the function whose
     * name was given in the call. If the function cannot be derived through
     * reflection, the method returns <code>null</code>.<br />
     * Note that, since this way of determining functions cannot determine
     * return value types, it is advised to use {@link FunctionProvider#addFunction}
     * instead.
     *
     * @see Parameter
     * @param clazz the class to perform reflection on.
     * @param map
     * @return A map of parameter definitions (Parameter[] objects), keys by function name (String)
    */
    public static Map getParameterDefinitonsByReflection(Class clazz, Map map) {

        log.debug("Searching " + clazz);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0 ; i < fields.length; i++) {
            Field field = fields[i];
            int mod = field.getModifiers();
            // only static public final Parameter[] constants are considered
            if (Modifier.isStatic(mod) && Modifier.isPublic(mod) && Modifier.isFinal(mod) &&
                    field.getType().equals(Parameter[].class)) {
                // get name (using convention)
                String name = field.getName().toLowerCase();
                int underscore = name.indexOf("_");
                if (underscore > 0) {
                    name = name.substring(0, underscore);
                }
                if (! map.containsKey(name)) { // overriding works, but don't do backwards :-)
                    try {
                        Parameter[] params = (Parameter[])field.get(null);
                        if (log.isDebugEnabled()) {
                            log.debug("Found a function definition '" + name + "' in " + clazz + " with parameters " + Arrays.asList(params));
                        }
                        map.put(name, params);
                    } catch (IllegalAccessException iae) {
                        // should not be thrown!
                        log.error("Found inaccessible parameter[] constant: " + field.getName());
                    }
                }
             }
        }
        Class sup = clazz.getSuperclass();
        if (sup != null) {
            getParameterDefinitonsByReflection(sup, map);
        }
        return map;
    }

}

/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.bridge.remote.generator;
import java.lang.reflect.*;
import org.w3c.dom.*;
import java.util.*;

/**
 * Basic class to do reflection on a Class file and create
 * an XML description of the class
 * @author Kees Jongenburger <keesj@dds.nl>
 **/
public class ClassToXML {

    public static Element classToXML(String orgClassName, String original, Document document) throws Exception {
        Element e = ClassToXML.classToXML(orgClassName, document);
        e.setAttribute("originalname", original);
        return e;
    }

    public static Element classToXML(String orgClassName, Document document) throws Exception {
        Set appendedMethods = new HashSet();
        Class clazz = Class.forName(orgClassName);

        Element xmle = document.createElement(clazz.isInterface() ? "interface" : "class");

        String className = orgClassName;
        int shortIndex = className.lastIndexOf(".");
        int dollarIndex = className.lastIndexOf("$");
        if (dollarIndex > -1) {
            className = className.substring(0,dollarIndex) + "." + className.substring(dollarIndex + 1);
        }
        xmle.setAttribute("name", className);
        String shortName = className.substring(shortIndex + 1);
        // test op static member classes
        xmle.setAttribute("shortname", shortName);

        // Determin which classes must be serialized, rather then RMIed.

        xmle.setAttribute("serializable", 
                          (java.io.Serializable.class.isAssignableFrom(clazz)  &&
                           ! org.mmbase.bridge.Cloud.class.equals(clazz)) // cloud is serializable for some reasons, but it cannot really well.
                          ? "true" : "false");

        Class[] interfaceClasses = clazz.getInterfaces();
        String implementsString = "";
        for (int counter = 0; counter < interfaceClasses.length; counter++) {
            if (counter != 0) {
                implementsString += ",";
            }
            implementsString += interfaceClasses[counter].getName();
        }
        //System.out.println("" + clazz + " implements " + implementsString);
        xmle.setAttribute("implements", implementsString);
        Method[] methods = clazz.getMethods();
        //add
        Method[] extraMethods = new Method[3];
        extraMethods[0] = String.class.getMethod("toString", new Class[] {});
        extraMethods[1] = String.class.getMethod("hashCode", new Class[] {});
        extraMethods[2] = String.class.getMethod("equals", new Class[] { Object.class });
        for (int x = 0; x < extraMethods.length; x++) {
            String name = extraMethods[x].getName();
            boolean add = true;
            for (int y = 0; y < methods.length; y++) {
                if (methods[y].getName().equals(extraMethods[x].getName())) {
                    add = false;
                }
            }
            if (add) {
                Method[] temp = new Method[methods.length + 1];
                System.arraycopy(methods, 0, temp, 0, methods.length);
                temp[methods.length] = extraMethods[x];
                methods = temp;
                //System.err.println("adding " + name + " to " + className);
            }
        }

        //Method toString = xmle.getClass().getM
        for (int i = 0; i < methods.length; i++) {
            if (Modifier.isStatic(methods[i].getModifiers())) continue;
            boolean createMethod = true;
            //see if the declared method belongs to the same class
            //we need to declare it
            if (!methods[i].getDeclaringClass().getName().equals(orgClassName)) {
                createMethod = false;
                String methodName = methods[i].getName();

                if (methods[i].getDeclaringClass().isInterface()) {
                    createMethod = true;
                }
                //add these methods that are part of Object
                if (methodName.equals("equals") || methodName.equals("hashCode") || methodName.equals("toString")) {
                    createMethod = true;
                }
            }
            if (createMethod) {
                //System.out.println("Creating method " + methods[i].getReturnType() + " " + methods[i].getName());
                String key = "";
                Element method = document.createElement("method");
                key += "method";
                method.setAttribute("name", methods[i].getName());
                key += methods[i].getName();

                Element parameters = document.createElement("input");
                Class[] parameterClasses = methods[i].getParameterTypes();
                key += "(";
                for (int x = 0; x < parameterClasses.length; x++) {
                    Class parameterClass = parameterClasses[x];
                    parameters.appendChild(ClassToXML.classToXML(parameterClass, document));
                    key += parameterClass.getName();
                }
                key += ")";
                method.appendChild(parameters);

                Element returnValue = document.createElement("output");
                Class returnType = methods[i].getReturnType();
                returnValue.appendChild(ClassToXML.classToXML(returnType, document));
                method.appendChild(returnValue);
                if (! appendedMethods.contains(key)) {
                    xmle.appendChild(method);
                    appendedMethods.add(key);
                }
            }
        }
        return xmle;
    }

    public static Element classToXML(Class c, Document document) {
        return classToXML(c, document, false);
    }

    public static Element classToXML(Class c, Document document, boolean isinarray) {
        Element retval = null;
        if (c.isArray()) {
            // retval= document.createElement("array");
            Class arr = c;
            while (arr.isArray()) {
                arr = arr.getComponentType();
            }
            Element e = ClassToXML.classToXML(arr, document, true);
            String className = arr.getName();
            int shortIndex = className.lastIndexOf(".");
            e.setAttribute("shortname", className.substring(shortIndex + 1));
            return e;
        } else if (c.isPrimitive()) {
            if (isinarray) {
                retval = document.createElement("array");
            } else {
                retval = document.createElement("primitiveclass");
            }
            retval.setAttribute("name", c.getName());
            retval.setAttribute("shortname", c.getName());
            String name = c.getName();
            if (name.equals("int")) {
                retval.setAttribute("classname", "java.lang.Integer");
            } else if (name.equals("char")) {
                retval.setAttribute("classname", "java.lang.Character");
            } else {
                String first = name.substring(0, 1);
                retval.setAttribute("classname", "java.lang." + first.toUpperCase() + name.substring(1));
            }
        } else if (c.getName().startsWith("java.") || c.getName().startsWith("javax.") || c.getName().startsWith("org.w3")) {
            if (isinarray) {
                retval = document.createElement("array");
            } else {
                retval = document.createElement("sunclass");
            }
            retval.setAttribute("name", c.getName());
        } else {
            if (isinarray) {
                retval = document.createElement("array");
            } else {
                retval = document.createElement("classReference");
            }
            String className = c.getName();
            int dollarIndex = className.lastIndexOf("$");
            if (dollarIndex > -1) {
                className = className.substring(0,dollarIndex) + "." + className.substring(dollarIndex + 1);
            }
            retval.setAttribute("name", className);
        }
        return retval;
    }
}

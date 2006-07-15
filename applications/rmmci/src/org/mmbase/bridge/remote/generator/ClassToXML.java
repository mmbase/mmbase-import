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
    
    public static Element classToXML(String className,String original, Document document) throws Exception{
        Element e = ClassToXML.classToXML(className, document);
        e.setAttribute("originalname",original);
        return e;
    }
    
    public static Element classToXML(String className, Document document) throws Exception{
        Hashtable methodHash = new Hashtable();
        Element xmle = document.createElement("class");
        xmle.setAttribute("name", className);
        int shortIndex = className.lastIndexOf(".");
        xmle.setAttribute("shortname",  className.substring(shortIndex +1 ));
        
        Class clazz = Class.forName(className);
        Class[] interfaceClasses = clazz.getInterfaces();
        String implementsString = "";
        for (int counter= 0 ; counter < interfaceClasses.length;counter++){
            if (counter != 0){
                implementsString += ",";
            }
            implementsString += interfaceClasses[counter].getName();
        }
        xmle.setAttribute("implements",implementsString);
        Method[] methods = clazz.getMethods();
        for (int i =0 ; i < methods.length ; i++){
            boolean createMethod = true;
            //see if the declared method belongs to the same class
            //we need to declare it
            if (! methods[i].getDeclaringClass().getName().equals(className)){
                createMethod = false;
                String name = methods[i].getDeclaringClass().getName();
                
                if (methods[i].getDeclaringClass().isInterface()){
                    createMethod = true;
                }
            }
            if (createMethod) {
                String key ="";
                Element method = document.createElement("method");
                key += "method";
                method.setAttribute("name", methods[i].getName());
                key +=  methods[i].getName();
                
                Element parameters = document.createElement("input");
                Class[] parameterClasses = methods[i].getParameterTypes();
                key +=  "(" ;
                for (int x =0 ; x < parameterClasses.length; x++){
                    Class parameterClass = parameterClasses[x];
                    parameters.appendChild(ClassToXML.classToXML(parameterClass,document));
                    key +=  parameterClass.getName();
                }
                key += ")";
                method.appendChild(parameters);
                
                Element returValue = document.createElement("output");
                Class returnType = methods[i].getReturnType();
                returValue.appendChild(ClassToXML.classToXML(returnType,document));
                method.appendChild(returValue);
                if (methodHash.get(key) == null){
                    xmle.appendChild(method);
                    methodHash.put(key,"true");
                }
            }
        }
        return xmle;
    }
    
    public static Element classToXML(Class c, Document document){
        return classToXML(c,document,false);
    }
    
    public static Element classToXML(Class c, Document document, boolean isinarray){
        Element retval=null;
        if(c.isArray()){
            // retval= document.createElement("array");
            Class arr = c;
            while(arr.isArray()){
                arr = arr.getComponentType();
            }
            Element e =ClassToXML.classToXML(arr,document,true);
            String className = arr.getName();
            int shortIndex = className.lastIndexOf(".");
            e.setAttribute("shortname",  className.substring(shortIndex +1 ));
            return e;
        } else if (c.isPrimitive()){
            if (isinarray) {
                retval=document.createElement("array");
            } else {
                retval=document.createElement("primitiveclass");
            }
            retval.setAttribute("name",c.getName());
            retval.setAttribute("shortname",c.getName());
            String name = c.getName();
            if (name.equals("int")){
                retval.setAttribute("classname","java.lang.Integer");
            } else if (name.equals("char")){
                retval.setAttribute("classname","java.lang.Character");
            } else {
                String first = name.substring(0,1);
                retval.setAttribute("classname","java.lang." + first.toUpperCase() + name.substring(1));
            }
        } else if (c.getName().startsWith("java.") || c.getName().startsWith("javax.") || c.getName().startsWith("org.w3")) {
            if (isinarray) {
                retval=document.createElement("array");
            } else {
                retval=document.createElement("sunclass");
            }
            retval.setAttribute("name",c.getName());
        } else {
            if (isinarray) {
                retval=document.createElement("array");
            } else {
                retval=document.createElement("classReference");
            }
            retval.setAttribute("name",c.getName());
        }
        return retval;
    }
}

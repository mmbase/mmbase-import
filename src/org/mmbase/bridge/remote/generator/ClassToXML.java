/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.remote.generator;
import java.lang.reflect.*;
import nanoxml.*;
import java.util.*;

/**
 * Basic class to do reflection on a Class file and create
 * an XML description of the class
 * @author Kees Jongenburger <keesj@framfab.nl>
 **/
public class ClassToXML {
    
    
    public static XMLElement classToXML(String className,String original) throws Exception{
        XMLElement e = ClassToXML.classToXML(className);
        e.addProperty("originalname",original);
        return e;
    }
    
    public static XMLElement classToXML(String className) throws Exception{
        Hashtable methodHash = new Hashtable();
        XMLElement xmle = new XMLElement();
        xmle.setTagName("class");
        xmle.addProperty("name", className);
        int shortIndex = className.lastIndexOf(".");
        xmle.addProperty("shortname",  className.substring(shortIndex +1 ));
        
        Class clazz = Class.forName(className);
        Class[] interfaceClasses = clazz.getInterfaces();
        String implementsString = "";
        for (int counter= 0 ; counter < interfaceClasses.length;counter++){
            if (counter != 0){
                implementsString += ",";
            }
            implementsString += interfaceClasses[counter].getName();
        }
        xmle.addProperty("implements",implementsString);
        Method[] methods = clazz.getMethods();
        for (int i =0 ; i < methods.length ; i++){
            boolean createMethod = true;
            //see if the declared belongs to the same class
            //if not we don't need to declare it
            if (! methods[i].getDeclaringClass().getName().equals(className)){
                createMethod = false;
                String name = methods[i].getDeclaringClass().getName();
                
                if (methods[i].getDeclaringClass().isInterface()){
                    createMethod = true;
                }
                //if ( name.startsWith("java.util")){
                //    createMethod= false;
                //}
            }
            if (createMethod) {
                String key ="";
                XMLElement method = new XMLElement();
                
                method.setTagName("method");
                key += "method";
                method.addProperty("name", methods[i].getName());
                key +=  methods[i].getName();
                
                XMLElement parameters = new XMLElement();
                parameters.setTagName("input");
                Class[] parameterClasses = methods[i].getParameterTypes();
                key +=  "(" ;
                for (int x =0 ; x < parameterClasses.length; x++){
                    Class parameterClass = parameterClasses[x];
                    parameters.addChild(ClassToXML.classToXML(parameterClass));
                    key +=  parameterClass.getName();
                }
                key += ")";
                method.addChild(parameters);
                
                XMLElement returValue = new XMLElement();
                returValue.setTagName("output");
                Class returnType = methods[i].getReturnType();
                returValue.addChild(ClassToXML.classToXML(returnType));
                method.addChild(returValue);
                if (methodHash.get(key) == null){
                    xmle.addChild(method);
                    methodHash.put(key,"true");
                }
            }
        }
        return xmle;
    }
    
    
    public static XMLElement classToXML(Class c){
        XMLElement retval = new XMLElement();
        if(c.isArray()){
            retval.setTagName("array");
            Class arr = c;
            while(arr.isArray()){
                arr = arr.getComponentType();
            }
            XMLElement e =ClassToXML.classToXML(arr);
            e.setTagName("array");
	    String className = arr.getName();
       	    int shortIndex = className.lastIndexOf(".");
            e.addProperty("shortname",  className.substring(shortIndex +1 ));
            return e;
        } else {
            
            if (c.isPrimitive()){
                retval.setTagName("primitiveclass");
                retval.addProperty("name",c.getName());
                retval.addProperty("shortname",c.getName());
               String name = c.getName();
               if (name.equals("int")){
                   retval.addProperty("classname","java.lang.Integer");
               } else if (name.equals("char")){
                   retval.addProperty("classname","java.lang.Character");
               } else {
                   String first = name.substring(0,1);
                   retval.addProperty("classname","java.lang." + first.toUpperCase() + name.substring(1));
               }
            } else {
		if (c.getName().startsWith("java.") || c.getName().startsWith("javax.")  ){
			retval.setTagName("sunclass");
			retval.addProperty("name",c.getName());
		} else {
			retval.setTagName("classReference");
			retval.addProperty("name",c.getName());
		}
            }
        }
        return retval;
    }
    
    /**
     *
     **/
    public static void main(String [] argv) throws Exception{
        System.out.println(ClassToXML.classToXML(argv[0]));
    }
}

/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.remote.generator;
import nanoxml.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * @author Kees Jongenburger <keesj@framfab.nl>
 **/
public class XMLMethod extends XMLClass{
    
    public XMLMethod(){
        super();
    }
    
    public static XMLClass fromXML(XMLElement xml){
        XMLMethod method = new XMLMethod();
        method.setXML(xml);
        return method;
    }
    public Method getJavaMethod(Class clazz){
        Method[] methods = clazz.getMethods();
        for (int i =0 ; i < methods.length;i++){
            if (methods[i].getName().equals(getName())){
                Class[] params = methods[i].getParameterTypes();
                List list = getParameterList();
                boolean ok = false;
                if (params != null){
                    if (params.length == list.size()){
                        ok = true;
                        for (int p =0 ; p < params.length; p++){
                            if (! (params[p].getName().equals( ((XMLClass)list.get(p)).getOriginalName()))){
                                ok = false;
                            }
                        }
                    }
                }
                if (ok){
                    return methods[i];
                }
            }
        }
        System.err.println("Method not found");
        return null;
    }
}

/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.bridge.remote.generator;
import org.w3c.dom.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * @author Kees Jongenburger <keesj@dds.nl>
 **/
public class XMLMethod extends XMLClass {

    public XMLMethod(Document document) {
        super(document);
    }

    public static XMLClass fromXML(Element xml) {
        Document doc = xml.getOwnerDocument();
        XMLMethod method = new XMLMethod(doc);
        method.setXML(xml);
        return method;
    }

    public Method getJavaMethod(Class clazz) {
        Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(getName())) {
                Class[] params = methods[i].getParameterTypes();
                List list = getParameterList();
                boolean ok = false;
                if (params != null) {
                    if (params.length == list.size()) {
                        ok = true;
                        for (int p = 0; p < params.length; p++) {
                            if (!(params[p].getName().equals(((XMLClass)list.get(p)).getOriginalName()))) {
                                ok = false;
                            }
                        }
                    }
                }
                if (ok) {
                    return methods[i];
                }
            }
        }
        System.err.println("Method not found");
        return null;
    }
}

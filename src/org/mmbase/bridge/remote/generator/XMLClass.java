/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.remote.generator;
import nanoxml.*;
import java.util.*;

/**
 * @author Kees Jongenburger <keesj@framfab.nl>
 **/
public class XMLClass{
    XMLElement xml;
    Hashtable methods;
    Vector methodsVector;
    Vector realInput;
    Object data;
    boolean dataIsXMLClass= false;
    public boolean isArray = false;
    public boolean isPrimitive = false;
    
    
    public Object getData(){
        return data;
        
    }
    public void setData(XMLClass data){
        dataIsXMLClass = true;
        this.data = data;
    }
    public void setData(double data){
        this.data = new Double(data);
    }
    public void setData(boolean data){
        this.data = new Boolean(data);
    }
    public void setData(float data){
        this.data = new Float(data);
    }
    public void setData(int data){
        this.data = new Integer(data);
    }
    public void setData(Object data){
        this.data = data;
    }
    public XMLClass(){
        methods = new Hashtable();
        methodsVector = new Vector();
        realInput = new Vector();
    }
    
    public Class getJavaClass() throws ClassNotFoundException{
        return Class.forName(getName());
    }
    
    
    public void setXML(XMLElement xml){
        this.xml = xml;
    }
    public void addInput(XMLClass xmlClass){
        realInput.addElement(xmlClass);
    }
    
    
    public static XMLClass fromXML(XMLElement xml){
        String elementName = xml.getTagName();
        
        if (elementName.equals("primitiveclass")){
            XMLClass xmlClass = new XMLClass();
            xmlClass.isPrimitive = true;
            xmlClass.setXML(xml);
	    return xmlClass;
        } else if (elementName.equals("sunclass")){
            XMLClass xmlClass = new XMLClass();
            xmlClass.setXML(xml);
	    return xmlClass;
        } else if (elementName.equals("array") || elementName.equals("class")){
            XMLClass xmlClass = new XMLClass();
            if (elementName.equals("array")){
                xmlClass.isArray= true;
            }
            xmlClass.xml = xml;
            Enumeration enum = xml.enumerateChildren();
            while(enum.hasMoreElements()){
                XMLElement element = (XMLElement)enum.nextElement();
                String name = element.getTagName();
                if (name.equals("data")){
                    if (element.getProperty("type").equals("input")){
                        xmlClass.setData(element.getContents());
                    }
                }
                if (name.equals("method")){
                    XMLMethod xmlMethod= (XMLMethod)XMLMethod.fromXML(element);
                    xmlClass.methods.put(xmlMethod.getName(), xmlMethod);
                    xmlClass.methodsVector.addElement(xmlMethod);
                }
            }
            return xmlClass;
        } else if (elementName.equals("classReference")){
            try {
                MMCI mmci = MMCI.getDefaultMMCI();
                return mmci.getClass(xml.getProperty("name"));
            } catch (Exception e){
                System.err.println("FROMXML ERROR " + e.getMessage());
            }
        }
        return null;
    }
    
    public Object clone(boolean deep){
        //return new XMLClass().fromXML(xml.clone(true));
        return new XMLClass().fromXML(xml);
        
    }
    
    public Vector getInput(){
        return realInput;
    }
    public String getImplements(){
        return xml.getProperty("implements");
    }
    public String getName(){
        return xml.getProperty("name");
    }
    public String getShortName(){
        return xml.getProperty("shortname");
    }
    public String getOriginalName(){
        if (xml.getProperty("originalname") != null){
            return xml.getProperty("originalname");
        } else {
            return getName();
        }
    }
    
    public XMLElement toXMLInput(){
        XMLElement xmle = new XMLElement();
        xmle.setTagName("class");
        xmle.addProperty("name",getName());
        XMLElement xmlData = new XMLElement();
        xmlData.setTagName("data");
        xmlData.addProperty("type","input");
        xmlData.setContent("" + data);
        xmle.addChild(xmlData);
        return xmle;
    }
    
    
    public Vector getMethods(){
        return methodsVector;
    }
    /**
     *@return an XMLMethod
     **/
    public XMLMethod getMethod(String name){
        return (XMLMethod)methods.get(name);
    }
    
    public XMLClass getReturnType(){
        Enumeration enum = xml.enumerateChildren();
        while(enum.hasMoreElements()){
            XMLElement element = (XMLElement)enum.nextElement();
            String name = element.getTagName();
            if (name.equals("output")){
                Enumeration returnEnum = element.enumerateChildren();
                while(returnEnum.hasMoreElements()){
                    XMLElement returnValue = (XMLElement)returnEnum.nextElement();
                    return XMLClass.fromXML(returnValue);
                }
            }
        }
        return null;
    }
    public List getParameterList(){
        Vector retval= new Vector();
        Enumeration enum = xml.enumerateChildren();
        while(enum.hasMoreElements()){
            XMLElement element = (XMLElement)enum.nextElement();
            String name = element.getTagName();
            if (name.equals("input")){
                Enumeration returnEnum = element.enumerateChildren();
                while(returnEnum.hasMoreElements()){
                    XMLElement returnValue = (XMLElement)returnEnum.nextElement();
                    retval.addElement(XMLClass.fromXML(returnValue));
                }
            }
        }
        return retval;
    }
    
}

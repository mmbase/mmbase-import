/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.bridge.remote.generator;
import org.w3c.dom.*;

import java.util.*;

/**
 * @author Kees Jongenburger <keesj@dds.nl>
 **/
public class XMLClass {
    Element xml;
    Hashtable methods;
    Vector methodsVector;
    Vector realInput;
    Object data;
    Document document=null;
    boolean dataIsXMLClass= false;
    public boolean isArray = false;
    public boolean isPrimitive = false;
    
    XMLClass(Document document) {
        this.document= document;
        methods = new Hashtable();
        methodsVector = new Vector();
        realInput = new Vector();
    }
    
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
    
    public Class getJavaClass() throws ClassNotFoundException{
        return Class.forName(getName());
    }
    
    public void setXML(Element xml){
        this.xml = xml;
    }
    
    public void addInput(XMLClass xmlClass){
        realInput.addElement(xmlClass);
    }
    
    
    public static XMLClass fromXML(Element xml){
        Document doc=xml.getOwnerDocument();
        String elementName = xml.getTagName();
        
        if (elementName.equals("primitiveclass")){
            XMLClass xmlClass = new XMLClass(doc);
            xmlClass.isPrimitive = true;
            xmlClass.setXML(xml);
            return xmlClass;
        } else if (elementName.equals("sunclass")){
            XMLClass xmlClass = new XMLClass(doc);
            xmlClass.setXML(xml);
            return xmlClass;
        } else if (elementName.equals("array") || elementName.equals("class")){
            XMLClass xmlClass = new XMLClass(doc);
            if (elementName.equals("array")){
                xmlClass.isArray= true;
            }
            xmlClass.setXML(xml);
            NodeList nl= xml.getElementsByTagName("*");
            for(int i=0; i<nl.getLength(); i++) {
                Element element = (Element)nl.item(i);
                String name = element.getTagName();
                if (name.equals("data")){
                    if (element.getAttribute("type").equals("input")){
                        String content="";
                        NodeList nl2 = element.getChildNodes();
                        for (int j=0;i<nl2.getLength();j++) {
                            Node n = nl2.item(j);
                            if (n.getNodeType() == Node.TEXT_NODE) {
                                content+=n.getNodeValue();
                            }
                        }
                        xmlClass.setData(content);
                    }
                } else if (name.equals("method")){
                    XMLMethod xmlMethod= (XMLMethod)XMLMethod.fromXML(element);
                    xmlClass.methods.put(xmlMethod.getName(), xmlMethod);
                    xmlClass.methodsVector.addElement(xmlMethod);
                }
            }
            return xmlClass;
        } else if (elementName.equals("classReference")){
            try {
                MMCI mmci = MMCI.getDefaultMMCI();
                return mmci.getClass(xml.getAttribute("name"));
            } catch (Exception e){
                System.err.println("FROMXML ERROR " + e.getMessage());
            }
        }
        return null;
    }
    
    public Object clone(boolean deep){
        //return new XMLClass().fromXML(xml.clone(true));
        return XMLClass.fromXML(xml);
    }
    
    public Vector getInput(){
        return realInput;
    }
    public String getImplements(){
        return xml.getAttribute("implements");
    }
    public String getName(){
        return xml.getAttribute("name");
    }
    public String getShortName(){
        String res=xml.getAttribute("shortname");
        if (res.equals("")){
            res = getName();
        }
        return res;
    }
    public String getOriginalName(){
        String result=xml.getAttribute("originalname");
        if (result.equals("")){
            result=getName();
        }
        return result;
    }
    
    public Element toXMLInput(){
        Element xmle = document.createElement("class");
        xmle.setAttribute("name",getName());
        Element xmlData = document.createElement("data");
        xmlData.setAttribute("type","input");
        Text text=document.createTextNode(""+data);
        xmlData.appendChild(text);
        xmle.appendChild(xmlData);
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
        NodeList nl= xml.getElementsByTagName("output");
        for(int i=0; i<nl.getLength(); i++) {
            Element element = (Element)nl.item(i);
            NodeList nl2= element.getElementsByTagName("*");
            for(int j=0; j<nl2.getLength(); j++) {
                Element returnvalue = (Element)nl2.item(j);
                return XMLClass.fromXML(returnvalue);
            }
        }
        return null;
    }
    
    public List getParameterList(){
        Vector results= new Vector();
        NodeList nl= xml.getElementsByTagName("input");
        for(int i=0; i<nl.getLength(); i++) {
            Element element = (Element)nl.item(i);
            NodeList nl2= element.getElementsByTagName("*");
            for(int j=0; j<nl2.getLength(); j++) {
                Element par = (Element)nl2.item(j);
                results.addElement(XMLClass.fromXML(par));
            }
        }
        return results;
    }
    
}

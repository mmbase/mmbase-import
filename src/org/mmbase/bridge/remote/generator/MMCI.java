/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.bridge.remote.generator;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.xml.serialize.*;
import java.io.*;
import java.util.*;
import org.mmbase.util.XMLBasicReader;

/**
 * @author Kees Jongenburger <keesj@framfab.nl>
 **/
public class MMCI{
    Hashtable classes;
    Vector classesVector;
    
    private static MMCI STATIC_MMCI = null;
    
    public MMCI(){
        classes = new Hashtable();
        classesVector = new Vector();
    }
    
    public static MMCI getDefaultMMCI() throws Exception{
        return getDefaultMMCI("MMCI.xml");
    }
    
    public static MMCI getDefaultMMCI(String fileName) throws Exception{
        if (MMCI.STATIC_MMCI == null){
            XMLBasicReader reader=new XMLBasicReader(fileName);
            MMCI.STATIC_MMCI =  MMCI.fromXML(reader);
        }
        return MMCI.STATIC_MMCI;
    }
    
    public static MMCI fromXML(XMLBasicReader reader) throws Exception{
        MMCI mmci =  new MMCI();
        Element xmle=reader.getElementByPath("mmci");
        for(Enumeration enum = reader.getChildElements(xmle,"class");
        enum.hasMoreElements();) {
            Element element = (Element)enum.nextElement();
            XMLClass myClass = XMLClass.fromXML(element);
            mmci.classes.put(myClass.getName(),myClass);
            mmci.classesVector.addElement(myClass);
        }
        return mmci;
    }
    
    public Vector getClasses(){
        return classesVector;
    }
    public XMLClass getClass(String name) throws NotInMMCIException{
        if (classes.get(name) == null){
            throw new NotInMMCIException("Class " + name + " is not known to the MMCI");
        }
        return (XMLClass)((XMLClass)classes.get(name)).clone(true);
    }
    
    public static void addDefaultBridgeClasses(Element xmle, Document doc) throws Exception {
        //mmbase interfaces
        //xmle.setComment("MMCI XML description file\nCreated on " + new java.util.Date() + "\nby remote.common.MMCI");
        //should we use BridgeException interface?
        //xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.BridgeException"));
        
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.Cloud",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.CloudContext",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.Field",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.FieldIterator",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.FieldList",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.Module",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.ModuleIterator",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.ModuleList",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.Node",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.NodeIterator",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.NodeList",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.NodeManager",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.NodeManagerIterator",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.NodeManagerList",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.Relation",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.RelationIterator",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.RelationList",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.RelationManager",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.RelationManagerIterator",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.RelationManagerList",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.StringIterator",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.StringList",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.Transaction",doc));
        xmle.appendChild(ClassToXML.classToXML("org.mmbase.bridge.User",doc));
    }
    
    public static void main(String [] argv) throws Exception{
        OutputStream os = System.out;
        if (argv.length >1){
            System.err.println("Usage: java org.mmbase.bridge.remote.generator.MMCI <outputfile>");
        } else {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element xmle =doc.createElement("mmci");
            doc.appendChild(xmle);
            MMCI.addDefaultBridgeClasses(xmle, doc);
            if (argv.length==1) {
                os = new FileOutputStream(argv[0]);
            }
            OutputFormat format = new OutputFormat(doc);
            format.setIndenting(true);
            format.setPreserveSpace(false);
            XMLSerializer prettyXML = new XMLSerializer(os,format);
            prettyXML.serialize(doc);
            os.flush();
        }
    }
    
}


/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.bridge.remote.generator;
import nanoxml.*;
import java.io.*;
import java.util.*;
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
            File file = new File(fileName);
            if (!file.exists()){
                throw new Exception("file {"+ fileName +"} does not exsit");
            }
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] data = new byte[300];
            int count = 0;
            while((count = fis.read(data)) > 0){
                baos.write(data,0,count);
            }
            String xmlString = new String(baos.toByteArray());
            XMLElement xmle = new XMLElement();
            xmle.parseString(xmlString);
            
            
            MMCI.STATIC_MMCI =  MMCI.fromXML(xmle);
        }
        return MMCI.STATIC_MMCI;
    }
    
    
    public static MMCI fromXML(XMLElement xmle) throws Exception{
        MMCI mmci =  new MMCI();
        Enumeration enum = xmle.enumerateChildren();
        while(enum.hasMoreElements()){
            XMLElement element = (XMLElement)enum.nextElement();
            String name = element.getTagName();
            if (name.equals("class")){
                XMLClass myClass = XMLClass.fromXML(element);
                mmci.classes.put(myClass.getName(),myClass);
                mmci.classesVector.addElement(myClass);
                
            }
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
    public static void addDefaultBridgeClasses(XMLElement xmle) throws Exception{
        //mmbase interfaces
        //xmle.setComment("MMCI XML description file\nCreated on " + new java.util.Date() + "\nby remote.common.MMCI");
	//should we use BridgeException interface?
        //xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.BridgeException"));

        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.Cloud"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.CloudContext"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.Field"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.FieldIterator"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.FieldList"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.Module"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.ModuleIterator"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.ModuleList"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.Node"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.NodeIterator"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.NodeList"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.NodeManager"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.NodeManagerIterator"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.NodeManagerList"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.Relation"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.RelationIterator"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.RelationList"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.RelationManager"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.RelationManagerIterator"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.RelationManagerList"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.StringIterator"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.StringList"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.Transaction"));
        xmle.addChild(ClassToXML.classToXML("org.mmbase.bridge.User"));
    }
    
    public static void main(String [] argv) throws Exception{
	if (argv.length != 1){
		System.err.println("usage remote.common.MMCI outputfile");
	} else {
		XMLElement xmle = new XMLElement();
		MMCI.addDefaultBridgeClasses(xmle);
		xmle.setTagName("mmci");

		FileOutputStream fos = new FileOutputStream(argv[0]);
		fos.write(xmle.toString().getBytes());
		fos.close();
	}
    }
    
}


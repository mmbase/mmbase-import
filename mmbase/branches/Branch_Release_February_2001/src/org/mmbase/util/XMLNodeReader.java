/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.*;
import java.util.*;

import org.xml.sax.*;
import org.apache.xerces.parsers.*;
import org.w3c.dom.*;
import org.w3c.dom.traversal.*;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.support.*;

/**
*/
public class XMLNodeReader  {

    Document document;
    DOMParser parser;
    String applicationpath;


    public XMLNodeReader(String filename,String applicationpath,MMBase mmbase) {
        try {
            parser = new DOMParser();
            parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
            parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
            //Errors errors = new Errors();
            //parser.setErrorHandler(errors);
	    filename="file:///"+filename;
            parser.parse(filename);
            document = parser.getDocument();
	    this.applicationpath=applicationpath;

		/*
	    System.out.println("*** START XML APPLICATION READER FOR : "+filename);	
	    System.out.println("ExportSource="+getExportSource());	
	    System.out.println("TimeStamp="+getTimeStamp());	
	    System.out.println("Nodes nodes="+getNodes(mmbase));	
	    System.out.println("*** END XML APPLICATION READER FOR : "+filename);	
		*/
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }


    /**
    * get the name of this application
    */
    public String getExportSource() {
	Vector nodes=new Vector();
	Node n1=document.getFirstChild();
	while (n1!=null) {
		NamedNodeMap nm=n1.getAttributes();
		if (nm!=null) {
			Node n2=nm.getNamedItem("exportsource");
			return(n2.getNodeValue());
		}
	}
	return(null);
    }


    /**
    * get the name of this application
    */
    public int getTimeStamp() {
	Vector nodes=new Vector();
	Node n1=document.getFirstChild();
	while (n1!=null) {
		NamedNodeMap nm=n1.getAttributes();
		if (nm!=null) {
			Node n2=nm.getNamedItem("timestamp");
			int times=DateSupport.parsedatetime(n2.getNodeValue());
			return(times);
		}
	}
	return(-1);
    }

    /**
    * get the name of this application
    */
    public Vector getNodes(MMBase mmbase) {
	Vector nodes=new Vector();
	Node n1=document.getFirstChild();
	while (n1!=null) {
		MMObjectBuilder bul=mmbase.getMMObject(n1.getNodeName());
		if (bul!=null) {
			Node n2=n1.getFirstChild();
			while (n2!=null) {
				if (n2.getNodeName().equals("node")) {
					NamedNodeMap nm=n2.getAttributes();
					if (nm!=null) {
						Node n4=nm.getNamedItem("owner");
						MMObjectNode newnode=bul.getNewNode(n4.getNodeValue());
						n4=nm.getNamedItem("alias");
						if (n4!=null) newnode.setAlias(n4.getNodeValue());
						n4=nm.getNamedItem("number");
						try {
							int num=Integer.parseInt(n4.getNodeValue());
			
							newnode.setValue("number",num);
						} catch(Exception e) {}
						Node n5=n2.getFirstChild();
						while (n5!=null) {
							String key=n5.getNodeName();
							if (!key.equals("#text")) {
								Node n6=n5.getFirstChild();
								String value="";
								if (n6!=null) value=n6.getNodeValue();
								int type=bul.getDBType(key);
								if (type!=-1) {
									if (type==FieldDefs.TYPE_STRING) {
										newnode.setValue(key,value);
									} else if (type==FieldDefs.TYPE_INTEGER) {
										try { 
											newnode.setValue(key,Integer.parseInt(value));
										} catch(Exception e) {
											newnode.setValue(key,-1);
										}
									} else if (type==FieldDefs.TYPE_FLOAT) {
										try { 
											newnode.setValue(key,Float.parseFloat(value));
										} catch(Exception e) {
											newnode.setValue(key,-1);
										}
									} else if (type==FieldDefs.TYPE_DOUBLE) {
										try { 
											newnode.setValue(key,Double.parseDouble(value));
										} catch(Exception e) {
											newnode.setValue(key,-1);
										}
									} else if (type==FieldDefs.TYPE_LONG) {
										try { 
											newnode.setValue(key,Long.parseLong(value));
										} catch(Exception e) {
											newnode.setValue(key,-1);
										}
									} else if (type==FieldDefs.TYPE_BYTE) {
										NamedNodeMap nm2=n5.getAttributes();
										Node n7=nm2.getNamedItem("file");
										newnode.setValue(key,readBytesFile(applicationpath+n7.getNodeValue()));
									} else { 
										System.out.println("XMLNodeReader node error : "+key+" "+value+" "+type);
									}
								}
							}
							n5=n5.getNextSibling();
						}
						nodes.addElement(newnode);
					}
				}
				n2=n2.getNextSibling();
			}
		} else {
			System.out.println("XMLNodeReader can't access builder : "+bul);
		}
		n1=n1.getNextSibling();
	}
	return(nodes);
    }


    byte[] readBytesFile(String filename) {
	File bfile = new File(filename);
	int filesize = (int)bfile.length();
	byte[] buffer=new byte[filesize];
	try {
			FileInputStream scan = new FileInputStream(bfile);
		int len=scan.read(buffer,0,filesize);
		scan.close();
	} catch(FileNotFoundException e) {
		System.out.println("error getfile : "+filename);
 	} catch(IOException e) {}
	return(buffer);
    }
}

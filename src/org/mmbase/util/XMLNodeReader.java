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

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * This class reads a node from an exported application
 */
public class XMLNodeReader  {

    private static Logger log = Logging.getLoggerInstance(XMLNodeReader.class.getName());

    Document  document;
    DOMParser parser;
    String    applicationpath;

    /**
     * Constructor
     * @param filename from the file to read from
     * @param applicationpath the path where this application was exported to
     * @param mmbase
     */

    public XMLNodeReader(String filename,String applicationpath,MMBase mmbase) {
        try {
            parser = new DOMParser();
	    // are the lines below generic?
            parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
            parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
            EntityResolver resolver = new XMLEntityResolver();
            parser.setEntityResolver(resolver);
            filename="file:///"+filename;
            parser.parse(filename);
            document = parser.getDocument();
            this.applicationpath=applicationpath;
            /*
	      System.out.println("*** START XML APPLICATION READER FOR : "+filename);
	      System.out.println("ExportSource="+getExportSource());
	      System.out.println("TimeStamp="+getTimeStamp());
	      System.out.println("*** END XML APPLICATION READER FOR : "+filename);
            */
        } catch(Exception e) {
	    // argh.. no distinction between different type of error messages?
            log.error(Logging.stackTrace(e));
        }
    }


    /**
     *
     */
    public String getExportSource() {
        Vector nodes=new Vector();
        Node n1=document.getFirstChild();
        if (n1.getNodeType()==Node.DOCUMENT_TYPE_NODE) {
            n1=n1.getNextSibling();
        }
        while (n1!=null) {
            NamedNodeMap nm=n1.getAttributes();
            if (nm!=null) {
                Node n2=nm.getNamedItem("exportsource");
                return(n2.getNodeValue());
            }
        }
        return null;
    }


    /**
     *
     */
    public int getTimeStamp() {
        Vector nodes=new Vector();
        Node n1=document.getFirstChild();
        if (n1.getNodeType()==Node.DOCUMENT_TYPE_NODE) {
            n1=n1.getNextSibling();
        }
        while (n1!=null) {
            NamedNodeMap nm=n1.getAttributes();
            if (nm!=null) {
                Node n2=nm.getNamedItem("timestamp");
                try {
                    java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat ("yyyyMMddhhmmss", Locale.US);
                    int times = (int) (formatter.parse(n2.getNodeValue()).getTime()/ 1000);
                    //int times=DateSupport.parsedatetime(n2.getNodeValue());
                    return times;
                } catch (java.text.ParseException e) {
		    log.warn("error retrieving timestamp: " + Logging.stackTrace(e));
                    return -1;
                }

            }
	}
        return -1;
    }

    /**
     *
     */
    public Vector getNodes(MMBase mmbase) {
	Vector nodes=new Vector();
	Node n1=document.getFirstChild();
	if (n1.getNodeType()==Node.DOCUMENT_TYPE_NODE) {
	    n1=n1.getNextSibling();
	}
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
					if (type==FieldDefs.TYPE_STRING || type==FieldDefs.TYPE_XML) {
					    if (value==null) value="";
					    newnode.setValue(key,value);
					} 
					else if (type==FieldDefs.TYPE_NODE) {
					    try {
						newnode.setValue(key,Integer.parseInt(value));
					    } 
					    catch(Exception e) {
						log.warn("error setting node-field " + e);
						newnode.setValue(key,-1);
					    }
					} 
					else if (type==FieldDefs.TYPE_INTEGER) {
					    try {
						newnode.setValue(key,Integer.parseInt(value));
					    } 
					    catch(Exception e) {
						log.warn("error setting integer-field " + e);
						newnode.setValue(key,-1);
					    }
					} 
					else if (type==FieldDefs.TYPE_FLOAT) {
					    try {
						newnode.setValue(key,Float.parseFloat(value));
					    } 
					    catch(Exception e) {
						log.warn("error setting float-field " + e);
						newnode.setValue(key,-1);
					    }
					} 
					else if (type==FieldDefs.TYPE_DOUBLE) {
					    try {
						newnode.setValue(key,Double.parseDouble(value));
					    } 
					    catch(Exception e) {
						log.warn("error setting double-field " + e);
						newnode.setValue(key,-1);
					    }
					}
					else if (type==FieldDefs.TYPE_LONG) {
					    try {
						newnode.setValue(key,Long.parseLong(value));
					    } 
					    catch(Exception e) {
						log.warn("error setting long-field " + e);
						newnode.setValue(key,-1);
					    }
					} 
					else if (type==FieldDefs.TYPE_BYTE) {
					    NamedNodeMap nm2=n5.getAttributes();
					    Node n7=nm2.getNamedItem("file");
					    newnode.setValue(key,readBytesFile(applicationpath+n7.getNodeValue()));
					} 
					else {
					    log.error("FieldDefs not found for #" + type + " was not known for field with name: '"+key+"' and with value: '"+value+"'");
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
	    } 
	    else {
		log.error("Could not find builder with name: "+n1.getNodeName()+"'");
	    }
	    n1=n1.getNextSibling();
	}
	return nodes;
    }


    byte[] readBytesFile(String filename) {
        File bfile = new File(filename);
        int filesize = (int)bfile.length();
        byte[] buffer=new byte[filesize];
        try {
            FileInputStream scan = new FileInputStream(bfile);
            int len=scan.read(buffer,0,filesize);
            scan.close();
        } 
	catch(FileNotFoundException e) {
            log.error("error getfile : " + filename + " " + Logging.stackTrace(e));
        } 
	catch(IOException e) {
            log.error("error getfile : " + filename + " " + Logging.stackTrace(e));
	}
        return(buffer);
    }
}

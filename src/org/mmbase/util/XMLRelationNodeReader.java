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
import org.mmbase.util.logging.*;

/**
*/
public class XMLRelationNodeReader  {

	/**
	* Logger routine
	*/
	private static Logger log = Logging.getLoggerInstance(XMLRelationNodeReader.class.getName());

    Document document;
    DOMParser parser;


    public XMLRelationNodeReader(String filename,MMBase mmbase) {
        try {
            parser = new DOMParser();
            parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
            parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
            //Errors errors = new Errors();
            //parser.setErrorHandler(errors);

            EntityResolver resolver = new XMLEntityResolver();
            parser.setEntityResolver(resolver);
	    filename="file:///"+filename;
            parser.parse(filename);
            document = parser.getDocument();

		/*
	    log.debug("*** START XML RELATION READER FOR : "+filename);	
	    log.debug("ExportSource="+getExportSource());	
	    log.debug("TimeStamp="+getTimeStamp());	
	    log.debug("Nodes nodes="+getNodes(mmbase));	
	    log.debug("*** END XML RELATION READER FOR : "+filename);	
		*/
	} catch(Exception e) {
	    log.error(e);
	    log.error(Logging.stackTrace(e));
    }
    }


    /**
    * get the name of this application
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
	return(null);
    }


    /**
    * get the name of this application
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
			int times=DateSupport.parsedatetime(n2.getNodeValue());
			return(times);
		}
	}
	return(-1);
    }


    /**
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
						try {
							n4=nm.getNamedItem("number");
							int num=Integer.parseInt(n4.getNodeValue());
							newnode.setValue("number",num);

							n4=nm.getNamedItem("snumber");
							int rnum=Integer.parseInt(n4.getNodeValue());
							newnode.setValue("snumber",rnum);
							n4=nm.getNamedItem("dnumber");
							int dnum=Integer.parseInt(n4.getNodeValue());
							newnode.setValue("dnumber",dnum);
							n4=nm.getNamedItem("rtype");
							String rname=n4.getNodeValue();
							RelDef reldef=mmbase.getRelDef();
							if (reldef==null) {
								log.error("XMLRelationReader -> can't get reldef builder");
								return null;
							}
							// figure out rnumber
							int rnumber=reldef.getGuessedNumber(rname);
							newnode.setValue("rnumber",rnumber);
							
							// directionality
							
							if (InsRel.usesdir) {
							    n4=nm.getNamedItem("dir");
							    int dir=0;
							    if (n4!=null) {
							        String dirs=n4.getNodeValue();
							        if ("unidirectional".equals(dirs)) {
							            dir=1;
							        } else if ("bidirectional".equals(dirs)) {
							            dir=2;
							        } else {
    							        log.error("invalid 'dir' attribute encountered in "+bul.getTableName()+" value="+dirs);
							        }
							    }
							    if (dir==0) {
				                    MMObjectNode relnode = reldef.getNode(rnumber);
				                    if (relnode!=null) {
				                        dir = relnode.getIntValue("dir");
				                    }
							    }
							    if (dir!=1) dir=2;
				                newnode.setValue("dir",dir);
							}
							
						} catch(Exception e) {
						    log.error(e);
						    log.error(Logging.stackTrace(e));
						}
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
										if (value==null)  value="";
										newnode.setValue(key,value);
									} else if (type==FieldDefs.TYPE_INTEGER) {
										try { 
											newnode.setValue(key,Integer.parseInt(value));
										} catch(Exception e) {
											newnode.setValue(key,-1);
										}
									} else { 
										log.error("XMLRelationNodeReader node error : "+key+" "+value+" "+type);
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
			log.error("XMLRelationNodeReader can't access builder : "+bul);
		}
		n1=n1.getNextSibling();
	}
	return(nodes);
    }


}

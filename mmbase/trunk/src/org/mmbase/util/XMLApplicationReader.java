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

import org.mmbase.module.corebuilders.*;
import org.mmbase.module.database.support.*;

/**
*/
public class XMLApplicationReader  {

    Document document;
    DOMParser parser;


    public XMLApplicationReader(String filename) {

	File file = new File(filename);
       	if(!file.exists()) {
  		System.out.println("ERROR -> Application file "+filename+" does not exist");
         }
        try {
            parser = new DOMParser();
            parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
            parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
            //Errors errors = new Errors();
            //parser.setErrorHandler(errors);
            parser.parse(filename);
            document = parser.getDocument();


	   /*
	    System.out.println("*** START XML APPLICATION READER FOR : "+filename);	
	    System.out.println("Application name="+getApplicationName());	
	    System.out.println("Application version="+getApplicationVersion());	
	    System.out.println("Application auto-deploy="+getApplicationAutoDeploy());	
	    System.out.println("Needed builders="+getNeededBuilders());	
	    System.out.println("Needed reldefs="+getNeededRelDefs());	
	    System.out.println("Allowed relations="+getAllowedRelations());	
	    System.out.println("DataSources="+getDataSources());	
	    System.out.println("RelationSources="+getRelationSources());	
	    System.out.println("*** END XML APPLICATION READER FOR : "+filename);	
	    */
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }

    /**
    * get the name of this application
    */
    public String getApplicationName() {
	Node n1=document.getFirstChild();
	if (n1!=null) {
		NamedNodeMap nm=n1.getAttributes();
		if (nm!=null) {
			Node n2=nm.getNamedItem("name");
			String name=n2.getNodeValue();
			return(name);
		}
	}
	return(null);
    }


    /**
    * get the version of this application
    */
    public int getApplicationVersion() {
	Node n1=document.getFirstChild();
	if (n1!=null) {
		NamedNodeMap nm=n1.getAttributes();
		if (nm!=null) {
			Node n2=nm.getNamedItem("version");
			String tmp=n2.getNodeValue();
			try {
				int version=Integer.parseInt(tmp);
				return(version);
			} catch (Exception e) {
				return(-1);
			}
		}
	}
	return(-1);
    }

    /**
    * get the version of this application
    */
    public boolean getApplicationAutoDeploy() {
	Node n1=document.getFirstChild();
	if (n1!=null) {
		NamedNodeMap nm=n1.getAttributes();
		if (nm!=null) {
			Node n2=nm.getNamedItem("auto-deploy");
			if (n2!=null) {
				String tmp=n2.getNodeValue();
				if (tmp.equals("true")) return(true);
			}
		}
	}
	return(false);
    }


    /**
    * get the version of this application
    */
    public String getApplicationMaintainer() {
	Node n1=document.getFirstChild();
	if (n1!=null) {
		NamedNodeMap nm=n1.getAttributes();
		if (nm!=null) {
			Node n2=nm.getNamedItem("maintainer");
			if (n2!=null) {
				String tmp=n2.getNodeValue();
				return(tmp);
			}
		}
	}
	return(null);
    }




    /**
    * getNeededBuilders for this application
    */
    public Vector getNeededBuilders() {
	Vector results=new Vector();
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("neededbuilderlist")) {
				Node n3=n2.getFirstChild();
				while (n3!=null) {
					if (n3.getNodeName().equals("builder")) {
						Hashtable bset=new Hashtable();	
						Node n5=n3.getFirstChild();
						bset.put("name",n5.getNodeValue());
						NamedNodeMap nm=n3.getAttributes();
						if (nm!=null) {
							Node n4=nm.getNamedItem("maintainer");
							if (n4!=null) bset.put("maintainer",n4.getNodeValue());
							n4=nm.getNamedItem("version");
							if (n4!=null) bset.put("version",n4.getNodeValue());
						}
						results.addElement(bset);
					}
					n3=n3.getNextSibling();
				}
			}
			n2=n2.getNextSibling();
		}
	}
	return(results);
    }


    /**
    * getNeededRelDefs for this application
    */
    public Vector getNeededRelDefs() {
	Vector results=new Vector();
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("neededreldeflist")) {
				Node n3=n2.getFirstChild();
				while (n3!=null) {
					if (n3.getNodeName().equals("reldef")) {
						Hashtable bset=new Hashtable();	
						NamedNodeMap nm=n3.getAttributes();
						if (nm!=null) {
							Node n4=nm.getNamedItem("source");
							if (n4!=null) bset.put("source",n4.getNodeValue());
							n4=nm.getNamedItem("target");
							if (n4!=null) bset.put("target",n4.getNodeValue());
							n4=nm.getNamedItem("direction");
							if (n4!=null) bset.put("direction",n4.getNodeValue());
							n4=nm.getNamedItem("guisourcename");
							if (n4!=null) bset.put("guisourcename",n4.getNodeValue());
							n4=nm.getNamedItem("guitargetname");
							if (n4!=null) bset.put("guitargetname",n4.getNodeValue());
						}
						results.addElement(bset);
					}
					n3=n3.getNextSibling();
				}
			}
			n2=n2.getNextSibling();
		}
	}
	return(results);
    }


    /**
    * getNeededBuilders for this application
    */
    public Vector getAllowedRelations() {
	Vector results=new Vector();
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("allowedrelationlist")) {
				Node n3=n2.getFirstChild();
				while (n3!=null) {
					if (n3.getNodeName().equals("relation")) {
						Hashtable bset=new Hashtable();	
						NamedNodeMap nm=n3.getAttributes();
						if (nm!=null) {
							Node n4=nm.getNamedItem("from");
							if (n4!=null) bset.put("from",n4.getNodeValue());
							n4=nm.getNamedItem("to");
							if (n4!=null) bset.put("to",n4.getNodeValue());
							n4=nm.getNamedItem("type");
							if (n4!=null) bset.put("type",n4.getNodeValue());
						}
						results.addElement(bset);
					}
					n3=n3.getNextSibling();
				}
			}
			n2=n2.getNextSibling();
		}
	}
	return(results);
    }


    /**
    * datasources attached to this application
    */
    public Vector getDataSources() {
	Vector results=new Vector();
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("datasourcelist")) {
				Node n3=n2.getFirstChild();
				while (n3!=null) {
					if (n3.getNodeName().equals("datasource")) {
						Hashtable bset=new Hashtable();	
						NamedNodeMap nm=n3.getAttributes();
						if (nm!=null) {
							Node n4=nm.getNamedItem("path");
							if (n4!=null) bset.put("path",n4.getNodeValue());
						}
						results.addElement(bset);
					}
					n3=n3.getNextSibling();
				}
			}
			n2=n2.getNextSibling();
		}
	}
	return(results);
    }


    /**
    * relationsources attached to this application
    */
    public Vector getRelationSources() {
	Vector results=new Vector();
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("relationsourcelist")) {
				Node n3=n2.getFirstChild();
				while (n3!=null) {
					if (n3.getNodeName().equals("relationsource")) {
						Hashtable bset=new Hashtable();	
						NamedNodeMap nm=n3.getAttributes();
						if (nm!=null) {
							Node n4=nm.getNamedItem("path");
							if (n4!=null) bset.put("path",n4.getNodeValue());
						}
						results.addElement(bset);
					}
					n3=n3.getNextSibling();
				}
			}
			n2=n2.getNextSibling();
		}
	}
	return(results);
    }


    /**
    * contextsources attached to this application
    */
    public Vector getContextSources() {
	Vector results=new Vector();
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("contextsourcelist")) {
				Node n3=n2.getFirstChild();
				while (n3!=null) {
					if (n3.getNodeName().equals("contextsource")) {
						Hashtable bset=new Hashtable();	
						NamedNodeMap nm=n3.getAttributes();
						if (nm!=null) {
							Node n4=nm.getNamedItem("path");
							if (n4!=null) bset.put("path",n4.getNodeValue());
							n4=nm.getNamedItem("type");
							if (n4!=null) bset.put("type",n4.getNodeValue());
							n4=nm.getNamedItem("goal");
							if (n4!=null) bset.put("goal",n4.getNodeValue());
						}
						results.addElement(bset);
					}
					n3=n3.getNextSibling();
				}
			}
			n2=n2.getNextSibling();
		}
	}
	return(results);
    }


    /**
    */
    public String getInstallNotice() {
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("install-notice")) {
				Node n3=n2.getFirstChild();
		    		String value="";
				if (n3!=null) {
					value=n3.getNodeValue();
				}
				return(value);
			
			}
			n2=n2.getNextSibling();
		}
	}
	return(null);
    }


    /**
    */
    public String getDescription() {
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("description")) {
				Node n3=n2.getFirstChild();
		    		String value="";
				if (n3!=null) {
					value=n3.getNodeValue();
				}
				return(value);
			
			}
			n2=n2.getNextSibling();
		}
	}
	return(null);
    }
}

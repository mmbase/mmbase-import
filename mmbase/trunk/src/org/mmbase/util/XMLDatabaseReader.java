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
public class XMLDatabaseReader  {

    Document document;
    DOMParser parser;


    public XMLDatabaseReader(String filename) {
        try {
            parser = new DOMParser();
            parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
            parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
            //Errors errors = new Errors();
            //parser.setErrorHandler(errors);
			File file = new File(filename);
        	if(!file.exists()) {
  				System.out.println("ERROR -> Database file "+filename+" does not exist (check <DATABASE> tag in mmbaseroot.xml)");
           	}
            parser.parse(filename);
            document = parser.getDocument();

	    /*
	    System.out.println("*** START XML CONFIG READER FOR : "+filename);	
	    System.out.println("database name="+getName());	
	    System.out.println("database type-mapping="+getTypeMapping());	
	    System.out.println("database create="+getCreateScheme());	
	    System.out.println("database not-null="+getNotNullScheme());	
	    System.out.println("database disallowed fields="+getDisallowedFields());	
	    System.out.println("*** END XML CONFIG READER FOR : "+filename);	
	    */
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }

    /**
    * get the status of this builder
    */
    public String getName() {
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("name")) {
				Node n3=n2.getFirstChild();
		    		return(n3.getNodeValue());
			
			}
			n2=n2.getNextSibling();
		}
	}
	return(null);
    }




    /**
    * mmbase database driver
    */
    public String getMMBaseDatabaseDriver() {
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("mmbasedriver")) {
				Node n3=n2.getFirstChild();
		    		return(n3.getNodeValue());
			
			}
			n2=n2.getNextSibling();
		}
	}
	return(null);
    }

    /**
    */
    public Hashtable getDisallowedFields() {
	Hashtable results=new Hashtable();
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("disallowed")) {
				Node n3=n2.getFirstChild();
				while (n3!=null) {
					if (n3.getNodeName().equals("field")) {
						NamedNodeMap nm=n3.getAttributes();
						if (nm!=null) {
							Node n5=nm.getNamedItem("name");
							String name=n5.getNodeValue();
							Node n6=nm.getNamedItem("replacement");
							String replacement=n6.getNodeValue();
							//System.out.println("DIS="+name+" replacement="+replacement);
							results.put(name,replacement);
						}
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
    public String getCreateScheme() {
	Hashtable table=new Hashtable();
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("scheme")) {
				Node n3=n2.getFirstChild();
				while (n3!=null) {
					if (n3.getNodeName().equals("create")) {
						Node n4=n3.getFirstChild();
		    				return(n4.getNodeValue());
					}
					n3=n3.getNextSibling();
				}
			}
			n2=n2.getNextSibling();
		}
	}
	return(null);
    }

    /**
    */
    public String getPrimaryKeyScheme() {
	Hashtable table=new Hashtable();
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("scheme")) {
				Node n3=n2.getFirstChild();
				while (n3!=null) {
					if (n3.getNodeName().equals("primary-key")) {
						Node n4=n3.getFirstChild();
		    				return(n4.getNodeValue());
					}
					n3=n3.getNextSibling();
				}
			}
			n2=n2.getNextSibling();
		}
	}
	return(null);
    }

    /**
    */
    public String getKeyScheme() {
	Hashtable table=new Hashtable();
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("scheme")) {
				Node n3=n2.getFirstChild();
				while (n3!=null) {
					if (n3.getNodeName().equals("key")) {
						Node n4=n3.getFirstChild();
		    				return(n4.getNodeValue());
					}
					n3=n3.getNextSibling();
				}
			}
			n2=n2.getNextSibling();
		}
	}
	return(null);
    }


    /**
    */
    public String getNotNullScheme() {
	Hashtable table=new Hashtable();
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("scheme")) {
				Node n3=n2.getFirstChild();
				while (n3!=null) {
					if (n3.getNodeName().equals("not-null")) {
						Node n4=n3.getFirstChild();
		    				return(n4.getNodeValue());
					}
					n3=n3.getNextSibling();
				}
			}
			n2=n2.getNextSibling();
		}
	}
	return(null);
    }


    /**
    */
    public Hashtable getTypeMapping() {
	Hashtable table=new Hashtable();
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("mapping")) {
				Node n3=n2.getFirstChild();
				while (n3!=null) {
		    			if (n3.getNodeName().equals("type-mapping")) {
						Node n4=n3.getFirstChild();
						NamedNodeMap nm=n3.getAttributes();
						if (nm!=null) {
							Node n5=nm.getNamedItem("mmbase-type");
							String tmp=n5.getNodeValue();
							String dbtype=n4.getNodeValue();

							int mmbasetype=-1;
							if (tmp.equals("VARCHAR")) mmbasetype=FieldDefs.TYPE_STRING;
							if (tmp.equals("STRING")) mmbasetype=FieldDefs.TYPE_STRING;
							if (tmp.equals("INTEGER")) mmbasetype=FieldDefs.TYPE_INTEGER;
							if (tmp.equals("TEXT")) mmbasetype=FieldDefs.TYPE_TEXT;
							if (tmp.equals("BYTE")) mmbasetype=FieldDefs.TYPE_BYTE;
							if (tmp.equals("FLOAT")) mmbasetype=FieldDefs.TYPE_FLOAT;
							if (tmp.equals("DOUBLE")) mmbasetype=FieldDefs.TYPE_DOUBLE;
							if (tmp.equals("LONG")) mmbasetype=FieldDefs.TYPE_LONG;
			
							dTypeInfos dtis=(dTypeInfos)table.get(new Integer(mmbasetype));
							if (dtis==null) {
								dtis=new dTypeInfos();
								table.put(new Integer(mmbasetype),dtis);
							}
							dTypeInfo dti=new dTypeInfo();
							dti.mmbaseType=mmbasetype;
							dti.dbType=dbtype;
		
							// does it also have a min size ?
							n5=nm.getNamedItem("min-size");
							if (nm!=null) {
								try {
									tmp=n5.getNodeValue();
									int size=Integer.parseInt(tmp);
									dti.minSize=size;
								} catch(Exception e) {}
							}

							// does it also have a min size ?
							n5=nm.getNamedItem("max-size");
							if (nm!=null) {
								try {
									tmp=n5.getNodeValue();
									int size=Integer.parseInt(tmp);
									dti.maxSize=size;
								} catch(Exception e) {}
							}

							dtis.maps.addElement(dti);
						}	
					}
					n3=n3.getNextSibling();
				}
			
			}
			n2=n2.getNextSibling();
		}
	}
	return(table);
    }

    /**
    * get the pluralnames of this builder
    */
    public Hashtable getPluralNames() {
	Hashtable hash=new Hashtable();
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("names")) {
				Node n3=n2.getFirstChild();
				while (n3!=null) {
					if (n3.getNodeName().equals("plural")) {
						Node n4=n3.getFirstChild();
						NamedNodeMap nm=n3.getAttributes();
						if (nm!=null) {
							Node n5=nm.getNamedItem("xml:lang");
							hash.put(n5.getNodeValue(),n4.getNodeValue());
						}	
					}
					n3=n3.getNextSibling();
				}
			
			}
			n2=n2.getNextSibling();
		}
	}
	return(hash);
    }

}

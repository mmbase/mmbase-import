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

/**
*/
public class XMLBuilderReader  {

    Document document;
    DOMParser parser;


    public XMLBuilderReader(String filename) {
        try {
            parser = new DOMParser();
            parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
            parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
	    //if there's a dtd use it to validate, otherwise do nothing
            parser.setFeature("http://apache.org/xml/features/validation/dynamic", true);
            //set own errorHandler
	    XMLErrorHandler errorHandler = new XMLErrorHandler();
            parser.setErrorHandler(errorHandler);
	    //use own entityResolver for converting the dtd's url to a local file
            EntityResolver resolver = new XMLEntityResolver();
	    parser.setEntityResolver(resolver);
            parser.parse(filename);
            document = parser.getDocument();
	
	    /*
	    System.out.println("*** START XML CONFIG READER FOR : "+filename);	
	    System.out.println("builder status="+getStatus());	
	    System.out.println("builder classfile="+getClassFile());	
	    System.out.println("builder properties="+getProperties());	
	    System.out.println("builder fielddefs="+getFieldDefs());	
	    System.out.println("builder pluralnames="+getPluralNames());	
	    System.out.println("builder signularnames="+getSingularNames());	
	    System.out.println("builder descriptions="+getDescriptions());	
	    System.out.println("builder searchage="+getSearchAge());	
	    System.out.println("*** END XML CONFIG READER FOR : "+filename);	
	    */
	} catch(Exception e) {
	    e.printStackTrace();
	}
    }

    /**
    * get the status of this builder
    */
    public String getStatus() {
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("status")) {
				Node n3=n2.getFirstChild();
		    		return(n3.getNodeValue());
			
			}
			n2=n2.getNextSibling();
		}
	}
	return(null);
    }

    /**
    * get the Search Age
    */
    public int getSearchAge() {
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("searchage")) {
				Node n3=n2.getFirstChild();
				int val=30;
				try {
					val=Integer.parseInt(n3.getNodeValue());
				} catch(Exception e) {}
		    		return(val);
			
			}
			n2=n2.getNextSibling();
		}
	}
	return(30);
    }

    /**
    * get the classfile of this builder
    */
    public String getClassFile() {
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("classfile")) {
				Node n3=n2.getFirstChild();
		    		return(n3.getNodeValue());
			
			}
			n2=n2.getNextSibling();
		}
	}
	return(null);
    }


    /**
    * get the fieldDefs of this builder
    */
    public Vector getFieldDefs() {
	Vector defs=new Vector();
	int pos=1;
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("fieldlist")) {
				Node n3=n2.getFirstChild();
				while (n3!=null) {
		    		 	String val=n3.getNodeValue();
					if (n3.getNodeName().equals("field")) {
						FieldDefs def=decodeFieldDef(n3);
						def.DBPos=pos++;
						defs.addElement(def);
					}
					n3=n3.getNextSibling();
				}
			}
			n2=n2.getNextSibling();
		}
	}
	return(defs);
    }

 
	/**
	* decode one fielddef 
	*/
	public FieldDefs decodeFieldDef(Node n1) {
		// create a new FieldDefs we need to fill
		FieldDefs def=new FieldDefs();
	
		// we got the main field node, find the different types and decode
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			String name=n2.getNodeName();
			if (name.equals("gui")) {
				Node n3=n2.getFirstChild();
				while (n3!=null) {
					String name2=n3.getNodeName();
					if (name2.equals("name") || name2.equals("guiname")) {
						getGUIName(n3,def);
					} if (name2.equals("type") || name2.equals("guitype")) {
						def.GUIType=getGUIType(n3);
					}
					n3=n3.getNextSibling();
				}
			} else if (name.equals("db")) {
				Node n3=n2.getFirstChild();
				while (n3!=null) {
					String name2=n3.getNodeName();
					if (name2.equals("name")) {
						def.DBName=getDBName(n3);
					} if (name2.equals("type")) {
						getDBType(n3,def);
					}
					n3=n3.getNextSibling();
				}
			} else if (name.equals("editor")) {
				Node n3=n2.getFirstChild();
				while (n3!=null) {
					String name2=n3.getNodeName();
					if (name2.equals("positions")) { 
						Node n4=n3.getFirstChild();
						while (n4!=null) {
							String name3=n4.getNodeName();
							if (name3.equals("input")) {
								def.GUIPos=getEditorInputPos(n4);
							} else if (name3.equals("list")) {
								def.GUIList=getEditorListPos(n4);
							} else if (name3.equals("search")) {
								def.GUISearch=getEditorListPos(n4);
							}
							n4=n4.getNextSibling();
						}	
					}
					n3=n3.getNextSibling();
				}	
			}
			n2=n2.getNextSibling();
		}
		return(def);
	}
  
	public String getDBType(Node n1,FieldDefs def) {
		Node n2=n1.getFirstChild();
		String val=n2.getNodeValue();
		if (val.equals("INTEGER")) val="int";
		if (val.equals("VARCHAR")) val="varchar";
		if (val.equals("BYTE")) val="byte";
		def.DBType=val;
		NamedNodeMap nm=n1.getAttributes();
		if (nm!=null) {
			int size=-1;
			Node n3=nm.getNamedItem("size");
			if (n3!=null) {
				try {
					size=Integer.parseInt(n3.getNodeValue());
					def.DBSize=size;
				} catch(Exception e) {
				}
			}

			n3=nm.getNamedItem("notnull");
			if (n3!=null) {
				if (n3.getNodeValue().equals("true")) {
					def.DBNotNull=true;
				} else {
					def.DBNotNull=false;
				}
			}


			n3=nm.getNamedItem("key");
			if (n3!=null) {
				if (n3.getNodeValue().equals("true")) {
					def.isKey=true;
				} else {
					def.isKey=false;
				}
			}

			n3=nm.getNamedItem("state");
			if (n3!=null) {
				String tmp=n3.getNodeValue();
				if (tmp.equals("persistent")) {
					def.DBState=FieldDefs.DBSTATE_PERSISTENT;
				} else if (tmp.equals("virtual")) {
					def.DBState=FieldDefs.DBSTATE_VIRTUAL;
				} else if (tmp.equals("system")) {
					def.DBState=FieldDefs.DBSTATE_SYSTEM;
				} else {
					def.DBState=FieldDefs.DBSTATE_UNKNOWN;
				}
			}
		}
		return(val);
	}
 
	public String getGUIType(Node n1) {
		Node n2=n1.getFirstChild();
		String val=n2.getNodeValue();
		return(val);
	}


	public String getDBName(Node n1) {
		Node n2=n1.getFirstChild();
		String val=n2.getNodeValue();
		return(val);
	}


	public int getEditorInputPos(Node n1) {
		try { 
			Node n2=n1.getFirstChild();
			int val=Integer.parseInt(n2.getNodeValue());
			return(val);
		} catch(Exception e) {
			return(-1);
		}
	}


	public int getEditorListPos(Node n1) {
		try { 
			Node n2=n1.getFirstChild();
			int val=Integer.parseInt(n2.getNodeValue());
			return(val);
		} catch(Exception e) {
			return(-1);
		}
	}


	public int getEditorSearchPos(Node n1) {
		try { 
			Node n2=n1.getFirstChild();
			int val=Integer.parseInt(n2.getNodeValue());
			return(val);
		} catch(Exception e) {
			return(-1);
		}
	}

    /**
    * get the properties of this builder
    */
    public Hashtable getProperties() {
	Hashtable hash=new Hashtable();
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("properties")) {
				Node n3=n2.getFirstChild();
				while (n3!=null) {
		    		 	String val=n3.getNodeValue();
					if (n3.getNodeName().equals("property")) {
						Node n4=n3.getFirstChild();
						NamedNodeMap nm=n3.getAttributes();
						if (nm!=null) {
							Node n5=nm.getNamedItem("name");
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


    /**
    * get the descriptions of this builder
    */
    public Hashtable getDescriptions() {
	Hashtable hash=new Hashtable();
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("descriptions")) {
				Node n3=n2.getFirstChild();
				while (n3!=null) {
					if (n3.getNodeName().equals("description")) {
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

    /**
    */
    public void getGUIName(Node n1,FieldDefs def) {
	Node n2=n1.getFirstChild();
	if (n2!=null) {
		NamedNodeMap nm=n1.getAttributes();
		if (nm!=null) {
			Node n3=nm.getNamedItem("xml:lang");
			def.GUINames.put(n3.getNodeValue(),n2.getNodeValue());

		}	
	}
    }


    /**
    * get the pluralnames of this builder
    */
    public Hashtable getSingularNames() {
	Hashtable hash=new Hashtable();
	Node n1=document.getFirstChild();
	if (n1!=null) {
		Node n2=n1.getFirstChild();
		while (n2!=null) {
			if (n2.getNodeName().equals("names")) {
				Node n3=n2.getFirstChild();
				while (n3!=null) {
					if (n3.getNodeName().equals("singular")) {
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

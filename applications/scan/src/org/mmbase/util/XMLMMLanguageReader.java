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
 * cjr@dds.nl
 *
 * XMLLanguageReader parses the .xml file in its argument in its constructor.
 * This .xml file should be formatted according to mmlanguage.dtd.
 * 
 * - getLanguageCode() - returns the language code of the .xml file
 * - getDictionary() - returns a hashtable with mmbase term identifiers to their
 *     translations in this specific language.
 * 
 * Uglinesses:
 * - no check whether the input file exists
 * - doesn't read when the xml:lang attribute is absent, but doesn't generate an error either
 * - left static main testcode in because the class isn't yet called from mmbase
 *
 */
public class XMLMMLanguageReader  {

    Document document;
    DOMParser parser;

    Hashtable languageList; // Hashtable from languagecode to Hashtables with dictionaries

    String languagecode;  // code for language, e.g. 'nl'
    Hashtable dictionary; // dictionary of mmbase term identifiers to translations in language


    public XMLMMLanguageReader(String filename) {
	dictionary = null;

        try {
            parser = new DOMParser();
            parser.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", true);
            parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);
            parser.parse(filename);
            document = parser.getDocument();
	    generateFromDOM();

	} catch(Exception e) {
	    e.printStackTrace();
	}
    }


    /**
     * generateFromDOM() walks the DOM-tree and converts the result to a Hashtable
     *
     */
    protected void generateFromDOM() {
	dictionary = new Hashtable();
	Node n1=document.getFirstChild();
	while (n1!=null) {
	    if (n1.getNodeName().equalsIgnoreCase("mmlanguage")) {
		NamedNodeMap nm=n1.getAttributes();
		if (nm!=null) {
		    Node nattr=nm.getNamedItem("xml:lang");
		    languagecode = nattr.getNodeValue();
		    //System.out.println("attribuut xml:lang = "+ nattr.getNodeValue());
		    
		    //System.out.println("toplevel: "+n1.getNodeName());
		    Node n2=n1.getFirstChild();
		    while (n2!=null) {
			if (n2.getNodeName().equalsIgnoreCase("dictionary")) {
			    System.out.println(n2.getNodeName());
			    Node n3=n2.getFirstChild();
			    while (n3!=null) {
				Node n4 = n3.getFirstChild();
				if (n4 != null && n4.getNodeType() == n4.TEXT_NODE) {
				    System.out.println(n3.getNodeName() + " -> " + n4.getNodeValue());
				    dictionary.put(n3.getNodeName(),n4.getNodeValue());
				}
				n3 = n3.getNextSibling();
			    }
			    break;
			}
			n2=n2.getNextSibling();
		    }
		    break;
		} else {
		    // Hm. This is a language file 
		}
	    }
	    n1 = n1.getNextSibling();
	}
	System.out.println("done");
    }
	
    /*
     * @return Language code of the language of the read .xml file
     */
    public String getLanguageCode() {
	return languagecode;
    }

    /*
     * @return Hashtable from mmbase term identifiers to their translations in the language.
     */
    public Hashtable getDictionary() {
	return dictionary;
    }

    /**
     * Test code:
     *
     */
    public static void main(String[] argv) {
	String path = "/opt2/mmbase/org/mmbase/config/future/modules/languages/nl.xml";


	    File f = new File(path);
	    if (f.exists()) {
		System.out.println("file exists");
		XMLMMLanguageReader reader = new XMLMMLanguageReader(path);
		//reader.generateLanguageList();
		System.out.println("language = "+reader.getLanguageCode());
		Hashtable dict = reader.getDictionary();
		Enumeration enum = dict.keys();
		while (enum.hasMoreElements()) {
		    String s = (String)enum.nextElement();
		    System.out.println(s+" => "+dict.get(s));
		}
		    
	    } else {
		System.out.println(path + "doesn't exist");
	    }

	System.out.println(path);
	System.out.println("test");
    }

}








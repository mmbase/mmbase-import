/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
package org.mmbase.util;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.Element;

/**
 * @author cjr@dds.nl
 * @version $Id: XMLMMLanguageReader.java,v 1.7 2003-02-10 23:44:38 nico Exp $
 *
 * $Log: not supported by cvs2svn $
 * Revision 1.6  2002/10/25 21:25:14  michiel
 * michiel: hack location of dtds
 *
 * Revision 1.5  2001/04/19 12:00:25  michiel
 * michiel: logging, indentation
 *
 * Revision 1.4  2000/08/20 10:53:44  case
 * cjr: minor changes, this class is now actually used
 *
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
 *
 */
public class XMLMMLanguageReader extends XMLBasicReader {

    private static Logger log = Logging.getLoggerInstance(XMLMMLanguageReader.class.getName());

    Hashtable languageList; // Hashtable from languagecode to Hashtables with dictionaries

    String languagecode;  // code for language, e.g. 'nl'
    Hashtable dictionary; // dictionary of mmbase term identifiers to translations in language


    public XMLMMLanguageReader(String filename) {
        super(filename, XMLMMLanguageReader.class);

        dictionary = null;

        generateFromDOM();
    }


    /**
     * generateFromDOM() walks the DOM-tree and converts the result to a Hashtable
     *
     */
    protected void generateFromDOM() {
        dictionary = new Hashtable();
        Element e = document.getDocumentElement();
        languagecode = getElementAttributeValue(e,"xml:lang");
        Element d = getElementByPath("mmlanguage.dictionary");
        Enumeration enum = getChildElements(d);
        while (enum.hasMoreElements()) {
            Element a = (Element)enum.nextElement();
            //System.out.println(getElementName(a)+" -> "+getElementValue(a));
            dictionary.put(getElementName(a),getElementValue(a));
        }
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








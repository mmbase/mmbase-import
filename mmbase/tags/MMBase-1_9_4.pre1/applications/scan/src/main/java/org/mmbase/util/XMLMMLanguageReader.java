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

import org.w3c.dom.Element;

/**
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
 * @application SCAN
 * @deprecated not used anywhere
 * @author cjr@dds.nl
 * @version $Id$
 */
public class XMLMMLanguageReader extends XMLBasicReader {

    Hashtable languageList; // Hashtable from languagecode to Hashtables with dictionaries

    String languagecode;  // code for language, e.g. 'nl'
    Hashtable<String, String> dictionary; // dictionary of mmbase term identifiers to translations in language


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
        dictionary = new Hashtable<String, String>();
        Element e = document.getDocumentElement();
        languagecode = getElementAttributeValue(e,"xml:lang");
        Element d = getElementByPath("mmlanguage.dictionary");

        for (Object element : getChildElements(d)) {
            Element a = (Element) element;
            dictionary.put(getElementName(a),getElementValue(a));
        }
    }

    /**
     * @return Language code of the language of the read .xml file
     */
    public String getLanguageCode() {
        return languagecode;
    }

    /*
     * @return Hashtable from mmbase term identifiers to their translations in the language.
     */
    public Hashtable<String, String> getDictionary() {
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
            Hashtable<String, String> dict = reader.getDictionary();
            Enumeration<String> enumeration = dict.keys();
            while (enumeration.hasMoreElements()) {
                String s = enumeration.nextElement();
                System.out.println(s+" => "+dict.get(s));
            }

        } else {
            System.out.println(path + "doesn't exist");
        }

        System.out.println(path);
        System.out.println("test");
    }

}








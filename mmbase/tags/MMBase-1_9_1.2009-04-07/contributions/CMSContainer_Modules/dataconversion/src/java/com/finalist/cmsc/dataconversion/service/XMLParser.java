package com.finalist.cmsc.dataconversion.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class XMLParser {

   public static Document parseXmlToDOM(String file) throws Exception {
      InputStream is; 
      is = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
      
      if(is == null) {
         is =  new FileInputStream(new File(file));
      }
      return parseXMLToDOM(is);
   }
   public static Document parseXMLToDOM(String s) throws Exception {
      return parseXMLToDOM(new StringReader(s));
    }

    public static Document parseXMLToDOM(StringReader stringReader)
                                  throws Exception {
       return parseXMLToDOM(new InputSource(stringReader));
    }

    public static Document parseXMLToDOM(InputStream inputStream)
                                  throws Exception {
       InputSource inputsource = new InputSource(inputStream);
       return parseXMLToDOM(inputsource);
    }

    public static Document parseXMLToDOM(InputSource inputSource)
                                  throws Exception {
      
       DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
       factory.setIgnoringComments(true);
       factory.setIgnoringElementContentWhitespace(true);
       DocumentBuilder builder = factory.newDocumentBuilder();
       factory.setValidating(true);       
       return  builder.parse(inputSource);
    }
  
}

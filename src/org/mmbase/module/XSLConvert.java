/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
package org.mmbase.module;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import org.mmbase.util.*;

/**
 * @author Case Roole, cjr@dds.nl
 * 
 * $Id: XSLConvert.java,v 1.1 2000-08-10 15:08:17 case Exp $
 *
 * $Log: not supported by cvs2svn $
 */
public class XSLConvert extends ProcessorModule {

    private String classname = getClass().getName();
    private String configpath;

    public void init() {
        configpath = System.getProperty("mmbase.config");
        if (configpath.endsWith(File.separator)) {
            configpath = configpath.substring(0,configpath.length()-1);
        }
    }



    public void reload() {}



    public void onload() {}



    public void unload() {}



    public void shutdown() {}


    /**
     * CALC, a support module for servscan.
     */
    public XSLConvert() {}

    /**
     * Generate a list of values from a command to the processor
     * 
     * NOT IMPLEMENTED FOR XSLConvert
     */
    public Vector  getList(scanpage sp,StringTagger tagger, String value) throws ParseException {
        return null;
    }

    /**
     * Execute the commands provided in the form values
     */
    public boolean process(scanpage sp, Hashtable cmds,Hashtable vars) {
        return(false);
    }

    /**
    *	Handle a $MOD command
    *
    * NOT IMPLEMENTED FOR XSLConvert
    */
    public String replace(scanpage sp, String cmds) {
        StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
        int count = tok.countTokens();
        String[] argv = new String[count];
        for (int i=0; i<count; i++) {
            argv[i] = tok.nextToken();
        }

        if (argv.length != 2) {
            return "$MOD-XSLCONVERT should have two arguments, e.g. $MOD-XSLCONVERT-xmlPath-xslFile";
        } else {
            String xmlPath = configpath+File.separator+argv[0];
            String xslPath = configpath+File.separator+"xslt"+File.separator+argv[1];

            if (!xslPath.endsWith(".xsl")) {
                xslPath = xslPath+".xsl";
            }
            if (!xmlPath.endsWith(".xml")) {
                xmlPath = xmlPath+".xml";
            }

            return transform(xmlPath,xslPath);
        }
        //return "[no command defined]";
    }

    /**
     * Transform XML file using an XSL file
     *
     * @param xmlPath Path to XML file
     * @param xslPath Path to XSL file
     * @return Converted document
     */
    public String transform(String xmlPath, String xslPath) {
        // Do nothing for the time being
        debug("XML file = "+xmlPath);
        debug("XSL file = "+xslPath);
        XSLTransformer T = new XSLTransformer();
        return T.transform(xmlPath,xslPath);
    }

    public String getModuleInfo() {
        return("Support XSL transformations of XML files, cjr@dds.nl");
    }

    private void debug( String msg ) {
        System.out.println( classname +":"+msg );
    }
}

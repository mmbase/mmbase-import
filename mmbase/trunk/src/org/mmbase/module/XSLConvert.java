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

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * @author Case Roole, cjr@dds.nl
 *
 * XSL conversion module
 *
 * Right now, only the replace() method is defined. It is called as:
 *   $MOD-XSLCONVERT-xmlPath-xslFile
 * where xmlPath is the path relative to mmbase.config and xslFile is
 * and xsl file located in the subdirectory xslt of mmbase.config.
 * 
 * $Id: XSLConvert.java,v 1.5 2001-07-09 12:30:02 jaco Exp $
 *
 * $Log: not supported by cvs2svn $
 * Revision 1.4  2001/04/11 10:06:55  michiel
 * michiel: new logging system
 *
 * Revision 1.3  2000/08/22 09:34:30  daniel
 * small fix for mmdemo
 *
 * Revision 1.2  2000/08/10 20:06:04  case
 * cjr: removed some debug and added description of module
 *
 * Revision 1.1  2000/08/10 15:08:17  case
 * cjr: XSL conversion module - call as $MOD-XSLCONVERT-xmlpath-xslfile
 *
 */
public class XSLConvert extends ProcessorModule {

    private static Logger log = Logging.getLoggerInstance(XSLConvert.class.getName()); 

    private String configpath;

    public void init() {
        String dtmp=System.getProperty("mmbase.mode");
        if (dtmp!=null && dtmp.equals("demo")) {
            String curdir=System.getProperty("user.dir");
            if (curdir.endsWith("orion")) {
                curdir=curdir.substring(0,curdir.length()-6);
            }
            configpath=curdir+"/config";
        } else {
            configpath = MMBaseContext.getConfigPath();
        }
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
    * It is called as:
    *   $MOD-XSLCONVERT-xmlPath-xslFile
    * where:
    *  - xmlPath is the path relative to mmbase.config and,
    *  - xslFile is xsl file located in the subdirectory xslt of mmbase.config.
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
        if (log.isDebugEnabled()) {
            log.debug("XML file = "+xmlPath);
            log.debug("XSL file = "+xslPath);
        }
        XSLTransformer T = new XSLTransformer();
        return T.transform(xmlPath,xslPath);
    }

    public String getModuleInfo() {
        return("Support XSL transformations of XML files, cjr@dds.nl");
    }

}

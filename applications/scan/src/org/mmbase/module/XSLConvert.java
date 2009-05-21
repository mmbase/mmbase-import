/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.module;

import java.util.*;
import java.io.*;

import org.mmbase.module.core.MMBaseContext;
import org.mmbase.util.*;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * XSL conversion module
 *
 * Right now, only the replace() method is defined. It is called as:
 *   $MOD-XSLCONVERT-xmlPath-xslFile
 * where xmlPath is the path relative to mmbase.config and xslFile is
 * and xsl file located in the subdirectory xslt of mmbase.config.
 *
 * @application XSL or Tools
 * @move org.mmbase.util.xml
 * @author Case Roole, cjr@dds.nl
 * @version $Id$
 */
public class XSLConvert extends ProcessorModule {

    private static final Logger log = Logging.getLoggerInstance(XSLConvert.class);

    private String configpath;

    public void init() {
        configpath = MMBaseContext.getConfigPath();
    }

    public XSLConvert(String name) {
        super(name);
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
    @Override public String replace(PageInfo sp, String cmds) {
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
        return XSLTransformer.transform(xmlPath,xslPath);
    }

    public String getModuleInfo() {
        return "Support XSL transformations of XML files, cjr@dds.nl";
    }

}

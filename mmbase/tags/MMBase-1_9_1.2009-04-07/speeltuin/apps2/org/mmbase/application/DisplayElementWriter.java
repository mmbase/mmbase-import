/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.application;

import java.io.*;
import java.util.*;
import org.mmbase.module.core.*;
import java.text.DateFormat;

import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;

/**
 * write the configuration of a Display element.
 *
 * @author Rob Vermeulen (VPRO)
 */
public class DisplayElementWriter  {
    private static Logger log = Logging.getLoggerInstance(DisplayElementWriter.class.getName());
    
    DisplayElementWriter(String filename, DisplayElement dc) throws Exception {
        String body="";
        // body+= "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n"+
        //               "<!DOCTYPE builder PUBLIC \"//MMBase - Application//\" \"http://www.mmbase.org/dtd/builder.dtd\">\n";
        
        body+="<display name=\""+dc.getName()+"\" version=\""+dc.getInstalledVersion()+
        "\" create-date=\""+dc.getCreationDate()+"\" MD5=\""+"hier moet nog een functie voor komen"+"\">\n\n";
        
        // write description
        body+="\t<description>";
        body+=dc.getDescription();
        body+="</description>\n\n";
        
        body+="\t<requires>\n";
        for (Enumeration e = dc.getDependencies().elements();e.hasMoreElements();) {
            Hashtable os = (Hashtable)e.nextElement();
            body+="\t\t<require ";
            body+="component=\""+os.get("component")+"\" ";
            body+="name=\""+os.get("name")+"\" ";
            body+="method=\""+os.get("method")+"\" ";
            body+="create-date=\""+os.get("create-date")+"\" ";
            body+="/>\n";
        }
        body+="\t</requires>\n\n";
        
        // write default web path
        body+="\t<default-web-path>";
        body+=dc.getDefaultWebPath();
        body+="</default-web-path>\n";
        
        // write content to file
        body+="</display>\n";
        FileUtils.saveFile(filename, body);
    }
}

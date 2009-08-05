/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.application;

import java.io.*;
import java.util.*;
import java.text.DateFormat;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;

/**
 * write the configuration of a DataSet element
 *
 * @author Rob Vermeulen (VPRO)
 */
public class DataSetElementWriter  {
    private static Logger log = Logging.getLoggerInstance(DataSetElementWriter.class.getName());
    
    DataSetElementWriter(String filename, DataSetElement dc) throws Exception {
        String body="";
        // body+= "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n"+
        //               "<!DOCTYPE builder PUBLIC \"//MMBase - Application//\" \"http://www.mmbase.org/dtd/builder.dtd\">\n";
        
        DateFormat df = DateFormat.getDateTimeInstance();
        body+="<dataset name=\""+dc.getName()+"\" version=\""+dc.getInstalledVersion()+
        "\" create-date=\""+df.format(new Date())+"\" md5=\""+"fb3fb9 000000 df2b9f ffffff"+"\">\n\n";
        
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
        
        body+="\t<objects>\n";
        for (Enumeration e = dc.getObjectSources().elements();e.hasMoreElements();) {
            Hashtable os = (Hashtable)e.nextElement();
            body+="\t\t<source ";
            body+="nodemanager=\""+os.get("nodemanager")+"\" ";
            FileInputStream fis = new FileInputStream(dc.getElementPath()+"data"+File.separator+os.get("nodemanager")+".xml");
            body+="md5=\""+MD5.calculate(fis)+"\" ";
            body+="/>\n";
        }
        body+="\t</objects>\n\n";
        
        body+="\t<relations>\n";
        for (Enumeration e = dc.getRelationSources().elements();e.hasMoreElements();) {
            Hashtable os = (Hashtable)e.nextElement();
            body+="\t\t<source ";
            body+="nodemanager=\""+os.get("nodemanager")+"\" ";
            FileInputStream fis = new FileInputStream(dc.getElementPath()+"data"+File.separator+os.get("nodemanager")+".xml");
            body+="md5=\""+MD5.calculate(fis)+"\" ";
            body+="/>\n";
        }
        body+="\t</relations>\n\n";
        
        // write backup method
        body+="\t<backupmethod>\n";
        for (Enumeration e = dc.getBackupMethods().elements();e.hasMoreElements();) {
            Hashtable bm = (Hashtable)e.nextElement();
            body+="\t\t<contextsource path=\""+bm.get("path")+"\" ";
            body+="type=\""+bm.get("type")+"\" ";
            body+="goal=\""+bm.get("goal")+"\" />\n ";
        }
        body+="\t</backupmethod>\n";
        
        // write content to file
        body+="</dataset>\n";
        FileUtils.saveFile(filename, body);
    }
}

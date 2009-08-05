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
 * writes all configuration information of the main application config file.
 *
 * @author Rob Vermeulen (VPRO)
 */
public class ApplicationConfigWriter  {
    private static Logger log = Logging.getLoggerInstance(ApplicationConfigWriter.class.getName());
    
    /**
     * write the application configuration file.
     * This configuration file contains information about the components and elements of the application.
     */
    ApplicationConfigWriter(String filename,Application app) throws Exception {
        String body="";
        // body+= "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n"+
        //               "<!DOCTYPE builder PUBLIC \"//MMBase - Application//\" \"http://www.mmbase.org/dtd/builder.dtd\">\n";
        
        DateFormat df = DateFormat.getDateTimeInstance();
        body+="<application name=\""+app.getName()+"\" version=\""+app.getInstalledVersion()+"\" auto-deploy=\""+app.getAutoDeploy()+
        "\" create-date=\""+df.format(new Date())+"\" MD5=\""+"hier moet nog een functie voor komen"+"\">\n\n";
        
        // write description
        body+="\t<description>";
        body+=app.getDescription();
        body+="</description>\n\n";
        
        // Write maintainer information
        body+="\t<maintainer>\n";
        body+="\t\t<name>"+app.getMaintainerName()+"</name>\n";
        body+="\t\t<company>"+app.getMaintainerCompany()+"</company>\n";
        body+="\t\t<info>"+app.getMaintainerInfo()+"</info>\n";
        body+="\t\t<email>"+app.getMaintainerEmail()+"</email>\n";
        body+="\t\t<homebase>"+app.getMaintainerHomebase()+"</homebase>\n";
        body+="\t</maintainer>\n\n";
        
        // write Display Components
        body+="\t<displays>\n";
        Component c = app.getComponent("Displays");
        if(c!=null) {
            for (Enumeration e = c.getElements().elements();e.hasMoreElements();) {
                ElementInterface ei = (ElementInterface)e.nextElement();
                body+="\t\t<display ";
                body+="name=\""+ei.getName()+"\" ";
                body+="auto-deploy=\""+ei.getAutoDeploy()+"\" ";
                body+="create-date=\""+ei.getCreationDate()+"\" ";
                body+="MD5=\""+ei.getMD5()+"\" ";
                body+="/>\n";
            }
        }
        body+="\t</displays>\n\n";
        
        // write CloudLayout Components
        body+="\t<cloudlayouts>\n";
        c = app.getComponent("CloudLayouts");
        if(c!=null)  {
            for (Enumeration e = c.getElements().elements();e.hasMoreElements();) {
                ElementInterface ei = (ElementInterface)e.nextElement();
                body+="\t\t<cloudlayout ";
                body+="name=\""+ei.getName()+"\" ";
                body+="auto-deploy=\""+ei.getAutoDeploy()+"\" ";
                body+="create-date=\""+ei.getCreationDate()+"\" ";
                body+="MD5=\""+ei.getMD5()+"\" ";
                body+="/>\n";
            }
        }
        body+="\t</cloudlayouts>\n\n";
        
        // write Data Components
        body+="\t<datasets>\n";
        c = app.getComponent("DataSets");
        if(c!=null) {
            for (Enumeration e = c.getElements().elements();e.hasMoreElements();) {
                ElementInterface ei = (ElementInterface)e.nextElement();
                body+="\t\t<dataset ";
                body+="name=\""+ei.getName()+"\" ";
                body+="auto-deploy=\""+ei.getAutoDeploy()+"\" ";
                body+="create-date=\""+ei.getCreationDate()+"\" ";
                body+="MD5=\""+ei.getMD5()+"\" ";
                body+="/>\n";
            }
        }
        body+="\t</datasets>\n";
        
        // write content to file
        body+="</application>\n";
        FileUtils.saveFile(filename, body);
    }
}

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
 * write the configuration of a CloudLayout element
 *
 * @author Rob Vermeulen (VPRO)
 */
public class CloudLayoutElementWriter  {
    private static Logger log = Logging.getLoggerInstance(CloudLayoutElementWriter.class.getName());
    
    CloudLayoutElementWriter(String filename, CloudLayoutElement ce) throws Exception {
        String body="";
        // body+= "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n"+
        //               "<!DOCTYPE builder PUBLIC \"//MMBase - Application//\" \"http://www.mmbase.org/dtd/builder.dtd\">\n";
        
        DateFormat df = DateFormat.getDateTimeInstance();
        body+="<cloudlayout name=\""+ce.getName()+"\" version=\""+ce.getInstalledVersion()+
        "\" create-date=\""+df.format(new Date())+"\" md5=\""+"fb3fb9 000000 df2b9f ffffff"+"\">\n\n";
        
        // write description
        body+="\t<description>";
        body+=ce.getDescription();
        body+="</description>\n\n";
        
        body+="\t<requires>\n";
        for (Enumeration e = ce.getDependencies().elements();e.hasMoreElements();) {
            Hashtable os = (Hashtable)e.nextElement();
            body+="\t\t<require ";
            body+="component=\""+os.get("component")+"\" ";
            body+="name=\""+os.get("name")+"\" ";
            body+="method=\""+os.get("method")+"\" ";
            body+="create-date=\""+os.get("create-date")+"\" ";
            body+="/>\n";
        }
        body+="\t</requires>\n\n";
        
        
        body+="\t<neededbuilderlist>\n";
        for (Enumeration e = ce.getNeededBuilders().elements();e.hasMoreElements();) {
            Hashtable os = (Hashtable)e.nextElement();
            body+="\t\t<builder ";
            body+="maintainer=\""+os.get("maintainer")+"\" ";
            body+="version=\""+os.get("version")+"\" ";
            FileInputStream fis = new FileInputStream(ce.getElementPath()+"builders"+File.separator+os.get("name")+".xml");
            body+="md5=\""+MD5.calculate(fis)+"\"";
            body+=">";
            body+=os.get("name")+"</builder>\n";
        }
        body+="\t</neededbuilderlist>\n\n";
        
        body+="\t<neededreldeflist>\n";
        for (Enumeration e = ce.getNeededRelDefs().elements();e.hasMoreElements();) {
            Hashtable os = (Hashtable)e.nextElement();
            body+="\t\t<reldef ";
            body+="source=\""+os.get("source")+"\" ";
            body+="target=\""+os.get("target")+"\" ";
            body+="direction=\""+os.get("direction")+"\" ";
            body+="guisourcename=\""+os.get("guisourcename")+"\" ";
            body+="guitargetname=\""+os.get("guitargetname")+"\" ";
            body+="builder=\""+os.get("builder")+"\" ";
            body+="/>\n";
        }
        body+="\t</neededreldeflist>\n\n";
        
        body+="\t<allowedrelationlist>\n";
        for (Enumeration e = ce.getNeededRelations().elements();e.hasMoreElements();) {
            Hashtable os = (Hashtable)e.nextElement();
            body+="\t\t<relation ";
            body+="from=\""+os.get("from")+"\" ";
            body+="to=\""+os.get("to")+"\" ";
            body+="type=\""+os.get("type")+"\" ";
            if(os.get("count")!=null) {
                body+="count=\""+os.get("count")+"\" ";
            } else {
                body+="count=\"-1\" ";
            }
            body+="/>\n";
        }
        body+="\t</allowedrelationlist>\n";
        
        // write content to file
        body+="</cloudlayout>\n";
        FileUtils.saveFile(filename, body);
    }
}

/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/
package org.mmbase.util;

import java.io.*;
import java.util.*;
import org.mmbase.module.*;
import org.mmbase.module.core.*;

import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;

/**
 * @javadoc
 * @deprecated-now use org.mmbase.xml.ModuleWriter instead
 * @author Daniel Ockeloen
 * @version 19 Apr 2001
 */
public class XMLModuleWriter  {

    // logger
    private static Logger log = Logging.getLoggerInstance(XMLModuleWriter.class.getName());

    public static boolean writeXMLFile(String filename,Module mod) {
        String header = "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n"+
                        "<!DOCTYPE module PUBLIC \"//MMBase - module//\" \"http://www.mmbase.org/dtd/module.dtd\">\n";
        //String header = "";

        String body=header+"<module maintainer=\""+mod.getMaintainer()+"\" version=\""+mod.getVersion()+"\">\n\n";

        // status
        body+="\t<!-- <status>\n";
        body+="\twhat is the status of this module options : active or inactive\n";
        body+="\t-->\n";
        body+="\t<status>active</status>\n\n";


        // classfile
        body+="\t<!-- <classfile>\n";
        body+="\t-->\n";
        body+="\t<classfile>"+mod.getClassName()+"</classfile>\n\n";

        // properties
        body+="\t<!-- <properties>\n";
        body+="\tyou can define properties to be used by the classfile (if used) it uses\n";
        body+="\ta key/value system. Its a optional tag.\n";
        body+="\t-->\n";
        body+="\t<properties>\n";
        Hashtable props=mod.getInitParameters();
        if (props!=null) {
            for (Enumeration e=props.keys();e.hasMoreElements();) {
                String name=(String)e.nextElement();
                String value=(String)props.get(name);
                body+="\t\t<property name=\""+name+"\">"+value+"</property>\n";
            }
        }
        body+="\t</properties>\n\n";

        // the end of the builder file
        body+="</module>";

        // print it
        saveFile(filename,body);
        return true;
    }



    static boolean saveFile(String filename,String value) {
        File sfile = new File(filename);
        try {
            DataOutputStream scan = new DataOutputStream(new FileOutputStream(sfile));
            scan.writeBytes(value);
            scan.flush();
            scan.close();
        } catch(Exception e) {
            log.error(e.getMessage());
        log.error(Logging.stackTrace(e));
        }
        return true;
    }
}

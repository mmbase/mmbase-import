/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.module.tools;

import org.mmbase.module.*;
import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.util.logging.*;
import java.util.*;

public class RelDefConvert {
    /**
     * Logger routine
     */
    private static Logger log = Logging.getLoggerInstance(RelDefConvert.class.getName());
    
    private static MMBase mmbaseRoot;
    
    public RelDefConvert() throws Exception {
        // get the mmbaseRoot, when not already there, it will be started....
        mmbaseRoot=(MMBase)Module.getModule("MMBASEROOT");
        // check if the system was started as it is supposed 2 start...
        if(mmbaseRoot==null){
            throw new Exception("Could not find MMBASEROOT Module : Property 'mmbase.config' == <?incorrect?>");
        }
    }
    
    public static void main(String[] argv) {
        // check if the property has been set for config dir....
        if (MMBaseContext.getConfigPath() == null) {
            log.fatal("Please use the property mmbase.config");
            log.fatal("Usage: java -Dmmbase.config=<path-to-config> RelDefConvert");
        } else {
            try {
                RelDefConvert rdc=new RelDefConvert();
                RelDef reldef=mmbaseRoot.getRelDef();
                if (reldef==null) {
                    throw new Exception("RelDef does not exist ("+mmbaseRoot.baseName+"_reldef)");
                }
                
                if (!reldef.usesbuilder) {
                    throw new Exception("RelDef does not have a builder field defined ("+mmbaseRoot.baseName+"_reldef)");
                }
                
                int insRelID = mmbaseRoot.getTypeDef().getIntValue("insrel");
                if (insRelID<=0) {
                    throw new Exception("insrel does not exist in ("+mmbaseRoot.baseName+"_typedef)");
                }
                
                int i=0;
                int ie=0;
                for (Enumeration nodes=reldef.search(""); nodes.hasMoreElements();) {
                    ie++;
                    MMObjectNode node=(MMObjectNode)nodes.nextElement();
                    int ibuilder = node.getIntValue("builder");
                    if (ibuilder<=0) {
                        String sname=node.getStringValue("sname");
                        ibuilder=mmbaseRoot.getTypeDef().getIntValue(sname);
                        if (ibuilder==-1) ibuilder =insRelID;
                        node.setValue("builder",ibuilder);
                        try {
                            node.commit();
                        } catch (Exception e) {
                            log.error("Possible invalid data in relation definition node : " +e.toString() );
                        }
                        i++;
                    };
                }
                log.info("Checked "+ie+" nodes, changed "+i+" nodes.");
            } catch(Exception e) {
                log.fatal( e.toString() );
            }
            
        }
        System.exit(0);
    }
}









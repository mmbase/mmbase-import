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

public class InsRelConvert extends Object {
    /**
     * Logger routine
     */
    private static Logger log = Logging.getLoggerInstance(InsRelConvert.class.getName());
    
    private static MMBase mmbaseRoot;
    
    InsRelConvert() {
        // get the mmbaseRoot, when not already there, it will be started....
        mmbaseRoot=(MMBase)Module.getModule("MMBASEROOT");
        // check if the system was started as it is supposed 2 start...
        if(mmbaseRoot==null){
            throw new RuntimeException("Could not find MMBASEROOT Module : Property 'mmbase.config' == <?incorrect?>");
        }
    }
    
    public static void main(String[] argv) {
        // check if the property has been set for config dir....
        if (MMBaseContext.getConfigPath() == null) {
            log.fatal("Please use the property mmbase.config");
            log.fatal("Usage: java -Dmmbase.config=<path-to-config> RelDefConvert");
        } else {
            try {
                InsRelConvert rdc=new InsRelConvert();
                if (!InsRel.usesdir) {
                    throw new RuntimeException("You have not yet converted all relation builders - you need to define a dir field!");
                }
                for(Enumeration buls = mmbaseRoot.getMMObjects(); buls.hasMoreElements();) {
                    MMObjectBuilder bul = (MMObjectBuilder)buls.nextElement();
                    if (bul instanceof InsRel) {
                        int i=0;
                        int ie=0;
                        for (Enumeration nodes=bul.search(""); nodes.hasMoreElements();) {
                            MMObjectNode node=(MMObjectNode)nodes.nextElement();
                            ie++;
                            int dir = node.getIntValue("dir");
                            if (dir<=0) {
                                int rnumber=node.getIntValue("rnumber");
                                MMObjectNode reldefnode= bul.getNode(rnumber);
                                dir=reldefnode.getIntValue("dir");
                                if ((dir<1) || (dir>2)) dir=2;
                                node.setValue("dir",dir);
                                try {
                                    node.commit();
                                } catch (Exception e) {
                                    log.error("Possible invalid data in relation node : " +e.toString() );
                                }
                                i++;
                            }
                        }
                        log.info("Checked "+ie+" nodes in "+bul.getTableName()+", changed "+i+" nodes.");
                    }
                }
            } catch(Exception e) {
                log.fatal( e.toString() ); e.printStackTrace();
            }
            
        }
        System.exit(0);
    }
}









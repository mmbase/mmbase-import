/* -*- tab-width: 4; -*-
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */
package org.mmbase.module.tools;

import java.util.*;
import java.io.*;

import org.mmbase.util.*;
import org.mmbase.module.*;
import org.mmbase.module.core.*;

import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;

/**
 * The module which provides access to the multimedia database
 * it creates, deletes and gives you methods to keep track of
 * multimedia objects. It does not give you direct methods for
 * inserting and reading them thats done by other objects
 *
 * @author Daniel Ockeloen
 */
public class MMImport extends ProcessorModule {
    
    private static Logger log = Logging.getLoggerInstance(MMImport.class.getName());
    
    MMBase mmb=null;
    
    public void init() {
        mmb=(MMBase)getModule("MMBASEROOT");
    }
    
    
    /**
     */
    public MMImport() {
    }
    
    /**
     * Generate a list of values from a command to the processor
     */
    public Vector  getList(scanpage sp,StringTagger tagger, String value) throws ParseException {
        String line = Strip.DoubleQuote(value,Strip.BOTH);
        StringTokenizer tok = new StringTokenizer(line,"-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            
        }
        return(null);
    }
    
    /**
     * Execute the commands provided in the form values
     */
    public boolean process(scanpage sp, Hashtable cmds,Hashtable vars) {
        String cmdline,token;
        
        for (Enumeration h = cmds.keys();h.hasMoreElements();) {
            cmdline=(String)h.nextElement();
            StringTokenizer tok = new StringTokenizer(cmdline,"-\n\r");
            token = tok.nextToken();
            if (token.equals("IMPORTXML")) {
                doImportXML(cmds,vars);
            }
        }
        return(false);
    }
    
    /**
     *	Handle a $MOD command
     */
    public String replace(scanpage sp, String cmds) {
        StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
        if (tok.hasMoreTokens()) {
            String cmd=tok.nextToken();
            if (cmd.equals("FIELD")) {
            }
        }
        return("No command defined");
    }
    
    public void maintainance() {
    }
    
    public void doImportXML(Hashtable cmds, Hashtable vars) {
        String importdir=(String)vars.get("importdir")+".xml";
        String importtype=(String)vars.get("importtype");
        String body=loadFile(importdir);
        if (importtype.equals("XML")) {
            importXML(body,vars);
        }
    }
    
    private void importXML(String body,Hashtable vars) {
        // get one xml file
        int pos=body.indexOf("<?xml");
        int count=0;
        while (pos!=-1) {
            String xmlpart=body.substring(pos);
            int nextpos=body.indexOf("<?xml",6);
            if (nextpos!=-1) {
                body=xmlpart.substring(nextpos);
                xmlpart=xmlpart.substring(0,nextpos);
                parseOneXML(xmlpart, vars);
            } else {
                parseOneXML(xmlpart, vars);
                body="";
            }
            pos=body.indexOf("<?xml");
            log.info("MMImport item "+(count++));
        }
    }
    
    private void parseOneXML(String body,Hashtable vars) {
        StringTokenizer tok = new StringTokenizer(body,"\n\r");
        String xmlline=tok.nextToken();
        String docline=tok.nextToken();
        
        
        String builderline=tok.nextToken();
        String endtoken="</"+builderline.substring(1);
        
        
        MMObjectBuilder bul=mmb.getMMObject(builderline.substring(1,builderline.length()-1));
        if (bul!=null) {
            MMObjectNode node=bul.getNewNode("import");
            // weird way
            String nodedata=body.substring(body.indexOf(builderline)+builderline.length());
            nodedata=nodedata.substring(0,nodedata.indexOf(endtoken));
            
            int bpos=nodedata.indexOf("<");
            while (bpos!=-1) {
                String key=nodedata.substring(bpos+1);
                key=key.substring(0,key.indexOf(">"));
                String begintoken="<"+key+">";
                endtoken="</"+key+">";
                
                String value=nodedata.substring(nodedata.indexOf(begintoken)+begintoken.length());
                value=value.substring(0,value.indexOf(endtoken));
                
                // set the node
                if (!key.equals("number") && !key.equals("otype") && !key.equals("owner")) {
                    node.setValue( key, node.getDBType(key), value);
                }
                
                nodedata=nodedata.substring(nodedata.indexOf(endtoken)+endtoken.length());
                bpos=nodedata.indexOf("<");
            }
            bul.insert("import",node);
        } else {
            log.warn("Import illegal builder");
        }
    }
    
    
    public String loadFile(String filename) {
        try {
            File sfile = new File(filename);
            FileInputStream scan =new FileInputStream(sfile);
            int filesize = (int)sfile.length();
            byte[] buffer=new byte[filesize];
            int len=scan.read(buffer,0,filesize);
            if (len!=-1) {
                String value=new String(buffer,0);
                return(value);
            }
            scan.close();
        } catch(Exception e) {
            log.error(Logging.stackTrace(e));
            return(null);
        }
        return(null);
    }
}

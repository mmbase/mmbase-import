/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.application;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import org.mmbase.module.core.*;
import org.mmbase.module.builders.Versions;

import java.io.File;
import java.util.*;
import java.util.jar.*;

import org.w3c.dom.*;

/**
 */
public class MD5Diff {
    private static Logger log = Logging.getLoggerInstance(Application.class.getName());
    private boolean calculated=false;
    
    Vector deleted = new Vector();
    Vector changed = new Vector();
    Vector added   = new Vector();
    
    Hashtable newMD5 = new Hashtable();
    Hashtable oldMD5 = new Hashtable();
    
    MD5Diff(String newfiles, String oldfiles) {
        
        XMLBasicReader reader = new XMLBasicReader(newfiles);
        for (Enumeration n = reader.getChildElements("MD5","entry");n.hasMoreElements();) {
            Element element= (Element)n.nextElement();
            newMD5.put(element.getAttribute("name"),element.getAttribute("MD5"));
        }
        
        reader = new XMLBasicReader(oldfiles);
        for (Enumeration n = reader.getChildElements("MD5","entry");n.hasMoreElements();) {
            Element element= (Element)n.nextElement();
            oldMD5.put(element.getAttribute("name"),element.getAttribute("MD5"));
        }
    }
    
    /**
     * calculate which files are: deleted, added, and changed.
     */
    private void calculate() {
        if(!calculated) {
            log.service("Calculating differences in MD5 files");
            
            //calculate added entries
            for(Enumeration e = newMD5.keys();e.hasMoreElements();) {
                Object filename = e.nextElement();
                if (!oldMD5.containsKey(filename)) {
                    added.addElement(filename);
                }
            }
            
            //calculate deleted entries
            for(Enumeration e = oldMD5.keys();e.hasMoreElements();) {
                Object filename = e.nextElement();
                if (!newMD5.containsKey(filename)) {
                    deleted.addElement(filename);
                }
            }
            
            // calculate changed entries
            for(Enumeration e = newMD5.keys();e.hasMoreElements();) {
                Object filename = e.nextElement();
                if (oldMD5.containsKey(filename)) {
                    String md5newfile=(String)newMD5.get(filename);
                    String md5oldfile=(String)oldMD5.get(filename);
                    if(!md5oldfile.equals(md5newfile)) {
                        changed.add(filename);
                    }
                }
            }
            
            // only calculate once
            calculated=true;
        }
    }
    
    /**
     * get the added file names
     * @return get the added files
     */
    public synchronized Vector getAdded() {
        calculate();
        return added;
    }
    
    /**
     * get the deleted file names
     * @return the deleted file names
     */
    public synchronized Vector getDeleted() {
        calculate();
        return deleted;
    }
    
    /**
     * get the changed file names
     * @return the changed file names
     */
    public synchronized Vector getChanged() {
        calculate();
        return changed;
    }
    
    /**
     * for testing purposes
     */
    public void showVector(Vector v) {
        log.debug("showing a vector");
        for(Enumeration e = v.elements();e.hasMoreElements();) {
            log.debug(""+e.nextElement());
        }
    }
    
    public synchronized boolean isChanged() {
        calculate();
        if (added.size()==0 && changed.size()==0 && deleted.size()==0) {
            return false;
        } else {
            return true;
        }
    }
}

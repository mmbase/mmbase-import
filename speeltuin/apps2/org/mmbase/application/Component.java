/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.application;

import org.mmbase.module.core.*;
import org.mmbase.module.corebuilders.*;
import org.mmbase.module.builders.*;
import org.mmbase.util.*;
import org.mmbase.util.logging.*;

import java.util.*;
import java.io.*;

/**
 * A Component is the superclass for all components.
 *
 * @author Rob Vermeulen (VPRO)
 */
public class Component {
    protected Logger log = Logging.getLoggerInstance(Component.class.getName());
    protected Application application = null;
    protected MMBase mmbase = null;
    private Hashtable status = new Hashtable();
    // the elements of this component. e.g. the real HTML or JSP pages of the Display Component.
    private Hashtable elements = new Hashtable();
    
    Component() {
        log.error("Use constructor Component(Application, MMBase");
    }
    
    Component(Application app, MMBase mmb) {
        application = app;
        mmbase = mmb;
    }
    
    /**
     * install all elements
     */
    public void install() throws Exception {
        for (Enumeration e = getElements().elements(); e.hasMoreElements();) {
            ElementInterface ei = (ElementInterface)e.nextElement();
            ei.install();
        }
    }
    
    /**
     * auto deploy all elements
     */
    public void autoDeploy() throws Exception {
        for (Enumeration e = getElements().elements(); e.hasMoreElements();) {
            ElementInterface ei = (ElementInterface)e.nextElement();
            ei.autoDeploy();
        }
    }
    
    /**
     * uninstall all elements
     */
    public void uninstall() throws Exception {
        for (Enumeration e = getElements().elements(); e.hasMoreElements();) {
            ElementInterface ei = (ElementInterface)e.nextElement();
            ei.uninstall();
        }
    }
    
    /**
     * save all elements
     */
    public void save() throws Exception {
        for (Enumeration e = getElements().elements(); e.hasMoreElements();) {
            ElementInterface ei = (ElementInterface)e.nextElement();
            ei.save();
        }
    }
    /**
     * give status of a certain action
     */
    public String getStatus(String which) {
        if (status.containsKey(which)) {
            return (String)status.get(which);
        } else {
            return "";
        }
    }
    
    /**
     * add status of a certain action
     */
    protected void addStatus(String key, String text) {
        if(status.containsKey(key)) {
            String value = (String)status.get(key);
            status.put(key,value+text);
        } else {
            status.put(key,text);
        }
    }
    
    /**
     * create a MD5 file with MD5 stamps of files.
     * @param md5file the MD5 stamps will be saved in this file
     * @param sources the root of the files you want to give an MD5 stamp
     */
    static protected void createMD5(String md5file, String sources) {
        try {
            // delete md5file if it already exists
            File file = new File(md5file);
            if (file.exists()) {
                if (file.isDirectory()) {
                    // FOUT
                } else {
                    file.delete();
                }
            }
            RandomAccessFile raf = new RandomAccessFile(md5file,"rw");
            raf.writeBytes("<MD5>\n");
            
            // If sources exist, create the entries, otherwise file is empty
            file = new File(sources);
            if (file.exists()) {
                createMD5Entries(raf, sources,sources.length());
            }
            raf.writeBytes("</MD5>\n");
            raf.close();
        } catch (Exception e) {
            System.out.println("component hier niet afvangen"+e);
        }
    }
    
    static private void createMD5Entries(RandomAccessFile md5file, String source, int pathlength) throws Exception {
        File file = new File(source);
        if (file.isDirectory()) {
            if(source.length()-1!=source.lastIndexOf(File.separator)) {
                source+=File.separator;
            }
            String[] files = file.list();
            for(int i=0;i<files.length;) {
                createMD5Entries(md5file, source+files[i],pathlength);
                i++;
            }
        } else {
            //copyFile(source, destination);a
            FileInputStream fis = new FileInputStream(source);
            md5file.writeBytes("    <entry MD5=\""+MD5.calculate(fis)+"\" name=\""+source.substring(pathlength)+"\"/>\n");
        }
    }
    
    /**
     * add an element
     * @param key name of the element
     * @param value the element itself
     */
    protected void addElement(Object key, Object value) {
        elements.put(key,value);
    }
    
    /**
     * delete an element
     * @param key the name of the element
     */
    protected void removeElement(Object key) {
        elements.remove(key);
    }
    
    /**
     * get all the elements. Key is the name of the element, value is the element itself.
     * @return all elements
     */
    public Hashtable getElements() {
        return elements;
    }
    
    /**
     * check if the element exists
     * @param elementName name of element to check
     * @return true if element exists, false otherwise
     */
    public boolean exists(String elementName) {
        if (elements.containsKey(elementName)) {
            return true;
        }
        return false;
    }
    
    /**
     * get an element
     * @return the element
     */
    public ComponentElement getElement(String name) {
        return (ComponentElement)elements.get(name);
    }
    
    /**
     * check dependencies of all elements
     */
    public void checkDependencies() throws Exception {
        for(Enumeration e = elements.elements();e.hasMoreElements();) {
            ComponentElement ce = (ComponentElement)e.nextElement();
            ce.checkDependencies();
        }
    }
}


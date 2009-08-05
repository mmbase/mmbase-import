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
import java.lang.*;

/**
 * DisplayComponent handles all display elements.
 *
 * @author Rob Vermeulen (VPRO)
 */
public class DisplayComponent extends Component implements ComponentInterface {
    
    DisplayComponent(Application app, MMBase mmb) {
        super(app,mmb);
        log.info("\t\tStarting Display Component");
        setDisplayElements();
    }
    
    /**
     * init all display elements
     */
    private void setDisplayElements() {
        for (Enumeration e = application.getDisplays().elements(); e.hasMoreElements();) {
            Hashtable display = (Hashtable)e.nextElement();
            DisplayElement de = new DisplayElement(display,this,application);
            addElement(display.get("name"),de);
            log.info("\t\t\tAdding DisplayElement "+display.get("name"));
        }
    }
    
    /**
     * create a new display element
     * @param name name of the new display element
     * @return the display element created
     */
    public DisplayElement createElement(String name) {
        Hashtable displayinfo = new Hashtable();
        displayinfo.put("name",name);
        DisplayElement de = new DisplayElement(displayinfo,this,application);
        addElement(name,de);
        log.info("\t\t\tAdding DisplayElement "+name);
        return de;
    }
    
    /**
     * delete a display element
     * @param name the name of the display element to delete
     */
    public void deleteElement(String name) throws Exception{
        DisplayElement display = (DisplayElement)getElement(name);
        if(display==null) {
            log.warn("Cannot delete display element "+name+", because it doesn't exists");
            return;
        }
        display.delete();
        removeElement(name);
        application.writeConfiguration();
        
        // If it was the last display component, than also remove the /displays/ directory
        if(getElements().size()==0) {
            log.info("Latest display element is removed");
            FileUtils.deleteFiles(application.getApplicationLocation()+"displays");
        }
    }
}

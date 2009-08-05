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
import java.io.File;

/**
 * CloudLayout component handles all cloud layout elements.
 *
 * @author Rob Vermeulen (VPRO)
 */
public class CloudLayoutComponent extends Component implements ComponentInterface {
    
    /**
     * create a new cloudlayout component.
     * @param app the application that creates this component
     * @param mmb a reference to MMBase
     */
    CloudLayoutComponent(Application app, MMBase mmb) {
        super(app,mmb);
        log.info("\t\tStarting CloudLayout component");
        setCloudLayouts();
    }
    
    /**
     * set all CloudLayouts
     */
    private void setCloudLayouts() {
        for (Enumeration e = application.getCloudLayouts().elements(); e.hasMoreElements();) {
            Hashtable dataset = (Hashtable)e.nextElement();
            CloudLayoutElement oe = new CloudLayoutElement(dataset,this,application);
            addElement(dataset.get("name"),oe);
            log.info("\t\t\tAdding CloudLayoutElement "+dataset.get("name"));
        }
    }
    
    /**
     * give the version of a builder
     * @param buildername the name of the builder
     * @return the version, -1 if the application is not installed
     **/
    public int getBuilderVersion(String buildername) {
        return ApplicationManager.getVersionBuilder().getInstalledVersion(buildername,"builder");
    }
    
    /**
     * create a new CloudLayout
     * @param name name of the cloudlayout
     */
    public CloudLayoutElement createElement(String name) {
        Hashtable cloudlayoutinfo = new Hashtable();
        cloudlayoutinfo.put("name",name);
        CloudLayoutElement ce = new CloudLayoutElement(cloudlayoutinfo,this,application);
        addElement(name,ce);
        log.info("\t\t\tAdding CloudLayout element "+name);
        return ce;
    }
    
    /**
     * delete a cloudlayout element
     * @param name the name of the element to delete
     */
    public void deleteElement(String name) throws Exception{
        CloudLayoutElement cloudlayout = (CloudLayoutElement)getElement(name);
        if(cloudlayout==null) {
            log.warn("Cannot delete cloudlayout element "+name+", because it doesn't exists");
            return;
        }
        cloudlayout.delete();
        removeElement(name);
        application.writeConfiguration();
        
        // If it was the last display component, than also remove the /displays/ directory
        if(getElements().size()==0) {
            FileUtils.deleteFiles(application.getApplicationLocation()+"cloudlayouts");
        }
    }
}


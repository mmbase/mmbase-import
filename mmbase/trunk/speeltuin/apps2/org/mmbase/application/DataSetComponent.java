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
 * DataSet Component controls all DATA (objects and relations) belonging to an application.
 *
 * @author Rob Vermeulen (VRPRO)
 */
public class DataSetComponent extends Component implements ComponentInterface {
    private Vector dataSources = null;
    private Vector relationSources = null;
    
    DataSetComponent(Application app, MMBase mmb) {
        super(app,mmb);
        log.info("\t\tStarting DataSetComponent");
        setDataSetElements();
    }
    
    /**
     * configure all data set elements
     */
    private void setDataSetElements() {
        for (Enumeration e = application.getDataSets().elements(); e.hasMoreElements();) {
            Hashtable dataset = (Hashtable)e.nextElement();
            DataSetElement de = new DataSetElement(dataset,this,application);
            addElement(dataset.get("name"),de);
            log.info("\t\t\tAdding DataSetElement "+dataset.get("name"));
        }
    }
    
    /**
     * create a new dataset element
     * @param name of dataset to create
     * @return the new dataset element
     */
    public DataSetElement createElement(String name) {
        Hashtable datasetinfo = new Hashtable();
        datasetinfo.put("name",name);
        DataSetElement de = new DataSetElement(datasetinfo,this,application);
        addElement(name,de);
        log.info("\t\t\tAdding DataSet Element "+name);
        return de;
    }
    
    /**
     * delete a dataset element
     * @param name the name of the dataset to delete
     */
    public void deleteElement(String name) throws Exception{
        ElementInterface dataset = (ElementInterface)getElement(name);
        if(dataset==null) {
            log.warn("Cannot delete dataset element "+name+", because it doesn't exists");
            return;
        }
        dataset.delete();
        removeElement(name);
        application.writeConfiguration();
        
        // If it was the last display component, than also remove the /displays/ directory
        if(getElements().size()==0) {
            log.info("Last dataset Component Element is removed, and the directory will also be removed.");
            FileUtils.deleteFiles(application.getApplicationLocation()+"datasets");
        }
    }
}

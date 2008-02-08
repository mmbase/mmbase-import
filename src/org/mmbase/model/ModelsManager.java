/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.model;

import org.mmbase.util.logging.*;
import org.mmbase.util.*;
import java.util.*;

public class ModelsManager {

    private static Logger log = Logging.getLoggerInstance(ModelsManager.class);
    private static HashMap models =  new HashMap();

    static {
        init();
    }

    public static void init() {
        ResourceLoader applicationLoader = ResourceLoader.getConfigurationRoot().getChildResourceLoader("applications");
        Iterator i = applicationLoader.getResourcePaths(ResourceLoader.XML_PATTERN, false).iterator();
        while (i.hasNext()) {
            String modelname = (String) i.next();
	    addModel(modelname.substring(0,modelname.length()-4),"applications/"+modelname);
        }
    }


    public static CloudModel addModel(String modelname,String path) {
	CloudModel cm =  new CloudModel(modelname);
	cm.setPath(path);
	models.put(modelname,cm);
	return cm;
    } 

    public static CloudModel  getModel(String name) {
	return (CloudModel)models.get(name);
    }

}

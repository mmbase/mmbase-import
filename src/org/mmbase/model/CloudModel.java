/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.model;

import org.mmbase.util.*;
import org.mmbase.util.logging.*;
import java.util.*;
import java.io.*;

public class CloudModel {

    private static Logger log = Logging.getLoggerInstance(CloudModel.class); 
    private String name;
    private String path;
    private HashMap builders = new HashMap();

    public CloudModel(String name) {
	this.name =  name;
    }
  
    public CloudModelBuilder addBuilder(String buildername,String path) {
	CloudModelBuilder cmb =  new CloudModelBuilder(buildername);
	cmb.setPath(path);
	builders.put(buildername,cmb);
	return cmb;
    } 

    public void setPath(String path) {
	this.path = path;
    }

    public CloudModelBuilder getModelBuilder(String name) {
	return (CloudModelBuilder)builders.get(name);
    }


    public boolean writeToFile(String filepath) {
       	    InputStream in = ResourceLoader.getConfigurationRoot().getResourceAsStream(path);
	    if (in!=null) {
       	    	try {                
			FileOutputStream out = new FileOutputStream(filepath);
        		byte[] buf = new byte[1024];
        		int len;
        		while ((len = in.read(buf)) > 0) {
            			out.write(buf, 0, len);
        		}
        		in.close();
                        out.flush();
        		out.close();
                    } catch(Exception e) {
                       e.printStackTrace();
		       return false;
                    }
		} else {
			log.info("Resource not found : "+path);
		}
	return true;
    }

}

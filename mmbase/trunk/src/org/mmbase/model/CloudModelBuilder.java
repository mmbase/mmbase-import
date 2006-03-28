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
import java.io.*;

public class CloudModelBuilder {

    private static Logger log = Logging.getLoggerInstance(CloudModelBuilder.class); 
    private String name;
    private String path;

    public CloudModelBuilder(String name) {
	this.name =  name;
   }

   public void setPath(String path) {
       this.path = path;
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
			log.error("Resource not found : "+path);
		}
	return true;
    }

}

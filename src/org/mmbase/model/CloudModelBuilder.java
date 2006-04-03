/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

 */

package org.mmbase.model;

import org.mmbase.util.logging.*;
import org.mmbase.core.*;
import org.mmbase.module.core.*;
import org.mmbase.util.*;
import org.mmbase.util.xml.*;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.xml.parsers.*;

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

    public boolean addField(int pos,String name,String type,String guitype,String state,String required,String unique,String size) {
	try {
	
       	     Document doc = ResourceLoader.getConfigurationRoot().getDocument(path);

             BuilderReader reader = new BuilderReader(doc,MMBase.getMMBase());

	    // add the extra field
            Element fe = reader.getElementByPath(doc.getDocumentElement(),"builder.fieldlist");
            if (fe!=null) {
		String newpart ="    <field>\r";
		newpart +="      <positions>\r";
		newpart +="        <input>"+pos+"</input>\r";
		newpart +="        <list>"+pos+"</list>\r";
		newpart +="        <search>"+pos+"</search>\r";
		newpart +="      </positions>\r";

		// convert to the new format, have to check this some more
		String ntype=guitype;
	        if (guitype.equals("string")) ntype="line";	
	        if (guitype.equals("field")) ntype="field";	

		newpart +="      <datatype base=\""+ntype+"\" xmlns=\"http://www.mmbase.org/xmlns/datatypes\"/>\r";
		newpart +="      <db>\r";
		newpart +="        <name>"+name+"</name>\r";
		newpart +="        <type key=\""+unique+"\" notnull=\""+required+"\" size=\""+size+"\" state=\""+state+"\">"+type+"</type>\r";
		newpart +="      </db>\r";
		newpart +="    </field>\r";
		Element nf = (reader.getDocumentBuilder(false,null,null).parse(new InputSource(new StringReader(newpart)))).getDocumentElement();
		fe.appendChild(doc.importNode(nf,true));
            }

	    // save the file back using the ResourceLoader
            try {                
                  ResourceLoader.getConfigurationRoot().storeDocument(path,doc);
            } catch(Exception e) {
                    e.printStackTrace();
            }

	    } catch (Exception e) {
                log.error("missing builderfile file ");
		e.printStackTrace();
	    }
	return true;
    }


}

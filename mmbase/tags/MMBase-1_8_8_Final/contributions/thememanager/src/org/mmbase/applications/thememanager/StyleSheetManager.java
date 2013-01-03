/*

This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.

The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license

*/

package org.mmbase.applications.thememanager;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;
import org.w3c.dom.*;
import org.mmbase.module.core.*;

import org.mmbase.util.*;

import org.mmbase.util.logging.Logging;
import org.mmbase.util.logging.Logger;

/**
 * @author Daniel Ockeloen
 * 
 */
public class StyleSheetManager {
 
    // logger
    static private Logger log = Logging.getLoggerInstance(StyleSheetManager.class); 
    private String id;
    private HashMap stylesheetclasses = new HashMap();


   public StyleSheetManager(String id) {
	this.id=id;
	readStyleSheet();
   }

   public String getId() {
	return id;
   }

   public Iterator getStyleSheetClasses() {
	return stylesheetclasses.values().iterator();
   }

   public StyleSheetClass getStyleSheetClass(String id) {
	return (StyleSheetClass)stylesheetclasses.get(id);
   }

   public StyleSheetClass addStyleSheetClass(String id) {
	StyleSheetClass nc=  new StyleSheetClass(id);
	stylesheetclasses.put(id,nc);
	return nc;
   }

   public void removeStyleSheetClass(String id) {
	stylesheetclasses.remove(id);
   }
	

   public boolean readStyleSheet() {
       String filename = MMBaseContext.getHtmlRoot()+File.separator+"mmbase"+File.separator+"thememanager"+File.separator+"css"+File.separator+id;
	   try {
        BufferedReader in = new BufferedReader(new FileReader(filename));
        String str;
        while ((str = in.readLine()) != null) {
	    if (str.indexOf("{")!=-1) decodeStyleClass(str,in);
        }
        in.close();
    } catch (IOException e) {
    }
    return true;
   }

   public boolean save() {
	String body = "";
        Iterator i=getStyleSheetClasses();
        while (i.hasNext()) {
              StyleSheetClass stc=(StyleSheetClass)i.next();
	      body+=stc.getId()+" {\n";
              Iterator j=stc.getProperties();
              while (j.hasNext()) {
		StyleSheetProperty ssp = (StyleSheetProperty)j.next();
		body+="\t"+ssp.getName()+": "+ssp.getValue()+";\n";
	      }
	      body+="}\n\n";
	}
       String filename = "mmbase"+File.separator+"thememanager"+File.separator+"css"+File.separator+id;
	saveFile(filename,body);
	return true;
   }

   private void decodeStyleClass(String line,BufferedReader in) {
        String str;
	line = line.substring(0,line.indexOf("{"));
	line = Strip.Whitespace(line,Strip.BOTH);
	StyleSheetClass sc = new StyleSheetClass(line);
	stylesheetclasses.put(line,sc);
	try {
        	while ((str = in.readLine()) != null && str.indexOf("}")==-1) {
			int pos = str.indexOf(":");
			if (pos!=-1) {
				String name = str.substring(0,pos);	
				name = Strip.Whitespace(name,Strip.BOTH);
				int end = str.indexOf(";");
				if (end!=-1) {
					String value = str.substring(pos+1,end);
					value = Strip.Whitespace(value,Strip.BOTH);
					sc.setProperty(name,value);
				}
			}
        	}
    	} catch (IOException e) {
    	}
   }



    static boolean saveFile(String filename,String value) {
        try {                
		Writer wr = ResourceLoader.getWebRoot().getWriter(filename);
                wr.write(value);
                wr.flush();
                wr.close();
        } catch(Exception e) {
                e.printStackTrace();
        }
        return true;
    }

}

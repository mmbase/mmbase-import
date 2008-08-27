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
 * @javadoc
 * @author Daniel Ockeloen
 * @version $Id: StyleSheetManager.java,v 1.4 2008-08-27 17:04:39 michiel Exp $
 */
public class StyleSheetManager {
 
    static private Logger log = Logging.getLoggerInstance(StyleSheetManager.class); 
    private final String id;
    private final Map<String, StyleSheetClass> stylesheetclasses = new HashMap<String, StyleSheetClass>();


    public StyleSheetManager(String id) {
        this.id = id;
        readStyleSheet();
    }

    public String getId() {
        return id;
    }

    public Iterator getStyleSheetClasses() {
        return stylesheetclasses.values().iterator();
    }

    public StyleSheetClass getStyleSheetClass(String id) {
        return stylesheetclasses.get(id);
    }

    public StyleSheetClass addStyleSheetClass(String id) {
        StyleSheetClass nc = new StyleSheetClass(id);
        stylesheetclasses.put(id, nc);
        return nc;
    }

    public void removeStyleSheetClass(String id) {
        stylesheetclasses.remove(id);
    }
    /**
     * @javadoc
     */
    public boolean readStyleSheet() {

        // TODO, doesn't work in war.
        String filename = MMBaseContext.getHtmlRoot() + File.separator + "mmbase" + File.separator + "thememanager" + File.separator + "css" + File.separator + id;
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

    /**
     * @javadoc
     */
    public boolean save() {
        String body = ""; // TODO StringBuilder
        Iterator<StyleSheetClass> i = getStyleSheetClasses();
        while (i.hasNext()) {
            StyleSheetClass stc = i.next();
            body += stc.getId() + " {\n";
            Iterator j = stc.getProperties();
            while (j.hasNext()) {
                StyleSheetProperty ssp = (StyleSheetProperty)j.next();
                body += "\t" + ssp.getName() + ": " + ssp.getValue() + ";\n";
            }
            body += "}\n\n";
        }
        String filename = "mmbase" + File.separator + "thememanager" + File.separator + "css" + File.separator + id;
        saveFile(filename,body);
        return true;
    }

    private void decodeStyleClass(String line,BufferedReader in) {
        String str;
        line = line.substring(0,line.indexOf("{"));
        line = Strip.whitespace(line,Strip.BOTH);
        StyleSheetClass sc = new StyleSheetClass(line);
        stylesheetclasses.put(line,sc);
        try {
            while ((str = in.readLine()) != null && str.indexOf("}")==-1) {
                int pos = str.indexOf(":");
                if (pos!=-1) {
                    String name = str.substring(0,pos);    
                    name = Strip.whitespace(name,Strip.BOTH);
                    int end = str.indexOf(";");
                    if (end!=-1) {
                        String value = str.substring(pos+1,end);
                        value = Strip.whitespace(value,Strip.BOTH);
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
            log.error(e);
        }
        return true;
    }

}

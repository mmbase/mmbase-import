/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
*/
package org.mmbase.module;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.mmbase.util.*;
import org.mmbase.module.core.*;

//XercesParser
import org.apache.xerces.parsers.*;
import org.xml.sax.*;

/**
 * Module , the wrapper for the modules.
 *
 * @author Rico Jansen
 * @author Rob Vermeulen (securitypart)
 *
 * @version $Revision: 1.14 $ $Date: 2000-12-20 00:27:31 $
 */
public abstract class ModuleXML extends Module {
    private static boolean debug = false;
    private static void     debug( String msg ) {
        System.out.println( classname +":"+ msg );
    }

    public static synchronized Hashtable loadModulesFromDisk() {
        return(loadModulesFromDisk_xml());
    }

    public static synchronized Hashtable loadModulesFromDisk_xml() {
        Class newclass;
        XMLProperties xmlReader=null;
        SAXParser parser=null;

        // SAXParser, if this is gonna used by more classes
        // it must not be created here but somewhere else (higher?)
        // and it must be possible to use another parser
        try {
            parser = new SAXParser();
            // Telling the parser it must not use some features
            // we're not using right now as dtd-validation and namespaces
            try {
                parser.setFeature("http://xml.org/sax/features/validation",false);
                parser.setFeature("http://xml.org/sax/features/namespaces",false);
            } catch (SAXNotSupportedException ex) {
                debug("loadModulesFromDisk(): failed because parser didn't support feature");
                ex.printStackTrace();
            }
            catch (SAXNotRecognizedException ex) {
                debug("loadModulesFromDisk(): failed because parser didn't recognized feature");
                ex.printStackTrace();
            }
            //create new ContentHandler and let the parser use it
            xmlReader = new XMLProperties();
            EntityResolver resolver = new XMLEntityResolver();
            parser.setEntityResolver(resolver);
            parser.setContentHandler(xmlReader);
        } catch(Exception e) {}




        if (debug) debug("loadModulesFromDisk(): mmbase.config="+System.getProperty("mmbase.config"));

        String dtmp=System.getProperty("mmbase.mode");
        if (dtmp!=null && dtmp.equals("demo")) {
            String curdir=System.getProperty("user.dir");
            if (curdir.endsWith("orion")) {
                curdir=curdir.substring(0,curdir.length()-6);
            }
            mmbaseconfig=curdir+"/config";
        } else {
            mmbaseconfig=System.getProperty("mmbase.config");
            if (mmbaseconfig==null) 
                debug("mmbase.config not defined, use property (-D)mmbase.config=/my/config/dir/");
        }

        MMBaseContext.setConfigPath(mmbaseconfig);

        // the container for the started modules
        Hashtable results=new Hashtable();


        // get us a (normal) propertie reader
        ExtendedProperties Reader=new ExtendedProperties();
        Hashtable mods = null;
        String filename_upper;

        // load the properties file of this server
        String filename=mmbaseconfig+"/modules";
        filename=filename.replace('/',(System.getProperty("file.separator")).charAt(0));
        filename=filename.replace('\\',(System.getProperty("file.separator")).charAt(0));

        //check if there's a xml-configuration file
        if (parser!=null && (new File(filename+".xml")).exists()) {
            filename = filename + ".xml";
            //parse the configuration file
            try {
		// rewrite as uri
		filename="file:///"+filename;
                parser.parse(new InputSource(filename));
                mods = xmlReader.getProperties();
            } catch(Exception f) {
                f.printStackTrace();
            }
        } else {
            filename = filename + ".properties";
            mods = Reader.readProperties(filename);
        }

        if (debug) debug("mods =" + mods.toString());

        // oke try loading all these modules and start em up
        for (Enumeration e=mods.keys();e.hasMoreElements();) {
            String key=(String)e.nextElement();
            String value=(String)mods.get(key);
            if( debug ) debug("loadModulesFromDisk(): MODULE="+key+" VAL="+value);

            // try starting the module and give it its properties
            try {
                newclass=Class.forName(value);
                if( debug ) debug("loadModulesFromDisk(): Loaded load class : "+newclass);
                Object mod = newclass.newInstance();
                if (mod!=null) {
                    results.put(key,mod);
                    Hashtable modprops = null;

                    // try to load the properties that are defined for this module
                    filename=mmbaseconfig+"/modules/"+key;
                    filename=filename.replace('/',(System.getProperty("file.separator")).charAt(0));
                    filename=filename.replace('\\',(System.getProperty("file.separator")).charAt(0));

                    filename_upper=mmbaseconfig+"/modules/"+key.toUpperCase();
                    filename_upper=filename_upper.replace('/',(System.getProperty("file.separator")).charAt(0));
                    filename_upper=filename_upper.replace('\\',(System.getProperty("file.separator")).charAt(0));

                    // extra check to load propertie files from weird places (security reasons for example)
                    String tmp=System.getProperty("mmbase.mod_"+key);
                    if (tmp!=null) {
                        if (debug) debug("Reading "+key+" mod file from : "+tmp);
                        filename=tmp;
                    }

                    //check if there's a xml-configuration file
                    if (parser!=null && (new File(filename+".xml")).exists()) {
                        filename = filename + ".xml";
			// rewrite as uri
			filename="file:///"+filename;
                        //parse the configuration file
                        parser.parse(new InputSource(filename));
                        modprops = xmlReader.getProperties();

                        // [begin] Special case for mmlanguage
                        // If there is no mmlanguage.xml file, look for the xml files in the ./languages/ subdirectory
                        // Each of these should contain the localization for one language
                    }
                    else if (key.equals("mmlanguage")) {
                        String languagedirname = mmbaseconfig+File.separator+"modules"+File.separator+"languages";
                        File dir = new File(languagedirname);
                        if (!dir.isDirectory()) {
                            throw new IOException("Path "+languagedirname+" is not a directory.\n");
                        } else {
                            String[] dirlist = dir.list(new XMLFilenameFilter());
                            XMLMMLanguageReader mmlreader;
                            modprops = new Hashtable();
                            Hashtable dictionary;
                            String languagecode;
                            Enumeration enum;
                            String k,v;
                            for (int i=0;i<dirlist.length;i++) {
                                if (debug) {
                                    debug("xml language file: "+languagedirname+File.separator+dirlist[i]);
                                }
                                mmlreader = new XMLMMLanguageReader(languagedirname+File.separator+dirlist[i]);
                                languagecode = mmlreader.getLanguageCode();
                                dictionary = mmlreader.getDictionary();
                                enum = dictionary.keys();

                                while (enum.hasMoreElements()) {
                                    k = (String)enum.nextElement();
                                    v = (String)dictionary.get(k);
                                    modprops.put(languagecode+"_"+k,v);
                                }
                            }
                            /* TO BE REMOVED
                            // debugging: let's see what we found
                            enum = modprops.keys();
                            while (enum.hasMoreElements()) {
                            k = (String)enum.nextElement();
                            v = (String)modprops.get(k);
                            debug("modprobs["+k+"] = "+v);
                        }
                            */
                        }
                        // [end]
                    }
                    else {
                        // Warning this is rather blunt -- revert to old-style "properties" format
                        filename = filename_upper + ".properties";
                        modprops = Reader.readProperties(filename);
                        //System.out.println("ModuleXML -> "+filename);
                        //System.out.println("ModuleXML -> "+modprops);
                    }
                    //debug("loadModulesFromDisk(): MOD "+key+" "+modprops);



                    if (modprops!=null) {
                        ((Module)mod).properties=modprops;
                    }
                }
            } catch(Exception f) {
                f.printStackTrace();
            }
        }
        return(results);
    }


    public static Object getModule(String name) {
        if (xmlinstalled) name=name.toLowerCase();
        // are the modules loaded yet ? if not load them
        if (modules==null) {
            debug("getModule("+name+"): Modules not loaded, loading them..");
            modules=loadModulesFromDisk();
            startModules();
            // also start the maintaince thread that calls all modules every x seconds
            mprobe = new ModuleProbe(modules);
        }

        // try to obtain the ref to the wanted module
        Object obj=modules.get(name);
        if (obj!=null) {
            return(obj);
        } else {
            // Ugly and should be removed ROB, I Agree DANIEL :)
            if(!name.equals("playlists")) {
                debug("getModule("+name+"): ERROR: No module loaded with this name!");
            }
            return(null);
        }
    }

}

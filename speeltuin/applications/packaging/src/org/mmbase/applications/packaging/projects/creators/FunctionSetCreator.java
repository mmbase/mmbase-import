/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.packages.projects.creators;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.Versions;
import org.mmbase.applications.packages.*;
import org.mmbase.applications.packages.projects.*;


import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

import org.w3c.dom.*;

/**
 * DisplayHtmlPackage, Handler for html packages
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class FunctionSetCreator extends BasicCreator implements CreatorInterface {


    private static Logger log = Logging.getLoggerInstance(FunctionSetCreator.class.getName());


    public static final String DTD_PACKAGING_FUNCTION_SET_1_0 = "packaging_function_set_1_0.dtd";
    public static final String PUBLIC_ID_PACKAGING_FUNCTION_SET_1_0 = "-//MMBase//DTD packaging_function_set config 1.0//EN";
 
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_PACKAGING_FUNCTION_SET_1_0, "DTD_PACKAGING_FUNCTION_SET_1_0", FunctionSetCreator.class);    }

    public FunctionSetCreator() {
    	cl=FunctionSetCreator.class;
   	prefix="packaging_function_set";
    }


   public boolean createPackage(Target target,int newversion) {

   	clearPackageSteps();

        // step1
        packageStep step=getNextPackageStep();
        step.setUserFeedBack("function/set packager started");

	String functionsetfile=target.getBaseDir()+getItemStringValue(target,"functionsetfile");	
	String functionsetdir=target.getBaseDir()+getItemStringValue(target,"functionsetdir");	

        step=getNextPackageStep();
        step.setUserFeedBack("used functionsetfile : "+functionsetfile);
        step=getNextPackageStep();
        step.setUserFeedBack("used functionsetdir : "+functionsetdir);

        String newfilename=MMBaseContext.getConfigPath()+"/packages/build/"+getName(target).replace(' ','_')+"@"+getMaintainer(target)+"_function_set_"+newversion;
	try {
  		JarOutputStream jarfile = new JarOutputStream(new FileOutputStream(newfilename+".tmp"),new Manifest());

	        step=getNextPackageStep();
       	 	step.setUserFeedBack("creating package.xml file...");
		createPackageMetaFile(jarfile,target,newversion);
        	step.setUserFeedBack("creating package.xml file...done");
	        step=getNextPackageStep();
       	 	step.setUserFeedBack("creating depends.xml file...");
		createDependsMetaFile(jarfile,target);
        	step.setUserFeedBack("creating depends.xml file...done");

                addFile(jarfile,functionsetfile,"functionsetfile.xml","functionset","");

		int filecount=addFiles(jarfile,functionsetdir,"xml","","functionset","");
                if (filecount==0) {
                        step=getNextPackageStep();
                        step.setUserFeedBack("did't add any function files, no files found");
                        step.setType(packageStep.TYPE_WARNING);
                }
		jarfile.close();
	} catch(Exception e) {
		e.printStackTrace();
	}

        step=getNextPackageStep();
        step.setUserFeedBack("function/set packager ended : "+getErrorCount()+" errors and "+getWarningCount()+" warnings");

        // update the build file to reflect the last build, should only be done if no errors
        if (getErrorCount()==0) {
                File f1 = new File(newfilename+".tmp");
                File f2 = new File(newfilename+".mmp");
                if (f1.renameTo(f2)) {
                        updatePackageTime(target,new Date(),newversion);
                        target.save();
                }
        }
        return true;
   }    

  public boolean decodeItems(Target target) {
        super.decodeItems(target);
        decodeStringItem(target,"functionsetfile");
        decodeStringItem(target,"functionsetdir");
        return true;
  }


   public String getXMLFile(Target target) {
        String body=getDefaultXMLHeader(target);
        body+=getDefaultXMLMetaInfo(target);
        body+="\t<functionsetfile>"+getItemStringValue(target,"functionsetfile")+"</functionsetfile>\n";
        body+="\t<functionsetdir>"+getItemStringValue(target,"functionsetdir")+"</functionsetdir>\n";
        body+=getPackageDependsXML(target);
        body+=getRelatedPeopleXML("initiators","initiator",target);
        body+=getRelatedPeopleXML("supporters","supporter",target);
        body+=getRelatedPeopleXML("developers","developer",target);
        body+=getRelatedPeopleXML("contacts","contact",target);
        body+=getDefaultXMLFooter(target);
        return body;
   }

   public void setDefaults(Target target) {
        target.setItem("functionsetfile","config/functions/functionsetfile.xml");
        target.setItem("functionsetdir","config/functions/sets/");
   }

}

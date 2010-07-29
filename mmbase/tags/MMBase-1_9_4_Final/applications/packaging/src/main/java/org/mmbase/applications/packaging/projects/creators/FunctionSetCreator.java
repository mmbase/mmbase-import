/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.projects.creators;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.mmbase.applications.packaging.ProviderManager;
import org.mmbase.applications.packaging.projects.Target;
import org.mmbase.applications.packaging.projects.packageStep;
import org.mmbase.util.xml.EntityResolver;

/**
 * DisplayHtmlPackage, Handler for html packages
 *
 * @author     Daniel Ockeloen (MMBased)
 */
public class FunctionSetCreator extends BasicCreator implements CreatorInterface {

    /**
     *  Description of the Field
     */
    public final static String DTD_PACKAGING_FUNCTION_SET_1_0 = "packaging_function_set_1_0.dtd";
    /**
     *  Description of the Field
     */
    public final static String PUBLIC_ID_PACKAGING_FUNCTION_SET_1_0 = "-//MMBase//DTD packaging_function_set config 1.0//EN";


    /**
     *  Description of the Method
     */
    public static void registerPublicIDs() {
        EntityResolver.registerPublicID(PUBLIC_ID_PACKAGING_FUNCTION_SET_1_0, "DTD_PACKAGING_FUNCTION_SET_1_0", FunctionSetCreator.class);
    }


    /**
     *Constructor for the FunctionSetCreator object
     */
    public FunctionSetCreator() {
        cl = FunctionSetCreator.class;
        prefix = "packaging_function_set";
    }


    /**
     *  Description of the Method
     *
     * @param  target      Description of the Parameter
     * @param  newversion  Description of the Parameter
     * @return             Description of the Return Value
     */
    public boolean createPackage(Target target, int newversion) {

        clearPackageSteps();

        // step1
        packageStep step = getNextPackageStep();
        step.setUserFeedBack("function/set packager started");

        String functionsetfile = target.getBaseDir() + getItemStringValue(target, "functionsetfile");
        String functionsetdir = target.getBaseDir() + getItemStringValue(target, "functionsetdir");

        step = getNextPackageStep();
        step.setUserFeedBack("used functionsetfile : " + functionsetfile);
        step = getNextPackageStep();
        step.setUserFeedBack("used functionsetdir : " + functionsetdir);

        String newfilename = getBuildPath() + getName(target).replace(' ', '_') + "@" + getMaintainer(target) + "_function_set_" + newversion;
        try {
            JarOutputStream jarfile = new JarOutputStream(new FileOutputStream(newfilename + ".tmp"), new Manifest());

            step = getNextPackageStep();
            step.setUserFeedBack("creating package.xml file...");
            createPackageMetaFile(jarfile, target, newversion);
            step.setUserFeedBack("creating package.xml file...done");
            step = getNextPackageStep();
            step.setUserFeedBack("creating depends.xml file...");
            createDependsMetaFile(jarfile, target);
            step.setUserFeedBack("creating depends.xml file...done");

            addFile(jarfile, functionsetfile, "functionsetfile.xml", "functionset", "");

            int filecount = addFiles(jarfile, functionsetdir, "xml", "", "functionset", "");
            if (filecount == 0) {
                step = getNextPackageStep();
                step.setUserFeedBack("did't add any function files, no files found");
                step.setType(packageStep.TYPE_WARNING);
            }
            jarfile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // update the build file to reflect the last build, should only be done if no errors
        if (getErrorCount() == 0) {
	    if (renameTempFile(newfilename)) {
                updatePackageTime(target, new Date(), newversion);
                target.save();
	    }
        }

	// do we need to send this to a publish provider ?
	if (target.getPublishState()) {
                ProviderManager.resetSleepCounter();
        	step=getNextPackageStep();
        	step.setUserFeedBack("publishing to provider : "+target.getPublishProvider());
        	step=getNextPackageStep();
        	step.setUserFeedBack("sending file : "+target.getId()+" ...");
		if (target.publish(newversion,step)) {
        		step.setUserFeedBack("sending file : "+target.getId()+" ... done");
		} else {
        		step.setUserFeedBack("sending file : "+target.getId()+" ... failed");
		}
	}

        step = getNextPackageStep();
        step.setUserFeedBack("function/set packager ended : " + getErrorCount() + " errors and " + getWarningCount() + " warnings");
        return true;
    }


    /**
     *  Description of the Method
     *
     * @param  target  Description of the Parameter
     * @return         Description of the Return Value
     */
    public boolean decodeItems(Target target) {
        super.decodeItems(target);
        decodeStringItem(target, "functionsetfile");
        decodeStringItem(target, "functionsetdir");
        return true;
    }


    /**
     *  Gets the xMLFile attribute of the FunctionSetCreator object
     *
     * @param  target  Description of the Parameter
     * @return         The xMLFile value
     */
    public String getXMLFile(Target target) {
        String body = getDefaultXMLHeader(target);
        body += getDefaultXMLMetaInfo(target);
        body += "\t<functionsetfile>" + getItemStringValue(target, "functionsetfile") + "</functionsetfile>\n";
        body += "\t<functionsetdir>" + getItemStringValue(target, "functionsetdir") + "</functionsetdir>\n";
        body += getPackageDependsXML(target);
        body += getRelatedPeopleXML("initiators", "initiator", target);
        body += getRelatedPeopleXML("supporters", "supporter", target);
        body += getRelatedPeopleXML("developers", "developer", target);
        body += getRelatedPeopleXML("contacts", "contact", target);
	if (target.getPublishProvider()!=null) {
		if (target.getPublishState()) {
			body+="\t<publishprovider name=\""+target.getPublishProvider()+"\" state=\"active\" sharepassword=\""+target.getPublishSharePassword()+"\" />\n";
		} else {
			body+="\t<publishprovider name=\""+target.getPublishProvider()+"\" state=\"inactive\" sharepassword=\""+target.getPublishSharePassword()+"\" />\n";
		}
	}
        body += getDefaultXMLFooter(target);
        return body;
    }


    /**
     *  Sets the defaults attribute of the FunctionSetCreator object
     *
     * @param  target  The new defaults value
     */
    public void setDefaults(Target target) {
        target.setItem("functionsetfile", "config/functions/functionsetfile.xml");
        target.setItem("functionsetdir", "config/functions/sets/");
    }

  public String getDefaultTargetName() {
        return "functionsets";
  }

}


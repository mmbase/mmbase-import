/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.projects.creators;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.Versions;
import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.util.*;
import org.mmbase.applications.packaging.projects.*;
import org.mmbase.applications.packaging.packagehandlers.*;
import org.mmbase.applications.packaging.projects.creators.dataapptools.*;
import org.mmbase.applications.packaging.projects.editors.cloudmodel.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

import org.w3c.dom.*;

/**
 * DisplayHtmlPackage, Handler for html packages
 *
 * @author     Daniel Ockeloen (MMBased)
 */
public class DataApps1Creator extends BasicCreator implements CreatorInterface {

    private static Logger log = Logging.getLoggerInstance(DataApps1Creator.class);

    /**
     *  Description of the Field
     */
    public final static String DTD_PACKAGING_DATA_APPS1_1_0 = "packaging_data_apps1_1_0.dtd";

    /**
     *  Description of the Field
     */
    public final static String PUBLIC_ID_PACKAGING_DATA_APPS1_1_0 = "-//MMBase//DTD packaging_data_apps1 config 1.0//EN";


    /**
     *  Description of the Method
     */
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_PACKAGING_DATA_APPS1_1_0, "DTD_PACKAGING_DATA_APPS1_1_0", DataApps1Creator.class);
    }


    /**
     *Constructor for the DataApps1Creator object
     */
    public DataApps1Creator() {
        cl = DataApps1Creator.class;
        prefix = "packaging_data_apps1";
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
        step.setUserFeedBack("data/apps1 packager started");

        String datafile = target.getBaseDir() + getItemStringValue(target, "datafile");
        //String datadir = target.getBaseDir() + getItemStringValue(target, "datadir");
        String datadir = datafile.substring(0,datafile.length()-8);

        boolean dataexport = false;
	if (getItemStringValue(target, "dataexport").equals("true")) {
		dataexport = true;
	}

        step = getNextPackageStep();
        step.setUserFeedBack("used datafile : " + datafile);
        //step = getNextPackageStep();
        //step.setUserFeedBack("used datadir : " + datadir);
        step = getNextPackageStep();
        step.setUserFeedBack("used dataexport :" + dataexport);

        String newfilename = getBuildPath() + getName(target).replace(' ', '_') + "@" + getMaintainer(target) + "_data_apps1_" + newversion;
        try {
            JarOutputStream jarfile = new JarOutputStream(new FileOutputStream(newfilename + ".tmp"), new Manifest());

	    if (dataexport) {
            	step = getNextPackageStep();
           	step.setUserFeedBack("exporting dataset ...");
	    	if (exportDataSet(step,target,datafile,datadir)) {
            		step.setUserFeedBack("creating dataset ...done");
	    	} else {
            		step.setUserFeedBack("creating dataset ...failed");
	    	}
	    }

            step = getNextPackageStep();
            step.setUserFeedBack("creating package.xml file...");
            createPackageMetaFile(jarfile, target, newversion);
            step.setUserFeedBack("creating package.xml file...done");
            step = getNextPackageStep();
            step.setUserFeedBack("creating depends.xml file...");
            createDependsMetaFile(jarfile, target);
            step.setUserFeedBack("creating depends.xml file...done");

            addFile(jarfile, datafile, "data.xml", "data", "");

            addFiles(jarfile, datadir, ".xml", "", "data", "");
            addFiles(jarfile, datadir, ".handle", "", "data", "");
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
        step.setUserFeedBack("data/apps1 packager ended : " + getErrorCount() + " errors and " + getWarningCount() + " warnings");
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
        decodeStringItem(target, "datacreator");
        decodeStringItem(target, "datafile");
        decodeStringItem(target, "datadir");
        decodeStringItem(target, "dataexport");
    	decodeDataFile(target,target.getBaseDir() + getItemStringValue(target, "datafile"));
        return true;
    }


    /**
     *  Gets the xMLFile attribute of the DataApps1Creator object
     *
     * @param  target  Description of the Parameter
     * @return         The xMLFile value
     */
    public String getXMLFile(Target target) {
        String body = getDefaultXMLHeader(target);
        body += getDefaultXMLMetaInfo(target);
        //body += "\t<datacreator>" + getItemStringValue(target, "datacreator") + "</datacreator>\n";
        body += "\t<datafile>" + getItemStringValue(target, "datafile") + "</datafile>\n";
        //body += "\t<datadir>" + getItemStringValue(target, "datadir") + "</datadir>\n";
        body += "\t<dataexport>" + getItemStringValue(target, "dataexport") + "</dataexport>\n";
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
     *  Sets the defaults attribute of the DataApps1Creator object
     *
     * @param  target  The new defaults value
     */
    public void setDefaults(Target target) {
        target.setItem("datafile", "datasets/example/data.xml");
        //target.setItem("datadir", "datasets/example/data/");
        target.setItem("dataexport", "false");
    }

    private boolean decodeDataFile(Target target,String datafile) {
	log.info("BLA="+datafile);
        File file = new File(datafile);
        if (file.exists()) {
		/*
            ExtendedDocumentReader reader = new ExtendedDocumentReader(datafile,DataApps1Package.class);
            if (reader != null) {
                org.w3c.dom.Node dc=reader.getElementByPath("dataset.selection");
                NamedNodeMap nm=dc.getAttributes();
                if (nm!=null) {
                       org.w3c.dom.Node n3=nm.getNamedItem("type");
                       if (n3!=null) {
				String type=n3.getNodeValue();
				target.setItem("datatype",type);	
				log.info("SETTING TYPE="+type);
			}
		}
	    }
	    */
	}
	return true;
    }

    private boolean exportDataSet(packageStep step,Target target,String datafile,String datadir) {
  	packageStep substep=step.getNextPackageStep();
        substep.setUserFeedBack("reading data.xml : "+datafile);
        File file = new File(datafile);
        if (file.exists()) {
            ExtendedDocumentReader reader = new ExtendedDocumentReader(datafile,DataApps1Package.class);
            if (reader != null) {
  		substep=step.getNextPackageStep();
        	substep.setUserFeedBack("reader : "+reader);
                org.w3c.dom.Node dc=reader.getElementByPath("dataset.selection");
                NamedNodeMap nm=dc.getAttributes();
                if (nm!=null) {
                       org.w3c.dom.Node n3=nm.getNamedItem("type");
                       if (n3!=null) {
				String type=n3.getNodeValue();
				if (type.equals("depth")) {
					exportDataSetApps1(reader,dc,step,target,datafile,datadir);
				}
		       }
		}
	    }
        }
	return true;
    }

    private boolean exportDataSetApps1(ExtendedDocumentReader reader,org.w3c.dom.Node dc,packageStep step,Target target,String datafile,String datadir) {
        org.w3c.dom.Node n=reader.getElementByPath("dataset.selector.model");
        NamedNodeMap nm=n.getAttributes();
        if (nm!=null) {
		String name = null;
		String maintainer = null;
		String version = null;
                org.w3c.dom.Node n3=nm.getNamedItem("name");
                if (n3!=null) {
			name=n3.getNodeValue();
		}
                n3=nm.getNamedItem("maintainer");
                if (n3!=null) {
			maintainer=n3.getNodeValue();
		}
                n3=nm.getNamedItem("version");
                if (n3!=null) {
			version=n3.getNodeValue();
		}
		log.info("NAME="+name+" maintainer="+maintainer+" version="+version);

		/*
        	org.w3c.dom.Node n2=reader.getElementByPath("datacreator.model.startnode");
		if (n2!=null) {
			String alias = null;
			String builder = null;
			String where = null;
			String depth = null;
        		nm=n2.getAttributes();
		        if (nm!=null) {
				// needs the alias check
                		n3=nm.getNamedItem("alias");
		                if (n3!=null) {
					alias=n3.getNodeValue();
				}
			} 
        		n3=reader.getElementByPath("datacreator.model.startnode.builder");
			if (n3!=null) {
				builder = n3.getFirstChild().getNodeValue();
			}
        		n3=reader.getElementByPath("datacreator.model.startnode.where");
			if (n3!=null) {
				where = n3.getFirstChild().getNodeValue();
			}
        		n3=reader.getElementByPath("datacreator.model.depth");
			if (n3!=null) {
				depth = n3.getFirstChild().getNodeValue();
			}
			log.info("alias="+alias+" builder="+builder+" where="+where+" depth="+depth);
			Vector resultmsgs = new Vector();
        		String id = name + "@" + maintainer + "_cloud/model";
		        id = id.replace(' ', '_');
        		id = id.replace('/', '_');
			HashSet filters = getWantedBuilders(target,id);
			Apps1DataWriter.write(filters,alias,builder,where,5,datafile,datadir);
		}
		*/
	}
	return true;
    }

    private HashSet getWantedBuilders(Target target,String id) {
	HashSet set = new HashSet();
	Project prj = target.getParent();
	Target mod = prj.getTargetById(id);
	CloudModelCreator modcr = (CloudModelCreator)mod.getCreator();
        Model model = new Model(modcr.getModelFile(mod));

        Iterator nbl=model.getNeededBuilders();
        while (nbl.hasNext()) {
            NeededBuilder nb=(NeededBuilder)nbl.next();
	    int i=MMBase.getMMBase().getTypeDef().getIntValue(nb.getName());
	    set.add(new Integer(i));
        }
	return set;
    }

  public String getDefaultTargetName() {
        return "data";
  }

}


/*
 *  This software is OSI Certified Open Source Software.
 *  OSI Certified is a certification mark of the Open Source Initiative.
 *  The license (Mozilla version 1.0) can be read at the MMBase site.
 *  See http://www.MMBase.org/license
 */
package org.mmbase.applications.packaging.projects.creators;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.mmbase.applications.packaging.ProjectManager;
import org.mmbase.applications.packaging.ProviderManager;
import org.mmbase.applications.packaging.packagehandlers.DataApps1Package;
import org.mmbase.applications.packaging.projects.Project;
import org.mmbase.applications.packaging.projects.Target;
import org.mmbase.applications.packaging.projects.packageStep;
import org.mmbase.applications.packaging.projects.creators.dataapptools.Apps1DataWriter;
import org.mmbase.applications.packaging.projects.editors.cloudmodel.Model;
import org.mmbase.applications.packaging.projects.editors.cloudmodel.NeededBuilder;
import org.mmbase.applications.packaging.util.ExtendedDocumentReader;
import org.mmbase.module.core.MMBase;
import org.mmbase.module.core.MMObjectNode;
import org.mmbase.module.core.VirtualBuilder;
import org.mmbase.util.xml.EntityResolver;
import org.mmbase.util.logging.Logger;
import org.mmbase.util.logging.Logging;
import org.w3c.dom.NamedNodeMap;

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
        EntityResolver.registerPublicID(PUBLIC_ID_PACKAGING_DATA_APPS1_1_0, "DTD_PACKAGING_DATA_APPS1_1_0", DataApps1Creator.class);
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
        File file = new File(datafile);
        if (file.exists()) {
            ExtendedDocumentReader reader = new ExtendedDocumentReader(datafile,DataApps1Package.class);
            if (reader != null) {
                org.w3c.dom.Node dc=reader.getElementByPath("dataset.selection");
		if (dc!=null) {
                	NamedNodeMap nm=dc.getAttributes();
                	if (nm!=null) {
                      	        org.w3c.dom.Node n3=nm.getNamedItem("type");
               	        	if (n3!=null) {
					String type=n3.getNodeValue();
					target.setItem("datatype",type);	
					// this should be in sepr.files 
					target.setItem("type",type);
					if (type.equals("depth")) decodeDepthItems(target,reader);
				}
			}
		}
	    }
	}
	return true;
    }

    private void decodeDepthItems(Target target,ExtendedDocumentReader reader) {

       	org.w3c.dom.Node n2=reader.getElementByPath("dataset.selection.model");
	if (n2!=null) {
       		NamedNodeMap nm=n2.getAttributes();
	        if (nm!=null) {
               		org.w3c.dom.Node n3=nm.getNamedItem("name");
	                if (n3!=null)  target.setItem("depthname",n3.getNodeValue());
               		n3=nm.getNamedItem("maintainer");
	                if (n3!=null)  target.setItem("depthmaintainer",n3.getNodeValue());
               		n3=nm.getNamedItem("version");
	                if (n3!=null)  target.setItem("depthversion",n3.getNodeValue());
		} 
	}

       	n2=reader.getElementByPath("dataset.selection.startnode");
	if (n2!=null) {
       		NamedNodeMap nm=n2.getAttributes();
	        if (nm!=null) {
               		org.w3c.dom.Node n3=nm.getNamedItem("alias");
	                if (n3!=null)  target.setItem("depthalias",n3.getNodeValue());
		} 
       		n2=reader.getElementByPath("dataset.selection.startnode.builder");
		if (n2!=null)  target.setItem("depthbuilder",n2.getFirstChild().getNodeValue());
       		n2=reader.getElementByPath("dataset.selection.startnode.where");
		if (n2!=null)  target.setItem("depthwhere",n2.getFirstChild().getNodeValue());
	}

       	n2=reader.getElementByPath("dataset.selection.depth");
	if (n2!=null)  target.setItem("depth",n2.getFirstChild().getNodeValue());
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
			//log.info("alias="+alias+" builder="+builder+" where="+where+" depth="+depth);
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

    private HashSet<Integer> getWantedBuilders(Target target,String id) {
	HashSet<Integer> set = new HashSet<Integer>();
	Project prj = target.getParent();
	Target mod = prj.getTargetById(id);
	log.info("mod="+mod);
	CloudModelCreator modcr = (CloudModelCreator)mod.getCreator();
        Model model = new Model(modcr.getModelFile(mod));

        Iterator<NeededBuilder> nbl=model.getNeededBuilders();
        while (nbl.hasNext()) {
            NeededBuilder nb=nbl.next();
	    int i=MMBase.getMMBase().getTypeDef().getIntValue(nb.getName());
	    set.add(new Integer(i));
        }
	return set;
    }

  public String getDefaultTargetName() {
        return "data";
  }

    public MMObjectNode getTypeInfo(String project, String target) {
        VirtualBuilder builder = new VirtualBuilder(MMBase.getMMBase());
        MMObjectNode virtual = builder.getNewNode("admin");
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            if (t != null) {
                    virtual.setValue("type", t.getItem("type"));
                    virtual.setValue("depthname", t.getItem("depthname"));
                    virtual.setValue("depthmaintainer", t.getItem("depthmaintainer"));
                    virtual.setValue("depthversion", t.getItem("depthversion"));
                    virtual.setValue("depthalias", t.getItem("depthalias"));
                    virtual.setValue("depthbuilder", t.getItem("depthbuilder"));
                    virtual.setValue("depthwhere", t.getItem("depthwhere"));
                    virtual.setValue("depthcount", t.getItem("depth"));
            }
        }
        return virtual;
    }

    public void setDataFileType(String project, String target,String type) {
        Project p = ProjectManager.getProject(project);
        if (p != null) {
            Target t = p.getTarget(target);
            if (t != null) {
                    t.setItem("type",type);
		    saveDataFile(project,t);
	    }
        }
    }

    private boolean saveDataFile(String project,Target target) {
        String datafile = target.getBaseDir() + getItemStringValue(target, "datafile");
        String id = target.getItem("depthname") + "@" + target.getItem("depthmaintainer") + "_cloud/model";
        id = id.replace(' ', '_');
        id = id.replace('/', '_');
        HashSet<Integer> filters = getWantedBuilders(target,id);
        Apps1DataWriter.writeDataXML(filters,datafile,target);
	return true;
    }

}


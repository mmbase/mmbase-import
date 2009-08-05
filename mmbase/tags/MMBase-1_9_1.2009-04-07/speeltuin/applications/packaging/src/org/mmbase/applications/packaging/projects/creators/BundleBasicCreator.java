/*
 
This software is OSI Certified Open Source Software.
OSI Certified is a certification mark of the Open Source Initiative.
 
The license (Mozilla version 1.0) can be read at the MMBase site.
See http://www.MMBase.org/license
 
 */

package org.mmbase.applications.packaging.projects.creators;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;
import org.mmbase.util.logging.*;
import org.mmbase.util.*;
import org.mmbase.module.builders.Versions;
import org.mmbase.applications.packaging.*;
import org.mmbase.applications.packaging.packagehandlers.*;
import org.mmbase.applications.packaging.projects.*;


import java.io.*;
import java.net.*;
import java.util.*;
import java.util.jar.*;
import java.util.zip.*;

import org.w3c.dom.*;

/**
 * BundleBasicCreator, Handler for basic bundles
 *
 * @author Daniel Ockeloen (MMBased)
 */
public class BundleBasicCreator extends BasicCreator implements CreatorInterface {

    private static Logger log = Logging.getLoggerInstance(BundleBasicCreator.class);


    public static final String DTD_PACKAGING_BUNDLE_BASIC_1_0 = "packaging_bundle_basic_1_0.dtd";
    public static final String PUBLIC_ID_PACKAGING_BUNDLE_BASIC_1_0 = "-//MMBase//DTD packaging_bundle_basic config 1.0//EN";

 
    public static void registerPublicIDs() {
        XMLEntityResolver.registerPublicID(PUBLIC_ID_PACKAGING_BUNDLE_BASIC_1_0, "DTD_PACKAGING_BUNDLE_BASIC_1_0", BundleBasicCreator.class);    }

    public BundleBasicCreator() {
    	cl=BundleBasicCreator.class;
   	prefix="packaging_bundle_basic";
    }


   public boolean createPackage(Target target,int newversion) {

   	clearPackageSteps();

        // step1
        packageStep step=getNextPackageStep();
        step.setUserFeedBack("bundle/basic packager started");

	String newfilename=getBuildPath()+getName(target).replace(' ','_')+"@"+getMaintainer(target)+"_bundle_basic_"+newversion;

	try {
  		JarOutputStream jarfile = new JarOutputStream(new FileOutputStream(newfilename+".tmb"),new Manifest());

	        step=getNextPackageStep();
       	 	step.setUserFeedBack("creating bundle.xml file...");
		createBundleMetaFile(jarfile,target,newversion);
        	step.setUserFeedBack("creating bundle.xml file...done");

		// loop the included packages to put them in the bundle jar
		ArrayList packages=(ArrayList)target.getItem("includedpackages");
        	for (Iterator i = packages.iterator(); i.hasNext();) {
               		IncludedPackage ip=(IncludedPackage)i.next();
			if (ip.getIncluded()) {
				PackageInterface p=PackageManager.getPackage(ip.getId(),ip.getVersion());
				JarFile jf=p.getJarFile();
				if (jf!=null) {
					String includename=ip.getId()+"_"+ip.getVersion()+".mmp";	
					String buildname=jf.getName();
	                		addFile(jarfile,buildname,includename,"packagefile","");
				}
			}
						
		}

		jarfile.close();
	} catch(Exception e) {
		e.printStackTrace();
	}


	// update the build file to reflect the last build, should only be done if no errors
	if (getErrorCount()==0) {
    		File f1 = new File(newfilename+".tmb");
    		File f2 = new File(newfilename+".mmb");
    		if (f1.renameTo(f2)) {
			updatePackageTime(target,new Date(),newversion);	
			target.save();
		}
        	step=getNextPackageStep();
        	step.setUserFeedBack("Saving new version : "+newversion);
	}

	// do we need to send this to a publish provider ?
	if (target.getPublishState()) {
                ProviderManager.resetSleepCounter();
        	step=getNextPackageStep();
        	step.setUserFeedBack("publishing to provider : "+target.getPublishProvider());
        	step=getNextPackageStep();
        	step.setUserFeedBack("sending file (version "+newversion+") : "+target.getId()+" ...");
		if (target.publish(newversion)) {
        		step.setUserFeedBack("sending file (version "+newversion+") : "+target.getId()+" ... done");
		} else {
        		step.setUserFeedBack("sending file (version "+newversion+") : "+target.getId()+" ... failed");
		}
	}

        step=getNextPackageStep();
        step.setUserFeedBack("bundle/basic packager ended : "+getErrorCount()+" errors and "+getWarningCount()+" warnings");
        return true;
   }    


   public void createBundleMetaFile(JarOutputStream jarfile,Target target,int newversion) {
	Date d=new Date();
	String body="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	body+="<!DOCTYPE bundle PUBLIC \"-//MMBase/DTD bundle config 1.0//EN\" \"http://www.mmbase.org/dtd/bundle_1_0.dtd\">\n";
	body+="<bundle name=\""+getName(target)+"\" type=\""+getType()+"\" maintainer=\""+getMaintainer(target)+"\" version=\""+newversion+"\" creation-date=\""+d.toString()+"\" >\n";
	body+="\t<description>"+getDescription(target)+"</description>\n";
	body+="\t<license type=\""+getLicenseType(target)+"\" version=\""+getLicenseVersion(target)+"\" name=\""+getLicenseName(target)+"\" />\n";
	body+="\t<releasenotes>\n"+getReleaseNotes(target)+"\n</releasenotes>\n";
	body+="\t<installationnotes>\n"+getInstallationNotes(target)+"\n</installationnotes>\n";


  	body+=getRelatedPeopleXML("initiators","initiator",target);
  	body+=getRelatedPeopleXML("supporters","supporter",target);
  	body+=getRelatedPeopleXML("developers","developer",target);
  	body+=getRelatedPeopleXML("contacts","contact",target);

	body+=getNeededPackagesXML(target);

	body+="</bundle>\n";
	try {
       		JarEntry entry = new JarEntry("bundle.xml");
           	jarfile.putNextEntry(entry);
       		jarfile.write(body.getBytes("UTF-8"));
	} catch (Exception e) {
		e.printStackTrace();
	}
   }


   public String getNeededPackagesXML(Target target) {
	ArrayList packages=(ArrayList)target.getItem("includedpackages");
	String body="\t<neededpackages>\n";
	if (packages!=null) {
        	for (Iterator i = packages.iterator(); i.hasNext();) {
               		IncludedPackage ip=(IncludedPackage)i.next();
			body+="\t\t<package name=\""+ip.getName()+"\" type=\""+ip.getType()+"\" maintainer=\""+ip.getMaintainer()+"\" version=\""+ip.getVersion()+"\" included=\""+ip.getIncluded()+"\" />\n";
		}
	}
	body+="\t</neededpackages>\n";
	return body;
     }

  public ArrayList getIncludedPackages(Target target) {
	Object o=target.getItem("includedpackages");
	if (o!=null) {
		return (ArrayList)o;
	}
	return null;
  }

  public boolean decodeItems(Target target) {
			super.decodeItems(target);
			// decode the needed packages	
			ArrayList includedpackages=new ArrayList();
			XMLBasicReader reader=target.getReader();
                       	org.w3c.dom.Node n=reader.getElementByPath(prefix+".neededpackages");
        		org.w3c.dom.Node n2=n.getFirstChild();
     		   	while (n2!=null) {
         		if (n2.getNodeName().equals("package")) {
              			NamedNodeMap nm=n2.getAttributes();
				String name=null;
				String type=null;
				String maintainer=null;
				String version=null;
				String included=null;
              			if (nm!=null) {
                       			org.w3c.dom.Node n3=nm.getNamedItem("name");
                       			name=n3.getNodeValue();
                       			n3=nm.getNamedItem("type");
                       			type=n3.getNodeValue();
                       			n3=nm.getNamedItem("maintainer");
                       			maintainer=n3.getNodeValue();
                       			n3=nm.getNamedItem("version");
                       			version=n3.getNodeValue();
                       			n3=nm.getNamedItem("included");
                       			included=n3.getNodeValue();
					IncludedPackage ip=new IncludedPackage();
					ip.setName(name);
					ip.setMaintainer(maintainer);
					ip.setVersion(version);
					ip.setType(type);
					if (included.equals("true")) {
						ip.setIncluded(true);
					} else {
						ip.setIncluded(false);
					}
					includedpackages.add(ip);
				}
			}
        		n2=n2.getNextSibling();
		}
		target.setItem("includedpackages",includedpackages);
        return false;
  }

  public boolean setIncludedVersion(Target target,String id,String newversion) {
	ArrayList packages=(ArrayList)target.getItem("includedpackages");
       	for (Iterator i = packages.iterator(); i.hasNext();) {
              	IncludedPackage ip=(IncludedPackage)i.next();
		if (ip.getId().equals(id)) {
			ip.setVersion(newversion);
		}
	}
	target.save();
        return true;
  }


  public boolean delIncludedPackage(Target target,String id) {
	ArrayList packages=(ArrayList)target.getItem("includedpackages");
       	for (Iterator i = packages.iterator(); i.hasNext();) {
              	IncludedPackage ip=(IncludedPackage)i.next();
		if (ip.getId().equals(id)) {
			packages.remove(ip);
			break;
		}
	}
	target.save();
        return true;
  }

   public String getXMLFile(Target target) {
        String body=getDefaultXMLHeader(target);
	body+=getDefaultXMLMetaInfo(target);
        body+=getRelatedPeopleXML("initiators","initiator",target);
        body+=getRelatedPeopleXML("supporters","supporter",target);
        body+=getRelatedPeopleXML("developers","developer",target);
        body+=getRelatedPeopleXML("contacts","contact",target);
	body+=getNeededPackagesXML(target);
	if (target.getPublishProvider()!=null) {
		if (target.getPublishState()) {
			body+="\t<publishprovider name=\""+target.getPublishProvider()+"\" state=\"active\" sharepassword=\""+target.getPublishSharePassword()+"\" />\n";
		} else {
			body+="\t<publishprovider name=\""+target.getPublishProvider()+"\" state=\"inactive\" sharepassword=\""+target.getPublishSharePassword()+"\" />\n";
		}
	}
	body+=getDefaultXMLFooter(target);
        return body;
   }

  public boolean addPackage(Target target,String newpackage) {
	ArrayList packages=(ArrayList)target.getItem("includedpackages");
	PackageInterface p=PackageManager.getPackage(newpackage);
	if (p!=null) {
             IncludedPackage ip=new IncludedPackage();
             ip.setName(p.getName());
             ip.setMaintainer(p.getMaintainer());
             ip.setVersion(p.getVersion());
             ip.setType(p.getType());
             ip.setIncluded(true);
	     if (packages==null) {
		packages=new ArrayList();
		target.setItem("includedpackages",packages);
	     }
	     packages.add(ip);
	     target.save(); 
             return true;
	}
        return false;
  }


}
